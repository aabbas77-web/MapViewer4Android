package com.oghab.mapviewer.osmdroid;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.osmdroid.views.overlay.compass.IOrientationConsumer;
import org.osmdroid.views.overlay.compass.IOrientationProvider;

public class GestureCompassOrientationProvider implements IOrientationProvider {
    private float mAzimuth;

    public GestureCompassOrientationProvider(Context context) {
    }

    //
    // IOrientationProvider
    //

    /**
     * Enable orientation updates from the internal compass sensor and show the compass.
     */
    @Override
    public boolean startOrientationProvider(IOrientationConsumer orientationConsumer) {
        boolean result = true;
        return result;
    }

    @Override
    public void stopOrientationProvider() {
    }

    @Override
    public float getLastKnownOrientation() {
        return mAzimuth;
    }

    @Override
    public void destroy() {
        stopOrientationProvider();
    }

//    @Override
//    public void onSensorChanged(final SensorEvent event) {
//        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
//            if (event.values != null) {
//                mAzimuth = event.values[0];
//                if (mOrientationConsumer != null)
//                    mOrientationConsumer.onOrientationChanged(mAzimuth, this);
//            }
//        }
//    }
}
