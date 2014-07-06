package edu.asu.ying.net.push;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import javax.annotation.concurrent.Immutable;

import edu.asu.ying.net.retry.ExponentialBackoff;
import edu.asu.ying.net.retry.RetryPolicy;

/**
 * @author matt@osbolab.com (Matt Barnard)
 */
@Immutable
public final class BackgroundPushSender implements SendsPushNotifications, Callable<HasSendResult> {

  private final Logger log = Logger.getLogger(BackgroundPushSender.class.getName());

  private final AuthenticatedTransport transport;
  private final RetryPolicy retryPolicy = ExponentialBackoff.builder().build();

  private final BlockingDeque<Pushable> sendQueue = new LinkedBlockingDeque<>();
  private final ListeningExecutorService executor =
      MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

  public BackgroundPushSender(AuthenticatedTransport transport) {
    this.transport = transport;
  }

  @Override
  public ListenableFuture<HasSendResult> send(Pushable notification) {
    sendQueue.add(notification);
    return executor.submit(this);
  }

  @Override
  public HasSendResult call() throws InterruptedException {
    HasSendResult result;
    int attempts = 0;
    long elapsedMs = 0;
    boolean retry = false;

    Pushable notification = sendQueue.take();

    Set<Name> unsentRecipients = new HashSet<>(notification.recipients());
    List<HasRecipientException> noRetryErrors = new ArrayList<>();

    do {
      long startMs = System.currentTimeMillis();
      ++attempts;

      if (attempts > 1) {
        log.info(attempts + " send attempts after " + elapsedMs + "ms");
        log.info("Retrying " + unsentRecipients.size() +
                 "/" + notification.recipients().size() + " recipients.");
      }

      /** send **/
      result = transport.send(unsentRecipients, notification.contents());

      // Assume all recipients successful
      unsentRecipients.clear();

      HasSendException error = result.exception();
      if (error != null) {
        // Catastrophic error
        retry = error.canRetry();
        logSendError(error, retry, attempts);
      } else {
        // Count off unsuccessful recipients
        for (HasRecipientException recpError : result.recipientExceptions()) {
          if (recpError.canRetry()) {
            unsentRecipients.add(recpError.recipient());
          } else {
            noRetryErrors.add(recpError);
          }
        }
      }

      long waitMs = retryPolicy.nextWaitDurationMs();
      if (waitMs == RetryPolicy.STOP) {
        int totalUnsent = unsentRecipients.size() + noRetryErrors.size();
        //noinspection ThrowableInstanceNeverThrown
        Throwable cause = new TimeoutException(
            "Sending notification took too long (" + elapsedMs + "ms); " +
            totalUnsent + '/' + notification.recipients().size() + " recipients not sent to.");

        retry = false;
        result = ImmutableSendResult.builder(result.messageId())
                                    .with(result.recipientExceptions())
                                    .with(cause, false)
                                    .build();
      }

      elapsedMs += System.currentTimeMillis() - startMs;
    } while (retry);

    return result;
  }

  private void logSendError(HasSendException error, boolean retry, int attempts) {
    StringBuilder errorMsg = new StringBuilder();
    errorMsg.append("Error on send attempt #").append(attempts);
    errorMsg.append(" (").append(retry ? "retry" : "no retry").append("): ");
    //noinspection ThrowableResultOfMethodCallIgnored
    errorMsg.append(error.exception().toString());
    if (retry) {
      log.warning(errorMsg.toString());
    } else {
      log.severe(errorMsg.toString());
    }
  }
}
