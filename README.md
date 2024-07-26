# Fundamental OS and Java concepts demonstration

## Concurrency Library
Want to get a little more info about the mutex, conditional variables or semaphores which are the core of the multithreading? Read about them a bit more here: [MutexAndCVIntro.md](src/main/java/dileepshah/dev/os/concurrency/MutexAndCVIntro.md)
### Overview
This library provides implementations of various concurrency primitives and executor services using OS-level threads. The components include a blocking queue, a delayed queue, a fixed-size executor service, and a scheduled executor service built on top of the fixed-size executor service.

### Components

#### Blocking Queue [SimpleBlockingQueue.java](src/main/java/dileepshah/dev/os/concurrency/SimpleBlockingQueue.java)

A thread-safe blocking queue implementation that supports concurrent operations by multiple producer and consumer threads. It uses an internal queue and condition variables to manage blocking and signaling for thread synchronization.

##### Key Features:
- **Thread Safety**: Ensures safe concurrent access by multiple threads.
- **Blocking Operations**: Supports `put` and `take` operations that block when the queue is full or empty, respectively.
- **Condition Variables**: Uses `wait`, `notify`, and `notifyAll` for efficient thread synchronization.

#### Delayed Queue [DelayQueue.java](src/main/java/dileepshah/dev/os/concurrency/DelayQueue.java)
A queue that supports scheduling tasks to be executed after a specified delay. Tasks are ordered by their scheduled execution time, and a dedicated worker thread processes tasks when their delay has expired.

##### Key Features:
- **Scheduled Execution**: Allows tasks to be scheduled for execution after a specified delay.
- **Priority Queue**: Uses a priority queue to order tasks by their execution time.
- **Worker Thread**: A dedicated thread continuously checks and executes tasks whose delay has expired.

#### Executor Service of Fixed Size [FixedSizeExecutorService.java](src/main/java/dileepshah/dev/os/concurrency/FixedSizeExecutorService.java)
A fixed-size executor service that manages a pool of worker threads to execute submitted tasks. The number of threads in the pool is specified at creation time, and tasks are queued if all threads are busy.

##### Key Features:
- **Fixed Thread Pool**: Manages a fixed number of worker threads.
- **Task Queueing**: Queues tasks when all threads are busy and executes them as threads become available.
- **Graceful Shutdown**: Provides a mechanism to shut down the executor service gracefully, ensuring all tasks are completed.

#### Scheduled Executor Service [ScheduledExecutorService.java](src/main/java/dileepshah/dev/os/concurrency/ScheduledExecutorService.java)
A scheduled executor service that builds on top of the fixed-size executor service to support scheduling tasks with delays. Tasks are executed by the fixed-size executor service after their specified delay has expired.

##### Key Features:
- **Delayed Task Scheduling**: Supports scheduling tasks to run after a specified delay.
- **Integration with Fixed-Size Executor**: Leverages the fixed-size executor service to execute tasks.
- **Efficient Scheduling**: Uses a priority queue and a worker thread to manage task scheduling and execution.

### Usage Examples

#### Blocking Queue
```java
BlockingQueue<Integer> queue = new BlockingQueue<>(10);

// Producer thread
new Thread(() -> {
    try {
        queue.put(1);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}).start();

// Consumer thread
new Thread(() -> {
    try {
        Integer item = queue.take();
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}).start();
```

#### Delayed Queue
```java
DelayedQueue<Runnable> delayedQueue = new DelayedQueue<>();

delayedQueue.put(() -> System.out.println("Task executed!"), 1000); // Delay of 1 second

delayedQueue.start();
```

#### Executor Service of Fixed Size
```java
FixedSizeExecutorService<Runnable> executorService = new FixedSizeExecutorService<>(2);

executorService.submit(() -> System.out.println("Task 1 executed!"));
executorService.submit(() -> System.out.println("Task 2 executed!"));

executorService.shutdown();
```

#### Scheduled Executor Service
```java
ScheduledExecutorService<ScheduledExecutorService.DelayTask> scheduledExecutorService = new ScheduledExecutorService<>(2);

for (int i = 0; i < 5; i++) {
    long delay = (i + 1) * 1000; // 1, 2, 3, 4, 5 seconds delay
    ScheduledExecutorService.DelayTask task = new ScheduledExecutorService.DelayTask(i, delay);
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
```

### Conclusion
This library provides robust concurrency primitives and executor services for handling multithreaded task execution. By using these components, developers can efficiently manage concurrent tasks, schedule future tasks, and ensure thread safety in their applications.

## Java multithreading

### Threads creation and destruction

### Synchronization, volatile, and Atomic classes

### Intrinsic locking and possible issues

### Lock and Reentrant locks
- Demonstrates locking and unlocking along with deadlocking and how tryLock helps protect against deadlocking.

### ThreadsFactory 
### Executors, ThreadPoolExecutor, and ExecutorService

### Latches and CountDownLatch

### Barrier/ Cyclic Barriers

### Semaphore

### Exchanger

You can find the details about `Exchanger` [here in Exchanger.md](src/main/java/dileepshah/dev/multithreading/exchanger/Exchanger.md).

### Phaser
### Executors
### Fork-Join Pool

### BlockingQueues and Pub-Sub
#### Demonstrate Pub-Sub(MQ) for log event writing: [Event logging Demo](src/main/java/dileepshah/dev/eventlogging/README.md).
