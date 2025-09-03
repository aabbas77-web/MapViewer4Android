package com.oghab.mapviewer.mapviewer;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.DefaultOverlayManager;
import org.osmdroid.views.overlay.TilesOverlay;


public class MyOverlayManager extends DefaultOverlayManager{
    static MapView map = null;

    /**
     * Create MyOverlayManager
     */
    public static MyOverlayManager create(MapView mapView, Context context) {
        map = mapView;
        MapTileProviderBase mTileProvider = mapView.getTileProvider();
        TilesOverlay tilesOverlay = new TilesOverlay(mTileProvider, context);
        mapView.setOverlayManager(new MyOverlayManager(tilesOverlay));
        return new MyOverlayManager(tilesOverlay);
    }

    /**
     * Default constructor
     */
    public MyOverlayManager(final TilesOverlay tilesOverlay) {
        super(tilesOverlay);
    }

    @Override
    public void onDraw(final Canvas c, final MapView pMapView) {
        super.onDraw(c, pMapView);
        if(pMapView != null)    pMapView.invalidate();//potential fix for #52
    }

    /**
     * @since 6.1.0
     */
    @Override
    public void onDraw(final Canvas c, final Projection pProjection) {
        super.onDraw(c, pProjection);
        if(map != null) map.invalidate();//potential fix for #52
    }

    /**
     * Override event & do nothing
     */
//    @Override
//    public boolean onDoubleTap(MotionEvent e, MapView pMapView) {
//        return true;
//    }

    /**
     * Override event & do nothing
     */
//    @Override
//    public boolean onDoubleTapEvent(MotionEvent e, MapView pMapView) {
//        return true;
//    }
}
