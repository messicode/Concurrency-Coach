package edu.uic.cs454.s25.a1.solution;

import edu.uic.cs454.s25.a1.Action;
import edu.uic.cs454.s25.a1.Ticket;

import java.util.ArrayList;
import java.util.List;


public class TicketSolution implements Ticket {
    //Constructor/get/set
    private final int id; //see if needed
    private final List<Action<TicketSolution>> audit;
    private Status status;
    private BusSolution currentBus;
    private final List<BusSolution> busHistory;

    public TicketSolution(int id) {
        this.id = id;
        this.audit = new ArrayList<>();
        this.status = Status.ISSUED;
        this.currentBus=null;
        this.busHistory = new ArrayList<>();
    }

    @Override
    public Status getStatus() {
         return status;
    }
    public void setStatus(Status status) {
        this.status = status;
        addAudit(new Action<>(Action.Direction.valueOf(status.name()), this));
    }

    public void setCurrentBus(BusSolution bus) {
        this.currentBus = bus;
        if (bus != null && (busHistory.isEmpty() || busHistory.get(busHistory.size() - 1) != bus)) {
            busHistory.add(bus);
        }
    }


    public BusSolution getCurrentBus() {
        return currentBus;
    }

    public void addAudit(Action<TicketSolution> action) {
        audit.add(action);
    }

    public List<Action<TicketSolution>> getAudit() {
        return audit;
    }
    public List<BusSolution> getBusHistory() {
        return new ArrayList<>(busHistory); // Return a copy to maintain encapsulation
    }
}
