package github.jadetang.aiven.metric.consumer;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricConsumer {

  private static final Logger log = LoggerFactory.getLogger(MetricConsumer.class);
  private final List<ConsumerThread> consumerThreads;
  private final ExecutorService executor;
  private volatile boolean running;

  public MetricConsumer(final List<ConsumerThread> consumerThreads) {
    this.executor = Executors.newFixedThreadPool(consumerThreads.size(),
        new ThreadFactoryBuilder().setNameFormat("Metric-consumer-thread-%d").build());
    this.consumerThreads = consumerThreads;
  }

  public synchronized void start() {
    if (running) {
      return;
    }
    for (final ConsumerThread consumerThread : consumerThreads) {
      executor.execute(consumerThread);
    }
    running = true;
  }

  public synchronized void stop() {
    if (!running) {
      return;
    }
    consumerThreads.forEach(ConsumerThread::stop);
    closeThreadPool();
  }

  private void closeThreadPool() {
    log.info("Closing consumer thread pool.");
    executor.shutdown();
    try {
      executor.awaitTermination(10, TimeUnit.SECONDS);
    } catch (final InterruptedException e) {
      log.error("Error closing consumer thread pool.");
      Thread.currentThread().interrupt();
    }
  }
}
