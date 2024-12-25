package ru.jetlabs.acquiringmockbackend.model.dto;

import ru.jetlabs.acquiringmockbackend.entity.ScheduledTasksEntity;
import ru.jetlabs.acquiringmockbackend.entity.TransactionEntity;

public record TransactionNotification(ScheduledTasksEntity task, TransactionEntity transaction) {}