package com.subrat.Oxygen.utilities;

import android.graphics.PointF;

import com.subrat.Oxygen.graphics.object.DrawableCircle;
import com.subrat.Oxygen.graphics.object.DrawableLine;
import com.subrat.Oxygen.graphics.object.DrawableObject;
import com.subrat.Oxygen.physics.PhysicsManager;
import com.subrat.Oxygen.physics.object.PhysicsCircle;
import com.subrat.Oxygen.physics.object.PhysicsLine;
import com.subrat.Oxygen.physics.object.PhysicsObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by subrat.panda on 18/12/15.
 */
public class ObjectMapper {
    private static ObjectMapper objectMapper = null;

    private Map<Integer, DrawableObject> physicsObjectIdToDrawableObjectMap = null;

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) objectMapper = new ObjectMapper();
        return objectMapper;
    }

    private ObjectMapper() {
        physicsObjectIdToDrawableObjectMap = new HashMap<Integer, DrawableObject>();
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

    public void createObjectMapping(PhysicsObject physicsObject, DrawableObject drawableObject) {
        if (physicsObjectIdToDrawableObjectMap.containsKey(physicsObject.getObjectId())) return;
        physicsObjectIdToDrawableObjectMap.put(physicsObject.getObjectId(), drawableObject);
    }

    public DrawableObject getDrawableObjectFromPhysicsObject(PhysicsObject object) {
        return physicsObjectIdToDrawableObjectMap.get(object.getObjectId());
    }


}
