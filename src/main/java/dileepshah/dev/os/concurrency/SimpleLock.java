package dileepshah.dev.os.concurrency;

import java.util.concurrent.atomic.AtomicReference;

/**
 * This is a simple lock and allows one thread to acquire the lock at most once.
 * This implementation doesn't use the synchronized block that has performance over head, rather uses low level
 * compareAndSet method allowed to have atomic operation on os level without certain the permanent hold, which is a
 * drawback of the synchronized.
 * Compare and set allows atomic operation and create the lock without blocking.
 * @see java.util.concurrent.atomic.AtomicReference
 * @see java.util.concurrent.atomic.AtomicReference#compareAndSet(Object, Object)
 */
public final class SimpleLock {
    final AtomicReference<Thread> holdingThread;

    public SimpleLock() {
        this.holdingThread = new AtomicReference<>(null);
    }

    /**
     * Acquire the lock if the none of the other threads are holding the lock. If any other threads are holding the
     * lock, it spins and waits.
     */
    public void lock(){
        while(!this.holdingThread.compareAndSet(null, Thread.currentThread())){
            // Spin and wait until another thread has released the lock
        }
    }

    /**
     * Allows the thread to release the lock which it holds.
     */
    public void unlock(){
        Thread currentThread = Thread.currentThread();
        if (!holdingThread.compareAndSet(currentThread, null)) {
            throw new IllegalMonitorStateException("This thread is not holding the lock.");
        }
    }

    public static void main(String[] args) {
        final SimpleLock lock = new SimpleLock();
        Runnable runnable = () -> {
            System.out.println("Trying to acquire the lock for thread: " + Thread.currentThread().getName());
            lock.lock();
            System.out.println("Acquired the lock for thread: " + Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            lock.unlock();
            System.out.println("Released the lock for thread: " + Thread.currentThread().getName());
        };

        for (int i = 0; i < 5; i++) {
            new Thread(runnable).start();
        }
    }
}
