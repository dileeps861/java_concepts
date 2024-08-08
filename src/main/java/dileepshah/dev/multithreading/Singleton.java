package dileepshah.dev.multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Demonstration for lock-free impl of a Singleton pattern.
 */
public class Singleton {
    static AtomicReference<Singleton> INSTANCE = new AtomicReference<>(null);
//    static Singleton INSTANCE = null;

    private Singleton() {

    }

    static Singleton getInstance() {
//        if (INSTANCE == null) {
//            synchronized (INSTANCE) {
//                if (Singleton.INSTANCE == null) {
//                    Singleton.INSTANCE = new Singleton();
//                }
//                return Singleton.INSTANCE;
//            }
//        }
//        return instance;

//        Lock free impl of the same
        Singleton currentInstance = INSTANCE.get();
        if (currentInstance == null) {
            Singleton newInstance = new Singleton();
            if (INSTANCE.compareAndSet(null, newInstance)) {
                return newInstance;
            } else {
                return INSTANCE.get();
            }
        }
        return currentInstance;

    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i <= 5; i++) {
            executorService.submit(() -> System.out.println(Singleton.getInstance()));
        }

        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
