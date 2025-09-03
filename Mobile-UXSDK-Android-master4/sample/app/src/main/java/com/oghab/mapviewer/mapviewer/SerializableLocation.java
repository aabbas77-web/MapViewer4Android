package com.oghab.mapviewer.mapviewer;

import android.location.Location;
import android.location.LocationManager;
import android.os.Build;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class SerializableLocation implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final int HAS_ALTITUDE_MASK                      = 1;
    private static final int HAS_SPEED_MASK                         = 2;
    private static final int HAS_BEARING_MASK                       = 4;
    private static final int HAS_HORIZONTAL_ACCURACY_MASK           = 8;
    private static final int HAS_MOCK_PROVIDER_MASK                 = 16;
    private static final int HAS_VERTICAL_ACCURACY_MASK             = 32;
    private static final int HAS_SPEED_ACCURACY_MASK                = 64;
    private static final int HAS_BEARING_ACCURACY_MASK              = 128;
    private static final int HAS_ELAPSED_REALTIME_UNCERTAINTY_MASK  = 256;

    private String provider;
    private long   time                             = 0;
    private long   elapsedRealtimeNanos             = 0;
    private double elapsedRealtimeUncertaintyNanos  = 0.0f;
    private double latitude                         = 0.0;
    private double longitude                        = 0.0;
    private double altitude                         = 0.0f;
    private float  speed                            = 0.0f;
    private float  bearing                          = 0.0f;
    private float  horizontalAccuracyMeters         = 0.0f;
    private float  verticalAccuracyMeters           = 0.0f;
    private float  speedAccuracyMetersPerSecond     = 0.0f;
    private float  bearingAccuracyDegrees           = 0.0f;
    private int    fieldsMask                       = 0;
//  private Bundle extras = null;

    private boolean hasElapsedRealtimeUncertaintyNanos() {
        return (fieldsMask & HAS_ELAPSED_REALTIME_UNCERTAINTY_MASK) != 0;
    }

    private boolean hasAltitude() {
        return (fieldsMask & HAS_ALTITUDE_MASK) != 0;
    }

    private boolean hasSpeed() {
        return (fieldsMask & HAS_SPEED_MASK) != 0;
    }

    private boolean hasBearing() {
        return (fieldsMask & HAS_BEARING_MASK) != 0;
    }

    private boolean hasAccuracy() {
        return (fieldsMask & HAS_HORIZONTAL_ACCURACY_MASK) != 0;
    }

    private boolean hasVerticalAccuracy() {
        return (fieldsMask & HAS_VERTICAL_ACCURACY_MASK) != 0;
    }

    private boolean hasSpeedAccuracy() {
        return (fieldsMask & HAS_SPEED_ACCURACY_MASK) != 0;
    }

    private boolean hasBearingAccuracy() {
        return (fieldsMask & HAS_BEARING_ACCURACY_MASK) != 0;
    }

    private boolean isFromMockProvider() {
        return (fieldsMask & HAS_MOCK_PROVIDER_MASK) != 0;
    }

    public SerializableLocation(@NonNull Location l) {

        provider             = l.getProvider();
        time                 = l.getTime();
        elapsedRealtimeNanos = l.getElapsedRealtimeNanos();
        latitude             = l.getLatitude();
        longitude            = l.getLongitude();

        if (l.hasAltitude()) {
            altitude = l.getAltitude();
            fieldsMask |= HAS_ALTITUDE_MASK;
        }
        if (l.hasSpeed()) {
            speed = l.getSpeed();
            fieldsMask |= HAS_SPEED_MASK;
        }
        if (l.hasBearing()) {
            bearing = l.getBearing();
            fieldsMask |= HAS_BEARING_MASK;
        }
        if (l.hasAccuracy()) {
            horizontalAccuracyMeters = l.getAccuracy();
            fieldsMask |= HAS_HORIZONTAL_ACCURACY_MASK;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (l.hasVerticalAccuracy()) {
                verticalAccuracyMeters = l.getVerticalAccuracyMeters();
                fieldsMask |= HAS_VERTICAL_ACCURACY_MASK;
            }
            if (l.hasSpeedAccuracy()) {
                speedAccuracyMetersPerSecond =
                        l.getSpeedAccuracyMetersPerSecond();
                fieldsMask |= HAS_SPEED_ACCURACY_MASK;
            }
            if (l.hasBearingAccuracy()) {
                bearingAccuracyDegrees = l.getBearingAccuracyDegrees();
                fieldsMask |= HAS_BEARING_ACCURACY_MASK;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            if (l.hasElapsedRealtimeUncertaintyNanos()) {
                elapsedRealtimeUncertaintyNanos =
                        l.getElapsedRealtimeUncertaintyNanos();
                fieldsMask |= HAS_ELAPSED_REALTIME_UNCERTAINTY_MASK;
            }
        }

        if (l.isFromMockProvider()) {
            fieldsMask |= HAS_MOCK_PROVIDER_MASK;
        }
    }

    public Location toLocation() {

        Location l = new Location(provider);

        l.setTime(time);
        l.setElapsedRealtimeNanos(elapsedRealtimeNanos);
        l.setLatitude(latitude);
        l.setLongitude(longitude);

        if (hasAltitude()) {
            l.setAltitude(altitude);
        }
        if (hasSpeed()) {
            l.setSpeed(speed);
        }
        if (hasBearing()) {
            l.setBearing(bearing);
        }
        if (hasAccuracy()) {
            l.setAccuracy(horizontalAccuracyMeters);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (hasVerticalAccuracy()) {
                l.setVerticalAccuracyMeters(verticalAccuracyMeters);
            }
            if (hasSpeedAccuracy()) {
                l.setSpeedAccuracyMetersPerSecond(speedAccuracyMetersPerSecond);
            }
            if (hasBearingAccuracy()) {
                l.setBearingAccuracyDegrees(bearingAccuracyDegrees);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            if (hasElapsedRealtimeUncertaintyNanos()) {
                l.setElapsedRealtimeUncertaintyNanos(
                        elapsedRealtimeUncertaintyNanos
                );
            }
        }

//        l.setIsFromMockProvider(isFromMockProvider());

        return l;
    }

    static public Location getLocation(double lon, double lat, double alt, float speed, float bearing) {

        Location l = new Location(LocationManager.GPS_PROVIDER);

//        Date now = Calendar.getInstance().getTime();
//        l.setTime(now.getTime());
        l.setTime(System.currentTimeMillis());
        l.setElapsedRealtimeNanos(android.os.SystemClock.elapsedRealtime()*1000);
        l.setLatitude(lat);
        l.setLongitude(lon);

//        if (hasAltitude()) {
            l.setAltitude(alt);
//        }
//        if (hasSpeed()) {
            l.setSpeed(speed);
//        }
//        if (hasBearing()) {
            l.setBearing(bearing);
//        }
//        if (hasAccuracy()) {
            l.setAccuracy(0.0f);
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

//            if (hasVerticalAccuracy()) {
                l.setVerticalAccuracyMeters(0.0f);
//            }
//            if (hasSpeedAccuracy()) {
                l.setSpeedAccuracyMetersPerSecond(0.0f);
//            }
//            if (hasBearingAccuracy()) {
                l.setBearingAccuracyDegrees(0.0f);
//            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

//            if (hasElapsedRealtimeUncertaintyNanos()) {
                l.setElapsedRealtimeUncertaintyNanos(
                        0.0
                );
//            }
        }

//        l.setIsFromMockProvider(isFromMockProvider());

        return l;
    }
}
