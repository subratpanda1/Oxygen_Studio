package com.subrat.Oxygen.utilities;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by subrat.panda on 20/12/15.
 */
public class Statistics {
    private static Statistics statistics = null;
    public static Statistics getStatistics() {
        if (statistics == null) statistics = new Statistics();
        return statistics;
    }

    private int testRenders;
    private AtomicInteger numRenders;
    private AtomicInteger numPhysicsUpdates;

    private Statistics() {
        numRenders = new AtomicInteger(0);
        numPhysicsUpdates = new AtomicInteger(0);
        testRenders = 0;
    }

    public long getNumPhysicsUpdates() {
        return numPhysicsUpdates.get();
    }

    public void incrementNumPhysicsUpdates() {
        numPhysicsUpdates.incrementAndGet();
    }

    public long getNumRenders() {
        return numRenders.get();
    }

    public void incrementNumRenders() {
        numRenders.incrementAndGet();
        ++testRenders;
    }

    public void resetStatistics() {
        numPhysicsUpdates.set(0);
        numRenders.set(0);
    }
}
