package generator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReetrantLockDemo {

    private boolean flag = false;
    private final int MAX_NO = 5;
    ReentrantLock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    public void getA(){
        try {
            lock.lock();
            while(flag == true){
               condition.await();
            }
            System.out.print("a");
            flag = true;
            condition.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void getB(){
        try {
            lock.lock();
            while(flag == false){
                condition.await();
            }
            System.out.print("b");
            flag = false;
            condition.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private static class Arunnable implements Runnable{
        private ReetrantLockDemo reetrantLockDemo;
        public Arunnable(ReetrantLockDemo reetrantLockDemo) {
            this.reetrantLockDemo = reetrantLockDemo;
        }

        @Override
        public void run() {
            for (int i = 0; i <reetrantLockDemo.MAX_NO ; i++) {
                reetrantLockDemo.getA();
            }
        }
    }

    private static class Brunnable implements Runnable{
        private ReetrantLockDemo reetrantLockDemo;
        public Brunnable(ReetrantLockDemo reetrantLockDemo) {
            this.reetrantLockDemo = reetrantLockDemo;
        }

        @Override
        public void run() {
            for (int i = 0; i <reetrantLockDemo.MAX_NO ; i++) {
                reetrantLockDemo.getB();
            }
        }
    }

    public static void main(String[] args) {
        ReetrantLockDemo reetrantLockDemo = new ReetrantLockDemo();
        ExecutorService threads = Executors.newFixedThreadPool(10);
        Arunnable arunnable = new Arunnable(reetrantLockDemo);
        Brunnable brunnable = new Brunnable(reetrantLockDemo);
        threads.submit(arunnable);
        threads.submit(brunnable);11221112
    }

}
