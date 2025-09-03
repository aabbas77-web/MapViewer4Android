package com.oghab.mapviewer.mapviewer;

import org.osmdroid.api.IMapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.Priority;
import com.oghab.mapviewer.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import net.sf.marineapi.nmea.io.AbstractDataReader;
import net.sf.marineapi.nmea.io.ExceptionListener;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.parser.GLLParser;
import net.sf.marineapi.nmea.parser.PositionParser;
import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.parser.SentenceParser;
import net.sf.marineapi.nmea.parser.UnsupportedSentenceException;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.GSASentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceValidator;
import net.sf.marineapi.nmea.util.Position;
import net.sf.marineapi.provider.PositionProvider;
import net.sf.marineapi.provider.event.PositionEvent;
import net.sf.marineapi.provider.event.PositionListener;

/**
 * location provider, by default, uses {@link LocationManager#GPS_PROVIDER} and {@link LocationManager#NETWORK_PROVIDER}
 */
public class GpsmvLocationProvider implements ImvLocationProvider, LocationListener, PositionListener, ExceptionListener {
	private static final String TAG = "GpsmvLocationProvider";
	int LOCATION_REQUEST_CODE = 10001;
	private LocationManager mLocationManager = null;
	static private Location mLocation = null;
	static public GnssStatus curr_status = null;

	private ImvLocationConsumer mMyLocationConsumer = null;
	private long mLocationUpdateMinTime = 0;
	private float mLocationUpdateMinDistance = 0.0f;
	//	private NetworkLocationIgnorer mIgnorer = new NetworkLocationIgnorer();
	private final Set<String> locationSources = new HashSet<>();

	FusedLocationProviderClient fusedLocationProviderClient;
	LocationRequest locationRequest;

	LocationCallback locationCallback = new LocationCallback() {
		@Override
		public void onLocationResult(LocationResult locationResult) {
			if (locationResult == null) {
				return;
			}
			for (Location location : locationResult.getLocations()) {
//				Log.d(TAG, "onLocationResult: " + location.toString());
				onLocationChanged(location);
			}
		}
	};

	Context ctx = null;

	public GpsmvLocationProvider(Context context) {
		ctx = context;

		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		locationSources.add(LocationManager.GPS_PROVIDER);

//		locationSources.add(LocationManager.NETWORK_PROVIDER);
//		locationSources.add(LocationManager.PASSIVE_PROVIDER);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//			locationSources.add(LocationManager.FUSED_PROVIDER);
//		}

		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ctx);
		locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
				.setWaitForAccurateLocation(false)
				.setMinUpdateIntervalMillis(500)
				.setMaxUpdateDelayMillis(1000)
				.setMinUpdateDistanceMeters(0)
				.build();

		reader.setExceptionListener(this);
		provider = new PositionProvider(reader);
		provider.addListener(this);
		reader.start();


//		String nmeaSentence = "$GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,*47";
//		SentenceParser parser = new SentenceParser(nmeaSentence);
//		GGASentence ggaSentence = (GGASentence) parser.parse(nmeaSentence);
//		Position position = ggaSentence.getPosition();
//		System.out.println("Latitude: " + position.getLatitude());
//		System.out.println("Longitude: " + position.getLongitude());

//		String nmea = "$GPGSA,A,3,03,05,07,08,10,15,18,19,21,28,,,1.4,0.9,1.1*3A";
//		SentenceFactory sf = SentenceFactory.getInstance();
//		GSASentence gsa = (GSASentence) sf.createParser(nmea);

//		String nmea = "$GPGSA,A,3,03,05,07,08,10,15,18,19,21,28,,,1.4,0.9,1.1*3A";
//		GLLParser parser = new GLLParser(nmea);
//		Position pos = parser.getPosition();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	/**
	 * removes all sources, again, only useful before startLocationProvider is called
	 */
	public void clearLocationSources() {
		locationSources.clear();
	}

	/**
	 * adds a new source to listen for location data. Has no effect after startLocationProvider has been called
	 * unless startLocationProvider is called again
	 */
	public void addLocationSource(String source) {
		locationSources.add(source);
	}

	/**
	 * returns the live list of GPS sources that we accept, changing this list after startLocationProvider
	 * has no effect unless startLocationProvider is called again
	 * @return
	 */
	public Set<String> getLocationSources() {
		return locationSources;
	}

	public long getLocationUpdateMinTime() {
		return mLocationUpdateMinTime;
	}

	/**
	 * Set the minimum interval for location updates. See
	 * {@link LocationManager#requestLocationUpdates(String, long, float, LocationListener)}. Note
	 * that you should call this before calling {@link MyLocationNewOverlay#enableMyLocation()}.
	 *
	 * @param milliSeconds
	 */
	public void setLocationUpdateMinTime(final long milliSeconds) {
		mLocationUpdateMinTime = milliSeconds;
	}

	public float getLocationUpdateMinDistance() {
		return mLocationUpdateMinDistance;
	}

	/**
	 * Set the minimum distance for location updates. See
	 * {@link LocationManager#requestLocationUpdates(String, long, float, LocationListener)}. Note
	 * that you should call this before calling {@link MyLocationNewOverlay#enableMyLocation()}.
	 *
	 * @param meters
	 */
	public void setLocationUpdateMinDistance(final float meters) {
		mLocationUpdateMinDistance = meters;
	}

	//
	// IMyLocationProvider
	//

	/**
	 * Enable location updates and show your current location on the map. By default this will
	 * request location updates as frequently as possible, but you can change the frequency and/or
	 * distance by calling {@link #setLocationUpdateMinTime} and/or {@link
	 * #setLocationUpdateMinDistance} before calling this method.
	 */
	@SuppressLint({"MissingPermission", "NewApi"})
	@Override
	public boolean startLocationProvider(ImvLocationConsumer myLocationConsumer) {
		mMyLocationConsumer = myLocationConsumer;
		boolean result = false;
		if(mLocationManager != null) {
//AliSoft		for (final String provider : mLocationManager.getProviders(true)) {
//		for (final String provider : mLocationManager.getProviders(false)) {
			for (final String provider : mLocationManager.getProviders(true)) {
				if (locationSources.contains(provider)) {
					try {
//						mLocationManager.requestLocationUpdates(provider, mLocationUpdateMinTime, mLocationUpdateMinDistance, this);
						mLocationManager.registerGnssStatusCallback(mStatusCallback, null);
						mLocationManager.addNmeaListener(mMessageListener, null);
						result = true;
					} catch (Throwable ex) {
						Log.e(IMapView.LOGTAG, "Unable to attach listener for location provider " + provider + " check permissions?", ex);
					}
				}
			}
		}
//		else {
			if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
				checkSettingsAndStartLocationUpdates();
			} else {
				askLocationPermission();
			}
			result = true;
//		}

		return result;
	}

	@SuppressLint({"MissingPermission", "NewApi"})
	@Override
	public void stopLocationProvider() {
		mMyLocationConsumer = null;
		if (mLocationManager != null) {
			try {
				mLocationManager.removeUpdates(this);
				mLocationManager.unregisterGnssStatusCallback(mStatusCallback);
				mLocationManager.removeNmeaListener(mMessageListener);
			} catch (Throwable ex) {
				Log.w(IMapView.LOGTAG, "Unable to deattach location listener", ex);
			}
		}
//		else {
			stopLocationUpdates();
//		}
	}

	private void checkSettingsAndStartLocationUpdates() {
		LocationSettingsRequest request = new LocationSettingsRequest.Builder()
				.addLocationRequest(locationRequest).build();
		SettingsClient client = LocationServices.getSettingsClient(ctx);

		Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
		locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
			@Override
			public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
				//Settings of device are satisfied and we can start location updates
				startLocationUpdates();
			}
		});

		locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				if (e instanceof ResolvableApiException) {
					ResolvableApiException apiException = (ResolvableApiException) e;
					try {
						// Show the dialog by calling startResolutionForResult(),
						// and check the result in onActivityResult().
						// IF STATEMENT THAT PREVENTS THE DIALOG FROM PROMPTING.
						// "For better experience, turn on device location, which uses Google's location service."
						 if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
							apiException.startResolutionForResult(MainActivity.activity, 1001);
						 }
					} catch (IntentSender.SendIntentException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
	}

	@SuppressLint("MissingPermission")
	private void startLocationUpdates() {
		fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
	}

	private void stopLocationUpdates() {
		fusedLocationProviderClient.removeLocationUpdates(locationCallback);
	}

	private void getLastLocation() {
		@SuppressLint("MissingPermission") Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
		locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
			@Override
			public void onSuccess(Location location) {
				if (location != null) {
					//We have a location
					Log.d(TAG, "onSuccess: " + location.toString());
					Log.d(TAG, "onSuccess: " + location.getLatitude());
					Log.d(TAG, "onSuccess: " + location.getLongitude());
				} else  {
					Log.d(TAG, "onSuccess: Location was null...");
				}
			}
		});
		locationTask.addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Log.e(TAG, "onFailure: " + e.getLocalizedMessage() );
			}
		});
	}

	private void askLocationPermission() {
		if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
				Log.d(TAG, "askLocationPermission: you should show an alert dialog...");
				ActivityCompat.requestPermissions(MainActivity.activity, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
			} else {
				ActivityCompat.requestPermissions(MainActivity.activity, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == LOCATION_REQUEST_CODE) {
			if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permission granted
//                getLastLocation();
				checkSettingsAndStartLocationUpdates();
			} else {
				//Permission not granted
			}
		}
	}

	@Override
	public Location getLastKnownLocation() {
		return mLocation;
	}

	@Override
	public GnssStatus getLastKnownStatus() {
		return curr_status;
	}

	@Override
	public void destroy() {
		stopLocationProvider();
		mLocation = null;
		mLocationManager = null;
		mMyLocationConsumer = null;
//		mIgnorer = null;
	}

	//
	// LocationListener
	//
	@Override
	public void onLocationChanged(Location location) {
//		Log.v("MapViewer", "onLocationChanged: "+location);
//		if (mIgnorer == null) {
//			Log.w(IMapView.LOGTAG, "GpsMyLocation provider, mIgnore is null, unexpected. Location update will be ignored");
//			return;
//		}
		if ((location == null) || (location.getProvider() == null))
			return;
		// ignore temporary non-gps fix
//AliSoft 2023.08.12
//		if (mIgnorer.shouldIgnore(location.getProvider(), System.currentTimeMillis()))
//			return;

		mLocation = location;
		if (mMyLocationConsumer != null) {
			mMyLocationConsumer.onLocationChanged(mLocation, this);

//			if(MainActivity.tab_map.hsv_sensors.getVisibility() == View.VISIBLE) {
//				float bearing = MainActivity.tab_map.sb_gps_yaw.getProgress();
//				mLocation.setBearing(bearing);
//
//				float speed = MainActivity.tab_map.sb_gps_speed.getProgress();
//				mLocation.setSpeed(speed);
//
//				float alt = MainActivity.tab_map.sb_gps_alt.getProgress();
//				mLocation.setAltitude(alt);
//			}
		}
	}

	//AliSoft 2023.08.12
//	@Override
//	public void onProviderDisabled(final String provider) {
//	}
//
//	@Override
//	public void onProviderEnabled(final String provider) {
//	}
//
//	@Override
//	public void onStatusChanged(final String provider, final int status, final Bundle extras) {
//	}

	/** Report satellite status */
	@SuppressLint("NewApi")
	private GnssStatus.Callback mStatusCallback = new GnssStatus.Callback() {
		//AliSoft 2023.08.12
//		@Override
//		public void onStarted() { }
//
//		@Override
//		public void onStopped() { }
//
//		@Override
//		public void onFirstFix(int ttffMillis) { }

		@Override
		public void onSatelliteStatusChanged(GnssStatus status) {
			curr_status = status;

//			Parcel p = Parcel.obtain();
//			status.writeToParcel(p, 0);
//			p.setDataPosition(0);
//			curr_status = null;
//			try {
//				curr_status = (GnssStatus) status.getClass().getDeclaredConstructor(new Class[]{Parcel.class}).newInstance(p);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}

//			Log.v("MapViewer", "GNSS Status: " + status.getSatelliteCount() + " satellites.");
		}
	};

	// Sleep time between failed read attempts to prevent busy-looping
	private static final int SLEEP_TIME = 100;
	private static final Logger LOGGER = Logger.getLogger(AbstractDataReader.class.getName());

	private SentenceReader reader = new SentenceReader(new AbstractDataReader(){
		@Override
		public String read() throws Exception {
			return null;
		}
	});
	private volatile boolean isRunning = true;
	SentenceFactory factory = SentenceFactory.getInstance();
	PositionProvider provider;

	/** Report raw NMEA messages */
	@SuppressLint("NewApi")
	private OnNmeaMessageListener mMessageListener = new OnNmeaMessageListener() {
		@Override
		public void onNmeaMessage(String message, long timestamp) {
			try{
//				MainActivity.writeToLog(message);
//				Log.v("MapViewer", "NMEA: " + message);

//				GLLParser parser = new GLLParser(message);
//				Position pos = parser.getPosition();
//				Log.v("MapViewer", "NMEA: " + pos.toString());
			}
			catch (Throwable ex)
			{
//				MainActivity.MyLog(ex);
			}

//			Sentence s = factory.createParser(message);
//			reader.fireSentenceEvent(s);

//			try {
//				if (message == null) {
////					Thread.sleep(SLEEP_TIME);
//				} else if (SentenceValidator.isValid(message)) {
//					Log.v("MapViewer", "NMEA1: " + message);
//					Sentence s = factory.createParser(message);
//					reader.fireSentenceEvent(s);
//				} else if (!SentenceValidator.isSentence(message)) {
//					Log.v("MapViewer", "NMEA2: " + message);
//					reader.fireDataEvent(message);
//				} else {
//					Log.v("MapViewer", "NMEA0: " + message);
//				}
//			} catch (UnsupportedSentenceException use) {
//				Log.v("MapViewer", "NMEA3: " + use.getMessage());
//				LOGGER.warning(use.getMessage());
//			} catch (Exception e) {
//				Log.v("MapViewer", "NMEA4: " + e.getMessage());
//				reader.handleException("Data read failed", e);
////				try {
////					Thread.sleep(SLEEP_TIME);
////				} catch (InterruptedException interruptException) {}
//			} finally {
//			}

//			Log.v("MapViewer", "NMEA: " + message);
//			if(mLocation == null) {
//				mLocation = new Location(GPS_PROVIDER);
//				float bearing = MainActivity.tab_map.sb_gps_yaw.getProgress();
//				mLocation.setBearing(bearing);
//
//				float speed = MainActivity.tab_map.sb_gps_speed.getProgress();
//				mLocation.setSpeed(speed);
//
//				float alt = MainActivity.tab_map.sb_gps_alt.getProgress();
//				mLocation.setAltitude(alt);
//				onLocationChanged(mLocation);
//			}
		}
	};

	@Override
	public void onException(Exception e) {

	}

	@Override
	public void providerUpdate(PositionEvent evt) {
		Log.v("MapViewer", "NMEA: " + evt.toString());
	}
}
