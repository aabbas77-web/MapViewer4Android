package com.oghab.mapviewer.mapviewer;

import static com.oghab.mapviewer.MainActivity.activity;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;
import com.oghab.mapviewer.utils.mv_utils;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;

import java.util.List;
import java.util.Locale;

public class mv_LocationOverlay extends mvLocationNewOverlay {

    static public Location curr_location = null;
    static public Location prev_location = null;

    long Time_new;
    double Speed_new ;
    long Time_old ;
    double Speed_old = 0.0;

    public mv_LocationOverlay(ImvLocationProvider myLocationProvider, MapView mapView) {
        super(myLocationProvider, mapView);
        curr_location = new Location(LocationManager.GPS_PROVIDER);
        prev_location = new Location(LocationManager.GPS_PROVIDER);
    }

    static public int prev_I = -1;
    static public int prev_hash_code = -1;

    @SuppressLint("NewApi")
    public void update_position_status(){
        try {
            if(curr_location == null)   return;
            if(MainActivity.bNavigation && Tab_Map.map_status)
            {
                char format_char = ' ';
                String strStart = " ";
                String strEnd = " \n";
                String strExt = "";
                String cDeg = ""+(char)0x00B0;
                String cMeter = " m";
                String cKMeter = " km";
                String cSeconds = " sec";
//                String cAccuracy = " "+(char)0x00B1;
                String cAccuracy = ""+(char)0x00B1;
//                String cSpeed = " "+(char)0x33A7;// m/s
                String cSpeed = " "+(char)0x339E+"/h";// km/h
                float fSpeed = 3.6f;
                String cInfinity = ""+(char)0x221E;
                int nLength = 24;
                String strText = "";
//                if(!MainActivity.isRealDevice()) strText += strStart + "Emulator" + strEnd;
//                if (MainActivity.IsDemoVersionJNI())
//                    strText += strStart + "[Demo]" + strEnd;
//                else
//                    strText += strStart + "[Registered]" + strEnd;

                double alt = 0;
                float bearing = 0;
                float speed = 0;
                float speed_km = 0;

//                double alt0 = 0;
//                alt0 = curr_location.getAltitude();
//                if(MainActivity.tab_map != null) {
//                    alt = alt0 - MainActivity.tab_map.getAltitudeEGM96Correction(curr_location.getLatitude(), curr_location.getLongitude());
//                }else{
//                    alt = alt0;
//                }
//                curr_location.setAltitude(alt);
                alt = curr_location.getAltitude();

                bearing = curr_location.getBearing();
                speed = curr_location.getSpeed();
                speed_km = fSpeed * speed;
                strExt = "";
                strText += strStart + "H: " + String.format(Locale.ENGLISH, "%.02f", bearing) + strExt + cDeg;
                strExt = "";
                strText +=  strEnd + strStart + "S: " + String.format(Locale.ENGLISH, "%.02f", speed_km) + strExt + cSpeed;
                strExt = "";
                strText += strEnd + strStart + "A: " + String.format(Locale.ENGLISH, "%.0f", alt) + strExt + cMeter;
                if(Tab_Map.gps_coordinates) {
//                    strText += strEnd + strStart + MainActivity.CoordinateToDMS(curr_location.getLatitude()) + (curr_location.getLatitude() >= 0 ? " N" : " S");
//                    strText += strEnd + strStart + MainActivity.CoordinateToDMS(curr_location.getLongitude()) + (curr_location.getLongitude() >= 0 ? " E" : " W");

                    strText += strEnd + strStart + Tab_Map.convert_coordinates(curr_location.getLongitude(),curr_location.getLatitude(),Tab_Map.map_coordinate_index,false,true);
                }

/*
                // location
//                strText += strStart + "Longitude: " + String.format(Locale.ENGLISH, "%.06f", curr_location.getLongitude()) + cDeg + strEnd;
//                strText += strStart + "Latitude: " + String.format(Locale.ENGLISH, "%.06f", curr_location.getLatitude()) + cDeg + strEnd;
//                strText += strStart + mv_utils.append_chars("Longitude: ",format_char, 11, true)+String.format(Locale.ENGLISH, "%.06f", curr_location.getLongitude()) + cDeg + strEnd;
//                strText += strStart + mv_utils.append_chars("Latitude: ",format_char, 11, true)+String.format(Locale.ENGLISH, "%.06f", curr_location.getLatitude()) + cDeg + strEnd;
//                strText += strStart + "N: "+String.format(Locale.ENGLISH, "%.06f", curr_location.getLatitude()) + cDeg + strEnd;
//                strText += strStart + "E: "+String.format(Locale.ENGLISH, "%.06f", curr_location.getLongitude()) + cDeg + strEnd;
//                strText += strStart + MainActivity.CoordinatesToDMS(curr_location.getLongitude(),curr_location.getLatitude()) + strEnd;
                if(Tab_Map.gps_coordinates) {
                    strText += strStart + MainActivity.CoordinateToDMS(curr_location.getLatitude()) + (curr_location.getLatitude() >= 0 ? " N" : " S") + strEnd;
                    strText += strStart + MainActivity.CoordinateToDMS(curr_location.getLongitude()) + (curr_location.getLongitude() >= 0 ? " E" : " W") + strEnd;
                }

////                if (curr_location.hasAccuracy()) {
//                    float accuracy = curr_location.getAccuracy();
//                    strExt = cAccuracy + String.format(Locale.ENGLISH, "%.0f", accuracy);
////                    strText += strStart + "Accuracy: " + strExt + cMeter + strEnd;
//                    strText += strStart + mv_utils.append_chars("Accuracy: ",format_char, 11, true) + strExt + cMeter + strEnd;
////                }

                // altitude
//                if (curr_location.hasAltitude()) {
                    alt0 = curr_location.getAltitude();
//                    if (alt0 > 0.05) {
                        alt = alt0 - MainActivity.tab_map.getAltitudeEGM96Correction(curr_location.getLatitude(), curr_location.getLongitude());
//                    }
                    strExt = "";
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        if (curr_location.hasVerticalAccuracy()) {
//                            float alt_accuracy = curr_location.getVerticalAccuracyMeters();
//                            strExt = cAccuracy + String.format(Locale.ENGLISH, "%.0f", alt_accuracy);
//                        }
//                    }
//                    strText += strStart + "Altitude: " + String.format(Locale.ENGLISH, "%.0f", alt) + strExt + cMeter + strEnd;
//                    strText += strStart + mv_utils.append_chars("A: ",format_char, nLength, true) + String.format(Locale.ENGLISH, "%.0f", alt) + strExt + cMeter + strEnd;
                    strText += strStart + "A: " + String.format(Locale.ENGLISH, "%.0f", alt) + strExt + cMeter + strEnd;
//                }

                // bearing
//                if (curr_location.hasBearing()) {
                    bearing = curr_location.getBearing();
//                }

                // speed
//                if (curr_location.hasSpeed()) {
                    speed = fSpeed * curr_location.getSpeed();
//                }

                strExt = "";
//                if (speed > fSpeed * 0.05) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        if (curr_location.hasSpeedAccuracy()) {
//                            float speed_accuracy = fSpeed * curr_location.getSpeedAccuracyMetersPerSecond();
//                            strExt = cAccuracy + String.format(Locale.ENGLISH, "%.02f", speed_accuracy);
//                        }
//                    }
//                    strText += strStart + "Speed: " + String.format(Locale.ENGLISH, "%.02f", speed) + strExt + cSpeed + strEnd;
//                    strText += strStart + mv_utils.append_chars("S: ",format_char, nLength, true) + String.format(Locale.ENGLISH, "%.02f", speed) + strExt + cSpeed + strEnd;
                    strText += strStart + "S: " + String.format(Locale.ENGLISH, "%.02f", speed) + strExt + cSpeed + strEnd;

                    // bearing
                    strExt = "";
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        if (curr_location.hasBearingAccuracy()) {
//                            float bearing_accuracy = curr_location.getBearingAccuracyDegrees();
//                            strExt = cAccuracy + String.format(Locale.ENGLISH, "%.02f", bearing_accuracy);
//                        }
//                    }
//                    strText += strStart + "Heading: " + String.format(Locale.ENGLISH, "%.02f", bearing) + strExt + cDeg + strEnd;
//                    strText += strStart + mv_utils.append_chars("H: ",format_char, nLength, true) + String.format(Locale.ENGLISH, "%.02f", bearing) + strExt + cDeg + strEnd;
                    strText += strStart + "H: " + String.format(Locale.ENGLISH, "%.02f", bearing) + strExt + cDeg + strEnd;
//                }
//                else
//                {
//                    long t_ns = curr_location.getElapsedRealtimeNanos();
//                    long prev_t_ns = prev_location.getElapsedRealtimeNanos();
//                    long d_t_ns = t_ns - prev_t_ns;
//                    double d_t_s = d_t_ns / 1000000000.0;
//
//                    float[] list0 = new float[2];
//                    Location.distanceBetween(prev_location.getLatitude(), prev_location.getLongitude(), curr_location.getLatitude(), curr_location.getLongitude(), list0);
//                    double dist0 = Math.round(list0[0]);
//                    float bearing0 = (float) MainActivity.db_deg(list0[1]);
//
//                    float speed0 = (float) (dist0 / d_t_s);
//
//                    prev_location = curr_location;
//                    curr_location.setSpeed(speed0);
//                    curr_location.setBearing(bearing0);
//                    this.setLocation(curr_location);
//
//                    speed = fSpeed * speed0;
//                    bearing = bearing0;
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        if (curr_location.hasSpeedAccuracy()) {
//                            float speed_accuracy = fSpeed * curr_location.getSpeedAccuracyMetersPerSecond();
//                            strExt = cAccuracy + String.format(Locale.ENGLISH, "%.02f", speed_accuracy);
//                        } else {
//                            strExt = cAccuracy + cInfinity;
//                        }
//                    } else {
//                        strExt = cAccuracy + cInfinity;
//                    }
//                    strText += strStart + "Speed Est: " + String.format(Locale.ENGLISH, "%.02f", speed) + strExt + cSpeed + strEnd;
//
//                    // bearing
//                    strExt = "";
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        if (curr_location.hasBearingAccuracy()) {
//                            float bearing_accuracy = curr_location.getBearingAccuracyDegrees();
//                            strExt = cAccuracy + String.format(Locale.ENGLISH, "%.02f", bearing_accuracy);
//                        } else {
//                            strExt = cAccuracy + cInfinity;
//                        }
//                    } else {
//                        strExt = cAccuracy + cInfinity;
//                    }
//                    strText += strStart + "Heading Est: " + String.format(Locale.ENGLISH, "%.02f", bearing) + strExt + cDeg + strEnd;
//                }

                // update current location
//                curr_location.setBearing(bearing);
//                curr_location.setSpeed(speed);
*/
                if(Tab_Map.map_satellites){
                    int type = GnssStatus.CONSTELLATION_GPS;
                    int nSatelliteCount = 0;
                    int[] nTypes = new int[7];
                    String[] strTypes = new String[7];
                    int[] nUsed = new int[7];
//                    strTypes[0] = "UNKNOWN";
//                    strTypes[1] = "GPS";
//                    strTypes[2] = "SBAS";
//                    strTypes[3] = "GLONASS";
//                    strTypes[4] = "QZSS";
//                    strTypes[5] = "BEIDOU";
//                    strTypes[6] = "GALILEO";

                    strTypes[0] = "UNK";
                    strTypes[1] = "GPS";
                    strTypes[2] = "SBA";
                    strTypes[3] = "GLO";
                    strTypes[4] = "QZS";
                    strTypes[5] = "BEI";
                    strTypes[6] = "GAL";

                    GnssStatus status = getMyLocationProvider().getLastKnownStatus();
                    if(status != null) {
                        nSatelliteCount = status.getSatelliteCount();
                        for(int i=0;i<nSatelliteCount;i++)
                        {
                            type = status.getConstellationType(i);
                            switch(type)
                            {
                                case GnssStatus.CONSTELLATION_GPS:// GPS
                                {
                                    nTypes[GnssStatus.CONSTELLATION_GPS]++;
                                    if(status.usedInFix(i)) nUsed[GnssStatus.CONSTELLATION_GPS]++;
                                    break;
                                }
                                case GnssStatus.CONSTELLATION_SBAS:// SBAS
                                {
                                    nTypes[GnssStatus.CONSTELLATION_SBAS]++;
                                    if(status.usedInFix(i)) nUsed[GnssStatus.CONSTELLATION_SBAS]++;
                                    break;
                                }
                                case GnssStatus.CONSTELLATION_GLONASS:// GLONASS
                                {
                                    nTypes[GnssStatus.CONSTELLATION_GLONASS]++;
                                    if(status.usedInFix(i)) nUsed[GnssStatus.CONSTELLATION_GLONASS]++;
                                    break;
                                }
                                case GnssStatus.CONSTELLATION_QZSS:// QZSS
                                {
                                    nTypes[GnssStatus.CONSTELLATION_QZSS]++;
                                    if(status.usedInFix(i)) nUsed[GnssStatus.CONSTELLATION_QZSS]++;
                                    break;
                                }
                                case GnssStatus.CONSTELLATION_BEIDOU:// BEIDOU
                                {
                                    nTypes[GnssStatus.CONSTELLATION_BEIDOU]++;
                                    if(status.usedInFix(i)) nUsed[GnssStatus.CONSTELLATION_BEIDOU]++;
                                    break;
                                }
                                case GnssStatus.CONSTELLATION_GALILEO:// GALILEO
                                {
                                    nTypes[GnssStatus.CONSTELLATION_GALILEO]++;
                                    if(status.usedInFix(i)) nUsed[GnssStatus.CONSTELLATION_GALILEO]++;
                                    break;
                                }
                                case GnssStatus.CONSTELLATION_UNKNOWN:// UNKNOWN
                                default:// UNKNOWN
                                {
                                    nTypes[GnssStatus.CONSTELLATION_UNKNOWN]++;
                                    if(status.usedInFix(i)) nUsed[GnssStatus.CONSTELLATION_UNKNOWN]++;
                                    break;
                                }
                            }
                        }
                    }

                    strText += strStart + strEnd;
                    int used = 0;
                    for(int i=1;i<7;i++)
                    {
//                        strText += strStart + mv_utils.append_chars(strTypes[i]+":",format_char, 8, true)+mv_utils.append_chars(String.valueOf(nTypes[i]),format_char, 2, false)+mv_utils.append_chars(", used:",format_char, 7, true)+mv_utils.append_chars(String.valueOf(nUsed[i]),format_char, 2, false)+strEnd;
//                        strText += strStart + strTypes[i]+":"+String.valueOf(99)+",use:"+String.valueOf(99)+strEnd;
                        strText += strEnd + strStart + strTypes[i]+":"+String.format(Locale.ENGLISH, "%02d", nTypes[i])+",use:"+String.format(Locale.ENGLISH, "%02d", nUsed[i]);
                        used += nUsed[i];
                    }
//                    strText += strStart + mv_utils.append_chars("SATs:",format_char, 8, true)+mv_utils.append_chars(String.valueOf(nSatelliteCount),format_char, 2, false)+mv_utils.append_chars(", used:",format_char, 7, true)+mv_utils.append_chars(String.valueOf(used),format_char, 2, false)+strEnd;
//                    strText += strStart + "SATs:"+String.valueOf(nSatelliteCount)+", used:"+String.valueOf(used)+strEnd;
//                    strText += strStart + "TOT:"+String.valueOf(99)+",use:"+String.valueOf(99)+strEnd;
                    strText += strEnd + strStart + "TOT:"+String.format(Locale.ENGLISH, "%02d", nSatelliteCount)+",use:"+String.format(Locale.ENGLISH, "%02d", used);
                }
                if((MainActivity.tab_map != null) && (MainActivity.tab_map.cross_overlay != null)) MainActivity.tab_map.cross_overlay.setInfo(strText);

                if((MainActivity.tab_map != null)  && (Tab_Map.target_Marker != null) && Tab_Map.navigation_mode){
                    float[] list1 = new float[2];
                    if(Tab_Map.auto_select_target){
                        if(Tab_Map.target_item instanceof MyMarker)
                        {
                            MyMarker marker0 = (MyMarker) Tab_Map.target_item;
                            // same target point
                        }
                        else
                        if(Tab_Map.target_item instanceof MyPolyline) {
                            MyPolyline polyline0 = (MyPolyline) Tab_Map.target_item;

                            List<GeoPoint> points = polyline0.getActualPoints();
                            float min_dist = 1e16F;
                            int I = -1;
                            for(int i=0;i<points.size();i++){
                                GeoPoint p = points.get(i);
                                Location.distanceBetween(p.getLatitude(), p.getLongitude(),curr_location.getLatitude(), curr_location.getLongitude(), list1);
                                float dist = list1[0];
                                if(dist < min_dist){
                                    min_dist = dist;
                                    I = i;
                                }
                            }
                            if((I >= 0) && (I != prev_I)){
                                prev_I = I;
                                Tab_Map.target_Marker.setEnabled(true);
                                Tab_Map.target_Marker.setId(Integer.toString(I));
                                Tab_Map.target_Marker.setPosition(points.get(I));
                                Tab_Map.target_Marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                                Tab_Map.target_Marker.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                                Tab_Map.target_Marker.setIcon(mv_utils.getDrawable(MainActivity.ctx, R.drawable.marker_target));
                                Tab_Map.target_Marker.setTitle(activity.getString(R.string.target));
                                Tab_Map.target_Marker.setInfo1("");
                                Tab_Map.target_Marker.setInfo2("");
                            }
                        }else
                        if(Tab_Map.target_item instanceof MyPolygon) {
                            MyPolygon polygon0 = (MyPolygon) Tab_Map.target_item;

                            List<GeoPoint> points = polygon0.getActualPoints();
                            float min_dist = 1e16F;
                            int I = -1;
                            for(int i=0;i<points.size();i++){
                                GeoPoint p = points.get(i);
                                Location.distanceBetween(p.getLatitude(), p.getLongitude(),curr_location.getLatitude(), curr_location.getLongitude(), list1);
                                float dist = list1[0];
                                if(dist < min_dist){
                                    min_dist = dist;
                                    I = i;
                                }
                            }
                            if((I >= 0) && (I != prev_I)){
                                prev_I = I;
                                Tab_Map.target_Marker.setEnabled(true);
                                Tab_Map.target_Marker.setId(Integer.toString(I));
                                Tab_Map.target_Marker.setPosition(points.get(I));
                                Tab_Map.target_Marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                                Tab_Map.target_Marker.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                                Tab_Map.target_Marker.setIcon(mv_utils.getDrawable(MainActivity.ctx, R.drawable.marker_target));
                                Tab_Map.target_Marker.setTitle(activity.getString(R.string.target));
                                Tab_Map.target_Marker.setInfo1("");
                                Tab_Map.target_Marker.setInfo2("");
                            }
                        }
                    }
                    GeoPoint target_point = Tab_Map.target_Marker.getPosition();
                    Location.distanceBetween(curr_location.getLatitude(), curr_location.getLongitude(), target_point.getLatitude(), target_point.getLongitude(), list1);
                    double dist1 = Math.round(list1[0]);
                    double bearing1 = (float) MainActivity.db_deg(list1[1]);
                    double alt1 = MainActivity.GetHeightJNI(target_point.getLongitude(), target_point.getLatitude());
                    double delay_s = dist1 / speed;
                    String strInfo = "";
                    if(dist1 < 1000)
                        strInfo += "D: " + String.format(Locale.ENGLISH, "%.0f", dist1) + cMeter + "\n";
                    else
                        strInfo += "D: " + String.format(Locale.ENGLISH, "%.0f", dist1/1000.0f) + cKMeter + "\n";
                    strInfo += "H: " + String.format(Locale.ENGLISH, "%.01f", bearing1) + cDeg + "\n";
                    strInfo += "A: " + String.format(Locale.ENGLISH, "%.01f", alt1) + cMeter + "\n";
                    strInfo += "T: " + String.format(Locale.ENGLISH, "%.01f", delay_s) + cSeconds;
                    setInfo1(strInfo);

                    if(dist1 <= Tab_Map.map_target_radius){
                        int hash_code = target_point.hashCode();
                        if(hash_code != prev_hash_code) {
                            prev_hash_code = hash_code;

                            if(Tab_Map.target_item instanceof MyMarker)
                            {
                                MyMarker marker0 = (MyMarker) Tab_Map.target_item;

                                if(Tab_Map.target_Marker != null) {
                                    int idx = Integer.parseInt(Tab_Map.target_Marker.getId());
                                    if(idx >= 0) {
                                        Tab_Map.navigation_mode = false;
                                        Tab_Map.target_Marker.setEnabled(false);
                                        Tab_Map.target_Marker.setId(Integer.toString(-1));
                                        if(marker0 != null) {
                                            Tab_Map.CustomInfoWindow customInfoWindow = (Tab_Map.CustomInfoWindow) marker0.getInfoWindow();
                                            if (customInfoWindow != null) {
                                                customInfoWindow.setEdit(false);
                                            }
                                        }
                                        MainActivity.mission_finished(true);
                                    }
                                }
                            }
                            else
                            if(Tab_Map.target_item instanceof MyPolyline)
                            {
                                MyPolyline polyline0 = (MyPolyline) Tab_Map.target_item;

                                List<GeoPoint> points = polyline0.getActualPoints();
                                if(points.size() > 0) {
                                    if(Tab_Map.target_Marker != null) {
                                        int idx = Integer.parseInt(Tab_Map.target_Marker.getId());
                                        if(idx >= 0) {
                                            idx++;
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
                                                if(polyline0 != null) {
                                                    Tab_Map.CustomInfoWindow customInfoWindow = (Tab_Map.CustomInfoWindow) polyline0.getInfoWindow();
                                                    if (customInfoWindow != null) {
                                                        customInfoWindow.setEdit(false);
                                                    }
                                                }
                                                MainActivity.mission_finished(true);
                                            }
                                        }
                                    }
                                }
                            }
                            else
                            if(Tab_Map.target_item instanceof MyPolygon)
                            {
                                MyPolygon polygon0 = (MyPolygon) Tab_Map.target_item;

                                List<GeoPoint> points = polygon0.getActualPoints();
                                if(points.size() > 0) {
                                    int idx = Integer.parseInt(Tab_Map.target_Marker.getId());
                                    if(idx >= 0) {
                                        idx++;
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
                                            if(polygon0 != null) {
                                                Tab_Map.CustomInfoWindow customInfoWindow = (Tab_Map.CustomInfoWindow) polygon0.getInfoWindow();
                                                if (customInfoWindow != null) {
                                                    customInfoWindow.setEdit(false);
                                                }
                                            }
                                            MainActivity.mission_finished(true);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (Tab_Map.gun_Marker != null) {
                        GeoPoint gun_point = Tab_Map.gun_Marker.getPosition();
//                            Location.distanceBetween(curr_location.getLatitude(), curr_location.getLongitude(), gun_point.getLatitude(), gun_point.getLongitude(), list1);
                        Location.distanceBetween(gun_point.getLatitude(), gun_point.getLongitude(), curr_location.getLatitude(), curr_location.getLongitude(), list1);
                        dist1 = Math.round(list1[0]);
                        bearing1 = (float) MainActivity.db_deg(list1[1]);
                        alt1 = MainActivity.GetHeightJNI(gun_point.getLongitude(), gun_point.getLatitude());
//                            String strInfo2 = "Distance: " + String.format(Locale.ENGLISH, "%.0f", dist1) + cMeter + "\n";
//                            strInfo2 += "Heading: " + String.format(Locale.ENGLISH, "%.01f", bearing1) + cDeg + "\n";
                        String strInfo2 = "";
                        if(dist1 < 1000)
                            strInfo2 += "D: " + String.format(Locale.ENGLISH, "%.0f", dist1) + cMeter + "\n";
                        else
                            strInfo2 += "D: " + String.format(Locale.ENGLISH, "%.0f", dist1/1000.0f) + cKMeter + "\n";
                        strInfo2 += "H: " + String.format(Locale.ENGLISH, "%.01f", bearing1) + cDeg + "\n";
                        strInfo2 += "A: " + String.format(Locale.ENGLISH, "%.01f", alt1) + cMeter + "\n";
                        delay_s = dist1 / speed;
                        strInfo2 += "Time to fire: " + String.format(Locale.ENGLISH, "%.01f", delay_s) + cSeconds;
                        Tab_Map.gun_Marker.setInfo2(strInfo2);
                    }
                }

                if (Tab_Map.ProjectileSettings.auto_update) {
//                        if (alt0 > 0.05) {
                    Tab_Map.ProjectileSettings.z0 = alt;
                    Tab_Map.ProjectileSettings.alt0 = Tab_Map.ProjectileSettings.z0;
//                        } else
//                            Tab_Map.ProjectileSettings.z0 = Tab_Map.ProjectileSettings.alt0;
                    Tab_Map.ProjectileSettings.velocity0 = speed;
                }

                if (Tab_Map.ProjectileSettings.auto_calculate) {
                    Tab_Map.ProjectileSettings.gun_lon0 = curr_location.getLongitude();
                    Tab_Map.ProjectileSettings.gun_lat0 = curr_location.getLatitude();
                    if(MainActivity.tab_map != null) MainActivity.tab_map.projectile_line(Tab_Map.ProjectileSettings.gun_lon0, Tab_Map.ProjectileSettings.gun_lat0, Tab_Map.ProjectileSettings.target_lon0, Tab_Map.ProjectileSettings.target_lat0, Tab_Map.ProjectileSettings.iterations, Tab_Map.ProjectileSettings.z0, Tab_Map.ProjectileSettings.time_step, Tab_Map.ProjectileSettings.velocity0, Tab_Map.ProjectileSettings.angle0, Tab_Map.ProjectileSettings.diameter0, Tab_Map.ProjectileSettings.mass0, Tab_Map.ProjectileSettings.wind0, Tab_Map.ProjectileSettings.error, Tab_Map.ProjectileSettings.dencity0, Tab_Map.ProjectileSettings.cofficient0, Tab_Map.ProjectileSettings.temp0, Tab_Map.ProjectileSettings.gravity0, Tab_Map.ProjectileSettings.const_gravity0);
                }

                Tab_Map.map.postInvalidate();
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    @Override
    public void draw(Canvas canvas, MapView map, boolean shadow) {
        super.draw(canvas, map, shadow);
        try {
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    public void updateLocation(Location location) {
        if (MainActivity.checkGpsCoordinates(location.getLatitude(), location.getLongitude())) {
            prev_location.set(curr_location);
            curr_location.set(location);

            double alt0 = curr_location.getAltitude();
            double alt = alt0;
            if (MainActivity.tab_map != null) {
                alt = alt0 - MainActivity.tab_map.getAltitudeEGM96Correction(curr_location.getLatitude(), curr_location.getLongitude());
            }
            curr_location.setAltitude(alt);

            update_position_status();

            // convert speed to km/h
//        Speed_new = location.getSpeed() * 3.6;
//        Time_new = System.currentTimeMillis();
//        double acceleration = acc((Speed_new - Speed_old), dt(Time_new, Time_old));

            MainActivity.uav_lon = curr_location.getLongitude();
            MainActivity.uav_lat = curr_location.getLatitude();
            MainActivity.uav_alt = curr_location.getAltitude();
            MainActivity.uav_ground_alt = MainActivity.GetHeightJNI(MainActivity.uav_lon, MainActivity.uav_lat);
            MainActivity.uav_alt_above_ground = curr_location.getAltitude() - MainActivity.uav_ground_alt;

            if (MainActivity.tab_map != null)   MainActivity.tab_map.set_cam_pos(MainActivity.uav_lon, MainActivity.uav_lat, MainActivity.uav_alt, true);
            if (MainActivity.tab_camera != null) MainActivity.tab_camera.update_status0(false);

            Tab_Map.map.postInvalidate();
        }
    }

    @Override
    @SuppressLint("NewApi")
    public void onLocationChanged(Location location, ImvLocationProvider source) {
        try {
            if(MainActivity.bNavigation) {
                boolean isMock = false;
                boolean isKalman = false;
                boolean isVirtual = false;
                Bundle extraBundle = location.getExtras();
                if(extraBundle != null){
                    isMock = extraBundle.getBoolean("isMock");
                    isKalman = extraBundle.getBoolean("isKalman");
                    isVirtual = extraBundle.getBoolean("isVirtual");
//                    Log.d("kalman", "isMock: " + isMock + ", isKalman: " + isKalman);
                }

                if(CitiesAdapter.bIsSimulating){
                    if(isMock){
                        super.onLocationChanged(location,source);
                        updateLocation(location);
//                        Log.d("kalman", "location12: " + location);
                    }
                }else{
                    if ((Tab_Map.is_kalman) && (isKalman)) {
                        super.onLocationChanged(location,source);
                        updateLocation(location);
//                        Log.d("kalman", "location13: " + location);
                    } else if (isVirtual) {
                        super.onLocationChanged(location,source);
                        updateLocation(location);
//                        Log.d("kalman", "location15: " + location);
                    } else {
                        super.onLocationChanged(location,source);
                        updateLocation(location);
//                        Log.d("kalman", "location14: " + location);
                    }
                }
            }
//            else {
//                super.onLocationChanged(location,source);
//                if ((MainActivity.tab_camera != null) && (MainActivity.tab_camera.cb_gps != null) && (!MainActivity.tab_camera.cb_gps.isChecked())) {
//                    if (MainActivity.checkGpsCoordinates(location.getLatitude(), location.getLongitude())) {
//                        prev_location.set(curr_location);
//                        curr_location.set(location);
//
//                        MainActivity.uav_lon = curr_location.getLongitude();
//                        MainActivity.uav_lat = curr_location.getLatitude();
//                        MainActivity.uav_alt_above_ground = location.getAltitude();
//                        MainActivity.uav_ground_alt = MainActivity.GetHeightJNI(MainActivity.uav_lon, MainActivity.uav_lat);
//                        MainActivity.uav_alt = MainActivity.uav_ground_alt + MainActivity.uav_alt_above_ground;
//
//                        if(MainActivity.tab_map != null)    MainActivity.tab_map.set_cam_pos(MainActivity.uav_lon, MainActivity.uav_lat, MainActivity.uav_alt, true);
//                        if(MainActivity.tab_camera != null) MainActivity.tab_camera.update_status(false);
//                        Tab_Map.map.postInvalidate();
//                    }
//                }
//            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }
    private double dt (long new_time, long old_time){
        double dt = new_time- old_time;
        Time_old = Time_new;

        // converting dt from ms to h. 60 mins per hour multiply by 60 seconds per min.
        return dt / 3600;
    }
    private double acc(double DU, double DT){

        if ( Speed_new == Speed_old && DT == 0){
            return 0.0;
        }

        Speed_old = Speed_new;
        return DU/DT;
    }
}
