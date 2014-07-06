package edu.asu.ying.net.push;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author matt@osbolab.com (Matt Barnard)
 */
public interface RegistersForPush {

  ListenableFuture<HasPushRegistration> register();
}
