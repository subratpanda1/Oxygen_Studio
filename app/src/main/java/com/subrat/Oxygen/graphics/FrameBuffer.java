package com.subrat.Oxygen.graphics;

import android.graphics.PointF;
import android.util.Log;

import com.subrat.Oxygen.graphics.object.DrawableCircle;
import com.subrat.Oxygen.graphics.object.DrawableLine;
import com.subrat.Oxygen.graphics.object.DrawableObject;
import com.subrat.Oxygen.physics.object.PhysicsObject;
import com.subrat.Oxygen.utilities.MathUtils;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by subrat.panda on 19/12/15.
 */
public class FrameBuffer {
    private static FrameBuffer frameBuffer = null;

    public static FrameBuffer getFrameBuffer() {
        if (frameBuffer == null) frameBuffer = new FrameBuffer();
        return frameBuffer;
    }

    private ArrayList<DrawableObject> readFrameBuffer;
    private ArrayList<DrawableObject> writeFrameBuffer;
    Lock readLock;
    Lock writeLock;

    private FrameBuffer() {
        readFrameBuffer = new ArrayList<>();
        writeFrameBuffer = new ArrayList<>();
        readLock = new ReentrantLock();
        writeLock = new ReentrantLock();
        frameBufferReadInProgress = false;
    }

    private boolean frameBufferReadInProgress;
    public void startFrameBufferRead() {
        readLock.lock();
        frameBufferReadInProgress = true;
    }
    public void stopFrameBufferRead() {
        frameBufferReadInProgress = false;
        readLock.unlock();
    }

    public DrawableObject getFrameObject(int i) {
        if (frameBufferReadInProgress == false) return null;
        if (readFrameBuffer.size() <= i) return null;
        return readFrameBuffer.get(i);
    }

    public int getFrameBufferSize() {
        if (frameBufferReadInProgress == false) return 0;
        return readFrameBuffer.size();
    }

    public void writeToFrameBuffer(ArrayList<PhysicsObject> physicsObjects) {
        try {
            writeLock.lock();
            writeFrameBuffer.clear();
            for (PhysicsObject physicsObject : physicsObjects) {
                DrawableObject drawableObject = GraphicsObjectBuilder.getGraphicsObjectBuilder().buildObject(physicsObject);
                writeFrameBuffer.add(drawableObject);
            }

            readLock.lock();
            ArrayList<DrawableObject> tmpFrameBuffer = readFrameBuffer;
            readFrameBuffer = writeFrameBuffer;
            writeFrameBuffer = tmpFrameBuffer;
        } catch (Exception e) {
            Log.e("Subrat", e.getMessage(), e);
            throw e;
        } finally {
            readLock.unlock();
            writeLock.unlock();
        }
    }

    public void printAllObjects() {
        int serial = 0;
        String objectId;
        String objectType;
        String objectPosition;
        String objectRotation;
        String printString = "";

        for (DrawableObject object : readFrameBuffer) {
            ++serial;
            objectId = "" + object.getObjectId();
            if (object instanceof DrawableCircle) {
                DrawableCircle drawableCircle = (DrawableCircle)object;
                objectType = "Circle: ";
                objectPosition = "Center: " + MathUtils.getMathUtils().getPointString(drawableCircle.getCenter());
                objectRotation = "Rotation: " + drawableCircle.getRotation();
                printString = "Drawable Sl: " + serial + ", " + "Id: " + objectId + ", " + objectType + ", " + objectPosition + ", " + objectRotation;
            } else if (object instanceof DrawableLine) {
                DrawableLine drawableLine = (DrawableLine)object;
                objectType = "Line: ";
                objectPosition = "Position: Start: " + MathUtils.getMathUtils().getPointString(drawableLine.getStart()) + ", " +
                        "End: " + MathUtils.getMathUtils().getPointString(drawableLine.getEnd());
                printString = "Drawable Sl: " + serial + ", " + "Id: " + objectId + ", " + objectType + ", " + objectPosition;
            // } else if (object instanceof DrawableWaterParticle) {

            }
            Log.i("Subrat", printString);
        }
    }

    public void clearFrameBuffers() {
        readLock.lock();
        readFrameBuffer.clear();
        readLock.unlock();

        writeLock.lock();
        writeFrameBuffer.clear();
        writeLock.unlock();
    }
}
