package com.hardrubic.concurrent;


import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 生产者消费者
 */
public class ProducerConsumerExample {
    public void testProducerConsumer() {
        final BlockingQueue<Long> queue = new ArrayBlockingQueue(10);

        new Thread(() -> {
            try {
                while (true) {
                    if (queue.size() >= 10) {
                        System.out.println("producer wait");
                        wait();
                    } else {
                        Random random = new Random();
                        queue.put(random.nextLong());
                        System.out.println("producer make,now size:" + queue.size());
                        wait(1000);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
