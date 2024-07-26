package dileepshah.dev.os.concurrency;

/**
 * This interface represents the task to be submitted for the execution
 */
public interface Task extends Runnable, Comparable<Task> {
    String getName();
    long getStartTime();
}
