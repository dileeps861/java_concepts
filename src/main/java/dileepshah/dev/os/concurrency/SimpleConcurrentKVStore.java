package dileepshah.dev.os.concurrency;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simple concurrent KV to be thread safe and allow multiple threads access to the store.
 */
public class SimpleConcurrentKVStore<K, V> {
    private Map<K, V> store;
    private Lock lock;

    public SimpleConcurrentKVStore() {
        this.store = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    public V get(K key) {
        lock.lock();
        try {
            if (key == null || !store.containsKey(key)) {
                throw new IllegalArgumentException("Invalid key or key doesn't exist!");
            }
            return store.get(key);
        } finally {
            lock.unlock();
        }
    }

    public void put(K key, V value) {
        lock.lock();
        if (key == null) {
            throw new IllegalArgumentException("Invalid key");
        }
        if (value == null) {
            throw new IllegalArgumentException("Invalid value");
        }

        try {
            store.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    public V delete(K key) {
        lock.lock();
        try {
            if (key == null || !store.containsKey(key)) {
                throw new IllegalArgumentException("Invalid key or it doesn't exist");
            }
            return store.remove(key);
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        final SimpleConcurrentKVStore<Integer, String> kvStore = new SimpleConcurrentKVStore<>();

        // Creating a thread pool with 5 threads
        final ExecutorService executor = Executors.newFixedThreadPool(5);

        // Testing concurrent access
        executor.execute(() -> {
            kvStore.put(1, "Apple");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        });
        executor.execute(() -> System.out.println("Get 1: " + kvStore.get(1)));
        executor.execute(() -> {
            kvStore.put(1, "Avocado");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }); // Update value
        executor.execute(() -> System.out.println("Updated Get 1: " + kvStore.get(1)));
        executor.execute(() -> kvStore.delete(1));

        // Shutdown and await termination of thread pool
        try {
            if (!executor.awaitTermination(8000, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow(); // Cancel currently executing tasks
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        // Checking the final state of the store
        try{
            System.out.println("Final value for key 1 (should be null if delete worked): " + kvStore.get(1));
        } catch (IllegalArgumentException iae){
            System.out.println("Exception thrown: " + iae.getMessage());
        }


    }
}
