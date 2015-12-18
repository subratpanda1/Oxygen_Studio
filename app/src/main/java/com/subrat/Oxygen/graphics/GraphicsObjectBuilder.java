package com.subrat.Oxygen.graphics;

import com.subrat.Oxygen.graphics.object.DrawableCircle;
import com.subrat.Oxygen.graphics.object.DrawableLine;
import com.subrat.Oxygen.graphics.object.DrawableObject;
import com.subrat.Oxygen.physics.PhysicsObjectBuilder;
import com.subrat.Oxygen.physics.object.PhysicsCircle;
import com.subrat.Oxygen.physics.object.PhysicsLine;
import com.subrat.Oxygen.physics.object.PhysicsObject;

/**
 * Created by subrat.panda on 19/12/15.
 */
public class GraphicsObjectBuilder {
    private static GraphicsObjectBuilder graphicsObjectBuilder = null;
    public static GraphicsObjectBuilder getGraphicsObjectBuilder() {
        if (graphicsObjectBuilder == null) graphicsObjectBuilder = new GraphicsObjectBuilder();
        return graphicsObjectBuilder;
    }

    private int objectIdCounter = 0;
    public int getNextObjectId() { return objectIdCounter++; }

    public DrawableObject buildObject(PhysicsObject physicsObject) {
        if (physicsObject instanceof PhysicsCircle) {
            PhysicsCircle physicsCircle = (PhysicsCircle)physicsObject;
            return createDrawableCircle(physicsCircle);
        } else if (physicsObject instanceof PhysicsLine) {
            PhysicsLine physicsLine = (PhysicsLine)physicsObject;
            return createDrawableLine(physicsLine);
        }

        return null;
    }

    public DrawableCircle createDrawableCircle(PhysicsCircle physicsCircle) {
        DrawableCircle drawableCircle = new DrawableCircle(physicsCircle.getCenter(), physicsCircle.getRadius(), physicsCircle.getRotation());
        drawableCircle.setObjectId(getNextObjectId());
        HadaGraphicsEngine.getHadaGraphicsEngine().getObjectList().add(drawableCircle);
        return drawableCircle;
    }

    public DrawableLine createDrawableLine(PhysicsLine physicsLine) {
        DrawableLine drawableLine = new DrawableLine(physicsLine.getStart(), physicsLine.getEnd());
        drawableLine.setObjectId(getNextObjectId());
        HadaGraphicsEngine.getHadaGraphicsEngine().getObjectList().add(drawableLine);
        return drawableLine;
    }
}
