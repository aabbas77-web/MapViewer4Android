package com.oghab.mapviewer.mapviewer;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.GnssStatus;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.utils.mv_utils;

import org.osmdroid.api.IMapController;
import org.osmdroid.api.IMapView;
import org.osmdroid.config.Configuration;
import org.osmdroid.library.R;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.TileSystem;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.IOverlayMenuProvider;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Overlay.Snappable;

import java.util.LinkedList;

/**
 * 
 * @author Marc Kurtz
 * @author Manuel Stahl
 * 
 */
public class mvLocationNewOverlay extends Overlay implements ImvLocationConsumer,
		IOverlayMenuProvider, Snappable {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected Paint mPaint = new Paint();
	protected Paint mCirclePaint = new Paint();

	protected final float mScale;

	protected Bitmap mPersonBitmap;
	protected Bitmap mDirectionArrowBitmap;

	protected MapView mMapView;

	private IMapController mMapController;
	public ImvLocationProvider mMyLocationProvider;

	private final LinkedList<Runnable> mRunOnFirstFix = new LinkedList<Runnable>();
	private final Point mDrawPixel = new Point();
	private final Point mSnapPixel = new Point();
	private Handler mHandler;
	private Object mHandlerToken = new Object();

	/**
	 * if true, when the user pans the map, follow my location will automatically disable
	 * if false, when the user pans the map, the map will continue to follow current location
	 */
	protected boolean enableAutoStop=true;
	private Location mLocation;
	private final GeoPoint mGeoPoint = new GeoPoint(0.0, 0.0); // for reuse
	private boolean mIsLocationEnabled = false;
	protected boolean mIsFollowing = false; // follow location updates
	protected boolean mDrawAccuracyEnabled = true;

	/** Coordinates the feet of the person are located scaled for display density. */
	protected final PointF mPersonHotspot;

	protected float mDirectionArrowCenterX;
	protected float mDirectionArrowCenterY;

	public static final int MENU_MY_LOCATION = getSafeMenuId();

	private boolean mOptionsMenuEnabled = true;

	private boolean wasEnabledOnPause=false;
	DisplayMetrics dm;
	private TextPaint textPaint;
	String strInfo1 = "";
	String strInfo2 = "";

	protected Point mTarget_Px,mGPS_Px,mGun_Px;
	private Paint p_red;
	private Paint p_blue;
	// ===========================================================
	// Constructors
	// ===========================================================

	public mvLocationNewOverlay(MapView mapView) {
		this(new GpsmvLocationProvider(mapView.getContext()), mapView);
	}

	public mvLocationNewOverlay(ImvLocationProvider myLocationProvider, MapView mapView) {
		super();
		mScale = mapView.getContext().getResources().getDisplayMetrics().density;

		// Get the display metrics
		Resources resources = mapView.getContext().getResources();
		dm = resources.getDisplayMetrics();

		mMapView = mapView;
		mMapController = mapView.getController();
		mCirclePaint.setARGB(0, 100, 100, 255);
		mCirclePaint.setAntiAlias(true);
		mPaint.setFilterBitmap(true);
		mPaint.setStrokeWidth(3);
		mPaint.setColor(Color.RED);
		mPaint.setAlpha(192);

		mTarget_Px = new Point(0,0);
		mGPS_Px = new Point(0,0);
		mGun_Px = new Point(0,0);

		p_red = new Paint();
		p_red.setColor(0xFFCC0000);  // alpha.r.g.b
		p_red.setStyle(Paint.Style.FILL_AND_STROKE);
		p_red.setStrokeWidth(5.0F);
		p_red.setAntiAlias(true);
		p_red.setStrokeCap(Paint.Cap.BUTT);
		p_red.setDither(false);

		p_blue = new Paint();
		p_blue.setColor(0xFF0000CC);  // alpha.r.g.b
		p_blue.setStyle(Paint.Style.FILL_AND_STROKE);
		p_blue.setStrokeWidth(5.0F);
		p_blue.setAntiAlias(true);
		p_blue.setStrokeCap(Paint.Cap.BUTT);
		p_blue.setDither(false);

		textPaint = new TextPaint();
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(dm.density * Tab_Map.map_text_size);
		textPaint.setColor(Color.GREEN);

		setDirectionArrow(((BitmapDrawable) mv_utils.getDrawable(MainActivity.ctx,R.drawable.person)).getBitmap(),
				((BitmapDrawable)mv_utils.getDrawable(MainActivity.ctx,R.drawable.twotone_navigation_black_48)).getBitmap());

		// Calculate position of person icon's feet, scaled to screen density
		mPersonHotspot = new PointF(24.0f * mScale + 0.5f, 39.0f * mScale + 0.5f);

		mHandler = new Handler(Looper.getMainLooper());
		setMyLocationProvider(myLocationProvider);
		enableMyLocation();
	}

	/**
	 * fix for https://github.com/osmdroid/osmdroid/issues/249
	 * @param personBitmap
	 * @param directionArrowBitmap
     */
	public void setDirectionArrow(final Bitmap personBitmap, final Bitmap directionArrowBitmap){
		this.mPersonBitmap = personBitmap;
		this.mDirectionArrowBitmap=directionArrowBitmap;

		mDirectionArrowCenterX = mDirectionArrowBitmap.getWidth() / 2.0f - 0.5f;
		mDirectionArrowCenterY = mDirectionArrowBitmap.getHeight() / 2.0f - 0.5f;

	}

	@Override
	public void onResume(){
		super.onResume();
		if (wasEnabledOnPause)
			this.enableFollowLocation();
		this.enableMyLocation();
	}
	@Override
	public void onPause(){
		wasEnabledOnPause=mIsFollowing;
		this.disableMyLocation();
		super.onPause();
	}

	@Override
	public void onDetach(MapView mapView) {
		this.disableMyLocation();
		/*if (mPersonBitmap != null) {
			mPersonBitmap.recycle();
		}
		if (mDirectionArrowBitmap != null) {
			mDirectionArrowBitmap.recycle();
		}*/
		this.mMapView = null;
		this.mMapController = null;
		mHandler = null;
		mCirclePaint = null;
		//mPersonBitmap = null;
		//mDirectionArrowBitmap = null;
		mHandlerToken = null;
		mLocation = null;
		mMapController = null;
		if (mMyLocationProvider!=null)
			mMyLocationProvider.destroy();

		mMyLocationProvider = null;
		super.onDetach(mapView);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	/**
	 * If enabled, an accuracy circle will be drawn around your current position.
	 * 
	 * @param drawAccuracyEnabled
	 *            whether the accuracy circle will be enabled
	 */
	public void setDrawAccuracyEnabled(final boolean drawAccuracyEnabled) {
		mDrawAccuracyEnabled = drawAccuracyEnabled;
	}

	/**
	 * If enabled, an accuracy circle will be drawn around your current position.
	 * 
	 * @return true if enabled, false otherwise
	 */
	public boolean isDrawAccuracyEnabled() {
		return mDrawAccuracyEnabled;
	}

	public ImvLocationProvider getMyLocationProvider() {
		return mMyLocationProvider;
	}

	protected void setMyLocationProvider(ImvLocationProvider myLocationProvider) {
		if (myLocationProvider == null)
			throw new RuntimeException(
					"You must pass an IMyLocationProvider to setMyLocationProvider()");

		if (isMyLocationEnabled())
			stopLocationProvider();

		mMyLocationProvider = myLocationProvider;
	}

	public void setPersonHotspot(float x, float y) {
		mPersonHotspot.set(x, y);
	}

	public void setInfo1(String text) {
		try
		{
			strInfo1 = text;
		}
		catch (Throwable ex)
		{
			MainActivity.MyLog(ex);
		}
	}

	public String getInfo1() {
		return strInfo1;
	}

	public void setInfo2(String text) {
		try
		{
			strInfo2 = text;
		}
		catch (Throwable ex)
		{
			MainActivity.MyLog(ex);
		}
	}

	public String getInfo2() {
		return strInfo2;
	}

	@SuppressLint("NewApi")
	protected void drawMyLocation(final Canvas canvas, final Projection pj, final Location lastFix) {
		pj.toPixels(mGeoPoint, mDrawPixel);

		if (mDrawAccuracyEnabled) {
			final float radius = lastFix.getAccuracy()
					/ (float) TileSystem.GroundResolution(lastFix.getLatitude(),
							pj.getZoomLevel());

			mCirclePaint.setAlpha(50);
			mCirclePaint.setStyle(Style.FILL);
			canvas.drawCircle(mDrawPixel.x, mDrawPixel.y, radius, mCirclePaint);

			mCirclePaint.setAlpha(150);
			mCirclePaint.setStyle(Style.STROKE);
			canvas.drawCircle(mDrawPixel.x, mDrawPixel.y, radius, mCirclePaint);
		}

		if (lastFix.hasBearing()) {
			// draw lines between gps and target/gun
			Location location = mv_LocationOverlay.curr_location;
			if(location != null) {
				// gps location
				GeoPoint gps_point = new GeoPoint(location.getLatitude(), location.getLongitude(), location.getAltitude());
				pj.toPixels(gps_point, mGPS_Px);

				// draw line between gps and gun
				if(MainActivity.tab_map.gun_Marker != null) {
					GeoPoint gun_point = MainActivity.tab_map.gun_Marker.getPosition();
					pj.toPixels(gun_point, mGun_Px);
					p_blue.setColor(Tab_Map.ProjectileSettings.color);
					canvas.drawLine(mGPS_Px.x, mGPS_Px.y, mGun_Px.x, mGun_Px.y, p_blue);
				}

				// draw line between gps and target
				if(Tab_Map.navigation_mode) {
					if (MainActivity.tab_map.target_Marker != null) {
						int idx = Integer.parseInt(Tab_Map.target_Marker.getId());
						if (idx >= 0) {
							GeoPoint target_point = MainActivity.tab_map.target_Marker.getPosition();
							pj.toPixels(target_point, mTarget_Px);
							canvas.drawLine(mGPS_Px.x, mGPS_Px.y, mTarget_Px.x, mTarget_Px.y, p_red);

							String strInfo = "";
							if (strInfo1.length() > 0) {
								strInfo = strInfo1;
								if (strInfo2.length() > 0) {
									strInfo += "\n" + strInfo2;
								}
							} else {
								if (strInfo2.length() > 0) {
									strInfo = strInfo2;
								}
							}
							if (strInfo.length() > 0) {
								Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
								textPaint.setTextSize(dm.density * Tab_Map.map_text_size);
								Rect rec = MainActivity.getTextBackgroundSize(0, 0, mv_utils.multi_line_text_max_line(strInfo), textPaint);
								int layout_width = rec.width();
								StaticLayout staticLayout = new StaticLayout(strInfo, textPaint, layout_width, alignment, 1.0f, 0, false);

								Paint mTextBackground = new Paint();
								mTextBackground.setColor(0x80000000);
								mTextBackground.setStyle(Paint.Style.FILL);

//                int dx = 150;
//                int dy = 210;
								int dx = staticLayout.getWidth() / 2;
								int dy = staticLayout.getHeight();
								Rect rectangle = new Rect();
//                rectangle.set(mPositionPixels.x - dx, mPositionPixels.y, mPositionPixels.x + dx, mPositionPixels.y + dy);
//                rectangle.set( -dx, 0, dx, dy);
								rectangle.set(0, 0, 2 * dx, dy);
//                Rect rectangle = MainActivity.getTextBackgroundSize(mPositionPixels.x, mPositionPixels.y, strInfo, textPaint);
//                pj.save(canvas, false, false);
								canvas.save();
//                canvas.translate(mPositionPixels.x - dx, mPositionPixels.y);
								canvas.translate(mGPS_Px.x - dx, mGPS_Px.y + 30);
								canvas.rotate(-pj.getOrientation());
								canvas.drawRect(rectangle, mTextBackground);
								staticLayout.draw(canvas);
								canvas.restore();
//                pj.restore(canvas, false);
							}
						}
					}
				}
			}

			canvas.save();

			// Rotate the icon if we have a GPS fix, take into account if the map is already rotated
			float mapRotation;
			mapRotation=lastFix.getBearing();
			if (mapRotation >=360.0f)
				mapRotation=mapRotation-360f;
			canvas.rotate(mapRotation, mDrawPixel.x, mDrawPixel.y);
			// Draw the bitmap
			canvas.drawBitmap(mDirectionArrowBitmap, mDrawPixel.x
					- mDirectionArrowCenterX, mDrawPixel.y - mDirectionArrowCenterY,
					mPaint);

			// draw cross
			float d = 16;
			canvas.drawLine(mDrawPixel.x - d,mDrawPixel.y,mDrawPixel.x + d,mDrawPixel.y,mPaint);
			canvas.drawLine(mDrawPixel.x,mDrawPixel.y - d,mDrawPixel.x,mDrawPixel.y + d,mPaint);

			canvas.restore();
		} else {
			canvas.save();
			// Unrotate the icon if the maps are rotated so the little man stays upright
			canvas.rotate(-mMapView.getMapOrientation(), mDrawPixel.x,
					mDrawPixel.y);
			// Draw the bitmap
			canvas.drawBitmap(mPersonBitmap, mDrawPixel.x - mPersonHotspot.x,
					mDrawPixel.y - mPersonHotspot.y, mPaint);
			canvas.restore();
		}

//		GnssStatus status = getMyLocationProvider().getLastKnownStatus();
//		if(status != null) {
//			String strStatus = "";
//			strStatus += "SatelliteCount: "+status.getSatelliteCount()+"\n";
//			canvas.drawText(strStatus, mDrawPixel.x, mDrawPixel.y, mPaint);
//		}
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void draw(Canvas c, Projection pProjection) {
		if (!isEnabled())   return;
		if (mLocation != null && isMyLocationEnabled()) {
			drawMyLocation(c, pProjection, mLocation);
		}
	}

	@Override
	public boolean onSnapToItem(final int x, final int y, final Point snapPoint,
			final IMapView mapView) {
		if (this.mLocation != null) {
			Projection pj = mMapView.getProjection();
			pj.toPixels(mGeoPoint, mSnapPixel);
			snapPoint.x = mSnapPixel.x;
			snapPoint.y = mSnapPixel.y;
			final double xDiff = x - mSnapPixel.x;
			final double yDiff = y - mSnapPixel.y;
			boolean snap = xDiff * xDiff + yDiff * yDiff < 64;
			if (Configuration.getInstance().isDebugMode()) {
                    Log.d(IMapView.LOGTAG, "snap=" + snap);
			}
			return snap;
		} else {
			return false;
		}
	}

	public void setEnableAutoStop(boolean value){
		this.enableAutoStop=value;
	}
	public boolean getEnableAutoStop(){
		return this.enableAutoStop;
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event, final MapView mapView) {
		final boolean isSingleFingerDrag = (event.getAction() == MotionEvent.ACTION_MOVE)
				&& (event.getPointerCount() == 1);

		if (event.getAction() == MotionEvent.ACTION_DOWN && enableAutoStop) {
			this.disableFollowLocation();
		} else if (isSingleFingerDrag && isFollowLocationEnabled()) {
			return true;  // prevent the pan
		}

		return super.onTouchEvent(event, mapView);
	}

	// ===========================================================
	// Menu handling methods
	// ===========================================================

	@Override
	public void setOptionsMenuEnabled(final boolean pOptionsMenuEnabled) {
		this.mOptionsMenuEnabled = pOptionsMenuEnabled;
	}

	@Override
	public boolean isOptionsMenuEnabled() {
		return this.mOptionsMenuEnabled;
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu pMenu, final int pMenuIdOffset,
			final MapView pMapView) {
		pMenu.add(0, MENU_MY_LOCATION + pMenuIdOffset, Menu.NONE,
				pMapView.getContext().getResources().getString(R.string.my_location)
				)
				.setIcon(
						mv_utils.getDrawable(MainActivity.ctx,R.drawable.ic_menu_mylocation)
						)
				.setCheckable(true);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu pMenu, final int pMenuIdOffset,
			final MapView pMapView) {
		pMenu.findItem(MENU_MY_LOCATION + pMenuIdOffset).setChecked(this.isMyLocationEnabled());
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem pItem, final int pMenuIdOffset,
			final MapView pMapView) {
		final int menuId = pItem.getItemId() - pMenuIdOffset;
		if (menuId == MENU_MY_LOCATION) {
			if (this.isMyLocationEnabled()) {
				this.disableFollowLocation();
				this.disableMyLocation();
			} else {
				this.enableFollowLocation();
				this.enableMyLocation();
			}
			return true;
		} else {
			return false;
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * Return a GeoPoint of the last known location, or null if not known.
	 */
	public GeoPoint getMyLocation() {
		if (mLocation == null) {
			return null;
		} else {
			return new GeoPoint(mLocation);
		}
	}

	public Location getLastFix() {
		return mLocation;
	}

	/**
	 * Enables "follow" functionality. The map will center on your current location and
	 * automatically scroll as you move. Scrolling the map in the UI will disable.
	 */
	public void enableFollowLocation() {
		mIsFollowing = true;

		// set initial location when enabled
		if (isMyLocationEnabled()) {
			Location location = mMyLocationProvider.getLastKnownLocation();
			if (location != null) {
				setLocation(location);
			}
		}

		// Update the screen to see changes take effect
		if (mMapView != null) {
			mMapView.postInvalidate();
		}
	}

	/**
	 * Disables "follow" functionality.
	 */
	public void disableFollowLocation() {
		mMapController.stopAnimation(false);
		mIsFollowing = false;
	}

	/**
	 * If enabled, the map will center on your current location and automatically scroll as you
	 * move. Scrolling the map in the UI will disable.
	 * 
	 * @return true if enabled, false otherwise
	 */
	public boolean isFollowLocationEnabled() {
		return mIsFollowing;
	}

	@Override
	public void onLocationChanged(Location location, ImvLocationProvider source) {

		if (location != null && mHandler!=null) {
			// These location updates can come in from different threads
			mHandler.postAtTime(new Runnable() {
				@Override
				public void run() {
					setLocation(location);

					for (final Runnable runnable : mRunOnFirstFix) {
						Thread t = new Thread(runnable);
						t.setName(this.getClass().getName() + "#onLocationChanged");
						t.start();
					}
					mRunOnFirstFix.clear();
				}
			}, mHandlerToken, 0);
		}
	}

	protected void setLocation(Location location) {
		mLocation = location;
		mGeoPoint.setCoords(mLocation.getLatitude(), mLocation.getLongitude());
		if (mIsFollowing) {
			mMapController.animateTo(mGeoPoint);
		} else if ( mMapView != null ) {
			mMapView.postInvalidate();
		}
	}

	public boolean enableMyLocation(ImvLocationProvider myLocationProvider) {
		// Set the location provider. This will call stopLocationProvider().
		setMyLocationProvider(myLocationProvider);

		boolean success = mMyLocationProvider.startLocationProvider(this);
		mIsLocationEnabled = success;

		// set initial location when enabled
		if (success) {
			Location location = mMyLocationProvider.getLastKnownLocation();
			if (location != null) {
				setLocation(location);
			}
		}

		// Update the screen to see changes take effect
		if (mMapView != null) {
			mMapView.postInvalidate();
		}

		return success;
	}

	/**
	 * Enable receiving location updates from the provided IMyLocationProvider and show your
	 * location on the maps. You will likely want to call enableMyLocation() from your Activity's
	 * Activity.onResume() method, to enable the features of this overlay. Remember to call the
	 * corresponding disableMyLocation() in your Activity's Activity.onPause() method to turn off
	 * updates when in the background.
	 */
	public boolean enableMyLocation() {
		if(mMyLocationProvider != null) {
			return enableMyLocation(mMyLocationProvider);
		}else{
			return false;
		}
	}

	/**
	 * Disable location updates
	 */
	public void disableMyLocation() {
		mIsLocationEnabled = false;

		stopLocationProvider();

		// Update the screen to see changes take effect
		if (mMapView != null) {
			mMapView.postInvalidate();
		}
	}

	protected void stopLocationProvider() {
		if (mMyLocationProvider != null) {
			mMyLocationProvider.stopLocationProvider();
		}
		if (mHandler!=null && mHandlerToken!=null)
			mHandler.removeCallbacksAndMessages(mHandlerToken);
	}

	/**
	 * If enabled, the map is receiving location updates and drawing your location on the map.
	 * 
	 * @return true if enabled, false otherwise
	 */
	public boolean isMyLocationEnabled() {
		return mIsLocationEnabled;
	}

	/**
	 * Queues a runnable to be executed as soon as we have a location fix. If we already have a fix,
	 * we'll execute the runnable immediately and return true. If not, we'll hang on to the runnable
	 * and return false; as soon as we get a location fix, we'll run it in in a new thread.
	 */
	public boolean runOnFirstFix(final Runnable runnable) {
		if (mMyLocationProvider != null && mLocation != null) {
			Thread t = new Thread(runnable);
			t.setName(this.getClass().getName() + "#runOnFirstFix");
			t.start();
			return true;
		} else {
			mRunOnFirstFix.addLast(runnable);
			return false;
		}
	}
     
     /**
      * enabls you to change the my location 'person' icon at runtime. note that the
      * hotspot is not updated with this method. see 
      * {@link #setPersonHotspot}
      * @param icon 
      */
     public void setPersonIcon(Bitmap icon){
          mPersonBitmap = icon;
     }
}
