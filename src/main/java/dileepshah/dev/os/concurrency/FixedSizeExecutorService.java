package dileepshah.dev.os.concurrency;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is a simple executor service implementation with fixed size. We specify that what is the size of the thread pool
 * and then these threads could be used to execute the tasks given.
 */
public class FixedSizeExecutorService<T extends Task> {
    private final int size;
    private final BlockingQueue<T> queue;
    private final Thread[] threads;
    private final AtomicBoolean isRunning;

    public FixedSizeExecutorService(int size) {
        this.size = size;
        this.queue = new ArrayBlockingQueue<>(10);
        this.threads = new Thread[size];
        this.isRunning = new AtomicBoolean(true);
        for (int i = 0; i < size; i++) {
            threads[i] = new Thread(this::runTask);
            threads[i].start();
        }
    }

    public static void main(String[] args) {
        FixedSizeExecutorService<MyTask> executorService = new FixedSizeExecutorService<>(2);
        for (int i = 0; i < 10; i++) {
            executorService.publishTask(new MyTask(i));
        }
        executorService.shutdown();
        System.out.println("Executor service has shut down.");
    }

    public void publishTask(T task) {
        if (Objects.isNull(task)) {
            throw new IllegalArgumentException("Argument cannot be null to publish task.");
        }
        synchronized (this.queue) {
            while (this.queue.size() == this.size) {
                try {
                    this.queue.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            this.queue.add(task);
            this.queue.notifyAll();
        }
    }

    private void runTask() {
        while (isRunning.get() || !queue.isEmpty()) {
            T task;
            try {
                synchronized (this.queue) {
                    while (queue.isEmpty() && isRunning.get()) {
                        this.queue.wait();
                    }
                    if (!isRunning.get() && queue.isEmpty()) {
                        break;
                    }
                    task = this.queue.take();
                    this.queue.notifyAll();
                }
                task.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break; // Exit the loop if interrupted
            }
        }
    }

    public void shutdown() {
        isRunning.set(false);
        synchronized (this.queue) {
            this.queue.notifyAll();
        }
        for (Thread t : threads) {
            t.interrupt();
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class MyTask implements Task {
        private final int id;

        public MyTask(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            System.out.println("Running task " + id);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // Handle the interruption properly
                System.out.println("Task " + id + " was interrupted.");
                Thread.currentThread().interrupt();
            }
            System.out.println("Finished task " + id);
        }

        @Override
        public String getName() {
            return "Task_" + id;
        }

        @Override
        public long getStartTime() {
            return 0;
        }

        @Override
        public int compareTo(Task o) {
            return 0;
        }
    }
}
