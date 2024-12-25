package ru.jetlabs.acquiringmockbackend.util;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public abstract class AbstractTimedData {
    private LocalDateTime expiresAt;
    protected AbstractTimedData(LocalDateTime expiresAt){
        this.expiresAt = expiresAt;
    }
}
