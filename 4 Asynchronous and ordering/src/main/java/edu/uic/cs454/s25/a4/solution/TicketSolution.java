package edu.uic.cs454.s25.a4.solution;

import edu.uic.cs454.s25.a4.Ticket;

import java.util.concurrent.atomic.AtomicReference;

public class TicketSolution implements Ticket {
    /*default*/ volatile Status status= Status.ISSUED;


    @Override
    public Status getStatus() {
        return status;
    }
}
