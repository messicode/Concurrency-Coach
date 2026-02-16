package edu.uic.cs454.s25.a5.solution;

import edu.uic.cs454.s25.a5.Action;
import edu.uic.cs454.s25.a5.Depot;
import edu.uic.cs454.s25.a5.Ticket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

//Restoration Point

public class DepotSolution extends Depot<BusSolution, TicketSolution> {

    //Make this universal lock-free with consensus object
    //private LinkedList<Action<BusTicket>> log = new LinkedList<Action<BusTicket>>();

    /*default*/ final AtomicReference<Node> tail = new AtomicReference<>(new Node(null, null));
//    /*default*/ final AtomicReference<Set<BusSolution>> buses = new AtomicReference<>(Collections.emptySet());

    //My List
    /*default*/ static class Node {
        Action<BusTicket> action;
        Node prev;

        Node(Action<BusTicket> action, Node prev) {
            this.action = action;
            this.prev = prev;
        }
    }

//    private boolean append(Node node) {
//        Node last = tail.get();
//        if () {
//            return true;
//        }
//        return false;
//    }

    //    private Set<BusTicket> currContents(BusSolution bus){
    //
    //    }

    @Override
    public BusSolution createBus(int capacity) {
        return new BusSolution(capacity);
//        bus.lastSeen.set(tail.get());
//        while(true){
//        Set<BusSolution> old =buses.get();
//        Set<BusSolution> upd = new HashSet<>(old);
//        upd.add(bus);
//        if(buses.compareAndSet(old, upd))break;
//        }
//        return bus;
    }

    @Override
    public TicketSolution issueTicket(int id) {
        return new TicketSolution(id, this);
    }

    @Override
    public boolean boardBus(BusSolution bus, Set<TicketSolution> tickets) {
        while(true){

            for (TicketSolution t : tickets) {
                if (t.getStatus() != Ticket.Status.ISSUED) {
                    return false;
                }
            }


            Node old = tail.get();

            Node seen=bus.lastSeen.get();
            ArrayList<Node> diff= new ArrayList<>();

            for(Node n=old;n != null && n != seen;n=n.prev){
                if(n.action!=null)diff.add(n);
            }


            HashSet<TicketSolution> curr = new HashSet<>(bus.getContents());
            for (int i = diff.size() - 1; i >= 0; i--) {
                Action<BusTicket> a = diff.get(i).action;
                if (a.get().bus == bus) {
                    TicketSolution t = a.get().ticket;
                    switch (a.getDirection()) {
                        case MOVED_IN:
                        case IN_CIRCULATION:
                            curr.add(t);
                            break;
                        default:
                            curr.remove(t);

                    }
                }
            }
            bus.setContents(curr);
            bus.lastSeen.set(old);
            //Validate
            if (curr.size() + tickets.size() > bus.getCapacity()) {
                return false;
            }


//            HashSet<TicketSolution> visited = new HashSet<>();
//            HashSet<TicketSolution> contents = new HashSet<>();
//            HashSet<TicketSolution> check = new HashSet<>(tickets);
//            int cnt=0;
//            for (Node curr = old; curr.action!= null; curr = curr.prev) {
//                Action<BusTicket> a = curr.action;
//                TicketSolution t=a.get().ticket;
//
//                if(!visited.add(t))continue;//ticket already seen
//
//                if(a.get().bus == bus){
//                    switch (a.getDirection()) {
//                        case MOVED_IN:
//                        case IN_CIRCULATION:
//                            cnt++;
//                            break;
//                        default:
//                            cnt--;
//
//                    }
//                }
//
//            }



//        for (TicketSolution t : tickets){
//            Ticket.Status status = Ticket.Status.ISSUED;
//            for (Node curr=tail.get();curr.action!=null; curr=curr.prev) {
//                Action<BusTicket> a=curr.action;
//                if (a.get().ticket != t) continue;
//                switch (a.getDirection()){
//                    case IN_CIRCULATION:
//                    case MOVED_IN:
//                        status = Ticket.Status.IN_CIRCULATION;
//                        break;
//                    case USED:
//                        status = Ticket.Status.USED;
//                        break;
//                    case EXPIRED:
//                        status = Ticket.Status.EXPIRED;
//                        break;
//                    case MOVED_OUT:
//                        break;
//                    default:
//                        throw new Error("Should be dead here");
//                }
//                break;
//            }
//            if (status != Ticket.Status.ISSUED) {
//                return false;
//            }
//        }


            //Make the changes atomically append to list
            Node batchTail=old;
            for(TicketSolution t : tickets){
                batchTail=new Node(new Action<>(Action.Direction.IN_CIRCULATION, new BusTicket(bus, t)),batchTail);
            }

            if(!tail.compareAndSet(old, batchTail)) continue;

            Set<TicketSolution> next = new HashSet<>(curr);
            next.addAll(tickets);
            bus.setContents(next);
            bus.lastSeen.set(batchTail);
            return true;
        }

    }

    @Override
    public boolean transferTickets(BusSolution from, BusSolution to, Set<TicketSolution> tickets) {
        while(true){

            Node old = tail.get();
            Node seenFrom = from.lastSeen.get();
            Node seenTo = to.lastSeen.get();

            HashSet<TicketSolution> currFrom= new HashSet<>(from.getContents());
            HashSet<TicketSolution> currTo= new HashSet<>(to.getContents());

            boolean flagFrom=false, flagTo = false;

            for(Node n=old; n != null && !(flagFrom && flagTo);n=n.prev){
                if(n==seenFrom){flagFrom=true;}
                if(n==seenTo){flagTo=true;}

                if(n.action==null)continue;

                Action<BusTicket> a=n.action;
                BusTicket bt= a.get();

                switch (a.getDirection()) {
                    case IN_CIRCULATION:
                    case MOVED_IN:
                        if (bt.bus == from) currFrom.add(bt.ticket);
                        if (bt.bus == to) currTo.add(bt.ticket);
                        break;
                    case MOVED_OUT:
                    case USED:
                    case EXPIRED:
                        if (bt.bus == from) currFrom.remove(bt.ticket);
                        if (bt.bus == to) currTo.remove(bt.ticket);
                        break;
                    default:
                        throw new Error("Should be dead here");
                }

            }
            from.setContents(currFrom);
            to.setContents(currTo);
            from.lastSeen.set(old);
            to.lastSeen.set(old);


            if(!currFrom.containsAll(tickets)) return false;
            if(currTo.size() + tickets.size() > to.getCapacity()) return false;


//            HashSet<TicketSolution> fromContents = new HashSet<>();
//            int toCount=0;
//            HashSet<TicketSolution> visited = new HashSet<>();
//
//            for (Node curr = old; curr.action != null; curr = curr.prev) {
//                Action<BusTicket> a = curr.action;
//                BusTicket bt = a.get();
//
//                if (!visited.add(a.get().ticket)) continue;
//                switch (a.getDirection()) {
//                    case IN_CIRCULATION:
//                    case MOVED_IN:
//                        if (bt.bus == from) fromContents.add(bt.ticket);
//                        if (bt.bus == to) toCount++;
//                        break;
//                    case MOVED_OUT:
//                    case USED:
//                    case EXPIRED:
//                        if (bt.bus == from) fromContents.remove(bt.ticket);
////                        if (bt.bus == to) toCount--;
//                        break;
//                    default:
//                        throw new Error("Should be dead here");
//                }
//            }




            // Apply changes
            {

                Node batchTail=old;
                for(TicketSolution t : tickets){
                    batchTail=new Node(new Action<>(Action.Direction.MOVED_OUT, new BusTicket(from, t)),batchTail);
                    batchTail=new Node(new Action<>(Action.Direction.MOVED_IN, new BusTicket(to, t)),batchTail);

                }

                if(!tail.compareAndSet(old, batchTail)) continue;

                Set<TicketSolution> newFrom = new HashSet<>(from.getContents());
                newFrom.removeAll(tickets);
                from.setContents(newFrom);
                from.lastSeen.set(batchTail);

                Set<TicketSolution> newTo = new HashSet<>(to.getContents());
                newTo.addAll(tickets);
                to.setContents(newTo);
                to.lastSeen.set(batchTail);

                return true;
            }
        }
    }

    @Override
    public boolean useTickets(BusSolution bus, Set<TicketSolution> tickets) {

        while(true){

//            for(TicketSolution t : tickets){
//                if(t.getStatus() != Ticket.Status.IN_CIRCULATION){
//                    return false;
//                }
//            }
            Node old = tail.get();
            Node seen= bus.lastSeen.get();
            ArrayList<Node> diff= new ArrayList<>();

            for(Node n=old;n != null && n != seen ;n=n.prev){
                if(n.action!=null)diff.add(n);
            }


            HashSet<TicketSolution> curr = new HashSet<>(bus.getContents());
            for (int i = diff.size() - 1; i >= 0; i--) {
                Action<BusTicket> a = diff.get(i).action;
                if (a.get().bus == bus) {
                    TicketSolution t = a.get().ticket;
                    switch (a.getDirection()) {
                        case MOVED_IN:
                        case IN_CIRCULATION:
                            curr.add(t);
                            break;
                        default:
                            curr.remove(t);

                    }
                }
            }
            bus.setContents(curr);
            bus.lastSeen.set(old);
//            HashSet<TicketSolution> contents = new HashSet<>();
//            HashSet<TicketSolution> visited = new HashSet<>();
//            HashSet<TicketSolution> check = new HashSet<>(tickets);


//            for (Node curr = tail.get(); curr.action != null; curr = curr.prev) {
//                Action<BusTicket> a = curr.action;
//
//                if (!visited.add(a.get().ticket)) continue;

//                if (a.get().bus == bus) {
//                    switch (a.getDirection()) {
//                        case IN_CIRCULATION:
//                        case MOVED_IN:
//                            check.remove(a.get().ticket);
//                            break;
//                        default:
//                            return false;
//                    }
//                }


//                switch (a.getDirection()) {
//                    case MOVED_IN:
//                    case IN_CIRCULATION:
//                        if (a.get().bus == bus) contents.add(a.get().ticket);
//                        break;
//                    case MOVED_OUT:
//                    case USED:
//                    case EXPIRED:
//                        contents.remove(a.get().ticket);
//                        break;
//                }
//                if(check.contains(a.get().ticket)){
//                    if(contents.contains(a.get().ticket)){
//                        check.remove(a.get().ticket);
//                    }
//                    else{
//                        return false;
//                    }
//                }

//            }

            // Validate


            if (!curr.containsAll(tickets)) {
                return false;
            }


            {

                Node batchTail = old;
                for (TicketSolution t : tickets) {
                    batchTail = new Node(new Action<>(Action.Direction.USED, new BusTicket(bus, t)), batchTail);
                }

                if (!tail.compareAndSet(old, batchTail)) continue;

                Set<TicketSolution> upd = new HashSet<>(curr);
                upd.removeAll(tickets);
                bus.setContents(upd);
                bus.lastSeen.set(batchTail);

                return true;
            }

        }
    }

    @Override
    public boolean expireTickets(BusSolution bus, Set<TicketSolution> tickets) {
        while(true){

            for(TicketSolution t : tickets){
                if(t.getStatus() != Ticket.Status.IN_CIRCULATION){
                    return false;
                }
            }

            Node old = tail.get();
            Node seen= bus.lastSeen.get();
            ArrayList<Node> diff= new ArrayList<>();

            for(Node n=old;n != null && n != seen;n=n.prev){
                if( n.action!=null)diff.add(n);
            }


            HashSet<TicketSolution> curr = new HashSet<>(bus.getContents());
            for (int i = diff.size() - 1; i >= 0; i--) {
                Action<BusTicket> a = diff.get(i).action;
                if (a.get().bus == bus) {
                    TicketSolution t = a.get().ticket;
                    switch (a.getDirection()) {
                        case MOVED_IN:
                        case IN_CIRCULATION:
                            curr.add(t);
                            break;
                        default:
                            curr.remove(t);

                    }
                }
            }
            bus.setContents(curr);
            bus.lastSeen.set(old);

            if (!curr.containsAll(tickets)) {return false;}


            // Apply changes

            {

                Node batchTail = old;
                for (TicketSolution t : tickets) {
                    batchTail = new Node(new Action<>(Action.Direction.EXPIRED, new BusTicket(bus, t)), batchTail);

                }

                if (!tail.compareAndSet(old, batchTail)) continue;

                Set<TicketSolution> upd = new HashSet<>(curr);
                upd.removeAll(tickets);
                bus.contents.set(upd);
                bus.lastSeen.set(batchTail);

                return true;
            }

        }
    }

    @Override
    public Set<TicketSolution> getTickets() {
        HashSet<TicketSolution> result = new HashSet<>();
        HashSet<TicketSolution> visited = new HashSet<>();

        for (Node curr = tail.get(); curr.action != null; curr = curr.prev) {
            Action<BusTicket> a = curr.action;
            if (!visited.add(a.get().ticket)) continue;
            switch (a.getDirection()) {
                case MOVED_IN:
                case IN_CIRCULATION:
                    result.add(a.get().ticket);
                    break;
                default:
            }
        }

        return result;
    }

    @Override
    public Set<TicketSolution> getTickets(BusSolution bus) {

        HashSet<TicketSolution> result = new HashSet();
        HashSet<TicketSolution> visited = new HashSet();
        for (Node curr = tail.get(); curr.action != null; curr = curr.prev) {
            Action<BusTicket> a = curr.action;
            if (!visited.add(a.get().ticket)) continue;
            if (a.get().bus != bus) continue;
            switch (a.getDirection()) {
                case MOVED_IN:
                case IN_CIRCULATION:
                    result.add(a.get().ticket);
                    break;
            }

        }
        return result;
    }
//    Point 2
}
