package com.subrat.Oxygen.hadaGraphicsEngine;

import android.graphics.PointF;

import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.hadaPhysicsEngine.HadaPhysicsEngine;
import com.subrat.Oxygen.objects.ObjectMapper;
import com.subrat.Oxygen.objects.abstractObject.ObjectBuilder;
import com.subrat.Oxygen.objects.drawableObject.DrawableCircle;
import com.subrat.Oxygen.objects.drawableObject.DrawableLine;
import com.subrat.Oxygen.objects.drawableObject.DrawableObject;
import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.MathUtils;

import java.util.ArrayList;

/**
 * Created by subrat.panda on 18/12/15.
 */
public class HadaGraphicsEngine {
    private static HadaGraphicsEngine hadaGraphicsEngine = null;

    public static HadaGraphicsEngine getHadaGraphicsEngine() {
        if (hadaGraphicsEngine == null) hadaGraphicsEngine = new HadaGraphicsEngine();
        return hadaGraphicsEngine;
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
            radiusList.add(MathUtils.getDistance(center, point));
        }

        float meanRadius = MathUtils.getMean(radiusList);
        if (meanRadius < Configuration.CIRCLE_MIN_RADIUS) return false;
        float standardDeviation = MathUtils.getStandardDeviation(radiusList, meanRadius);
        if (standardDeviation > Configuration.CIRCLE_DEVIATION_THRESHOLD) return false;

        // Do not create if overlapping with other circles
        DrawableCircle drawableCircle = HadaGraphicsEngine.getHadaGraphicsEngine().constructDrawableCircle(points);
        for (DrawableObject object : DrawableObject.getObjectList()) {
            if (HadaPhysicsEngine.getHadaPhysicsEngine().checkOverlap(ObjectMapper.getObjectMapper().getPhysicsObjectFromDrawableObject(drawableCircle),
                    ObjectMapper.getObjectMapper().getPhysicsObjectFromDrawableObject(object))) {
                return false;
            }
        }

        return true;
    }

    public DrawableCircle constructDrawableCircle(ArrayList<PointF> points) {
        float avgx = 0, avgy = 0;
        for (PointF point : points) {
            avgx += point.x;
            avgy += point.y;
        }

        avgx /= points.size();
        avgy /= points.size();

        PointF center = new PointF(avgx, avgy);
        // float radius = Configuration.getCircleRadius();

        ArrayList<Float> radiusList = new ArrayList<Float>();
        for (PointF point: points) {
            radiusList.add(MathUtils.getDistance(center, point));
        }

        float radius = MathUtils.getMean(radiusList);

        DrawableCircle drawableCircle = new DrawableCircle(center, radius, 0);
        return drawableCircle;

    }

    public DrawableCircle createDrawableCircle(ArrayList<PointF> points) {
        DrawableCircle drawableCircle = constructDrawableCircle(points);
        drawableCircle.setObjectId(ObjectBuilder.getNextObjectId());
        DrawableObject.getObjectList().add(drawableCircle);

        HadaPhysicsEngine.getHadaPhysicsEngine().createPhysicsCircle(drawableCircle.getCenter(), drawableCircle.getRadius(), drawableCircle.getRotation());

        return drawableCircle;
    }

    public boolean detectLine(ArrayList<PointF> points) {
        if (points.size() < Configuration.LINE_MIN_PIXELS) return false;

        PointF start = points.get(0);
        PointF end = points.get(points.size() - 1);

        float lineLength = MathUtils.getDistance(start, end);
        if (lineLength < Configuration.LINE_MIN_LENGTH) return false;

        // Check if bounding box is thin enough to be approximated by a line
        DrawableLine line = constructDrawableLine(start, end);
        ArrayList<Float> distanceList = new ArrayList<Float>();
        for (PointF point : points) {
            distanceList.add(MathUtils.getDistance(point, line));
        }

        float meanDistance = MathUtils.getMean(distanceList);
        if ((lineLength / meanDistance) < Configuration.LINE_DEVIATION_THRESHOLD) return false;

        return true;
    }

    public DrawableLine constructDrawableLine(ArrayList<PointF> points) {
        DrawableLine line = new DrawableLine(points.get(0), points.get(points.size() - 1));
        return line;
    }

    public DrawableLine constructDrawableLine(PointF start, PointF end) {
        DrawableLine line = new DrawableLine(start, end);
        return line;
    }

    public DrawableLine createDrawableLine(ArrayList<PointF> points) {
        DrawableLine line = constructDrawableLine(points);
        line.setObjectId(ObjectBuilder.getNextObjectId());
        DrawableObject.getObjectList().add(line);

        HadaPhysicsEngine.getHadaPhysicsEngine().createPhysicsLine(line.getStart(), line.getEnd());

        return line;
    }
}
