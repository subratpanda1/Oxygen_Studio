package com.subrat.Oxygen.graphics;

import android.graphics.PointF;

import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.graphics.object.DrawableLine;
import com.subrat.Oxygen.graphics.object.DrawableObject;
import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.MathUtils;

import java.util.ArrayList;

/**
 * Created by subrat.panda on 07/05/15.
 */
public class ObjectBuilder {
    private static int objectIdCounter = 0;
    public static int getNextObjectId() { return objectIdCounter++; }

    public static DrawableObject buildObject(ArrayList<PointF> points) {
    	MathUtils.transformToMeterBasedPoints(points);
        if (HadaGraphicsEngine.getHadaGraphicsEngine().detectLine(points)) {
            return HadaGraphicsEngine.getHadaGraphicsEngine().createDrawableLine(points);
        } 
        
        else if (HadaGraphicsEngine.getHadaGraphicsEngine().detectCircle(points)) {
            return HadaGraphicsEngine.getHadaGraphicsEngine().createDrawableCircle(points);
        }

        return null;
    }

    public static void createOrUpdateBoundaryLines() {
    	if (OxygenActivity.getContext() == null) return;
        float canvasMargin = Configuration.CANVAS_MARGIN;
        PointF topLeft = new PointF(canvasMargin, canvasMargin);
        PointF topRight = new PointF(OxygenActivity.getWorldWidth() - canvasMargin, canvasMargin);
        PointF bottomLeft = new PointF(canvasMargin, OxygenActivity.getWorldHeight() - canvasMargin);
        PointF bottomRight = new PointF(OxygenActivity.getWorldWidth() - canvasMargin, OxygenActivity.getWorldHeight() - canvasMargin);

        if (HadaGraphicsEngine.getHadaGraphicsEngine().getObjectList().isEmpty()) {
            HadaGraphicsEngine.getHadaGraphicsEngine().createDrawableLine(topLeft, topRight);
            HadaGraphicsEngine.getHadaGraphicsEngine().createDrawableLine(bottomLeft, bottomRight);
            HadaGraphicsEngine.getHadaGraphicsEngine().createDrawableLine(topLeft, bottomLeft);
            HadaGraphicsEngine.getHadaGraphicsEngine().createDrawableLine(topRight, bottomRight);
        } else {
            ((DrawableLine) HadaGraphicsEngine.getHadaGraphicsEngine().getObjectList().get(0)).editLine(topLeft, topRight);
            ((DrawableLine) HadaGraphicsEngine.getHadaGraphicsEngine().getObjectList().get(1)).editLine(bottomLeft, bottomRight);
            ((DrawableLine) HadaGraphicsEngine.getHadaGraphicsEngine().getObjectList().get(2)).editLine(topLeft, bottomLeft);
            ((DrawableLine) HadaGraphicsEngine.getHadaGraphicsEngine().getObjectList().get(3)).editLine(topRight, bottomRight);
        }
    }
}
