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
    private static HadaGraphicsEngine hadaGraphicsEngine = null;

    public static HadaGraphicsEngine getHadaGraphicsEngine() {
        if (hadaGraphicsEngine == null) hadaGraphicsEngine = new HadaGraphicsEngine();
        return hadaGraphicsEngine;
    }

    private ArrayList<DrawableObject> objectList = new ArrayList<DrawableObject>();
    public ArrayList<DrawableObject> getObjectList() { return objectList; }

    private ArrayList<DrawableCircle> particleList = new ArrayList<DrawableCircle>();
    public ArrayList<DrawableCircle> getParticleList() { return particleList; }

    private Paint waterPainter = null;

    public HadaGraphicsEngine() {
        waterPainter = new Paint();
        waterPainter.setColor(Color.CYAN);
        waterPainter.setStyle(Paint.Style.STROKE);
        waterPainter.setStrokeWidth(MathUtils.getMathUtils().getPixelFromMeter(Configuration.PARTICLE_RADIUS / 4));
    }

    protected Paint getWaterPainter() {
        return waterPainter;
    }

    public void drawParticles(Canvas canvas) {
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
