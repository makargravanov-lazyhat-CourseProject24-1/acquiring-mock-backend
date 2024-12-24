package ru.jetlabs.acquiringmockbackend.model.dto;

import java.time.LocalDateTime;

public record PaymentDto(
        Double sum,
        LocalDateTime expiredAt
) {
}
