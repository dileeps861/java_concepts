package dileepshah.dev.multithreading.lockrentrantlock;

import lombok.AllArgsConstructor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@AllArgsConstructor
public class ThreadWithTryLock implements Runnable {
    private final Lock lock1;
    private final Lock lock2;

    @Override
    public void run() {
        // Let's handle deadlock with tryLock(), tryLock() will return true if the lock is acquired.
        // If the lock is acquired, then it will return true. If the lock is not acquired then it will return false.
        // This is useful to avoid deadlocks. If thread 1 acquires the lock and thread 2 tries to acquire the lock, then thread 2 will be blocked.
        // So when deadlock occurs, then because of tryLock() method then thread 2 will not be blocked.
        // Also, upon printing the lock is acquired, then it will print the lock name.
        // Example:
        // Thread-2 failed to acquire:java.util.concurrent.locks.ReentrantLock@6bc9f3b5[Locked by thread Thread-3]
        // Thread-3 failed to acquire:java.util.concurrent.locks.ReentrantLock@74b890e6[Locked by thread Thread-2]
        if (lock1.tryLock()) {
            System.out.println(Thread.currentThread().getName() + " is running");
            if (lock2.tryLock()) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("Deadlock is resolved for " + Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock2.unlock();
                    lock1.unlock();
                }
            }else {
                System.out.println(Thread.currentThread().getName() + " failed to acquire:" + lock2);
            }
        } else {
            System.out.println(Thread.currentThread().getName() + " is not running can not acquire lock");
        }
    }
}
