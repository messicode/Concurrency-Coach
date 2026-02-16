package edu.uic.cs454.s25.a2.solution;

import edu.uic.cs454.s25.a2.Action;
import edu.uic.cs454.s25.a2.Bus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BusSolution implements Bus {
    private final int capacity;
    /*default*/ final Set<TicketSolution> tickets;
    private final List<Action<BusSolution>> audit;
    private final List<TicketSolution> ticketHistory;
//    private static int counter=0;
//    private int id;

    public BusSolution(int capacity) {
//        this.id=++counter;
        this.capacity = capacity;
        this.tickets = new HashSet<TicketSolution>();
        this.audit=new ArrayList<Action<BusSolution>>();
        this.ticketHistory = new ArrayList<>();
    }
    public boolean hasCapacity(int count)
    {
        return (this.tickets.size() + count <= this.capacity) ;
    }

    public boolean hasTickets(Set<TicketSolution> tickets){
        return this.tickets.containsAll(tickets);
    }

    public List<Action<BusSolution>> getAudit() {
        return audit;
    }


}
