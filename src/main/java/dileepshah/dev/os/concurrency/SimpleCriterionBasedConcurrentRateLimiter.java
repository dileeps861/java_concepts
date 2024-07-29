package dileepshah.dev.os.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * SimpleCriterionBasedConcurrentRateLimiter is a rate limiter implementation that uses
 * a key-value store to manage rate limiters for different access criteria.
 *
 * @param <T> The type of access criteria used for the rate limiting.
 */
public class SimpleCriterionBasedConcurrentRateLimiter<T> {
    private final ImprovedSimpleConcurrentKVStore<T, SimpleGenericConcurrentRateLimiter<T>> kvStore;
    private final Lock lock;
    private final long allowedNoOfRequests;
    private final long requestWindow;

    /**
     * Constructs a SimpleCriterionBasedConcurrentRateLimiter with a specified request window and allowed number of requests.
     *
     * @param requestWindow       The time range (in milliseconds) within which a certain number of requests are allowed.
     * @param allowedNoOfRequests The maximum number of requests allowed within the specified time range.
     */
    public SimpleCriterionBasedConcurrentRateLimiter(long requestWindow, long allowedNoOfRequests) {
        this.kvStore = new ImprovedSimpleConcurrentKVStore<>();
        this.lock =  new ReentrantLock();
        this.requestWindow = requestWindow;
        this.allowedNoOfRequests = allowedNoOfRequests;
    }

    /**
     * Requests access based on the specified access criteria. This method checks if the access request
     * can be granted based on the rate limiting rules.
     *
     * @param accessCriteria The criteria for the access request.
     * @return true if the request is allowed, false otherwise.
     */
    public boolean requestAccess(T accessCriteria) {
        this.lock.lock();
        try {
            if (!this.kvStore.containsKey(accessCriteria)) {
                this.kvStore.put(accessCriteria, new SimpleGenericConcurrentRateLimiter<>(this.requestWindow,
                        this.allowedNoOfRequests));
            }
        }finally {
            this.lock.unlock();
        }
        return this.kvStore.get(accessCriteria).requestAccess(accessCriteria);
    }

    // Define a task that requests access
    static Runnable createAccessTask(String ipAddress, SimpleCriterionBasedConcurrentRateLimiter<String> rateLimiter) {
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
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
    }

    public static void main(String[] args) {
        final int numberOfThreads = 10;
        final long requestWindow = 1000; // 1 second
        final long allowedNoOfRequests = 5; // Allow 5 requests per second

        // Instantiate the rate limiter
        SimpleCriterionBasedConcurrentRateLimiter<String> rateLimiter =
                new SimpleCriterionBasedConcurrentRateLimiter<>(requestWindow, allowedNoOfRequests);
        // Create a thread pool
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        // Define IP addresses as criteria
        String[] ipAddresses = {
                "192.168.0.1", "192.168.0.2", "192.168.0.3",
                "192.168.0.4", "192.168.0.5"
        };


        // Submit the tasks to the executor for each IP address
        int i = 0;
        while (i < numberOfThreads) {
            executor.submit(createAccessTask(ipAddresses[i % 4], rateLimiter));
            i ++;
        }

        // Shutdown the executor
        executor.shutdown();
        try {
            if (!executor.awaitTermination(8000, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow(); // Cancel currently executing tasks
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        // Final output to check the last state
        System.out.println("Rate limiter test completed.");
    }
}
