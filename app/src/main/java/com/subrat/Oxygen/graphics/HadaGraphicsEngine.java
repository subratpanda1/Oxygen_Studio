package com.subrat.Oxygen.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.subrat.Oxygen.graphics.object.DrawableCircle;
import com.subrat.Oxygen.graphics.object.DrawableObject;
import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.MathUtils;

import java.util.ArrayList;

/**
 * Created by subrat.panda on 18/12/15.
 */
public class HadaGraphicsEngine {
    private static ArrayList<DrawableObject> objectList = new ArrayList<DrawableObject>();
    public static ArrayList<DrawableObject> getObjectList() { return objectList; }

    private static ArrayList<DrawableCircle> particleList = new ArrayList<DrawableCircle>();
    public static ArrayList<DrawableCircle> getParticleList() { return particleList; }

    private static Paint waterPainter = null;

    protected static Paint getWaterPainter() {
        if (waterPainter == null) {
            waterPainter = new Paint();
            waterPainter.setColor(Color.CYAN);
            waterPainter.setStyle(Paint.Style.STROKE);
            waterPainter.setStrokeWidth(MathUtils.getMathUtils().getPixelFromMeter(Configuration.PARTICLE_RADIUS / 4));
        }
        return waterPainter;
    }

    private static HadaGraphicsEngine hadaGraphicsEngine = null;

    public static HadaGraphicsEngine getHadaGraphicsEngine() {
        if (hadaGraphicsEngine == null) hadaGraphicsEngine = new HadaGraphicsEngine();
        return hadaGraphicsEngine;
    }

    public static void drawParticles(Canvas canvas) {
        float[] points = new float[2 * particleList.size()];
        int i = 0;
        for (DrawableCircle drawableCircle : particleList) {
            points[i++] = MathUtils.getMathUtils().getPixelFromMeter(drawableCircle.getCenter().x);
            points[i++] = MathUtils.getMathUtils().getPixelFromMeter(drawableCircle.getCenter().y);
        }

        canvas.drawPoints(points, getWaterPainter());
        // canvas.drawVertices(Canvas.VertexMode.TRIANGLES, points.length, points, 0, null, 0, null, 0, null, 0, 0, getWaterPainter());
    }
}
