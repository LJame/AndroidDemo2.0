package com.hardrubic.concurrent.interrupt;

/**
 * 线程中断例子
 */
public class InterruptedExample {

    /**
     * 1、调用interrupt并不意味着立即停止目标线程，只是传递了请求中断；
     * 2、只有实现了线程中断策略的代码才可以屏蔽中断，否则要保持中断状态；
     * 3、每个线程拥有各自的中断策略，除非知道中断对线程的含义，否则不应该中断这个线程
     */

    public static void main(String[] args) throws Exception {
        InterruptedExample interruptedExample = new InterruptedExample();
        interruptedExample.start();
    }

    public void start() {
        MyThread myThread = new MyThread();
        myThread.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        myThread.cancel();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class MyThread extends Thread{

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {   //判断线程是否被中断
                try {
                    System.out.println("test");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("interrupt");
                    //如果不触发InterruptedException，中断会保持
                    //抛出InterruptedException后中断标志被清除，标准做法是再次调用interrupt恢复中断
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("stop");
        }

        public void cancel(){
            interrupt();
        }
    }
}
