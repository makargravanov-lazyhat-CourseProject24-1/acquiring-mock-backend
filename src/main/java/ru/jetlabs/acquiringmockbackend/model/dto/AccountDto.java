package ru.jetlabs.acquiringmockbackend.model.dto;

import ru.jetlabs.acquiringmockbackend.model.enumerations.AccountTypes;

import java.time.LocalDateTime;

public record AccountDto(
        Long id,
        String number,
        String cvv,
        LocalDateTime expirationDate,
        Double balance,
        AccountTypes accountType
) {}
