package com.oghab.mapviewer.mapviewer;

/**
 * @author Ali Abbas
 */

import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;
import com.oghab.mapviewer.utils.mv_utils;

import java.util.Locale;

/**
 * Created by MapViewer on 27/03/2017.
 */

public class Tab_Sensors extends Fragment implements View.OnClickListener {
    private static final String TAG = "Sensors";

    static EditText et_lon;
    static EditText et_lat;
    static EditText et_alt;

    EditText et_yaw;
    EditText et_pitch;
    EditText et_roll;

    EditText et_metal_threshold;
    EditText et_metal_power;

    static public float fYawEnc = 0.0f;
    static public float fPitchEnc = 0.0f;
    static public float fRollEnc = 0.0f;

    static public float fYaw = 0.0f;
    static public float fPitch = 0.0f;
    static public float fRoll = 0.0f;

    static public float dDeviceYaw = 0.0f;
    static public float dDevicePitch = 0.0f;
    static public float dDeviceRoll = 0.0f;
    static public boolean bIsDeviceCalibrated = false;

    static public long metal_power = 0;
    static public boolean metal_found = false;

//    CheckBox cb_dead_reckoning_navigation;
    CheckBox cb_cam_pos;
    CheckBox cb_cam_angles;
    CheckBox cb_meta_detection;
    private LinearLayout ll_settings;

    SensorManager sensorManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * Inflate the layout for this fragment
         */
        View view = null;
        try
        {
            view = inflater.inflate(R.layout.tab_sensors, container, false);
            init(view);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return view;
    }

    private void init(View view) {
        try
        {
//            activity = (MainActivity)activity0;
//            ctx = activity.getApplicationContext();

            et_lon = view.findViewById(R.id.gps_lon);
            et_lat = view.findViewById(R.id.gps_lat);
            et_alt = view.findViewById(R.id.gps_alt);

            et_yaw = view.findViewById(R.id.cam_yaw);
            et_pitch = view.findViewById(R.id.cam_pitch);
            et_roll = view.findViewById(R.id.cam_roll);

            et_metal_threshold = view.findViewById(R.id.et_metal_threshold);
            et_metal_power = view.findViewById(R.id.et_metal_power);

//            cb_dead_reckoning_navigation = view.findViewById(R.id.cb_dead_reckoning_navigation);
//            cb_dead_reckoning_navigation.setOnClickListener(this);
            cb_cam_pos = view.findViewById(R.id.cb_cam_pos);
            cb_cam_pos.setOnClickListener(this);
            cb_cam_angles = view.findViewById(R.id.cb_cam_angles);
            cb_cam_angles.setOnClickListener(this);
            cb_meta_detection = view.findViewById(R.id.cb_meta_detection);

            if(MainActivity.bNavigation) {
//                if(MainActivity.isDevelpoment())
//                    cb_dead_reckoning_navigation.setVisibility(View.VISIBLE);
//                else
//                    cb_dead_reckoning_navigation.setVisibility(View.GONE);
                cb_cam_pos.setChecked(true);
                cb_cam_angles.setChecked(true);
            }else{
//                cb_dead_reckoning_navigation.setVisibility(View.GONE);
                cb_cam_pos.setChecked(false);
                cb_cam_angles.setChecked(false);
            }

            ll_settings = view.findViewById(R.id.ll_settings);
            ll_settings.setVisibility(View.GONE);

            et_yaw.setText("0.0");
            et_pitch.setText("0.0");
            et_roll.setText("0.0");

            // sensors
            String service_name = Context.SENSOR_SERVICE;
            sensorManager = (SensorManager)MainActivity.activity.getSystemService(service_name);

//            assert sensorManager != null;
//            registerAccelerometerAndMagnetometer(sensorManager);
//        deprecatedSensorListener();

            // load settings
            float[] res = MainActivity.GetDeviceCalibrationDataJNI();
            dDeviceYaw = res[0];
            dDevicePitch = res[1];
            dDeviceRoll = res[2];

            SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
            bIsDeviceCalibrated = settings.getBoolean("bIsDeviceCalibrated", bIsDeviceCalibrated);

            // GNSS
//            AbsoluteDate initialDate = new AbsoluteDate(2004, 01, 01, 23, 30, 00.000, TimeScalesFactory.getUTC());
//            Vector3D position  = new Vector3D(-6142438.668, 3492467.560, -25767.25680);
//            Vector3D velocity  = new Vector3D(505.8479685, 942.7809215, 7435.922231);
//            Orbit initialOrbit = new KeplerianOrbit(new PVCoordinates(position, velocity),
//                    FramesFactory.getEME2000(), initialDate,
//                    Constants.EIGEN5C_EARTH_MU);

            //starting our task which update textview every 1000 ms
//            new RefreshTask().execute();

            // initialize
//            LocationManager locationManager = (LocationManager)MainActivity.activity.getSystemService(Context.LOCATION_SERVICE);
//            LocationListener locationListener = new MyLocationListener();
//            try {
//                locationManager.requestLocationUpdates("fused", 100, 0.5f, locationListener);
//            }
//            catch (SecurityException se)
//            {
//
//            }

            MainActivity.set_fullscreen();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void toggle_settings() {
        try
        {
            if(ll_settings.getVisibility() == View.GONE)
                ll_settings.setVisibility(View.VISIBLE);
            else
                ll_settings.setVisibility(View.GONE);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    @Override
    public void onClick(View view) {
        try
        {
            switch (view.getId()) {
//                case R.id.cb_dead_reckoning_navigation:
//                {
//
//                    break;
//                }
                case R.id.cb_cam_pos:
                {
                    if(!MainActivity.bNavigation) {
                        if (cb_cam_pos.isChecked()) {
                            if (MainActivity.tab_camera.cb_gps.isChecked()) {
                                cb_cam_pos.setChecked(false);
                                MainActivity.activity.show_toast("Please uncheck camera gps flag");
                            }
                        }
                    }
                    GpsmvLocationProvider.is_real_gps = true;
                    GpsmvLocationProvider.is_real_gps_enabled = MainActivity.tab_sensors.cb_cam_pos.isChecked();
                    break;
                }
                case R.id.cb_cam_angles:
                {
                    if(!MainActivity.bNavigation) {
                        if (cb_cam_angles.isChecked()) {
                            if (MainActivity.tab_camera.cb_imu.isChecked()) {
                                cb_cam_angles.setChecked(false);
                                MainActivity.activity.show_toast("Please uncheck camera imu flag");
                            }
                        }
                    }
                    break;
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    //class which updates our textview every second
//    class RefreshTask extends AsyncTask {
//
//        @Override
//        protected void onProgressUpdate(Object... values) {
//            super.onProgressUpdate(values);
////            String text = String.valueOf(System.currentTimeMillis());
////            myTextView.setText(text);
////            Tab_Main.server.SendBinary(Server.MV_CMD_AZI_ELE, fYaw, fPitch);
////            Tab_Main.server.SendBinary_ex(Server.MV_CMD_AZI_ELE, fYaw, fPitch);
////            Tab_Main.server.SendBinary_ex(Server.MV_CMD_AZI_ELE, fYaw, fPitch);
//        }
//
//        @Override
//        protected Object doInBackground(Object... params) {
//            while(true) {
//                try {
//                    //and update textview in ui thread
//                    publishProgress();
//
//                    //sleep for 1s in background...
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//
//                };
////                return null;
//            }
//        }
//    }

    @Override
    public void onStop() {
        try {
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        super.onStop();
    }
    static public boolean step_counter_found = false;
    @Override
    public void onResume() {
        super.onResume();
        step_counter_found = false;
        stepCount = 0;
        try {
//            if(cb_dead_reckoning_navigation.isChecked()) {
//                Sensor sSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//                if (sSensor != null) {
//                    sensorManager.registerListener(mySensorListener, sSensor, SensorManager.SENSOR_DELAY_FASTEST);
//                    step_counter_found = true;
//                } else {
////                ToastUtils.setResultToToast("No Step Counter Sensor Detected");
//                    MainActivity.MyLogInfo("No Step Counter Sensor Detected");
//                }
//
//                Sensor dSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
//                if (sSensor != null) {
//                    sensorManager.registerListener(mySensorListener, dSensor, SensorManager.SENSOR_DELAY_FASTEST);
//                    step_counter_found = true;
//                } else {
////                ToastUtils.setResultToToast("No Step Detector Sensor Detected");
//                    MainActivity.MyLogInfo("No Step Detector Sensor Detected");
//                }
//            }

            Sensor aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (aSensor != null) {
                sensorManager.registerListener(mySensorListener, aSensor, SensorManager.SENSOR_DELAY_FASTEST);
            } else {
//                ToastUtils.setResultToToast("No Accelerator Sensor Detected");
                MainActivity.MyLogInfo("No Accelerator Sensor Detected");
            }

            Sensor mfSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            if (mfSensor != null) {
                sensorManager.registerListener(mySensorListener, mfSensor, SensorManager.SENSOR_DELAY_FASTEST);
            } else {
//                ToastUtils.setResultToToast("No Magnetic Field Sensor Detected");
                MainActivity.MyLogInfo("No Magnetic Field Sensor Detected");
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    @Override
    public void onPause() {
        try {
            sensorManager.unregisterListener(mySensorListener);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        try {
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        super.onDestroy();
    }

//    private class MyLocationListener implements LocationListener {
//
//        @Override
//        public void onLocationChanged(Location loc) {
//            try {
//                if(cb_cam_pos.isChecked()) {
//                    MainActivity.uav_lon = loc.getLongitude();
//                    MainActivity.uav_lat = loc.getLatitude();
//                    MainActivity.uav_alt_above_ground = loc.getAltitude();
//                    MainActivity.uav_ground_alt = MainActivity.GetHeightJNI(MainActivity.uav_lon,MainActivity.uav_lat);
//                    MainActivity.uav_alt = MainActivity.uav_ground_alt + MainActivity.uav_alt_above_ground;
//
//                    et_lon.setText(String.format(Locale.ENGLISH,"%.06f", MainActivity.uav_lon));
//                    et_lat.setText(String.format(Locale.ENGLISH,"%.06f", MainActivity.uav_lat));
//                    et_alt.setText(String.format(Locale.ENGLISH,"%.01f", MainActivity.uav_alt));
//                    MainActivity.tab_map.set_cam_pos(MainActivity.uav_lon,MainActivity.uav_lat,MainActivity.uav_alt,true);
//                    MainActivity.tab_camera.update_status();
//                }
//            } catch (Throwable ex) {
//                MainActivity.MyLog(ex);
//            }
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {}
//
//        @Override
//        public void onProviderEnabled(String provider) {}
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {}
//    }

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];
    private boolean bAcc = false;
    private boolean bMag = false;

    float alpha1 = 0.25f;
    float fX1 = 0.0f;
    float fY1 = 0.0f;
    float fZ1 = 0.0f;
    float X1,Y1,Z1;

    float alpha2 = 0.25f;
    float fX2 = 0.0f;
    float fY2 = 0.0f;
    float fZ2 = 0.0f;
    float X2,Y2,Z2;

    float alpha3 = 0.25f;
    float fX3 = 0.0f;
    float fY3 = 0.0f;
    float fZ3 = 0.0f;
    float X3,Y3,Z3;

    private float[] lowPassFilter(float[] input, float[] prev) {
        float ALPHA = 0.1f;
        if(input == null || prev == null) {
            return null;
        }
        for (int i=0; i< input.length; i++) {
            prev[i] = prev[i] + ALPHA * (input[i] - prev[i]);
        }
        return prev;
    }

    private float[] prev = {0f,0f,0f};
    private int stepCount = 0;
    private static final int ABOVE = 1;
    private static final int BELOW = 0;
    private static int CURRENT_STATE = 0;
    private static int PREVIOUS_STATE = BELOW;
    private long streakStartTime;
    private long streakPrevTime;
    double uav_lon0 = 0;
    double uav_lat0 = 0;
    private void handleEvent(SensorEvent event) {
        float threshold = 9.50f;// 10.5f
        prev = lowPassFilter(event.values,prev);
//        Accelerometer raw = new Accelerometer(event.values);
        Accelerometer data = new Accelerometer(prev);
//        StringBuilder text = new StringBuilder();
//        text.append("X: " + data.X);
//        text.append("Y: " + data.Y);
//        text.append("Z: " + data.Z);
//        text.append("R: " + data.R);
//        rawData.appendData(new DataPoint(rawPoints++,raw.R), true,1000);
//        lpData.appendData(new DataPoint(rawPoints, data.R), true, 1000);
        if(data.R > threshold)
        {
            CURRENT_STATE = ABOVE;
            if(PREVIOUS_STATE != CURRENT_STATE)
            {
                streakStartTime = System.currentTimeMillis();
                if ((streakStartTime - streakPrevTime) <= 250f) {
                    streakPrevTime = System.currentTimeMillis();
                    return;
                }
                streakPrevTime = streakStartTime;
                stepCount++;
                double temp = stepCount * 0.001f;

                if((uav_lon0 == 0) || (uav_lat0 == 0))
                {
                    uav_lon0 = MainActivity.uav_lon;
                    uav_lat0 = MainActivity.uav_lat;
                }

                MainActivity.uav_lon = uav_lon0 + temp * Math.sin(Math.toRadians(fYaw));
                MainActivity.uav_lat = uav_lat0 + temp * Math.cos(Math.toRadians(fYaw));
                MainActivity.uav_alt = 0;
                MainActivity.tab_camera.update_status(true);
//                MainActivity.activity.show_toast("Step: "+stepCount);
            }
            PREVIOUS_STATE = CURRENT_STATE;
        }
        else if(data.R < threshold)
        {
            CURRENT_STATE = BELOW;
            PREVIOUS_STATE = CURRENT_STATE;
        }
//        stepView.setText(""+(stepCount));
    }

    final SensorEventListener mySensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {
//            if(cb_dead_reckoning_navigation.isChecked()) {
//                if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
////                    stepCount = (int) sensorEvent.values[0];
////                    double temp = stepCount * 0.001f;
////
////                    if((uav_lon0 == 0) || (uav_lat0 == 0))
////                    {
////                        uav_lon0 = MainActivity.uav_lon;
////                        uav_lat0 = MainActivity.uav_lat;
////                    }
////
////                    MainActivity.uav_lon = uav_lon0 + temp * Math.sin(Math.toRadians(fYaw));
////                    MainActivity.uav_lat = uav_lat0 + temp * Math.cos(Math.toRadians(fYaw));
////                    MainActivity.uav_alt = 0;
////                    MainActivity.tab_camera.update_status(true);
////                    ToastUtils.setResultToToast("Step: "+stepCount);
//                }
//
//                if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
//                    stepCount++;
//                    double temp = 0.001f;
//
//                    if((uav_lon0 == 0) || (uav_lat0 == 0))
//                    {
//                        uav_lon0 = MainActivity.uav_lon;
//                        uav_lat0 = MainActivity.uav_lat;
//                    }
//
//                    MainActivity.uav_lon = uav_lon0 + temp * Math.sin(Math.toRadians(fYaw));
//                    MainActivity.uav_lat = uav_lat0 + temp * Math.cos(Math.toRadians(fYaw));
//                    MainActivity.uav_alt = 0;
//                    MainActivity.tab_camera.update_status(true);
////                    MainActivity.activity.show_toast("Step: "+stepCount);
//                }
//            }

            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                //Low Pass Filter
                X1 = sensorEvent.values[0];
                Y1 = sensorEvent.values[1];
                Z1 = sensorEvent.values[2];
                fX1 = X1 * alpha1 + (fX1 * (1.0f - alpha1));
                fY1 = Y1 * alpha1 + (fY1 * (1.0f - alpha1));
                fZ1 = Z1 * alpha1 + (fZ1 * (1.0f - alpha1));
                accelerometerValues[0] = fX1;
                accelerometerValues[1] = fY1;
                accelerometerValues[2] = fZ1;

                bAcc = true;
                calculateRemappedOrientation();
//                if(cb_dead_reckoning_navigation.isChecked())
//                {
//                    if(step_counter_found)  handleEvent(sensorEvent);
//                }
            }

            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                //Low Pass Filter
                X2 = sensorEvent.values[0];
                Y2 = sensorEvent.values[1];
                Z2 = sensorEvent.values[2];
                fX2 = X2 * alpha2 + (fX2 * (1.0f - alpha2));
                fY2 = Y2 * alpha2 + (fY2 * (1.0f - alpha2));
                fZ2 = Z2 * alpha2 + (fZ2 * (1.0f - alpha2));
                magneticFieldValues[0] = fX2;
                magneticFieldValues[1] = fY2;
                magneticFieldValues[2] = fZ2;

                if(cb_meta_detection.isChecked()) {
                    metal_power = Math.round(Math.sqrt(Math.pow(fX2, 2) + Math.pow(fY2, 2) + Math.pow(fZ2, 2)));
                    et_metal_power.setText(String.format(Locale.ENGLISH,"%d", metal_power));
                    metal_found = false;
                    double metal_threshold = 0;
                    try {
                        metal_threshold = mv_utils.parseDouble(et_metal_threshold.getText().toString());
                    }
                    catch (Exception ignored)
                    {

                    }

                    if (metal_power >= metal_threshold)
                    {
                        metal_found = true;
                        Vibrator vibrator = (Vibrator)MainActivity.activity.getSystemService(Context.VIBRATOR_SERVICE);
                        assert vibrator != null;
                        vibrator.vibrate(10);
                    }
                }

                bMag = true;
                calculateRemappedOrientation();
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            switch(accuracy)
            {
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
//                    this.accuracy.setText("SENSOR_STATUS_ACCURACY_HIGH");
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
//                    this.accuracy.setText("SENSOR_STATUS_ACCURACY_MEDIUM");
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
//                    this.accuracy.setText("SENSOR_STATUS_ACCURACY_LOW");
                    break;
                case SensorManager.SENSOR_STATUS_UNRELIABLE:
//                    this.accuracy.setText("SENSOR_STATUS_UNRELIABLE");
                    break;
            }
        }
    };

    private void calculateRemappedOrientation() {
        try {
            float[] inR = new float[9];
            float[] outR = new float[9];
            float[] values = new float[3];
            if(bAcc && bMag) {
                if(!SensorManager.getRotationMatrix(inR, null, accelerometerValues, magneticFieldValues))   return;
/*
                int screenRotation = Surface.ROTATION_0;
                screenRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                int axisX, axisY;
                boolean isUpSideDown = accelerometerValues[2] < 0;

                switch (screenRotation) {
                    case Surface.ROTATION_0:
                        axisX = (isUpSideDown ? SensorManager.AXIS_MINUS_X : SensorManager.AXIS_X);
                        axisY = (Math.abs(accelerometerValues[1]) > 6.0f ?
                                (isUpSideDown ? SensorManager.AXIS_MINUS_Z : SensorManager.AXIS_Z) :
                                (isUpSideDown ? SensorManager.AXIS_MINUS_Y : SensorManager.AXIS_Y));
                        break;
                    case Surface.ROTATION_90:
                        axisX = (isUpSideDown ? SensorManager.AXIS_MINUS_Y : SensorManager.AXIS_Y);
                        axisY = (Math.abs(accelerometerValues[0]) > 6.0f ?
                                (isUpSideDown ? SensorManager.AXIS_Z : SensorManager.AXIS_MINUS_Z) :
                                (isUpSideDown ? SensorManager.AXIS_X : SensorManager.AXIS_MINUS_X));
                        break;
                    case  Surface.ROTATION_180:
                        axisX = (isUpSideDown ? SensorManager.AXIS_X : SensorManager.AXIS_MINUS_X);
                        axisY = (Math.abs(accelerometerValues[1]) > 6.0f ?
                                (isUpSideDown ? SensorManager.AXIS_Z : SensorManager.AXIS_MINUS_Z) :
                                (isUpSideDown ? SensorManager.AXIS_Y : SensorManager.AXIS_MINUS_Y));
                        break;
                    case Surface.ROTATION_270:
                        axisX = (isUpSideDown ? SensorManager.AXIS_Y : SensorManager.AXIS_MINUS_Y);
                        axisY = (Math.abs(accelerometerValues[0]) > 6.0f ?
                                (isUpSideDown ? SensorManager.AXIS_MINUS_Z : SensorManager.AXIS_Z) :
                                (isUpSideDown ? SensorManager.AXIS_MINUS_X : SensorManager.AXIS_X));
                        break;
                    default:
                        axisX = (isUpSideDown ? SensorManager.AXIS_MINUS_X : SensorManager.AXIS_X);
                        axisY = (isUpSideDown ? SensorManager.AXIS_MINUS_Y : SensorManager.AXIS_Y);
                }

                SensorManager.remapCoordinateSystem(inR, axisX, axisY, outR);
*/
                SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X, SensorManager.AXIS_Z, outR);// ROTATION_0
//                SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, outR);// ROTATION_0

                // Obtain the new, remapped, orientation values.
                SensorManager.getOrientation(outR, values);

//                RotateAnimation ra = new RotateAnimation(currentDegree, azimut, self, 0.5f,
//                        self, 0.5f);
//                ra.setDuration(1000);
//                ra.setFillAfter(true);
//                arrow.startAnimation(ra);

                int screenRotation = MainActivity.activity.getWindowManager().getDefaultDisplay().getRotation();
                switch (screenRotation) {
                    case Surface.ROTATION_0:
                        X3 = (float) Math.toDegrees(values[0]); // Azimuth
                        Y3 = -(float) Math.toDegrees(values[1]); // Pitch
                        Z3 = (float) Math.toDegrees(values[2]); // Roll
                        break;
                    case Surface.ROTATION_90:
                        X3 = (float) Math.toDegrees(values[0]); // Azimuth
                        Y3 = -(float) Math.toDegrees(values[1]); // Pitch
                        Z3 = (float) Math.toDegrees(values[2]) + 90.0f; // Roll
                        break;
                    case Surface.ROTATION_180:
                        X3 = (float) Math.toDegrees(values[0]); // Azimuth
                        Y3 = -(float) Math.toDegrees(values[1]); // Pitch
                        Z3 = (float) Math.toDegrees(values[2]) - 180.0f; // Roll
                        break;
                    case Surface.ROTATION_270:
                        X3 = (float) Math.toDegrees(values[0]); // Azimuth
                        Y3 = -(float) Math.toDegrees(values[1]); // Pitch
                        Z3 = (float) Math.toDegrees(values[2]) - 90.0f; // Roll
                        break;
                }

                //Low Pass Filter
                fX3 = X3 * alpha3 + (fX3 * (1.0f - alpha3));
                fY3 = Y3 * alpha3 + (fY3 * (1.0f - alpha3));
                fZ3 = Z3 * alpha3 + (fZ3 * (1.0f - alpha3));
                fYawEnc = fX3;
                fPitchEnc = fY3;
                fRollEnc = fZ3;

                fYaw = fYawEnc + dDeviceYaw;
                fPitch = fPitchEnc + dDevicePitch;
                fRoll = fRollEnc + dDeviceRoll;

                // Dead Reckoning Navigation
//                if(cb_dead_reckoning_navigation.isChecked()) {
//                    MainActivity.uav_yaw = fYaw;
//                    MainActivity.uav_pitch = 0;
//                    MainActivity.uav_roll = 0;
//
//                    MainActivity.gimb_yaw = 0;
//                    MainActivity.gimb_pitch = 0;
//                    MainActivity.gimb_roll = 0;
//
//                    MainActivity.image_yaw_enc = (float) MainActivity.db_deg(MainActivity.uav_yaw + MainActivity.gimb_yaw);
//                    MainActivity.image_pitch_enc = MainActivity.uav_pitch + MainActivity.gimb_pitch;
//                    MainActivity.image_roll_enc = MainActivity.uav_roll + MainActivity.gimb_roll;
//
//                    MainActivity.image_yaw = (float) MainActivity.db_deg(MainActivity.image_yaw_enc + MainActivity.dYaw);
//                    MainActivity.image_pitch = MainActivity.image_pitch_enc + MainActivity.dPitch;
//                    MainActivity.image_roll = MainActivity.image_roll_enc + MainActivity.dRoll;
//
//                    MainActivity.tab_camera.update_status(true);
//                }

                if(cb_cam_angles.isChecked()) {
                    if(MainActivity.bNavigation) {
                        MainActivity.image_yaw = fYaw;
                        MainActivity.image_pitch = fPitch;
                        MainActivity.image_roll = fRoll;

                        et_yaw.setText(String.format(Locale.ENGLISH,"%.02f", fYaw));
                        et_pitch.setText(String.format(Locale.ENGLISH,"%.02f", fPitch));
                        et_roll.setText(String.format(Locale.ENGLISH,"%.02f", fRoll));
//                    Tab_Camera.crosshairView.invalidate();
//                    MainActivity.tab_map.set_camera_azi(fYaw,true);
//                    Tab_Main.server.SendBinary_ex(Server.MV_CMD_AZI_ELE, fYaw, fPitch);
//                    MainActivity.tab_sensors.mServer.sendBinary(new User("Server", "", mServer.commandToBuffer(TcpServer.MV_CMD_AZI_ELE, MainActivity.image_yaw, MainActivity.image_pitch)));
                    }else{
                        if((MainActivity.tab_camera.cb_imu != null) && (!MainActivity.tab_camera.cb_imu.isChecked())) {
                            MainActivity.image_yaw = fYaw;
                            MainActivity.image_pitch = fPitch;
                            MainActivity.image_roll = fRoll;

                            et_yaw.setText(String.format(Locale.ENGLISH,"%.02f", fYaw));
                            et_pitch.setText(String.format(Locale.ENGLISH,"%.02f", fPitch));
                            et_roll.setText(String.format(Locale.ENGLISH,"%.02f", fRoll));
//                    Tab_Camera.crosshairView.invalidate();
//                    MainActivity.tab_map.set_camera_azi(fYaw,true);
//                    Tab_Main.server.SendBinary_ex(Server.MV_CMD_AZI_ELE, fYaw, fPitch);
//                    MainActivity.tab_sensors.mServer.sendBinary(new User("Server", "", mServer.commandToBuffer(TcpServer.MV_CMD_AZI_ELE, MainActivity.image_yaw, MainActivity.image_pitch)));
                        }
                    }
                }
            }
        }
        catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }
}
