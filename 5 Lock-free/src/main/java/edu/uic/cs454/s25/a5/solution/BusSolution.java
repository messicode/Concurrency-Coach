package edu.uic.cs454.s25.a5.solution;

import edu.uic.cs454.s25.a5.Bus;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class BusSolution implements Bus {
    private final int capacity;
    final AtomicReference<Set<TicketSolution>> contents=new AtomicReference<>(new HashSet<>());;
    final AtomicReference<DepotSolution.Node> lastSeen = new AtomicReference<>(null);

    public BusSolution(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {return capacity;}


    public void setContents(Set<TicketSolution> contents) {
        this.contents.set(contents);
    }
    public Set<TicketSolution> getContents() {return contents.get();}
}

