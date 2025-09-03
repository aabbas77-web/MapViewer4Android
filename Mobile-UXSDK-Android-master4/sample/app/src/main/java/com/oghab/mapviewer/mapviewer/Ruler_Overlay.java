package com.oghab.mapviewer.mapviewer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.DisplayMetrics;

import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @author Ali Abbas
 */

public class Ruler_Overlay extends Overlay {
//    public class Ruler_Overlay extends Overlay implements Overlay.Snappable {
//    private boolean bClicked = false;

	protected GeoPoint mPos1 = null,mPos2 = null;
	protected Point mPix1, mPix2;
    private Paint p_red,p_green;
	private float dist = 0.0f;
    private float bearing = 0.0f;
    private String mDistance = "";
    private String mAzimuth = "";
    private String mText2 = "";
    private String mText3 = "";
    DisplayMetrics dm;

    protected boolean mEdit = false;
    protected MapView map = null;
    public final ArrayList<MyMarker> mMarkers = new ArrayList<>();
    Context context = null;
    protected Ruler_Overlay.OnRulerListener mOnMeasureListener = null;

	public Ruler_Overlay(Context context, MapView mapView) {
		super();
        try
        {
            this.context = context;
            this.map = mapView;
            // Get the display metrics
            Resources resources = context.getResources();
            dm = resources.getDisplayMetrics();

            mPix1 = new Point();
            mPix2 = new Point();

            p_red = new Paint();
            p_red.setColor(0xFFCC0000);  // alpha.r.g.b
            p_red.setStyle(Paint.Style.FILL_AND_STROKE);
            p_red.setStrokeWidth(2.0F);
            p_red.setAntiAlias(true);
            p_red.setStrokeCap(Paint.Cap.BUTT);
            p_red.setDither(false);
            p_red.setTextSize(dm.density * Tab_Map.map_text_size);

            p_green = new Paint();
            p_green.setColor(Color.YELLOW);
            p_green.setStyle(Paint.Style.STROKE);
            p_green.setStrokeWidth(5.0F);
            p_green.setAntiAlias(true);
            p_green.setStrokeCap(Paint.Cap.BUTT);
            p_green.setDither(false);
            p_green.setTextSize(dm.density * Tab_Map.map_text_size);

            mOnMeasureListener = null;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
	}

    public String getDistance(){
        return mDistance;
    }

    public String getAzimuth(){
        return mAzimuth;
    }

    public String getText2(){
        return mText2;
    }

    public String getText3(){
        return mText3;
    }

    public GeoPoint getPos1(){
        return mPos1.clone();
    }

    public void update(){
        double lat1 = mPos1.getLatitude();
        double lng1 = mPos1.getLongitude();
        double lat2 = mPos2.getLatitude();
        double lng2 = mPos2.getLongitude();
        float[] list = new float[2];
        Location.distanceBetween(lat1, lng1, lat2, lng2, list);
        dist = Math.round(list[0]);
        bearing = (float) MainActivity.db_deg(list[1]);

        float fAlt1 = MainActivity.GetHeightJNI(lng1,lat1);
        float fAlt2 = MainActivity.GetHeightJNI(lng2,lat2);

        mDistance = String.format(Locale.ENGLISH, "%.01f", dist);
        mAzimuth = String.format(Locale.ENGLISH, "%.02f", bearing);

        mText2 = "";
//        mText2 += MainActivity.CoordinatesToDMS(lng1,lat1,true)+" ,A: "+String.format(Locale.ENGLISH, "%.01f", fAlt1);
        mText2 += Tab_Map.convert_coordinates(lng1,lat1,Tab_Map.map_coordinate_index,true,true)+" ,A: "+String.format(Locale.ENGLISH, "%.01f", fAlt1);

        mText3 = "";
//        mText3 += MainActivity.CoordinatesToDMS(lng2,lat2,true)+" ,A: "+String.format(Locale.ENGLISH, "%.01f", fAlt2);
        mText3 += Tab_Map.convert_coordinates(lng2,lat2,Tab_Map.map_coordinate_index,true,true)+" ,A: "+String.format(Locale.ENGLISH, "%.01f", fAlt2);

        if(mOnMeasureListener != null){
            mOnMeasureListener.onMeasure();
        }
    }

    public static double[] findGeoCoordinates(double distanceInMeters,
                                              double azimuthAngleInDegrees, double refLatitude,
                                              double refLongitude) {
        double lat1 = Math.toRadians(refLatitude); // Current lat point
        // converted to radians
        double lon1 = Math.toRadians(refLongitude); // Current long point
        // converted to radians
        double radius = getEarthRadiusForLatitude(lat1); // #Radius of the Earth

        azimuthAngleInDegrees = Math.toRadians(azimuthAngleInDegrees);

        double lat2 = Math.asin(Math.sin(lat1)
                * Math.cos(distanceInMeters / radius) + Math.cos(lat1)
                * Math.sin(distanceInMeters / radius)
                * Math.cos(azimuthAngleInDegrees));

        double lon2 = lon1
                + Math.atan2(
                Math.sin(azimuthAngleInDegrees)
                        * Math.sin(distanceInMeters / radius)
                        * Math.cos(lat1),
                Math.cos(distanceInMeters / radius)
                        - Math.sin(lat1) * Math.sin(lat2));

        return new double[] { Math.toDegrees(lat2), Math.toDegrees(lon2) };
    }

    private static double getEarthRadiusForLatitude(double latitude) {
        double equatorRadius = 6378.137; // equatorial radius in km
        double polarRadius = 6356.7523142; // polar radius in km
        return equatorRadius
                * Math.sqrt(Math.pow(polarRadius, 4)
                / Math.pow(equatorRadius, 4)
                * Math.pow((Math.sin(latitude)), 2)
                + Math.pow(Math.cos(latitude), 2))
                / Math.sqrt(1
                - (1 - (polarRadius * polarRadius)
                / (equatorRadius * equatorRadius))
                * Math.pow(Math.sin(latitude), 2));
    }

    public void update2(double distance,double azimuth){
        double lat1 = mPos1.getLatitude();
        double lng1 = mPos1.getLongitude();

        double[] res = findGeoCoordinates(distance/1000.0, azimuth, lat1, lng1);
        double lat2 = res[0];
        double lng2 = res[1];
        setPos2(new GeoPoint(lat2,lng2));
    }

    public void setOnMeasureListener(OnRulerListener listener){
        mOnMeasureListener = listener;
    }

	public void setPos1(GeoPoint position){
        try
        {
            mPos1 = position.clone();
//            dist = getDistanceInMeters(mPos1,mPos2);
//            bearing = getBearing(mPos1,mPos2);

            if(mPos2 != null) {
                update();
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
	}

	public GeoPoint getPos2(){
		return mPos2.clone();
	}

	public void setPos2(GeoPoint position){
        try
        {
            mPos2 = position.clone();
//            dist = getDistanceInMeters(mPos1,mPos2);
//            bearing = getBearing(mPos1,mPos2);

            if(mPos1 != null) {
                update();
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
	}

	@Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        draw(canvas, mapView, shadow, mapView.getProjection());
//        try
//        {
//            if (shadow)	return;
//            if (!isEnabled())   return;
//
//            final Projection pj = mapView.getProjection();
//
//            pj.toPixels(mPos1, mPix1);
//            pj.toPixels(mPos2, mPix2);
//
//            float len = (float)Math.sqrt((mPix2.x - mPix1.x)*(mPix2.x - mPix1.x)+(mPix2.y - mPix1.y)*(mPix2.y - mPix1.y));
//
//            canvas.drawLine(mPix1.x, mPix1.y, mPix2.x, mPix2.y, p_green);
//            canvas.drawCircle(mPix1.x, mPix1.y, len, p_green);
//
////            text = "";
////            text += "dist: "+String.format(Locale.ENGLISH, "%.01f", dist) + " m, bearing: ";
////            text += String.format(Locale.ENGLISH, "%.02f", bearing) + (char)0x00B0;
////            canvas.drawText(text, mPix2.x, mPix2.y, p_red);
//        }
//        catch (Throwable ex)
//        {
//            MainActivity.MyLog(ex);
//        }
    }

    public void draw(Canvas canvas, MapView mapView, boolean shadow, Projection projection) {
        try
        {
            if (shadow)	return;
            if (!isEnabled())   return;

            final Projection pj = projection;

            pj.toPixels(mPos1, mPix1);
            pj.toPixels(mPos2, mPix2);

            float len = (float)Math.sqrt((mPix2.x - mPix1.x)*(mPix2.x - mPix1.x)+(mPix2.y - mPix1.y)*(mPix2.y - mPix1.y));

            canvas.drawLine(mPix1.x, mPix1.y, mPix2.x, mPix2.y, p_green);
            canvas.drawCircle(mPix1.x, mPix1.y, len, p_green);

//            text = "";
//            text += "dist: "+String.format(Locale.ENGLISH, "%.01f", dist) + " m, bearing: ";
//            text += String.format(Locale.ENGLISH, "%.02f", bearing) + (char)0x00B0;
//            canvas.drawText(text, mPix2.x, mPix2.y, p_red);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

//	@Override
//	public boolean onTouchEvent(final MotionEvent event, final MapView mapView) {
//        int action = event.getAction();
//        GeoPoint eventPosition = (GeoPoint)mapView.getProjection().fromPixels((int)event.getX(), (int)event.getY());
//        switch(action)
//        {
//            case MotionEvent.ACTION_DOWN: {
//                bClicked = true;
//                mPos1 = eventPosition;
//                break;
//            }
//            case MotionEvent.ACTION_MOVE: {
//                mPos2 = eventPosition;
//                if (bClicked) update();
//                break;
//            }
//            case MotionEvent.ACTION_UP: {
//                mPos2 = eventPosition;
//                if (bClicked) update();
//                bClicked = false;
//                break;
//            }
//            default: {
//                break;
//            }
//        }
//        return super.onTouchEvent(event, mapView);
//	}

    public float getDistanceInMeters(GeoPoint p1, GeoPoint p2) {
        try
        {
            if(p1 == null)  return 0.0f;
            if(p2 == null)  return 0.0f;
            double lat1 = p1.getLatitude();
            double lng1 = p1.getLongitude();
            double lat2 = p2.getLatitude();
            double lng2 = p2.getLongitude();
            float [] dist = new float[1];
            Location.distanceBetween(lat1, lng1, lat2, lng2, dist);
            return dist[0];
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
            return 0.0f;
        }
    }

    private static double degreeToRadians(double latLong) {
        return (Math.PI * latLong / 180.0);
    }

    private static double radiansToDegree(double latLong) {
        return (latLong * 180.0 / Math.PI);
    }

    public float getBearing(GeoPoint p1, GeoPoint p2) {
        try
        {
            if(p1 == null)  return 0.0f;
            if(p2 == null)  return 0.0f;
            double lat1 = p1.getLatitude();
            double lng1 = p1.getLongitude();
            double lat2 = p2.getLatitude();
            double lng2 = p2.getLongitude();


            float[] res = MainActivity.CalculateAngles(lng1, lat1, 0.0, lng2, lat2, 0.0);
            double azi = MainActivity.db_deg(Math.toDegrees(res[0]));
            return (float)azi;


//            double fLat = degreeToRadians(lat1);
//            double fLong = degreeToRadians(lng1);
//            double tLat = degreeToRadians(lat2);
//            double tLong = degreeToRadians(lng2);
//
//            double dLon = (tLong - fLong);
//
//            float degree = (float)radiansToDegree(Math.atan2(Math.sin(dLon) * Math.cos(tLat),
//                    Math.cos(fLat) * Math.sin(tLat) - Math.sin(fLat) * Math.cos(tLat) * Math.cos(dLon)));
//
//            if (degree >= 0) {
//                return degree;
//            } else {
//                return 360 + degree;
//            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
            return 0.0f;
        }
    }

//    private void update()
//    {
//        dist = getDistanceInMeters(mPos1,mPos2);
//    }

    //move mapView
    private boolean doPanning(GeoPoint e, MapView mapView)
    {
        try
        {
//        GeoPoint mapCenter = (GeoPoint) mapView.getMapCenter();
//        GeoPoint panToCenter = new GeoPoint((int)(mapCenter.getLatitudeE6() + (e.getLatitude() - savedTouchedY) * 1E5),
//                (int)(mapCenter.getLongitudeE6() - (e.getLongitude() - savedTouchedX) * 1E5));
//        mapView.getController().setCenter(panToCenter);
//        savedTouchedX = e.getLongitude();
//        savedTouchedY = e.getLatitude();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return true;
    }

    MyMarker.OnMarkerDragListener onMarkerDrag = new MyMarker.OnMarkerDragListener() {
        @Override
        public void onMarkerDrag(MyMarker mark) {
            try{
                int idx = Integer.parseInt(mark.getId());
                if(idx == 0)
                    setPos1(mark.getPosition());
                else
                    setPos2(mark.getPosition());

                MainActivity.tab_map.updateInfo(true);
//                map.postInvalidate();
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

    public void add_mark(GeoPoint p, int idx){
        if(map == null) return;
        try{
            MyMarker marker = new MyMarker(map);
            marker.setPosition(p);
            marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_CENTER);
            marker.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_CENTER);
            if(idx == 0) {
//                marker.setIcon(mv_utils.getDrawable(context, R.drawable.ruler1));
                Bitmap cross = BitmapFactory.decodeResource(context.getResources(),R.drawable.sniper);
                cross = MainActivity.addColor(cross,Color.RED);
                Drawable drawable = new BitmapDrawable(context.getResources(), cross);
                marker.setIcon(drawable);
            }else{
//                marker.setIcon(mv_utils.getDrawable(context, R.drawable.ruler2));
                Bitmap cross = BitmapFactory.decodeResource(context.getResources(),R.drawable.sniper);
                cross = MainActivity.addColor(cross,Color.BLUE);
                Drawable drawable = new BitmapDrawable(context.getResources(), cross);
                marker.setIcon(drawable);
            }
            marker.setTitle("");
            marker.setInfo1("");
            marker.setInfo2("");
            marker.setSubDescription("");
            marker.setEnabled(true);
            marker.setDraggable(true);
            marker.setId(String.valueOf(idx));
            marker.setOnMarkerDragListener(onMarkerDrag);
            marker.setOnMarkerClickListener(null);
            marker.setOnMarkerLongPressListener(null);
            marker.setOnMarkerLongPressListener(null);
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

    public void clear_markers(){
        try{
            for(int i=0;i<mMarkers.size();i++){
                Overlay overlay = mMarkers.get(i);
                if (overlay != null) {
                    map.getOverlays().remove(overlay);
                }
            }
            mMarkers.clear();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void setEdit(boolean value) {
        mEdit = value;
        if(map == null) return;
        try{
            clear_markers();
            if(mEdit){
                add_mark(mPos1, 0);
                add_mark(mPos2, 1);
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

    public interface OnRulerListener {
        abstract boolean onMeasure();
    }

//    private final GeoPoint MAP_CENTER = new GeoPoint(33.513805, 36.276518);
//
//    @Override
//    public boolean onSnapToItem(int x, int y, Point snapPoint, IMapView mapView) {
//        final IProjection projection = mapView.getProjection();
//        projection.toPixels(MAP_CENTER, snapPoint);
//        return true;
//    }
}
