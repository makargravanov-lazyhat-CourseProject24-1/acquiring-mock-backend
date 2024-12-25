package ru.jetlabs.acquiringmockbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ScheduledTasksEntity {
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne
    @JoinColumn(name = "transaction_id")
    private TransactionEntity transaction;
    private String callbackUrl;
}
