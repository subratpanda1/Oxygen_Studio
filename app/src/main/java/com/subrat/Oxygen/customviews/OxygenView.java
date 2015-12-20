package com.subrat.Oxygen.customviews;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.graphics.HadaGraphicsEngine;
import com.subrat.Oxygen.physics.PhysicsObjectBuilder;
import com.subrat.Oxygen.graphics.object.DrawableObject;

import java.util.ArrayList;

/**
 * Created by subrat.panda on 07/05/15.
 */
public class OxygenView extends View implements View.OnTouchListener {
    Paint paint;
    ArrayList<PointF> points;
    public boolean drawingMode = false;
    Path path = new Path();
    public OxygenActivity oxygenActivity;

    private void initializeView() {
        if (isInEditMode()) return;
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(0);
        points = new ArrayList<PointF>();
    }

    public OxygenView(Context context) {
        super(context);
        initializeView();
    }

    public OxygenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }

    private void drawAllObjects(Canvas canvas) {
    	if (OxygenActivity.getContext() == null) return;
    	OxygenActivity.setCanvasDimensions(canvas.getWidth(), canvas.getHeight());
        PhysicsObjectBuilder.getPhysicsObjectBuilder().createOrUpdateBoundaryLines();

        HadaGraphicsEngine.getHadaGraphicsEngine().drawObjects(canvas);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawingMode) {
            boolean firstTime = true;
            for (PointF point : points) {
                if (firstTime) {
                    path.moveTo(point.x, point.y);
                    firstTime = false;
                } else {
                    path.lineTo(point.x, point.y);
                }
            }

            canvas.drawPath(path, paint);
            path.reset();
        }

        drawAllObjects(canvas);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        PointF point = new PointF(event.getX(), event.getY());
        points.add(point);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawingMode = true;
                oxygenActivity.pauseSimulation();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                PhysicsObjectBuilder.getPhysicsObjectBuilder().buildObject(points);
                points.clear();
                drawingMode = false;
                oxygenActivity.resumeSimulation();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }
}

