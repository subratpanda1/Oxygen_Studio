package com.subrat.Oxygen.objects.drawableObject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.MathUtils;

import java.util.ArrayList;

/**
 * Created by subrat.panda on 18/12/15.
 */
public abstract class DrawableObject {
    public abstract boolean draw(Canvas canvas);

    private int objectId;
    public int getObjectId() { return objectId; }
    public void setObjectId(int id) { objectId = id; }

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
            waterPainter.setStrokeWidth(MathUtils.getPixelFromMeter(Configuration.PARTICLE_RADIUS / 4));
        }
        return waterPainter;
    }

    public static void drawParticles(Canvas canvas) {
        float[] points = new float[2 * particleList.size()];
        int i = 0;
        for (DrawableCircle drawableCircle : particleList) {
            points[i++] = MathUtils.getPixelFromMeter(drawableCircle.getCenter().x);
            points[i++] = MathUtils.getPixelFromMeter(drawableCircle.getCenter().y);
        }

        canvas.drawPoints(points, getWaterPainter());
        // canvas.drawVertices(Canvas.VertexMode.TRIANGLES, points.length, points, 0, null, 0, null, 0, null, 0, 0, getWaterPainter());
    }
}
