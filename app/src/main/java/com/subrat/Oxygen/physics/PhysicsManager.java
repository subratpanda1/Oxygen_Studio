package com.subrat.Oxygen.physics;

import android.graphics.PointF;

import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.graphics.object.DrawableCircle;
import com.subrat.Oxygen.physics.object.PhysicsCircle;
import com.subrat.Oxygen.physics.object.PhysicsLine;
import com.subrat.Oxygen.physics.object.PhysicsObject;
import com.subrat.Oxygen.utilities.Configuration;

import java.util.ArrayList;

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
    }

    private ArrayList<PhysicsObject> objectList = new ArrayList<>();
    public ArrayList<PhysicsObject> getObjectList() { return objectList; }

    private ArrayList<DrawableCircle> particleList = new ArrayList<>();
    public ArrayList<DrawableCircle> getParticleList() { return particleList; }

    public LiquidFunEngine getLiquidFunEngine() {
        return liquidFunEngine;
    }

    public void step(float stepDuration) {
        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
            liquidFunEngine.stepWorld(stepDuration);
            for (PhysicsObject object : getObjectList()) {
                if (object instanceof PhysicsCircle) {
                    liquidFunEngine.updateCircle((PhysicsCircle) object);
                } else if (object instanceof PhysicsLine) {
                    liquidFunEngine.updateLine((PhysicsLine) object);
                }
            }

            liquidFunEngine.updateParticles(particleList);
        } else {
            try {
                for (PhysicsObject object1 : getObjectList()) {
                    for (PhysicsObject object2 : getObjectList()) {
                        if (object2.getObjectId() > object1.getObjectId()) {
                            if (HadaPhysicsEngine.getHadaPhysicsEngine().checkCollision(object1, object2)) {
                                HadaPhysicsEngine.getHadaPhysicsEngine().updateCollision(object1, object2);
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }

            for (PhysicsObject object : getObjectList()) {
                object.updatePosition();
            }
        }
    }

    public void resetVelocities() {
        for (PhysicsObject object : getObjectList()) {
            if (object instanceof PhysicsCircle) {
                PhysicsCircle physicsCircle = (PhysicsCircle) object;
                physicsCircle.initRandomVelocity();
            }
        }
    }

    public void editLine(PhysicsLine line) {
        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
            liquidFunEngine.editLine(line);
        }
    }

    public void addWater() {
        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
            liquidFunEngine.addWater();
        }
    }

    public void clearWorld() {
        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
            liquidFunEngine.clearWorld();
        }
    }

    public void setGravity(PointF gravity) {
        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
            liquidFunEngine.setGravity(gravity);
        }
    }
}
