/**
 * 线程队列
 * <p>
 * Java中，提供了两种线程安全队列的实现方式:一种是阻塞机制，另一种是非阻塞机制。
 * </p>
 * <p>
 * BlockingQueue是一个线程安全的阻塞队列,通过使用锁的方式来实现
 * </p>
 * <p>
 * ConcurrentLinkedQueue是一个非阻塞机制的队列,通过使用CAS方式实现
 * </p>
 *
 * @author maoxiaoxu
 * @version 1.0
 */
package thread.queue;