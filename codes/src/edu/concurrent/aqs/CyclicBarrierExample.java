package edu.concurrent.aqs;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CyclicBarrierExample {
    public static void main(String[] args) {
        final int nThreads = 5;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(nThreads);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0; i<nThreads; i++){
            executorService.execute( () -> {
                System.out.println("before...");
                try {
                    cyclicBarrier.await();
                }catch (InterruptedException | BrokenBarrierException e){
                    e.printStackTrace();
                }
                System.out.println("after...");
            });
        }
        executorService.shutdown();
    }
}
