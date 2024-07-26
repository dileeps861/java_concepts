package dileepshah.dev.os.concurrency;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is a simple scheduled executor service that uses the fixed executor service and then can be used to schedule the tasks to run in future.
 * How does this work? We pass in the number of threads required then we create fixed size executor service
 * The whenever any executor finishes the execution, it picks up the task which is at the schedule and is waiting.
 * We use the conditional variables and locks to wait signal and mutex to allow the thread to be aware of the tasks and statuses.
 */
public class ScheduledExecutorService<T extends Task> {
    private final int size;
    private final FixedSizeExecutorService<T> fixedSizeExecutorService;
    private final PriorityBlockingQueue<T> priorityBlockingQueue;
    private final AtomicBoolean isRunning;

    public ScheduledExecutorService(int size) {
        this.size = size;
        this.fixedSizeExecutorService = new FixedSizeExecutorService<>(size);
        this.isRunning = new AtomicBoolean(true);
        this.priorityBlockingQueue = new PriorityBlockingQueue<>();

        // Scheduler thread
        new Thread(this::startWorker).start();
    }

    public static void main(String[] args) {
        final ScheduledExecutorService<Task> scheduledExecutorService = new ScheduledExecutorService<>(2);

        for (int i = 0; i < 5; i++) {
            long delay = (i + 1) * 1000; // 1, 2, 3, 4, 5 seconds delay
            DelayTask task = new DelayTask(i, delay);
            scheduledExecutorService.scheduleTask(task);
        }

        // Allow some time for tasks to complete
        try {
            Thread.sleep(10000); // 10 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        scheduledExecutorService.shutdown();
        System.out.println("Scheduled executor service has shut down.");
    }

    public void scheduleTask(T task) {
        if (task == null) {
            throw new IllegalArgumentException("Task to be executed cannot be null.");
        }
        synchronized (this.priorityBlockingQueue) {
            this.priorityBlockingQueue.offer(task);
            this.priorityBlockingQueue.notifyAll();
        }
    }

    private void startWorker() {
        while (true) {
            synchronized (this.priorityBlockingQueue) {
                while (this.priorityBlockingQueue.isEmpty() && this.isRunning.get()) {
                    try {
                        this.priorityBlockingQueue.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                if (!this.isRunning.get() && this.priorityBlockingQueue.isEmpty()) {
                    System.out.println("Shutdown conditions met, shutting down scheduler...");
                    break;
                }

                while (!this.priorityBlockingQueue.isEmpty() && this.priorityBlockingQueue.peek().getStartTime() > System.currentTimeMillis()) {
                    try {
                        this.priorityBlockingQueue.wait(this.priorityBlockingQueue.peek().getStartTime()
                                - System.currentTimeMillis());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                if (!this.priorityBlockingQueue.isEmpty()) {
                    T task = this.priorityBlockingQueue.poll();
                    this.fixedSizeExecutorService.publishTask(task);
                    this.priorityBlockingQueue.notifyAll();
                }
            }
        }
    }

    public void shutdown() {
        isRunning.set(false);
        synchronized (this.priorityBlockingQueue) {
            this.priorityBlockingQueue.notifyAll();
        }
        fixedSizeExecutorService.shutdown();
    }

    /**
     * Schedule task which can be started with a given delay.
     */
    static class DelayTask implements Task {
        private final int id;
        private final long startTime;

        public DelayTask(int id, long delayInMillis) {
            this.startTime = System.currentTimeMillis() + delayInMillis;
            this.id = id;
        }

        @Override
        public String getName() {
            return "Task_" + this.id;
        }

        @Override
        public long getStartTime() {
            return this.startTime;
        }

        @Override
        public int compareTo(Task o) {
            return Long.compare(this.startTime, o.getStartTime());
        }

        @Override
        public void run() {
            System.out.println("Running task " + id + " at " + System.currentTimeMillis());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Task " + id + " was interrupted.");
            }
            System.out.println("Finished task " + id + " at " + System.currentTimeMillis());
        }
    }
}