package com.subrat.Oxygen.objects.interfaces;

import android.graphics.PointF;

/**
 * Created by subrat.panda on 18/12/15.
 */
public interface LineInterface {
    PointF getStart();
    void setStart(PointF start);

    PointF getEnd();
    void setEnd(PointF end);
}
