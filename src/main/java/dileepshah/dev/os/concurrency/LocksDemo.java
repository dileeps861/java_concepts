package dileepshah.dev.os.concurrency;

/**
 * This example shows how to create lock in concurrent programming in Java, i.e how do we build a lock with given
 * POSIX primitives and guideline to use them.
 * We will be building a lock like ReentrantLock and ReentrantReadWriteLock.
 */
public class LocksDemo {
    private int readerCount;

    public LocksDemo(int readerCount) {
        this.readerCount = readerCount;
    }

    public static void main(String[] args) {
        LocksDemo locksDemo = new LocksDemo(0);
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                locksDemo.lock();
                System.out.println("T1 thread got the lock after getting the signal");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                locksDemo.unlock();
                System.out.println("T2 thread sent the signal and waiting for the lock");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void lock() {
        synchronized (this) {
            while (readerCount <= 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            readerCount--;
        }
    }

    public void unlock() {
        synchronized (this) {
            readerCount++;
            notify();
        }
    }
}