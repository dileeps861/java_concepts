package dileepshah.dev.multithreading.lockrentrantlock.deadlock;

import dileepshah.dev.multithreading.lockrentrantlock.ThreadWithTryLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadlockRun {
    public static void main(String[] args) {
        // Let's cause a deadlock
        // We can create a deadlock with 2 locks and 2 threads with the same locks.
        // Say thread1 first acquires lock1 and then tries to acquire lock2. And thread2 first acquires lock2 and then tries to acquire lock1.
        // So thread1 will be blocked waiting for lock2 and thread2 will be blocked waiting for lock1.
        Lock lock1 = new ReentrantLock();
        Lock lock2 = new ReentrantLock();
        Thread thread1 = new Thread(new ThreadForDeadLock(lock1, lock2));
        Thread thread2 = new Thread(new ThreadForDeadLock(lock2, lock1));
//        thread1.start();
//        thread2.start();

        // So how to handle deadlock?
        // We can use tryLock() method.
        // What is tryLock() method? It is used to acquire the lock in a try block.
        // If the lock is acquired then it will return true. If the lock is not acquired then it will return false.
        // This is useful to avoid deadlocks. If thread 1 acquires the lock and thread 2 tries to acquire the lock then thread 2 will be blocked.
        // So when deadlock occurs then because of tryLock() method then thread 2 will not be blocked.
        Lock lockTry1 = new ReentrantLock();
        Lock lockTry2 = new ReentrantLock();
        ThreadWithTryLock threadWithTryLock = new ThreadWithTryLock(lockTry1, lockTry2);
        ThreadWithTryLock threadWithTryLock2 = new ThreadWithTryLock(lockTry2, lockTry1);
        new Thread(threadWithTryLock).start();
        new Thread(threadWithTryLock2).start();
    }
}
