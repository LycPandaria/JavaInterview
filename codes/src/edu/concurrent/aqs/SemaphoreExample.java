package edu.concurrent.aqs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SemaphoreExample {

    public static void main(String[] args) {
        final int clientCount = 3;
        final int totalRequest = 10;

        Semaphore semaphore = new Semaphore(clientCount);
        ExecutorService executorService = Executors.newCachedThreadPool();

        for(int i = 0; i < totalRequest; i++){
            executorService.execute( () -> {
                try {
                    semaphore.acquire();
                    System.out.println("Processing...");
                    System.out.println("Available permits: " +semaphore.availablePermits());
                    Thread.sleep(5000);
                    System.out.println("Finish process! Do release!");
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    semaphore.release();
                }
            });
        }
        executorService.shutdown();
    }

}
