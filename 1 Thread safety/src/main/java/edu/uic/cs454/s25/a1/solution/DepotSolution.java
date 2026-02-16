package edu.uic.cs454.s25.a1.solution;

import edu.uic.cs454.s25.a1.Action;
import edu.uic.cs454.s25.a1.Bus;
import edu.uic.cs454.s25.a1.Depot;
import edu.uic.cs454.s25.a1.Ticket;

import java.util.*;

public class DepotSolution extends Depot<BusSolution,TicketSolution> {

    private final Object lock = new Object();
    private final Map<Integer,TicketSolution> tickets = new HashMap<Integer,TicketSolution>();
    //private final Map<Integer,BusSolution> buses = new HashMap<Integer,BusSolution>();

    @Override
    public BusSolution createBus(int capacity) {
            BusSolution bus = new BusSolution(capacity);
            //buses.put(bus.getId(), bus);
            return bus;
    }

    @Override
    public TicketSolution issueTicket(int id) {
        synchronized (lock) {
            TicketSolution ticket = new TicketSolution(id);
            if (tickets.containsKey(id)) {
                throw new IllegalArgumentException("Ticket already issued.");
            }

            tickets.put(id, ticket);
            return ticket;
        }
    }

    @Override
    public boolean boardBus(BusSolution bus, Set<TicketSolution> tickets) {
        synchronized (lock){
            if (!bus.hasCapacity(tickets.size())) {
                return false;
            }

            return bus.addTickets(tickets);
        }
    }

    @Override
    public boolean transferTickets(BusSolution from, BusSolution to, Set<TicketSolution> tickets) {
        synchronized (lock){
            //Need 'if' to ensure atomicity
            if(to.hasCapacity(tickets.size()) && from.hasTickets(tickets)){
               return (from.removeTickets(tickets) && to.addTickets(tickets));
            }
            return false;
        }
    }

    @Override
    public boolean useTickets(BusSolution bus, Set<TicketSolution> tickets) {
        synchronized (lock){
            boolean used = true;
            for (TicketSolution ticket : tickets) {
                if (ticket.getStatus() == Ticket.Status.IN_CIRCULATION) {
                    ticket.setStatus(Ticket.Status.USED);
                } else {
                    used = false;
                }
            }
            return used;
        }
    }

    @Override
    public boolean expireTickets(BusSolution bus, Set<TicketSolution> tickets) {
        synchronized (lock){
            boolean expired = true;
            for (TicketSolution ticket : tickets) {
                if (ticket.getStatus() == Ticket.Status.IN_CIRCULATION) {
                    ticket.setStatus(Ticket.Status.EXPIRED);
                } else {
                    expired = false;
                }
            }
            if(expired)bus.removeTickets(tickets);
            return expired;
        }
    }

    @Override
    public Set<TicketSolution> getTickets() {
        synchronized (lock){
            Set<TicketSolution> result=new HashSet<TicketSolution>();
            for(TicketSolution ticket:tickets.values()){
                if(ticket.getStatus() == Ticket.Status.IN_CIRCULATION){result.add(ticket);}
            }
            return result;
        }
    }

    @Override
    public Set<TicketSolution> getTickets(BusSolution bus) {
        synchronized (lock){
            return bus.getTickets();
        }
    }

    @Override
    public List<Action<TicketSolution>> audit(BusSolution bus) {
        List<Action<TicketSolution>> result = new ArrayList<>();
        for (TicketSolution ticket : bus.getTicketHistory()) {
            result.addAll(ticket.getAudit());
        }
        return result;
    }

    @Override
    public List<Action<BusSolution>> audit(TicketSolution ticket) {
        List<Action<BusSolution>> result = new ArrayList<>();
        for (BusSolution bus : ticket.getBusHistory()) {
            result.add(new Action<>(Action.Direction.MOVED_IN, bus));
        }
        return result;
    }
}
