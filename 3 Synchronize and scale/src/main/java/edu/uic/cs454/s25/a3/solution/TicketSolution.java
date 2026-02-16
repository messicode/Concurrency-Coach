package edu.uic.cs454.s25.a3.solution;


import edu.uic.cs454.s25.a3.Ticket;

import java.util.concurrent.locks.Lock;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class TicketSolution implements Ticket {
    //Constructor/get
    private volatile Status status=Status.ISSUED;
    private final int id;
    private final ReentrantLock ticketLock = new ReentrantLock();


    private boolean statusUpdated = false;


    public TicketSolution(int id) {
        this.id=id;
    }

    public int getId(){return id;}
    public Lock getLock() {
        return ticketLock;
    }

    public Status getStatus() {
//        return nonLinearStatus();
        ticketLock.lock();
         try{
             return status;
         }
         finally {
             ticketLock.unlock();
         }
    }


    public void setStatus(Status status) {
        ticketLock.lock();
        try{
            this.status=status;
            this.statusUpdated=true;
        }
        finally {ticketLock.unlock();}
    }

    public Status nonLinearStatus() {
        return status;//volatile makes sure writes happens before reads
    }

}
