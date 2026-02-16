package edu.uic.cs454.s25.a4.solution;

import edu.uic.cs454.s25.a4.Action;
import edu.uic.cs454.s25.a4.Depot;
import edu.uic.cs454.s25.a4.Result;

import java.util.HashSet;
import java.util.Set;

public class DepotSolution extends Depot<BusSolution,TicketSolution> {

    private final Set<BusSolution> buses = new HashSet<>();

    @Override
    public synchronized BusSolution createBus(int capacity) {

        BusSolution ret = new BusSolution(capacity);
        buses.add(ret);
        return ret;
    }

    @Override
    public synchronized TicketSolution issueTicket(int id) {
        return new TicketSolution();
    }

    @Override
    public boolean boardBus(BusSolution bus, Set<TicketSolution> tickets) {
        return boardBusAsync(bus,tickets).getResult();
    }

    @Override
    public boolean transferTickets(BusSolution from, BusSolution to, Set<TicketSolution> tickets) {
        ResultSolution<Boolean> removeRes = new ResultSolution<>();
        from.submitAction(new Action<>(Action.Direction.MOVE_OUT, tickets, removeRes));
        if (!removeRes.getResult()) return false;

        ResultSolution<Boolean> addRes = new ResultSolution<>();
        to.submitAction(new Action<>(Action.Direction.MOVE_IN, tickets, addRes));
        if (addRes.getResult()) return true;

        while(true){
            ResultSolution<Boolean> r = new ResultSolution<>();
            from.submitAction(new Action<>(Action.Direction.MOVE_IN, tickets, r));
            if (r.getResult()) return false;

            ResultSolution<Boolean> r1 = new ResultSolution<>();
            from.submitAction(new Action<>(Action.Direction.MOVE_IN, tickets, r1));
            if (r1.getResult()) return true;
        }
    }

    @Override
    public boolean useTickets(BusSolution bus, Set<TicketSolution> tickets) {
        return useTicketsAsync(bus,tickets).getResult();
    }

    @Override
    public boolean expireTickets(BusSolution bus, Set<TicketSolution> tickets) {
        return expireTicketsAsync(bus,tickets).getResult();
    }

    @Override
    public synchronized Set<TicketSolution> getTickets() {
        Set<TicketSolution> ret = new HashSet<>();

        for(BusSolution bus : buses) {
            ret.addAll(getTickets(bus));
        }
        return ret;
    }

    @Override
    public synchronized Set<TicketSolution> getTickets(BusSolution bus) {
        return getTicketsAsync(bus).getResult();
    }

    @Override
    public Result<Boolean> boardBusAsync(BusSolution bus, Set<TicketSolution> tickets) {
        ResultSolution<Boolean> res = new ResultSolution<>();
        bus.submitAction(new Action<>(Action.Direction.ADD, tickets, res));
        return res;
    }

    @Override
    public Result<Boolean> transferTicketsAsync(BusSolution from, BusSolution to, Set<TicketSolution> tickets) {


        ResultSolution<Boolean> removeRes = new ResultSolution<>();
        from.submitAction(new Action<>(Action.Direction.MOVE_OUT, tickets, removeRes));


        ResultSolution<Boolean> addRes = new ResultSolution<>();
        to.submitAction(new Action<>(Action.Direction.MOVE_IN, tickets, addRes));

        return new Result<Boolean>() {
            @Override
            public void setResult(Boolean result) {
                throw new Error("No calls here");
            }

            @Override
            public Boolean getResult() {
                if(removeRes.getResult() && addRes.getResult()) return true;
                if(!removeRes.getResult() && !addRes.getResult()) return false;

                if(removeRes.getResult() && !addRes.getResult()) {
                    while(true){
                        ResultSolution<Boolean> addBack = new ResultSolution<>();
                        from.submitAction(new Action<>(Action.Direction.MOVE_IN, tickets, addBack));

                        if(addBack.getResult()==true)return false;

                        ResultSolution<Boolean> addTo = new ResultSolution<>();
                        to.submitAction(new Action<>(Action.Direction.MOVE_IN, tickets, addTo));

                        if(addTo.getResult()==true)return true;
                    }
                }

                if(!removeRes.getResult() && addRes.getResult()) {
                    while(true){
                        ResultSolution<Boolean> removeTo = new ResultSolution<>();
                        to.submitAction(new Action<>(Action.Direction.MOVE_OUT, tickets, removeTo));

                        if(removeTo.getResult()==true)return false;

                        ResultSolution<Boolean> removeFrom = new ResultSolution<>();
                        from.submitAction(new Action<>(Action.Direction.MOVE_OUT, tickets, removeFrom));

                        if(removeFrom.getResult()==true)return true;
                    }

                }
            throw new Error("Dead code");
            }
        };
    }

    @Override
    public Result<Boolean> useTicketsAsync(BusSolution bus, Set<TicketSolution> tickets) {
        ResultSolution<Boolean> res = new ResultSolution<>();
        bus.submitAction(new Action<>(Action.Direction.USED, tickets, res));
        return res;
    }

    @Override
    public Result<Boolean> expireTicketsAsync(BusSolution bus, Set<TicketSolution> tickets) {
        ResultSolution<Boolean> res = new ResultSolution<>();
        bus.submitAction(new Action<>(Action.Direction.EXPIRED, tickets, res));
        return res;
    }

    @Override
    public Result<Set<TicketSolution>> getTicketsAsync() {
        Set<Result<Set<TicketSolution>>> res = new HashSet<>();

        for(BusSolution bus : buses) {
            ResultSolution<Set<TicketSolution>> r = new ResultSolution<>();
            bus.submitAction(new Action<>(Action.Direction.CONTENTS, null, r));
            res.add(r);
        }
        return new Result<Set<TicketSolution>>() {
            @Override
            public void setResult(Set<TicketSolution> result) {
                throw new Error("Not needed");
            }

            @Override
            public Set<TicketSolution> getResult () {
                Set<TicketSolution> ret = new HashSet<>();
                for(Result<Set<TicketSolution>> r : res) {
                    ret.addAll(r.getResult());
                }
                return ret;
            }
        };
    }

    @Override
    public Result<Set<TicketSolution>> getTicketsAsync(BusSolution bus) {
        ResultSolution<Set<TicketSolution>> res = new ResultSolution<>();
        bus.submitAction(new Action<>(Action.Direction.CONTENTS, null, res));
        return res;
    }
}
