package com.oghab.mapviewer.mapviewer;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

//import com.google.android.gms.maps.model.LatLng;
import com.oghab.mapviewer.MainActivity;

import java.util.ArrayList;

import mad.location.manager.lib.Commons.Utils;
import mad.location.manager.lib.Interfaces.LocationServiceInterface;
import mad.location.manager.lib.Services.ServicesHelper;

import mad.location.manager.lib.Commons.Utils;
import mad.location.manager.lib.Interfaces.ILogger;
import mad.location.manager.lib.Interfaces.LocationServiceInterface;
import mad.location.manager.lib.Loggers.GeohashRTFilter;
import mad.location.manager.lib.SensorAux.SensorCalibrator;
import mad.location.manager.lib.Services.KalmanLocationService;
import mad.location.manager.lib.Services.ServicesHelper;
import mad.location.manager.lib.Services.Settings;

public class mv_Kalman implements LocationServiceInterface {
    private final Context m_context;
//    private final GeohashRTFilter m_geoHashRTFilter;
    private final Settings settings;

    public mv_Kalman (Context context) {
        m_context = context;

//        m_geoHashRTFilter = new GeohashRTFilter(8, 2);

        settings = new Settings(Utils.ACCELEROMETER_DEFAULT_DEVIATION,
                Utils.GPS_MIN_DISTANCE,
                Utils.GPS_MIN_TIME,
                Utils.SENSOR_POSITION_MIN_TIME,
                Utils.GEOHASH_DEFAULT_PREC,
                Utils.GEOHASH_DEFAULT_MIN_POINT_COUNT,
                Utils.SENSOR_DEFAULT_FREQ_HZ,
                null, true, false, false, Utils.DEFAULT_VEL_FACTOR, Utils.DEFAULT_POS_FACTOR, Settings.LocationProvider.FUSED);

        ServicesHelper.addLocationServiceInterface(this);
        Log.d("kalman", "init");
    }

    public void start_k () {
        ServicesHelper.getLocationService(m_context, value -> {
            if (value.IsRunning()) {
                return;
            }

            value.stop();
            value.reset(settings); //warning!! here you can adjust your filter behavior

//            m_geoHashRTFilter.reset(null);// ILogger

            value.start();
        });
        Log.d("kalman", "Start");
    }

    public void stop_k () {
        ServicesHelper.getLocationService(m_context, KalmanLocationService::stop);
//        m_geoHashRTFilter.stop();
        Log.d("kalman", "Stop");
    }

    @Override
    public void locationChanged(Location location) {
        if(!MainActivity.bNavigation) return;
//        m_geoHashRTFilter.filter( location );

        if(location == null)    return;
        if(Tab_Map.is_kalman && (!CitiesAdapter.bIsSimulating)) {
            if ((MainActivity.tab_map != null) && (MainActivity.tab_map.mLocationOverlay != null)) {
                GpsmvLocationProvider locationProvider = (GpsmvLocationProvider) MainActivity.tab_map.mLocationOverlay.getMyLocationProvider();
                if(locationProvider != null) {
                    Bundle extraBundle = new Bundle();
                    extraBundle.putBoolean("isMock",true);
                    extraBundle.putBoolean("isKalman",true);
                    extraBundle.putBoolean("isVirtual",false);
                    location.setExtras(extraBundle);

                    locationProvider.onLocationChanged(location);
//                    Log.d("kalman", "location2: " + location);
                }
            }
        }

//        m_geoHashRTFilter.getGeoFilteredTrack();
//        for (Location loc : new ArrayList<>(m_geoHashRTFilter.getGeoFilteredTrack())) {
//            routeFilteredWithGeoHash.add(new LatLng(loc.getLatitude(), loc.getLongitude()));
//        }
    }

    @Override
    public void anglesChanged(float[] angles) {
        if(!MainActivity.bNavigation) return;

        MainActivity.image_yaw_enc = angles[0];
        MainActivity.image_yaw = (float) MainActivity.db_deg(MainActivity.image_yaw_enc + MainActivity.dYaw);

        MainActivity.image_pitch_enc = angles[1];
        MainActivity.image_pitch = MainActivity.image_pitch_enc + MainActivity.dPitch;

        MainActivity.image_roll_enc = angles[2];
        MainActivity.image_roll = MainActivity.image_roll_enc + MainActivity.dRoll;

        if (Tab_Camera.crosshairView != null) Tab_Camera.crosshairView.invalidate();
    }
}
