package ru.jetlabs.acquiringmockbackend.model.dto;

public record LoginUserDto(
        String email,
        String password
) {}
