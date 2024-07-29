package dileepshah.dev.os.concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * SimpleConcurrentRateLimiter is a leaky bucket rate limiter implementation to control
 * the rate of access requests to a resource in a concurrent environment.
 *
 * @param <T> The type of access criteria used for the rate limiting.
 */
public class SimpleGenericConcurrentRateLimiter<T> {
    private BlockingQueue<RequestData<T>> leakyBucket;
    private long requestRange;
    private long allowedNoOfRequests;
    private ReadWriteLock readWriteLock;

    /**
     * Constructs a SimpleConcurrentRateLimiter with a specified request range and allowed number of requests.
     *
     * @param requestRange       The time range (in milliseconds) within which a certain number of requests are allowed.
     * @param allowedNoOfRequests The maximum number of requests allowed within the specified time range.
     */
    public SimpleGenericConcurrentRateLimiter(long requestRange, long allowedNoOfRequests) {
        this.leakyBucket = new LinkedBlockingDeque<>();
        this.requestRange = requestRange;
        this.allowedNoOfRequests = allowedNoOfRequests;
        this.readWriteLock =  new ReentrantReadWriteLock();
    }

    /**
     * Requests access based on the specified access criteria. This method checks if the access request
     * can be granted based on the rate limiting rules.
     *
     * @param accessCriteria The criteria for the access request.
     * @return true if the request is allowed, false otherwise.
     */
    public boolean requestAccess(T accessCriteria){
        long requestTime = System.currentTimeMillis();
        long oldestTime = requestTime - this.requestRange;
        this.readWriteLock.writeLock().lock();
        try {

            while (!this.leakyBucket.isEmpty() && this.leakyBucket.peek().timeStamp < oldestTime) {
                this.leakyBucket.poll();
            }
            if(this.leakyBucket.size() < this.allowedNoOfRequests) {
                this.leakyBucket.add(new RequestData<>(accessCriteria, requestTime));
                return true;
            }
            return false;
        }
        finally {
            this.readWriteLock.writeLock().unlock();
        }
    }


    /**
     * RequestData is a helper class to store information about each access request.
     *
     * @param <T> The type of access criteria.
     */
    static class RequestData<T> implements Comparable<RequestData<T>>{
        T accessType;
        long timeStamp;

        /**
         * Constructs a RequestData instance with the specified access type and timestamp.
         *
         * @param accessType The type of access criteria.
         * @param timeStamp  The timestamp of the request.
         */
        public RequestData(T accessType, long timeStamp) {
            this.accessType = accessType;
            this.timeStamp = timeStamp;
        }

        @Override
        public int compareTo(RequestData<T> o) {
            return Long.compare(this.timeStamp, o.timeStamp);
        }
    }


    public static void main(String[] args) {
        final int numberOfThreads = 100;
        final long requestRange = 1000; // 1 second
        final long allowedNoOfRequests = 5; // Allow 5 requests per second

        // Instantiate the rate limiter
        SimpleGenericConcurrentRateLimiter<String> rateLimiter = new SimpleGenericConcurrentRateLimiter<>(requestRange, allowedNoOfRequests);

        // Create a thread pool
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        // Define a task that requests access
        Runnable accessTask = () -> {
            String threadName = Thread.currentThread().getName();
            boolean isAllowed = rateLimiter.requestAccess(threadName);
            if (isAllowed) {
                System.out.println(threadName + " was allowed access.");
            } else {
                System.out.println(threadName + " was denied access.");
            }
        };

        // Submit the tasks to the executor
        for (int i = 0; i < numberOfThreads * 4; i++) {
            executor.execute(accessTask);
            try {
                // Simulate requests at intervals
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
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
