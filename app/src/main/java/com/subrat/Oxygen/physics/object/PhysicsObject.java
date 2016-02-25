package com.subrat.Oxygen.physics.object;

import android.graphics.PointF;

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

    private int engineRefId;

    public int getEngineRefId() {
        return engineRefId;
    }

    public void setEngineRefId(int engineRefId) {
        this.engineRefId = engineRefId;
    }

    public abstract void updatePosition();


}
