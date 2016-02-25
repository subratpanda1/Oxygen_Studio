package com.subrat.Oxygen.graphics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.util.Log;

import com.google.fpl.liquidfun.Draw;
import com.subrat.Oxygen.R;
import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.customviews.OxygenView;
import com.subrat.Oxygen.graphics.object.DrawableCircle;
import com.subrat.Oxygen.graphics.object.DrawableObject;
import com.subrat.Oxygen.graphics.object.DrawableWaterParticle;
import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.MathUtils;
import com.subrat.Oxygen.utilities.Statistics;

import java.util.ArrayList;
import java.util.Date;
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
    Date prevRenderTime;

    private HadaGraphicsEngine() {
        waterPainter = new Paint();
        waterPainter.setColor(Color.CYAN);
        waterPainter.setStyle(Paint.Style.STROKE);
        // waterPainter.setStrokeWidth(MathUtils.getMathUtils().getPixelFromMeter(Configuration.PARTICLE_RADIUS * 10));
        waterPainter.setStrokeWidth(3);

        textPainter = new Paint();
        textPainter.setColor(Color.WHITE);
        textPainter.setStyle(Paint.Style.FILL);
        textPainter.setTextSize(40);

        bitmapCache = new HashMap<>();

        repeatTaskRunning = false;
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

    public Paint getWaterPainter() {
        return waterPainter;
    }

    public void drawObjects(Canvas canvas) {
        FrameBuffer.getFrameBuffer().startFrameBufferRead();
        int size = FrameBuffer.getFrameBuffer().getFrameBufferSize();
        for (int i = 0; i < size; ++i) {
            DrawableObject drawableObject = FrameBuffer.getFrameBuffer().getFrameObject(i);
            if (drawableObject instanceof DrawableWaterParticle) {

            } else {
                FrameBuffer.getFrameBuffer().getFrameObject(i).draw(canvas);
            }
        }

        // Particle drawing is done together for optimized
        drawParticles(canvas);
        FrameBuffer.getFrameBuffer().stopFrameBufferRead();
        showStatistics(canvas);
    }

    public void drawParticles(Canvas canvas) {
        ArrayList<DrawableWaterParticle> particleList = new ArrayList<>();
        int size = FrameBuffer.getFrameBuffer().getFrameBufferSize();
        for (int i = 0; i < size; ++i) {
            DrawableObject drawableObject = FrameBuffer.getFrameBuffer().getFrameObject(i);
            if (drawableObject instanceof DrawableWaterParticle) {
                particleList.add((DrawableWaterParticle)drawableObject);
            }
        }

        if (particleList.isEmpty()) return;

        float[] points = new float[2 * particleList.size()];
        int index = 0;
        for (DrawableWaterParticle drawableWaterParticle : particleList) {
            points[index++] = drawableWaterParticle.getPosition().x;
            points[index++] = drawableWaterParticle.getPosition().y;
        }

        canvas.drawPoints(points, getWaterPainter());
        // canvas.drawVertices(Canvas.VertexMode.TRIANGLES, points.length, points, 0, null, 0, null, 0, null, 0, 0, getWaterPainter());
    }

    public void initRenderLoop(final OxygenView oxygenView) {
        final int refreshMsec = 1000 / Configuration.GRAPHICS_FPS;
        repeatHandler = new Handler();
        repeatRunnable = new Runnable() {
            @Override
            public void run() {
                Date renderStartTime = new Date();
                oxygenView.invalidate();
                Statistics.getStatistics().incrementNumRenders();
                Date renderEndTime = new Date();
                long renderTime = renderEndTime.getTime() - renderStartTime.getTime();
                if (renderTime < refreshMsec) {
                    repeatHandler.postDelayed(repeatRunnable, refreshMsec - renderTime + 5);
                } else {
                    repeatHandler.post(repeatRunnable);
                }
                /*
                Date currentTime = new Date();
                long timeElapsed;
                if (prevRenderTime == null) {
                    timeElapsed = refreshMsec;
                } else {
                    timeElapsed = currentTime.getTime() - prevRenderTime.getTime();
                }
                if (timeElapsed < refreshMsec) {
                    repeatHandler.postDelayed(repeatRunnable, refreshMsec - timeElapsed);
                } else {
                    oxygenView.invalidate();
                    Statistics.getStatistics().incrementNumRenders();
                    currentTime = new Date();
                    timeElapsed = currentTime.getTime() - prevRenderTime.getTime();
                    prevRenderTime = currentTime;
                    if (timeElapsed < refreshMsec) {
                        repeatHandler.postDelayed(repeatRunnable, refreshMsec - timeElapsed);
                    } else {
                        repeatHandler.post(repeatRunnable);
                    }
                }
                */
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
        canvas.drawText("Renders: " + Statistics.getStatistics().getNumRenders(), 40, 60, textPainter);
        canvas.drawText("RenderFps: " + Statistics.getStatistics().getRenderFps(), 40, 90, textPainter);
        canvas.drawText("Physics: " + Statistics.getStatistics().getNumPhysicsUpdates(), 40, 120, textPainter);
        canvas.drawText("PhysicsFps: " + Statistics.getStatistics().getPhysicsFps(), 40, 150, textPainter);
    }
}
