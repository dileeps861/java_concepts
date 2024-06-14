package dileepshah.dev.multithreading.phaser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class PhaserExample {

    public static void main(String[] args) {

        // So how to use Phaser? This is a simple example. It shows how to use it.
        // How it works is when a same task is passed to multiple threads, then only one thread will be able to execute
        // the task. So as we see below, there are 2 threads running, and a same task is passed to them.
        // And in the PhaserExample
        // we execute the task. So only one thread will be able to execute the task and the other thread will be blocked.
        Phaser phaser = new Phaser(3);

        Runnable task = () -> {
            for (int phase = 0; phase < 2; phase++) {
                System.out.println("Thread " + Thread.currentThread().getName() + " performing phase " + phase);
                // Simulate some work with sleep
                try {
                    Thread.sleep((long) (Math.random() * 1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Wait for all threads to complete the current phase
                phaser.arriveAndAwaitAdvance();
            }

            // Deregister from the phaser
            phaser.arriveAndDeregister();
        };
        // Create two threads using ExecutorService
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.execute(task);
        executorService.execute(task);
        executorService.execute(task);

        // Wait for all threads to complete
        while (phaser.getRegisteredParties() != 0) {
            // The Main thread can perform other tasks or just wait
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Shut down the ExecutorService
        executorService.shutdown();
        System.out.println("All threads have completed.");
    }
}
