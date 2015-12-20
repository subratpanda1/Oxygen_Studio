package com.subrat.Oxygen.physics;

import android.graphics.PointF;
import android.util.Log;

import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.physics.engines.LiquidFunEngine;
import com.subrat.Oxygen.physics.object.PhysicsCircle;
import com.subrat.Oxygen.physics.object.PhysicsLine;
import com.subrat.Oxygen.physics.object.PhysicsObject;
import com.subrat.Oxygen.physics.object.PhysicsWaterParticle;
import com.subrat.Oxygen.simulation.Simulator;
import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.MathUtils;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by subrat.panda on 19/12/15.
 */
public class PhysicsManager {
    private static PhysicsManager physicsManager = null;
    private LiquidFunEngine liquidFunEngine = null;

    public static PhysicsManager getPhysicsManager() {
        if (physicsManager == null) physicsManager = new PhysicsManager();
        return physicsManager;
    }

    public PhysicsManager() {
        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
            liquidFunEngine = new LiquidFunEngine();
        }
        objectListLock = new ReentrantLock();
        objectList = new ArrayList<>();
    }

    public ReentrantLock objectListLock;
    private ArrayList<PhysicsObject> objectList = null;
    private boolean objectListReadInProgress;
    public void startObjectListAccess() {
        objectListLock.lock();
        objectListReadInProgress = true;
    }
    public void stopObjectListAccess() {
        objectListReadInProgress = false;
        objectListLock.unlock();
    }

    public PhysicsObject getPhysicsObject(int i) {
        if (objectListReadInProgress == false) return null;
        if (objectList.size() <= i) return null;
        return objectList.get(i);
    }

    public int getObjectListSize() {
        if (objectListReadInProgress == false) return 0;
        return objectList.size();
    }

    public void clearObjectList() {
        startObjectListAccess();
        objectList.clear();
        stopObjectListAccess();
    }

    public void addPhysicsObject(PhysicsObject physicsObject) {
        startObjectListAccess();
        objectList.add(physicsObject);
        stopObjectListAccess();
    }

    public LiquidFunEngine getLiquidFunEngine() {
        return liquidFunEngine;
    }

    public void step(float stepDuration) {
        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
            liquidFunEngine.stepWorld(stepDuration);
        }
    }

    public void resetVelocities() {
        startObjectListAccess();
        int size = getObjectListSize();
        for (int i = 0; i < size; ++i) {
            PhysicsObject object = getPhysicsObject(i);
            if (object instanceof PhysicsCircle) {
                PhysicsCircle physicsCircle = (PhysicsCircle) object;
                physicsCircle.initRandomVelocity();
            }
        }
        stopObjectListAccess();
    }

    public void editLine(PhysicsLine line) {
        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
            // liquidFunEngine.editLine(line);
        }
    }

    public void addWater() {
        float shift = MathUtils.getMathUtils().getRandom(OxygenActivity.getWorldWidth() / 5, (OxygenActivity.getWorldWidth() * 3) / 5);
        Simulator.getSimulator().pauseSimulator();
        for (int x = 1; x < 5; ++x) {
            for (int y = 1; y < 5; ++y) {
                float borderDistance = 2 * Configuration.CANVAS_MARGIN + 2 * Configuration.LINE_THICKNESS;
                PointF position = new PointF(borderDistance + shift + ((float) x) / 7,
                        OxygenActivity.getWorldHeight() - borderDistance - ((float) y) / 7);
                PhysicsObjectBuilder.getPhysicsObjectBuilder().createPhysicsWaterParticle(position);
            }
        }
        Simulator.getSimulator().resumeSimulator();
    }

    public void initWorld() {
        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
            liquidFunEngine.initWorld();
        }
    }

    public void clearWorld() {
        clearObjectList();

        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
            liquidFunEngine.clearWorld();
        }
    }

    public void setGravity(PointF gravity) {
        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
            liquidFunEngine.setGravity(gravity);
        }
    }

    public void updateAllObjects() {
        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
            startObjectListAccess();
            int size = getObjectListSize();
            for (int i = 0; i < size; ++i) {
                PhysicsObject physicsObject = getPhysicsObject(i);
                liquidFunEngine.updatePhysicsObjectFromWorldObject(physicsObject);
            }
            stopObjectListAccess();
        }
    }

    public void printAllObjects() {
        int serial = 0;
        String objectId;
        String objectType;
        String objectPosition;
        String objectRotation;
        String printString = "";
        startObjectListAccess();
        int size = getObjectListSize();
        for (int i = 0; i < size; ++i) {
            PhysicsObject object = getPhysicsObject(i);
            ++serial;
            objectId = "" + object.getObjectId();
            if (object instanceof PhysicsCircle) {
                PhysicsCircle physicsCircle = (PhysicsCircle)object;
                objectType = "Circle: ";
                objectPosition = "Center: " + MathUtils.getMathUtils().getPointString(physicsCircle.getCenter());
                objectRotation = "Rotation: " + physicsCircle.getRotation();
                printString = "Physics Sl: " + serial + ", " + "Id: " + objectId + ", " + objectType + ", " + objectPosition + ", " + objectRotation;
            } else if (object instanceof PhysicsLine) {
                PhysicsLine physicsLine = (PhysicsLine)object;
                objectType = "Line: ";
                objectPosition = "Position: Start: " + MathUtils.getMathUtils().getPointString(physicsLine.getStart()) + ", " +
                                           "End: " + MathUtils.getMathUtils().getPointString(physicsLine.getEnd());
                printString = "Physics Sl: " + serial + ", " + "Id: " + objectId + ", " + objectType + ", " + objectPosition;
            } else if (object instanceof PhysicsWaterParticle) {

            }
            Log.i("Subrat", printString);
        }
        stopObjectListAccess();
    }
}
