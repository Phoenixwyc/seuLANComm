package cn.seu.edu.LANComm.communication.util;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2018/2/1.
 */
public class DataProducer implements Runnable{
    private volatile boolean isRunning = true;
    private BlockingQueue queue;
    private static AtomicInteger cout = new AtomicInteger();
    private static final int DEFAULT_RANGE_FOR_SLEEP = 1000;

    public DataProducer(BlockingQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        String data = null;
        Random random = new Random();
        System.out.println("启动生产者线程");
        try {
            while (isRunning) {
                System.out.println("正在生产数据");
                Thread.sleep(random.nextInt(DEFAULT_RANGE_FOR_SLEEP));
                data = "data: " + cout.incrementAndGet();
                System.out.println("将数据: " + data + " 放入队列");
                if (!queue.offer(data, 2, TimeUnit.SECONDS)) {
                    System.out.println("数据：" + data + " 放入失败");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("退出生产者线程");
        }
    }

    public void stop() {
        isRunning = false;
    }
}
