package edu.concurrent.aqs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountdownLatchExmple {
    public static void main(String[] args) throws Exception{
        final int nThreads = 10;
        CountDownLatch latch = new CountDownLatch(nThreads);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0; i<nThreads; i++){
            executorService.execute( () -> {
                System.out.println("Run...");
                latch.countDown();
            });
        }
        latch.await();
        System.out.println("End...");
        executorService.shutdown();
    }
}
