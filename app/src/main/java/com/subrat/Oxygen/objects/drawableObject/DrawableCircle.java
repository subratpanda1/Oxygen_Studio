package com.subrat.Oxygen.objects.drawableObject;

import android.graphics.*;

import com.subrat.Oxygen.R;
import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.objects.interfaces.CircleInterface;
import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.MathUtils;

/**
 * Created by subrat.panda on 07/05/15.
 */
public class DrawableCircle extends DrawableObject implements CircleInterface {
    private PointF center; // In mtr
    public PointF getCenter() { return center; }
    public void setCenter(PointF point) { center = point; }

    private float radius; // In mtr
    public float getRadius() { return radius; }
    public void setRadius(float radius) {
        this.radius = radius;
    }

    private int rotation; // In degree
    public int getRotation() { return rotation; }
    public void setRotation(int deg) { rotation = deg; }

    private Paint fillPainter = null;
    private Paint strokePainter = null;
    Bitmap pic;
    
    private boolean isParticle = false;

    // Should only be called from HadaGraphicsEngine
    public DrawableCircle(PointF center, float radius, int rotation) {
        this.center = center;
        this.radius = radius;
        this.rotation = rotation;
        this.isParticle = false;

        initBitmap();
    }

    // Should only be called from HadaGraphicsEngine
    public DrawableCircle(PointF center, float radius, int rotation, boolean isParticle) {
        this.center = center;
        this.radius = radius;
        this.rotation = rotation;
        this.isParticle = isParticle;

        if (isParticle != true) {
        	initBitmap();
    	}
    }
    
    private void initBitmap() {
        pic = BitmapFactory.decodeResource(OxygenActivity.getContext().getResources(), R.drawable.tennis_ball);
        pic = Bitmap.createScaledBitmap(pic, 2 * MathUtils.getPixelFromMeter(this.radius), 2 * MathUtils.getPixelFromMeter(this.radius),  true);
    }

    protected Paint getFillPainter() {
        if (fillPainter == null) {
            fillPainter = new Paint();
            fillPainter.setColor(Color.parseColor(MathUtils.getRandomColor()));
            fillPainter.setAntiAlias(true);
            fillPainter.setStyle(Paint.Style.FILL);
        }
        return fillPainter;
    }

    protected Paint getStrokePainter() {
        if (strokePainter == null) {
            strokePainter = new Paint();
            strokePainter.setColor(Color.parseColor(MathUtils.getRandomColor()));
            strokePainter.setAntiAlias(true);
            strokePainter.setStyle(Paint.Style.STROKE);
            strokePainter.setStrokeWidth(Configuration.CIRCLE_BORDER);
        }
        return strokePainter;
    }
    
    public boolean draw(Canvas canvas) {
    	if (isParticle) {
    	} else {
    		int bitmapCornerX = MathUtils.getPixelFromMeter(this.getCenter().x - this.getRadius());
    		int bitmapCornerY = MathUtils.getPixelFromMeter(this.getCenter().y - this.getRadius());
    		
    		Matrix matrix = new Matrix();
    		// System.out.println("Drawing at angle: " + rotation);
    		matrix.setRotate(rotation, pic.getWidth() / 2, pic.getHeight() / 2);
    		matrix.postTranslate(bitmapCornerX, bitmapCornerY);
    		
    		// canvas.drawBitmap(pic, bitmapCornerX, bitmapCornerY, null);
    		canvas.drawBitmap(pic, matrix, null);
    	}
        return true;
    }
}
