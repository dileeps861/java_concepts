package dileepshah.dev.os.concurrency;

import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is a simple blocking queue implementation that's thread safe and multiple threads could access the queue.
 * It uses lock and two conditional variables.
 * @param <T> the type of the item the queue can store
 */
public class SimpleBlockingQueue<T> {
    private int size;
    private Lock lock;
    private Condition fullCondition;
    private Condition emptyCondition;
    private Queue<T> queue;
    public SimpleBlockingQueue(int size){
        this.size = size;
        this.queue = new ArrayDeque<>();
        this.lock = new ReentrantLock();
        this.fullCondition = lock.newCondition();
        this.emptyCondition = lock.newCondition();

    }
    public T take(){
        lock.lock();
        while (this.queue.isEmpty()){
            try {
                this.fullCondition.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        T item = queue.poll();
        this.emptyCondition.signal();
        lock.unlock();
        return item;
    }

    public void put(T item) {
        if(Objects.isNull(item)){
            throw new IllegalArgumentException("Item to put cannot be null.");
        }
        lock.lock();
        while (this.queue.size() == this.size){
            try {
                this.emptyCondition.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        queue.add(item);
        this.fullCondition.signal();
        lock.unlock();
    }

    public static void main(String[] args) {
        SimpleBlockingQueue<String> bq = new SimpleBlockingQueue<>(5);

        // producers
        Runnable producer = () -> {
            for(int i =0; i < 10; i++){
                long startTime = Calendar.getInstance().getTimeInMillis();
                System.out.println("Producing for the q:" + "Item_" + i);
                bq.put("Item_" + i);
                System.out.println(" Took item ms:" + (Calendar.getInstance().getTimeInMillis() - startTime));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        // consumers

        Runnable consumer = () -> {
            while(true){
                long startTime = Calendar.getInstance().getTimeInMillis();
                System.out.println("consuming from the q:");
                System.out.println("item:" + bq.take());
                System.out.println(" Took item ms:" + (Calendar.getInstance().getTimeInMillis() - startTime));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Thread t1 = new Thread(consumer);
        t1.start();
        Thread t2 = new Thread(consumer);
        t2.start();

        Thread t3 = new Thread(producer);
        t3.start();
        Thread t4 = new Thread(producer);
        t4.start();
        Thread t5 = new Thread(producer);
        t5.start();

        try{
            t1.join();
            t2.join();
            t3.join();
            t4.join();
            t5.join();

        } catch (Exception e){
            System.out.println("interrupted");
        }

    }
}
