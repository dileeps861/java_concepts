package dileepshah.dev.os.concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * SimpleFairReadWriteLock provides a fair read-write lock implementation
 * allowing multiple readers or a single writer.
 */
public class SimpleFairReadWriteLock {
    private final Lock lock;
    private final Condition conditionOnLock;
    private int readers;
    private int writers;
    private int writersWaiting;

    public SimpleFairReadWriteLock() {
        this.lock = new ReentrantLock();
        this.conditionOnLock = this.lock.newCondition();
        this.readers = 0;
        this.writers = 0;
        this.writersWaiting = 0;
    }

    public void lockRead() throws InterruptedException {
        lock.lock();
        try {
            while (writers > 0 || writersWaiting > 0) {
                conditionOnLock.await();
            }
            readers++;
        } finally {
            lock.unlock();
        }
    }

    public void unlockRead() {
        lock.lock();
        try {
            readers--;
            if (readers == 0) {
                conditionOnLock.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    public void lockWrite() throws InterruptedException {
        lock.lock();
        try {
            writersWaiting++;
            while (readers > 0 || writers > 0) {
                conditionOnLock.await();
            }
            writersWaiting--;
            writers++;
        } finally {
            lock.unlock();
        }
    }

    public void unlockWrite() {
        lock.lock();
        try {
            writers--;
            conditionOnLock.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        final SimpleFairReadWriteLock fairReadWriteLock = new SimpleFairReadWriteLock();

        Runnable readTask = () -> {
            try {
                fairReadWriteLock.lockRead();
                System.out.println(Thread.currentThread().getName() + " acquired read lock");
                Thread.sleep(1000); // Simulate read operation
                fairReadWriteLock.unlockRead();
                System.out.println(Thread.currentThread().getName() + " released read lock");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Runnable writeTask = () -> {
            try {
                fairReadWriteLock.lockWrite();
                System.out.println(Thread.currentThread().getName() + " acquired write lock");
                Thread.sleep(2000); // Simulate write operation
                System.out.println(Thread.currentThread().getName() + " released write lock");
                fairReadWriteLock.unlockWrite();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Thread writer1 = new Thread(writeTask, "Writer1");
        Thread writer2 = new Thread(writeTask, "Writer2");
        Thread reader1 = new Thread(readTask, "Reader1");
        Thread reader2 = new Thread(readTask, "Reader2");

        writer1.start();
        reader1.start();
        writer2.start();
        reader2.start();
    }
}