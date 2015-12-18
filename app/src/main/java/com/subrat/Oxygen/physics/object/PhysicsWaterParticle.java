package com.subrat.Oxygen.physics.object;

import android.graphics.PointF;

import com.subrat.Oxygen.interfaces.ParticleInterface;

/**
 * Created by subrat.panda on 19/12/15.
 */
public class PhysicsWaterParticle extends PhysicsObject implements ParticleInterface {
    private PointF center;
    public PointF getPosition() { return center; }
    public void setPosition(PointF point) { center = point; }

    private PointF acceleration;
    public PointF getAcceleration() { return acceleration; }
    public void setAcceleration(PointF point) { acceleration = point; }

    private int rotation; // In degree
    public int getRotation() { return rotation; }
    public void setRotation(int deg) { rotation = deg; }

    private PointF velocity; // In mtr per sec
    public PointF getVelocity() { return velocity; }
    public void setVelocity(PointF point) { velocity = point; }

    public void updatePosition() { }

    public float getMass() { return 0; }
    public void setMass(float mass) { }
}
