package ru.jetlabs.acquiringmockbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import ru.jetlabs.acquiringmockbackend.model.dto.AccountDto;
import ru.jetlabs.acquiringmockbackend.model.enumerations.AccountTypes;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static ru.jetlabs.acquiringmockbackend.util.TimeUtil.nowBetweenOf;

@Getter
@Setter
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
    public static AccountEntity createRandomAccount(UserEntity owner, AccountTypes accountType) {
        AccountEntity account = new AccountEntity();
        account.owner = owner;
        account.number = account.generateRandomCardNumber();
        account.cvv = account.generateRandomCVV();
        account.creationDate = LocalDateTime.now();
        account.expirationDate = account.creationDate.plusYears(3);
        account.balance = 0.0;
        account.accountType = accountType;
        return account;
    }
    private String generateRandomCardNumber() {
        return RandomStringUtils.randomNumeric(16);
    }
    private String generateRandomCVV() {
        return RandomStringUtils.randomNumeric(3);
    }
    public AccountDto toDto() {
        return new AccountDto(
                this.getId(),
                this.getNumber(),
                this.getCvv(),
                this.getExpirationDate(),
                this.getBalance(),
                this.getAccountType()
        );
    }
    public void addBalance(Double b){
        balance+=b;
    }
}