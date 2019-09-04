package edu.concurrent.lock;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockExample {
    private Lock lock = new ReentrantLock();

    public void func(){
        lock.lock();
        try{
            for(int i=0; i < 10; i++)
                System.out.print(i + " ");
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        LockExample eg = new LockExample();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(() -> eg.func());
        executorService.execute(() -> eg.func());

    }
}
