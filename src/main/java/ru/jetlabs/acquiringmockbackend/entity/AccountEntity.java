package ru.jetlabs.acquiringmockbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.jetlabs.acquiringmockbackend.model.enumerations.AccountTypes;

import java.time.LocalDateTime;

import static ru.jetlabs.acquiringmockbackend.util.TimeUtil.nowBetweenOf;

@Data
@Entity
@Table(name = "accounts")
public class AccountEntity {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;
    @Column(unique = true, nullable = false)
    private String number;
    @Column(nullable = false)
    private String cvv;
    @Column(nullable = false)
    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;
    @Column(nullable = false)
    private Double balance;
    @Column(nullable = false)
    private AccountTypes accountType;

    public boolean isActive(){
        return expirationDate == null || nowBetweenOf(creationDate, expirationDate);
    }
}