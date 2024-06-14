package dileepshah.dev.multithreading.lockrentrantlock.deadlock;

import lombok.AllArgsConstructor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@AllArgsConstructor
public class ThreadForDeadLock implements Runnable {
    Lock lock1;
    Lock lock2;
    @Override
    public void run() {
        // Let's cause a deadlock
        lock1.lock();

        System.out.println(Thread.currentThread().getName() + " is running");
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " done running");

        lock2.lock();
        System.out.println("Deadlock is resolved for " + Thread.currentThread().getName());
        lock1.unlock();
        lock2.unlock();
    }
}
