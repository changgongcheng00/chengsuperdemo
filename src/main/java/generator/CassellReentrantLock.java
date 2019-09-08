package generator;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

public class CassellReentrantLock implements Lock {

    //原子特殊线程
    AtomicReference<Thread> owner = new AtomicReference<>();
    //等待列表，存放没有抢到锁的线程
    LinkedBlockingDeque<Thread> waiter = new LinkedBlockingDeque<>();

    @Override
    public void lock() {
        //比较旧值null，如一致则替换为新值Thread.currentThread()
        while(!owner.compareAndSet(null,Thread.currentThread())){
            //没有抢到，进入等待列表
            waiter.add(Thread.currentThread());
            //等待，类似wait
            LockSupport.park();
            //线程被唤醒，从等待列表中移除该线程
            waiter.remove(Thread.currentThread());
        }
    }

    @Override
    public void unlock() {
        //比较旧值Thread.currentThread()，如一致则替换为新值null
        if(owner.compareAndSet(Thread.currentThread(),null)){
            //遍历等待线程列表，唤醒
            Object[] objects = waiter.toArray();
            for (Object object : objects) {
                Thread next = (Thread)object;
                LockSupport.unpark(next);
            }
        }
    }

    @Override
    public void lockInterruptibly() {
    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
