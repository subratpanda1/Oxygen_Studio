package com.subrat.Oxygen.graphics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;

import com.google.fpl.liquidfun.Draw;
import com.subrat.Oxygen.R;
import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.customviews.OxygenView;
import com.subrat.Oxygen.graphics.object.DrawableCircle;
import com.subrat.Oxygen.graphics.object.DrawableObject;
import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.MathUtils;
import com.subrat.Oxygen.utilities.Statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by subrat.panda on 18/12/15.
 */
public class HadaGraphicsEngine {
    private static HadaGraphicsEngine hadaGraphicsEngine = null;

    public static HadaGraphicsEngine getHadaGraphicsEngine() {
        if (hadaGraphicsEngine == null) hadaGraphicsEngine = new HadaGraphicsEngine();
        return hadaGraphicsEngine;
    }

    private Paint waterPainter = null;
    private Paint textPainter = null;

    private Map<Integer, Bitmap> bitmapCache = null;

    // Repeat Task
    private Handler repeatHandler;
    Runnable repeatRunnable;
    boolean repeatTaskRunning;

    private HadaGraphicsEngine() {
        waterPainter = new Paint();
        waterPainter.setColor(Color.CYAN);
        waterPainter.setStyle(Paint.Style.STROKE);
        waterPainter.setStrokeWidth(MathUtils.getMathUtils().getPixelFromMeter(Configuration.PARTICLE_RADIUS / 4));

        textPainter = new Paint();
        textPainter.setColor(Color.WHITE);
        textPainter.setStyle(Paint.Style.FILL);
        textPainter.setTextSize(40);

        bitmapCache = new HashMap<>();
    }

    public Bitmap getScaledBitmap(int radius) {
        if (bitmapCache.containsKey(radius)) {
            return bitmapCache.get(radius);
        } else {
            Bitmap pic = BitmapFactory.decodeResource(OxygenActivity.getContext().getResources(), R.drawable.tennis_ball);
            pic = Bitmap.createScaledBitmap(pic, (int) (2 * radius), (int) (2 * radius), true);
            bitmapCache.put(radius, pic);
            return pic;
        }
    }

    protected Paint getWaterPainter() {
        return waterPainter;
    }

    public void drawObjects(Canvas canvas) {
        FrameBuffer.getFrameBuffer().startFrameBufferRead();
        int size = FrameBuffer.getFrameBuffer().getFrameBufferSize();
        for (int i = 0; i < size; ++i) {
            FrameBuffer.getFrameBuffer().getFrameObject(i).draw(canvas);
        }
        FrameBuffer.getFrameBuffer().stopFrameBufferRead();

        showStatistics(canvas);
    }

    public void drawParticles(Canvas canvas) {
        /*
        float[] points = new float[2 * particleList.size()];
        int i = 0;
        for (DrawableCircle drawableCircle : particleList) {
            points[i++] = MathUtils.getMathUtils().getPixelFromMeter(drawableCircle.getCenter().x);
            points[i++] = MathUtils.getMathUtils().getPixelFromMeter(drawableCircle.getCenter().y);
        }

        canvas.drawPoints(points, getWaterPainter());
        // canvas.drawVertices(Canvas.VertexMode.TRIANGLES, points.length, points, 0, null, 0, null, 0, null, 0, 0, getWaterPainter());
        */
    }

    public void initRenderLoop(final OxygenView oxygenView) {
        repeatHandler = new Handler();
        repeatRunnable = new Runnable() {
            @Override
            public void run() {
                oxygenView.invalidate();
                Statistics.getStatistics().incrementNumRenders();
                repeatHandler.postDelayed(repeatRunnable, (int)Configuration.REFRESH_INTERVAL * 1000/*in msec*/);
            }
        };
    }

    public void startRenderLoop() {
        if (repeatTaskRunning) return;
        repeatRunnable.run();
        repeatTaskRunning = true;
    }

    public void stopRenderLoop() {
        if (!repeatTaskRunning) return;
        repeatHandler.removeCallbacks(repeatRunnable);
        repeatTaskRunning = false;
    }

    public void showStatistics(Canvas canvas) {
        canvas.drawText("Physics: " + Statistics.getStatistics().getNumPhysicsUpdates(), 40, 60, textPainter);
        canvas.drawText("Renders: " + Statistics.getStatistics().getNumRenders(), 40, 90, textPainter);
    }
}
