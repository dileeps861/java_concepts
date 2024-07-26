package dileepshah.dev.os.concurrency;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * This is a simple class demonstrating the reentramt lock implementation in java using synchronized blocks,
 * and it's useful in acquiring mutex and allow accessing the critical section of the code by only one thread.
 *
 * Simple lock allows each thread to acquire lock only once due to this can cause a deadlock situation if not handled
 * correctly.
 *
 * Reentrant lock handles this by allowing acquiring lock to acquire the lock multiple times, and lock can be acquired
 * by other threads only when all the locks are released by holding thread.
 */
public class SimpleReentrantLockUsingSynchronized {
    private boolean lockMutex;
    private Thread lockingThread;
    private int lockCount;
    /**
     * Default constructor to instantiate the lock.
     */
    public SimpleReentrantLockUsingSynchronized() {
        this.lockMutex = false;
        this.lockingThread = null;
        this.lockCount = 0;
    }

    /**
     * Allows locking the mutex. Can same thread renter and acquire the lock multiple times.
     */
    public void lock() {
        synchronized (this) {
            while (lockMutex && this.lockingThread != Thread.currentThread()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            this.lockMutex = true;
            this.lockCount ++;
            this.lockingThread = Thread.currentThread();
        }
    }

    /**
     * Allows unlocking the acquired lock on the mutex. Thread will release the lock only when all the locks acquired
     * by the thread are released.
     */
    public void unlock() {
        synchronized (this) {
            if (Thread.currentThread() == this.lockingThread) {
                lockCount--;
                if (lockCount == 0) {
                    lockMutex = false;
                    this.lockingThread = null;
                    notify();
                }
            }
        }
    }

    /**
     * This method returns a new conditional variable associated with the lock. It is used to wait and signal thread
     * about the statuses.
     *
     * @return the Condition object
     */
    public Condition createCondition() {
        return new ConditionObject();
    }

    /**
     *
     * Placeholder Condition variable for this lock. Implementation is yet to be done completely.
     */
    static class ConditionObject implements Condition{

        @Override
        public void await() throws InterruptedException {

        }

        @Override
        public void awaitUninterruptibly() {

        }

        @Override
        public long awaitNanos(long nanosTimeout) throws InterruptedException {
            return 0;
        }

        @Override
        public boolean await(long time, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public boolean awaitUntil(Date deadline) throws InterruptedException {
            return false;
        }

        @Override
        public void signal() {

        }

        @Override
        public void signalAll() {

        }
    }

    public static void main(String[] args) {
        final SimpleReentrantLockUsingSynchronized lock = new SimpleReentrantLockUsingSynchronized();
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
