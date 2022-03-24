package com.example;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;
import org.junit.runners.Parameterized;
import org.junit.runners.model.RunnerScheduler;

public class ParallelParameterized extends Parameterized {

  public ParallelParameterized(Class<?> klass) throws Throwable {
    super(klass);
    this.setScheduler(new ParallelScheduler());
  }

  private static class ParallelScheduler implements RunnerScheduler {

    private static final Logger LOGGER = Logger.getLogger(ParallelScheduler.class.getName());

    private final Phaser childCounter;
    private final ExecutorService executorService;


    private ParallelScheduler() {
      ThreadFactory threadFactory =
          new ThreadFactoryBuilder()
              .setDaemon(true)
              .setNameFormat("parallel-test-runner-%02d")
              .build();
      // attempt to leave some space for the testbench server running alongside these tests
      int coreCount = Runtime.getRuntime().availableProcessors();
      int threadCount = Math.max(1, coreCount) * 2;
      LOGGER.info("Using up to " + threadCount + " threads to run tests.");
      executorService = Executors.newFixedThreadPool(threadCount, threadFactory);
      childCounter = new Phaser();
    }

    @Override
    public void schedule(Runnable childStatement) {
      childCounter.register();
      executorService.submit(
          () -> {
            try {
              childStatement.run();
            } finally {
              childCounter.arrive();
            }
          });
    }

    @Override
    public void finished() {
      try {
        childCounter.awaitAdvance(0);
      } finally {
        executorService.shutdownNow();
      }
    }
  }
}
