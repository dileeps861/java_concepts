package dileepshah.dev.multithreading.exchanger;

import lombok.SneakyThrows;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <div>
 *     <p>Created by dileepshah on 2024-06-13 </p>
 * </br>
 * <p>
 * Exchangers, unlike semaphores which control access, are used for exchanging data between threads.
 * Here's how they work in software development:
 * <p>
 * Exchanger facilitates the exchange of information between two threads. One thread deposits data into the exchanger,
 * and the other thread retrieves it. However, only one thread can access the exchanger at a time.
 * </p>
 * <h3> Real-world use cases for Exchanger include: </h3>
 * </br>
 * <p>
 *  <h3>1. Data Producer-Consumer with Buffering: </h3>
 * Imagine a system where data is produced at a different rate than it's consumed.
 * An Exchanger can act as a buffer between the producer and consumer threads. The producer deposits data into the
 * exchanger, and the consumer retrieves it. This allows the threads to operate independently without waiting for each
 * other, improving overall efficiency.
 *
 *  <h3>2. Thread Communication with Acknowledgement:  </h3>
 * Sometimes, threads need to exchange data and confirm receipt. An Exchanger
 * can be used here. One thread deposits data, and the other retrieves it. The retrieving thread can then deposit an
 * acknowledgement signal back into the Exchanger, informing the first thread that the data has been received.
 *
 * <h3>3. Two-Phase Commit Protocols: </h3>
 *  These protocols ensure data consistency across multiple databases during transactions.
 * An Exchanger can be used to coordinate the commit or rollback decision between the participating database servers.
 * One server deposits the decision (commit/rollback) into the Exchanger, and all other servers retrieve it to perform
 * the same action on their databases.
 *
 *  <h3>4. Authentication and Authorization: </h3>
 *  This authentication method involves a client sending a challenge to a server,
 * and the server responding with a calculated response based on the challenge and a secret key. An Exchanger can be
 * used to securely exchange the challenge and response between the client and server threads.
 *
 * <h3>5. Data Handoff Between Stages in a Pipeline: </h3>
 * In multi-stage processing pipelines, where data is transformed as it
 * progresses, an Exchanger can be used to efficiently transfer data between stages. One stage deposits the processed
 * data into the Exchanger, and the next stage retrieves it for further processing.
 * * </p>
 * </div>
 *
 * @see java.util.concurrent.Exchanger
 */
public class ExchangerExample {


    public static void main(String[] args) {
        // So how to use Exchanger? This is a simple example. It shows how to use it.
        // How it works is when the same lock is used by multiple threads, then only one thread will be able to acquire
        // the lock.

        // Create an Exchanger object
        Exchanger<String> exchanger = new Exchanger<>();

        // Create two threads that will exchange data
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Data to exchange
                    String data = "Hello";
                    System.out.println("Thread 1 Sending:  " + data);

                    // Exchange data with Thread 2
                    String receivedData = exchanger.exchange(data, 2000, TimeUnit.MILLISECONDS); // Exchange data with Thread 2
                    // time out specified ensures that the second thread will receive the data.
                    System.out.println("Thread 1 Received:  " + receivedData);
                } catch (InterruptedException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                // Data to exchange
                String data = "World";
                System.out.println("Thread 2 Sending:  " + data);

                // If commented out below line, then it will throw an exception for the first thread to receive the data
                // because the first thread will be waiting for the second thread to receive the data.
                // If no time out is specified, then it will wait indefinitely which is somewhat like deadlock.
                String receivedData = exchanger.exchange(data);   // Exchange data with Thread 1
                System.out.println("Thread 2 Received:  " + receivedData);
            }
        });

        // Start both threads
        thread1.start();
        thread2.start();
    }
}
