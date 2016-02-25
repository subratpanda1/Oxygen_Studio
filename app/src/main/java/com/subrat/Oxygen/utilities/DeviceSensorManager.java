package com.subrat.Oxygen.utilities;


import android.content.Context;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.subrat.Oxygen.activities.OxygenActivity;

/**
 * Created by subrat.panda on 19/12/15.
 */
public class DeviceSensorManager {
    private static DeviceSensorManager deviceSensorManager = null;
    public static DeviceSensorManager getDeviceSensorManager() {
        if (deviceSensorManager == null) deviceSensorManager = new DeviceSensorManager();
        return deviceSensorManager;
    }

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    private DeviceSensorManager() {
        mSensorManager = (SensorManager) OxygenActivity.getContext().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
    }

    public void registerSensors() {
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void unregisterSensors() {
        mSensorManager.unregisterListener(mShakeDetector);
    }

    public PointF getAcceleration() {
        float[] accelValues = mShakeDetector.getAccelValues(); // Got in mtr per sec per sec
        PointF acceleration = new PointF(accelValues[0], accelValues[1]);
        return acceleration;
    }

    public void registerShakeListener(final Runnable runnable) {
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                runnable.run();
            }
        });
    }

    public void unregisterShakeListener() {
        mShakeDetector.setOnShakeListener(null);
    }
}
