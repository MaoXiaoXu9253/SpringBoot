package thread.pool.simple;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import thread.BatchExecuteTasks;
import thread.pool.AbstractThreadPool;

/**
 * 简单线程池
 * <p>
 * 参数:
 *   <ul>
 *     corePoolSize:线程池中的常驻核心线程数
 *     线程池中的线程数目达到corePoolSize后,
 *     就会把到达的任务放入到缓存队列当中
 *     <li>CorePoolSize==min(TEN, CPU Size)</li>
 *     线程池能够容纳同时执行的最大线程数
 *     <li>MaximumPoolSize==CPU Size</li>
 *     多余的空闲线程存活时间,当空间时间达到keepAliveTime值时,
 *     多余的线程会被销毁直到只剩下corePoolSize个线程为
 *     <li>KeepAliveTime==ONE</li>
 *     keepAliveTime的单位
 *     <li>TimeUnit==MINUTES</li>
 *     ArrayBlockingQueue
 *      <p>
 *      是一个数组实现的有界阻塞队列 (有界队列)，队列中元素按 FIFO排序；
 *      ArrayBlockingQueue 在创建时必须设置大小，接收的任务超出 corePoolSize 数量时，
 *      则任务被缓存到该阻塞队列中，任务缓存的数量只能为创建时设置的大小；
 *      若该阻塞队列满，则会为新的任务则创建线程，直到线程池中的线程总数> maximumPoolSize。
 *      </p>
 *      LinkedBlockingQueue
 *      <p>
 *      是一个基于链表实现的阻塞队列，按 FIFO 排序任务，可以设置容量(有界队列)，
 *      不设置容量则默认使用 Integer.Max_VALUE 作为容量 （无界队列）。
 *      该队列的吞吐量高于 ArrayBlockingQueue。如果不设置 LinkedBlockingQueue 的容量（无界队列），
 *      当接收的任务数量超出 corePoolSize数量时，则新任务可以被无限制地缓存到该阻塞队列中，直到资源耗尽。
 *      无界队列：Executors.newSingleThreadExecutor、Executors.newFixedThreadPool
 *      </p>
 *     <li>BlockingQueue==LinkedBlockingDeque(MaximumPoolSize)</li>
 *     线程工厂
 *     <li>ThreadFactory==SimpleThreadFactory</li>
 *     拒绝策略,表示当线程队列满了并且工作线程大于等于线程池的最大显示数(MaximumPoolSize)时如何来拒绝
 *      AbortPolicy：丢弃任务并抛出 RejectedExecutionException 异常。（默认这种）
 *      DiscardPolicy：也是丢弃任务，但是不抛出异常
 *      DiscardOldestPolicy：丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程）
 *      CallerRunsPolicy：由调用线程处理该任务
 *      <li>RejectedExecutionHandler==CallerRunsPolicy</li>
 *   </ul>
 * </p>
 *
 * @author maoxiaoxu
 * @version 1.0
 */
@Slf4j
public final class SimpleThreadPool extends AbstractThreadPool implements BatchExecuteTasks {

  private static final int defaultBatchSize = 20;
  private static final long threadId = 925327007L;
  private static final String threadGroupName = "SimpleThread";
  private static final BlockingQueue<Runnable> blockingQueue = new LinkedBlockingDeque<>(getDefaultMaximumPoolSize());
  private final RejectedExecutionHandler handler = new CallerRunsPolicy();
  private final ThreadFactory threadFactory = super.getSimpleThreadFactory();
  private int batchSize;

  /**
   * 构造对象
   */
  public SimpleThreadPool () {
    super(SimpleThreadPool.threadGroupName, SimpleThreadPool.threadId);

  }

  /**
   * 构造对象
   *
   * @param batchSize 批处理数量
   */
  public SimpleThreadPool (int batchSize) {
    super(SimpleThreadPool.threadGroupName, SimpleThreadPool.threadId);
    this.batchSize = batchSize;
  }

  /**
   * 构造对象
   *
   * @param threadGroupName 线程组名称
   * @param threadId        线程ID
   */
  public SimpleThreadPool (String threadGroupName, long threadId) {
    super(threadGroupName, threadId);
  }

  /**
   * 构造对象
   *
   * @param threadGroupName 线程组名称
   * @param threadId        线程ID
   * @param batchSize       批处理数量
   */
  public SimpleThreadPool (String threadGroupName, long threadId, int batchSize) {
    super(threadGroupName, threadId);
    this.batchSize = batchSize;
  }

  /**
   * 获取线程服务
   *
   * @return ExecutorService
   */
  public ExecutorService getExecutorService () {
    return this.getDefaultPoolExecutor();
  }

  /**
   * getThreadFactory
   *
   * @return SimpleThreadFactory
   */
  @Override
  public ThreadFactory getThreadFactory () {
    return this.threadFactory;
  }

  /**
   * 链表阻塞队列 size = MaximumPoolSize
   *
   * @return LinkedBlockingDeque
   */
  @Override
  public BlockingQueue<Runnable> getBlockingQueue () {
    return SimpleThreadPool.blockingQueue;
  }

  /**
   * getRejectedExecutionHandler
   *
   * @return CallerRunsPolicy
   */
  @Override
  public RejectedExecutionHandler getRejectedExecutionHandler () {
    return this.handler;
  }

  @Override
  public void execute (List<Runnable> runnable) {

    if (null == runnable) {
      log.warn("{} runnable are null", Thread.currentThread().getName());
      throw new NullPointerException("runnable are null");
    }

    if (runnable.isEmpty()) {
      log.warn("{} runnable are empty", Thread.currentThread().getName());
      throw new NullPointerException("runnable are empty");
    }

    if (this.batchSize <= 0) {
      this.batchSize = SimpleThreadPool.defaultBatchSize;
    }
    int tasks = runnable.size();
    int batchTasks = tasks % this.batchSize == 0 ? tasks / this.batchSize : tasks / this.batchSize + 1;

    for (int i = 0; i < batchTasks; i++) {
      int start = i * this.batchSize;
      int end = Math.min(tasks, start + this.batchSize);
      ExecutorService service = this.getExecutorService();
      runnable.subList(start, end).forEach(service::execute);
      service.shutdown();
    }

  }

  /**
   * 获取默认的线程池
   *
   * @return ThreadPoolExecutor
   */
  private SimpleThreadPoolExecutor getDefaultPoolExecutor () {
    return new SimpleThreadPoolExecutor(
        getThreadFactory(),
        getBlockingQueue(),
        getRejectedExecutionHandler());
  }

  /**
   * 简单线程池
   */
  private static class SimpleThreadPoolExecutor implements ExecutorService {

    /**
     * 线程池
     */
    private final ThreadPoolExecutor threadPoolExecutor;

    /**
     * 构造对象
     *
     * @param getThreadFactory 线程工厂
     * @param blockingQueue    线程处理队列
     * @param handler          线程拒绝策略
     */
    public SimpleThreadPoolExecutor (ThreadFactory getThreadFactory,
        BlockingQueue<Runnable> blockingQueue,
        RejectedExecutionHandler handler) {
      this.threadPoolExecutor = new ThreadPoolExecutor(
          getDefaultCorePoolSize(),
          getDefaultMaximumPoolSize(),
          getDefaultKeepAliveTime(),
          getDefaultTimeUnit(),
          blockingQueue,
          getThreadFactory,
          handler
      );
      // 启动所有的核心线程
      this.threadPoolExecutor.prestartAllCoreThreads();
      // 回收核心线程数
      this.threadPoolExecutor.allowCoreThreadTimeOut(Boolean.TRUE);
    }

    /**
     * shutdown方法，调用这个方法也就意味着， 这个线程池不会再接收任何新的任务，但是已经提交的任务还会继续执行
     */
    @Override
    public void shutdown () {
      this.threadPoolExecutor.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow () {
      return this.threadPoolExecutor.shutdownNow();
    }

    @Override
    public boolean isShutdown () {
      return this.threadPoolExecutor.isShutdown();
    }

    @Override
    public boolean isTerminated () {
      return this.threadPoolExecutor.isTerminated();
    }

    /**
     * 设定线程池在关闭之前的最大超时时间，如果在超时时间结束之前线程池能够正常关闭则会返回true，否则，超时会返回false
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return boolean
     * @throws InterruptedException InterruptedException
     */
    @Override
    public boolean awaitTermination (long timeout, TimeUnit unit) throws InterruptedException {
      return this.threadPoolExecutor.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit (Callable<T> task) {
      return this.threadPoolExecutor.submit(task);
    }

    @Override
    public <T> Future<T> submit (Runnable task, T result) {
      return this.threadPoolExecutor.submit(task, result);
    }

    @Override
    public Future<?> submit (Runnable task) {
      return this.threadPoolExecutor.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll (Collection<? extends Callable<T>> tasks) throws InterruptedException {
      return this.threadPoolExecutor.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll (Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException {
      return this.threadPoolExecutor.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny (Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
      return this.threadPoolExecutor.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny (Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
      return this.threadPoolExecutor.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute (Runnable command) {
      this.threadPoolExecutor.execute(command);
    }

  }

}