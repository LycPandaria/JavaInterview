package edu.concurrent.lock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AwaitSignalExample {
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void before(){
        lock.lock();
        try{
            System.out.println("Before!");
            condition.signalAll();          // 唤醒等待的线程
        }finally {
            lock.unlock();
        }
    }

    public void after(){
        lock.lock();
        try{
            condition.await();              // 等待 condition 的 signal
            System.out.println("After!");
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        AwaitSignalExample eg = new AwaitSignalExample();
        executorService.execute(() -> eg.after());
        executorService.execute(() -> eg.before());
    }
}
