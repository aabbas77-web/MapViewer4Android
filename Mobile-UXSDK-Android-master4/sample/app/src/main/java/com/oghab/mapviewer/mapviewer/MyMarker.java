package com.oghab.mapviewer.mapviewer;

import static com.oghab.mapviewer.MainActivity.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;
import com.oghab.mapviewer.bonuspack.kml.KmlPlacemark;
import com.oghab.mapviewer.utils.mv_utils;

import org.osmdroid.tileprovider.BitmapPool;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.RectL;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapViewRepository;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayWithIW;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A marker is an icon placed at a particular point on the map's surface that can have a popup-{@link org.osmdroid.views.overlay.infowindow.InfoWindow} (a bubble)
 * Mimics the MyMarker class from Google Maps Android API v2 as much as possible. Main differences:<br>
 * <p>
 * - Doesn't support Z-Index: as other osmdroid overlays, MyMarker is drawn in the order of appearance. <br>
 * - The icon can be any standard Android Drawable, instead of the BitmapDescriptor introduced in Google Maps API v2. <br>
 * - The icon can be changed at any time. <br>
 * - The InfoWindow hosts a standard Android View. It can handle Android widgets like buttons and so on. <br>
 * - Supports a "sub-description", to be displayed in the InfoWindow, under the snippet, in a smaller text font. <br>
 * - Supports an image, to be displayed in the InfoWindow. <br>
 * - Supports "panning to view" on/off option (when touching a marker, center the map on marker position). <br>
 * - Opening a MyMarker InfoWindow automatically close others only if it's the same InfoWindow shared between Markers. <br>
 * - Events listeners are set per marker, not per map. <br>
 *
 * <img alt="Class diagram around MyMarker class" width="686" height="413" src='src='./doc-files/marker-infowindow-classes.png' />
 *
 * @author M.Kergall
 * @see MarkerInfoWindow
 * see also <a href="http://developer.android.com/reference/com/google/android/gms/maps/model/MyMarker.html">Google Maps MyMarker</a>
 */
public class MyMarker extends OverlayWithIW {

    /* attributes for text labels, used for osmdroid gridlines */
    protected int mTextLabelBackgroundColor = Color.WHITE;
    protected int mTextLabelForegroundColor = Color.BLACK;
    protected int mTextLabelFontSize = (int)Tab_Map.map_text_size;
    DisplayMetrics dm;
    private TextPaint textPaint;
    //    String strInfo = " MapViewer\n Speed: 10 m/s\n Direction: 45 deg";
    String strInfo1 = "";
    String strInfo2 = "";
    boolean isTarget = false;

    /*attributes for standard features:*/
    protected Drawable mIcon;
    protected GeoPoint mPosition;
    protected float mBearing;
    protected float mAnchorU, mAnchorV;
    protected float mIWAnchorU, mIWAnchorV;
    protected float mAlpha;
    protected boolean mDraggable, mIsDragged;
    protected boolean mFlat;
    protected MyMarker.OnMarkerClickListener mOnMarkerClickListener;
    protected MyMarker.OnMarkerDragListener mOnMarkerDragListener;
    protected MyMarker.OnMarkerLongPressListener mOnMarkerLongPressListener;

    /*attributes for non-standard features:*/
    protected Drawable mImage;
    protected boolean mPanToView;
    protected float mDragOffsetY;

    /*internals*/
    protected Point mPositionPixels;
    protected Resources mResources;

    /**
     * @since 6.0.3
     */
    private MapViewRepository mMapViewRepository;

    /**
     * Usual values in the (U,V) coordinates system of the icon image
     */
    public static final float ANCHOR_CENTER = 0.5f, ANCHOR_LEFT = 0.0f, ANCHOR_TOP = 0.0f, ANCHOR_RIGHT = 1.0f, ANCHOR_BOTTOM = 1.0f;

    /**
     * @since 6.0.3
     */
    private boolean mDisplayed;
    private final Rect mRect = new Rect();
    private final Rect mOrientedMarkerRect = new Rect();
    private Paint mPaint;
    private Paint fillPaint;
    private Paint strokePaint;

    protected boolean mEdit = false;
    protected MapView map = null;
    KmlPlacemark placemark = null;
    Context context = null;
    MyMarker marker0 = null;

    public MyMarker(MapView mapView) {
        this(mapView, (mapView.getContext()));
        map = mapView;
    }

    public MyMarker(MapView mapView, final Context resourceProxy) {
        super();
        map = mapView;

        mMapViewRepository = mapView.getRepository();
        mResources = mapView.getContext().getResources();
        mBearing = 0.0f;
        mAlpha = 1.0f; //opaque
        mPosition = new GeoPoint(0.0, 0.0);
        mAnchorU = ANCHOR_CENTER;
        mAnchorV = ANCHOR_CENTER;
        mIWAnchorU = ANCHOR_CENTER;
//        mIWAnchorV = ANCHOR_TOP;
        mIWAnchorV = ANCHOR_BOTTOM;
        mDraggable = false;
        mIsDragged = false;
        mPositionPixels = new Point();
        mPanToView = true;
        mDragOffsetY = 0.0f;
        mFlat = false; //billboard
        mOnMarkerClickListener = null;
        mOnMarkerDragListener = null;
        mOnMarkerLongPressListener = null;
        setDefaultIcon();
        setInfoWindow(mMapViewRepository.getDefaultMarkerInfoWindow());

        setId("-1");

        // Get the display metrics
        Resources resources = resourceProxy.getResources();
        dm = resources.getDisplayMetrics();

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(dm.density * Tab_Map.map_text_size);
        textPaint.setColor(Color.GREEN);
        //        textPaint.setStyle(Paint.Style.STROKE);
        //        textPaint.setStrokeWidth(1);

        fillPaint = new Paint();
        fillPaint.setColor(Color.CYAN);
        fillPaint.setAlpha(64);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setStrokeWidth(1.0f);

        strokePaint = new Paint();
        strokePaint.setColor(Color.BLUE);
        strokePaint.setAlpha(255);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(2.0f);
    }

    MyMarker.OnMarkerDragListener onMarkerDrag = new MyMarker.OnMarkerDragListener() {
        @Override
        public void onMarkerDrag(MyMarker mark) {
            try{
                if(placemark != null){
                    placemark.mGeometry.mCoordinates.set(0, mark.getPosition());
                }
                setPosition(mark.getPosition());

                map.postInvalidate();
            }
            catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
            }
        }

        @Override
        public void onMarkerDragEnd(MyMarker mark) {

        }

        @Override
        public void onMarkerDragStart(MyMarker mark) {

        }
    };

    MyMarker.OnMarkerLongPressListener onMarkerLongPressDeleteListener = new MyMarker.OnMarkerLongPressListener() {
        @Override
        public boolean onMarkerLongPress(MyMarker mark, MapView mapView) {
            try{
                new AlertDialog.Builder(activity)
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.delete)
                        .setMessage(R.string.are_you_sure_delete_point)
                        .setPositiveButton(R.string.yes_message, (dialog, which) -> {
//                                    if(placemark != null){
//                                        placemark.mGeometry.mCoordinates.set(0, mark.getPosition());
//                                    }
//                                    remove(map);

//                                    clear_markers();
//                                    setEdit(context,placemark,true);
//                                    map.postInvalidate();

                            Tab_Map.CustomInfoWindow info = (Tab_Map.CustomInfoWindow)getInfoWindow();
                            if(info != null){
                                MainActivity.tab_map.delete_city(info.city);
                            }

                            //Tab_Messenger.showToast("["+mark.getTitle() +"] Deleted...");
                            MainActivity.hide_keyboard(null);
                        })
                        .setNegativeButton(R.string.no_message, (dialog, which) -> {
                            MainActivity.hide_keyboard(null);
                        })
                        .show();
            }
            catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
            }
            return true;
        }
    };

//    MyMarker.OnMarkerClickListener onMarkerClickListener = new MyMarker.OnMarkerClickListener() {
//        @Override
//        public boolean onMarkerClick(MyMarker mark, MapView mapView) {
//            int idx0 = Integer.parseInt(mark.getId());
//            Tab_Messenger.showToast(Integer.toString(idx0+1));
////            Tab_Messenger.showToast("Clicked");
//
//            List<GeoPoint> points = getActualPoints();
//            if(points.size() > 0) {
//                if(Tab_Map.target_Marker != null) {
//                    int idx = Integer.parseInt(mark.getId());
//                    if(idx >= 0) {
//                        if (idx < points.size()) {
//                            Tab_Map.target_Marker.setEnabled(true);
//                            Tab_Map.target_Marker.setId(Integer.toString(idx));
//                            Tab_Map.target_Marker.setPosition(points.get(idx));
//                            Tab_Map.target_Marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
//                            Tab_Map.target_Marker.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
//                            Tab_Map.target_Marker.setIcon(mv_utils.getDrawable(MainActivity.ctx, R.drawable.marker_target));
//                            Tab_Map.target_Marker.setTitle("Target point: ");
//                            Tab_Map.target_Marker.setInfo1("");
//                            Tab_Map.target_Marker.setInfo2("");
//                        } else {
//                            Tab_Map.navigation_mode = false;
//                            Tab_Map.target_Marker.setEnabled(false);
//                            Tab_Map.target_Marker.setId(Integer.toString(-1));
//                            Tab_Messenger.showToast("Mission Finished");
//                        }
//                    }
//                }
//            }
//            return true;
//        }
//    };

//    public void add_mark(GeoPoint p, int idx){
//        if(map == null) return;
//        try{
//            MyMarker marker = new MyMarker(map);
//            marker.setPosition(p);
//            marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_CENTER);
//            marker.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_CENTER);
//            if(Tab_Map.edit_mode)
//                marker.setIcon(mv_utils.getDrawable(context, R.drawable.focus_4x));
//            else
//                marker.setIcon(mv_utils.getDrawable(context, R.drawable.delete_4x));
//            marker.setTitle("");
//            marker.setInfo1("");
//            marker.setInfo2("");
//            marker.setSubDescription("");
//            marker.setEnabled(true);
//            marker.setDraggable(true);
//            marker.setId(String.valueOf(idx));
//            marker.setOnMarkerDragListener(onMarkerDrag);
//            marker.setOnMarkerClickListener(null);
//            marker.setOnMarkerLongPressListener(onMarkerLongPressDeleteListener);
//            marker.setPanToView(false);
//            marker.setInfoWindow(null);
//            map.getOverlays().add(marker);
//
//            marker0 = marker;
//        }
//        catch (Throwable ex)
//        {
//            MainActivity.MyLog(ex);
//        }
//    }

    public void add_mark(GeoPoint p, int idx){
        if(map == null) return;
        try{
            MyMarker marker = new MyMarker(map);
            marker.setPosition(p);
            marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_CENTER);
            marker.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_CENTER);
            marker.setTitle("");
            marker.setInfo1("");
            marker.setInfo2("");
            marker.setSubDescription("");
            marker.setEnabled(true);
            marker.setDraggable(true);
            marker.setId(String.valueOf(idx));
            marker.setIsTarget(false);
            if(Tab_Map.navigation_mode) {
                marker.setIsTarget(true);
                marker.setDraggable(false);
//                marker.setTitle(Integer.toString(idx));
                marker.setNumberIcon(String.format(Locale.ENGLISH, "%02d", idx+1),Math.round(2*(2+Tab_Map.map_text_size)),Math.round(2*(2+Tab_Map.map_text_size)),Math.round((2+Tab_Map.map_text_size)));
                marker.setOnMarkerDragListener(null);
//                marker.setOnMarkerClickListener(onMarkerClickListener);
                marker.setOnMarkerClickListener(null);
                marker.setOnMarkerLongPressListener(null);
            } else {
                if(Tab_Map.edit_mode) {
                    marker.setIcon(mv_utils.getDrawable(context, R.drawable.focus_4x));
                    marker.setOnMarkerDragListener(onMarkerDrag);
                    marker.setOnMarkerClickListener(null);
                    marker.setOnMarkerLongPressListener(null);
                } else {
                    marker.setIcon(mv_utils.getDrawable(context, R.drawable.delete_4x));
                    marker.setOnMarkerDragListener(null);
                    marker.setOnMarkerClickListener(null);
                    marker.setOnMarkerLongPressListener(onMarkerLongPressDeleteListener);
                }
            }
            marker.setPanToView(false);
            marker.setInfoWindow(null);
            map.getOverlays().add(marker);

            marker0 = marker;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void clear_markers(){
        try{
            Overlay overlay = marker0;
            if (overlay != null) {
                map.getOverlays().remove(overlay);
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void setEdit(Context context, KmlPlacemark placemark, boolean value) {
        this.context = context;
        if(placemark != null)   this.placemark = placemark;
        mEdit = value;
        if(map == null) return;
        try{
            clear_markers();
            if(mEdit){
                GeoPoint p = getPosition();
                add_mark(p, 0);
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public boolean getEdit() {
        return mEdit;
    }

    public void setIsTarget(boolean value) {
        try
        {
            isTarget = value;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public boolean getIsTarget() {
        return isTarget;
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

    /**
     * Sets the icon for the marker. Can be changed at any time.
     * This is used on the map view.
     * The anchor will be left unchanged; you may need to call {@link #setAnchor(float, float)}
     * Two exceptions:
     * - for text icons, the anchor is set to (center, center)
     * - for the default icon, the anchor is set to the corresponding position (the tip of the teardrop)
     * Related methods: {@link #setTextIcon(String)}, {@link #setDefaultIcon()} and {@link #setAnchor(float, float)}
     *
     * @param icon if null, the default osmdroid marker is used.
     */
    public void setIcon(final Drawable icon) {
        if (icon != null) {
            mIcon = icon;
        } else {
            setDefaultIcon();
        }
    }

    /**
     * @since 6.0.3
     */
    public void setDefaultIcon() {
        mIcon = mMapViewRepository.getDefaultMarkerIcon();
        setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM);
    }

    /**
     * @since 6.0.3
     */
    public void setTextIcon(final String pText) {
        final Paint background = new Paint();
        background.setColor(mTextLabelBackgroundColor);
        final Paint p = new Paint();
//        p.setTextSize(mTextLabelFontSize);
        p.setTextSize(dm.density * Tab_Map.map_text_size);
        p.setColor(mTextLabelForegroundColor);
        p.setAntiAlias(true);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setTextAlign(Paint.Align.LEFT);
        final int width = (int) (p.measureText(pText) + 0.5f);
        final float baseline = (int) (-p.ascent() + 0.5f);
        final int height = (int) (baseline + p.descent() + 0.5f);
        final Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas c = new Canvas(image);
        c.drawPaint(background);
        c.drawText(pText, 0, baseline, p);
        mIcon = new BitmapDrawable(mResources, image);
        setAnchor(ANCHOR_CENTER, ANCHOR_CENTER);
    }

    public void setNumberIcon(final String pText, final int width, final int height, int radius) {
        int w = Math.round(dm.density * Math.max(width,48));
        int h = Math.round(dm.density * Math.max(height,48));
        int r = Math.round(dm.density * Math.max(radius,48));

        // Get projection
//        Projection proj = map.getProjection();
        // How many pixels in 100 meters for this zoom level
//        radius = Math.round(proj.metersToPixels(100));
        // How many meters in 100 pixels for this zoom level
//        float meters = 1 / proj.metersToPixels(1 / 100);
        // You could also get a raw meters-per-pixels value by using TileSystem.GroundResolution()

//        final Paint background = new Paint();
//        background.setColor(mTextLabelBackgroundColor);
        final Paint p = new Paint();
//        p.setTextSize(mTextLabelFontSize);
        p.setTextSize(dm.density * Tab_Map.map_text_size);
        p.setColor(mTextLabelForegroundColor);
        p.setAntiAlias(true);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setTextAlign(Paint.Align.CENTER);
        final float baseline = (int) (-p.ascent() + 0.5f);
        final int text_width = (int) (p.measureText(pText) + 0.5f);
//        final int width = (int) (p.measureText(pText) + 0.5f);
//        final int height = (int) (baseline + p.descent() + 0.5f);
        final Bitmap image = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        final Canvas c = new Canvas(image);
//        c.drawPaint(background);
        p.setColor(Color.MAGENTA);
        p.setAlpha(128);
        r = Math.round(c.getWidth()/2.0f);
        c.drawCircle(c.getWidth()/2.0f,c.getHeight()/2.0f,r,p);
        p.setColor(Color.WHITE);
//        c.drawText(pText, (w-text_width)/2.0f, baseline, p);
//        c.drawText(pText, w/2.0f, baseline, p);
        c.drawText(pText, w/2.0f, h/2.0f+baseline/2.0f, p);
        mIcon = new BitmapDrawable(mResources, image);
        setAnchor(ANCHOR_CENTER, ANCHOR_CENTER);
    }

    /**
     * @return
     * @since 6.0.0?
     */
    public Drawable getIcon() {
        return mIcon;
    }

    public GeoPoint getPosition() {
        return mPosition;
    }

    /**
     * sets the location on the planet where the icon is rendered
     *
     * @param position
     */
    public void setPosition(GeoPoint position) {
        mPosition = position.clone();
        if (isInfoWindowShown()) {
            closeInfoWindow();
            showInfoWindow();
        }
        mBounds = new BoundingBox(position.getLatitude(), position.getLongitude(), position.getLatitude(), position.getLongitude());
    }

    public float getRotation() {
        return mBearing;
    }

    /**
     * rotates the icon in relation to the map
     *
     * @param rotation
     */
    public void setRotation(float rotation) {
        mBearing = rotation;
    }

    /**
     * @param anchorU WIDTH 0.0-1.0 percentage of the icon that offsets the logical center from the actual pixel center point
     * @param anchorV HEIGHT 0.0-1.0 percentage of the icon that offsets the logical center from the actual pixel center point
     */
    public void setAnchor(float anchorU, float anchorV) {
        mAnchorU = anchorU;
        mAnchorV = anchorV;
    }

    public void setInfoWindowAnchor(float anchorU, float anchorV) {
        mIWAnchorU = anchorU;
        mIWAnchorV = anchorV;
    }

    public void setAlpha(float alpha) {
        mAlpha = alpha;
    }

    public float getAlpha() {
        return mAlpha;
    }

    public void setDraggable(boolean draggable) {
        mDraggable = draggable;
    }

    public boolean isDraggable() {
        return mDraggable;
    }

    public void setFlat(boolean flat) {
        mFlat = flat;
    }

    public boolean isFlat() {
        return mFlat;
    }

    /**
     * Removes this MyMarker from the MapView.
     * Note that this method will operate only if the MyMarker is in the MapView overlays
     * (it should not be included in a container like a FolderOverlay).
     *
     * @param mapView
     */
    public void remove(MapView mapView) {
        mapView.getOverlays().remove(this);
    }

    public void setOnMarkerClickListener(MyMarker.OnMarkerClickListener listener) {
        mOnMarkerClickListener = listener;
    }

    public void setOnMarkerDragListener(MyMarker.OnMarkerDragListener listener) {
        mOnMarkerDragListener = listener;
    }

    public void setOnMarkerLongPressListener(MyMarker.OnMarkerLongPressListener listener) {
        mOnMarkerLongPressListener = listener;
    }

    /**
     * set an image to be shown in the InfoWindow  - this is not the marker icon
     */
    public void setImage(Drawable image) {
        mImage = image;
    }

    /**
     * get the image to be shown in the InfoWindow - this is not the marker icon
     */
    public Drawable getImage() {
        return mImage;
    }

    /**
     * set the offset in millimeters that the marker is moved up while dragging
     */
    public void setDragOffset(float mmUp) {
        mDragOffsetY = mmUp;
    }

    /**
     * get the offset in millimeters that the marker is moved up while dragging
     */
    public float getDragOffset() {
        return mDragOffsetY;
    }

    /**
     * Set the InfoWindow to be used.
     * Default is a MarkerInfoWindow, with the layout named "bonuspack_bubble".
     * You can use this method either to use your own layout, or to use your own sub-class of InfoWindow.
     * Note that this InfoWindow will receive the MyMarker object as an input, so it MUST be able to handle MyMarker attributes.
     * If you don't want any InfoWindow to open, you can set it to null.
     */
    public void setInfoWindow(MarkerInfoWindow infoWindow) {
        mInfoWindow = infoWindow;
    }

    /**
     * If set to true, when clicking the marker, the map will be centered on the marker position.
     * Default is true.
     */
    public void setPanToView(boolean panToView) {
        mPanToView = panToView;
    }

    /**
     * shows the info window, if it's open, this will close and reopen it
     */
    public void showInfoWindow() {
        if (mInfoWindow == null)
            return;
        final int markerWidth = mIcon.getIntrinsicWidth();
        final int markerHeight = mIcon.getIntrinsicHeight();
        final int offsetX = (int) (markerWidth * (mIWAnchorU - mAnchorU));
        final int offsetY = (int) (markerHeight * (mIWAnchorV - mAnchorV));
        if (mBearing == 0) {
            mInfoWindow.open(this, mPosition, offsetX, offsetY);
            return;
        }
        final int centerX = 0;
        final int centerY = 0;
        final double radians = -mBearing * Math.PI / 180.;
        final double cos = Math.cos(radians);
        final double sin = Math.sin(radians);
        final int rotatedX = (int) RectL.getRotatedX(offsetX, offsetY, centerX, centerY, cos, sin);
        final int rotatedY = (int) RectL.getRotatedY(offsetX, offsetY, centerX, centerY, cos, sin);
        mInfoWindow.open(this, mPosition, rotatedX, rotatedY);
    }

    public boolean isInfoWindowShown() {
        if (mInfoWindow instanceof MyMarkerInfoWindow) {
            MyMarkerInfoWindow iw = (MyMarkerInfoWindow) mInfoWindow;
            return (iw != null) && iw.isOpen() && (iw.getMarkerReference() == this);
        } else
            return super.isInfoWindowOpen();
    }

    @Override
    public void draw(Canvas canvas, Projection pj) {
        if (mIcon == null)  return;
        if (!isEnabled())   return;

        pj.toPixels(mPosition, mPositionPixels);

        // Get projection
        if(isTarget) {
//        Projection proj = map.getProjection();
            // How many pixels in 100 meters for this zoom level
            float radius = pj.metersToPixels(Tab_Map.map_target_radius);
            // How many meters in 100 pixels for this zoom level
//        float meters = 1 / proj.metersToPixels(1 / 100);
            // You could also get a raw meters-per-pixels value by using TileSystem.GroundResolution()
            canvas.drawCircle(mPositionPixels.x, mPositionPixels.y, radius, fillPaint);
            canvas.drawCircle(mPositionPixels.x, mPositionPixels.y, radius, strokePaint);
        }

        float rotationOnScreen = (mFlat ? -mBearing : -pj.getOrientation() - mBearing);
        drawAt(canvas, mPositionPixels.x, mPositionPixels.y, rotationOnScreen);

        //AliSoft Draw Label
        String title = getTitle();
        if((title != null) && (!Objects.equals(title, ""))){
            canvas.save();
            canvas.translate(mPositionPixels.x, mPositionPixels.y);
            canvas.rotate(-pj.getOrientation() - mBearing);

//        String pText = getTitle();
//        final Paint background = new Paint();
//        background.setColor(mTextLabelBackgroundColor);
            final Paint p = new Paint();
//        p.setTextSize(dm.density * mTextLabelFontSize);
            p.setTextSize(dm.density * Tab_Map.map_text_size);
            p.setAntiAlias(true);
            p.setTypeface(Typeface.DEFAULT_BOLD);
            p.setTextAlign(Paint.Align.LEFT);
//        final int width = (int) (p.measureText(pText) + 0.5f);
//        final float baseline = (int) (-p.ascent() + 0.5f);
//        final int height = (int) (baseline + p.descent() + 0.5f);
//        final Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        final Canvas c = new Canvas(image);
//        c.drawPaint(background);
//        c.drawText(pText, 0, baseline, p);
//        mIcon = new BitmapDrawable(mResources, image);
//        setAnchor(ANCHOR_CENTER, ANCHOR_CENTER);

            Paint.FontMetrics fm = new Paint.FontMetrics();
            p.setColor(0x80ffffff);
            p.getFontMetrics(fm);
            Rect bounds = new Rect();
            p.getTextBounds(title,0,title.length(),bounds);
            float margin = 1;
            float x,y;
            x = -bounds.width()/2.0f-margin;
            y = bounds.height()+margin;
            canvas.drawRect(x+bounds.left-margin, y+bounds.top-margin, x+bounds.right+margin, y+bounds.bottom+margin, p);
            p.setColor(mTextLabelForegroundColor);
            canvas.drawText(title, x, y, p);
            canvas.restore();
        }

        // Info
//        if(pj.getZoomLevel() >= MainActivity.dMarkersZoomLevel) {
            String strInfo = "";
            if(strInfo1.length() > 0){
                strInfo = strInfo1;
                if(strInfo2.length() > 0) {
                    strInfo += "\n" + strInfo2;
                }
            }else{
                if(strInfo2.length() > 0) {
                    strInfo = strInfo2;
                }
            }
            if (strInfo.length() > 0) {
                textPaint.setTextSize(dm.density * Tab_Map.map_text_size);
                Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
//                int layout_width = 300;
//                Rect rec;
//                if(strInfo1.length() >= strInfo2.length())
//                    rec = MainActivity.getTextBackgroundSize(0, 0, strInfo1, textPaint);
//                else
//                    rec = MainActivity.getTextBackgroundSize(0, 0, strInfo2, textPaint);
                Rect rec = MainActivity.getTextBackgroundSize(0, 0, mv_utils.multi_line_text_max_line(strInfo), textPaint);
                int layout_width = rec.width();
                StaticLayout staticLayout = new StaticLayout(strInfo, textPaint, layout_width, alignment, 1.0f, 0, false);

                Paint mTextBackground = new Paint();
                mTextBackground.setColor(0x80000000);
                mTextBackground.setStyle(Paint.Style.FILL);

//                int dx = 150;
//                int dy = 210;
                int dx = staticLayout.getWidth()/2;
                int dy = staticLayout.getHeight();
                Rect rectangle = new Rect();
//                rectangle.set(mPositionPixels.x - dx, mPositionPixels.y, mPositionPixels.x + dx, mPositionPixels.y + dy);
//                rectangle.set( -dx, 0, dx, dy);
                rectangle.set( 0, 0, 2*dx, dy);
//                Rect rectangle = MainActivity.getTextBackgroundSize(mPositionPixels.x, mPositionPixels.y, strInfo, textPaint);
//                pj.save(canvas, false, false);
                canvas.save();
//                canvas.translate(mPositionPixels.x - dx, mPositionPixels.y);
                canvas.translate(mPositionPixels.x, mPositionPixels.y);
                canvas.rotate(-pj.getOrientation() - mBearing);
                canvas.drawRect(rectangle, mTextBackground);
                staticLayout.draw(canvas);
                canvas.restore();
//                pj.restore(canvas, false);
            }
//        }

        if (isInfoWindowShown()) {
            //showInfoWindow();
            mInfoWindow.draw();
        }
    }

    /**
     * Null out the static references when the MapView is detached to prevent memory leaks.
     */
    @Override
    public void onDetach(MapView mapView) {
        BitmapPool.getInstance().asyncRecycle(mIcon);
        mIcon = null;
        BitmapPool.getInstance().asyncRecycle(mImage);
        //cleanDefaults();
        this.mOnMarkerClickListener = null;
        this.mOnMarkerDragListener = null;
        this.mOnMarkerLongPressListener = null;
        this.mResources = null;
        setRelatedObject(null);
        if (isInfoWindowShown())
            closeInfoWindow();
        //	//if we're using the shared info window, this will cause all instances to close

        mMapViewRepository = null;
        setInfoWindow(null);
        onDestroy();


        super.onDetach(mapView);
    }


    /**
     * Prevent memory leaks and call this when you're done with the map
     * reference https://github.com/MKergall/osmbonuspack/pull/210
     */
    @Deprecated
    public static void cleanDefaults() {
    }

    public boolean hitTest(final MotionEvent event, final MapView mapView) {
        return mIcon != null && mDisplayed && mOrientedMarkerRect.contains((int) event.getX(), (int) event.getY()); // "!=null": fix for #1078
    }

    @Override
    public boolean onSingleTapConfirmed(final MotionEvent event, final MapView mapView) {
        boolean touched = hitTest(event, mapView);
        if (touched) {
            if (mOnMarkerClickListener == null) {
                return onMarkerClickDefault(this, mapView);
            } else {
                return mOnMarkerClickListener.onMarkerClick(this, mapView);
            }
        }
        return touched;
    }

    public void moveToEventPosition(final MotionEvent event, final MapView mapView) {
        float offsetY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, mDragOffsetY, mapView.getContext().getResources().getDisplayMetrics());
        final Projection pj = mapView.getProjection();
        setPosition((GeoPoint) pj.fromPixels((int) event.getX(), (int) (event.getY() - offsetY)));
        mapView.invalidate();
    }

//AliSoft
//    @Override
//    public boolean onLongPress(final MotionEvent event, final MapView mapView) {
//        boolean touched = hitTest(event, mapView);
//        if (touched) {
//            if(mOnMarkerLongPressListener != null) {
//                return mOnMarkerLongPressListener.onMarkerLongPress(this, mapView);
////            boolean res = mOnMarkerLongPressListener.onMarkerLongPress(this, mapView);
////            if(!res)
////                return mDraggable && mIsDragged;
////            else
////                return true;
//            } else
//                return false;
////        else
////            return mDraggable && mIsDragged;
//        }else
//            return touched;
//
////        boolean touched = hitTest(event, mapView);
////        if (touched) {
////            if (mDraggable) {
////                //starts dragging mode:
////                mIsDragged = true;
////                closeInfoWindow();
////                if (mOnMarkerDragListener != null)
////                    mOnMarkerDragListener.onMarkerDragStart(this);
////                moveToEventPosition(event, mapView);
////            }
////        }
////        return touched;
////        return true;
//    }

    @Override
    public boolean onDown(final MotionEvent event, final MapView mapView) {
        boolean touched = hitTest(event, mapView);
        try{
            if (touched) {
                if(Tab_Map.edit_mode){
                    if (mDraggable) {
                        //starts dragging mode:
                        mIsDragged = true;
                        closeInfoWindow();
                        if (mOnMarkerDragListener != null)
                            mOnMarkerDragListener.onMarkerDragStart(this);
                        moveToEventPosition(event, mapView);
                    }
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return touched;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event, final MapView mapView) {
        try{
            if (mDraggable && mIsDragged) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mIsDragged = false;
                    if (mOnMarkerDragListener != null)
                        mOnMarkerDragListener.onMarkerDragEnd(this);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    moveToEventPosition(event, mapView);
                    if (mOnMarkerDragListener != null)
                        mOnMarkerDragListener.onMarkerDrag(this);
                    return true;
                } else
                    return false;
            } else
                return false;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    @Override
    public boolean onDoubleTap(final MotionEvent event, final MapView mapView) {
        boolean touched = hitTest(event, mapView);
        try{
            if (touched) {
                if(!Tab_Map.edit_mode) {
                    if (mOnMarkerLongPressListener != null) {
                        mOnMarkerLongPressListener.onMarkerLongPress(this, mapView);
                    }
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return touched;
    }

//    @Override
//    public boolean onDoubleTapEvent(final MotionEvent event, final MapView mapView) {
//        boolean touched = hitTest(event, mapView);
//        if (touched) {
//            if(mOnMarkerLongPressListener != null) {
//                mOnMarkerLongPressListener.onMarkerLongPress(this, mapView);
//            }
//        }
//        return touched;
//    }

//    MapView mapView = null;
//    MyMarker marker = null;
//    final Handler handler = new Handler();
//    Runnable mLongPressed = new Runnable() {
//        public void run() {
//            handler.removeCallbacks(mLongPressed);
//            if(mOnMarkerLongPressListener != null) {
//                mOnMarkerLongPressListener.onMarkerLongPress(marker, mapView);
//            }
//        }
//    };

//    @Override
//    public boolean onTouchEvent(final MotionEvent event, final MapView mapView) {
////            this.mapView = mapView;
////            this.marker = this;
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
////                handler.postDelayed(mLongPressed, android.view.ViewConfiguration.getLongPressTimeout());
//                boolean touched = hitTest(event, mapView);
//                if (touched) {
//                    if (mDraggable) {
//                        //starts dragging mode:
//                        mIsDragged = true;
//                        closeInfoWindow();
//                        if (mOnMarkerDragListener != null)
//                            mOnMarkerDragListener.onMarkerDragStart(this);
//                        moveToEventPosition(event, mapView);
//                    }else
//                        return false;
//                }
//                return touched;
//            } else if (event.getAction() == MotionEvent.ACTION_UP) {
////                handler.removeCallbacks(mLongPressed);
//                if (mDraggable && mIsDragged) {
//                    mIsDragged = false;
//                    if (mOnMarkerDragListener != null)
//                        mOnMarkerDragListener.onMarkerDragEnd(this);
//                    return true;
//                } else
//                    return false;
//            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
////                handler.removeCallbacks(mLongPressed);
//                if (mDraggable && mIsDragged) {
//                    moveToEventPosition(event, mapView);
//                    if (mOnMarkerDragListener != null)
//                        mOnMarkerDragListener.onMarkerDrag(this);
//                    return true;
//                } else
//                    return false;
//            } else
//                return false;
//    }

    public void setVisible(boolean visible) {
        if (visible)
            setAlpha(1f);
        else
            setAlpha(0f);
    }

    //-- MyMarker events listener interfaces ------------------------------------

    public interface OnMarkerClickListener {
        abstract boolean onMarkerClick(MyMarker mark, MapView mapView);
    }

    public interface OnMarkerLongPressListener {
        abstract boolean onMarkerLongPress(MyMarker mark, MapView mapView);
    }

    public interface OnMarkerDragListener {
        abstract void onMarkerDrag(MyMarker mark);

        abstract void onMarkerDragEnd(MyMarker mark);

        abstract void onMarkerDragStart(MyMarker mark);
    }

    /**
     * default behaviour when no click listener is set
     */
    protected boolean onMarkerClickDefault(MyMarker marker, MapView mapView) {
        marker.showInfoWindow();
        if (marker.mPanToView)
            mapView.getController().animateTo(marker.getPosition());
        return true;
    }

    /**
     * used for when the icon is explicitly set to null and the title is not, this will
     * style the rendered text label
     *
     * @return
     */
    public int getTextLabelBackgroundColor() {
        return mTextLabelBackgroundColor;
    }

    /**
     * used for when the icon is explicitly set to null and the title is not, this will
     * style the rendered text label
     *
     * @return
     */
    public void setTextLabelBackgroundColor(int mTextLabelBackgroundColor) {
        this.mTextLabelBackgroundColor = mTextLabelBackgroundColor;
    }

    /**
     * used for when the icon is explicitly set to null and the title is not, this will
     * style the rendered text label
     *
     * @return
     */
    public int getTextLabelForegroundColor() {
        return mTextLabelForegroundColor;
    }

    /**
     * used for when the icon is explicitly set to null and the title is not, this will
     * style the rendered text label
     *
     * @return
     */
    public void setTextLabelForegroundColor(int mTextLabelForegroundColor) {
        this.mTextLabelForegroundColor = mTextLabelForegroundColor;
    }

    /**
     * used for when the icon is explicitly set to null and the title is not, this will
     * style the rendered text label
     *
     * @return
     */
    public int getTextLabelFontSize() {
        return mTextLabelFontSize;
    }

    /**
     * used for when the icon is explicitly set to null and the title is not, this will
     * style the rendered text label
     *
     * @return
     */
    public void setTextLabelFontSize(int mTextLabelFontSize) {
        this.mTextLabelFontSize = mTextLabelFontSize;
    }

    /**
     * @since 6.0.3
     */
    public boolean isDisplayed() {
        return mDisplayed;
    }

    /**
     * Optimized drawing
     *
     * @since 6.0.3
     */
    protected void drawAt(final Canvas pCanvas, final int pX, final int pY, final float pOrientation) {
        final int markerWidth = Math.round(Tab_Map.map_icon_scale * mIcon.getIntrinsicWidth());
        final int markerHeight = Math.round(Tab_Map.map_icon_scale * mIcon.getIntrinsicHeight());
        final int offsetX = pX - Math.round(markerWidth * mAnchorU);
        final int offsetY = pY - Math.round(markerHeight * mAnchorV);
        mRect.set(offsetX, offsetY, offsetX + markerWidth, offsetY + markerHeight);
        RectL.getBounds(mRect, pX, pY, pOrientation, mOrientedMarkerRect);
        mDisplayed = Rect.intersects(mOrientedMarkerRect, pCanvas.getClipBounds());
        if (!mDisplayed) { // optimization 1: (much faster, depending on the proportions) don't try to display if the MyMarker is not visible
            return;
        }
        if (mAlpha == 0) {
            return;
        }
        if (pOrientation != 0) { // optimization 2: don't manipulate the Canvas if not needed (about 25% faster) - step 1/2
            pCanvas.save();
            pCanvas.rotate(pOrientation, pX, pY);
        }
        /*
        if (mIcon instanceof BitmapDrawable) { 
            // optimization 3: (about 15% faster) - Unfortunate optimization with displayed size side effects: introduces issue #1738 
            final Paint paint;
            if (mAlpha == 1) {
                paint = null;
            } else {
                if (mPaint == null) {
                    mPaint = new Paint();
                }
                mPaint.setAlpha((int) (mAlpha * 255));
                paint = mPaint;
            }
            pCanvas.drawBitmap(((BitmapDrawable) mIcon).getBitmap(), offsetX, offsetY, paint);
        } else {
        */
        mIcon.setAlpha((int) (mAlpha * 255));
        mIcon.setBounds(mRect);
        mIcon.draw(pCanvas);
        //}
        if (pOrientation != 0) { // optimization 2: step 2/2
            pCanvas.restore();
        }
    }
}
