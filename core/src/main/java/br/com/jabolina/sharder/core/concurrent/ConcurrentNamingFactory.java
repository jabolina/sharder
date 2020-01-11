package br.com.jabolina.sharder.core.concurrent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;

import java.util.concurrent.ThreadFactory;

/**
 * @author jab
 * @date 1/11/20
 */
public final class ConcurrentNamingFactory {
  public static ThreadFactory name(String pattern, Logger logger) {
    return new ThreadFactoryBuilder()
        .setNameFormat(pattern)
        .setThreadFactory(new ConcurrentThreadFactory())
        .setUncaughtExceptionHandler((thread, e) -> logger.error("Uncaught exception on " + thread.getName(), e))
        .build();
  }
}
