package edu.asu.ying.net.push;

/**
 * @author matt@osbolab.com (Matt Barnard)
 */
public interface PublishesNotifications {

  void addConsumer(ConsumesNotifications consumer);

  void removeConsumer(ConsumesNotifications consumer);
}
