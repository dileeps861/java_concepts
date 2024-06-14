package dileepshah.dev.multithreading.latches;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LatchesExample {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(2);

        Runnable thread1 = () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Thread 1 counting down...");
            latch.countDown();
        };

        Runnable thread2 = () -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Thread 2 counting down...");
            latch.countDown();
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(thread1);
        executorService.submit(thread2);
        try {
            // Wait for both threads to complete
            System.out.println("Latches waiting...");
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Latches done");
    }
}
