package ru.jetlabs.acquiringmockbackend.model.dto;

import ru.jetlabs.acquiringmockbackend.model.enumerations.TransactionStatus;


public record TransactionStatusUpdate (
     String transactionUuid,
     TransactionStatus status
){}