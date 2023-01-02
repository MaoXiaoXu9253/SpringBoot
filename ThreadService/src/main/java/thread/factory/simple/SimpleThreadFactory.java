package thread.factory.simple;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简单线程池工厂
 *
 * @author maoxiaoxu
 * @version 1.0
 */
public final class SimpleThreadFactory implements ThreadFactory {

  /**
   * 线程池数量
   */
  private static final AtomicInteger poolNumber = new AtomicInteger(1);
  /**
   * 线程组名称
   */
  private final String threadGroupName;
  /**
   * 线程名称
   */
  private final String threadName;
  /**
   * 线程数量
   */
  private final AtomicInteger threadNumber = new AtomicInteger(1);

  /**
   * 构造方法
   *
   * @param threadGroupName 线程名称
   * @param threadId        线程ID
   */
  public SimpleThreadFactory (String threadGroupName, long threadId) {
    this.threadGroupName = threadGroupName;
    this.threadName =
        threadId + "-pool-" + SimpleThreadFactory.poolNumber.getAndIncrement() + "-thread-";

  }

  /**
   * 线程ThreadGroup
   *
   * @return ThreadGroup
   */
  private ThreadGroup getThreadGroup () {
    return new ThreadGroup(this.threadGroupName);
  }

  /**
   * 线程名称
   *
   * @return ThreadName
   */
  private String getThreadName () {
    return this.threadName + this.threadNumber.getAndIncrement();
  }

  @Override
  public Thread newThread (Runnable r) {
    Thread thread = new Thread(this.getThreadGroup(), r, this.getThreadName());
    if (thread.isDaemon()) {
      thread.setDaemon(false);
    }
    if (thread.getPriority() != Thread.NORM_PRIORITY) {
      thread.setPriority(Thread.NORM_PRIORITY);
    }
    return thread;
  }
}