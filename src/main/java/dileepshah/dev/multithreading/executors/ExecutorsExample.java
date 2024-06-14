package dileepshah.dev.multithreading.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by dileepshah on 2024-06-13
 * This class is used to show how to use Executors. Executors are used to execute tasks in a thread pool.
 * It eliminates the need to manually create and manage threads. It also provides a framework for asynchronous
 * execution of tasks.
 * Also, it provides a thread pool that allows multiple threads to execute tasks concurrently.
 * Executors are efficient in terms of both time and memory usage as they manage threads internally and threads are
 * cached in a thread pool.
 */
public class ExecutorsExample {
    public static void main(String[] args) {
        // There are 3 types of Executors: SingleThreadExecutor, CachedThreadPool, FixedThreadPool
        // SingleThreadExecutor is used to execute a task in a single thread.
        // CachedThreadPool is used to execute a task in multiple threads.
        // FixedThreadPool is used to execute a task in a fixed number of threads.
        // We can also create a ScheduledThreadPool.
        // We can also create a ScheduledExecutorService.
        // We can also create a ScheduledThreadPoolExecutor.
        // We can also create a ScheduledExecutorService.

        // So how to use Executors? This is a simple example. It shows how to use it.
        // How it works is when a same task is passed to multiple threads, then only one thread will be able to execute the task.
        // So as we see below, there are 2 threads running, and a same task is passed to them. And in the ExecutorsExample
        // we execute the task. So only one thread will be able to execute the task and the other thread will be blocked.
        Runnable task = () -> {
            System.out.println(Thread.currentThread().getName() + " is running");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " done running");
        };
        // Traditional way of creating threads
        //        Thread thread1 = new Thread(task);
        //        Thread thread2 = new Thread(task);
        //        thread1.start();
        //        thread2.start();

        // Let's just first create single thread executor
        System.out.println("\nLet's create a single thread executor: ");

        ExecutorService executorService1 = Executors.newSingleThreadExecutor();
        executorService1.execute(task);
        executorService1.execute(task);
        executorService1.shutdown(); // This is used to shut down the thread pool. This is used to shut down the thread pool.
        // If we don't shut down the thread pool, then it will continue to execute the task.

        System.out.println("\n \nLet's create a thread pool with 2 threads with newFixedThreadPool: ");

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(task);
        executorService.execute(task);

        executorService.shutdown();

        System.out.println("\n \nLet's create a thread pool with 2 threads but 5 tasks: ");
        // Let's create a thread pool with 2 threads
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 5; i++) {
            newFixedThreadPool.execute(task);
        }
        newFixedThreadPool.shutdown();

        // Let's create CachedThreadPool, which is used to execute a task in multiple threads.
        // How is it different from FixedThreadPool?
        // FixedThreadPool is used to execute a task in a fixed number of threads.
        // CachedThreadPool is used to execute a task in multiple threads. And threads are cached in a thread pool.
        System.out.println("\n \nLet's create a CachedThreadPool: ");
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            newCachedThreadPool.execute(task);
        }
        newCachedThreadPool.shutdown();

        // Let's create a ScheduledThreadPool, which is used to execute a task in multiple threads.
        // How is it different from FixedThreadPool?
        // FixedThreadPool is used to execute a task in a fixed number of threads.
        // ScheduledThreadPool is used to schedule a task in multiple threads.
        // Scheduled thread pool can be scheduled for a fixed time or for a fixed period of time.
        System.out.println("\n \nLet's create a ScheduledThreadPool: ");
        ScheduledExecutorService newScheduledThreadPool = Executors.newScheduledThreadPool(2);
        newScheduledThreadPool.scheduleAtFixedRate(task, 1, 2, TimeUnit.SECONDS);
        for (int i = 0; i < 1; i++) {
            newScheduledThreadPool.execute(task);
        }
        // Let's create a ScheduledExecutorService, to keep running at fixed rate commented out below line
        newScheduledThreadPool.shutdown();

    }
}
