package edu.asu.ying.net.push;

import java.util.Set;

import javax.annotation.concurrent.Immutable;

/**
 * @author matt@osbolab.com (Matt Barnard)
 */
@Immutable
public interface Pushable {

  /**
   * Gets the collection of recipient names for whom this notification is addressed.
   */
  Set<Name> recipients();

  /**
   * Gets the collection of contents included in this notification.
   */
  HasPushContent contents();
}
