package com.subrat.Oxygen.physics.object;

import android.graphics.PointF;

import com.subrat.Oxygen.interfaces.CircleInterface;
import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.MathUtils;

/**
 * Created by subrat.panda on 18/12/15.
 */
public class PhysicsCircle extends PhysicsObject implements CircleInterface {
    private PointF center; // In mtr
    public PointF getCenter() { return center; }
    public void setCenter(PointF point) { center = point; }

    public PointF getPosition() { return center; }
    public void setPosition(PointF point) { center = point; }

    private float radius; // In mtr
    public float getRadius() { return radius; }
    public void setRadius(float radius) { this.radius = radius; }

    private int rotation; // In degree
    public int getRotation() { return rotation; }
    public void setRotation(int deg) { rotation = deg; }

    private boolean isParticle = false;

    private PointF velocity; // In mtr per sec
    public PointF getVelocity() { return velocity; }
    public void setVelocity(PointF point) { velocity = point; }

    private PointF acceleration;
    public PointF getAcceleration() { return acceleration; }
    public void setAcceleration(PointF point) { acceleration = point; }

    private static PointF gravity = new PointF(0, 0); // In mtr per sec per sec
    public static PointF getGravity() { return gravity; }
    public static void setGravity(PointF pointf) { gravity = pointf; }

    public float getMass() {
        return (float) (3.14F * Math.pow(getRadius(), 2) * Configuration.CIRCLE_DENSITY);
    }

    public void setMass(float mass) {  }

    public PhysicsCircle(PointF center, float radius, int rotation) {
        this.center = center;
        this.radius = radius;
        this.rotation = 0;
        this.isParticle = false;

        initVelocity();
    }

    public PhysicsCircle(PointF center, float radius, int rotation, boolean isParticle) {
        this.center = center;
        this.radius = radius;
        this.rotation = 0;
        this.isParticle = isParticle;

        if (isParticle != true) {
            initVelocity();
        }
    }

    public void initVelocity() {
        velocity = new PointF();
        velocity.x = 0;
        velocity.y = 0;
    }

    public void initRandomVelocity() {
        velocity = new PointF();
        velocity.x = MathUtils.getRandom(-Configuration.MAX_VELOCITY, Configuration.MAX_VELOCITY);
        velocity.y = (float) (Math.sqrt(Math.pow(Configuration.MAX_VELOCITY, 2) - Math.pow(velocity.x, 2)) * MathUtils.getRandomSign());
    }

    public boolean isStill() {
        if (velocity.x == 0 && velocity.y == 0) return true;
        return false;
    }
    public void updatePosition() {
        // Don't change velocity if acceleration is very low
        // if (MathUtils.getAbsolute(this.getGravity()) > Configuration.getMinGravity()) {
        PointF velocityChange = MathUtils.scalePoint(getGravity(), Configuration.REFRESH_INTERVAL);
        MathUtils.addToPoint(velocity, velocityChange);
        // }

        // Don't change position if velocity is very low
        // if (MathUtils.getAbsolute(this.getVelocity()) > Configuration.getMinVelocity()) {
        PointF positionChange = MathUtils.scalePoint(getVelocity(), Configuration.REFRESH_INTERVAL);
        MathUtils.addToPoint(center, positionChange);
        // }
    }
}
