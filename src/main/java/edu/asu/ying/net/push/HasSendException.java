package edu.asu.ying.net.push;

/**
 * @author matt@osbolab.com (Matt Barnard)
 */
public interface HasSendException {

  PushException exception();

  /**
   * If {@code true} then this type is probably transient and you can try again to send the same
   * notification.
   */
  boolean canRetry();
}
