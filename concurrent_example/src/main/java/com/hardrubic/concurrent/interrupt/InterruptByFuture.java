package com.hardrubic.concurrent.interrupt;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 * 通过Future来实现中断
 */
public class InterruptByFuture {

    public static void main(String[] args) throws Exception {
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<?> task = es.submit(new MyThread());

        try {
            //限定时间获取结果
            task.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            //超时触发线程中止
            System.out.println("thread over time");
        } catch (ExecutionException e) {
            throw e;
        } finally {
            /**
             * mayInterruptIfRunning
             * 1、表示任务是否能够接收中断，不表示任务是否能检测并处理中断
             * 2、mayInterruptIfRunning=true时，任务如果在某个线程中运行，那么这个线程能够被中断
             * 3、mayInterruptIfRunning=false时，任务如果还未启动，就不要运行它，应用于不处理中断的任务
             */
            boolean mayInterruptIfRunning = true;
            task.cancel(mayInterruptIfRunning);
        }
    }

    private static class MyThread extends Thread {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {   //判断线程是否被中断
                try {
                    System.out.println("count");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("interrupt");
                    //如果不触发InterruptedException，中断会保持
                    //抛出InterruptedException后中断标志被清除，标准做法是再次调用interrupt恢复中断
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("thread stop");
        }

        public void cancel() {
            interrupt();
        }
    }
}
