package com.subrat.Oxygen.objects;

import com.subrat.Oxygen.objects.drawableObject.DrawableCircle;
import com.subrat.Oxygen.objects.drawableObject.DrawableObject;
import com.subrat.Oxygen.objects.physicsObject.PhysicsCircle;
import com.subrat.Oxygen.objects.physicsObject.PhysicsObject;

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
            DrawableCircle drawableCircle = (DrawableCircle) drawableObject;
            return new PhysicsCircle(drawableCircle.getCenter(), drawableCircle.getRadius(), drawableCircle.getRotation());
        }

        return null;
    }
}
