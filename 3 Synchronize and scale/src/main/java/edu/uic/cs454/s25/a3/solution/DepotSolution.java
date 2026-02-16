package edu.uic.cs454.s25.a3.solution;

import edu.uic.cs454.s25.a3.Depot;
import edu.uic.cs454.s25.a3.Ticket;

import java.util.*;
import java.util.concurrent.locks.Lock;

public class DepotSolution extends Depot<BusSolution,TicketSolution> {

//    private final Object lock = new Object();
    private Set<BusSolution> buses = new HashSet<>();
//    private LinkedList<Action<BusTicket>> auditLog = new LinkedList<>();

    @Override
    public BusSolution createBus(int capacity) {
            BusSolution bus = new BusSolution(capacity);
            buses.add(bus);
            return bus;
    }

    @Override
    public TicketSolution issueTicket(int id) {
            return new TicketSolution(id);
    }
    @Override
    public boolean boardBus(BusSolution bus, Set<TicketSolution> tickets) {
            Lock busLock=bus.getBusLock();
            busLock.lock();
            try{
                List<TicketSolution> ticketList = new ArrayList<>(tickets);
                ticketList.sort(Comparator.comparingInt(TicketSolution::getId)); // Sort tickets by their IDs
                for (TicketSolution ticket : ticketList) {
                    Lock ticketLock = ticket.getLock();
                    ticketLock.lock();
                }

                if (!bus.hasCapacity(tickets.size())) {
                    return false;
                }
                for(TicketSolution ticket : tickets) {
                        if(ticket.getStatus()!=Ticket.Status.ISSUED) return false;
                }

                bus.tickets.addAll(tickets);

                for(TicketSolution ticket : tickets) {
                    ticket.setStatus(Ticket.Status.IN_CIRCULATION);
                }

                return true;
            }
            finally {
                for (TicketSolution ticket : tickets) {
                    ticket.getLock().unlock();
                }
                busLock.unlock();
            }
    }

    @Override
    public boolean transferTickets(BusSolution from, BusSolution to, Set<TicketSolution> tickets) {
        Lock fromLock=from.getBusLock();
        Lock toLock=to.getBusLock();
        if (from.getId() > to.getId()) {
            fromLock.lock();
            toLock.lock();
        } else {
            toLock.lock();
            fromLock.lock();
        }
        try{
            //Need 'if' to ensure atomicity
            //Should i lock tickets too?
            List<TicketSolution> ticketList = new ArrayList<>(tickets);
            ticketList.sort(Comparator.comparingInt(TicketSolution::getId)); // Sort tickets by ID

            for (TicketSolution ticket : ticketList) {
                Lock ticketLock = ticket.getLock();
                ticketLock.lock();
            }

            if(!to.hasCapacity(tickets.size()) || !from.tickets.containsAll(tickets)){
                return false;
            }
            from.tickets.removeAll(tickets);//ticket locking?
            to.tickets.addAll(tickets);
            return true;
        }
        finally {
            for (TicketSolution ticket : tickets) {
                ticket.getLock().unlock();
            }
            fromLock.unlock();
            toLock.unlock();
        }

    }

    @Override
    public boolean useTickets(BusSolution bus, Set<TicketSolution> tickets) {
        Lock busLock = bus.getBusLock();
        busLock.lock();
        try{
            List<TicketSolution> ticketList = new ArrayList<>(tickets);
            ticketList.sort(Comparator.comparingInt(TicketSolution::getId));
            for (TicketSolution ticket : ticketList) {
                Lock ticketLock = ticket.getLock();
                ticketLock.lock(); // Lock each ticket in order
            }

            if(!bus.tickets.containsAll(tickets)){return false;}

            for (TicketSolution ticket : tickets) {
                    if (ticket.getStatus() != Ticket.Status.IN_CIRCULATION)
                        return false;
                    ticket.setStatus(Ticket.Status.USED);
            }
            bus.removeTickets(tickets);
            return true;
        }
        finally {
            for (TicketSolution ticket : tickets) {
                ticket.getLock().unlock();
            }
            busLock.unlock();
        }
    }

    @Override
    public boolean expireTickets(BusSolution bus, Set<TicketSolution> tickets) {
        Lock busLock = bus.getBusLock();
        busLock.lock();
        try{
            List<TicketSolution> ticketList = new ArrayList<>(tickets);
            ticketList.sort(Comparator.comparingInt(TicketSolution::getId));
            for (TicketSolution ticket : ticketList) {
                Lock ticketLock = ticket.getLock();
                ticketLock.lock(); // Lock each ticket in order
            }

            if(!bus.tickets.containsAll(tickets)){return false;}
            bus.tickets.removeAll(tickets);
            for(TicketSolution ticket : tickets) {
                ticket.setStatus(Ticket.Status.EXPIRED);
//            BusTicket busTicket = new BusTicket(bus, ticket);
//            auditLog.addLast(new Action<>(Action.Direction.EXPIRED,busTicket));
            }
            return true;

        }
        finally {
            for (TicketSolution ticket : tickets) {
                ticket.getLock().unlock();
            }
            busLock.unlock();
        }

    }

    @Override
    public Set<TicketSolution> getTickets() {
        List<BusSolution> busList = new ArrayList<>(buses);
        return new HashSet<>(getTickets(busList));

//        Set<TicketSolution> result=new HashSet<TicketSolution>();
//            for(BusSolution bus:buses){
//                Lock busLock = bus.getBusLock();
//                busLock.lock();
//                try{
//                    result.addAll(bus.tickets);
//            }
//                finally {busLock.unlock();}
//
//        }
//        return result;
    }

    @Override
    public Set<TicketSolution> getTickets(BusSolution bus) {
        Lock busLock = bus.busLock.readLock();
        busLock.lock(); // Lock the bus

        try {
            return new HashSet<>(bus.tickets);
        } finally {
            busLock.unlock(); // Unlock bus after fetching tickets
        }

    }

    @Override
    public Set<TicketSolution> getTickets(List<BusSolution> buses) {
        Set<TicketSolution> result=new HashSet<>();
        LinkedList<BusSolution> busList = new LinkedList<>(buses);

        busList.sort(Comparator.comparingLong(BusSolution::getId)); // Ensure buses are sorted by ID

        List<Lock> locks = new ArrayList<>();
        for (BusSolution bus : busList) {
            Lock busLock = bus.busLock.readLock();
            busLock.lock();
            locks.add(busLock);
        }

        try{
            for(BusSolution bus:buses)
                result.addAll(bus.tickets);
            return result;
        }
        finally{
            for(Lock lock:locks){
                lock.unlock();
            }
        }

    }

//    @Override
//    public List<Action<TicketSolution>> audit(BusSolution bus) {
//        LinkedList<Action<TicketSolution>> result=new LinkedList<>();
//        for(Action<BusTicket> a:auditLog) {
//            if(a.get().bus == bus)
//                result.addLast(new Action<>(a.getDirection(),a.get().ticket));
//        }
//
//        return result;
//    }
//
//    @Override
//    public List<Action<BusSolution>> audit(TicketSolution ticket) {
//        LinkedList<Action<BusSolution>> result = new LinkedList<>();
//        for (Action<BusTicket> a : auditLog) {
//            if (a.get().ticket == ticket)
//                result.addLast(new Action<>(a.getDirection(), a.get().bus));
//        }
//        return result;
//    }
}
