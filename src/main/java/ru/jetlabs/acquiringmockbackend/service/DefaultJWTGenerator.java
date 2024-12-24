package ru.jetlabs.acquiringmockbackend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.jetlabs.acquiringmockbackend.util.ConcurrentJWTKeys;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class DefaultJWTGenerator implements JWTGenerator {
    private final ConcurrentJWTKeys jwtKeys;
    @Value("${jwt.lifetime}")
    private Integer jwtLifetime;

    public DefaultJWTGenerator(ConcurrentJWTKeys jwtKeys) {
        this.jwtKeys = jwtKeys;
    }

    @Override
    public String generate(Long userId){
        Instant now = Instant.now();
        Instant exp = now.plus(jwtLifetime, ChronoUnit.MINUTES);

        return JWT.create()
                .withSubject(userId.toString())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(exp))
                .sign(Algorithm.HMAC256(jwtKeys.getActualKey()));
    }
}
