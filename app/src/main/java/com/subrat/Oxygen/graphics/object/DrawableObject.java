package com.subrat.Oxygen.graphics.object;

import android.graphics.Canvas;

/**
 * Created by subrat.panda on 18/12/15.
 */
public abstract class DrawableObject {
    public abstract boolean draw(Canvas canvas);

    private int objectId;
    public int getObjectId() { return objectId; }
    public void setObjectId(int id) { objectId = id; }
}
