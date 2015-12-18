package com.subrat.Oxygen.objects.physicsObject;

import android.graphics.PointF;

import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.hadaPhysicsEngine.HadaPhysicsEngine;
import com.subrat.Oxygen.objects.abstractObject.Force;
import com.subrat.Oxygen.objects.drawableObject.DrawableCircle;
import com.subrat.Oxygen.utilities.Configuration;

import java.util.ArrayList;

/**
 * Created by subrat.panda on 18/12/15.
 */
public abstract class PhysicsObject {
    public abstract float getMass();
    public abstract void setMass(float mass);

    public abstract PointF getPosition();
    public abstract void setPosition(PointF point);

    public abstract PointF getVelocity();
    public abstract void setVelocity(PointF point);

    public abstract PointF getAcceleration();
    public abstract void setAcceleration(PointF point);

    public abstract int getRotation();
    public abstract void setRotation(int rotation);

    private int objectId;
    public int getObjectId() { return objectId; }
    public void setObjectId(int id) { objectId = id; }

    protected ArrayList<Force> forceList = new ArrayList<Force>();

    private static ArrayList<PhysicsObject> objectList = new ArrayList<>();
    public static ArrayList<PhysicsObject> getObjectList() { return objectList; }

    private static ArrayList<DrawableCircle> particleList = new ArrayList<>();
    public static ArrayList<DrawableCircle> getParticleList() { return particleList; }

    public abstract void updatePosition();

    public static void updateAllObjects() {
        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
            if (OxygenActivity.getPhysicsEngine() != null) {
                OxygenActivity.getPhysicsEngine().stepWorld();
                for (PhysicsObject object : PhysicsObject.getObjectList()) {
                    if (object instanceof PhysicsCircle) {
                        OxygenActivity.getPhysicsEngine().updateCircle((PhysicsCircle) object);
                    } else if (object instanceof PhysicsLine) {
                        OxygenActivity.getPhysicsEngine().updateLine((PhysicsLine) object);
                    }
                }

                OxygenActivity.getPhysicsEngine().updateParticles(particleList);
            }
        } else {
            try {
                for (PhysicsObject object1 : PhysicsObject.getObjectList()) {
                    for (PhysicsObject object2 : PhysicsObject.getObjectList()) {
                        if (object2.getObjectId() > object1.getObjectId()) {
                            if (HadaPhysicsEngine.getHadaPhysicsEngine().checkCollision(object1, object2)) {
                                HadaPhysicsEngine.getHadaPhysicsEngine().updateCollision(object1, object2);
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }

            for (PhysicsObject object : PhysicsObject.getObjectList()) {
                object.updatePosition();
            }
        }
    }

    public static void resetVelocities() {
        for (PhysicsObject object : PhysicsObject.getObjectList()) {
            if (object instanceof PhysicsCircle) {
                PhysicsCircle physicsCircle = (PhysicsCircle) object;
                physicsCircle.initRandomVelocity();
            }
        }
    }
}
