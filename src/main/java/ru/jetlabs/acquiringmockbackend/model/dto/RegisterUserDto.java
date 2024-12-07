package ru.jetlabs.acquiringmockbackend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record RegisterUserDto(
        @Size(min = 1, max = 50)
        String name,
        @Email
        String email,
        @Size(min = 8, max = 50)
        String password
) {
}
