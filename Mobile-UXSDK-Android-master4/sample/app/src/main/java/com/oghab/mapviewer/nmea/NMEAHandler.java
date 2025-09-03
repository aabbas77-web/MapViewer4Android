package com.oghab.mapviewer.nmea;

import android.location.Location;

import java.util.List;

public interface NMEAHandler {
    void onStart();

    void onLocation(Location location);

    void onSatellites(List<GpsSatellite> satellites);

    void onUnrecognized(String sentence);

    void onBadChecksum(int expected, int actual);

    void onException(Exception e);

    void onFinish();
}
