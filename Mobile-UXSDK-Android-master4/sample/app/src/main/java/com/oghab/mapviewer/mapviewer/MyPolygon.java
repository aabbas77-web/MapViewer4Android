package com.oghab.mapviewer.mapviewer;

import static com.oghab.mapviewer.MainActivity.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.LinearRing;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.PolyOverlayWithIW;

import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;
import com.oghab.mapviewer.bonuspack.kml.KmlPlacemark;
import com.oghab.mapviewer.mapviewer.MyPolygon;
import com.oghab.mapviewer.utils.mv_utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyPolygon extends PolyOverlayWithIW {

    protected MyPolygon.OnClickListener mOnClickListener;

    protected boolean mEdit = false;
    protected MapView map = null;
    private final ArrayList<MyMarker> mMarkers = new ArrayList<>();
    private final ArrayList<MyMarker> mMiddleMarkers = new ArrayList<>();
    KmlPlacemark placemark = null;
    Context context = null;

    // ===========================================================
    // Constructors
    // ===========================================================

//    public MyPolygon() {
//        this(null);
//    }

    public MyPolygon(MapView mapView) {
        super(mapView, true, true);
        map = mapView;
        mFillPaint = new Paint();
        mFillPaint.setColor(Color.TRANSPARENT);
        mFillPaint.setStyle(Paint.Style.FILL);
        mOutlinePaint.setColor(Color.BLACK);
        mOutlinePaint.setStrokeWidth(3.0f);
        mOutlinePaint.setStyle(Paint.Style.STROKE);
        mOutlinePaint.setAntiAlias(true);
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
                    if(mMiddleMarkers.size() > 0){
                        if(i > 0) {
                            GeoPoint c = new GeoPoint((p0.getLatitude()+p.getLatitude())/2.0,(p0.getLongitude()+p.getLongitude())/2.0);
                            mMiddleMarkers.get(i-1).setPosition(c);
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
//            mMarkers.add(marker);
//            map.getOverlays().add(marker);
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
                int count = points.size();
                if(count > 0){
                    GeoPoint p0 = points.get(0);
                    GeoPoint pn = points.get(count-1);
                    boolean bClosed = pn.equals(p0);
                    for(int i=0;i<count;i++){
                        GeoPoint p = points.get(i);
                        if(bClosed){
                            if(i == 0){
                                GeoPoint p1 = new GeoPoint(p.getLatitude()-0.0001,p.getLongitude());
                                add_mark(p1, 0);
                            }else if(i == count-1){
                                GeoPoint p2 = new GeoPoint(p.getLatitude()+0.0001,p.getLongitude());
                                add_mark(p2, i);
                            }else{
                                add_mark(p, i);
                            }
                        }else{
                            add_mark(p, i);
                        }
                        if(!Tab_Map.edit_mode){
                            if((i > 0) && (p0 != null)) {
                                GeoPoint c = new GeoPoint((p0.getLatitude()+p.getLatitude())/2.0,(p0.getLongitude()+p.getLongitude())/2.0);
                                add_middle_mark(c, i);
                            }
                            p0 = p;
                        }
                    }
                    if(!Tab_Map.edit_mode){
                        int i = 0;
                        GeoPoint p = points.get(i);
                        GeoPoint c = new GeoPoint((p0.getLatitude()+p.getLatitude())/2.0,(p0.getLongitude()+p.getLongitude())/2.0);
                        add_middle_mark(c, i);
                    }
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

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    /**
     * @deprecated Use {@link #getFillPaint()} instead
     */
    @Deprecated
    public int getFillColor() {
        return mFillPaint.getColor();
    }

    /**
     * @deprecated Use {@link #getOutlinePaint()} instead
     */
    @Deprecated
    public int getStrokeColor() {
        return mOutlinePaint.getColor();
    }

    /**
     * @deprecated Use {@link #getOutlinePaint()} instead
     */
    @Deprecated
    public float getStrokeWidth() {
        return mOutlinePaint.getStrokeWidth();
    }

    /**
     * @return the Paint used for the filling. This allows to set advanced Paint settings.
     * @since 6.0.2
     */
    public Paint getFillPaint() {
        return super.getFillPaint(); // public instead of protected
    }

    /**
     * @return the list of polygon's vertices.
     * Warning: changes on this list may cause strange results on the polygon display.
     * @deprecated Use {@link PolyOverlayWithIW#getActualPoints()} instead
     */
    @Deprecated
    public List<GeoPoint> getPoints() {
        return getActualPoints();
    }

    /**
     * @deprecated Use {@link #getFillPaint()} instead
     */
    @Deprecated
    public void setFillColor(final int fillColor) {
        mFillPaint.setColor(fillColor);
    }

    /**
     * @deprecated Use {@link #getOutlinePaint()} instead
     */
    @Deprecated
    public void setStrokeColor(final int color) {
        mOutlinePaint.setColor(color);
    }

    /**
     * @deprecated Use {@link #getOutlinePaint()} instead
     */
    @Deprecated
    public void setStrokeWidth(final float width) {
        mOutlinePaint.setStrokeWidth(width);
    }

    public void setHoles(List<? extends List<GeoPoint>> holes) {
        mHoles = new ArrayList<>(holes.size());
        for (List<GeoPoint> sourceHole : holes) {
            LinearRing newHole = new LinearRing(mPath);
            newHole.setGeodesic(mOutline.isGeodesic());
            newHole.setPoints(sourceHole);
            mHoles.add(newHole);
        }
    }

    /**
     * returns a copy of the holes this polygon contains
     *
     * @return never null
     */
    public List<List<GeoPoint>> getHoles() {
        List<List<GeoPoint>> result = new ArrayList<>(mHoles.size());
        for (LinearRing hole : mHoles) {
            result.add(hole.getPoints());
            //TODO: completely wrong:
            // hole.getPoints() doesn't return a copy but a direct handler to the internal list.
            // - if geodesic, this is not the same points as the original list.
        }
        return result;
    }

    /**
     * Build a list of GeoPoint as a circle.
     *
     * @param center         center of the circle
     * @param radiusInMeters
     * @return the list of GeoPoint
     */
    public static ArrayList<GeoPoint> pointsAsCircle(GeoPoint center, double radiusInMeters) {
        ArrayList<GeoPoint> circlePoints = new ArrayList<GeoPoint>(360 / 6);
        for (int f = 0; f < 360; f += 6) {
            GeoPoint onCircle = center.destinationPoint(radiusInMeters, f);
            circlePoints.add(onCircle);
        }
        return circlePoints;
    }

    /**
     * Build a list of GeoPoint as a rectangle.
     *
     * @param rectangle defined as a BoundingBox
     * @return the list of 4 GeoPoint
     */
    public static ArrayList<IGeoPoint> pointsAsRect(BoundingBox rectangle) {
        ArrayList<IGeoPoint> points = new ArrayList<IGeoPoint>(4);
        points.add(new GeoPoint(rectangle.getLatNorth(), rectangle.getLonWest()));
        points.add(new GeoPoint(rectangle.getLatNorth(), rectangle.getLonEast()));
        points.add(new GeoPoint(rectangle.getLatSouth(), rectangle.getLonEast()));
        points.add(new GeoPoint(rectangle.getLatSouth(), rectangle.getLonWest()));
        return points;
    }

    /**
     * Build a list of GeoPoint as a rectangle.
     *
     * @param center         of the rectangle
     * @param lengthInMeters on longitude
     * @param widthInMeters  on latitude
     * @return the list of 4 GeoPoint
     */
    public static ArrayList<IGeoPoint> pointsAsRect(GeoPoint center, double lengthInMeters, double widthInMeters) {
        ArrayList<IGeoPoint> points = new ArrayList<IGeoPoint>(4);
        GeoPoint east = center.destinationPoint(lengthInMeters * 0.5, 90.0f);
        GeoPoint south = center.destinationPoint(widthInMeters * 0.5, 180.0f);
        double westLon = center.getLongitude() * 2 - east.getLongitude();
        double northLat = center.getLatitude() * 2 - south.getLatitude();
        points.add(new GeoPoint(south.getLatitude(), east.getLongitude()));
        points.add(new GeoPoint(south.getLatitude(), westLon));
        points.add(new GeoPoint(northLat, westLon));
        points.add(new GeoPoint(northLat, east.getLongitude()));
        return points;
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

    @Override
    public void onDetach(MapView mapView) {
        super.onDetach(mapView);
        mOnClickListener = null;
    }


    //-- Polygon events listener interfaces ------------------------------------

    public interface OnClickListener {
        boolean onClick(MyPolygon polygon, MapView mapView, GeoPoint eventPos);
    }

    /**
     * default behaviour when no click listener is set
     */
    public boolean onClickDefault(MyPolygon polygon, MapView mapView, GeoPoint eventPos) {
        mapView.getController().setCenter(eventPos);
        polygon.setInfoWindowLocation(eventPos);
        polygon.showInfoWindow();
        return true;
    }

    /**
     * @param listener
     * @since 6.0.2
     */
    public void setOnClickListener(MyPolygon.OnClickListener listener) {
        mOnClickListener = listener;
    }

    /**
     * @since 6.2.0
     */
    @Override
    protected boolean click(final MapView pMapView, final GeoPoint pEventPos) {
        if (mOnClickListener == null) {
            return onClickDefault(this, pMapView, pEventPos);
        } else {
            return mOnClickListener.onClick(this, pMapView, pEventPos);
        }
    }

    @Override
    public void draw(final Canvas pCanvas, final Projection pProjection) {
        if (!isEnabled()) {
            return;
        }
        super.draw(pCanvas,pProjection);
    }
}
