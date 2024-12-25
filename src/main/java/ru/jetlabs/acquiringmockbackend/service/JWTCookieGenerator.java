package ru.jetlabs.acquiringmockbackend.service;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class JWTCookieGenerator {

    public String create(Long id, String name, JWTGenerator generator) {
        return ResponseCookie.from(name)
                .value(generator.generate(id))
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("NONE")
                .build()
                .toString();
    }
}