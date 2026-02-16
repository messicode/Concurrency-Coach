package edu.uic.cs454.s25.a4.solution;

import edu.uic.cs454.s25.a4.Result;

import java.util.concurrent.locks.ReentrantLock;

public class ResultSolution<T> extends Result<T> {

    @Override
    public void setResult(T result) {
        synchronized (this) {
            this.set(result); // sets ready=true internally
            this.notifyAll();

        }
    }

    @Override
    public T getResult() {
        synchronized (this) {
            while(!this.isReady()){
                try{
                    this.wait(100L);
                    continue;
                }
                catch(InterruptedException e){
                    continue;
                }
            }
            return this.get();
        }

    }


}
