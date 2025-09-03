package com.oghab.mapviewer.mapviewer;

import static com.oghab.mapviewer.MainActivity.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;
import com.oghab.mapviewer.bonuspack.kml.KmlDocument;
import com.oghab.mapviewer.bonuspack.kml.KmlFolder;
import com.oghab.mapviewer.bonuspack.kml.KmlPlacemark;
import com.oghab.mapviewer.utils.mv_utils;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.LinearRing;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.PolyOverlayWithIW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A polyline is a list of points, where line segments are drawn between consecutive points.
 * Mimics the Polyline class from Google Maps Android API v2 as much as possible. Main differences:<br>
 * - Doesn't support Z-Index: drawing order is the order in map overlays<br>
 * - Supports InfoWindow (must be a BasicInfoWindow). <br>
 * <p>
 * <img alt="Class diagram around Marker class" width="686" height="413" src='src='./doc-files/marker-infowindow-classes.png' />
 *
 * @author M.Kergall
 * @see <a href="http://developer.android.com/reference/com/google/android/gms/maps/model/Polyline.html">Google Maps Polyline</a>
 */
public class MyPolyline extends PolyOverlayWithIW {

    protected MyPolyline.OnClickListener mOnClickListener;

    protected boolean mEdit = false;
    protected MapView map = null;
    private final ArrayList<MyMarker> mMarkers = new ArrayList<>();
    private final ArrayList<MyMarker> mMiddleMarkers = new ArrayList<>();
    KmlPlacemark placemark = null;
    Context context = null;

    /**
     * If MapView is not provided, infowindow popup will not function unless you set it yourself.
     */
//    public MyPolyline() {
//        this(null);
//    }

    /**
     * If MapView is null, infowindow popup will not function unless you set it yourself.
     */
    public MyPolyline(MapView mapView) {
        this(mapView, false);
        map = mapView;
    }

    /**
     * @since 6.2.0
     */
    public MyPolyline(final MapView pMapView, final boolean pUsePath, final boolean pClosePath) {
        super(pMapView, pUsePath, pClosePath);
        map = pMapView;
        //default as defined in Google API:
        mOutlinePaint.setColor(Color.BLACK);
        mOutlinePaint.setStrokeWidth(3.0f);
        mOutlinePaint.setStyle(Paint.Style.STROKE);
        mOutlinePaint.setStrokeJoin(Paint.Join.ROUND);
        mOutlinePaint.setStrokeCap(Paint.Cap.ROUND);
        mOutlinePaint.setAntiAlias(true);
    }

    /**
     * @param pUsePath true if you want the drawing to use Path instead of Canvas.drawLines
     *                 Not recommended in all cases, given the performances.
     *                 Useful though if you want clean alpha vertices
     *                 cf. https://github.com/osmdroid/osmdroid/issues/1280
     * @since 6.1.0
     */
    public MyPolyline(final MapView pMapView, final boolean pUsePath) {
        this(pMapView, pUsePath, false);
        map = pMapView;
    }

    MyMarker.OnMarkerDragListener onMarkerDrag = new MyMarker.OnMarkerDragListener() {
        @Override
        public void onMarkerDrag(MyMarker mark) {
            try{
                int idx = Integer.parseInt(mark.getId());
                if(placemark != null){
                    if(placemark.mGeometry.mCoordinates.size() > 0){
                        if((idx >= 0) && (idx < placemark.mGeometry.mCoordinates.size())){
                            placemark.mGeometry.mCoordinates.set(idx, mark.getPosition());
                        }
                    }
                }
                List<GeoPoint> points = new ArrayList<>(getActualPoints());
                points.set(idx, mark.getPosition());
                setPoints(points);

                // update middle points
                GeoPoint p0 = null;
                for(int i=0;i<points.size();i++){
                    GeoPoint p = points.get(i);
                    if(mMiddleMarkers.size() > 0) {
                        if (i > 0) {
                            GeoPoint c = new GeoPoint((p0.getLatitude() + p.getLatitude()) / 2.0, (p0.getLongitude() + p.getLongitude()) / 2.0);
                            mMiddleMarkers.get(i - 1).setPosition(c);
                        }
                    }
                    p0 = p;
                }

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
                            int idx = Integer.parseInt(mark.getId());
                            List<GeoPoint> points = new ArrayList<>(getActualPoints());
                            if(placemark != null){
                                if(placemark.mGeometry.mCoordinates.size() > 0) {
                                    if ((idx >= 0) && (idx < placemark.mGeometry.mCoordinates.size())) {
                                        placemark.mGeometry.mCoordinates.remove(idx);
                                    }
                                }
                            }
                            points.remove(idx);
                            setPoints(points);
                            map.postInvalidate();

                            clear_markers();
                            setEdit(context,placemark,true);
                            map.postInvalidate();

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

    MyMarker.OnMarkerLongPressListener onMarkerLongPressAddPointListener = new MyMarker.OnMarkerLongPressListener() {
        @Override
        public boolean onMarkerLongPress(MyMarker mark, MapView mapView) {
            try{
                new AlertDialog.Builder(activity)
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.add)
                        .setMessage(R.string.are_you_sure_add)
                        .setPositiveButton(R.string.yes_message, (dialog, which) -> {
                            int idx = Integer.parseInt(mark.getId());
                            List<GeoPoint> points = new ArrayList<>(getActualPoints());
                            if(placemark != null){
                                if(placemark.mGeometry.mCoordinates.size() > 0) {
                                    if ((idx >= 0) && (idx < placemark.mGeometry.mCoordinates.size())) {
                                        placemark.mGeometry.mCoordinates.add(idx, mark.getPosition());
                                    }
                                }
                            }
                            points.add(idx,mark.getPosition());
                            setPoints(points);
                            map.postInvalidate();

                            clear_markers();
                            setEdit(context,placemark,true);
                            map.postInvalidate();

                            //Tab_Messenger.showToast("["+mark.getTitle() +"] Added...");
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

    MyMarker.OnMarkerClickListener onMarkerClickListener = new MyMarker.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(MyMarker mark, MapView mapView) {
            int idx0 = Integer.parseInt(mark.getId());
            Tab_Messenger.showToast(Integer.toString(idx0+1));
//            Tab_Messenger.showToast("Clicked");

            List<GeoPoint> points = getActualPoints();
            if(points.size() > 0) {
                if(Tab_Map.target_Marker != null) {
                    int idx = Integer.parseInt(mark.getId());
                    if(idx >= 0) {
                        if (idx < points.size()) {
                            Tab_Map.target_Marker.setEnabled(true);
                            Tab_Map.target_Marker.setId(Integer.toString(idx));
                            Tab_Map.target_Marker.setPosition(points.get(idx));
                            Tab_Map.target_Marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                            Tab_Map.target_Marker.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                            Tab_Map.target_Marker.setIcon(mv_utils.getDrawable(MainActivity.ctx, R.drawable.marker_target));
                            Tab_Map.target_Marker.setTitle(activity.getString(R.string.target));
                            Tab_Map.target_Marker.setInfo1("");
                            Tab_Map.target_Marker.setInfo2("");
                            MainActivity.next_point();
                        } else {
                            Tab_Map.navigation_mode = false;
                            Tab_Map.target_Marker.setEnabled(false);
                            Tab_Map.target_Marker.setId(Integer.toString(-1));
                            MainActivity.mission_finished(true);
                        }
                    }
                }
            }
            return true;
        }
    };

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
                marker.setNumberIcon(String.format(Locale.ENGLISH, "%02d", idx+1),Math.round(2*(2+Tab_Map.map_text_size)),Math.round(2*(2+Tab_Map.map_text_size)),Math.round((2+Tab_Map.map_text_size)/2.0f));
                marker.setOnMarkerDragListener(null);
                marker.setOnMarkerClickListener(onMarkerClickListener);
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
            mMarkers.add(marker);
            map.getOverlays().add(marker);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void add_middle_mark(GeoPoint p, int idx){
        if(map == null) return;
        try{
            MyMarker marker = new MyMarker(map);
            marker.setPosition(p);
            marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_CENTER);
            marker.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_CENTER);
            marker.setIcon(mv_utils.getDrawable(context, R.drawable.add_4x));
            marker.setTitle("");
            marker.setInfo1("");
            marker.setInfo2("");
            marker.setSubDescription("");
            marker.setEnabled(true);
            marker.setDraggable(false);
            marker.setId(String.valueOf(idx));
            marker.setOnMarkerDragListener(null);
            marker.setOnMarkerLongPressListener(onMarkerLongPressAddPointListener);
            marker.setOnMarkerClickListener(null);
            marker.setPanToView(false);
            marker.setInfoWindow(null);
            mMiddleMarkers.add(marker);
            map.getOverlays().add(marker);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void clear_markers(){
        try{
            for(int i=0;i<mMarkers.size();i++){
                Overlay overlay = mMarkers.get(i);
                if (overlay != null) {
                    map.getOverlays().remove(overlay);
                }
            }
            mMarkers.clear();

            for(int i=0;i<mMiddleMarkers.size();i++){
                Overlay overlay = mMiddleMarkers.get(i);
                if (overlay != null) {
                    map.getOverlays().remove(overlay);
                }
            }
            mMiddleMarkers.clear();
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
                List<GeoPoint> points = new ArrayList<>(getActualPoints());
                if(points.size() > 0){
                    GeoPoint p0 = null;
                    for(int i=0;i<points.size();i++){
                        GeoPoint p = points.get(i);
                        add_mark(p, i);
                        if(!Tab_Map.edit_mode){
                            if((i > 0) && (p0 != null)) {
                                GeoPoint c = new GeoPoint((p0.getLatitude()+p.getLatitude())/2.0,(p0.getLongitude()+p.getLongitude())/2.0);
                                add_middle_mark(c, i);
                            }
                            p0 = p;
                        }
                    }
//                    if(!Tab_Map.edit_mode){
//                        int i = 0;
//                        GeoPoint p = points.get(i);
//                        GeoPoint c = new GeoPoint((p0.getLatitude()+p.getLatitude())/2.0,(p0.getLongitude()+p.getLongitude())/2.0);
//                        add_middle_mark(c, i);
//                    }
                }
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

    /**
     * @return a copy of the actual points
     * @deprecated Use {@link #getActualPoints()} instead; copy the list if necessary
     */
    @Deprecated
    public ArrayList<GeoPoint> getPoints() {
        return new ArrayList<>(getActualPoints());
    }

    /**
     * @deprecated Use {{@link #getOutlinePaint()}} instead
     */
    @Deprecated
    public int getColor() {
        return mOutlinePaint.getColor();
    }

    /**
     * @deprecated Use {{@link #getOutlinePaint()}} instead
     */
    @Deprecated
    public float getWidth() {
        return mOutlinePaint.getStrokeWidth();
    }

    /**
     * @deprecated Use {{@link #getOutlinePaint()}} instead
     */
    @Deprecated
    public Paint getPaint() {
        return getOutlinePaint();
    }

    /**
     * @deprecated Use {{@link #getOutlinePaint()}} instead
     */
    @Deprecated
    public void setColor(int color) {
        mOutlinePaint.setColor(color);
    }

    /**
     * @deprecated Use {{@link #getOutlinePaint()}} instead
     */
    @Deprecated
    public void setWidth(float width) {
        mOutlinePaint.setStrokeWidth(width);
    }

    public void setOnClickListener(MyPolyline.OnClickListener listener) {
        mOnClickListener = listener;
    }

    /**
     * Internal method used to ensure that the infowindow will have a default position in all cases,
     * so that the user can call showInfoWindow even if no tap occured before.
     * Currently, set the position on the "middle" point of the polyline.
     */
    public interface OnClickListener {
        abstract boolean onClick(MyPolyline polyline, MapView mapView, GeoPoint eventPos);
    }

    /**
     * default behaviour when no click listener is set
     */
    public boolean onClickDefault(MyPolyline polyline, MapView mapView, GeoPoint eventPos) {
        mapView.getController().setCenter(eventPos);
        polyline.setInfoWindowLocation(eventPos);
        polyline.showInfoWindow();
        return true;
    }

    @Override
    public void onDetach(MapView mapView) {
        super.onDetach(mapView);
        mOnClickListener = null;
    }

    /**
     * @return aggregate distance (in meters)
     * @since 6.0.3
     */
    public double getDistance() {
        return mOutline.getDistance();
    }

    public GeoPoint getPosition() {
        double lon = 0;
        double lat = 0;
        double alt = 0;
        List<GeoPoint> points = new ArrayList<>(getActualPoints());
        int n = points.size();
        if(n > 0){
            for(int i=0;i<points.size();i++){
                GeoPoint p = points.get(i);
                lon += p.getLongitude();
                lat += p.getLatitude();
                alt += p.getAltitude();
            }
            lon /= n;
            lat /= n;
            alt /= n;
        }
        return new GeoPoint(lat, lon, alt);
    }

    public void toggle_direction(){
        List<GeoPoint> points1 = new ArrayList<>(getActualPoints());
        int n1 = points1.size();
        if(n1 < 2)  return;
        List<GeoPoint> points2 = new ArrayList<>();
        for(int i=0;i<n1;i++){
            GeoPoint p = points1.get(n1-1 - i);
            points2.add(p);
        }
        setPoints(points2);
    }

    /**
     * @since 6.2.0
     */
    @Override
    protected boolean click(final MapView pMapView, final GeoPoint pEventPos) {
        if (mOnClickListener == null) {
            return onClickDefault(this, pMapView, pEventPos);
        }
        return mOnClickListener.onClick(this, pMapView, pEventPos);
    }

    @Override
    public void draw(final Canvas pCanvas, final Projection pProjection) {
        if (!isEnabled()) {
            return;
        }
        super.draw(pCanvas,pProjection);
    }
}
