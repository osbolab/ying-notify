package edu.asu.ying.net.push;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * @author matt@osbolab.com (Matt Barnard)
 */
public final class ImmutableSendResult implements HasSendResult {

  public static Builder builder(int messageId) {
    return new Builder(messageId);
  }

  private final int messageId;
  private final Set<HasRecipientException> recipientExceptions;
  private final HasSendException exception;

  private ImmutableSendResult(Builder builder) {
    this.messageId = builder.messageId;
    this.recipientExceptions = ImmutableSet.copyOf(builder.recipientExceptions);
    this.exception = builder.exception;
  }

  @Override
  public int messageId() {
    return messageId;
  }

  @Override
  public Set<HasRecipientException> recipientExceptions() {
    return recipientExceptions;
  }

  @Nullable
  @Override
  public HasSendException exception() {
    return exception;
  }

  /**
   * @author matt@osbolab.com (Matt Barnard)
   */
  public static final class Builder {

    private final int messageId;
    private final Set<HasRecipientException> recipientExceptions = new HashSet<>();
    private HasSendException exception;

    private Builder(int messageId) {
      this.messageId = messageId;
    }

    public HasSendResult build() {
      return new ImmutableSendResult(this);
    }

    public Builder with(HasRecipientException recipientError) {
      this.recipientExceptions.add(recipientError);
      return this;
    }

    public Builder with(Collection<HasRecipientException> recipientErrors) {
      this.recipientExceptions.addAll(recipientErrors);
      return this;
    }

    public Builder with(Throwable e, boolean canRetry) {
      exception = new SendError(new PushException("Exception sending push notification.", e),
                                canRetry);
      return this;
    }
  }

  /**
   * @author matt@osbolab.com (Matt Barnard)
   */
  private static final class SendError implements HasSendException {

    private final PushException cause;
    private final boolean canRetry;

    private SendError(PushException cause, boolean canRetry) {
      this.cause = cause;
      this.canRetry = canRetry;
    }

    @Override
    public PushException exception() {
      return cause;
    }

    @Override
    public boolean canRetry() {
      return canRetry;
    }
  }
}
