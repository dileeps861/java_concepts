# Fundamental OS and Java concepts demonstration

## Concurrency Library
Want to get a little more info about the mutex (locks), conditional variables or semaphores which are the core of the multithreading? Read about them a bit more here: [MutexAndCVIntro.md](src/main/java/dileepshah/dev/os/concurrency/MutexAndCVIntro.md)
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

#### Simple Unfair Read-Write Lock [SimpleUnfairReadWriteLock.java](src/main/java/dileepshah/dev/os/concurrency/SimpleUnfairReadWriteLock.java)
A custom implementation of an unfair read-write lock that manages access to shared resources in a multithreaded environment. This lock allows multiple readers to access the resource simultaneously, but only one writer at a time, and does not guarantee fairness, which might lead to writer or reader starvation.

##### Key Features:
- **Read and Write Lock Mechanisms**: Allows multiple threads to read simultaneously or one thread to write exclusively.
- **Atomic Operations**: Utilizes atomic operations like `compareAndSet` to manage lock states without traditional locking mechanisms.
- **Busy-Waiting Concurrency Control**: Employs busy-waiting loops to handle lock acquisition and release, optimizing response times at the cost of CPU usage.
- **Starvation Possibility**: As an unfair lock, it does not prevent potential starvation of writers or readers, which allows for greater throughput under less contentious conditions.

##### Implementation Details:
- **Read Lock Acquisition**: Ensures that no write operation is in progress and atomically increases the reader count.
- **Write Lock Acquisition**: Blocks new readers and waits for active readers to finish before allowing write operations to proceed.
- **Efficient State Management**: Uses `AtomicInteger` and `AtomicReference` to manage state changes and ensure thread safety.

This lock is designed for scenarios where the overhead of fairness is less critical than performance, making it suitable for high-performance applications where read operations significantly outnumber write operations.

### SimpleConcurrentKVStore and ImprovedKVStore README Documentation

---

#### Simple Concurrent Key-Value Store [SimpleConcurrentKVStore.java](src/main/java/dileepshah/dev/os/concurrency/SimpleConcurrentKVStore.java)
A basic implementation of a thread-safe key-value store that supports concurrent access using explicit locking mechanisms. This class ensures that multiple threads can safely read from and write to the store without data corruption.

##### Key Features:
- **Thread-Safe Operations**: Ensures safe access and modifications to the key-value store by multiple threads using `ReentrantLock`.
- **Basic CRUD Operations**: Supports basic operations such as `get`, `put`, and `delete`.
- **Exception Handling**: Validates input and handles exceptions by throwing `IllegalArgumentException` for invalid keys or values.
- **Lock-Based Concurrency Control**: Uses `ReentrantLock` to manage access to the store, providing mutual exclusion.

##### Implementation Details:
- **Get Operation**: Acquires the lock, checks if the key is valid and present, and retrieves the value.
- **Put Operation**: Acquires the lock, validates the key and value, and stores the key-value pair.
- **Delete Operation**: Acquires the lock, checks if the key is valid and present, and removes the key-value pair.
- **Thread Safety**: Uses `ReentrantLock` to ensure that only one thread can access the critical section of code at a time.

---

#### Improved Concurrent Key-Value Store [ImprovedSimpleConcurrentKVStore.java](src/main/java/dileepshah/dev/os/concurrency/ImprovedSimpleConcurrentKVStore.java)
An enhanced implementation of a concurrent key-value store that utilizes segment-based locking to improve concurrency and performance. This approach divides the store into segments, each with its own lock, allowing for more granular control over access.

##### Key Features:
- **Segment-Based Locking**: Divides the key-value store into multiple segments, each managed by a separate `SimpleConcurrentKVStore`, to reduce contention.
- **Improved Concurrency**: Allows multiple threads to operate on different segments simultaneously, enhancing throughput.
- **Scalable Design**: The number of segments can be configured to balance the trade-off between concurrency and memory usage.
- **Thread-Safe Operations**: Ensures safe access and modifications to the key-value store using segment-specific locking mechanisms.

##### Implementation Details:
- **Initialization**: Creates a configurable number of segments, each as an instance of `SimpleConcurrentKVStore`.
- **Hashing**: Uses a hash function to determine the segment for a given key, ensuring even distribution of keys across segments.
- **Segment Access**: Delegates `get`, `put`, and `delete` operations to the appropriate segment based on the hashed key.
- **Exception Handling**: Validates input and handles exceptions by throwing `IllegalArgumentException` for invalid keys or values.


#### SimpleCriterionBasedConcurrentRateLimiter[SimpleCriterionBasedConcurrentRateLimiter.java](src/main/java/dileepshah/dev/os/concurrency/SimpleCriterionBasedConcurrentRateLimiter.java)

A rate limiter implementation that uses a key-value store to manage rate limiters for different access criteria. Each access criterion has its own rate limiter, allowing for independent rate limiting rules.

##### Key Features:
- **Criterion-Based Rate Limiting**: Manages rate limiting independently for different access criteria.
- **Thread-Safe Operations**: Ensures safe access and modifications to the rate limiter store using `ReentrantLock`.
- **Dynamic Management**: Automatically creates a new rate limiter for new access criteria.
- **Customizable Limits**: Allows configuration of the time window and the maximum number of allowed requests per window.

##### Implementation Details:
- **Initialization**: Initializes an `ImprovedSimpleConcurrentKVStore` to store rate limiters for different access criteria.
- **Rate Limiter Creation**: Creates a new `SimpleGenericConcurrentRateLimiter` for new access criteria and stores it in the key-value store.
- **Thread Safety**: Uses `ReentrantLock` to ensure thread-safe access and modification of the key-value store.
- **Request Access**: Delegates access requests to the appropriate rate limiter based on the access criteria.
- **Exception Handling**: Handles invalid keys and values by throwing `IllegalArgumentException`.


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
##### SimpleCriterionBasedConcurrentRateLimiter

To test the `SimpleCriterionBasedConcurrentRateLimiter` in a multithreaded environment with multiple IP address criteria, you can use the following `main` method:

```java
package dileepshah.dev.os.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main class to test SimpleCriterionBasedConcurrentRateLimiter in a multithreaded environment.
 */
public class SimpleCriterionBasedConcurrentRateLimiterTest {

    public static void main(String[] args) {
        final int numberOfThreads = 10;
        final long requestWindow = 1000; // 1 second
        final long allowedNoOfRequests = 5; // Allow 5 requests per second

        // Instantiate the rate limiter
        SimpleCriterionBasedConcurrentRateLimiter<String> rateLimiter = new SimpleCriterionBasedConcurrentRateLimiter<>(requestWindow, allowedNoOfRequests);

        // Create a thread pool
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        // Define IP addresses as criteria
        String[] ipAddresses = {
            "192.168.0.1", "192.168.0.2", "192.168.0.3",
            "192.168.0.4", "192.168.0.5"
        };

        // Define a task that requests access
        Runnable createAccessTask(String ipAddress) {
            return () -> {
                for (int i = 0; i < 10; i++) {
                    boolean isAllowed = rateLimiter.requestAccess(ipAddress);
                    if (isAllowed) {
                        System.out.println(ipAddress + " was allowed access.");
                    } else {
                        System.out.println(ipAddress + " was denied access.");
                    }
                    try {
                        // Simulate requests at intervals
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            };
        }

        // Submit the tasks to the executor for each IP address
        for (String ipAddress : ipAddresses) {
            executor.submit(createAccessTask(ipAddress));
        }

        // Shutdown the executor
        executor.shutdown();
        try {
            if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow(); // Cancel currently executing tasks
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        // Final output to check the last state
        System.out.println("Rate limiter test completed.");
    }
}
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


#### Demo  git operations
