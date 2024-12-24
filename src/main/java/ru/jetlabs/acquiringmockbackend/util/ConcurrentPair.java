package ru.jetlabs.acquiringmockbackend.util;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentPair<K, V> {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = lock.writeLock();
    private final Lock readLock = lock.readLock();
    private AtomicReference<K> key;
    private AtomicReference<V> value;

    public ConcurrentPair(K key, V value) {
        this.key = new AtomicReference<>(key);
        this.value = new AtomicReference<>(value);
    }

    public ConcurrentPair() {
    }

    public void setPair(K key, V value) {
        writeLock.lock();
        try {
            this.key = new AtomicReference<>(key);
            this.value = new AtomicReference<>(value);
        } finally {
            writeLock.unlock();
        }

    }

    public K getKey() {
        readLock.lock();
        try {
            return this.key.get();
        } finally {
            readLock.unlock();
        }
    }

    public void setKey(K key) {
        writeLock.lock();
        try {
            this.key = new AtomicReference<>(key);
        } finally {
            writeLock.unlock();
        }
    }

    public V getValue() {
        readLock.lock();
        try {
            return this.value.get();
        } finally {
            readLock.unlock();
        }
    }

    public void setValue(V value) {
        writeLock.lock();
        try {
            this.value =  new AtomicReference<>(value);
        } finally {
            writeLock.unlock();
        }
    }
}