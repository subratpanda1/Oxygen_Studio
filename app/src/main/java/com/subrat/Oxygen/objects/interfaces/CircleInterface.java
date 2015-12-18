package com.subrat.Oxygen.objects.interfaces;

import android.graphics.PointF;

/**
 * Created by subrat.panda on 18/12/15.
 */
public interface CircleInterface {
    PointF getCenter();
    void setCenter(PointF center);

    float getRadius();
    void setRadius(float radius);

    int getRotation();
    void setRotation(int rotation);
}
