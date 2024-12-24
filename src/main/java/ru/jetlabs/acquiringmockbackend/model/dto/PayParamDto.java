package ru.jetlabs.acquiringmockbackend.model.dto;

public record PayParamDto(
        String number,
        String cvv,
        String expirationYear,
        String expirationMonth
){}
