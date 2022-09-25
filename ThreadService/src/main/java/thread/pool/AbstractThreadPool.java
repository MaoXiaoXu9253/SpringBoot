package thread.pool;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import thread.factory.simple.SimpleThreadFactory;

/**
 * 线程抽象类
 *
 * @author maoxiaoxu
 * @version 1.0
 */
public abstract class AbstractThreadPool {

  /**
   * 线程ID
   */
  private final long threadId;
  /**
   * 线程名称
   */
  private final String threadGroupName;

  /**
   * 构造对象
   *
   * @param threadGroupName 线程组名称
   * @param threadId        线程ID
   */
  public AbstractThreadPool (String threadGroupName, long threadId) {
    this.threadId = threadId;
    this.threadGroupName = threadGroupName;
  }

  /**
   * 最大线程为CPU数量
   *
   * @return maximumPoolSize
   */
  protected static int getDefaultMaximumPoolSize () {
    return Runtime.getRuntime().availableProcessors();
  }

  /**
   * 如果CPU数量小于TEN则核心线程数为CPU数量,如果CPU数量大于于TEN则核心线程数为TEN
   *
   * @return corePoolSize
   */
  protected static int getDefaultCorePoolSize () {
    return Math.min(BigInteger.TEN.intValue(), getDefaultMaximumPoolSize());
  }

  /**
   * 等待时间默认为MINUTES
   *
   * @return MINUTES
   */
  protected static TimeUnit getDefaultTimeUnit () {
    return TimeUnit.MINUTES;
  }

  /**
   * 存活时间
   *
   * @return ONE
   */
  protected static long getDefaultKeepAliveTime () {
    return BigInteger.ONE.longValue();
  }

  /**
   * SimpleThreadFactory
   *
   * @return SimpleThreadFactory
   */
  protected final ThreadFactory getSimpleThreadFactory () {
    return new SimpleThreadFactory(this.threadGroupName, this.threadId);
  }

  /**
   * 设置 ThreadFactory
   *
   * @return ThreadFactory
   */
  protected abstract ThreadFactory getThreadFactory ();

  /**
   * 设置 BlockingQueue
   *
   * @return BlockingQueue
   */
  protected abstract BlockingQueue<Runnable> getBlockingQueue ();

  /**
   * 设置 RejectedExecutionHandler
   *
   * @return RejectedExecutionHandler
   */
  protected abstract RejectedExecutionHandler getRejectedExecutionHandler ();

}