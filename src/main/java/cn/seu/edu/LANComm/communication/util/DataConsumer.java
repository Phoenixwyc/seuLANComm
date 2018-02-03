package cn.seu.edu.LANComm.communication.util;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/2/1.
 */
public class DataConsumer implements Runnable{
    /**
     * ArrayBlockingQueue 数据的放入与获取使用同一个对象锁
     * ArrayBlockingQueue 内部还保存着两个整形变量，分别标识着队列的头部和尾部在数组中的位置
     * ArrayBlockingQueue 插入或删除元素时不会产生或销毁任何额外的对象实例
     * ArrayBlockingQueue 可以控制对象的内部锁是否采用公平锁
     * LinkedBlockingQueue 数据的放入与获取使用不同的对象锁，可以实现两者同时运行
     * https://www.cnblogs.com/geningchao/p/6638781.html
     */
    private BlockingQueue<String> queue;
    private static final int DEFAULT_RANGE_FOR_SLEEP = 1000;

    public DataConsumer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        System.out.println("启动消费者线程");
        Random random = new Random();
        boolean isRunning = true;
        try {
            while (isRunning) {
                System.out.println("正在从队列获取数据");
                String data = queue.poll(2, TimeUnit.SECONDS);
                if (null != data) {
                    System.out.println("拿到数据：" + data);
                    System.out.println("正在消费数据：" + data);
                    Thread.sleep(random.nextInt(DEFAULT_RANGE_FOR_SLEEP));
                } else {
                    // 超时，认为生产者线程退出，退出消费者线程
                    isRunning = false;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("退出消费者线程");
        }
    }
}
