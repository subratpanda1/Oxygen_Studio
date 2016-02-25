package com.subrat.Oxygen.utilities;

import android.util.Log;

import java.util.Date;
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

    private AtomicInteger numRenders;
    private AtomicInteger numPhysicsUpdates;

    private AtomicInteger renderFps;
    private AtomicInteger physicsFps;

    private Date prevRenderTime;
    private Date prevPhysicsTime;

    private Statistics() {
        numRenders = new AtomicInteger(0);
        numPhysicsUpdates = new AtomicInteger(0);
        renderFps = new AtomicInteger(0);
        physicsFps = new AtomicInteger(0);
    }

    public int getNumPhysicsUpdates() {
        return numPhysicsUpdates.get();
    }

    public void incrementNumPhysicsUpdates() {
        Date currentDate = new Date();
        numPhysicsUpdates.incrementAndGet();
        if (prevPhysicsTime != null) {
            final int refreshMsec = 1000 / Configuration.PHYSICS_FPS;
            long timeDiff = currentDate.getTime() - prevPhysicsTime.getTime();
            /*
            if (timeDiff < refreshMsec) {
                Log.i("Subrat", "Here: Physics update in " + timeDiff + " msecs");
            }
            */
            if (timeDiff > refreshMsec) {
                physicsFps.set((int) (1000 / timeDiff));
            }
        }
        prevPhysicsTime = currentDate;
    }

    public int getPhysicsFps() {
        return physicsFps.get();
    }

    public int getNumRenders() {
        return numRenders.get();
    }

    public void incrementNumRenders() {
        Date currentDate = new Date();
        numRenders.incrementAndGet();
        if (prevRenderTime != null) {
            final int refreshMsec = 1000 / Configuration.GRAPHICS_FPS;
            long timeDiff = currentDate.getTime() - prevRenderTime.getTime();
            /*
            if (timeDiff < refreshMsec) {
                Log.i("Subrat", "Here: Render update in " + timeDiff + " msecs");
            }
            */
            if (timeDiff > refreshMsec) {
                renderFps.set((int) (1000 / timeDiff));
            }
        }
        prevRenderTime = currentDate;
    }

    public int getRenderFps() {
        return renderFps.get();
    }

    public void resetStatistics() {
        numPhysicsUpdates.set(0);
        numRenders.set(0);
        renderFps.set(0);
        physicsFps.set(0);
        prevRenderTime = null;
        prevPhysicsTime = null;
    }
}
