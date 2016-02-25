package com.subrat.Oxygen.simulation;

import android.graphics.PointF;
import android.os.Handler;
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

    private Lock lock;
    private ThreadInstruction threadInstruction = null;

    // Repeat Task
    private Handler repeatHandler;
    Runnable repeatRunnable;
    boolean repeatTaskRunning;
    Date prevSimulationTime;
    boolean updateInProgress;

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
        threadInstruction = ThreadInstruction.NO_OP;
        lock = new ReentrantLock();
    }

    private void simulatorEventLoop() {
        Date prevDate = null;
        final int refreshMsec = 1000 / Configuration.PHYSICS_FPS;
        while (true) {
            ThreadInstruction instruction = getThreadInstruction();
            if (instruction == ThreadInstruction.THREAD_STOP) {
                break;
            } else if (instruction == ThreadInstruction.THREAD_CONTINUE) {
                if (OxygenActivity.getContext() == null) return;
                updateSensorReading();
                Date currentDate = new Date();
                if (prevDate != null) {
                    long timeDiff = currentDate.getTime() - prevDate.getTime();
                    if (timeDiff < refreshMsec) {
                        try {
                            Thread.sleep(refreshMsec - timeDiff);
                        } catch(InterruptedException ex) {
                            thread.interrupt();
                        }
                        currentDate = new Date();
                        timeDiff = currentDate.getTime() - prevDate.getTime();
                    }
                    PhysicsManager.getPhysicsManager().step((float) timeDiff / 1000);
                }
                PhysicsManager.getPhysicsManager().updateAllObjects();
                // PhysicsManager.getPhysicsManager().printAllObjects();
                FrameBuffer.getFrameBuffer().writeToFrameBuffer();
                Statistics.getStatistics().incrementNumPhysicsUpdates();
                threadHandler.sendMessage(threadHandler.obtainMessage());
                prevDate = currentDate;
            } else if (instruction == ThreadInstruction.THREAD_PAUSE) {
                prevDate = null;
                try {
                    Thread.sleep(refreshMsec);
                } catch(InterruptedException ex) {
                    thread.interrupt();
                }
            }
        }
    }

    private void initSimulatorLoop() {
        Looper.prepare();
        updateInProgress = false;
        final int refreshMsec = 1000 / Configuration.PHYSICS_FPS;
        repeatHandler = new Handler();
        repeatRunnable = new Runnable() {
            @Override
            public void run() {
                ThreadInstruction instruction = getThreadInstruction();
                if (instruction == ThreadInstruction.THREAD_STOP) {
                    prevSimulationTime = null;
                    stopSimulatorLoop();
                } else if (instruction == ThreadInstruction.THREAD_CONTINUE) {
                    if (OxygenActivity.getContext() == null) return;
                    updateSensorReading();
                    Date currentTime = new Date();
                    long timeElapsed;

                    if (prevSimulationTime == null) {
                        timeElapsed = refreshMsec;
                    } else {
                        timeElapsed = currentTime.getTime() - prevSimulationTime.getTime();
                    }

                    if (updateInProgress) {
                        repeatHandler.postDelayed(repeatRunnable, refreshMsec);
                    } else if (timeElapsed < refreshMsec) {
                        repeatHandler.postDelayed(repeatRunnable, refreshMsec - timeElapsed);
                    } else {
                        PhysicsManager.getPhysicsManager().step(((float) timeElapsed) / 1000L);
                        prevSimulationTime = currentTime;
                        repeatHandler.postDelayed(repeatRunnable, refreshMsec/*in msec*/);

                        updateInProgress = true;
                        PhysicsManager.getPhysicsManager().updateAllObjects();
                        // PhysicsManager.getPhysicsManager().printAllObjects();
                        FrameBuffer.getFrameBuffer().writeToFrameBuffer();
                        Statistics.getStatistics().incrementNumPhysicsUpdates();
                        threadHandler.sendMessage(threadHandler.obtainMessage());
                        updateInProgress = false;
                    }
                } else if (instruction == ThreadInstruction.THREAD_PAUSE) {
                    prevSimulationTime = null;
                    repeatHandler.postDelayed(repeatRunnable, refreshMsec/*in msec*/);
                }
            }
        };
        Looper.loop();
    }

    private void startSimulatorLoop() {
        if (repeatTaskRunning) return;
        repeatRunnable.run();
        repeatTaskRunning = true;
    }

    private void stopSimulatorLoop() {
        if (!repeatTaskRunning) return;
        repeatHandler.removeCallbacks(repeatRunnable);
        repeatTaskRunning = false;
    }

    public void startSimulator() {
        setThreadInstruction(ThreadInstruction.NO_OP);

        if (thread == null) {
            thread = new Thread(new Runnable() {
                public void run() {
                    // simulatorEventLoop();
                    initSimulatorLoop();
                    startSimulatorLoop();
                }
            });
            thread.start();
        }

        resumeSimulator();
    }

    public void stopSimulator() {
        pauseSimulator();
        setThreadInstruction(ThreadInstruction.THREAD_STOP);
        thread = null;
        PhysicsManager.getPhysicsManager().clearWorld();
    }

    public void pauseSimulator() {
        setThreadInstruction(ThreadInstruction.THREAD_PAUSE);
        deviceSensorManager.unregisterShakeListener();
        deviceSensorManager.unregisterSensors();
    }

    public void resumeSimulator() {
        if (thread.getState().equals(Thread.State.WAITING)) {
            Log.e("Subrat", "Thread is waiting");
        }
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
