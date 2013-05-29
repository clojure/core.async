/*
 Copyright (c) Rich Hickey and contributors. All rights reserved.
 The use and distribution terms for this software are covered by the
 Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 which can be found in the file epl-v10.html at the root of this distribution.
 By using this software in any fashion, you are agreeing to be bound by
 the terms of this license.
 You must not remove this notice, or any other, from this software.
*/

package clojure.core.async;

import java.util.Random;

public class ThreadLocalRandom extends Random {

  private static final long serialVersionUID = -2599376724352996934L;

  private static ThreadLocal<ThreadLocalRandom> currentThreadLocalRandom = new ThreadLocal<ThreadLocalRandom>() {
    protected ThreadLocalRandom initialValue() {
      return new ThreadLocalRandom();
    }
  };


  /**
   * Returns the current ThreadLocalRandom for this thread. Clients must call current,
   * rather than constructing instances themselves. The ThreadLocalRandom instance will
   * be returned from a ThreadLocal variable.
   *
   * @return A ThreadLocalRandom for the current thread
   *
   * @see ThreadLocal
   * @see Random
   */
  public static ThreadLocalRandom current() {
    return currentThreadLocalRandom.get();
  }

  private ThreadLocalRandom() {
    super();
  }

  private ThreadLocalRandom(long seed) {
    super(seed);
  }

}
