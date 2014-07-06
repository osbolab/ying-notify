package edu.asu.ying.net.push;

/**
 * @author matt@osbolab.com (Matt Barnard)
 */
public interface ConsumesNotifications {

  void onPushNotification(HasNotification notification);

  void onPushNotification(PushException exception);
}
