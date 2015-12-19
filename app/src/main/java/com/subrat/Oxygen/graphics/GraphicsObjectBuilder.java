package com.subrat.Oxygen.graphics;

import android.graphics.PointF;

import com.subrat.Oxygen.graphics.object.DrawableCircle;
import com.subrat.Oxygen.graphics.object.DrawableLine;
import com.subrat.Oxygen.graphics.object.DrawableObject;
import com.subrat.Oxygen.physics.PhysicsObjectBuilder;
import com.subrat.Oxygen.physics.object.PhysicsCircle;
import com.subrat.Oxygen.physics.object.PhysicsLine;
import com.subrat.Oxygen.physics.object.PhysicsObject;
import com.subrat.Oxygen.utilities.MathUtils;

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

    private DrawableCircle createDrawableCircle(PhysicsCircle physicsCircle) {
        PointF drawableCenter = MathUtils.getMathUtils().getPixelBasedPointFromMeterBasedPoint(physicsCircle.getCenter());
        float drawableRadius = MathUtils.getMathUtils().getPixelFromMeter(physicsCircle.getRadius());
        int rotation = physicsCircle.getRotation();
        DrawableCircle drawableCircle = new DrawableCircle(drawableCenter, drawableRadius, rotation);
        drawableCircle.setObjectId(getNextObjectId());
        return drawableCircle;
    }

    private DrawableLine createDrawableLine(PhysicsLine physicsLine) {
        PointF drawableStart = MathUtils.getMathUtils().getPixelBasedPointFromMeterBasedPoint(physicsLine.getStart());
        PointF drawableEnd = MathUtils.getMathUtils().getPixelBasedPointFromMeterBasedPoint(physicsLine.getEnd());
        DrawableLine drawableLine = new DrawableLine(drawableStart, drawableEnd);
        drawableLine.setObjectId(getNextObjectId());
        return drawableLine;
    }
}
