package com.subrat.Oxygen.simulation;

import android.graphics.PointF;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.graphics.FrameBuffer;
import com.subrat.Oxygen.physics.PhysicsManager;
import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.DeviceSensorManager;
import com.subrat.Oxygen.utilities.Statistics;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by subrat.panda on 19/12/15.
 */
public class SimulatorNew {
    private static SimulatorNew simulator = null;

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

    private Lock lock;
    private ThreadInstruction threadInstruction = null;

    // Repeat Task
    private Handler repeatHandler;
    Runnable repeatRunnable;
    Date prevSimulationTime;
    boolean repeatTaskRunning;

    public ThreadInstruction getThreadInstruction() {
        lock.lock();
        ThreadInstruction value = threadInstruction;
        lock.unlock();
        return value;
    }

    public void setThreadInstruction(ThreadInstruction instruction) {
        lock.lock();
        threadInstruction = instruction;
        lock.unlock();
    }

    private DeviceSensorManager deviceSensorManager;

    public static SimulatorNew getSimulator() {
        if (simulator == null) simulator = new SimulatorNew();
        return simulator;
    }

    public SimulatorNew initSimulator() {
        final int refreshMsec = 1000 / Configuration.PHYSICS_FPS;
        HandlerThread hThread = new HandlerThread("HandlerThread");
        hThread.start();
        repeatHandler = new Handler(hThread.getLooper());
        repeatRunnable = new Runnable() {
            @Override
            public void run() {
                ThreadInstruction instruction = getThreadInstruction();
                if (instruction == ThreadInstruction.THREAD_STOP) {
                    prevSimulationTime = null;
                    stopSimulatorLoop();
                } else if (instruction == ThreadInstruction.THREAD_CONTINUE) {
                    if (OxygenActivity.getContext() == null) return;
                    Date currentTime = new Date();
                    long timeElapsed;

                    if (prevSimulationTime == null) {
                        timeElapsed = 0;
                    } else {
                        timeElapsed = currentTime.getTime() - prevSimulationTime.getTime();
                    }

                    Date physicsStartTime = new Date();
                    updateSensorReading();
                    PhysicsManager.getPhysicsManager().step(((float) timeElapsed) / 1000L);
                    PhysicsManager.getPhysicsManager().updateAllObjects();
                    FrameBuffer.getFrameBuffer().writeToFrameBuffer();
                    Statistics.getStatistics().incrementNumPhysicsUpdates();
                    Date physicsEndTime = new Date();
                    prevSimulationTime = currentTime;

                    long physicsTime = physicsEndTime.getTime() - physicsStartTime.getTime();
                    if (physicsTime < refreshMsec) {
                        repeatHandler.postDelayed(repeatRunnable, refreshMsec - physicsTime + 5);
                    } else {
                        repeatHandler.post(repeatRunnable);
                    }
                } else if (instruction == ThreadInstruction.THREAD_PAUSE) {
                    prevSimulationTime = null;
                    repeatHandler.postDelayed(repeatRunnable, refreshMsec/*in msec*/);
                } else {
                    prevSimulationTime = null;
                    repeatHandler.postDelayed(repeatRunnable, refreshMsec/*in msec*/);
                }
            }
        };

        PhysicsManager.getPhysicsManager().initWorld();
        return simulator;
    }

    private SimulatorNew() {
        deviceSensorManager = DeviceSensorManager.getDeviceSensorManager();
        threadInstruction = ThreadInstruction.NO_OP;
        lock = new ReentrantLock();
    }

    private void startSimulatorLoop() {
        if (repeatTaskRunning) return;
        repeatHandler.post(repeatRunnable);
        repeatTaskRunning = true;
    }

    private void stopSimulatorLoop() {
        if (!repeatTaskRunning) return;
        repeatHandler.removeCallbacks(repeatRunnable);
        repeatTaskRunning = false;
    }

    public void startSimulator() {
        setThreadInstruction(ThreadInstruction.NO_OP);
        startSimulatorLoop();
        resumeSimulator();
    }

    public void stopSimulator() {
        pauseSimulator();
        setThreadInstruction(ThreadInstruction.THREAD_STOP);
        PhysicsManager.getPhysicsManager().clearWorld();
    }

    public void pauseSimulator() {
        setThreadInstruction(ThreadInstruction.THREAD_PAUSE);
        deviceSensorManager.unregisterShakeListener();
        deviceSensorManager.unregisterSensors();
    }

    public void resumeSimulator() {
        deviceSensorManager.registerShakeListener(getShakeHandlerRunnable());
        deviceSensorManager.registerSensors();
        setThreadInstruction(ThreadInstruction.THREAD_CONTINUE);
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
        gravity.x = -gravity.x;
        gravity.y = -gravity.y;
        PhysicsManager.getPhysicsManager().setGravity(gravity);
    }
}
