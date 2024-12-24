package ru.jetlabs.acquiringmockbackend.service;

public interface JWTGenerator {
    String generate(Long userId);
}
