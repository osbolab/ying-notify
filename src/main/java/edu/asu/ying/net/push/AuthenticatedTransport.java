package edu.asu.ying.net.push;

import java.util.Set;

/**
 * @author matt@osbolab.com (Matt Barnard)
 */
public interface AuthenticatedTransport {

  HasSendResult send(Set<Name> recipients, HasPushContent content);
}
