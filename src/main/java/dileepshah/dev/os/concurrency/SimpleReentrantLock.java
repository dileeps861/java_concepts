package dileepshah.dev.os.concurrency;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleReentrantLock {
    private final SimpleLock simpleLock;
    private final AtomicInteger count;
    private AtomicReference<Thread> holdingThread;

    public SimpleReentrantLock() {
        this.simpleLock = new SimpleLock();
        this.count = new AtomicInteger(0);
        this.holdingThread = new AtomicReference<>(null);
    }

    public void lock(){
        Thread currentThread = Thread.currentThread();
        if (holdingThread.get() != currentThread) {
            simpleLock.lock();
            holdingThread.set(currentThread);
        }
        count.incrementAndGet();
    }

    public void unlock(){
        Thread currentThread = Thread.currentThread();
        if (holdingThread.get() != currentThread) {
            throw new IllegalMonitorStateException("The current thread does not hold the lock");
        }

        int newCount = count.decrementAndGet();
        if (newCount == 0) {
            holdingThread.set(null);
            simpleLock.unlock();
        }
    }

    public static void main(String[] args) {
        final SimpleReentrantLock reentrantLock = new SimpleReentrantLock();
        Runnable runnable = () -> {
            System.out.println("Trying to acquire the lock for thread: " + Thread.currentThread().getName());
            reentrantLock.lock();
            System.out.println("Acquired the lock for thread: " + Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            reentrantLock.unlock();
            System.out.println("Released the lock for thread: " + Thread.currentThread().getName());
        };

        for (int i = 0; i < 5; i++) {
            new Thread(runnable).start();
        }
    }
}
