package edu.asu.ying.net.retry;

/**
 * @author matt@osbolab.com (Matt Barnard)
 */
public interface RetryPolicy {

  public static final RetryPolicy NO_RETRY = new RetryPolicy() {
    @Override
    public long nextWaitDurationMs() {
      return STOP;
    }

    @Override
    public void reset() {
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public RetryPolicy clone() {
      return this;
    }
  };

  public static final RetryPolicy RETRY_IMMEDIATELY = new RetryPolicy() {
    @Override
    public long nextWaitDurationMs() {
      return NO_DELAY_MS;
    }

    @Override
    public void reset() {
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public RetryPolicy clone() {
      return null;
    }
  };

  public static final long STOP = -1;
  public static final long NO_DELAY_MS = 0;

  public long nextWaitDurationMs();

  public void reset();

  @SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
  public RetryPolicy clone();
}
