package com.subrat.Oxygen.engine;

import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.graphics.FrameBuffer;
import com.subrat.Oxygen.physics.PhysicsManager;
import com.subrat.Oxygen.utilities.DeviceSensorManager;

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
        return simulator;
    }

    public static Simulator initSimulator(final Runnable runnable) {
        if (simulator == null) simulator = new Simulator(runnable);
        return simulator;
    }

    private Simulator(final Runnable runnable) {
        threadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                runnable.run();
            }
        };
        deviceSensorManager = DeviceSensorManager.getDeviceSensorManager();
        threadInstruction = new AtomicInteger(ThreadInstruction.NO_OP.getValue());
    }

    private void simulatorEventLoop() {
        Date prevDate = null;
        while (true) {
            if (threadInstruction.get() == ThreadInstruction.THREAD_STOP.getValue()) {
                break;
            } else if (threadInstruction.get() == ThreadInstruction.THREAD_CONTINUE.getValue()) {
                Date currentDate = new Date();
                if (OxygenActivity.getContext() == null) return;
                updateSensorReading();
                long timeDiff = prevDate == null ? 10L : currentDate.getTime() - prevDate.getTime();
                PhysicsManager.getPhysicsManager().step(timeDiff / 1000);
                FrameBuffer.getFrameBuffer().copyPhysicsObjectsToFrameBuffer();
                prevDate = currentDate;
                // threadHandler.sendMessage(threadHandler.obtainMessage());
            } else if (threadInstruction.get() == ThreadInstruction.THREAD_PAUSE.getValue()) {
                // pauseSimulation
            }

            try {
                Log.i("Subrat", "Sleeping for 1000 ms");
                Thread.sleep(1000);
            } catch(InterruptedException ex) {
                thread.interrupt();
            }
        }
    }

    public void startSimulator() {
        threadInstruction.set(ThreadInstruction.NO_OP.getValue());
        PhysicsManager.getPhysicsManager().initWorld();

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
        // TODO: Scale gravity accordingly
        PhysicsManager.getPhysicsManager().setGravity(gravity);
    }
}
