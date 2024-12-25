package ru.jetlabs.acquiringmockbackend.util;

import org.springframework.scheduling.annotation.Scheduled;
import ru.jetlabs.acquiringmockbackend.entity.TransactionEntity;
import ru.jetlabs.acquiringmockbackend.model.enumerations.TransactionStatus;
import ru.jetlabs.acquiringmockbackend.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class TimedBuffer<K> {
    private final ConcurrentHashMap<K, AbstractTimedData> buffer = new ConcurrentHashMap<>();
    private final Executor taskExecutor;
    private final TransactionRepository transactionRepository;
    public TimedBuffer(Executor taskExecutor, TransactionRepository transactionRepository) {
        this.taskExecutor = taskExecutor;
        this.transactionRepository = transactionRepository;
    }

    public void put(K key, AbstractTimedData value) {
        buffer.put(key, value);
    }

    public AbstractTimedData get(K key) {
        return buffer.get(key);
    }
    public void removeByKey(K key){
        buffer.remove(key);
    }

    public boolean containsKey(K key) {
        return buffer.containsKey(key);
    }

    public void clear() {
        buffer.clear();
    }

    @Scheduled(fixedRate = 60000) // Выполняется каждые 60 секунд
    public void clearExpiredData() {
        taskExecutor.execute(() -> buffer.entrySet().removeIf(entry -> {
            LocalDateTime expiresAt = entry.getValue().getExpiresAt();
            if(expiresAt != null && expiresAt.isBefore(LocalDateTime.now())){
                Optional<TransactionEntity> opt = transactionRepository.findByUuid((String) entry.getKey());
                opt.ifPresent(transactionEntity ->{
                    transactionEntity.setTransactionStatus(TransactionStatus.EXPIRED);
                    transactionRepository.save(transactionEntity);
                });
            }
            return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
        }));
    }
}
