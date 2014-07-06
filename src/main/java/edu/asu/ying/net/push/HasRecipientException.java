package edu.asu.ying.net.push;

/**
 * @author matt@osbolab.com (Matt Barnard)
 */
public interface HasRecipientException {

  /**
   * Gets the name of the recipient to which sending failed because of this type.
   */
  Name recipient();

  PushException cause();

  /**
   * If {@code true} then this type is transient and sending again may be successful.
   */
  boolean canRetry();
}
