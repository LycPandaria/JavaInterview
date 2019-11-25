package edu.concurrent.aqs;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class FutureTaskExample {
    public static void main(String[] args) throws Exception {
        FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int result = 0;
                for(int i = 0; i < 100; i++){
                    result += i;
                    Thread.sleep(10);
                }
                return result;
            }
        });

        Thread computer = new Thread(futureTask);
        computer.start();

        while(!futureTask.isDone()){
            System.out.println("Processing...");
            Thread.sleep(1000);
        }
        System.out.println("Finish, result = " + futureTask.get());
    }
}
