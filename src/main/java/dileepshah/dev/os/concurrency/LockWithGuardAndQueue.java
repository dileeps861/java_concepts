package dileepshah.dev.os.concurrency;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * Guard Flag: This flag is used to queue threads for access to the lock. It ensures that only one thread can proceed
 * to attempt acquiring the actual lock.
 * Mutex Flag: After acquiring the guard, the thread will attempt to acquire the mutex for the critical section using
 * a second flag.
 * Queue for Parked Threads: If a thread cannot acquire the mutex (because another thread holds it), it is added to a
 * queue and parked until it is its turn to be unparked.
 * <p>
 * Implementation Plan:
 * Guard Flag: Ensures orderly queuing for lock acquisition.
 * Mutex Flag: Once the guard is acquired, the thread will try to acquire the actual lock.
 * Queue for Thread Management: Threads that fail to acquire the mutex will be parked in a queue and unparked once
 * the lock is released.
 */
public class LockWithGuardAndQueue {
    private final AtomicBoolean guard = new AtomicBoolean(false);  // Guard for queuing
    private final AtomicBoolean mutex = new AtomicBoolean(false);  // Mutex for critical section
    private final Queue<Thread> waitQueue = new LinkedList<>();    // Queue to manage parked threads

    // Acquire lock
    public void lock() {
        // Acquire guard to queue threads
        while (!guard.compareAndSet(false, true)) {
            // Guard not available, spin until we get it
        }

        // Try to acquire the actual lock (mutex)
        if (!mutex.compareAndSet(false, true)) {
            // If mutex is held by another thread, park current thread and add to wait queue
            Thread currentThread = Thread.currentThread();
            waitQueue.add(currentThread);
            guard.set(false);  // Release guard after parking
            LockSupport.park();  // Park the current thread
        } else {
            // If lock acquired, release guard for others to try
            guard.set(false);
        }
    }

    // Release lock
    public void unlock() {
        // Acquire guard to queue threads
        while (!guard.compareAndSet(false, true)) {
            // Guard not available, spin until we get it
        }
        if(waitQueue.isEmpty()){
            mutex.set(false);
        }

        Thread nextThread = waitQueue.poll();
        if (nextThread != null) {
            LockSupport.unpark(nextThread);  // Unpark the next waiting thread
        }

        guard.set(false);
    }
}

class SharedResource {
    private final LockWithGuardAndQueue lock = new LockWithGuardAndQueue();

    public void doWork(String threadName) {
        lock.lock();
        try {
            System.out.println(threadName + " is doing work...");
            Thread.sleep(1000);  // Simulate work
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}

class Worker implements Runnable {
    private final SharedResource resource;
    private final String threadName;

    public Worker(SharedResource resource, String threadName) {
        this.resource = resource;
        this.threadName = threadName;
    }

    @Override
    public void run() {
        resource.doWork(threadName);
    }
}

class CustomLockWithGuardDemo {
    public static void main(String[] args) {
        SharedResource resource = new SharedResource();

        // Create and start multiple worker threads
        Thread t1 = new Thread(new Worker(resource, "Thread-1"));
        Thread t2 = new Thread(new Worker(resource, "Thread-2"));
        Thread t3 = new Thread(new Worker(resource, "Thread-3"));

        t1.start();
        t2.start();
        t3.start();
        try{
            t1.join();
            t2.join();
            t3.join();
        } catch (Exception e){

        }

    }
}

