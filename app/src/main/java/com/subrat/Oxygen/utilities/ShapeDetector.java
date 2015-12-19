package com.subrat.Oxygen.utilities;

import android.graphics.PointF;

import com.subrat.Oxygen.physics.engines.HadaPhysicsEngine;
import com.subrat.Oxygen.physics.PhysicsManager;
import com.subrat.Oxygen.physics.PhysicsObjectBuilder;
import com.subrat.Oxygen.physics.object.PhysicsCircle;
import com.subrat.Oxygen.physics.object.PhysicsLine;
import com.subrat.Oxygen.physics.object.PhysicsObject;

import java.util.ArrayList;

/**
 * Created by subrat.panda on 19/12/15.
 */
public class ShapeDetector {
    private static ShapeDetector shapeDetector = null;

    public static ShapeDetector getShapeDetector() {
        if (shapeDetector == null) shapeDetector = new ShapeDetector();
        return shapeDetector;
    }

    public boolean detectLine(ArrayList<PointF> points) {
        if (points.size() < Configuration.LINE_MIN_PIXELS) return false;

        PointF start = points.get(0);
        PointF end = points.get(points.size() - 1);

        float lineLength = MathUtils.getMathUtils().getDistance(start, end);
        if (lineLength < Configuration.LINE_MIN_LENGTH) return false;

        // Check if bounding box is thin enough to be approximated by a line
        PhysicsLine line = PhysicsObjectBuilder.getPhysicsObjectBuilder().constructPhysicsLine(start, end);
        ArrayList<Float> distanceList = new ArrayList<Float>();
        for (PointF point : points) {
            distanceList.add(MathUtils.getMathUtils().getDistance(point, line));
        }

        float meanDistance = MathUtils.getMathUtils().getMean(distanceList);
        if ((lineLength / meanDistance) < Configuration.LINE_DEVIATION_THRESHOLD) return false;

        return true;
    }

    public static boolean detectCircle(ArrayList<PointF> points) {
        if (points.size() < Configuration.CIRCLE_MIN_PIXELS) return false;

        // Check if standard deviation of all points from center is low
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

        float meanRadius = MathUtils.getMathUtils().getMean(radiusList);
        if (meanRadius < Configuration.CIRCLE_MIN_RADIUS) return false;
        float standardDeviation = MathUtils.getMathUtils().getStandardDeviation(radiusList, meanRadius);
        if (standardDeviation > Configuration.CIRCLE_DEVIATION_THRESHOLD) return false;

        // Do not create if overlapping with other circles
        PhysicsCircle physicsCircle = PhysicsObjectBuilder.getPhysicsObjectBuilder().constructPhysicsCircle(points);
        for (PhysicsObject object : PhysicsManager.getPhysicsManager().getObjectList()) {
            if (HadaPhysicsEngine.getHadaPhysicsEngine().checkOverlap(physicsCircle, object)) {
                return false;
            }
        }

        return true;
    }
}
