package thread;

import java.util.List;

/**
 * 线程池执任务方法
 *
 * @author maoxiaoxu
 * @version 1.0
 */
public interface BatchExecuteTasks {

  /**
   * 执行批量任务
   *
   * @param runnable 任务集合
   */
  void execute (List<Runnable> runnable);
}