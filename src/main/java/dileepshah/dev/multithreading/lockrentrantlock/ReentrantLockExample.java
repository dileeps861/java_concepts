package dileepshah.dev.multithreading.lockrentrantlock;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@AllArgsConstructor
public class ReentrantLockExample implements Runnable {
    private final Lock lock;

    @SneakyThrows
    public void run() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " is running");
            Thread.sleep(TimeUnit.MICROSECONDS.toMillis(1));
            System.out.println(Thread.currentThread().getName() + " done running");
        } finally {
            lock.unlock();
        }
    }
}
