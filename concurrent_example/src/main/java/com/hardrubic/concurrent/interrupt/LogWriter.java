package com.hardrubic.concurrent.interrupt;

import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

/**
 * BlockingQueue提供生产者-消费者模式
 * 将日志消息放入某个队列中，由其他线程处理
 */
public class LogWriter {
    private final static int CAPACITY = 100;
    private BlockingQueue<String> queue;
    private LoggerThread logger;
    private PrintWriter writer;
    private boolean isShutdown;
    private int reservations;

    public void start() {
        logger.start();
    }

    public void stop() {
        synchronized (this) {
            isShutdown = true;
        }
        LoggerThread.interrupted();
    }

    public void log(String msg) throws InterruptedException {
        /*
         * 原子方式检查关闭，并递增一个计数器保证提交了必定能执行
         */
        synchronized (this) {
            if (isShutdown) {
                throw new IllegalStateException("logger is shutdown");
            }
            ++reservations;
        }
        queue.put(msg);
    }

    private class LoggerThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    try {
                        synchronized (LogWriter.this) {
                            //关闭标识=true 并且处理完所有已提交记录才能关闭
                            if (isShutdown && reservations == 0) {
                                break;
                            }
                        }
                        String msg = queue.take();
                        synchronized (LogWriter.this) {
                            --reservations;
                        }
                        writer.println(msg);
                    } catch (InterruptedException e) {
                        //retry
                    }
                }
            } finally {
                writer.close();
            }
        }
    }
}
