package ru.jetlabs.acquiringmockbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.jetlabs.acquiringmockbackend.model.enumerations.TransactionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "transactions_story", indexes = {
        @Index(name = "idx_transaction_uuid", columnList = "uuid")
})
public class TransactionEntity {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true, nullable = false)
    private String uuid;
    @Column(nullable = false)
    private Double total;
    @ManyToOne
    @JoinColumn(name = "from_account_id")
    private AccountEntity fromAccount;
    @ManyToOne
    @JoinColumn(name = "to_account_id", nullable = false)
    private AccountEntity toAccount;
    @Column(nullable = false)
    private TransactionStatus transactionStatus;
    @Column(nullable = false)
    private LocalDateTime transactionDate;

    public static TransactionEntity createTransaction(Double total, AccountEntity fromAccount, AccountEntity toAccount, TransactionStatus transactionStatus, Integer minutesToExpire) {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setUuid(UUID.randomUUID().toString());
        transaction.setTotal(total);
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setTransactionStatus(transactionStatus);
        transaction.setTransactionDate(LocalDateTime.now().plusMinutes(minutesToExpire));
        return transaction;
    }
}
