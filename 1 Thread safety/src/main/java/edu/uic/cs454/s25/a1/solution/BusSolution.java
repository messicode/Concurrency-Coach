package edu.uic.cs454.s25.a1.solution;

import edu.uic.cs454.s25.a1.Action;
import edu.uic.cs454.s25.a1.Bus;
import edu.uic.cs454.s25.a1.Ticket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BusSolution implements Bus {
    private final int capacity;
    private final List<TicketSolution> tickets;
    private final List<Action<BusSolution>> audit;
    private final List<TicketSolution> ticketHistory;
//    private static int counter=0;
//    private int id;

    public BusSolution(int capacity) {
//        this.id=++counter;
        this.capacity = capacity;
        this.tickets = new ArrayList<TicketSolution>();
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

//    public int getId(){return id;}


    public boolean addTickets(Set<TicketSolution> tickets){

        if(hasCapacity(tickets.size()))
        {
            //? Check if ticket is in another bus
            for (TicketSolution ticket : tickets) {
                if (ticket.getCurrentBus() == null || !ticket.getCurrentBus().equals(this)) {
                    if (ticket.getStatus() != Ticket.Status.EXPIRED && ticket.getStatus() != Ticket.Status.USED) {
                        ticket.setCurrentBus(this);

                        if (ticket.getStatus() == Ticket.Status.ISSUED) {
                            ticket.setStatus(Ticket.Status.IN_CIRCULATION);
                            ticket.addAudit(new Action<>(Action.Direction.IN_CIRCULATION, ticket));
                        }
                        audit.add(new Action<>(Action.Direction.MOVED_IN, this));
                        this.tickets.add(ticket);

                        if (!ticketHistory.contains(ticket)) {
                            ticketHistory.add(ticket);
                        }
                    } else {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public boolean removeTickets(Set<TicketSolution> tickets){

        if(hasTickets(tickets))
        {
            this.tickets.removeAll(tickets);
            for(TicketSolution ticket : tickets){
                ticket.setCurrentBus(null);
                audit.add(new Action<>(Action.Direction.MOVED_OUT,this));
                ticket.addAudit(new Action<>(Action.Direction.MOVED_OUT, ticket));
            }
            return true;
        }
        return false;
    }

    public Set<TicketSolution> getTickets(){
        Set<TicketSolution> result=new HashSet<TicketSolution>();
        for(TicketSolution ticket:tickets){
            if(ticket.getStatus() == Ticket.Status.IN_CIRCULATION || ticket.getStatus() == Ticket.Status.ISSUED)
            {result.add(ticket);}
        }
        return result;
    }

    public List<Action<BusSolution>> getAudit() {
        return audit;
    }
    public List<TicketSolution> getTicketHistory() {
        return new ArrayList<>(ticketHistory);
    }
}
