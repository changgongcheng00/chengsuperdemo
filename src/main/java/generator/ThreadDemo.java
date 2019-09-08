package generator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadDemo {
    //公共变量
    int no = 0;
    static AtomicInteger num = new AtomicInteger(0);
    static ReentrantLock lock = new ReentrantLock();
    static CassellReentrantLock clock = new CassellReentrantLock();
    //公共方法
    public void add(){//synchronized
//        synchronized (this){
//            no++;
//        }
        no++;
    }
    public static void increment(){
        num.getAndIncrement();
    }
    //线程类，继承Thread
    private static class CountThread extends Thread{

        private ThreadDemo threadDemo;

        public CountThread(ThreadDemo threadDemo) {
            this.threadDemo = threadDemo;
        }
        @Override
        public void run() {
            lock.lock();
            for (int i = 0; i <10000 ; i++) {
                threadDemo.add();
                increment();
            }
            lock.unlock();
        }
    }
    //线程类，实现runnable
    private static class CountRunnable implements Runnable{

        private ThreadDemo threadDemo;

        public CountRunnable(ThreadDemo threadDemo) {
            this.threadDemo = threadDemo;
        }
        @Override
        public void run() {
            clock.lock();
            for (int i = 0; i <10000 ; i++) {
                threadDemo.add();
                increment();
            }
            clock.unlock();
        }
    }
    //测试结果

    /**
     *Thread将线程执行体和线程绑定在了一起，执行多次需要手工创建多个线程对象。
     *Runnable将执行体与线程分开，用线程池负责线程的创建与回收
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        //创建简单线程池
        ExecutorService threads = Executors.newFixedThreadPool(10);
        //实例对象，传输公共属性和方法
        ThreadDemo td1 = new ThreadDemo();
        ThreadDemo td2 = new ThreadDemo();
        //线程类
        CountThread ct1 = new CountThread(td1);
        CountThread ct2 = new CountThread(td1);
        CountRunnable cr1 = new CountRunnable(td2);
        CountRunnable cr2 = new CountRunnable(td2);
        //启动线程
        ct1.start();
        ct2.start();
        threads.submit(cr1);
        threads.submit(cr2);
        //睡眠当前线程main，等待任务线程执行结束
        Thread.sleep(4000);
        System.out.println(td1.no+","+td2.no);
        System.out.println(ThreadDemo.num);
    }

}
