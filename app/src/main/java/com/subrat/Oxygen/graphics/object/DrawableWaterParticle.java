package com.subrat.Oxygen.graphics.object;

import android.graphics.Canvas;
import android.graphics.PointF;

import com.subrat.Oxygen.graphics.HadaGraphicsEngine;

/**
 * Created by subrat.panda on 20/12/15.
 */
public class DrawableWaterParticle extends DrawableObject {
    private PointF position;

    public PointF getPosition() {
        return position;
    }

    public void setPosition(PointF position) {
        this.position = position;
    }

    public DrawableWaterParticle(PointF position) {
        this.position = position;
    }

    public boolean draw(Canvas canvas) {
        canvas.drawPoint(position.x, position.y, HadaGraphicsEngine.getHadaGraphicsEngine().getWaterPainter());
        // canvas.drawVertices(Canvas.VertexMode.TRIANGLES, points.length, points, 0, null, 0, null, 0, null, 0, 0, getWaterPainter());
        return true;
    }
}
