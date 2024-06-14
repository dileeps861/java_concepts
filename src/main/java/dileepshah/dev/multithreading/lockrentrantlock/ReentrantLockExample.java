package dileepshah.dev.multithreading.lockrentrantlock;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Created by dileepshah on 2024-06-14
 * <p>
 *
 * This class is used to show how to use ReentrantLock.md. ReentrantLock.md is used to synchronize access to a resource.
 * ReentrantLock.md is a lock that can be acquired multiple times by the same thread.
 * </p>
 *
 * - Note: Below are interfaces that ReentrantLock.md implements.
 * @see java.util.concurrent.locks.Lock
 *
 * Implementations of Lock:
 * @see java.util.concurrent.locks.ReentrantLock
 *
 * - Note: Below are interfaces that ReentrantReadWriteLock implements. This class is used to read and write locks.
 * It has a way to synchronize access to a resource and also guarantees the fairness of access to the resource.
 * @see java.util.concurrent.locks.ReentrantReadWriteLock
 * @see java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock
 * @see java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock
 *
 */
@AllArgsConstructor
public class ReentrantLockExample implements Runnable {
    private final Lock lock;

    @SneakyThrows
    public void run() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " is running");
            Thread.sleep(TimeUnit.MICROSECONDS.toMillis(1));
            System.out.println(Thread.currentThread().getName() + " done running");
        } finally {
            lock.unlock();
        }
    }
}
