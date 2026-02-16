package edu.uic.cs454.s25.a2.solution;

import edu.uic.cs454.s25.a2.Action;
import edu.uic.cs454.s25.a2.CS454Lock;
import edu.uic.cs454.s25.a2.Depot;
import edu.uic.cs454.s25.a2.Ticket;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DepotSolution extends Depot<BusSolution,TicketSolution> {

//    private final Object lock = new Object();
    private CS454Lock lock = new LockSolution();

    private Set<BusSolution> buses = new HashSet<>();
    private LinkedList<Action<BusTicket>> auditLog = new LinkedList<>();

    @Override
    public BusSolution createBus(int capacity) {
            BusSolution bus = new BusSolution(capacity);
            buses.add(bus);
            return bus;
    }

    @Override
    public TicketSolution issueTicket(int id) {

        lock.lock();
        try
        {
            return new TicketSolution(this.lock);//sending the current lock to new Ticket
        }
        finally{
            lock.unlock();
        }
    }
    @Override
    public boolean boardBus(BusSolution bus, Set<TicketSolution> tickets) {
            lock.lock();
            try{
                if (!bus.hasCapacity(tickets.size())) {
                    return false;
                }
                for(TicketSolution ticket : tickets) {
                    if(ticket.status!=Ticket.Status.ISSUED)
                        return false;
                }

                bus.tickets.addAll(tickets);

                for(TicketSolution ticket : tickets) {
                    ticket.status=Ticket.Status.IN_CIRCULATION;
                    BusTicket busTicket = new BusTicket(bus, ticket);
                    auditLog.addLast(new Action<>(Action.Direction.IN_CIRCULATION,busTicket));
                }
                return true;
            }
            finally {
                lock.unlock();
            }
    }

    @Override
    public boolean transferTickets(BusSolution from, BusSolution to, Set<TicketSolution> tickets) {

            lock.lock();
            try{
                if(!to.hasCapacity(tickets.size()) || !from.tickets.containsAll(tickets)){
                    return false;
                }
                from.tickets.removeAll(tickets);
                to.tickets.addAll(tickets);

                for(TicketSolution ticket : tickets) {
                    BusTicket busTicket = new BusTicket(from, ticket);
                    auditLog.addLast(new Action<>(Action.Direction.MOVED_OUT, busTicket));
                }
                for(TicketSolution ticket : tickets) {
                    BusTicket busTicket = new BusTicket(to, ticket);
                    auditLog.addLast(new Action<>(Action.Direction.MOVED_IN,busTicket));
                }

                return true;
            }
            finally {
                lock.unlock();
            }
    }

    @Override
    public boolean useTickets(BusSolution bus, Set<TicketSolution> tickets) {
        lock.lock();
        try{
            if(!bus.tickets.containsAll(tickets)){return false;}
            bus.tickets.removeAll(tickets);
            for(TicketSolution ticket : tickets) {
                ticket.status=Ticket.Status.USED;
                BusTicket busTicket = new BusTicket(bus, ticket);
                auditLog.addLast(new Action<>(Action.Direction.USED,busTicket));
            }
            return true;
        }
        finally {lock.unlock();}
    }

    @Override
    public boolean expireTickets(BusSolution bus, Set<TicketSolution> tickets) {
        lock.lock();
        try{
            if(!bus.tickets.containsAll(tickets)){return false;}
            bus.tickets.removeAll(tickets);
            for(TicketSolution ticket : tickets) {
                ticket.status=Ticket.Status.EXPIRED;
                BusTicket busTicket = new BusTicket(bus, ticket);
                auditLog.addLast(new Action<>(Action.Direction.EXPIRED,busTicket));
            }
            return true;
        }
        finally {lock.unlock();}
    }

    @Override
    public Set<TicketSolution> getTickets() {
        lock.lock();
        try{
            Set<TicketSolution> result=new HashSet<TicketSolution>();
            for(BusSolution bus:buses){
                result.addAll(bus.tickets);
            }
            return result;
        }
        finally {lock.unlock();}
    }

    @Override
    public  Set<TicketSolution> getTickets(BusSolution bus) {
            lock.lock();
            try{
                return new HashSet<>(bus.tickets);
            }
            finally {
                lock.unlock();
            }
    }

    @Override
    public List<Action<TicketSolution>> audit(BusSolution bus) {
        LinkedList<Action<TicketSolution>> result=new LinkedList<>();
        for(Action<BusTicket> a:auditLog) {
            if(a.get().bus == bus)
                result.addLast(new Action<>(a.getDirection(),a.get().ticket));
        }

        return result;
    }

    @Override
    public List<Action<BusSolution>> audit(TicketSolution ticket) {
        LinkedList<Action<BusSolution>> result = new LinkedList<>();
        for (Action<BusTicket> a : auditLog) {
            if (a.get().ticket == ticket)
                result.addLast(new Action<>(a.getDirection(), a.get().bus));
        }
        return result;
    }
}
