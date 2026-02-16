package edu.uic.cs454.s25.a2.solution;


import edu.uic.cs454.s25.a2.CS454Lock;
import edu.uic.cs454.s25.a2.Ticket;



public class TicketSolution implements Ticket {
    //Constructor/get
    /*default*/ Status status=Status.ISSUED;
    /*default*/ DepotSolution depotSolution;
    private CS454Lock lock;
    //Updated the lock to be from depot and not a new lock
    public TicketSolution(CS454Lock lock) {
        this.lock=lock;
    }

    @Override
    public Status getStatus() {
         lock.lock();
          try{
             return status;
         }
          finally {lock.unlock();}
    }

}
