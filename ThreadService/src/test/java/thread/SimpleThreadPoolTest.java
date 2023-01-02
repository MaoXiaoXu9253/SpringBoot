package thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import thread.pool.simple.SimpleThreadPool;

/**
 * @author maoxiaoxu
 * @version 1.0
 */
@Slf4j
class SimpleThreadPoolTest {

  public static void main (String[] args) {

    SimpleThreadPoolTest test = new SimpleThreadPoolTest();
    int batchSize = 17;
    String name = "xiaoxu";
    long id = name.hashCode();
    test.simpleTest();
    test.simpleTest(batchSize);
    test.simpleTest(name, id);
    test.simpleTest(name, id, batchSize);

  }

  private static List<Runnable> getList () {
    int size = 55;
    List<Runnable> runnables = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      runnables.add(new Runnables());
    }
    return runnables;
  }

  void simpleTest () {
    List<Runnable> runnables = getList();
    new SimpleThreadPool().execute(runnables);
  }

  void simpleTest (int batchSize) {
    List<Runnable> runnables = getList();
    new SimpleThreadPool(batchSize).execute(runnables);
  }

  void simpleTest (String name, long id) {
    List<Runnable> runnables = getList();
    new SimpleThreadPool(name, id).execute(runnables);
  }

  void simpleTest (String name, long id, int batchSize) {
    List<Runnable> runnables = getList();
    new SimpleThreadPool(name, id, batchSize).execute(runnables);
  }

  private static class Runnables implements Runnable {

    private static final AtomicInteger taskNumber = new AtomicInteger(1);

    @Override
    public void run () {
      int increment = Runnables.taskNumber.getAndIncrement();
      try {
        log.info("taskNumber-[{}]-start", increment);
        int nextInt = new Random().nextInt(45);
        TimeUnit.SECONDS.sleep(nextInt);
      } catch (InterruptedException e) {
        log.warn("taskNumber-[{}]-error", increment, e);
      } finally {
        log.info("taskNumber-[{}]-end", increment);
      }
    }

  }
}