package ru.jetlabs.acquiringmockbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.jetlabs.acquiringmockbackend.entity.AccountEntity;
import ru.jetlabs.acquiringmockbackend.entity.UserEntity;
import ru.jetlabs.acquiringmockbackend.model.enumerations.AccountTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    // Поиск по номеру счета
    Optional<AccountEntity> findByNumber(String number);

    // Поиск всех активных счетов определенного типа
    List<AccountEntity> findByAccountTypeAndExpirationDateIsNullOrExpirationDateAfter(
            AccountTypes accountType, LocalDateTime now);

    // Поиск счетов с балансом больше указанного
    List<AccountEntity> findByBalanceGreaterThan(Double minBalance);

    // Поиск счетов конкретного владельца
    List<AccountEntity> findByOwnerId(Long ownerId);

    // Поиск счетов по типу и владельцу
    List<AccountEntity> findByAccountTypeAndOwner(AccountTypes type, UserEntity owner);

    // Подсчет количества счетов определенного типа
    long countByAccountType(AccountTypes accountType);

    // Удаление просроченных счетов
    @Modifying
    @Query("DELETE FROM AccountEntity a WHERE a.expirationDate < :date")
    void deleteExpiredAccounts(@Param("date") LocalDateTime date);
}
