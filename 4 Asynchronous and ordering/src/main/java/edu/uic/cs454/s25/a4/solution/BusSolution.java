package edu.uic.cs454.s25.a4.solution;

import edu.uic.cs454.s25.a4.Action;
import edu.uic.cs454.s25.a4.Bus;
import edu.uic.cs454.s25.a4.Result;
import edu.uic.cs454.s25.a4.Ticket;

import java.util.*;

public class BusSolution extends Bus<TicketSolution> {

    private final LinkedList<Action> actionQueue= new LinkedList<>();
    private final int capacity;

    public BusSolution(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void submitAction(Action a) {
        synchronized (this) {
            this.actionQueue.addLast(a);
            this.notifyAll();
        }
    }

    @Override
    protected Action getAction() {

        synchronized (this) {
            while (this.actionQueue.isEmpty()) {
                try {
                    this.wait(100L);
                    continue;
                } catch (InterruptedException e) {
                    continue;
                }
            }

            return this.actionQueue.removeFirst();
        }
    }

    @Override
    protected void boardBus(Set<TicketSolution> tickets, Result<Boolean> result) {
            Set<TicketSolution> currentContents = this.getContents();

            if (currentContents.size() + tickets.size() > this.capacity) {
                result.setResult(false);
                return;
            }

            for (TicketSolution ticket : tickets) {
                if (ticket.status!=Ticket.Status.ISSUED) {
                    result.setResult(false);
                    return;
                }
            }

            this.addTickets(tickets);  // use provided superclass method

            for (TicketSolution ticket : tickets) {
                ticket.status = Ticket.Status.IN_CIRCULATION;
            }
            result.setResult(true);
            return;
    }

    @Override
    protected void useTickets(Set<TicketSolution> tickets, Result<Boolean> result) {
            Set<TicketSolution> currentContents = this.getContents();

            if (!currentContents.containsAll(tickets)) {
                result.setResult(false);
                return;
            }


            for (TicketSolution ticket : tickets) {
               ticket.status=Ticket.Status.USED;
            }

            this.removeTickets(tickets);
            result.setResult(true);

    }

    @Override
    protected void expireTickets(Set<TicketSolution> tickets, Result<Boolean> result) {

            Set<TicketSolution> currentContents = this.getContents();

            if (!currentContents.containsAll(tickets)) {
                result.setResult(false);
                return;
            }

            for (TicketSolution ticket : tickets) {
                ticket.status=Ticket.Status.EXPIRED;
                }
            this.removeTickets(tickets);
            result.setResult(true);

    }

    @Override
    protected void contents(Result<Set<TicketSolution>> result) {
            result.setResult(this.getContents());
            return;
    }

    @Override
    protected void moveIn(Set<TicketSolution> tickets, Result<Boolean> result) {
        Set<TicketSolution> currentContents = this.getContents();

        if (currentContents.size() + tickets.size() > this.capacity) {
            result.setResult(false);
            return;
        }


        this.addTickets(tickets);  // use provided superclass method

        result.setResult(true);
        return;
    }

    @Override
    protected void moveOut(Set<TicketSolution> tickets, Result<Boolean> result) {
            Set<TicketSolution> currentContents = this.getContents();

            if (!currentContents.containsAll(tickets)) {
                result.setResult(false);
                return;
            }

            this.removeTickets(tickets);  // provided superclass method
            result.setResult(true);
            return;

    }
}
