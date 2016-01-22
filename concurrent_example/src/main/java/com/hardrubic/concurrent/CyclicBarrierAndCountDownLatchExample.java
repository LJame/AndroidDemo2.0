package com.hardrubic.concurrent;


import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierAndCountDownLatchExample {

    /**
     * CyclicBarrier
     * N个线程相互等待，任何一个线程完成之前，所有的线程都必须等待
     * 重点是全部线程到达栅栏之后，全部继续往前走
     * 参加活动的人全部集中一起再一起出发
     */
    public void startTestCyclicBarrier() {
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(10, new Runnable() {
            @Override
            public void run() {
                System.out.println("所有线程通过栅栏");
            }
        });

        for (int i = 0; i < 10; i++) {
            final int finalI = i + 1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("线程" + finalI + "启动");
                    Random random = new Random();
                    try {
                        Thread.sleep(random.nextInt(10000) + 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("线程" + finalI + "完毕");

                    //通知barrier已完成
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                    System.out.println("线程" + finalI + "继续往前");
                }
            }).start();
        }
    }

    /**
     * CountDownLatch
     * 一个线程(或者多个)， 等待另外N个线程完成某个事情之后才能执行
     * 重点是一个线程等待N个线程执行完毕，再继续执行
     * 国家等各个省对数据进行汇总后，再全国汇总
     */
    public void startTestCountDownLatch() {
        int threadNum = 10;
        final CountDownLatch countDownLatch = new CountDownLatch(threadNum);

        for (int i = 0; i < threadNum; i++) {
            final int finalI = i + 1;
            new Thread(() -> {
                System.out.println("thread " + finalI + " start");
                Random random = new Random();
                try {
                    Thread.sleep(random.nextInt(10000) + 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("thread " + finalI + " finish");

                countDownLatch.countDown();
            }).start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(threadNum + " thread finish");
    }
}
