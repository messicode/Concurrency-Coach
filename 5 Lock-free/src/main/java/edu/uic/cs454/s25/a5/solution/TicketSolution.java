package edu.uic.cs454.s25.a5.solution;

import edu.uic.cs454.s25.a5.Action;
import edu.uic.cs454.s25.a5.Ticket;

import java.util.concurrent.atomic.AtomicReference;

public class TicketSolution implements Ticket {
    /*default*/ AtomicReference<Status> status=new AtomicReference<>(Status.ISSUED);
    private final int id;
    /*default*/ DepotSolution depotSolution;
    public TicketSolution(int id, DepotSolution depotSolution) {this.id=id;this.depotSolution=depotSolution;}

    @Override
    public Status getStatus() {

        for(DepotSolution.Node curr=depotSolution.tail.get();curr.action!=null;curr=curr.prev){
            Action<BusTicket> action = curr.action;
            if(action.get().ticket==this){
                switch(action.getDirection()){
                    case IN_CIRCULATION:
                    case MOVED_IN:
                        return Status.IN_CIRCULATION;
                    case USED:
                        return Status.USED;
                    case EXPIRED:
                        return Status.EXPIRED;
                    case MOVED_OUT:
                        break;
                }
            }
        }
        return Status.ISSUED;
//        return status.get();
    }

}
