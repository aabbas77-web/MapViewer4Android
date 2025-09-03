package com.oghab.mapviewer.mapviewer;

import com.oghab.mapviewer.bonuspack.kml.KmlFeature;
import com.oghab.mapviewer.bonuspack.kml.KmlPlacemark;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayWithIW;

import java.util.Locale;

/**
 * Created by MapViewer on 09/04/2017.
 */

public class City {
    static public int POINT = 0;
    static public int POLYLINE = 1;
    static public int POLYGON = 2;

    public String strName;
    public double fLon,fLat;
    public float fAlt;
    public int geometry_type;// 0: point, 1: polyline, 2: polygon
    public int index;
    public KmlPlacemark placemark;

    public City()
    {

    }
}
