package ru.jetlabs.acquiringmockbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.jetlabs.acquiringmockbackend.entity.AccountEntity;
import ru.jetlabs.acquiringmockbackend.entity.TransactionEntity;
import ru.jetlabs.acquiringmockbackend.model.enumerations.TransactionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    // Поиск транзакций по UUID
    Optional<TransactionEntity> findByUuid(String uuid);

    // Поиск всех транзакций конкретного счета (отправленных или полученных)
    @Query("SELECT t FROM TransactionEntity t WHERE t.fromAccount = :account OR t.toAccount = :account")
    List<TransactionEntity> findAllByAccount(@Param("account") AccountEntity account);

    // Поиск транзакций за определенный период
    List<TransactionEntity> findByTransactionDateBetween(
            LocalDateTime startDate, LocalDateTime endDate);

    // Поиск транзакций по статусу и сумме больше указанной
    List<TransactionEntity> findByTransactionStatusAndTotalGreaterThan(
            TransactionStatus status, Double minAmount);

    // Подсчет количества транзакций по статусу
    long countByTransactionStatus(TransactionStatus status);

}
