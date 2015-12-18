package com.subrat.Oxygen.physics.object;

import android.graphics.PointF;

import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.interfaces.LineInterface;
import com.subrat.Oxygen.physics.PhysicsManager;
import com.subrat.Oxygen.utilities.Configuration;

/**
 * Created by subrat.panda on 18/12/15.
 */
public class PhysicsLine extends PhysicsObject implements LineInterface {
    private PointF start;
    public PointF getStart() { return start; }
    public void setStart(PointF point) { start = point; }

    private PointF end;
    public PointF getEnd() { return end; }
    public void setEnd(PointF point) { end = point; }

    private void setEndPoints(PointF start, PointF end) {
        this.start = start; this.end = end;
    }

    // Only to be called from HadaPhysicsEngine
    public PhysicsLine(PointF start, PointF end) {
        setEndPoints(start, end);
    }

    public float getMass() { return 0; }
    public void setMass(float mass) {  }

    public PointF getPosition() { return null; }
    public void setPosition(PointF position) { }

    public PointF getVelocity() { return null; }
    public void setVelocity(PointF velocity) { }

    public PointF getAcceleration() { return null; }
    public void setAcceleration(PointF acceleration) { }

    public int getRotation() { return 0; }
    public void setRotation(int rotation) { }

    public void editLine(PointF start, PointF end) {
        setEndPoints(start, end);

        PhysicsManager.getPhysicsManager().editLine(this);
    }

    public void updatePosition() {

    }
}
