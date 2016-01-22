package com.hardrubic.concurrent;


import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * 信号量控制同时访问某个资源的操作数量
 */
public class SemaphoreExample {

    /**
     * 只有3本书，10个学生阅读
     */
    public void startTestSemaphore() {
        //定义3个许可
        final Semaphore semaphore = new Semaphore(3);

        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            new Thread(() -> {
                try {
                    semaphore.acquire(); //如果没有许可，阻塞
                    System.out.println("student " + finalI + " read book");
                    Thread.sleep(new Random().nextInt(10000) + 1000);
                    System.out.println("student " + finalI + " finish read book");
                    semaphore.release(); //释放许可
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
