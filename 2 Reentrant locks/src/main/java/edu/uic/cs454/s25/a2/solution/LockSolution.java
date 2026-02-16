package edu.uic.cs454.s25.a2.solution;

import edu.uic.cs454.s25.a2.CS454Lock;

import java.util.concurrent.atomic.AtomicBoolean;
//Change counter=4
public class LockSolution extends CS454Lock {

    private AtomicBoolean locked = new AtomicBoolean(false);//To ensure Happens before
    private volatile Thread owner = null;//To avoid data races in owners, made this volatile
    private volatile int reCount=0;//To ensure writes Happens before reads

    private static final int MIN_DELAY=100;
    private static final int MAX_DELAY=10000;



    @Override
    public void lock() {
        if(locked.get() && owner==Thread.currentThread())//Updated for happens before
        {reCount++;return;}//if owner wants to get lock
        while(true){
            while(locked.get());//spin locally

            if(!locked.getAndSet(true)){
                owner=Thread.currentThread();
                reCount=1;
                return;//Get lock if its false
            }
            int delay=MIN_DELAY;
            sleep(delay);//Use my func for sleeping

            if(delay<MAX_DELAY){
                delay*=2;//Exponential
            }
        }

    }
    //My sleeping mechanism
    private void sleep(int delay){
        long start=System.nanoTime();
        while(System.nanoTime()-start<delay);
    }

    @Override
    public boolean tryLock() {
        //Updated for happens before
        if(locked.get()&&owner==Thread.currentThread()) {reCount++;return true;}//Thread Re-entry
        else if(locked.compareAndSet(false,true)){//Need to make this atomic to avoid data-race
            reCount=1;
            owner = Thread.currentThread();
            return true;
        }
        return false;
    }

    @Override
    public void unlock() {
        //checks
        if(!locked.get()) throw new IllegalMonitorStateException("lock already unlocked");//New added
        if(owner != Thread.currentThread())
            throw new IllegalMonitorStateException("Wrong Thread unlocking");
        reCount--;
        if(reCount==0) {
            owner = null;//Any subsequent lock calls will happen only after the owner fully releases lock
            locked.set(false);//Atomic: Thread only releases lock when owner is set to null
            }
    }

    @Override
    public boolean isReentered() {
        return reCount>1;
    }
}
