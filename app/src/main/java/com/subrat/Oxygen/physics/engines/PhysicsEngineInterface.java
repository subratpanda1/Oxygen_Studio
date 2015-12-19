package com.subrat.Oxygen.physics.engines;

import android.graphics.PointF;

import com.subrat.Oxygen.physics.object.PhysicsObject;

import java.util.ArrayList;

/**
 * Created by subrat.panda on 19/12/15.
 */
public interface PhysicsEngineInterface {
    void initWorld();
    void clearWorld();
    void stepWorld(float stepTimeInSecond);
    void setGravity(PointF point);

    void createObjectInWorld(PhysicsObject object);
    void deleteObjectFromWorld(PhysicsObject object);
    void updatePhysicsObjectFromWorldObject(PhysicsObject object);
    void updateWorldObjectFromPhysicsObject(PhysicsObject object);
    void updateAllPhysicsObjectsFromWorld(ArrayList<PhysicsObject> objects);
}
