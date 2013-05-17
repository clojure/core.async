/*
 Copyright (c) Rich Hickey and contributors. All rights reserved.
 The use and distribution terms for this software are covered by the
 Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 which can be found in the file epl-v10.html at the root of this distribution.
 By using this software in any fashion, you are agreeing to be bound by
 the terms of this license.
 You must not remove this notice, or any other, from this software.
*/

package core.async;

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
