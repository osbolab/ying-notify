package edu.asu.ying.net.push;

/**
 * @author matt@osbolab.com (Matt Barnard)
 */
public class PushException extends Exception {
  public PushException(String message) {
    super(message);
  }

  public PushException(String message, Throwable cause) {
    super(message, cause);
  }
}
