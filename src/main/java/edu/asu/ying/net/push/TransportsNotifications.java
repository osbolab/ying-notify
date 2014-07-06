package edu.asu.ying.net.push;

/**
 * @author matt@osbolab.com (Matt Barnard)
 */
public interface TransportsNotifications {

  AuthenticatedTransport as(String sender, AuthenticatesSender credential);
}
