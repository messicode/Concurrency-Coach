package edu.uic.cs454.s25.a3.solution;

import edu.uic.cs454.s25.a3.Bus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class BusSolution implements Bus {
    private final int capacity;
    /*default*/ final Set<TicketSolution> tickets;
//    private final List<Action<BusSolution>> audit;
    private final List<TicketSolution> ticketHistory;

    private final long id;
    /*default*/ final ReentrantReadWriteLock busLock;
    private volatile static AtomicLong idCounter = new AtomicLong(0);

    public BusSolution(int capacity) {
//        this.id=++counter;
        this.capacity = capacity;
        this.tickets = new HashSet<TicketSolution>();
//        this.audit=new ArrayList<Action<BusSolution>>();
        this.ticketHistory = new ArrayList<>();
        this.busLock = new ReentrantReadWriteLock();
        this.id=genID();
    }
    private long genID() {return idCounter.getAndIncrement();}
    public long getId() {return id;}


    public boolean hasCapacity(int count)
    {
        return (this.tickets.size() + count <= this.capacity) ;
    }

    public boolean hasTickets(Set<TicketSolution> tickets){
        return this.tickets.containsAll(tickets);
    }

    public Lock getBusLock() {
        return busLock.writeLock();
    }
    public void removeTickets(Set<TicketSolution> tickets) {
        this.tickets.removeAll(tickets);
    }


}
