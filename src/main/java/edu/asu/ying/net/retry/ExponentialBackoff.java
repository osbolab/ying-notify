package edu.asu.ying.net.retry;

import java.util.Random;

/**
 * @author matt@osbolab.com (Matt Barnard)
 */
public final class ExponentialBackoff implements RetryPolicy {

  private static final long INITIAL_INTERVAL_DEFAULT = 500;
  private static final long MAX_INTERVAL_DEFAULT = 6000;
  private static final long MAX_ELAPSED_DEFAULT = 120000;
  private static final double MULTIPLIER_DEFAULT = 1.5;
  private static final double RAND_FACTOR_DEFAULT = 0.5;

  public static ExponentialBackoff.Builder builder() {
    return new Builder();
  }

  private final long initialIntervalMs;
  private final long maxIntervalMs;
  private final long maxElapsedMs;
  private final double multiplier;
  private final double randMin;
  private final double randRange;
  private long intervalMs;
  private long msLastPolled;
  private long elapsedMs;

  private final Random random = new Random();

  private ExponentialBackoff(Builder builder) {
    initialIntervalMs = builder.initialIntervalMs;
    maxIntervalMs = builder.maxIntervalMs;
    maxElapsedMs = builder.maxElapsedMs;
    multiplier = builder.multiplier;
    randMin = 1.0 - builder.randFactor;
    randRange = (1.0 + builder.randFactor) - randMin;
  }

  private ExponentialBackoff(ExponentialBackoff clone) {
    initialIntervalMs = clone.initialIntervalMs;
    maxIntervalMs = clone.maxIntervalMs;
    maxElapsedMs = clone.maxElapsedMs;
    multiplier = clone.multiplier;
    randMin = clone.randMin;
    randRange = clone.randRange;
  }

  @Override
  public long nextWaitDurationMs() {
    if (msLastPolled > 0) {
      elapsedMs += System.currentTimeMillis() - msLastPolled;
    }
    msLastPolled = System.currentTimeMillis();
    if (elapsedMs >= maxElapsedMs) {
      return STOP;
    }
    if (intervalMs < 0) {
      intervalMs = initialIntervalMs;
      return intervalMs;
    }
    double rand = randMin + random.nextDouble() * randRange;
    intervalMs = Math.round((double) intervalMs * multiplier * rand);
    return intervalMs;
  }

  @Override
  public void reset() {
    intervalMs = initialIntervalMs;
    elapsedMs = 0;
    msLastPolled = 0;
  }

  @SuppressWarnings("CloneDoesntCallSuperClone")
  @Override
  public RetryPolicy clone() {
    return new ExponentialBackoff(this);
  }

  public static final class Builder {
    private long initialIntervalMs = INITIAL_INTERVAL_DEFAULT;
    private long maxIntervalMs = MAX_INTERVAL_DEFAULT;
    private long maxElapsedMs = MAX_ELAPSED_DEFAULT;
    private double multiplier = MULTIPLIER_DEFAULT;
    private double randFactor = RAND_FACTOR_DEFAULT;

    private Builder() {
    }

    public Builder setInitialIntervalMs(long ms) {
      initialIntervalMs = Math.max(ms, 0);
      return this;
    }

    public Builder setMaxIntervalMs(long ms) {
      maxIntervalMs = Math.max(ms, 0);
      return this;
    }

    public Builder setMaxElapsedMs(long ms) {
      maxElapsedMs = Math.max(ms, 0);
      return this;
    }

    public Builder setMultiplier(double multiplier) {
      multiplier = Math.max(multiplier, 0.0);
      return this;
    }

    public Builder setRandomizationFactor(double factor) {
      randFactor = Math.max(factor, 0.0);
      return this;
    }

    public ExponentialBackoff build() {
      return new ExponentialBackoff(this);
    }
  }
}
