package dileepshah.dev.multithreading.virtualthreads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VirtualThreadsExample {
    public static void main(String[] args) {
        // So how to use virtual threads? This is a simple example. It shows how to use it.
        // You can use it in your code.
        // What is the benefit of using virtual thread per-task executor?
        // The benefit of using virtual thread per task executor is that threads are not created until the task is submitted to the executor.

        // Creates virtual thread per task executor, which means each task is executed in a separate thread
        // What is the benefit of using virtual thread per-task executor?
        // The benefit of using virtual thread per task executor is that threads are not created until the task is submitted to the executor.
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        executorService.submit(() -> {
            // Thread will be like: VirtualThread[#21]/runnable@ForkJoinPool
            System.out.println(Thread.currentThread());
            // Virtual threads are not having a name, so we are using Thread.currentThread().getName() which will return ""
            System.out.println(String.format("%s",Thread.currentThread().getName()));
        });

        executorService.submit(() -> {
            // Thread will be like: VirtualThread[#21]/runnable@ForkJoinPool
            System.out.println(Thread.currentThread());
            // Virtual threads are not having a name, so we are using Thread.currentThread().getName() which will return ""
            System.out.println(String.format("%s",Thread.currentThread().getName()));
        });

        // Shut down the ExecutorService
        executorService.shutdown();
        System.out.println("All threads by executor have completed.");

        // Create a virtual thread
        Thread virtualThread = Thread.ofVirtual().start(() -> {
            System.out.println("Running in a virtual thread: " + Thread.currentThread());
        });

        // Wait for the virtual thread to finish
        try {
            virtualThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Creating a large number of virtual threads
        for (int i = 0; i < 100_000; i++) {
            Thread.ofVirtual().start(() -> {
                System.out.println("Task " + Thread.currentThread());
            });
        }

        System.out.println("All threads have completed.");
    }
}
