package ru.jetlabs.acquiringmockbackend.service;

import com.auth0.jwt.interfaces.DecodedJWT;

public interface JWTValidator {
    DecodedJWT validate(String token) throws NullPointerException ;
    Long getIdFromToken(DecodedJWT token) throws NullPointerException ;
}
