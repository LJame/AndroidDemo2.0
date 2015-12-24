package com.hardrubic.thread;


import com.hardrubic.util.LogUtils;

import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * 信号量控制同时访问某个资源的操作数量
 */
public class TestSemaphore {

    /**
     * 只有3本书，10个学生阅读
     */
    public void startTestSemaphore() {
        //定义3个许可
        final Semaphore semaphore = new Semaphore(3);

        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        semaphore.acquire(); //如果没有许可，阻塞
                        LogUtils.d("学生" + finalI + "读书");
                        Thread.sleep(new Random().nextInt(10000) + 1000);
                        LogUtils.d("学生" + finalI + "读完书");
                        semaphore.release(); //释放许可
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
