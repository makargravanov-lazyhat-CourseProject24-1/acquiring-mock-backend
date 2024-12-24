package ru.jetlabs.acquiringmockbackend.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentJWTKeys {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = lock.writeLock();
    private final Lock readLock = lock.readLock();
    private ConcurrentPair<String, LocalDateTime> oldKey = new ConcurrentPair<>();
    private ConcurrentPair<String, LocalDateTime> actualKey = new ConcurrentPair<>();
    private final Integer jwtLifetime;

    public ConcurrentJWTKeys(Integer jwtLifetime) {
        this.jwtLifetime = jwtLifetime;
        ConcurrentPair<String, LocalDateTime> old = new ConcurrentPair<>(gen(), LocalDateTime.now().plusMinutes(jwtLifetime));
        ConcurrentPair<String, LocalDateTime> actual = new ConcurrentPair<>(gen(), LocalDateTime.now());
        this.oldKey = old;
        this.actualKey = actual;
    }

    private String gen() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return new BigInteger(1, bytes).toString(16);
    }

    public void refreshKeys() {
        writeLock.lock();
        try {
            oldKey.setPair(actualKey.getKey(), LocalDateTime.now().plusMinutes(jwtLifetime));
            actualKey.setPair(gen(), LocalDateTime.now());
        } finally {
            writeLock.unlock();
        }
    }

    public String getOldKey() {
        readLock.lock();
        try {
            return oldKey.getKey();
        } finally {
            readLock.unlock();
        }
    }

    public String getActualKey() {
        readLock.lock();
        try {
            return actualKey.getKey();
        } finally {
            readLock.unlock();
        }
    }

    public LocalDateTime getTimeOfActual() {
        readLock.lock();
        try {
            return actualKey.getValue();
        } finally {
            readLock.unlock();
        }
    }

    public LocalDateTime getTimeOfOld() {
        readLock.lock();
        try {
            return oldKey.getValue();
        } finally {
            readLock.unlock();
        }
    }

}
