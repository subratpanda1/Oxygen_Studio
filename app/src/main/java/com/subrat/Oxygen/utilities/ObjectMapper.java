package com.subrat.Oxygen.utilities;

import com.subrat.Oxygen.graphics.object.DrawableCircle;
import com.subrat.Oxygen.graphics.object.DrawableLine;
import com.subrat.Oxygen.graphics.object.DrawableObject;
import com.subrat.Oxygen.physics.object.PhysicsCircle;
import com.subrat.Oxygen.physics.object.PhysicsLine;
import com.subrat.Oxygen.physics.object.PhysicsObject;

/**
 * Created by subrat.panda on 18/12/15.
 */
public class ObjectMapper {
    private static ObjectMapper objectMapper = null;

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) objectMapper = new ObjectMapper();
        return objectMapper;
    }

    public PhysicsObject getPhysicsObjectFromDrawableObject(DrawableObject drawableObject) {
        if (drawableObject instanceof DrawableCircle) {
            DrawableCircle drawableCircle = (DrawableCircle)drawableObject;
            return new PhysicsCircle(drawableCircle.getCenter(), drawableCircle.getRadius(), drawableCircle.getRotation());
        } else if (drawableObject instanceof DrawableLine) {
            DrawableLine drawableLine = (DrawableLine)drawableObject;
            return new PhysicsLine(drawableLine.getStart(), drawableLine.getEnd());
        }

        return null;
    }
}
