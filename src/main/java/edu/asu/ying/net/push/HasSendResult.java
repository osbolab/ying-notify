package edu.asu.ying.net.push;

import java.util.Set;

import javax.annotation.Nullable;

/**
 * @author matt@osbolab.com (Matt Barnard)
 */
public interface HasSendResult {

  int messageId();

  /**
   * Gets any errors that occurred for individual recipients.
   */
  Set<HasRecipientException> recipientExceptions();

  /**
   * Gets the causative exception if sending the notification failed.
   */
  @Nullable
  HasSendException exception();
}
