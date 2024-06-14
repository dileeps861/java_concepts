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
            // Thread will be like: irtualThread[#21]/
            System.out.println(Thread.currentThread());
            // Virtual threads are not having a name, so we are using Thread.currentThread().getName() which will return ""
            System.out.println(String.format("%s counting down... %d",Thread.currentThread().getName(), latch.getCount()));

            latch.countDown();
        };

        Runnable thread2 = () -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Thread will be like: irtualThread[#21]/
            System.out.println(Thread.currentThread());
            // Virtual threads are not having a name, so we are using Thread.currentThread().getName() which will return ""
            System.out.println(String.format("%s counting down... %d",Thread.currentThread().getName(), latch.getCount()));
            latch.countDown();
        };

        // Creates virtual thread per task executor, which means each task is executed in a separate thread
        // What is the benefit of using virtual thread per-task executor?
        // The benefit of using virtual thread per task executor is that threads are not created until the task is submitted to the executor.
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
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
