package com.oghab.mapviewer.mapviewer;

import android.location.GnssStatus;
import android.location.Location;

import androidx.annotation.NonNull;

public interface ImvLocationProvider {
	boolean startLocationProvider(ImvLocationConsumer myLocationConsumer);

	void stopLocationProvider();

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

    Location getLastKnownLocation();

	GnssStatus getLastKnownStatus();

	void destroy();
}
