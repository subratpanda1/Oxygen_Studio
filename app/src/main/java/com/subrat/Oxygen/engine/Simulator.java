package com.subrat.Oxygen.engine;

import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.graphics.FrameBuffer;
import com.subrat.Oxygen.physics.PhysicsManager;
import com.subrat.Oxygen.utilities.DeviceSensorManager;
import com.subrat.Oxygen.utilities.MathUtils;
import com.subrat.Oxygen.utilities.Statistics;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by subrat.panda on 19/12/15.
 */
public class Simulator {
    private static Simulator simulator = null;

    public enum ThreadInstruction {
        THREAD_CONTINUE(0), THREAD_STOP(1), THREAD_PAUSE(2), NO_OP(3);

        private final int value;
        ThreadInstruction(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    AtomicInteger threadInstruction = null;

    private Handler threadHandler = null;
    private Thread thread = null;
    private DeviceSensorManager deviceSensorManager;

    public static Simulator getSimulator() {
        if (simulator == null) simulator = new Simulator();
        return simulator;
    }

    public Simulator initSimulator(final Runnable runnable) {
        threadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                runnable.run();
            }
        };
        PhysicsManager.getPhysicsManager().initWorld();
        return simulator;
    }

    private Simulator() {
        deviceSensorManager = DeviceSensorManager.getDeviceSensorManager();
        threadInstruction = new AtomicInteger(ThreadInstruction.NO_OP.getValue());
    }

    private void simulatorEventLoop() {
        Date prevDate = null;
        while (true) {
            if (threadInstruction.get() == ThreadInstruction.THREAD_STOP.getValue()) {
                break;
            } else if (threadInstruction.get() == ThreadInstruction.THREAD_CONTINUE.getValue()) {
                if (OxygenActivity.getContext() == null) return;
                updateSensorReading();
                Date currentDate = new Date();
                if (prevDate != null) {
                    long timeDiff = currentDate.getTime() - prevDate.getTime();
                    if (timeDiff < 5) {
                        try {
                            Thread.sleep(5 - timeDiff);
                        } catch(InterruptedException ex) {
                            thread.interrupt();
                        }
                        currentDate = new Date();
                        timeDiff = currentDate.getTime() - prevDate.getTime();
                    }
                    PhysicsManager.getPhysicsManager().step((float) timeDiff / 1000);
                }
                PhysicsManager.getPhysicsManager().updateAllObjects();
                FrameBuffer.getFrameBuffer().writeToFrameBuffer(PhysicsManager.getPhysicsManager().getObjectList());
                Statistics.getStatistics().incrementNumPhysicsUpdates();
                threadHandler.sendMessage(threadHandler.obtainMessage());
                prevDate = currentDate;
            } else if (threadInstruction.get() == ThreadInstruction.THREAD_PAUSE.getValue()) {
                // pauseSimulation
            }
        }
    }

    public void startSimulator() {
        threadInstruction.set(ThreadInstruction.NO_OP.getValue());

        if (thread == null) {
            thread = new Thread(new Runnable() {
                public void run() {
                    simulatorEventLoop();
                }
            });
            thread.start();
        }

        resumeSimulator();
    }

    public void stopSimulator() {
        pauseSimulator();
        threadInstruction.set(ThreadInstruction.THREAD_STOP.getValue());
        thread = null;
        PhysicsManager.getPhysicsManager().clearWorld();
    }

    public void pauseSimulator() {
        deviceSensorManager.unregisterShakeListener();
        deviceSensorManager.unregisterSensors();
        threadInstruction.set(ThreadInstruction.THREAD_PAUSE.getValue());
    }

    public void resumeSimulator() {
        deviceSensorManager.registerShakeListener(getShakeHandlerRunnable());
        deviceSensorManager.registerSensors();
        threadInstruction.set(ThreadInstruction.THREAD_CONTINUE.getValue());
    }

    private Runnable getShakeHandlerRunnable() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                PhysicsManager.getPhysicsManager().resetVelocities();
            }
        };

        return runnable;
    }

    private void updateSensorReading() {
        PointF gravity = DeviceSensorManager.getDeviceSensorManager().getAcceleration();
        gravity.y = -gravity.y;
        PhysicsManager.getPhysicsManager().setGravity(gravity);
    }
}
