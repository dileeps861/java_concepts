package dileepshah.dev.os.concurrency;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ImprovedKVStore<K,V> {
    private static final int DEFAULT_SEGMENTS = 16;
    private final SimpleConcurrentKVStore<K, V>[] segments;
    private final int numberOfSegments;

    @SuppressWarnings("unchecked")
    public ImprovedKVStore(int numberOfSegments) {
        this.segments = new SimpleConcurrentKVStore[numberOfSegments];
        this.numberOfSegments = numberOfSegments;
        for (int i = 0; i < numberOfSegments; i++) {
            this.segments[i] = new SimpleConcurrentKVStore<>();
        }
    }

    public ImprovedKVStore() {
        this(DEFAULT_SEGMENTS);
    }

    public V get(K key) {
        SimpleConcurrentKVStore<K, V> kvStoreForHash = nullCheckAndGetStore(key);
        return kvStoreForHash.get(key);
    }

    private SimpleConcurrentKVStore<K, V> nullCheckAndGetStore(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Invalid key or value");
        }

        int hash = this.hash(key);
        return this.segments[hash];
    }

    public V delete(K key) {
        SimpleConcurrentKVStore<K, V> kvStoreForHash = nullCheckAndGetStore(key);
        return kvStoreForHash.delete(key);
    }

    public void put(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Invalid key or value");
        }
        SimpleConcurrentKVStore<K, V> kvStoreForHash = nullCheckAndGetStore(key);
        kvStoreForHash.put(key, value);
    }

    private int hash(K key) {
        return Math.abs(Objects.hash(key) % this.numberOfSegments);
    }

    public static void main(String[] args) {
        ImprovedKVStore<Integer, String> improvedKVStore = new ImprovedKVStore<>();

        // Creating a thread pool with 5 threads
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Testing concurrent access
        executor.execute(() -> improvedKVStore.put(1, "Apple"));
        executor.execute(() -> System.out.println("Get 1: " + improvedKVStore.get(1)));
        executor.execute(() -> improvedKVStore.put(1, "Avocado")); // Update value
        executor.execute(() -> System.out.println("Updated Get 1: " + improvedKVStore.get(1)));
        executor.execute(() -> improvedKVStore.delete(1));

        // Shutdown and await termination of thread pool
        executor.shutdown();
        try {
            if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow(); // Cancel currently executing tasks
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        // Checking the final state of the store
        try {
            System.out.println("Final value for key 1 (should be null if delete worked): " + improvedKVStore.get(1));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
