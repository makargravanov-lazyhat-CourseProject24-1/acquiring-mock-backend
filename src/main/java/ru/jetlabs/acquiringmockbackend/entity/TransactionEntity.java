package ru.jetlabs.acquiringmockbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.jetlabs.acquiringmockbackend.model.enumerations.TransactionStatuses;

import java.time.LocalDateTime;

@Data
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
    @JoinColumn(name = "from_account_id", nullable = false)
    private AccountEntity fromAccount;
    @ManyToOne
    @JoinColumn(name = "to_account_id", nullable = false)
    private AccountEntity toAccount;
    @Column(nullable = false)
    private TransactionStatuses transactionStatus;
    @Column(nullable = false)
    private LocalDateTime transactionDate;
}
