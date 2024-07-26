package dileepshah.dev.os.concurrency;

import java.util.concurrent.Semaphore;

public class SemaphoresDemo {

    private final Semaphore lock;

    public SemaphoresDemo(int readerCount) {
        this.lock = new Semaphore(readerCount); // 1 reader can acquire the lock can be used as mutex
    }

    public static void main(String[] args) {
        SemaphoresDemo semaphoresDemo = new SemaphoresDemo(5);
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    semaphoresDemo.lock.acquire();
                    System.out.println("T1 thread got the lock after getting the signal");
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                semaphoresDemo.lock.release();
                System.out.println("T2 thread sent the signal and waiting for the lock");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread1.start();
        thread2.start();
    }
}
