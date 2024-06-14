package dileepshah.dev.multithreading.semaphore;

import java.util.concurrent.Semaphore;

/**
 * <div>
 * <p>Created by dileepshah on 2024-06-13 </p>
 * </br>
 * This class is used to show how to use Semaphore. Semaphore is used to limit the number of threads that can access
 * a resource. In this example, we will understand how to use Semaphore and the need for it.
 *
 * <h3> Real world example: </h3>
 * </br>
 * <p>
 * <b>1. Connection Pool Management: </b> Say we want to limit the number of connections to a database. We can use Semaphore to limit the number of
 * connection pools that can be created.
 * </p>
 * <p>
 * <b>2. Reader-Writer Problem: </b>This scenario involves managing access to a shared file by multiple threads.
 * Semaphores can be used to ensure only one thread writes to the file at a time, while allowing multiple threads to
 * read concurrently. This prevents data corruption caused by simultaneous writes.
 ** </p>
 * </div>
 *
 * @see java.util.concurrent.Semaphore
 */
public class SemaphoreExample {

    public static void main(String[] args) {
        // So how to use Semaphore? This is a simple example. It shows how to use it.
        // How it works is when the same lock is used by multiple threads, then only one thread will be able to acquire
        // the lock.
        // So as we see below, there are 5 threads created, and the semaphore allows two limits to acquire the lock.
        // So only 2 threads will be able to acquire the lock, and the other 3 threads will be blocked until lock is release.

        // Practical use case of Semaphore is to limit the number of threads that can access a resource.
        // Say we want to manage the maximum number of connections to a database. We can use Semaphore to limit the number of
        // connection pools that can be created.
        final Semaphore semaphore = new Semaphore(2);

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + " is running");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                }
            }).start();
        }
    }
}
