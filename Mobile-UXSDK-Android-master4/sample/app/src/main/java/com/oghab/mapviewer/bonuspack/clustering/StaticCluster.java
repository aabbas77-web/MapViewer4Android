package com.oghab.mapviewer.bonuspack.clustering;

import com.oghab.mapviewer.utils.mv_utils;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;

/** 
 * Cluster of Markers. 
 * @author M.Kergall
 */
public class StaticCluster {
	protected final ArrayList<Overlay> mItems = new ArrayList<Overlay>();
	protected GeoPoint mCenter;
	protected Overlay mMarker;
	
	public StaticCluster(GeoPoint center) {
	    mCenter = center;
	}
	
	public void setPosition(GeoPoint center){
		mCenter = center;
	}
	
	public GeoPoint getPosition() {
	    return mCenter;
	}
	
	public int getSize() {
	    return mItems.size();
	}
	
	public Overlay getItem(int index) {
	    return mItems.get(index);
	}
	
	public boolean add(Overlay t) {
	    return mItems.add(t);
	}
	
	/** set the Overlay to be displayed for this cluster */
	public void setMarker(Overlay marker){
		mMarker = marker;
	}
	
	/** @return the Overlay to be displayed for this cluster */
	public Overlay getMarker(){
		return mMarker;
	}

	public BoundingBox getBoundingBox(){
		if (getSize()==0)
			return null;
//		GeoPoint p = getItem(0).getPosition();
//		BoundingBox bb = new BoundingBox(p.getLatitude(), p.getLongitude(), p.getLatitude(), p.getLongitude());
//		for (int i=1; i<getSize(); i++) {
//			p = getItem(i).getPosition();
//			double minLat = Math.min(bb.getLatSouth(), p.getLatitude());
//			double minLon = Math.min(bb.getLonWest(), p.getLongitude());
//			double maxLat = Math.max(bb.getLatNorth(), p.getLatitude());
//			double maxLon = Math.max(bb.getLonEast(), p.getLongitude());
//			bb.set(maxLat, maxLon, minLat, minLon);
//		}
		GeoPoint p = mv_utils.getPosition(getItem(0));
		BoundingBox bb = new BoundingBox(p.getLatitude(), p.getLongitude(), p.getLatitude(), p.getLongitude());
		for (int i=1; i<getSize(); i++) {
			p = mv_utils.getPosition(getItem(i));
			double minLat = Math.min(bb.getLatSouth(), p.getLatitude());
			double minLon = Math.min(bb.getLonWest(), p.getLongitude());
			double maxLat = Math.max(bb.getLatNorth(), p.getLatitude());
			double maxLon = Math.max(bb.getLonEast(), p.getLongitude());
			bb.set(maxLat, maxLon, minLat, minLon);
		}
		return bb;
	}
}
