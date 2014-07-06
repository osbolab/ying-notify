package edu.asu.ying.net.push;

import com.google.common.util.concurrent.ListenableFuture;

import javax.annotation.concurrent.ThreadSafe;

/**
 * @author matt@osbolab.com (Matt Barnard)
 */
@ThreadSafe
public interface SendsPushNotifications {

  ListenableFuture<HasSendResult> send(Pushable notification);
}
