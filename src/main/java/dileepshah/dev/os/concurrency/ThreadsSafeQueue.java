package dileepshah.dev.os.concurrency;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread safe non-blocking queue where there are two separate locks for read and write.
 *
 * @param <T> Queue type
 */
public class ThreadsSafeQueue<T> {
    private final QNode<T> head;
    private final QNode<T> tail;
    private final ReentrantReadWriteLock.ReadLock readLock;
    private final ReentrantReadWriteLock.WriteLock writeLock;
    public ThreadsSafeQueue() {
        this.head = new QNode<>(null);
        this.tail = new QNode<>(null);

        this.tail.left = this.head;
        this.head.right = this.tail;

        this.readLock = new ReentrantReadWriteLock().readLock();
        this.writeLock = new ReentrantReadWriteLock().writeLock();
    }

    public void put(T data) {
        final QNode<T> dataNode = new QNode<>(data);
        this.writeLock.lock();
        try {
            this.tail.left.right = dataNode;
            dataNode.left = this.tail.left;
            dataNode.right = tail;
            tail.left = dataNode;
        } finally {
            this.writeLock.unlock();
        }
    }

    public T take() {
        this.readLock.lock();
        try {
            while (isEmpty()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            QNode<T> dataNode = head.right;
            head.right = dataNode.right;
            dataNode.right.left = head;
            return dataNode.data;
        } finally {
            this.readLock.unlock();
        }

    }

    public boolean isEmpty() {
        this.writeLock.lock();
        try {
            return tail.left == head && head.right == tail;
        } finally {
            this.writeLock.unlock();
        }
    }

    static class QNode<T> {
        QNode<T> left;
        QNode<T> right;
        T data;

        public QNode(T data) {
            this.data = data;
        }
    }

    public static void main(String[] args) {
        ThreadsSafeQueue<Integer> queue = new ThreadsSafeQueue<>();

        // Create a few producer threads
        Thread producer1 = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                queue.put(i);
                System.out.println("Producer 1 put: " + i);
                try {
                    Thread.sleep((long) (Math.random() * 100));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread producer2 = new Thread(() -> {
            for (int i = 6; i <= 10; i++) {
                queue.put(i);
                System.out.println("Producer 2 put: " + i);
                try {
                    Thread.sleep((long) (Math.random() * 100));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // Create a few consumer threads
        Thread consumer1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                Integer value = queue.take();
                System.out.println("Consumer 1 took: " + value);
                try {
                    Thread.sleep((long) (Math.random() * 150));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread consumer2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                Integer value = queue.take();
                System.out.println("Consumer 2 took: " + value);
                try {
                    Thread.sleep((long) (Math.random() * 150));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // Start all threads
        producer1.start();
        producer2.start();
        consumer1.start();
        consumer2.start();

        // Wait for all threads to finish
        try {
            producer1.join();
            producer2.join();
            consumer1.join();
            consumer2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Check if the queue is empty at the end
        System.out.println("Queue is empty: " + queue.isEmpty());
    }

}
