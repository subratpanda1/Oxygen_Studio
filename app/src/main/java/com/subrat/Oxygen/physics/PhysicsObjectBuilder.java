package com.subrat.Oxygen.physics;

import android.graphics.PointF;

import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.physics.object.PhysicsCircle;
import com.subrat.Oxygen.physics.object.PhysicsLine;
import com.subrat.Oxygen.physics.object.PhysicsObject;
import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.MathUtils;
import com.subrat.Oxygen.utilities.ShapeDetector;

import java.util.ArrayList;

/**
 * Created by subrat.panda on 07/05/15.
 */
public class PhysicsObjectBuilder {
    private static PhysicsObjectBuilder physicsObjectBuilder = null;
    public static PhysicsObjectBuilder getPhysicsObjectBuilder() {
        if (physicsObjectBuilder == null) physicsObjectBuilder = new PhysicsObjectBuilder();
        return physicsObjectBuilder;
    }

    private int objectIdCounter = 0;
    public int getNextObjectId() { return objectIdCounter++; }

    public PhysicsObject buildObject(ArrayList<PointF> points) {
    	MathUtils.getMathUtils().transformToMeterBasedPoints(points);
        if (ShapeDetector.getShapeDetector().detectLine(points)) {
            return createPhysicsLine(points);
        } else if (ShapeDetector.getShapeDetector().detectCircle(points)) {
            return createPhysicsCircle(points);
        }

        return null;
    }

    public void createOrUpdateBoundaryLines() {
    	if (OxygenActivity.getContext() == null) return;
        float canvasMargin = Configuration.CANVAS_MARGIN;
        PointF topLeft = new PointF(canvasMargin, canvasMargin);
        PointF topRight = new PointF(OxygenActivity.getWorldWidth() - canvasMargin, canvasMargin);
        PointF bottomLeft = new PointF(canvasMargin, OxygenActivity.getWorldHeight() - canvasMargin);
        PointF bottomRight = new PointF(OxygenActivity.getWorldWidth() - canvasMargin, OxygenActivity.getWorldHeight() - canvasMargin);

        if (PhysicsManager.getPhysicsManager().getObjectList().isEmpty()) {
            createPhysicsLine(topLeft, topRight);
            createPhysicsLine(bottomLeft, bottomRight);
            createPhysicsLine(topLeft, bottomLeft);
            createPhysicsLine(topRight, bottomRight);
            // createPhysicsLine(new PointF(0, 0), new PointF(3, 0));
            // createPhysicsCircle(new PointF(1.5F, 3.5F), 0.3F, 0);
            createPhysicsCircle(new PointF(1.5F, 4.5F), 0.3F, 0);
        } else {
            ((PhysicsLine) PhysicsManager.getPhysicsManager().getObjectList().get(0)).editLine(topLeft, topRight);
            ((PhysicsLine) PhysicsManager.getPhysicsManager().getObjectList().get(1)).editLine(bottomLeft, bottomRight);
            ((PhysicsLine) PhysicsManager.getPhysicsManager().getObjectList().get(2)).editLine(topLeft, bottomLeft);
            ((PhysicsLine) PhysicsManager.getPhysicsManager().getObjectList().get(3)).editLine(topRight, bottomRight);
        }
    }

    public PhysicsCircle constructPhysicsCircle(PointF center, float radius, int rotation) {
        PhysicsCircle circle = new PhysicsCircle(center, radius, rotation);
        return circle;
    }

    public PhysicsCircle constructPhysicsCircle(ArrayList<PointF> points) {
        float avgx = 0, avgy = 0;
        for (PointF point : points) {
            avgx += point.x;
            avgy += point.y;
        }

        avgx /= points.size();
        avgy /= points.size();

        PointF center = new PointF(avgx, avgy);

        ArrayList<Float> radiusList = new ArrayList<Float>();
        for (PointF point: points) {
            radiusList.add(MathUtils.getMathUtils().getDistance(center, point));
        }

        float radius = MathUtils.getMathUtils().getMean(radiusList);

        PhysicsCircle circle = new PhysicsCircle(center, radius, 0);
        return circle;
    }

    public PhysicsLine constructPhysicsLine(PointF start, PointF end) {
        PhysicsLine line = new PhysicsLine(start, end);
        return line;
    }

    public PhysicsLine constructPhysicsLine(ArrayList<PointF> points) {
        PhysicsLine line = constructPhysicsLine(points.get(0), points.get(points.size() - 1));
        return line;
    }

    public PhysicsCircle createPhysicsCircle(PointF center, float radius, int rotation) {
        PhysicsCircle circle = constructPhysicsCircle(center, radius, rotation);
        circle.setObjectId(getNextObjectId());
        PhysicsManager.getPhysicsManager().getObjectList().add(circle);

        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
            PhysicsManager.getPhysicsManager().getLiquidFunEngine().createObjectInWorld(circle);
        }

        return circle;
    }

    public PhysicsCircle createPhysicsCircle(ArrayList<PointF> points) {
        PhysicsCircle circle = constructPhysicsCircle(points);
        circle.setObjectId(getNextObjectId());
        PhysicsManager.getPhysicsManager().getObjectList().add(circle);

        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
            PhysicsManager.getPhysicsManager().getLiquidFunEngine().createObjectInWorld(circle);
        }

        return circle;
    }

    public PhysicsLine createPhysicsLine(PointF start, PointF end) {
        PhysicsLine line = constructPhysicsLine(start, end);
        line.setObjectId(getNextObjectId());
        PhysicsManager.getPhysicsManager().getObjectList().add(line);

        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
            PhysicsManager.getPhysicsManager().getLiquidFunEngine().createObjectInWorld(line);
        }

        return line;
    }

    public PhysicsLine createPhysicsLine(ArrayList<PointF> points) {
        PhysicsLine line = constructPhysicsLine(points);
        line.setObjectId(getNextObjectId());
        PhysicsManager.getPhysicsManager().getObjectList().add(line);

        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
            PhysicsManager.getPhysicsManager().getLiquidFunEngine().createObjectInWorld(line);
        }

        return line;
    }
}
