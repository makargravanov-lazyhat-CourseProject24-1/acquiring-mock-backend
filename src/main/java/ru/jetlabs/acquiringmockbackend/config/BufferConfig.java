package ru.jetlabs.acquiringmockbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.jetlabs.acquiringmockbackend.util.ConcurrentJWTKeys;


@Configuration
public class BufferConfig {
    @Value("${jwt.lifetime}")
    private Integer jwtLifetime;

    @Bean(name = "concurrentJWTKeys")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ConcurrentJWTKeys concurrentJWTKeys() {
        return new ConcurrentJWTKeys(jwtLifetime);
    }
}
