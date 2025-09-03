package com.oghab.mapviewer.mapviewer;

import android.location.Location;

public interface ImvLocationConsumer {
	/**
	 * Call when a provider has a new location to consume. This can be called on any thread.
	 */
	void onLocationChanged(Location location, ImvLocationProvider source);
}
