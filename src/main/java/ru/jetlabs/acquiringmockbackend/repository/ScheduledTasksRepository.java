package ru.jetlabs.acquiringmockbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jetlabs.acquiringmockbackend.entity.ScheduledTasksEntity;
import ru.jetlabs.acquiringmockbackend.entity.TransactionEntity;

import java.util.Optional;

@Repository
public interface ScheduledTasksRepository extends JpaRepository<ScheduledTasksEntity, Long> {

    Optional<ScheduledTasksEntity> findByTransaction(TransactionEntity transaction);
}
