# 一、线程状态转换
![线程状态转换](../pic/线程状态转换.jpg)

## 新建-New
创建后没有启动

## 可运行-Runnable
可能正在运行，也可能正在等待 CPU 时间片。

包含了操作系统线程状态中的 Running 和 Ready。

## 阻塞-blocking
等待获取一个排它锁，如果其线程释放了锁就会结束此状态。

## 无限期等待-Waiting
等待其它线程显式地唤醒，否则不会被分配 CPU 时间片。

## 限期等待-Timed Waiting
无需等待其它线程显式地唤醒，在一定时间之后会被系统自动唤醒。

调用 Thread.sleep() 方法使线程进入限期等待状态时，常常用“使一个线程睡眠”进行描述。

调用 Object.wait() 方法使线程进入限期等待或者无限期等待时，常常用“挂起一个线程”进行描述。

睡眠和挂起是用来描述行为，而阻塞和等待用来描述状态。

阻塞和等待的区别在于，阻塞是被动的，它是在等待获取一个排它锁。而等待是主动的，通过调用 Thread.sleep() 和 Object.wait() 等方法进入。

## 死亡-Terminated
可以是线程结束任务之后自己结束，或者产生了异常而结束。

# 二、使用线程
1. 继承Thread类，重写run
Thread本质上也是实现了Runnable接口的一个实例，它代表一个线程的实例，启动线程的唯一办法是通过Thread的start方法，这是一个native方法。
```java
class MyThread extends Thread{
	public void run{ System.out.println("Thread Body");}
}
public class Test{
	public static void main(String[] args){
		MyThread t = new MyThread();
		t.start();
	}
}
```
2. 实现Runnable接口，并实现run()方法
```java
class MyThread implements Runnable{
	public void run{ System.out.println("Thread Body");}
}
public class Test{
	public static void main(String[] args){
		MyThread t = new MyThread();
		Thread thread = new Thread(t);
		thread.start();
	}
}
```
不管是通过继承Thread类还是通过Runnable接口实现多线程方法，最终都是要通过调用Thread对象的API来控制线程。

3. 实现Callable接口，重写call()方法。
Callable接口实际是属于Executor框架中的功能类：
   1. Callable可以在任务结束后提供一个返回值，Runnable不行
   2. Callable中的call()可以抛出异常
   3. 运行Callable可以拿到一个Future对象，表示异步计算的结果，它提供了检查计算是否完成的方法。可以使用Future监视目标线程调用call()方法的情况，当调用Future的get()方法获取结果，当前线程就会阻塞，直到call()方法结束返回结果。
```java
import java.util.concurrent.*;
public class CallableAndFuture{
//创建线程类
	public static class CallableTest implements Callable<String> {
		public String call() throws Exception{ return "Hello World";}
	}
	public static void main(String[] args){
		ExecutorService threadPool = Executor.newSingleThreadExecutor();
		//启动线程
		Future<String> future = threadPool.submit(new CallableTest());
		try{
			System.out.println(future.get());
		}catch(Exception e){
			e.printStackTrack();)
	}
}
}
```

## run()和start()
系统通过start()方法启动线程，此刻线程处于就绪状态，JVM调用run方法完成实际操作。   
如果直接调用线程类的run()方法，这会被当做一个普通的函数调用。

## 实现接口 VS 继承 Thread
实现接口会更好一些，因为：
  - Java 不支持多重继承，因此继承了 Thread 类就无法继承其它类，但是可以实现多个接口；
  - 类可能只要求可执行就行，继承整个 Thread 类开销过大。

# 三、基础线程机制
## Executor
Executor 管理多个异步任务的执行，而无需程序员显式地管理线程的生命周期。这里的异步是指多个任务的执行互不干扰，不需要进行同步操作。

Executor 主要有三种：
- CachedThreadPool：一个任务创建一个线程；
- FixedThreadPool：所有任务只能使用固定大小的线程；
- SingleThreadExecutor：相当于大小为 1 的 FixedThreadPool。

```java
public static void main(String[] args) {
    ExecutorService executorService = Executors.newCachedThreadPool();
    for (int i = 0; i < 5; i++) {
        executorService.execute(new MyRunnable());
    }
    executorService.shutdown();
}
```

## Daemon
守护线程是程序运行时在后台提供服务的线程，不属于程序中不可或缺的部分。

当所有非守护线程结束时，程序也就终止，同时会杀死所有守护线程。

main() 属于非守护线程。

使用 setDaemon() 方法将一个线程设置为守护线程。

```java
public static void main(String[] args) {
    Thread thread = new Thread(new MyRunnable());
    thread.setDaemon(true);
}
```

## sleep()
Thread.sleep(millisec) 方法会休眠当前正在执行的线程，millisec 单位为毫秒。

sleep() 可能会抛出 InterruptedException，因为异常不能跨线程传播回 main() 中，因此必须在本地进行处理。线程中抛出的其它异常也同样需要在本地进行处理。

```java
public void run() {
    try {
        Thread.sleep(3000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```

## yield()
对静态方法 Thread.yield() 的调用声明了当前线程已经完成了生命周期中最重要的部分，可以切换给其它线程来执行。该方法只是对线程调度器的一个建议，而且也只是建议具有相同优先级的其它线程可以运行。

```java
public void run() {
    Thread.yield();
}
```

# 四、中断
一个线程执行完毕之后会自动结束，如果在运行过程中发生异常也会提前结束
## InterruptedException
通过调用一个线程的 interrupt() 来中断该线程，如果该线程处于阻塞，等待的状态，就会抛出 InterruptedException，从而提前结束该线程，但是不能中断 I/O 阻塞和 synchronized 锁阻塞

对于以下代码，在 main() 中启动一个线程之后再中断它，由于线程中调用了 Thread.sleep() 方法，因此会抛出 InterruptedException，从而提前结束进程，不在执行后面的语句

```java
public class InterruptExample {
    private static class MyThread1 extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(2000);
                System.out.println("Thread run");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public static void main(String[] args) throws InterruptedException {
    Thread thread1 = new MyThread1();
    thread1.start();
    thread1.interrupt();
    System.out.println("Main run");
}
```
```text
Main run
java.lang.InterruptedException: sleep interrupted
    at java.lang.Thread.sleep(Native Method)
    at InterruptExample.lambda$main$0(InterruptExample.java:5)
    at InterruptExample$$Lambda$1/713338599.run(Unknown Source)
    at java.lang.Thread.run(Thread.java:745)
```

## interrupt()
如果一个线程的 run() 方法执行一个无限循环，并且没有执行 sleep() 等会抛出 InterruptedException 的操作，interrupt() 方法就无法使线程提前结束

但是调用 interrupt() 方法会设置线程的中断标记，此时调用 interrupt() 方法会返回 true。因此可以在循环体检查 interrupt() 方法来判断线程是否处于中断状态，从而提前结束线程。

```java
public class InterruptExample {
    private static class MyThread2 extends Thread {
        @Override
        public void run() {
            while (!interrupted()) {
                // ..
            }
            System.out.println("Thread end");
        }
    }
}

public static void main(String[] args) throws InterruptedException {
    Thread thread2 = new MyThread2();
    thread2.start();
    thread2.interrupt();
}
```
```text
Thread end
```

## Executor 的中断操作
调用 Executor 的 shutdown() 方法会等待线程都执行完毕之后再关闭，但是如果调用的是 shutdownNow() 方法，相当于调用每个线程的 interrupt() 方法
```java
public static void main(String[] args) {
    ExecutorService executorService = Executors.newCachedThreadPool();
    executorService.execute(() -> {
        try {
            Thread.sleep(2000);
            System.out.println("Thread run");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    });
    executorService.shutdownNow();
    System.out.println("Main run");
}
```
```text
Main run
java.lang.InterruptedException: sleep interrupted
    at java.lang.Thread.sleep(Native Method)
    at ExecutorInterruptExample.lambda$main$0(ExecutorInterruptExample.java:9)
    at ExecutorInterruptExample$$Lambda$1/1160460865.run(Unknown Source)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
    at java.lang.Thread.run(Thread.java:745)
```

如果只想中断 Executor 的一个线程，可以通过使用 submit() 方法来提交一个线程，它会返回一个 Future<?> 对象，调用该对象的 cancel(true) 方法就可以中断线程
```java
Future<?> future = executorService.submit(() -> {
    // ..
});
future.cancel(true);
```
