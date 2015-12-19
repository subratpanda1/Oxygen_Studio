package com.subrat.Oxygen.graphics;

import com.subrat.Oxygen.graphics.object.DrawableCircle;
import com.subrat.Oxygen.graphics.object.DrawableObject;
import com.subrat.Oxygen.physics.PhysicsManager;
import com.subrat.Oxygen.physics.object.PhysicsObject;

import java.util.ArrayList;

/**
 * Created by subrat.panda on 19/12/15.
 */
public class FrameBuffer {
    private static FrameBuffer frameBuffer;

    public static FrameBuffer getFrameBuffer() {
        if (frameBuffer == null) frameBuffer = new FrameBuffer();
        return frameBuffer;
    }

    private ArrayList<DrawableObject> bufferObjectList = new ArrayList<DrawableObject>();
    private ArrayList<DrawableCircle> bufferParticleList = new ArrayList<DrawableCircle>();

    public void copyPhysicsObjectsToFrameBuffer() {
        ArrayList<PhysicsObject> physicsObjects = PhysicsManager.getPhysicsManager().getObjectList();

        // TODO: Lock and do the copy
    }

    public void copyFrameBUfferToDrawable() {
        ArrayList<DrawableObject> drawableObjects = HadaGraphicsEngine.getHadaGraphicsEngine().getObjectList();

        // TODO: Lock and do the copy
    }
}
