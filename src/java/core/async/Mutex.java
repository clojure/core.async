package core.async.mutex;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

// non-recursive, non-reentrant mutex implementation based on example
// from Doug Lea's "The java.util.concurrent Synchronizer Framework"
// http://gee.cs.oswego.edu/dl/papers/aqs.pdf
public class Mutex {
    private static class Sync extends AbstractQueuedSynchronizer {
        public boolean tryAcquire(int ignored) {
            return compareAndSetState(0, 1);
        }

        public boolean tryRelease(int ignored) {
            setState(0);
            return true;
        }
    }

    private final Sync sync = new Sync();

    public Mutex() {}

    public void lock() {
        sync.acquire(1);
    }

    public void unlock() {
        sync.release(1);
    }
}
