package ru.jetlabs.acquiringmockbackend.util;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.jetlabs.acquiringmockbackend.entity.ScheduledTasksEntity;
import ru.jetlabs.acquiringmockbackend.entity.TransactionEntity;
import ru.jetlabs.acquiringmockbackend.model.dto.PayParamDto;
import ru.jetlabs.acquiringmockbackend.model.dto.TransactionNotification;
import ru.jetlabs.acquiringmockbackend.model.dto.TransactionStatusUpdate;
import ru.jetlabs.acquiringmockbackend.model.enumerations.TransactionStatus;
import ru.jetlabs.acquiringmockbackend.repository.ScheduledTasksRepository;
import ru.jetlabs.acquiringmockbackend.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class ScheduledTaskUtil {
    private final ScheduledTasksRepository scheduledTasksRepository;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;
    private final Executor taskExecutor;

    public ScheduledTaskUtil(ScheduledTasksRepository scheduledTasksRepository, TransactionRepository transactionRepository, RestTemplate restTemplate, @Qualifier("taskExecutor") Executor taskExecutor) {
        this.scheduledTasksRepository = scheduledTasksRepository;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
        this.taskExecutor = taskExecutor;
    }

    public void addTask(TransactionEntity transaction, String callbackUrl){
        ScheduledTasksEntity t = new ScheduledTasksEntity();
        t.setCallbackUrl(callbackUrl);
        t.setTransaction(transaction);
        scheduledTasksRepository.save(t);
    }
    public void sendCallbackManually(TransactionEntity t){
        Optional<ScheduledTasksEntity> opt = scheduledTasksRepository.findByTransaction(t);
        opt.ifPresent(this::processTransaction);
    }

    @Scheduled(fixedRate = 60000)
    public void clearExpiredData() {
        taskExecutor.execute(() ->
                scheduledTasksRepository.findAll()
                        .parallelStream()
                        .map(this::processTransaction)
                        .forEach(CompletableFuture::join)
        );
    }

    private CompletableFuture<Void> processTransaction(ScheduledTasksEntity task) {
        return CompletableFuture.supplyAsync(() -> {
            TransactionEntity te = task.getTransaction();
            if (te.getTransactionDate().isBefore(LocalDateTime.now())&&te.getTransactionStatus()==TransactionStatus.CREATED) {
                te.setTransactionStatus(TransactionStatus.EXPIRED);
                transactionRepository.save(te);
                return new TransactionNotification(task, te);
            } else if(te.getTransactionStatus()==TransactionStatus.APPROVED){
                return new TransactionNotification(task, te);
            }
            return null;
        }, taskExecutor).thenComposeAsync(notification -> {
            if (notification != null) {
                return sendNotification(notification, task);
            }
            return CompletableFuture.completedFuture(null);
        }, taskExecutor).exceptionally(t -> {
            System.out.println(t);
            return null;
        });
    }

    private CompletableFuture<Void> sendNotification(TransactionNotification notification, ScheduledTasksEntity task) {
        return CompletableFuture.runAsync(() -> {
            TransactionStatusUpdate statusUpdate = new TransactionStatusUpdate(
                    notification.transaction().getUuid(),
                    notification.transaction().getTransactionStatus()
            );

            try {
                System.out.println(statusUpdate);
                ResponseEntity<String> response = restTemplate.postForEntity(
                        notification.task().getCallbackUrl(),
                        statusUpdate,
                        String.class
                );

                if (response.getStatusCode().is2xxSuccessful()) {

                    scheduledTasksRepository.delete(task);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }, taskExecutor);
    }

}
