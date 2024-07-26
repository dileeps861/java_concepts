package dileepshah.dev.os.concurrency;

import java.util.Calendar;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DelayQueue<T extends DelayQueue.Task> {
    int size;
    PriorityQueue<T> priorityQueue;
    Lock lock;
    Condition emptyCondition;
    Condition fullCondition;

    public DelayQueue(int size) {
        this.size = size;
        this.lock = new ReentrantLock();
        this.fullCondition = this.lock.newCondition();
        this.emptyCondition = this.lock.newCondition();
        this.priorityQueue = new PriorityQueue<>();
        // Start a demon thread that runs for scheduled tasks
        new Thread(this::runWorker).start();
    }

    public static void main(String[] args) {
        DelayQueue<Task> dq = new DelayQueue<>(2);

        for (int i = 0; i < 5; i++) {
            Task t = new Task("t" + i, 200);
            Thread tr = new Thread(() -> dq.scheduledTask(t));
            tr.start();
        }
        Task t = new Task("t6", 10000);
        Thread tr = new Thread(() -> dq.scheduledTask(t));
        tr.start();

//        Another way of creating wait and notify with priority blocking queue and wait notify methods
//        we can wait when the queue is empty or when there's no task to execute or have timed wait when there is
//        a task but is at some delay
//        and notify when there's as soon as a new task is added to the queue
        PriorityBlockingQueue<Task> priorityBlockingQueue = new PriorityBlockingQueue<>();
        try {
            priorityBlockingQueue.wait();
            priorityBlockingQueue.notify();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Given the task object, it adds to the queue and tries to run the task at scheduled time
     *
     * @param task the task to schedule
     */
    public void scheduledTask(T task) {
        lock.lock();
        while (priorityQueue.size() >= this.size) {
            try {
                this.emptyCondition.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        this.priorityQueue.offer(task);
        this.fullCondition.signal();
        lock.unlock();

    }

    private void runWorker() {
        while (true) {
            lock.lock();
            try {
                while (priorityQueue.isEmpty() || priorityQueue.peek().getStartTime() > Calendar.getInstance().getTimeInMillis()) {
                    long sleepTime = priorityQueue.isEmpty() ? Long.MAX_VALUE : priorityQueue.peek().getStartTime() - Calendar.getInstance().getTimeInMillis();
                    if (sleepTime > 0) {
                        fullCondition.awaitNanos(sleepTime * 1_000_000);
                    }
                }
                priorityQueue.poll().run();
                emptyCondition.signal();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * This class represents the task which can be passed to delayed queue to schedule.
     */
    static class Task implements Runnable, Comparable<Task> {
        private final String taskName;
        private final long startTime;

        public Task(String taskName, long delayInMillis) {
            this.startTime = Calendar.getInstance().getTimeInMillis() + delayInMillis;
            this.taskName = taskName;
        }

        @Override
        public void run() {
            System.out.println("Entered the task=" + taskName + " for running..");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Done the task=" + taskName + " execution..");
        }

        public String getTaskName() {
            return taskName;
        }

        public long getStartTime() {
            return startTime;
        }

        @Override
        public int compareTo(Task o) {
            return Long.compare(this.startTime, o.startTime);
        }
    }
}
