package com.erdilalbayrak;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

public class AutoReleasedLock implements AutoCloseable {
    private final Lock lock;
    private final AtomicBoolean locked;

    public AutoReleasedLock(final Lock lock) {
        this.lock = lock;
        locked = new AtomicBoolean(false);
        this.lock();
    }

    @Override
    public void close() {
        if (false == locked.get()) {
            return;
        }
        lock.unlock();
        locked.set(false);
    }

    public void lock() {
        if (locked.get()) {
            throw new IllegalMonitorStateException("the lock is already locked.");
        }
        lock.lock();
        locked.set(true);
    }

    public void unlock() {
        if (false == locked.get()) {
            throw new IllegalMonitorStateException("the lock is already unlocked.");
        }
        lock.unlock();
        locked.set(false);
    }

    public boolean isLocked() {
        return locked.get();
    }
}
