package com.oghab.mapviewer.bonuspack.clustering;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.MotionEvent;

import org.osmdroid.api.IGeoPoint;
import com.oghab.mapviewer.bonuspack.kml.KmlFeature;
import com.oghab.mapviewer.mapviewer.MyMarker;
import com.oghab.mapviewer.mapviewer.MyPolygon;
import com.oghab.mapviewer.mapviewer.MyPolyline;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayWithIW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/** 
 * An overlay allowing to perform markers clustering. 
 * Usage: put your markers inside with add(Overlay), and add the MarkerClusterer to the map overlays. 
 * Depending on the zoom level, markers will be displayed separately, or grouped as a single Overlay. <br/>
 * 
 * This abstract class provides the framework. Sub-classes have to implement the clustering algorithm, 
 * and the rendering of a cluster. 
 * 
 * @author M.Kergall
 *
 */
public abstract class MarkerClusterer extends Overlay {

	/** impossible value for zoom level, to force clustering */
	protected static final int FORCE_CLUSTERING = -1;
	
	protected ArrayList<Overlay> mItems = new ArrayList<Overlay>();
	protected Point mPoint = new Point();
	protected ArrayList<StaticCluster> mClusters = new ArrayList<StaticCluster>();
	protected double mLastZoomLevel;
	protected Bitmap mClusterIcon;
	protected String mName, mDescription;
	
	// abstract methods: 
	
	/** clustering algorithm */
	public abstract ArrayList<StaticCluster> clusterer(MapView mapView);
	/** Build the marker for a cluster. */
	public abstract Overlay buildClusterMarker(StaticCluster cluster, MapView mapView);
	/** build clusters markers to be used at next draw */
	public abstract void renderer(ArrayList<StaticCluster> clusters, Canvas canvas, MapView mapView);
	
	public MarkerClusterer() {
		super();
		mLastZoomLevel = FORCE_CLUSTERING;
	}

	public void setName(String name){
		mName = name;
	}
	
	public String getName(){
		return mName;
	}
	
	public void setDescription(String description){
		mDescription = description;
	}
	
	public String getDescription(){
		return mDescription;
	}
	
	/** Set the cluster icon to be drawn when a cluster contains more than 1 marker. 
	 * If not set, default will be the default osmdroid marker icon (which is really inappropriate as a cluster icon). */
	public void setIcon(Bitmap icon){
		mClusterIcon = icon;
	}

	/** Add the Overlay. 
	 * Important: Markers added in a MarkerClusterer should not be added in the map overlays. */
	public void add(Overlay marker){
		mItems.add(marker);
	}

	/** remove the Overlay.
	 * Important: Markers added in a MarkerClusterer should not be added in the map overlays. */
	public void remove(Overlay marker){
		mItems.remove(marker);
	}

	/** Force a rebuild of clusters at next draw, even without a zooming action. 
	 * Should be done when you changed the content of a MarkerClusterer. */
	public void invalidate(){
		mLastZoomLevel = FORCE_CLUSTERING; 
	}
	
	/** @return the Overlay at id (starting at 0) */
	public Overlay getItem(int id){
		return mItems.get(id);
	}
	
	/** @return the list of Markers. */
	public ArrayList<Overlay> getItems(){
		return mItems;
	}

	protected void hideInfoWindows(){
		OverlayWithIW iw;
		for (Overlay m : mItems){
			if(m instanceof OverlayWithIW) {
				iw = (OverlayWithIW)m;
				if (iw.isInfoWindowOpen())
					iw.closeInfoWindow();
			}
		}
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		draw(canvas, mapView, shadow, mapView.getProjection());
//		if (shadow)
//			return;
//		//if zoom has changed and mapView is now stable, rebuild clusters:
//		double zoomLevel = mapView.getZoomLevelDouble();
//		if (zoomLevel != mLastZoomLevel && !mapView.isAnimating()){
//			hideInfoWindows();
//			mClusters = clusterer(mapView);
//			renderer(mClusters, canvas, mapView);
//			mLastZoomLevel = zoomLevel;
//		}
//
//		for (StaticCluster cluster:mClusters){
//			cluster.getMarker().draw(canvas, mapView.getProjection());
//		}
	}

	public void draw(Canvas canvas, MapView mapView, boolean shadow, Projection projection) {
		if (shadow)
			return;
		//if zoom has changed and mapView is now stable, rebuild clusters:
		double zoomLevel = mapView.getZoomLevelDouble();
		if (zoomLevel != mLastZoomLevel && !mapView.isAnimating()){
			hideInfoWindows();
			mClusters = clusterer(mapView);
			renderer(mClusters, canvas, mapView);
			mLastZoomLevel = zoomLevel;
		}

		for (StaticCluster cluster:mClusters){
			cluster.getMarker().draw(canvas, projection);
		}
	}

	public Iterable<StaticCluster> reversedClusters() {
		return new Iterable<StaticCluster>() {
			@Override
			public Iterator<StaticCluster> iterator() {
				final ListIterator<StaticCluster> i = mClusters.listIterator(mClusters.size());
				return new Iterator<StaticCluster>() {
					@Override
					public boolean hasNext() {
						return i.hasPrevious();
					}

					@Override
					public StaticCluster next() {
						return i.previous();
					}

					@Override
					public void remove() {
						i.remove();
					}
				};
			}
		};
	}

	@Override public boolean onSingleTapConfirmed(final MotionEvent event, final MapView mapView){
		for (final StaticCluster cluster : reversedClusters()) {
			if (cluster.getMarker().onSingleTapConfirmed(event, mapView))
				return true;
		}
		return false;
	}
	
	@Override public boolean onLongPress(final MotionEvent event, final MapView mapView) {
		for (final StaticCluster cluster : reversedClusters()) {
			if (cluster.getMarker().onLongPress(event, mapView))
				return true;
		}
		return false;
	}

	@Override public boolean onTouchEvent(final MotionEvent event, final MapView mapView) {
		for (StaticCluster cluster : reversedClusters()) {
			if (cluster.getMarker().onTouchEvent(event, mapView))
				return true;
		}
		return false;
	}

	@Override public BoundingBox getBounds(){
		if (mItems.size() == 0)
				return null;
		double minLat = Double.MAX_VALUE;
		double minLon = Double.MAX_VALUE;
		double maxLat = -Double.MAX_VALUE;
		double maxLon = -Double.MAX_VALUE;
		GeoPoint p;
		for (final Overlay item : mItems) {
			if(item instanceof MyMarker)
			{
				MyMarker marker0 = (MyMarker) item; //the marker on which you click to open the bubble
				p = marker0.getPosition();
				final double latitude = p.getLatitude();
				final double longitude = p.getLongitude();
				minLat = Math.min(minLat, latitude);
				minLon = Math.min(minLon, longitude);
				maxLat = Math.max(maxLat, latitude);
				maxLon = Math.max(maxLon, longitude);
			}
			else
			if(item instanceof MyPolyline)
			{
				MyPolyline polyline0 = (MyPolyline) item;
				List<GeoPoint> points = polyline0.getActualPoints();
				for(int i=0;i<points.size();i++){
					p = points.get(i);
					final double latitude = p.getLatitude();
					final double longitude = p.getLongitude();
					minLat = Math.min(minLat, latitude);
					minLon = Math.min(minLon, longitude);
					maxLat = Math.max(maxLat, latitude);
					maxLon = Math.max(maxLon, longitude);
				}
			}
			else
			if(item instanceof MyPolygon)
			{
				MyPolygon polygon0 = (MyPolygon) item;
				List<GeoPoint> points = polygon0.getActualPoints();
				for(int i=0;i<points.size();i++){
					p = points.get(i);
					final double latitude = p.getLatitude();
					final double longitude = p.getLongitude();
					minLat = Math.min(minLat, latitude);
					minLon = Math.min(minLon, longitude);
					maxLat = Math.max(maxLat, latitude);
					maxLon = Math.max(maxLon, longitude);
				}
			}
		}
		return new BoundingBox(maxLat, maxLon, minLat, minLon);
	}

}
