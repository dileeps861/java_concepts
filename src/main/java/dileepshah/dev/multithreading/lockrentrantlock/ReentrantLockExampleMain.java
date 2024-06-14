package dileepshah.dev.multithreading.lockrentrantlock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockExampleMain {

    public static void main(String[] args) {
        // So how to use ReentrantLock? This is a simple example. It shows how to use it.
        // You can use it in your code.

        // We use ReentrantLock to create a lock. We can also create a reentrant lock.
        // We can also create a non-reentrant lock.
        // We can also create a fair lock.
        // We can also create a non-fair lock.
        // We can also create a lock with a timeout.
        // We can also create a lock with a condition.
        // We can also create a lock with a fair condition.
        // We can also create a lock with a non-fair condition.
        // We can also create a lock with a timeout and a condition.
        // We can also create a lock with a timeout and a fair condition.
        // We can also create a lock with a timeout and a non-fair condition.
        // We can also create a lock with a timeout, a condition and a fair condition.
        // We can also create a lock with a timeout, a condition and a non-fair condition.

        // We can also create a lock with a timeout, a condition, a fair condition and a non-fair condition.

        // So how to use ReentrantLock? This is a simple example. It shows how to use it.
        // How it works is when same lock is used by multiple threads, then only one thread will be able to acquire the lock.

        // So as we see below there are 2 threads running, and same lock is passed to them. And in the ReentrantLockExample
        // we acquire the lock. So only one thread will be able to acquire the lock and the other thread will be blocked.
        Lock lockReentrant = new ReentrantLock();
        ReentrantLockExample example1 = new ReentrantLockExample(lockReentrant);
        ReentrantLockExample example2 = new ReentrantLockExample(lockReentrant);

        new Thread(example1).start();
        new Thread(example2).start();

        // Another way to use ReentrantLock is to use tryLock() method.
        // What is tryLock() method? It is used to acquire the lock in a try block.
        // If the lock is acquired then it will return true. If the lock is not acquired then it will return false.
        // This is useful to avoid deadlocks. If thread 1 acquires the lock and thread 2 tries to acquire the lock then thread 2 will be blocked.
        // So when deadlock occurs then because of tryLock() method then thread 2 will not be blocked.

        Lock lockTry = new ReentrantLock();
        Lock lockTry2 = new ReentrantLock();
        ThreadWithTryLock threadWithTryLock = new ThreadWithTryLock(lockTry, lockTry2);
        ThreadWithTryLock threadWithTryLock2 = new ThreadWithTryLock(lockTry2, lockTry);
        new Thread(threadWithTryLock).start();
        new Thread(threadWithTryLock2).start();
    }
}
