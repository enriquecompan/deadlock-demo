/**
 * The Main class demonstrates a deadlock scenario using two threads and two shared objects.
 * <p>
 * It simulates a deadlock by having two virtual threads acquire locks on two shared objects
 * (`obj1` and `obj2`) in opposite order. Thread 1 (`t1`) acquires a lock on `obj1` and then attempts
 * to acquire a lock on `obj2`. Thread 2 (`t2`) acquires a lock on `obj2` and then attempts to acquire
 * a lock on `obj1`.
 * <p>
 * The program illustrates:
 * - Deadlock conditions where two threads are mutually waiting for each other's locks.
 * - Use of virtual threads introduced in recent Java versions to perform concurrent operations.
 * <p>
 * Key operations:
 * - `Thread.ofVirtual().start()` is used to create and start virtual threads.
 * - `Thread.sleep()` is called within `t1` to delay the thread, increasing the likelihood of a deadlock.
 * - `synchronized` blocks are used for acquiring object locks.
 * - `join()` ensures the main thread waits for the completion of both threads unless a deadlock occurs.
 */
class Main {

    public static void main(String[] args) throws InterruptedException {

        // Objects used as resources that will be used to get locks
        Object obj1 = new Object();
        Object obj2 = new Object();

        System.out.println("--- Starting, attempting to deadlock (press Ctrl+C to stop)...");

        // Thread 1
        var t1 = Thread.ofVirtual().start(() -> {
            try {
                synchronized (obj1) {
                    System.out.println("t1 acquired lock on obj1.");
                    // Sleep for 500 ms to give t2 a chance to acquire lock on obj2 before proceeding
                    Thread.sleep(500);
                    System.out.println("t1 acquiring lock on obj2...");
                    synchronized (obj2) {
                        System.out.println("t1 acquired lock on obj2...");
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println("t1 is done.");
        });

        // Thread 2
        var t2 = Thread.ofVirtual().start(() -> {
            synchronized (obj2) {
                System.out.println("t2 acquired lock on obj2.");
                System.out.println("t2 acquiring lock on obj1...");
                synchronized (obj1) {
                    System.out.println("t2 acquired lock on obj1...");
                }
            }

            System.out.println("t2 is done.");
        });

        // An extra thread to report the status of the other threads and detect if a deadlock happened
        Thread.ofVirtual().start(() -> {
            try {
                // Wait a little bit to ensure the other thread have enough time to do their tasks
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("t1 state: " + t1.getState());
            System.out.println("t2 state: " + t2.getState());

            if (t1.getState() == Thread.State.BLOCKED && t2.getState() == Thread.State.BLOCKED) {
                System.out.println("Deadlock detected!");
            }
        });

        // Wait for both threads to finish gracefully (if they don't deadlock)
        t1.join();
        t2.join();

        System.out.println("--- Program finished (if we deadlocked, this will never get printed!).");
    }
}