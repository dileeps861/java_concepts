package dileepshah.dev.os.concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @see <a href="https://en.wikipedia.org/wiki/Conditional_variable">Conditional variable</a>
 * A conditional variable is a synchronization primitive that allows a thread to wait
 * until a condition becomes true.
 * We use an example of conditional variable in Java. To build a conditional variable, we need to use the `lock`.
 * and `wait`, `notify`, and `notifyAll` methods.
 * This example shows how to use conditional variable in Java with `ReentrantLock` and `Condition`.
 * This is to show how to Conditional variable which are needed in solving problems like a reader-writer problem and connection pool.
 */
public class ConditionalVariable {
    private final Lock lock;
    private int readerCount;
    private final Condition cond;
    public ConditionalVariable(Lock lock, int readerCount, Condition cond) {
        this.lock = lock;
        this.readerCount = readerCount;
        this.cond = cond;
    }

    public void condWait() {
        lock.lock();
        try {
            while (readerCount == 0) {
                cond.await();
            }
            readerCount--;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void condSignal() {
        lock.lock();
        try {
            readerCount++;
            cond.signal();
        } finally {
            lock.unlock();
        }
    }

    public void condSignalAll() {
        lock.lock();
        try {
            readerCount = 0;
            cond.signalAll();
        } finally {
            lock.unlock();
        }
    }
    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        Condition cond = lock.newCondition();
        ConditionalVariable cv = new ConditionalVariable(lock, 0, cond);

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                cv.condWait();
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
                cv.condSignal();
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
}
