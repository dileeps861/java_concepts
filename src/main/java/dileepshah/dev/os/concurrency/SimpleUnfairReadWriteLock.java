package dileepshah.dev.os.concurrency;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The Read-Write Lock class provides a mechanism for synchronizing access to shared resources that can be accessed
 * concurrently by multiple threads. It allows multiple readers to access the resource simultaneously,
 * but only one writer at a time.
 * This is a simple unfair lock where if read requests keep coming, write lock might not be acquired causing starvation.
 *
 * Usage
 * -----
 * To use the Read-Write Lock class, follow these steps:
 * <p>
 * 1. Create an instance of the Read-Write Lock class.
 * 2. Use the <code>acquireRead()</code> method to acquire a read lock. This allows multiple threads to read the shared resource simultaneously.
 * 3. Use the <code>acquireWrite()</code> method to acquire a write lock. This ensures exclusive access to the shared resource for writing.
 * 4. Use the <code>release()</code> method to release the lock after reading or writing.
 * <p>
 * <h2>Example</h2>
 * </br>
 * Here's an example of how to use the Read-Write Lock class:
 * <p>
 *
 * <pre>
 * <code>
 *
 * SimpleReadWriteLock lock = new SimpleReadWriteLock();
 *
 * // Acquire a read lock
 * lock.acquireRead();
 * try {
 *     // Read the shared resource
 *     String data = sharedResource.read();
 * } finally {
 *     // Release the read lock
 *     lock.release();
 * }
 *
 * // Acquire a write lock
 * lock.acquireWrite();
 * try {
 *     // Write to the shared resource
 *     sharedResource.write(data);
 * } finally {
 *     // Release the write lock
 *     lock.release();
 * }
 *</code>
 * </pre>
 *
 * <h3>Methods</h3>
 * <p>
 *
 * <code>acquireRead()</code>
 * <p>
 * Acquires a read lock. Multiple threads can acquire a read lock simultaneously.

 * <code>acquireWrite()</code>
 * <p>
 * Acquires a write lock. Only one thread can acquire a write lock at a time.
 * <p>
 * <code>release()</code>
 * <p>
 * Releases the lock after reading or writing.
 * <p>
 * Exceptions
 * ----------
 * The Read-Write Lock class may raise the following exceptions:
 * <p>
 * - <code>LockTimeoutException</code>: Raised when the lock acquisition times out.
 * - <code>LockException</code>: Raised when there is an error acquiring or releasing the lock.
 */
public class SimpleUnfairReadWriteLock {
    private AtomicInteger numberOfReads;
    private AtomicReference<Thread> writeLock;
    private AtomicReference<Boolean> readLock;

    public SimpleUnfairReadWriteLock() {
        this.numberOfReads = new AtomicInteger(0);
        this.writeLock = new AtomicReference<>(null);
        this.readLock = new AtomicReference<>(false);
    }

    public void lockRead() throws InterruptedException {
        // Acquire lock to not allow other threads to change the value
        while (!this.readLock.compareAndSet(false, true)){
            // Spin to acquire change lock
        }
        while (this.writeLock.get() != null){
            Thread.yield();
        }

        this.numberOfReads.incrementAndGet();
        this.readLock.set(false);
    }

    public void unlockRead() {
        if(this.numberOfReads.get() == 0){
            throw new RuntimeException("Not holding any readLock");
        }

        this.numberOfReads.decrementAndGet();
    }

    /**
     * Will give the write lock only when there's no read lock acquired as well as no other thread already holds
     * the write lock.
     * @throws InterruptedException the interrupted exception
     */
    public void lockWrite() throws InterruptedException {
        while(!this.readLock.compareAndSet(false, true)){

        }
        while (this.numberOfReads.get() > 0){
            // Spin to stop acquire read lock
        }
        while (!this.writeLock.compareAndSet(null, Thread.currentThread())){
            // Spin and wait to acquire lock
        }
        this.readLock.set(false);
    }

    /**
     * If this method called holding thread releases the write lock.
     */
    public void unlockWrite() {
        if(this.writeLock.get() == null){
            throw new RuntimeException("Thread doesnt hold the writed lock");
        }
        this.writeLock.set(null);
    }

    public static void main(String[] args) {
        final SimpleUnfairReadWriteLock lock = new SimpleUnfairReadWriteLock();
        final int sharedResource[] = {0}; // Simple shared resource for demonstration

        // Runnable for read operation
        Runnable readTask = () -> {
            try {
                lock.lockRead();
                // Simulating read operation duration
                System.out.println("Read operation: Shared Resource = " + sharedResource[0]);
                Thread.sleep(100L * sharedResource[0]);
                lock.unlockRead();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // Runnable for write operation
        Runnable writeTask = () -> {
            try {
                lock.lockWrite();
                sharedResource[0]++;
                System.out.println("Write operation: Incremented Shared Resource to " + sharedResource[0]);
                // Simulating write operation duration
                Thread.sleep(100);
                lock.unlockWrite();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // Starting reader and writer threads
        Thread[] readers = new Thread[5];
        Thread[] writers = new Thread[5];
        for (int i = 0; i < 5; i++) {
            readers[i] = new Thread(readTask);
            writers[i] = new Thread(writeTask);
            readers[i].start();
            writers[i].start();
        }

        // Waiting for all threads to finish
        for (int i = 0; i < 5; i++) {
            try {
                readers[i].join();
                writers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Final Shared Resource value: " + sharedResource[0]);
    }
}
