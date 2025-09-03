package com.oghab.mapviewer.mapviewer;

/**
 * @author Ali Abbas
 */

import static com.oghab.mapviewer.MainActivity.activity;
import static com.oghab.mapviewer.MainActivity.ctx;
import static com.oghab.mapviewer.MainActivity.tab_map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.blankj.subutil.util.LocationUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.oghab.mapviewer.MApplication;
import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;
import com.oghab.mapviewer.bonuspack.clustering.RadiusMarkerClusterer;
import com.oghab.mapviewer.utils.FileHelper;
import com.oghab.mapviewer.utils.Proj;
import com.oghab.mapviewer.utils.mv_utils;
import com.opencsv.CSVReader;

//import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.locationtech.proj4j.ProjCoordinate;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;

import com.oghab.mapviewer.bonuspack.kml.HotSpot;
import com.oghab.mapviewer.bonuspack.kml.IconStyle;
import com.oghab.mapviewer.bonuspack.kml.KmlDocument;
import com.oghab.mapviewer.bonuspack.kml.KmlFeature;
import com.oghab.mapviewer.bonuspack.kml.KmlFolder;
import com.oghab.mapviewer.bonuspack.kml.KmlLineString;
import com.oghab.mapviewer.bonuspack.kml.KmlPlacemark;
import com.oghab.mapviewer.bonuspack.kml.KmlPoint;
import com.oghab.mapviewer.bonuspack.kml.KmlPolygon;
import com.oghab.mapviewer.bonuspack.kml.KmlTrack;
import com.oghab.mapviewer.bonuspack.kml.Style;
import com.oghab.mapviewer.bonuspack.kml.StyleSelector;
import com.oghab.mapviewer.bonuspack.utils.BonusPackHelper;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.modules.OfflineTileProvider;
import org.osmdroid.tileprovider.modules.SqlTileWriter;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.PointReducer;
import org.osmdroid.util.TileSystem;
import org.osmdroid.util.TileSystemWebMercator;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.CustomZoomButtonsDisplay;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.drawing.MapSnapshot;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.GroundOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
//import org.osmdroid.views.overlay.ScaleBarOverlay;
//import org.osmdroid.views.overlay.compass.CompassOverlay;
//import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
//import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.gridlines.LatLonGridlineOverlay2;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
//import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
//import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
//import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import dji.common.error.DJIError;
import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;
import dji.common.mission.waypoint.WaypointDownloadProgress;
import dji.common.mission.waypoint.WaypointExecutionProgress;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionDownloadEvent;
import dji.common.mission.waypoint.WaypointMissionExecutionEvent;
import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import dji.common.mission.waypoint.WaypointMissionFlightPathMode;
import dji.common.mission.waypoint.WaypointMissionGotoWaypointMode;
import dji.common.mission.waypoint.WaypointMissionHeadingMode;
import dji.common.mission.waypoint.WaypointMissionUploadEvent;
import dji.common.mission.waypoint.WaypointUploadProgress;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;
import dji.sdk.sdkmanager.DJISDKManager;

//import android.arch.lifecycle.LifecycleOwner;
//import android.content.SharedPreferences;
//import android.support.annotation.Nullable;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.content.res.ResourcesCompat;
//import android.support.annotation.Nullable;
//import android.support.v4.content.res.ResourcesCompat;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.content.res.ResourcesCompat;
//import org.osmdroid.tileprovider.MapTile;
//import org.osmdroid.util.BoundingBoxE6;
//import androidx.annotation.Nullable;
//import androidx.core.content.res.ResourcesCompat;
//import androidx.annotation.Nullable;
//import androidx.core.content.res.ResourcesCompat;

/**
 * Created by MapViewer on 27/03/2017.
 */

public class Tab_Map extends Fragment
        implements View.OnClickListener, View.OnKeyListener {

    private static final String TAG = "Map";
    public static TimelineMissionControl timeline = null;

    static public MapView map = null;
    public static IMapController mapController = null;
    public Style defaultStyle = null;
    public MyKmlStyler styler = null;
    static public boolean edit_mode = true;
    static public boolean navigation_mode = false;
    MyRotationGestureOverlay mRotationGestureOverlay = null;

    static public GroundOverlay groundOverlay = null;
    static public GeoPoint top_left = new GeoPoint(33.515606288224,36.2724455624699);
    static public GeoPoint top_right = new GeoPoint(33.515606288224,36.2808516055226);
    static public GeoPoint bottom_right = new GeoPoint(33.5121400270246,36.2808516055226);
    static public GeoPoint bottom_left = new GeoPoint(33.5121400270246,36.2724455624699);

    GeoPoint position = null;
//    private MapCompassOverlay mCompassOverlay = null;
//    private CompassOverlay mCompassOverlay = null;
    MyScaleBarOverlay mScaleBarOverlay = null;
    private LatLonGridlineOverlay2 gridlineOverlay2 = null;
//    private CopyrightOverlay copyrightOverlay = null;
//    public mvLocationNewOverlay mLocationOverlay = null;
    public mv_LocationOverlay mLocationOverlay = null;
    public FOV_Overlay fov_overlay = null;
    private Ruler_Overlay ruler_overlay = null;
//    public Path_Overlay path_overlay = null;
    public Cross_Overlay cross_overlay = null;

    MyMarker cam_Marker = null;
    static public MyMarker target_Marker = null;
    MyMarker POI_Marker = null;
    MyMarker home_Marker = null;
    MyMarker start_Marker = null;
    static public MyMarker gun_Marker = null;
    boolean bIsCreated = false;
    GeoPoint cameraPoint = null;
    GeoPoint targetPoint = null;
    public GeoPoint poiPoint = null;
    GeoPoint homePoint = null;
    GeoPoint mapPoint = null;
    GeoPoint startPoint = null;
    GeoPoint gun_point = null;

    static public Object target_item = null;

    static public CheckBox sw_broadcast_map,sw_auto_rotate_map;

    CheckBox cb_add_favorite;
    static public CheckBox cb_map_status,cb_map_clustering,cb_kalman,cb_map_satellites,cb_gps_coordinates,cb_gps_egm96,cb_auto_select_target;
    CheckBox cb_add_path, cb_add_polygon;
    CheckBox cb_track;
    CheckBox cb_change_cam_pos;
    CheckBox cb_change_target_pos;
    CheckBox cb_change_home_pos;
    CheckBox cb_search;
    CheckBox cb_favorites;
    static public CheckBox cb_show_marks, cb_show_polylines, cb_show_polygons;
    Button b_send_all_marks;
    Button b_test;
    ImageView b_save,b_hide_favorites,b_hide_sensors,b_hide_search;
    TextView tv_update_ruler,tv_add_start,tv_add_end,tv_search_count;
    CheckBox cb_look_at;
    CheckBox cb_follow;
    CheckBox cb_auto;
    TextView tv_distance,tv_heading;
    EditText e_update_interval_sec,et_ruler_distance,et_ruler_azimuth;
    EditText et_icons_scale,et_target_radius,et_image_transparency;
    CheckBox cb_ruler, cb_utm_grid;
    CheckBox cb_goto;
    LinearLayout layout_map_toolbar,ll_image_transparency;
    EditText e_start_idx,e_point_count;
    public static EditText e_altitude;
    public static EditText e_mission_speed;
    public EditText e_ele_deg;
    CheckBox cb_custom_alt,cb_multi_view;
    static public TextView mission_progress;
    public CheckBox cb_sensors;
    public ScrollView hsv_sensors;
    Button b_enter, b_finish, b_edit_mode, b_edit_finish, b_finish_mission,tv_geom_info,b_toggle_path_direction;
    ImageView image_compass,b_snapshot;
    TextView text_compass,tv_measurements2,tv_measurements3;
    EditText et_angle;
    LinearLayout mapButtons;
    static LinearLayout mapEditButtons;
    LinearLayout mapCompass;
    LinearLayout mapRuler;
    static public LinearLayout mapFinishMission;
    Button b_open_gps_settings;

    public Button b_fake_uav_reset,b_fake_gimbal_reset;
    public TextView tv_uav_yaw;
    public SeekBar sb_uav_yaw;

    public TextView tv_uav_pitch;
    public SeekBar sb_uav_pitch;

    public TextView tv_uav_roll;
    public SeekBar sb_uav_roll;

    public TextView tv_uav_alt;
    public SeekBar sb_uav_alt;

    public TextView tv_gimb_yaw;
    public SeekBar sb_gimb_yaw;

    public TextView tv_gimb_pitch;
    public SeekBar sb_gimb_pitch;

    public TextView tv_gimb_roll;
    public SeekBar sb_gimb_roll;

    public TextView tv_gps_yaw;
    public SeekBar sb_gps_yaw;

    public TextView tv_gps_speed;
    public SeekBar sb_gps_speed;

    public TextView tv_gps_alt;
    public SeekBar sb_gps_alt;

    TextView tv_version_status_map;

    static public File favoritesFile;

    EditText et_search,et_search_favorites;
    static ListView lv_search, lv_favorites;
    HorizontalScrollView hsv_search, hsv_favorites;
    static public ArrayAdapter<String> listAdapter;

    static public ArrayList<City> arrayOfCities;
    static public CitiesAdapter cities_adapter;

    static public ArrayList<City> arrayOfFavorites;
    static public CitiesAdapter favorites_adapter;

    private ArrayList<GeoPoint> g_points = null;
    private MyPolyline g_polyline = null;
    private MyPolygon g_polygon = null;

    public ArrayList<GeoPoint> track_points = null;
    public MyPolyline track_polyline = null;

    private CheckBox cb_mission;
    public Button b_timeline_init,b_timeline_start,b_timeline_stop,b_timeline_pause,b_timeline_resume;
    public Button b_timeline_simulate;
    private HorizontalScrollView hsv_mission;
    private WaypointMissionOperator instance;
    static public String strMissionPath;
    static public String strMissionsPath;

    ArrayList<OverlayItem> overlayItemArray;
    static public KmlDocument kmlFavoritesDocument = null;
//    FolderOverlay kmlOverlay = null;
    static public RadiusMarkerClusterer kmlOverlay = null;
    private Overlay missionOverlay = null;
    static public CheckBox cb_target_lock;
    Spinner s_map_text_size,s_map_coordinates;
    static public AlertDialog missionsDialog;
    static public String strMissionDir;

    static public double prev_map_lon = 0.0, prev_map_lat = 0.0, prev_map_zoom = 1.0, prev_map_rot = 0.0;
    public void update_map_status(boolean bUpdate)
    {
        MainActivity.activity.runOnUiThread(() -> {
            try
            {
                if((MainActivity.tab_map != null) && (Tab_Map.map != null)) {
                    if (((Tab_Map.sw_broadcast_map != null) && Tab_Map.sw_broadcast_map.isChecked()) && ((Tab_Camera.sw_broadcast_map_status != null) && (!Tab_Camera.sw_broadcast_map_status.isChecked()))) {
                        try {
                            if((Math.abs(MainActivity.map_lon - prev_map_lon) > 0.000001) || (Math.abs(MainActivity.map_lat - prev_map_lat) > 0.000001) || (Math.abs(MainActivity.map_zoom - prev_map_zoom) > 0.1) || (Math.abs(MainActivity.map_rot - prev_map_rot) > 0.1)) {
                                Tab_Messenger.sendMessage("MAP_STATUS:" + MainActivity.map_lat + "," + MainActivity.map_lon + "," + MainActivity.map_zoom + "," + MainActivity.map_rot, true);
                                prev_map_lon = MainActivity.map_lon;
                                prev_map_lat = MainActivity.map_lat;
                                prev_map_zoom = MainActivity.map_zoom;
                                prev_map_rot = MainActivity.map_rot;
                            }
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    }

                    if(bUpdate) Tab_Map.map.postInvalidate();
                }
            }
            catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
            }
        });
    }

    private final Handler customMapHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));

    private Runnable updateMapTimerThread = new Runnable() {
        public void run() {
            try
            {
                update_map_status(false);

                customMapHandler.postDelayed(this, 40);
            }
            catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
                customMapHandler.postDelayed(this, 40);
            }
        }
    };

    private void showMissionsDialog(){
        try
        {
            final LinearLayout pathsLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.dialog_mission_paths, null);
            final ListView lv_paths = pathsLayout.findViewById(R.id.lv_mission_paths);

            // Construct the data source
            final ArrayList<String> arrayOfPaths = new ArrayList<>();
            final MissionsAdapter missions_adapter = new MissionsAdapter(MainActivity.ctx, arrayOfPaths);

            // Storage Paths
//            String strPath = Environment.getExternalStorageDirectory().getPath();
            String strPath = MainActivity.dir_internal;

            strMissionDir = strPath + "/MapViewer/Missions/";
            File[] fileList = new File(strMissionDir).listFiles();
            assert fileList != null;
            for (File file : fileList) {
                if ((!file.isDirectory()) && file.canRead()) {
//                        strPath = file.getPath();
                    strPath = file.getName();
                    if (strPath.endsWith(".kml")) {
                        missions_adapter.add(strPath);
                    }
                }
            }

            // Attach the adapter to a ListView
            lv_paths.setAdapter(missions_adapter);

            AlertDialog.Builder dialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialog)
                    .setTitle("")
                    .setCancelable(false)// for Modal Show
                    .setView(pathsLayout)
                    .setPositiveButton(getString(R.string.ok), (dialog12, id) -> {
                        MainActivity.hide_keyboard(pathsLayout);
                        load_mission();
                    })
                    .setNegativeButton(getString(R.string.cancel), (dialog1, id) -> {
                        MainActivity.hide_keyboard(pathsLayout);
                        dialog1.cancel();
                    });

            missionsDialog = dialog.create();
            missionsDialog.show();

            // adjust view width and height
            Rect displayRectangle = new Rect();
            Window window = getActivity().getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
            Objects.requireNonNull(missionsDialog.getWindow()).setLayout((int)(displayRectangle.width() * 0.9f), (int)(displayRectangle.height() * 0.9f));
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    static public int start_idx = 0;
    static public int point_count = 99;
    public void load_mission() {
        if (timeline == null) return;
        try
        {
            Tab_Map.strMissionPath = Tab_Map.strMissionDir + MissionsAdapter.strMissionName;

            String strText;
            strText = e_altitude.getText().toString();
            float altitude = Float.parseFloat(strText);

            strText = e_start_idx.getText().toString();
            start_idx = (int)mv_utils.parseDouble(strText) - 1;

            strText = e_point_count.getText().toString();
            point_count = (int)mv_utils.parseDouble(strText);

            strText = e_mission_speed.getText().toString();
            float mission_speed = Float.parseFloat(strText);

            boolean bCustomAlt = cb_custom_alt.isChecked();
            boolean bMultiView = cb_multi_view.isChecked();

            strText = e_ele_deg.getText().toString();
            int nPitch = mv_utils.parseInt(strText);

            timeline.initKMLTimeline(start_idx,altitude,mission_speed,point_count,bCustomAlt,bMultiView,nPitch);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * Inflate the layout for this fragment
         */
        view = inflater.inflate(R.layout.tab_map, container, false);
        init(view);
        return view;
    }

//    public Tab_Map(Context context) {
//        super(context);
//        try
//        {
//            init(context);
//        }
//        catch (Throwable ex)
//        {
//            MainActivity.MyLog(ex);
//        }
//    }
//
//    public Tab_Map(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        try
//        {
//            init(context);
//        }
//        catch (Throwable ex)
//        {
//            MainActivity.MyLog(ex);
//        }
//    }
//
//    public Tab_Map(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        try
//        {
//            init(context);
//        }
//        catch (Throwable ex)
//        {
//            MainActivity.MyLog(ex);
//        }
//    }

//    static class OnMarkerDragListenerTarget implements MyMarker.OnMarkerDragListener {
//
//        OnMarkerDragListenerTarget() {
//        }
//
//        @Override public void onMarkerDrag(MyMarker marker) {
//            try
//            {
//                Location location = mv_LocationOverlay.curr_location;
//                if(location != null) {
//                    GeoPoint target_point = target_Marker.getPosition();
//                    char cDeg = (char)0x00B0;
//                    String cMeter = " m";
//
//                    float[] list = new float[2];
//                    Location.distanceBetween(location.getLatitude(), location.getLongitude(), target_point.getLatitude(), target_point.getLongitude(), list);
//                    double dist = Math.round(list[0]);
//                    double bearing = (float) MainActivity.db_deg(list[1]);
//                    String strInfo = "";
//                    strInfo += "Distance: " + String.format(Locale.ENGLISH, "%.0f", dist) + cMeter + "\n";
//                    strInfo += "Heading: " + String.format(Locale.ENGLISH, "%.01f", bearing) + cDeg;
//                    target_Marker.setInfo1(strInfo);
//
//                    Tab_Map.ProjectileSettings.target_lon0 = target_point.getLongitude();
//                    Tab_Map.ProjectileSettings.target_lat0 = target_point.getLatitude();
//                    projectile_line(Tab_Map.ProjectileSettings.gun_lon0, Tab_Map.ProjectileSettings.gun_lat0, Tab_Map.ProjectileSettings.target_lon0, Tab_Map.ProjectileSettings.target_lat0, Tab_Map.ProjectileSettings.iterations, Tab_Map.ProjectileSettings.z0, Tab_Map.ProjectileSettings.time_step, Tab_Map.ProjectileSettings.velocity0, Tab_Map.ProjectileSettings.angle0, Tab_Map.ProjectileSettings.diameter0, Tab_Map.ProjectileSettings.mass0, Tab_Map.ProjectileSettings.wind0, Tab_Map.ProjectileSettings.error, Tab_Map.ProjectileSettings.dencity0, Tab_Map.ProjectileSettings.cofficient0, Tab_Map.ProjectileSettings.temp0, Tab_Map.ProjectileSettings.gravity0, Tab_Map.ProjectileSettings.const_gravity0);
//
//                    Tab_Map.map.postInvalidate();
//                }
//            }
//            catch (Throwable ex)
//            {
//                MainActivity.MyLog(ex);
//            }
//        }
//
//        @Override public void onMarkerDragEnd(MyMarker marker) {
//        }
//
//        @Override public void onMarkerDragStart(MyMarker marker) {
//            //mTrace.add(marker.getPosition());
//        }
//    }

    static class OnMarkerDragListenerDrawer implements MyMarker.OnMarkerDragListener {
        ArrayList<GeoPoint> mTrace;
        MyPolyline mPolyline;

        OnMarkerDragListenerDrawer() {
            mTrace = new ArrayList<GeoPoint>(100);
            mPolyline = new MyPolyline(map);
            mPolyline.getOutlinePaint().setColor(0xAA0000FF);
            mPolyline.getOutlinePaint().setStrokeWidth(2.0f);
            mPolyline.setGeodesic(true);
            map.getOverlays().add(mPolyline);
        }

        @Override public void onMarkerDrag(MyMarker marker) {
            //mTrace.add(marker.getPosition());
        }

        @Override public void onMarkerDragEnd(MyMarker marker) {
            mTrace.add(marker.getPosition());
            mPolyline.setPoints(mTrace);
            map.invalidate();
        }

        @Override public void onMarkerDragStart(MyMarker marker) {
            //mTrace.add(marker.getPosition());
        }
    }

    static public boolean map_status = true;
    static public boolean map_clustering = true;

    static public boolean is_kalman = true;
    static public boolean map_satellites = true;
    static public boolean gps_coordinates = true;
    static public boolean gps_egm96 = true;
    static public boolean auto_select_target = false;
    static public boolean auto_rotate_map = false;

    public void update_ruler(){
        et_ruler_distance.setText(ruler_overlay.getDistance());
        et_ruler_azimuth.setText(ruler_overlay.getAzimuth());
        tv_measurements2.setText(ruler_overlay.getText2());
        tv_measurements3.setText(ruler_overlay.getText3());
        if(ruler_overlay.mMarkers.size() > 1) {
            ruler_overlay.mMarkers.get(1).setPosition(ruler_overlay.getPos2());
        }
    }

    String[] map_text_sizes = {"2","4","6","8","10","12","14","16","18","20","22","24","26","28","30","32"};
    static public float map_text_size = 16;
    static public int map_text_size_index = 7;
    static public float map_icon_scale = 1.0f;
    static public float map_target_radius = 15.0f;
    static public float map_image_transparency = 0.0f;
    static public int map_max_points = 20;

    String[] map_coordinates = {"GEO","DMS","UTM","STM","SK42","SK42-GK"};
    static public int map_coordinate_index = 1;

    LooperThread looperThread;
    private void init(View view) {
        try
        {
//            inflate(context, R.layout.tab_map, this);

            looperThread = new LooperThread();
            looperThread.start();

            cb_map_status = view.findViewById(R.id.cb_map_status);
            cb_map_status.setOnClickListener(this);
            if(MainActivity.bNavigation)
                cb_map_status.setVisibility(View.VISIBLE);
            else
                cb_map_status.setVisibility(View.GONE);

            cb_map_clustering = view.findViewById(R.id.cb_map_clustering);
            cb_map_clustering.setOnClickListener(this);

            cb_kalman = view.findViewById(R.id.cb_kalman);
            cb_kalman.setOnClickListener(this);
            if(MainActivity.isDevelpoment())
                cb_kalman.setVisibility(View.VISIBLE);
            else
                cb_kalman.setVisibility(View.GONE);

            cb_map_satellites = view.findViewById(R.id.cb_map_satellites);
            cb_map_satellites.setOnClickListener(this);
            if(MainActivity.bNavigation)
                cb_map_satellites.setVisibility(View.VISIBLE);
            else
                cb_map_satellites.setVisibility(View.GONE);

            cb_gps_coordinates = view.findViewById(R.id.cb_gps_coordinates);
            cb_gps_coordinates.setOnClickListener(this);
            if(MainActivity.bNavigation)
                cb_gps_coordinates.setVisibility(View.VISIBLE);
            else
                cb_gps_coordinates.setVisibility(View.GONE);

            cb_gps_egm96 = view.findViewById(R.id.cb_gps_egm96);
            cb_gps_egm96.setOnClickListener(this);
//            if(MainActivity.bNavigation)
                cb_gps_egm96.setVisibility(View.VISIBLE);
//            else
//                cb_gps_egm96.setVisibility(View.GONE);

            cb_auto_select_target = view.findViewById(R.id.cb_auto_select_target);
            cb_auto_select_target.setOnClickListener(this);

            sw_auto_rotate_map = view.findViewById(R.id.sw_auto_rotate_map);
            sw_auto_rotate_map.setOnClickListener(this);
            if(MainActivity.bNavigation)
                sw_auto_rotate_map.setVisibility(View.VISIBLE);
            else
                sw_auto_rotate_map.setVisibility(View.GONE);

            s_map_text_size = view.findViewById(R.id.s_map_text_size);
            s_map_coordinates = view.findViewById(R.id.s_map_coordinates);

            SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
            map_status = settings.getBoolean("map_status", map_status);
            map_clustering = settings.getBoolean("map_clustering", map_clustering);
            is_kalman = settings.getBoolean("is_kalman", is_kalman);
            map_satellites = settings.getBoolean("map_satellites", map_satellites);
            auto_rotate_map = settings.getBoolean("auto_rotate_map", auto_rotate_map);
            gps_coordinates = settings.getBoolean("gps_coordinates", gps_coordinates);
            gps_egm96 = settings.getBoolean("gps_egm96", gps_egm96);
            auto_select_target = settings.getBoolean("auto_select_target", auto_select_target);
            map_icon_scale = settings.getFloat("map_icon_scale", map_icon_scale);
            map_target_radius = settings.getFloat("map_target_radius", map_target_radius);
            map_image_transparency = settings.getFloat("map_image_transparency", map_image_transparency);
            cb_map_status.setChecked(map_status);
            cb_map_clustering.setChecked(map_clustering);
            cb_kalman.setChecked(is_kalman);
            cb_map_satellites.setChecked(map_satellites);
            cb_gps_coordinates.setChecked(gps_coordinates);
            cb_gps_egm96.setChecked(gps_egm96);
            cb_auto_select_target.setChecked(auto_select_target);
            sw_auto_rotate_map.setChecked(auto_rotate_map);

            map_text_size_index = settings.getInt("map_text_size_index", map_text_size_index);

            map_coordinate_index = settings.getInt("map_coordinate_index", map_coordinate_index);

            ArrayAdapter mapScalesAdapter = new ArrayAdapter(MainActivity.ctx,android.R.layout.simple_spinner_item, map_text_sizes);
            mapScalesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            s_map_text_size.setAdapter(mapScalesAdapter);
            s_map_text_size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    map_text_size = Float.parseFloat(map_text_sizes[position]);
                    SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("map_text_size_index", position);
                    editor.apply();
                    MainActivity.set_fullscreen();
                    if((MainActivity.tab_map != null) && (cross_overlay != null)) {
                        cross_overlay.setTextSize(map_text_size);
                        setRulerTextSize(map_text_size);
                        kmlOverlay.invalidate();
                        map.postInvalidate();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    MainActivity.set_fullscreen();
                }
            });

            ArrayAdapter mapCoordinatesAdapter = new ArrayAdapter(MainActivity.ctx,android.R.layout.simple_spinner_item, map_coordinates);
            mapCoordinatesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            s_map_coordinates.setAdapter(mapCoordinatesAdapter);
            s_map_coordinates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    map_coordinate_index = position;
                    SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("map_coordinate_index", position);
                    editor.apply();
                    MainActivity.set_fullscreen();
                    if(MainActivity.tab_map != null) {
                        map.postInvalidate();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    MainActivity.set_fullscreen();
                }
            });

            cb_add_favorite = view.findViewById(R.id.cb_add_favorite);
            cb_add_favorite.setOnClickListener(this);

            cb_add_path = view.findViewById(R.id.cb_add_path);
            cb_add_path.setOnClickListener(this);

            cb_add_polygon = view.findViewById(R.id.cb_add_polygon);
            cb_add_polygon.setOnClickListener(this);

            cb_track = view.findViewById(R.id.cb_track);
            cb_track.setOnClickListener(this);
//            if(MainActivity.bNavigation)
//                cb_track.setVisibility(View.GONE);
//            else
                cb_track.setVisibility(View.VISIBLE);

            cb_target_lock = view.findViewById(R.id.cb_target_lock);
            cb_target_lock.setOnClickListener(this);

            cb_change_cam_pos = view.findViewById(R.id.cb_change_cam_pos);
            cb_change_cam_pos.setOnClickListener(this);
            if(MainActivity.isDevelpoment()) {
                if (MainActivity.bNavigation)
                    cb_change_cam_pos.setVisibility(View.GONE);
                else
                    cb_change_cam_pos.setVisibility(View.VISIBLE);
            }else{
                cb_change_cam_pos.setVisibility(View.GONE);
            }

            cb_change_target_pos = view.findViewById(R.id.cb_change_target_pos);
            cb_change_target_pos.setOnClickListener(this);
            if(MainActivity.isDevelpoment()) {
                if (MainActivity.bNavigation)
                    cb_change_target_pos.setVisibility(View.GONE);
                else
                    cb_change_target_pos.setVisibility(View.VISIBLE);
            }else{
                cb_change_target_pos.setVisibility(View.GONE);
            }

            cb_change_home_pos = view.findViewById(R.id.cb_change_home_pos);
            cb_change_home_pos.setOnClickListener(this);
            if(MainActivity.isDevelpoment()) {
                if (MainActivity.bNavigation)
                    cb_change_home_pos.setVisibility(View.GONE);
                else
                    cb_change_home_pos.setVisibility(View.VISIBLE);
            }else{
                cb_change_home_pos.setVisibility(View.GONE);
            }

            cb_look_at = view.findViewById(R.id.cb_look_at);
            cb_look_at.setOnClickListener(this);

            cb_goto = view.findViewById(R.id.cb_goto);
            cb_goto.setOnClickListener(this);

            cb_follow = view.findViewById(R.id.cb_follow);
            cb_follow.setOnClickListener(this);

            cb_auto = view.findViewById(R.id.cb_auto);
            cb_auto.setOnClickListener(this);

            e_update_interval_sec = view.findViewById(R.id.e_update_interval_sec);

            et_ruler_distance = view.findViewById(R.id.et_ruler_distance);
            et_ruler_azimuth = view.findViewById(R.id.et_ruler_azimuth);

            tv_distance = view.findViewById(R.id.tv_distance);
            tv_heading = view.findViewById(R.id.tv_heading);

            cb_ruler = view.findViewById(R.id.cb_ruler);
            cb_ruler.setOnClickListener(this);

            cb_utm_grid = view.findViewById(R.id.cb_latlon_grid);
            cb_utm_grid.setOnClickListener(this);

            cb_search = view.findViewById(R.id.cb_search);
            cb_search.setOnClickListener(this);

            cb_favorites = view.findViewById(R.id.cb_favorites);
            cb_favorites.setOnClickListener(this);

            cb_show_marks = view.findViewById(R.id.cb_show_marks);
            cb_show_marks.setOnClickListener(this);
            cb_show_marks.setChecked(settings.getBoolean("cb_show_marks", true));

            cb_show_polylines = view.findViewById(R.id.cb_show_polylines);
            cb_show_polylines.setOnClickListener(this);
            cb_show_polylines.setChecked(settings.getBoolean("cb_show_polylines", true));

            cb_show_polygons = view.findViewById(R.id.cb_show_polygons);
            cb_show_polygons.setOnClickListener(this);
            cb_show_polygons.setChecked(settings.getBoolean("cb_show_polygons", true));

            b_hide_search = view.findViewById(R.id.b_hide_search);
            b_hide_search.setOnClickListener(this);

            tv_search_count = view.findViewById(R.id.tv_search_count);

            b_hide_favorites = view.findViewById(R.id.b_hide_favorites);
            b_hide_favorites.setOnClickListener(this);

            b_send_all_marks = view.findViewById(R.id.b_send_all_marks);
            b_send_all_marks.setOnClickListener(this);
            if(MainActivity.isDevelpoment())
                b_send_all_marks.setVisibility(View.VISIBLE);
            else
                b_send_all_marks.setVisibility(View.GONE);

            b_hide_sensors = view.findViewById(R.id.b_hide_sensors);
            b_hide_sensors.setOnClickListener(this);

            tv_version_status_map = view.findViewById(R.id.tv_version_status_map);
            MainActivity.set_view_visibility(tv_version_status_map,MainActivity.IsDemoVersionJNI());
            tv_version_status_map.setText(R.string.demo_version);

            et_search = view.findViewById(R.id.et_search);
//            et_search.setOnKeyListener(this);
            TextWatcher dec_watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try
                    {
//                        MainActivity.set_fullscreen();
                        String strText = et_search.getText().toString();
                        int count = MainActivity.search(strText);
                        if(count > 0) {
                            cb_search.setChecked(true);
                            hsv_search.setVisibility(View.VISIBLE);
//                            map.postInvalidate();
                        }
                        else
                        {
                            cb_search.setChecked(false);
                            hsv_search.setVisibility(View.GONE);
//                            map.postInvalidate();
                        }
                        tv_search_count.setText(Integer.toString(count));
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                }
            };
            et_search.addTextChangedListener(dec_watcher);

            et_search_favorites = view.findViewById(R.id.et_search_favorites);
//            et_search.setOnKeyListener(this);
            TextWatcher sf_watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try
                    {
                        String strText = et_search_favorites.getText().toString();
                        int count = MainActivity.searchMarks(strText.toLowerCase());
                        if(count > 0) {
                            cb_search.setChecked(true);
                            hsv_search.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            cb_search.setChecked(false);
                            hsv_search.setVisibility(View.GONE);
                        }
                        tv_search_count.setText(Integer.toString(count));
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                }
            };
            et_search_favorites.addTextChangedListener(sf_watcher);

            cb_custom_alt = view.findViewById(R.id.cb_custom_alt);
            cb_multi_view = view.findViewById(R.id.cb_multi_view);
            e_altitude = view.findViewById(R.id.e_altitude);
            e_ele_deg = view.findViewById(R.id.e_ele_deg);
            e_mission_speed = view.findViewById(R.id.e_mission_speed);
            e_start_idx = view.findViewById(R.id.e_start_idx);
            e_point_count = view.findViewById(R.id.e_point_count);

            lv_search = view.findViewById(R.id.lv_search);
            lv_favorites = view.findViewById(R.id.lv_favorites);

            hsv_search = view.findViewById(R.id.hsv_search);
            hsv_search.setVisibility(View.GONE);

            hsv_favorites = view.findViewById(R.id.hsv_favorites);
            hsv_favorites.setVisibility(View.GONE);

            layout_map_toolbar = view.findViewById(R.id.layout_map_toolbar);
            layout_map_toolbar.setVisibility(View.GONE);

            initUI(view);
            addListener();

            // Construct the data source
            arrayOfCities = new ArrayList<>();
            // Create the adapter to convert the array to views
            cities_adapter = new CitiesAdapter(MainActivity.ctx, arrayOfCities);

            // Add item to adapter
            City city = new City();
            city.strName = "ناحية شين";
            city.fLon = 36.43;
            city.fLat = 34.76;
            city.fAlt = 710.0f;
            city.geometry_type = City.POINT;
            cities_adapter.add(city);

            // Attach the adapter to a ListView
            lv_search.setAdapter(cities_adapter);

            // Construct the data source
            arrayOfFavorites = new ArrayList<>();
            // Create the adapter to convert the array to views
            favorites_adapter = new CitiesAdapter(MainActivity.ctx, arrayOfFavorites);
            // Attach the adapter to a ListView
            lv_favorites.setAdapter(favorites_adapter);

//            Configuration.getInstance().setOsmdroidBasePath(new File(MainActivity.strMapsPath));
            Configuration.getInstance().setOsmdroidTileCache(new File(MainActivity.strCachePath));
            MainActivity.MyLogInfo("MapsList: "+MainActivity.strMapsPath);

//            Configuration.getInstance().setDebugMapTileDownloader(true);
//            Configuration.getInstance().setDebugMapView(true);
//            Configuration.getInstance().setDebugMode(true);
//            Configuration.getInstance().setDebugTileProviders(true);

//            File f = ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
//            Configuration.getInstance().setOsmdroidBasePath(new File(StorageUtils.getBestWritableStorage(ctx).path, "osmdroid"));
//            Configuration.getInstance().setOsmdroidBasePath(new File(ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile()+"/MapViewer", "Maps"));

//            Configuration.getInstance().setOsmdroidBasePath(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/MapViewer", "Maps"));
//            Configuration.getInstance().setOsmdroidTileCache(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/MapViewer", "Cache"));

//            try
//            {
//                IConfigurationProvider configuration = Configuration.getInstance();
//                File osmdroidBasePath = new File(dir_internal+"/MapViewer", "Maps");
//                osmdroidBasePath.mkdirs();
//                File osmdroidTilePath = new File(dir_internal+"/MapViewer", "Cache");
//                osmdroidTilePath.mkdirs();
//                configuration.setOsmdroidBasePath(osmdroidBasePath);
//                configuration.setOsmdroidTileCache(osmdroidTilePath);
//            }
//            catch (Throwable ex)
//            {
//                MainActivity.MyLog(ex);
//            }

//            IConfigurationProvider configuration = Configuration.getInstance();
//            File path = ctx.getFilesDir();
//            File osmdroidBasePath = new File(path, "osmdroid");
//            osmdroidBasePath.mkdirs();
//            File osmdroidTilePath = new File(osmdroidBasePath, "tiles");
//            osmdroidTilePath.mkdirs();
//            configuration.setOsmdroidBasePath(osmdroidBasePath);
//            configuration.setOsmdroidTileCache(osmdroidTilePath);

            //important! set your user agent to prevent getting banned from the osm servers
//            map = view.findViewById(R.id.map);
            map = new MapView(MainActivity.ctx);
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setTilesScaledToDpi(true);
            map.setUseDataConnection(false);// to load offline map

            //custom image placeholder for files that aren't available
//            map.getTileProvider().setTileLoadFailureImage(getResources().getDrawable(R.drawable.notfound));

            // update DefaultOverlayManager
            map.setOverlayManager(MyOverlayManager.create(map, MainActivity.ctx));

            // cache correction
            map.getTileProvider().getTileCache().clear();
            map.getTileProvider().getTileCache().setStressedMemory(true);
            map.getTileProvider().getTileCache().setAutoEnsureCapacity(false);
            map.getTileProvider().getTileCache().ensureCapacity(100);

            // Maps from Uri
            try {
                FileHelper.FileScanner fileScanner = new FileHelper.FileScanner("mbtiles");
                Uri uri0 = Uri.parse(MainActivity.strMapsUri);
                if(uri0 != null){
                    MainActivity.ctx.getContentResolver().takePersistableUriPermission(uri0, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    DocumentFile documentFile = DocumentFile.fromTreeUri(MainActivity.ctx,uri0);
                    if(documentFile != null){
                        fileScanner.scan(documentFile);
                        if(fileScanner.files.size() > 0) {
                            File[] tmp = new File[fileScanner.files.size()];
                            fileScanner.files.toArray(tmp);

                            FileHelper.FileScanner.str = "Maps Uri: "+MainActivity.strMapsUri+"\n";
                            for(File f:tmp){
                                FileHelper.FileScanner.str += f.getAbsolutePath()+"\n";
                            }

                            final IRegisterReceiver registerReceiver = new SimpleRegisterReceiver(MainActivity.ctx);
                            OfflineTileProvider provider = new OfflineTileProvider(registerReceiver, tmp);
                            map.setTileProvider(provider);
                            Tab_Map.map.invalidate();
                        }
//                        else{
//                            throw new Throwable("fileScanner.files.size() <= 0");
//                        }
                    }
//                    else{
//                        throw new Throwable("documentFile == null");
//                    }
                }
//                else{
//                    throw new Throwable("uri0 == null");
//                }
            } catch (Throwable ex) {
                try{
                    FileHelper.FileScanner fileScanner = new FileHelper.FileScanner("mbtiles");
                    DocumentFile documentFile = DocumentFile.fromFile(new File(MainActivity.strMapsPath));
                    fileScanner.scan(documentFile);
                    if(fileScanner.files.size() > 0) {
                        File[] tmp = new File[fileScanner.files.size()];
                        fileScanner.files.toArray(tmp);

                        FileHelper.FileScanner.str = "Maps Path: "+MainActivity.strMapsPath+"\n";
                        for(File f:tmp){
                            FileHelper.FileScanner.str += f.getAbsolutePath()+"\n";
                        }

                        final IRegisterReceiver registerReceiver = new SimpleRegisterReceiver(MainActivity.ctx);
                        OfflineTileProvider provider = new OfflineTileProvider(registerReceiver, tmp);
                        map.setTileProvider(provider);
                        Tab_Map.map.invalidate();
                    }
//                    else{
//                        throw new Throwable("fileScanner.files.size() <= 0");
//                    }
                } catch (Throwable ex2) {
                    MainActivity.MyLog(ex2);
                }
            }

//            MainActivity.set_maps_list();

//            File maps_list = new File(MainActivity.strMapsPath);
//            File[] maps_files = maps_list.listFiles();
//            if((maps_files != null) && (maps_files.length > 0)) {
//                Configuration.getInstance().setOsmdroidBasePath(new File(MainActivity.strMapsPath));
//                Configuration.getInstance().setOsmdroidTileCache(new File(MainActivity.strCachePath));
//                MainActivity.MyLogInfo("MapsList: "+MainActivity.strMapsPath);
//            }
//            else
//            {
//                File file_list = new File(MainActivity.dir_data + "/MapViewer/Maps/list.txt");
//                MainActivity.MyLogInfo("MapsList: "+MainActivity.dir_data + "/MapViewer/Maps/list.txt");
//                if(file_list.exists()) {
//                    ArrayList<File> files = new ArrayList<File>();
//                    FileInputStream is;
//                    BufferedReader reader;
//                    is = new FileInputStream(file_list);
//                    reader = new BufferedReader(new InputStreamReader(is));
//                    while(true){
//                        String line = reader.readLine();
//                        if(line == null)    break;
//                        MainActivity.MyLogInfo("MapsList: "+line);
//
//                        File file = new File(MainActivity.dir_data + "/MapViewer/Maps/"+line);
//                        if(file.exists()) {
//                            files.add(file);
//                        }
//                    }
//                    File[] tmp = new File[files.size()];
//                    files.toArray(tmp);
//
//                    final IRegisterReceiver registerReceiver = new SimpleRegisterReceiver(MainActivity.ctx);
//                    OfflineTileProvider provider = new OfflineTileProvider(registerReceiver, tmp);
//                    map.setTileProvider(provider);
//                }
//                else
//                {
//                    Configuration.getInstance().setOsmdroidBasePath(new File(MainActivity.strMapsPath));
//                    Configuration.getInstance().setOsmdroidTileCache(new File(MainActivity.strCachePath));
//                }
//            }

//            Configuration.getInstance().setMapViewHardwareAccelerated(false);// bug: markers icons size problem, this flag must be false
            Configuration.getInstance().setMapViewHardwareAccelerated(true);// bug: markers icons size problem, this flag must be false

            LinearLayout contentLayout = (LinearLayout) view.findViewById(R.id.mapLayout);
            org.osmdroid.views.MapView.LayoutParams mapParams = new org.osmdroid.views.MapView.LayoutParams(
                    org.osmdroid.views.MapView.LayoutParams.MATCH_PARENT,
                    org.osmdroid.views.MapView.LayoutParams.MATCH_PARENT,
                    null, 0, 0, 0);
            contentLayout.addView(map, mapParams);

//        OfflineOnlyTileProvider
//        ArchiveFileFactory
//        Configuration
//        OpenStreetMapTileProviderConstants.getBasePath();// "osmdroid"

//        String[] urlArray = {"http://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/"};
//        String[] urlArray = {"/sdcard/MapViewer/map/maps/tiles/sat/"};
//        String[] urlArray = {"http://localhost/sdcard/MapViewer/map/maps/tiles/sat/"};
//        map.setTileSource(new OnlineTileSourceBase("MapViewer", 0, 18, 256, "", urlArray) {
//            @Override
//            public String getTileURLString(MapTile aTile) {
//                String mImageFilenameEnding = ".png";
//                return getBaseUrl() + aTile.getZoomLevel() + "/"
//                        + aTile.getY() + "/" + aTile.getX()
//                        + mImageFilenameEnding;
//            }
//        });

//        OnlineTileSourceBase MapViewer_tiles = new OnlineTileSourceBase("MapViewer Sat", 0, 15, 256, "",
//                new String[]{"/sdcard/MapViewer/map/maps/tiles/sat/"},"MapViewer") {
//            @Override
//            public String getTileURLString(MapTile aTile) {
//                String mImageFilenameEnding = ".png";
//                return getBaseUrl() + aTile.getZoomLevel() + "/" + aTile.getY() + "/" + aTile.getX()+ mImageFilenameEnding;
//            }
//        };
//        map.setTileSource(MapViewer_tiles);

            //enables this opt in feature
            //AliSoft 2019.11.07
//            MyMarker.ENABLE_TEXT_LABELS_WHEN_NO_IMAGE = true;

            position = new GeoPoint(MainActivity.uav_lat,MainActivity.uav_lon,MainActivity.uav_alt);

            cameraPoint = new GeoPoint(33.517792, 36.342728);
            cameraPoint.setLongitude(MainActivity.uav_lon);
            cameraPoint.setLatitude(MainActivity.uav_lat);
            cameraPoint.setAltitude(MainActivity.uav_alt);

            targetPoint = new GeoPoint(33.517792, 36.342728);
            targetPoint.setLongitude(MainActivity.target_lon);
            targetPoint.setLatitude(MainActivity.target_lat);
            targetPoint.setAltitude(MainActivity.target_alt);

            gun_point = new GeoPoint(0.0,0.0);

//            poiPoint = new GeoPoint(33.517792, 36.342728);
            poiPoint = new GeoPoint(0, 0);

            homePoint = new GeoPoint(33.517792, 36.342728);
            homePoint.setLongitude(MainActivity.home_lon);
            homePoint.setLatitude(MainActivity.home_lat);
            homePoint.setAltitude(0.0);

            mapPoint = new GeoPoint(33.517792, 36.342728);
            mapPoint.setLongitude(MainActivity.map_lon);
            mapPoint.setLatitude(MainActivity.map_lat);
            mapPoint.setAltitude(0.0);

            startPoint = new GeoPoint(33.517792, 36.342728);
            startPoint.setLongitude(MainActivity.start_lon);
            startPoint.setLatitude(MainActivity.start_lat);
            startPoint.setAltitude(0.0);

            map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
            map.getZoomController().getDisplay().setPositions(true, CustomZoomButtonsDisplay.HorizontalPosition.RIGHT,CustomZoomButtonsDisplay.VerticalPosition.BOTTOM);

            map.setMultiTouchControls(true);
            map.setZoomRounding(false);
            map.setFlingEnabled(true);
            map.setMinZoomLevel(3.0);
            map.setMaxZoomLevel(19.0);
//            map.setMapOrientation(90.0f);
//            ((FrameLayout.LayoutParams) map.getZoomButtonsController().getZoomControls().getLayoutParams()).gravity = Gravity.RIGHT;
//            ZoomControls controls = (ZoomControls) map.getZoomButtonsController().getZoomControls();

            mapController = map.getController();
            mapController.setZoom(MainActivity.map_zoom);
            mapController.setCenter(mapPoint);
            map.setMapOrientation((float)MainActivity.map_rot);
            doRotate((float)MainActivity.map_rot, true);

//            CameraPosition restoredCamera = new CameraPosition.Builder()
//                    .target(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()))
//                    .zoom(15)
//                    .bearing(0) // Face north
//                    .tilt(0) // reset tilt (directly facing the Earth)
//                    .build();
//
//            map.moveCamera(CameraUpdateFactory.newCameraPosition(restoredCamera));

//            map.getOverlays().add(new RotationGestureOverlay(map));

//            mCompassOverlay = new CompassOverlay(MainActivity.ctx, new GestureCompassOrientationProvider(MainActivity.ctx), map);
//            mCompassOverlay = new CompassOverlay(MainActivity.ctx, new InternalCompassOrientationProvider(MainActivity.ctx), map);
//            mCompassOverlay.setPointerMode(false);
//            mCompassOverlay.enableCompass();
//            map.getOverlays().add(mCompassOverlay);

            map.addMapListener(new MapListener() {
                double prev_zoom = -1;
                double prev_lon = -1;
                double prev_lat = -1;

                @Override
                public boolean onScroll(ScrollEvent event) {
                    try
                    {
//                        SqlTileWriter sqlTileWriter = new SqlTileWriter();
//                        boolean isCleared = sqlTileWriter.purgeCache(map.getTileProvider().getTileSource().name());
//                        map.getTileProvider().clearTileCache();

//                        map.postInvalidate();

                        IGeoPoint mapCenter = map.getMapCenter();
                        MainActivity.map_lon = mapCenter.getLongitude();
                        MainActivity.map_lat = mapCenter.getLatitude();

//                        if (Tab_Map.sw_broadcast_map.isChecked()) {
//                            try {
//                                IGeoPoint mapCenter = map.getMapCenter();
//                                double lon,lat;
//                                lon = mapCenter.getLongitude();
//                                lat = mapCenter.getLatitude();
//                                if((Math.abs(lon - prev_lon) > 0.000001) || (Math.abs(lat - prev_lat) > 0.000001)) {
//                                    prev_lon = lon;
//                                    prev_lat = lat;
//                                    Tab_Messenger.sendMessage("CENTER:" + lon + "," + lat, true);
//                                    //MainActivity.hide_keyboard(null);
//                                }
//                            } catch (Throwable ex) {
//                                MainActivity.MyLog(ex);
//                            }
//                        }

//                        event.getX();
    //                    Log.i(IMapView.LOGTAG, System.currentTimeMillis() + " onScroll " + event.getX() + "," +event.getY() );
//                        updateInfo();
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                    return true;
                }

                @Override
                public boolean onZoom(ZoomEvent event) {
                    try
                    {
//                        SqlTileWriter sqlTileWriter = new SqlTileWriter();
//                        boolean isCleared = sqlTileWriter.purgeCache(map.getTileProvider().getTileSource().name());
//                        map.getTileProvider().clearTileCache();

//                        map.postInvalidate();
                        MainActivity.map_zoom = event.getZoomLevel();
//                        if (Tab_Map.sw_broadcast_map.isChecked()) {
//                            try {
//                                double zoom = event.getZoomLevel();
//                                if(Math.abs(zoom - prev_zoom) > 0.1) {
//                                    prev_zoom = zoom;
//                                    Tab_Messenger.sendMessage("ZOOM:" + zoom, true);
//                                    //MainActivity.hide_keyboard(null);
//                                }
//                            } catch (Throwable ex) {
//                                MainActivity.MyLog(ex);
//                            }
//                        }

//                        map.getController().stopPanning();
//                        map.getController().stopAnimation(false);
//                        map.getController().setCenter(new GeoPoint(33.0,66.0));
    //                    Log.i(IMapView.LOGTAG, System.currentTimeMillis() + " onZoom " + event.getZoomLevel());
//                        updateInfo();
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                    return true;
                }
            });

            strMissionPath = MainActivity.GetMissionPathJNI();
            strMissionsPath = MainActivity.GetMissionsPathJNI();

            kmlFavoritesDocument = new KmlDocument();
            favoritesFile = new File(MainActivity.GetFavoritesPathJNI());
            if(favoritesFile.exists()) {
                kmlFavoritesDocument.parseKMLFile(favoritesFile);
//            FolderOverlay kmlOverlay = (FolderOverlay)kmlDocument.mKmlRoot.buildOverlay(map, null, null, kmlDocument);
//            map.getOverlays().add(kmlOverlay);
            }
            else {
//                Tab_Messenger.showToast("[" + favoritesFile.getPath() + "] Not found.");
                MainActivity.MyLogInfo("[" + favoritesFile.getPath() + "] Not found.");
            }
//                Toast.makeText(MainActivity.ctx, "["+favoritesFile.getPath()+"] Not found.", Toast.LENGTH_LONG).show();

//            Bitmap pandaBitmap = MainActivity.activity.getResources().getDrawable(R.drawable.center,null);
//            Bitmap pandaBitmap = BitmapFactory.decodeResource(MainActivity.activity.getResources(), R.drawable.avatar48);
//            Style panda_area = new Style(pandaBitmap, 0x901010AA, 3.0f, 0x2010AA10);
//            kmlFavoritesDocument.putStyle("panda_area", panda_area);

            // Icons preparing
//            String strDir = MainActivity.strIconsPath;
//            File dir = new File(strDir);
//            if (!dir.exists()){
//                if(!dir.mkdirs()){
//                    MainActivity.MyLogInfo(strDir + " not created...");
//                }
//            }
//            File path = new File(strDir);
//            File[] Fs = path.listFiles(new FileFilter() {
//                @Override
//                public boolean accept(File file)
//                {
//                    return (file.getName().endsWith(".png"));
//                }
//            });
//            List<File> files = null;
//            if(Fs != null)  files = new ArrayList<>(Arrays.asList(Fs));
//
//            if(files != null) {
//                for (int i = 0; i < files.size(); i++) {
//                    File file = files.get(i);
//                    String fname = file.getName();
//                    Bitmap icon_bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
//                    Style style = new Style(icon_bmp, 0x901010AA, 3.0f, 0x2010AA10);
//                    kmlFavoritesDocument.putStyle(fname, style);
//                }
//            }

//            Bitmap pandaBitmap = BitmapFactory.decodeResource(MainActivity.activity.getResources(), R.drawable.avatar48);
//            Style panda_area = new Style(pandaBitmap, 0x901010AA, 3.0f, 0x2010AA10);
//            kmlFavoritesDocument.putStyle("panda_area", panda_area);

            // load kml CMD file
            // 2020.10.29
//            try {
//                if(MainActivity.uriCMDFile != null) {
//                    InputStream inputStream = MainActivity.activity.getContentResolver().openInputStream(MainActivity.uriCMDFile);
//                    if (inputStream != null) {
//                        KmlDocument kmlCMDDocument = new KmlDocument();
//                        kmlCMDDocument.parseKMLStream(inputStream, null);
//                        FolderOverlay kmlCMDOverlay = (FolderOverlay) kmlCMDDocument.mKmlRoot.buildOverlay(map, null, null, kmlCMDDocument);
//                        map.getOverlays().add(kmlCMDOverlay);
//                    }
//                }
//            } catch (Throwable ex) {
//                MainActivity.MyLog(ex);
//            }

//            File cmdFile = new File(MainActivity.uriCMDFile);
//            if(cmdFile.exists()) {
//                kmlCMDDocument.parseKMLFile(cmdFile);
//                FolderOverlay kmlCMDOverlay = (FolderOverlay)kmlCMDDocument.mKmlRoot.buildOverlay(map, null, null, kmlCMDDocument);
//                map.getOverlays().add(kmlCMDOverlay);
//            }

            //13.1 Simple styling
//            Drawable defaultMarker = ResourcesCompat.getDrawable(MainActivity.activity.getResources(), R.drawable.marker_cluster, null);
            Drawable defaultMarker = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.marker_icon, null);
            Bitmap defaultBitmap = null;
            if (defaultMarker != null) {
                defaultBitmap = ((BitmapDrawable) defaultMarker).getBitmap();
            }
            defaultStyle = new Style(defaultBitmap, 0x901010AA, 3.0f, 0x20AA1010);
//            defaultStyle = new Style(defaultBitmap, Color.GREEN, 3.0f, mv_utils.adjustAlpha(Color.YELLOW,64));

            //13.2 Advanced styling with Styler
            styler = new MyKmlStyler(defaultStyle);

//            kmlOverlay = (FolderOverlay) kmlFavoritesDocument.mKmlRoot.buildOverlay(map, defaultStyle, styler, kmlFavoritesDocument);
//            kmlOverlay = mvBuildOverlay(map, defaultStyle, styler, kmlFavoritesDocument);
//            map.getOverlays().add(kmlOverlay);

//            kmlOverlay = new RadiusMarkerClusterer(ctx);
            kmlOverlay = mvBuildOverlay(map, defaultStyle, styler, kmlFavoritesDocument);
//            Drawable clusterIconD = activity.getResources().getDrawable(R.drawable.marker_cluster,null);
//            Bitmap clusterIcon = ((BitmapDrawable)clusterIconD).getBitmap();
//            kmlOverlay.setIcon(clusterIcon);
            map.getOverlays().add(kmlOverlay);

            fillFavoritesList(kmlFavoritesDocument);

//            kmlOverlay.getTextPaint().setColor(Color.DKGRAY);
//            kmlOverlay.getTextPaint().setTextSize(12 * getResources().getDisplayMetrics().density); //taking into account the screen density
//            kmlOverlay.mAnchorU = MyMarker.ANCHOR_RIGHT;
//            kmlOverlay.mAnchorV = MyMarker.ANCHOR_BOTTOM;
//            kmlOverlay.mTextAnchorV = 0.40f;

            load_KMLs();

            //        map.setUseDataConnection(false);// disable network

            //your items
//        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
//        items.add(new OverlayItem("Title", "Description", new GeoPoint(0.0d,0.0d))); // Lat/Lon decimal degrees
//
//        //the overlay
//        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
//                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
//                    @Override
//                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
//                        //do something
//                        return true;
//                    }
//                    @Override
//                    public boolean onItemLongPress(final int index, final OverlayItem item) {
//                        return false;
//                    }
//                });
//        mOverlay.setFocusItemsOnTap(true);
//        map.getOverlays().add(mOverlay);

//        mMinimapOverlay = new MinimapOverlay(context, mMapView.getTileRequestCompleteHandler());
//        mMinimapOverlay.setWidth(dm.widthPixels / 5);
//        mMinimapOverlay.setHeight(dm.heightPixels / 5);
//        //optionally, you can set the minimap to a different tile source
//        //mMinimapOverlay.setTileSource(....);
//        mMapView.getOverlays().add(this.mMinimapOverlay);

        mScaleBarOverlay = new MyScaleBarOverlay(map);
        mScaleBarOverlay.getTextPaint().setColor(Color.GREEN);
        mScaleBarOverlay.getBarPaint().setColor(Color.GREEN);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setAlignBottom(true);
        mScaleBarOverlay.setAlignRight(true);
        mScaleBarOverlay.setScaleBarOffset(10, 10);
//        mScaleBarOverlay.setMaxLength(50);
//        mScaleBarOverlay.setMinZoom(0);
        mScaleBarOverlay.setEnabled(true);
        map.getOverlays().add(mScaleBarOverlay);

        gridlineOverlay2 = new LatLonGridlineOverlay2();
        gridlineOverlay2.setDecimalFormatter(new DecimalFormat("#.######"));
        gridlineOverlay2.setEnabled(false);
        gridlineOverlay2.getTextPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        gridlineOverlay2.setBackgroundColor(0x4020A3F6);
        gridlineOverlay2.setLineColor(Color.WHITE);
        gridlineOverlay2.setLineWidth(3.0f);
        map.getOverlays().add(gridlineOverlay2);

//            copyrightOverlay = new CopyrightOverlay(MainActivity.ctx);
//            copyrightOverlay.setCopyrightNotice("Oghab MapViewer Copyright © Reserved.");
//            copyrightOverlay.setAlignBottom(false);
//            copyrightOverlay.setAlignRight(false);
//            copyrightOverlay.setOffset(10,10);
//            copyrightOverlay.setTextColor(Color.RED);
//            copyrightOverlay.setTextSize(12);
//            map.getOverlays().add(copyrightOverlay);

        mRotationGestureOverlay = new MyRotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);
        mRotationGestureOverlay.setOnRotateListener(new MyRotationGestureOverlay.OnRotateListener() {
            @Override
            public void onRotate(float angle) {
                doRotate(angle, true);
            }
        });
        map.getOverlays().add(mRotationGestureOverlay);

        // Ground Ovelay for Camera Image
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ummayad_sq);
        groundOverlay = new GroundOverlay();
        groundOverlay.setBearing(0.0f);
        groundOverlay.setTransparency(map_image_transparency);
        groundOverlay.setImage(bmp);
        groundOverlay.setPosition(new GeoPoint(top_left.getLatitude(), top_left.getLongitude())
            , new GeoPoint(top_right.getLatitude(), top_right.getLongitude())
            , new GeoPoint(bottom_right.getLatitude(), bottom_right.getLongitude())
            , new GeoPoint(bottom_left.getLatitude(), bottom_left.getLongitude())
        );
        groundOverlay.setEnabled(false);
        map.getOverlays().add(groundOverlay);

//        mResourceProxy = new CustomResourceProxy(ctx);
//        final RelativeLayout rl = new RelativeLayout(ctx);
//        map = new MapView(this,mResourceProxy);
//        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map, mResourceProxy);
//        this.mLocationOverlay.enableMyLocation();
//        map.getOverlays().add(mLocationOverlay);
//        map.setMultiTouchControls(true);

//        MyPolygon circle = new MyPolygon();
//        circle.setPoints(MyPolygon.pointsAsCircle(cameraPoint, 500.0));
//        circle.setFillColor(0x12121212);
//        circle.setStrokeColor(Color.GREEN);
//        circle.setStrokeWidth(2);
//        map.getOverlays().add(circle);

            /////////////////
//        resourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
//        GeoPoint  currentLocation = new GeoPoint(55.860863,37.115046);
//        GeoPoint  currentLocation2 = new GeoPoint(55.8653,37.11556);
//        OverlayItem myLocationOverlayItem = new OverlayItem("Here", "Current Position", currentLocation);
//        Drawable myCurrentLocationMarker = this.getResources().getDrawable(R.drawable.a,null);
//        myLocationOverlayItem.setMarker(myCurrentLocationMarker);
//
//        final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
//        items.add(myLocationOverlayItem);
//
//        myLocationOverlayItem = new OverlayItem("Here", "Current Position", currentLocation2);
//        myCurrentLocationMarker = this.getResources().getDrawable(R.drawable.a,null);
//        myLocationOverlayItem.setMarker(myCurrentLocationMarker);
//
//        items.add(myLocationOverlayItem);
//        currentLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items,
//                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
//                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
//                        return true;
//                    }
//                    public boolean onItemLongPress(final int index, final OverlayItem item) {
//                        return true;
//                    }
//                }, resourceProxy);
//        this.mapView.getOverlays().add(this.currentLocationOverlay);

            // Routing
//        RoadManager roadManager = new OSRMRoadManager(ctx);
//        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
//        waypoints.add(startPoint);
//        GeoPoint endPoint = new GeoPoint(48.4, -1.9);
//        waypoints.add(endPoint);
//        Road road = roadManager.getRoad(waypoints);
//        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
//        map.getOverlays().add(roadOverlay);
//
//        Drawable nodeIcon = activity.getResources().getDrawable(R.drawable.next,null);
//        for (int i=0; i<road.mNodes.size(); i++){
//            RoadNode node = road.mNodes.get(i);
//            MyMarker nodeMarker = new MyMarker(map);
//            nodeMarker.setPosition(node.mLocation);
//            nodeMarker.setIcon(nodeIcon);
//            nodeMarker.setTitle("Step "+i);
//            map.getOverlays().add(nodeMarker);
//        }

//        nodeMarker.setSnippet(node.mInstructions);
//        nodeMarker.setSubDescription(Road.getLengthDurationText(this, node.mLength, node.mDuration));
//        Drawable icon = activity.getResources().getDrawable(R.drawable.next,null);
//        nodeMarker.setImage(icon);

//        NominatimPOIProvider poiProvider = new NominatimPOIProvider("");
//        ArrayList<POI> pois = poiProvider.getPOICloseTo(startPoint, "cinema", 50, 0.1);

//        FolderOverlay poiMarkers = new FolderOverlay(ctx);
//        map.getOverlays().add(poiMarkers);
//        Drawable poiIcon = activity.getResources().getDrawable(R.drawable.moreinfo_arrow_pressed,null);
//        ArrayList<POI> pois = poiProvider.getPOIAlong(road.getRouteLow(), "fuel", 50, 2.0);
//        for (POI poi:pois){
//            MyMarker poiMarker = new MyMarker(map);
//            poiMarker.setTitle(poi.mType);
//            poiMarker.setSnippet(poi.mDescription);
//            poiMarker.setPosition(poi.mLocation);
//            poiMarker.setIcon(poiIcon);
//            if (poi.mThumbnail != null){
//                poiItem.setImage(new BitmapDrawable(poi.mThumbnail));
//            }
//            poiMarkers.add(poiMarker);
//        }

//        BoundingBox bb = kmlDocument.mKmlRoot.getBoundingBox();
////        map.zoomToBoundingBox(bb);
//        map.getController().setCenter(bb.getCenter());
//
//        Drawable defaultMarker = activity.getResources().getDrawable(R.drawable.navto_small,null);
//        Bitmap defaultBitmap = ((BitmapDrawable)defaultMarker).getBitmap();
//        Style defaultStyle = new Style(defaultBitmap, 0x901010AA, 3.0f, 0x20AA1010);

//        FolderOverlay kmlOverlay = (FolderOverlay)mKmlDocument.mKmlRoot.buildOverlay(map, defaultStyle, null, mKmlDocument);
//        KmlFeature.Styler styler = new MyKmlStyler();
//        FolderOverlay kmlOverlay = (FolderOverlay)mKmlDocument.mKmlRoot.buildOverlay(map, null, styler, mKmlDocument);

//        Style panda_area = new Style(pandaBitmap, 0x901010AA, 3.0f, 0x2010AA10);
//        mKmlDocument.putStyle("panda_area", panda_area);
//        kmlDocument.mKmlRoot.addOverlay(roadOverlay, kmlDocument);
//        kmlDocument.mKmlRoot.addOverlay(roadNodes, kmlDocument);
//        File localFile = kmlDocument.getDefaultPathForAndroid("my_route.kml");
//        kmlDocument.saveAsKML(localFile);

//        File localFile = kmlDocument.getDefaultPathForAndroid("my_route.json");
//        kmlDocument.saveAsGeoJSON(localFile);

//        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(ctx, this);
//        map.getOverlays().add(0, mapEventsOverlay);

//        InfoWindow.closeAllInfoWindowsOn(map);

//        ArchiveFileFactory.getArchiveFile(ctx.getMapsSdCard());
//        File f1 = new File(strPath+"/MapViewer/KML/University.kmz");
//        if(f1.exists())
//        {
//            MBTilesFileArchive.getDatabaseFileArchive(f1);
//        }
//        else
//        {
//            Toast.makeText(ctx, "["+f1.getPath()+"] Not found.", Toast.LENGTH_LONG).show();
//        }

            if(!MainActivity.bNavigation) {
                fov_overlay = new FOV_Overlay(MainActivity.ctx);
                fov_overlay.setCameraPos(cameraPoint);
                fov_overlay.setTargetPos(targetPoint);
                fov_overlay.setHomePos(homePoint);
                fov_overlay.setYaw(0.0f);
                fov_overlay.setAzi(0.0f);
                fov_overlay.setFOV(60.0f);
                fov_overlay.update_calculations();
                map.getOverlays().add(fov_overlay);
            }

//            Bitmap direction_icon = BitmapFactory.decodeResource(MainActivity.ctx.getResources(), R.drawable.dir03);
//            Bitmap direction_icon = BitmapFactory.decodeResource(MainActivity.ctx.getResources(), R.drawable.dir04);
//            Bitmap direction_icon = BitmapFactory.decodeResource(MainActivity.ctx.getResources(), R.drawable.dir05);
//            Bitmap direction_icon = BitmapFactory.decodeResource(MainActivity.ctx.getResources(), R.drawable.dir05);
            Bitmap direction_icon = BitmapFactory.decodeResource(MainActivity.ctx.getResources(), R.drawable.dir10);
//            direction_icon = MainActivity.addColor(direction_icon,Color.BLUE);
            direction_icon = MainActivity.addColor(direction_icon,Color.YELLOW);

//            Bitmap no_direction_icon = BitmapFactory.decodeResource(MainActivity.ctx.getResources(), R.drawable.crosshair5);
//            no_direction_icon = MainActivity.addColor(no_direction_icon,Color.GREEN);
            Bitmap no_direction_icon = ((BitmapDrawable) mv_utils.getDrawable(MainActivity.ctx, org.osmdroid.library.R.drawable.person)).getBitmap();

            mLocationOverlay = new mv_LocationOverlay(new GpsmvLocationProvider(MainActivity.ctx),map);
            mLocationOverlay.disableFollowLocation();
            mLocationOverlay.setDirectionArrow(no_direction_icon,direction_icon);
            mLocationOverlay.enableMyLocation();
            mLocationOverlay.setEnabled(true);
            mLocationOverlay.setDrawAccuracyEnabled(true);
//            mLocationOverlay.setOptionsMenuEnabled(true);
            map.getOverlays().add(mLocationOverlay);

//            mLocationOverlay.runOnFirstFix(new Runnable() {
//                @Override
//                public void run() {
//                    final GeoPoint myLocation = mLocationOverlay.getMyLocation();
//                    if (myLocation != null) {
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                map.getController().animateTo(myLocation);
//                            }
//                        });
//                    };
//                }
//            });

            ruler_overlay = new Ruler_Overlay(MainActivity.ctx, map);
            ruler_overlay.setPos1(cameraPoint);
            ruler_overlay.setPos2(cameraPoint);
            ruler_overlay.setEnabled(true);
            ruler_overlay.setEdit(false);
            map.getOverlays().add(ruler_overlay);
            ruler_overlay.setOnMeasureListener(new Ruler_Overlay.OnRulerListener() {
                @Override
                public boolean onMeasure() {
                    update_ruler();
                    return false;
                }
            });

            cross_overlay = new Cross_Overlay(MainActivity.ctx);
            cross_overlay.setEnabled(true);
            map.getOverlays().add(cross_overlay);

//        path_overlay = new Path_Overlay();
//        map.getOverlays().add(path_overlay);
//        path_overlay.setEnabled(true);

            // Create an ArrayList with overlays to display objects on map
//        overlayItemArray = new ArrayList<OverlayItem>();
//
//        // Create som init objects
//        OverlayItem linkopingItem = new OverlayItem("Linkoping", "Sweden",
//                new GeoPoint(58.4109, 15.6216));
//        OverlayItem stockholmItem = new OverlayItem("Stockholm", "Sweden",
//                new GeoPoint(59.3073348, 18.0747967));
//
//        // Add the init objects to the ArrayList overlayItemArray
//        overlayItemArray.add(linkopingItem);
//        overlayItemArray.add(stockholmItem);
//
//        // Add the Array to the IconOverlay
//        ItemizedIconOverlay<OverlayItem> itemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(ctx, overlayItemArray, null);
//
//        // Add the overlay to the MapView
//        map.getOverlays().add(itemizedIconOverlay);

            // add camera marker
            if(!MainActivity.bNavigation) {
                cam_Marker = new MyMarker(map);
                cam_Marker.setPosition(cameraPoint);
                cam_Marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                cam_Marker.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                cam_Marker.setIcon(mv_utils.getDrawable(MainActivity.ctx, R.drawable.marker_icon));
                cam_Marker.setTitle("Camera point: ");
                cam_Marker.setInfoWindow(new CustomInfoWindow(0, map, null, null, null));
//            cam_Marker.setOnMarkerClickListener((item, arg1) -> {
//                item.showInfoWindow();
//                return true;
//            });
                map.getOverlays().add(cam_Marker);
//                set_cam_pos(cameraPoint.getLongitude(), cameraPoint.getLatitude(), cameraPoint.getAltitude(), false);
            }

            // add target marker
//            if(!bNavigation){
                target_Marker = new MyMarker(map);
                target_Marker.setPosition(targetPoint);
                target_Marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                target_Marker.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                target_Marker.setIcon(mv_utils.getDrawable(MainActivity.ctx, R.drawable.marker_target));
                target_Marker.setTitle(activity.getString(R.string.target));
                target_Marker.setInfo1("");
                target_Marker.setInfo2("");
//            target_Marker.setSubDescription("Target point: ");
//            target_Marker.setEnabled(true);
//            target_Marker.setDraggable(true);
//            target_Marker.setOnMarkerDragListener(new OnMarkerDragListenerTarget());
                target_Marker.setInfoWindow(new CustomInfoWindow(0, map, null, null, null));
//            target_Marker.setOnMarkerClickListener((item, arg1) -> {
//                item.showInfoWindow();
//                return true;
//            });
                map.getOverlays().add(target_Marker);
//            set_target_pos(targetPoint.getLongitude(),targetPoint.getLatitude(),targetPoint.getAltitude(),false);
//            }

            // gun
//            gun_Marker = new MyMarker(map);
//            gun_Marker.setPosition(gun_point);
//            gun_Marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
//            gun_Marker.setIcon(MainActivity.activity.getResources().getDrawable(R.drawable.marker_projectile,null));
//            gun_Marker.setTitle("Gun");
//            gun_Marker.setInfo("");
//            gun_Marker.setInfoWindow(new CustomInfoWindow(map));
////                        gun_Marker.setOnMarkerClickListener((item, arg1) -> {
////                            item.showInfoWindow();
////                            return true;
////                        });
//            map.getOverlays().add(gun_Marker);

            // add POI marker
            POI_Marker = new MyMarker(map);
            POI_Marker.setPosition(poiPoint);
            POI_Marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
            POI_Marker.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
            POI_Marker.setIcon(mv_utils.getDrawable(MainActivity.ctx, R.drawable.syria));
            POI_Marker.setTitle("POI point: ");
            POI_Marker.setInfoWindow(new CustomInfoWindow(0, map, null, null, null));
//            POI_Marker.setOnMarkerClickListener((item, arg1) -> {
//                item.showInfoWindow();
//                return true;
//            });
            map.getOverlays().add(POI_Marker);
            set_poi_pos(poiPoint.getLongitude(),poiPoint.getLatitude(),poiPoint.getAltitude(), "",false);

            // add home marker
            if(!MainActivity.bNavigation) {
                home_Marker = new MyMarker(map);
                home_Marker.setPosition(homePoint);
                home_Marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_CENTER);
                home_Marker.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                home_Marker.setIcon(mv_utils.getDrawable(MainActivity.ctx, R.drawable.center));
                home_Marker.setTitle("Home point: ");
                home_Marker.setInfoWindow(new CustomInfoWindow(0, map, null, null, null));
//            home_Marker.setOnMarkerClickListener((item, arg1) -> {
//                item.showInfoWindow();
//                return true;
//            });
                map.getOverlays().add(home_Marker);
//                set_home_pos(homePoint.getLongitude(), homePoint.getLatitude(), homePoint.getAltitude(), false);
            }

            // add start marker
            if(!MainActivity.bNavigation) {
                start_Marker = new MyMarker(map);
                start_Marker.setPosition(startPoint);
                start_Marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_CENTER);
                start_Marker.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                start_Marker.setIcon(mv_utils.getDrawable(MainActivity.ctx, R.drawable.icons8_leaving_geo_fence_40));
                start_Marker.setTitle("Start point: ");
                start_Marker.setInfoWindow(new CustomInfoWindow(0, map, null, null, null));
//            start_Marker.setOnMarkerClickListener((item, arg1) -> {
//                item.showInfoWindow();
//                return true;
//            });
                map.getOverlays().add(start_Marker);
                set_start_pos(startPoint.getLongitude(), startPoint.getLatitude(), false);
            }

//            IconPlottingOverlay plotter = new IconPlottingOverlay(this.getResources().getDrawable(R.drawable.osm_ic_follow_me_on,null));
//            map.getOverlayManager().add(plotter);

            if(!MainActivity.bNavigation) {
                try {
                    if(MApplication.isRealDevice()) {
                        timeline = new TimelineMissionControl();
                        timeline.onAttachedToWindow();
                    }
                } catch (Throwable ex) {
                    MainActivity.MyLog(ex);
                }
            }

//            CopyAssets(MainActivity.ctx);
            copyAssetsFileOrDir(MainActivity.ctx, "MapViewer");

            try {
                EGM96 egm96 = EGM96.getInstance();
                if (egm96 != null) {
                    if (!egm96.isEGMGridLoaded()) {
                        egm96.LoadGridFromFile(MainActivity.dir_internal + "/MapViewer/WW15MGH.DAC");
                    }
                }
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }

            // map events
//            MapEventsReceiver mReceive = new MapEventsReceiver() {
//                @Override
//                public boolean singleTapConfirmedHelper(GeoPoint p) {
////                    try
////                    {
////                        InfoWindow.closeAllInfoWindowsOn(map);
////                        p.setAltitude(MainActivity.GetHeightJNI(p.getLongitude(),p.getLatitude()));
////                        enter_point(p);
////                    }
////                    catch (Throwable ex)
////                    {
////                        MainActivity.MyLog(ex);
////                    }
//                    return false;
//                }
//
//                @Override
//                public boolean longPressHelper(GeoPoint p) {
////                    if(mapEditButtons.getVisibility() == View.VISIBLE) {
//////                        if(!Tab_Map.edit_mode) {
//////                            add_point(p, "Target", 0);
//////                            return true;
//////                        }
////                    }else if(cb_ruler.isChecked()){
////
////                    }else{
////                        add_point(p, "Target", 0);
////                        return true;
////                    }
//
//                    if(cb_add_favorite.isChecked()){
//                        p.setAltitude(MainActivity.GetHeightJNI(p.getLongitude(),p.getLatitude()));
//                        add_point(p, activity.getString(R.string.target), 0);
//                        return true;
//                    }else if(cb_add_path.isChecked() || cb_add_polygon.isChecked()){
//                        p.setAltitude(MainActivity.GetHeightJNI(p.getLongitude(),p.getLatitude()));
//                        enter_point(p);
//                    }
//                    return false;
//                }
//            };
//            MapEventsOverlay OverlayEvents = new MapEventsOverlay(mReceive);
//            map.getOverlays().add(OverlayEvents);

            customHandler.postDelayed(updateTimerThread, 50);

            s_map_text_size.setSelection(map_text_size_index);
            if(cross_overlay != null) {
                map_text_size = Float.parseFloat(map_text_sizes[map_text_size_index]);
                cross_overlay.setTextSize(map_text_size);
                setRulerTextSize(map_text_size);
            }

            if(map_clustering)
                kmlOverlay.setRadius(100);
            else
                kmlOverlay.setRadius(-1);
            kmlOverlay.invalidate();

            et_icons_scale.setText(String.valueOf(map_icon_scale));
            et_target_radius.setText(String.valueOf(map_target_radius));
            et_image_transparency.setText(String.valueOf(map_image_transparency));

            s_map_coordinates.setSelection(map_coordinate_index);

            customMapHandler.postDelayed(updateMapTimerThread, 100);

            // refresh map
            map.postInvalidate();
            bIsCreated = true;
            MainActivity.set_fullscreen();

            MainActivity.check_location_permission();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void copyAssetsFileOrDir(Context context, String path) {
        AssetManager assetManager = context.getAssets();
        String[] assets = null;
        try {
            assets = assetManager.list(path);
            assert assets != null;
            if (assets.length == 0) {
                copyFile(context, path);
//                Log.d("path = ",path);
            } else {
//                String fullPath = "/data/data/" + context.getPackageName() + "/" + path;
                String fullPath = MainActivity.dir_internal + File.separator + path;
//                Log.d("fullPath = ",fullPath);
                File dir = new File(fullPath);
                if (!dir.exists())
                    dir.mkdirs();
                for (String asset : assets) {
                    copyAssetsFileOrDir(context, path + "/" + asset);
                }
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    private void copyFile(Context context, String filename) {
//            String newFileName = "/data/data/" + context.getPackageName() + "/" + filename;
        String newFileName = MainActivity.dir_internal + File.separator + filename;
//        Log.d("newFileName = ", newFileName);
        if(FileHelper.FileNotExists(newFileName)) {
            AssetManager assetManager = context.getAssets();

            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                out = new FileOutputStream(newFileName);

                byte[] buffer = new byte[1024000];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        }
    }

    static private void CopyAssets(Context context) {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("MapViewer");
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        if(files != null) {
            String strFileName;
            for (String filename : files) {
                strFileName = MainActivity.strMapViewer + filename;
                if(FileHelper.FileNotExists(strFileName)) {
                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        in = assetManager.open("MapViewer/" + filename);   // if files resides inside the "Files" directory itself
                        out = new FileOutputStream(strFileName);
                        copyFile(in, out);
                        in.close();
                        in = null;
                        out.flush();
                        out.close();
                        out = null;
                    } catch (Exception e) {
                        Log.e("tag", e.getMessage());
                    }
                }
            }
        }
    }

    static private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

//    private static class CustomMarker extends MyMarker
//    {
//
//        public CustomMarker(MapView mapView, Context resourceProxy) {
//            super(mapView, resourceProxy);
//        }
//
//        public CustomMarker(MapView mapView) {
//            super(mapView);
//        }
//
//        public String desc;
//    }

//    public class CustomInfoWindow extends MarkerInfoWindow {
//
//        private CustomMarker marker;
//
//        public CustomInfoWindow(MapView mapView) {
//            super(R.layout.bonuspack_bubble, mapView);
//        }
//
//        @Override
//        public void onOpen(Object item) {
//            marker = (CustomMarker) item;
//            final Button btn = (Button) (mView.findViewById(R.id.bubble_moreinfo));
//            btn.setText("Ok");
//            final TextView tv_title = (TextView) (mView.findViewById(R.id.bubble_title));
//            btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (marker != null) {
//                        if (marker.name != null && marker.desc != null) {
//                            tv_title.setText(marker.name);
////                            btn.setText(marker.name);
////                            btn.setDescription(marker.desc);
//                        }
//                    }
//                }
//            });
//        }
//    }

    static public String convert_coordinates(double lon,double lat,int type,boolean oneLine, boolean showDirection)
    {
        String strText = "";
        double[] res;
        double X, Y;
        int zone;
        String strSep1 = " ";
        String strSep2 = ",";
        String strLonDirection = "";
        String strLatDirection = "";

        if(!oneLine){
            strSep1 = "\n";
            strSep2 = "\n";
        }

        if(showDirection){
            strLonDirection = (lon>=0?" E":" W");
            strLatDirection = (lat>=0?" N":" S");
        }

        if(type == 0) {
            if(oneLine)
                strText = "GEO:"+strSep1+String.format(Locale.ENGLISH,"%.06f", lat)+strLatDirection+strSep2+String.format(Locale.ENGLISH,"%.06f", lon)+strLonDirection;
            else
                strText = "GEO:"+strSep1+String.format(Locale.ENGLISH," LAT:%.06f", lat)+strLatDirection+strSep2+String.format(Locale.ENGLISH," LON:%.06f", lon)+strLonDirection;
        }

        if(type == 1) {
            strText = "DMS:"+strSep1+MainActivity.CoordinatesToDMS(lon,lat,oneLine);
        }

        if(type == 2) {
            int ZoneNumber;
            char UTMChar;
            res = MainActivity.LL2UTM(lon, lat);
            X = res[0];
            Y = res[1];
            ZoneNumber = (int)res[2];
            UTMChar = (char)res[3];
            String Zone = String.format(Locale.ENGLISH, "%d%c", ZoneNumber,UTMChar);
            if(oneLine)
                strText = "UTM:"+strSep1+String.format(Locale.ENGLISH, "%.01f", Y)+strLatDirection + strSep2 + String.format(Locale.ENGLISH, "%.01f", X)+strLonDirection + strSep2 + Zone;
            else
                strText = "UTM:"+strSep1+String.format(Locale.ENGLISH, " Y:%.01f", Y)+strLatDirection + strSep2 + String.format(Locale.ENGLISH, " X:%.01f", X)+strLonDirection + strSep2 + " ZONE:"+Zone;
        }

        if(type == 3) {
            res = MainActivity.LL2STM(lon, lat);
            X = res[0];
            Y = res[1];
            zone = (int) res[2];
            if(oneLine)
                strText = "STM:"+strSep1+String.format(Locale.ENGLISH, "%.01f", Y)+strLatDirection + strSep2 + String.format(Locale.ENGLISH, "%.01f", X)+strLonDirection + strSep2 + String.format(Locale.ENGLISH, "%d", zone);
            else
                strText = "STM:"+strSep1+String.format(Locale.ENGLISH, " Y:%.01f", Y)+strLatDirection + strSep2 + String.format(Locale.ENGLISH, " X:%.01f", X)+strLonDirection + strSep2 + String.format(Locale.ENGLISH, " ZONE:%d", zone);
        }

        if(type == 4) {
            ProjCoordinate result = Proj.wgs84_to_sk42(lon, lat);
            X = result.x;
            Y = result.y;
            zone = (int) result.z;
            if(oneLine)
                strText = "SK42:"+strSep1+String.format(Locale.ENGLISH, "%.06f", Y)+strLatDirection + strSep2 + String.format(Locale.ENGLISH, "%.06f", X)+strLonDirection;
            else
                strText = "SK42:"+strSep1+String.format(Locale.ENGLISH, " Y:%.06f", Y)+strLatDirection + strSep2 + String.format(Locale.ENGLISH, " X:%.06f", X)+strLonDirection;
        }

        if(type == 5) {
            ProjCoordinate result = Proj.wgs84_to_gauss_kruger(lon, lat);
//            X = result.x;
//            Y = result.y;
            X = result.y;
            Y = result.x;
            zone = (int) result.z;
            if(oneLine)
                strText = "SK42-GK:"+strSep1+String.format(Locale.ENGLISH, "%.01f", Y)+strLatDirection + strSep2 + String.format(Locale.ENGLISH, "%.01f", X)+strLonDirection + strSep2+"GK" + String.format(Locale.ENGLISH, "%d", zone);
            else
                strText = "SK42-GK:"+strSep1+String.format(Locale.ENGLISH, " Y:%.01f", Y)+strLatDirection + strSep2 + String.format(Locale.ENGLISH, " X:%.01f", X)+strLonDirection + strSep2+" ZONE:GK" + String.format(Locale.ENGLISH, "%d", zone);
        }

        return strText;
    }

    static public Object curr_object = null;

    //    public class CustomInfoWindow extends InfoWindow {
//        private InfoWindow thisInfo;
    public static class CustomInfoWindow extends MyMarkerInfoWindow {
        private final int type;
        private final MyMarkerInfoWindow thisInfo;
        public Overlay marker = null;
        public KmlPlacemark kmlPlacemark = null;
        public City city = null;

        public CustomInfoWindow(int type, MapView mapView, Overlay marker, KmlPlacemark kmlPlacemark, City city) {
            super(R.layout.mapviewer_bubble, mapView);//my custom layout and my mapView
            thisInfo = this;
            this.type = type;
            this.marker = marker;
            this.kmlPlacemark = kmlPlacemark;
            if(city != null) {
                this.city = city;
            }

//            GeoPoint p = new GeoPoint(0.0,0.0);
//            if(marker instanceof MyMarker)
//            {
////                MyMarker marker0 = (MyMarker) marker; //the marker on which you click to open the bubble
////                p = marker0.getPosition();
////
////                marker0.setInfoWindowLocation(p);
////                mapController.setCenter(p);
//            }
//            else
//            if(marker instanceof Polyline)
//            {
//                Polyline polyline0 = (Polyline) marker;
//                List<GeoPoint> points = polyline0.getActualPoints();
//                if(points.size() > 0) {
//                    p = points.get(0);
//                }
//                polyline0.setInfoWindowLocation(p);
////                mapController.setCenter(p);
//            }
//            else
//            if(marker instanceof MyPolygon)
//            {
//                MyPolygon polygon0 = (MyPolygon) marker;
//                List<GeoPoint> points = polygon0.getActualPoints();
//                if(points.size() > 0) {
//                    p = points.get(0);
//                }
//                polygon0.setInfoWindowLocation(p);
////                mapController.setCenter(p);
//            }
        }

        @Override
        public void onClose() {
            //by default, do nothing
        }

        public boolean writeKMLExtendedData(Writer writer){
            if(kmlPlacemark == null)    return false;
            if (kmlPlacemark.mExtendedData == null) return true;
            try {
                writer.write("<ExtendedData>\n");
                for (Map.Entry<String, String> entry : kmlPlacemark.mExtendedData.entrySet()) {
                    String name = entry.getKey();
                    String value = entry.getValue();
                    writer.write("<Data name=\""+name+"\"><value>"+ StringEscapeUtils.escapeXml10(value)+"</value></Data>\n");
                }
                writer.write("</ExtendedData>\n");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        public void writeKMLStyles(Writer writer){
            if(kmlPlacemark == null)    return;
            for (Map.Entry<String, StyleSelector> entry : kmlFavoritesDocument.getStyles().entrySet()) {
                String styleId = entry.getKey();
                if(Objects.equals(kmlPlacemark.mStyle, styleId)) {
                    StyleSelector styleSelector = entry.getValue();
                    styleSelector.writeAsKML(writer, styleId);
                }
            }
        }

        public boolean writeAsKML(Writer writer, boolean isDocument, KmlDocument kmlDocument){
            try {
                if(kmlPlacemark == null)    return false;
                kmlPlacemark.setExtendedData("App","MapViewer");

                //TODO: push this code in each subclass
                String objectType;
                objectType = "Placemark";
                writer.write('<'+objectType);
                if (kmlPlacemark.mId != null)
                    writer.write(" id=\"mId\"");
                writer.write(">\n");
                if (kmlPlacemark.mStyle != null){
                    writer.write("<styleUrl>#"+kmlPlacemark.mStyle+"</styleUrl>\n");
                    //TODO: if styleUrl is external, don't add the '#'
                }
                if (kmlPlacemark.mName != null){
                    writer.write("<name>"+ StringEscapeUtils.escapeXml10(kmlPlacemark.mName)+"</name>\n");
                }
                if (kmlPlacemark.mDescription != null){
                    writer.write("<description><![CDATA["+kmlPlacemark.mDescription+"]]></description>\n");
                }
                if (!kmlPlacemark.mVisibility){
                    writer.write("<visibility>0</visibility>\n");
                }
                kmlPlacemark.writeKMLSpecifics(writer);
                writeKMLExtendedData(writer);
                if (isDocument){
                    writeKMLStyles(writer);
                }
                writer.write("</"+objectType+">\n");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        public void sendMark(){
            try {
                if(kmlPlacemark == null)    return;
                File file = new File(MainActivity.strPlacemarkCaptured);
                try{
                    FileWriter fWriter = new FileWriter(file, false);
                    fWriter.write("<?xml version='1.0' encoding='UTF-8'?>\n");
                    fWriter.write("<kml xmlns='http://www.opengis.net/kml/2.2' xmlns:gx='http://www.google.com/kml/ext/2.2'>\n");
                    fWriter.write("<Document>\n");

//                    kmlPlacemark.writeAsKML(fWriter,true,kmlFavoritesDocument);
                    writeAsKML(fWriter,true,kmlFavoritesDocument);

                    fWriter.write("</Document>\n");
                    fWriter.write("</kml>\n");

                    fWriter.flush();
                    fWriter.close();
                } catch (Throwable ex) {
                    MainActivity.MyLog(ex);
                }
                Tab_Messenger.sendFile(MainActivity.strPlacemarkCaptured, true, "Placemark sending...", true, new tcp_io_handler.SendCallback() {
                    @Override
                    public void onFinish(int error) {
                        if(error != tcp_io_handler.TCP_OK) {
                            if (MainActivity.IsDebugJNI()) {
                                MainActivity.MyLogInfoSilent("The Current Line Number is " + Arrays.toString(new Throwable().getStackTrace()));
                            }
                        }
                        MapViewerView.process_click(R.id.radio_messenger);
                    }
                });
                thisInfo.close();
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        }

        @Override
        public void onOpen(Object item) {
            try {
                String strText = "";
                final double fLon,fLat;
                final float fAlt;
                GeoPoint p = new GeoPoint(0.0,0.0);

                if(item instanceof MyMarker)
                {
                    MyMarker marker0 = (MyMarker) item; //the marker on which you click to open the bubble
                    strText = marker0.getTitle();
                    p = marker0.getPosition();
//                    marker0.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER,MyMarker.ANCHOR_TOP);
//                    marker0.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER,MyMarker.ANCHOR_BOTTOM);
//                    marker0.setAnchor(MyMarker.ANCHOR_CENTER,MyMarker.ANCHOR_BOTTOM);
                    mapController.setCenter(p);
                }
                else
                if(item instanceof MyPolyline)
                {
                    MyPolyline polyline0 = (MyPolyline) item;
                    strText = polyline0.getTitle();

//                    List<GeoPoint> points = polyline0.getActualPoints();
//                    if(points.size() > 0) {
//                        p = points.get(0);
//                    }else{
//                        p = polyline0.getInfoWindowLocation();
//                    }
                    p = polyline0.getPosition();
//                    polyline0.setInfoWindowLocation(p);
//                    mapController.setCenter(p);
//                    p = polyline0.getInfoWindowLocation();

//                    polyline0.setInfoWindowLocation(p);
//                    polyline0.showInfoWindow();
//                    mapController.setCenter(p);
                }
                else
                if(item instanceof MyPolygon)
                {
                    MyPolygon polygon0 = (MyPolygon) item;
                    strText = polygon0.getTitle();
//                    List<GeoPoint> points = polygon0.getActualPoints();
//                    if(points.size() > 0) {
//                        p = points.get(0);
//                    }else{
//                        p = polygon0.getInfoWindowLocation();
//                    }
                    p = polygon0.getPosition();
//                    polygon0.setInfoWindowLocation(p);
//                    mapController.setCenter(p);
//                    p = polygon0.getInfoWindowLocation();

//                    polygon0.setInfoWindowLocation(p);
//                    polygon0.showInfoWindow();
//                    mapController.setCenter(p);
                }
                else
                {
                    return;
                }

                fLon = p.getLongitude();
                fLat = p.getLatitude();
//                fAlt = (float)p.getAltitude();
                fAlt = MainActivity.GetHeightJNI(fLon, fLat);

                LinearLayout ll_edit = mView.findViewById(R.id.ll_edit);
                if(city != null)
                    ll_edit.setVisibility(View.VISIBLE);
                else
                    ll_edit.setVisibility(View.GONE);

                TextView bubble_title = mView.findViewById(R.id.bubble_title);
                bubble_title.setTextSize(Tab_Map.map_text_size);
                bubble_title.setText(strText);

//                strText = Tab_Map.convert_coordinates(fLon,fLat,Tab_Map.map_coordinate_index,true)+" ,A: "+String.format(Locale.ENGLISH, "%.01f", fAlt);
                strText = Tab_Map.convert_coordinates(fLon,fLat,Tab_Map.map_coordinate_index,true,true)+"\nAltitude: "+String.format(Locale.ENGLISH, "%.01f", fAlt);
//                strText = Tab_Map.convert_coordinates(fLon,fLat,Tab_Map.map_coordinate_index,false)+"\n A: "+String.format(Locale.ENGLISH, "%.01f", fAlt);
                TextView bubble_dec_geo_coord = mView.findViewById(R.id.bubble_dec_geo_coord);
                bubble_dec_geo_coord.setTextSize(Tab_Map.map_text_size);
                bubble_dec_geo_coord.setText(strText);

                // area/length
                double area = 0,length = 0;
                int count = 0;
                if(city != null) {
                    area = MainActivity.tab_map.path_area(city.index);
                    length = MainActivity.tab_map.path_length(city.index);
                    count = MainActivity.tab_map.path_count(city.index);
                }
                String strInfo = "";
                if((count > 1) && ((area > 0) || (length > 0))) {
//                String strInfo = "Length: "+ String.format(Locale.ENGLISH,"%.03f",length) +" [m], Area: " + String.format(Locale.ENGLISH,"%.03f",area) + " [m²]";
//                    strInfo = "Length: " + String.format(Locale.ENGLISH, "%.03f", length / 1000) + " [km], Area: " + String.format(Locale.ENGLISH, "%.03f", area / 1000000) + " [km²]";
                    strInfo = "Count: "+String.format(Locale.ENGLISH, "%d", count)+" [points], Length: " + String.format(Locale.ENGLISH, "%.03f", length / 1000) + " [km], Area: " + String.format(Locale.ENGLISH, "%.03f", area / 1000000) + " [km²]";
                }
                TextView bubble_geo_coord = mView.findViewById(R.id.bubble_geo_coord);
                bubble_geo_coord.setTextSize(Tab_Map.map_text_size);
                bubble_geo_coord.setText(strInfo);

//                strText = convert_coordinates(fLon,fLat,0,true)+", "+String.format(Locale.ENGLISH,"%d",(int)fAlt);
//                TextView bubble_dec_geo_coord = mView.findViewById(R.id.bubble_dec_geo_coord);
//                bubble_dec_geo_coord.setText(strText);
//
//                strText = convert_coordinates(fLon,fLat,1,true);
//                TextView bubble_geo_coord = mView.findViewById(R.id.bubble_geo_coord);
//                bubble_geo_coord.setText(strText);
//
//                strText = convert_coordinates(fLon,fLat,2,true);
//                TextView bubble_utm_coord = mView.findViewById(R.id.bubble_utm_coord);
//                bubble_utm_coord.setText(strText);
//
//                strText = convert_coordinates(fLon,fLat,3,true);
//                TextView bubble_stm_coord = mView.findViewById(R.id.bubble_stm_coord);
//                bubble_stm_coord.setText(strText);
//
//                strText = convert_coordinates(fLon,fLat,4,true);
//                TextView bubble_sk42_coord = mView.findViewById(R.id.bubble_sk42_coord);
//                bubble_sk42_coord.setText(strText);
//
//                strText = convert_coordinates(fLon,fLat,5,true);
//                TextView bubble_sk42_gk_coord = mView.findViewById(R.id.bubble_sk42_gk_coord);
//                bubble_sk42_gk_coord.setText(strText);

                LinearLayout ll_drone = mView.findViewById(R.id.ll_drone);
                if(MainActivity.bNavigation){
                    ll_drone.setVisibility(View.GONE);
                }else{
                    if(MainActivity.isDevelpoment()) {
                        ll_drone.setVisibility(View.VISIBLE);
                    }else{
                        ll_drone.setVisibility(View.GONE);
                    }
                }

                Button bubble_goto = mView.findViewById(R.id.bubble_goto);
                bubble_goto.setTextSize(Tab_Map.map_text_size);
                bubble_goto.setOnClickListener(view -> {
                    try {
                        if (timeline == null) return;
                        String strText1 = e_altitude.getText().toString();
                        float altitude = Float.parseFloat(strText1);

                        timeline.GotoByTimeline(fLon, fLat, altitude);
                        Tab_Messenger.showToast("Goto here...");
//                            Toast.makeText(MainActivity.ctx, "Goto here...", Toast.LENGTH_LONG).show();
                        MainActivity.hide_keyboard(null);
                        thisInfo.close();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                });

                Button bubble_lookat = mView.findViewById(R.id.bubble_lookat);
                bubble_lookat.setTextSize(Tab_Map.map_text_size);
                bubble_lookat.setOnClickListener(view -> {
                    try {
//                        MainActivity.target_lon = fLon;
//                        MainActivity.target_lat = fLat;
//                        MainActivity.target_alt = fAlt;
                        MainActivity.tab_map.set_target_pos(fLon, fLat, fAlt,true);
                        MainActivity.tab_map.mapController.setCenter(MainActivity.tab_map.targetPoint);
                        MainActivity.tab_map.mapController.setZoom(17.0);
                        Tab_Camera.crosshairView.invalidate();

                        float[] res1 = MainActivity.CalculateAngles(MainActivity.uav_lon,MainActivity.uav_lat,MainActivity.uav_alt,MainActivity.target_lon,MainActivity.target_lat,MainActivity.target_alt);
                        MainActivity.lastYaw = (float)MainActivity.db_deg(Math.toDegrees(res1[0]));
                        if(MainActivity.lastYaw >= 180.0)   MainActivity.lastYaw -= 360.0f;
                        MainActivity.lastPitch = (float)Math.toDegrees(res1[1]);
                        timeline.ChangeUAV_YawByTimeline(MainActivity.lastYaw,MainActivity.lastPitch);

                        MainActivity.save_settings();
                        Tab_Messenger.showToast("Look at...");
//                            Toast.makeText(MainActivity.ctx, "Look at...", Toast.LENGTH_LONG).show();
                        MainActivity.hide_keyboard(null);
                        thisInfo.close();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                        thisInfo.close();
                    }
                });

                Button bubble_panorama = mView.findViewById(R.id.bubble_panorama);
                bubble_panorama.setTextSize(Tab_Map.map_text_size);
                bubble_panorama.setOnClickListener(view -> {
                    try {
                        if (timeline == null) return;
                        String str = e_altitude.getText().toString();
                        float altitude = Float.parseFloat(str);

                        str = e_mission_speed.getText().toString();
                        float speed = Float.parseFloat(str);

                        timeline.PanoramaByTimeline(fLon, fLat, altitude, speed);
//                        Tab_Messenger.showToast("Panorama...");
                        MainActivity.hide_keyboard(null);
                        thisInfo.close();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                });

                ImageView iv_mark_settings = mView.findViewById(R.id.iv_mark_settings);
                iv_mark_settings.setVisibility(View.GONE);
                if(item instanceof MyMarker){
                    iv_mark_settings.setVisibility(View.VISIBLE);
                    iv_mark_settings.setImageBitmap(((BitmapDrawable)((MyMarker)item).getIcon()).getBitmap());
                    iv_mark_settings.setOnClickListener(view -> {
                        try {
                            activity.showIconsDialog(this);
                            MainActivity.hide_keyboard(null);
                            thisInfo.close();
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    });
                }else if(item instanceof MyPolyline){
                    iv_mark_settings.setVisibility(View.VISIBLE);
                    iv_mark_settings.setImageResource(R.drawable.polyline48);
                    iv_mark_settings.setOnClickListener(view -> {
                        try {
                            activity.showGeometrySettingsDialog(this);
                            MainActivity.hide_keyboard(null);
                            thisInfo.close();
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    });
                }else {
                    iv_mark_settings.setVisibility(View.VISIBLE);
                    iv_mark_settings.setImageResource(R.drawable.polygon_icon48);
                    iv_mark_settings.setOnClickListener(view -> {
                        try {
                            activity.showGeometrySettingsDialog(this);
                            MainActivity.hide_keyboard(null);
                            thisInfo.close();
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    });
                }

                Button b_more = mView.findViewById(R.id.b_more);
                b_more.setTextSize(Tab_Map.map_text_size);
                b_more.setOnClickListener(view -> {
                    try {
                        try {
                            switch (type){
                                case 2:{
                                    activity.showMark3Dialog(this);
                                    break;
                                }
                                case 1:{
                                    activity.showMark2Dialog(this);
                                    break;
                                }
                                case 0:{
                                    activity.showMark1Dialog(this);
                                    break;
                                }
                            }
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                        MainActivity.hide_keyboard(null);
                        thisInfo.close();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                });

                Button b_target = mView.findViewById(R.id.b_target);
                b_target.setTextSize(Tab_Map.map_text_size);
//                if(item instanceof MyMarker)
//                    b_target.setVisibility(View.VISIBLE);
//                else
//                    b_target.setVisibility(View.GONE);
                b_target.setOnClickListener(view -> {
                    try {
                        navigation_mode = false;
                        setEdit(false);

                        target_item = item;
                        mv_LocationOverlay.prev_I = -1;
                        mv_LocationOverlay.prev_hash_code = -1;

                        if(target_Marker != null){
                            target_Marker.setInfo1("");
                            target_Marker.setInfo2("");
                        }

                        if(target_item instanceof MyMarker)
                        {
//                            target_Marker = (MyMarker)item; //the marker on which you click to open the bubble

                            MyMarker marker0 = (MyMarker) target_item;

                            if(target_Marker != null) {
                                navigation_mode = true;

                                target_Marker.setEnabled(true);
                                target_Marker.setId(Integer.toString(0));
                                target_Marker.setPosition(marker0.getPosition());
                                target_Marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                                target_Marker.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                                target_Marker.setIcon(mv_utils.getDrawable(MainActivity.ctx, R.drawable.marker_target));
                                target_Marker.setTitle(activity.getString(R.string.target));
                                target_Marker.setInfo1("");
                                target_Marker.setInfo2("");
                                mapFinishMission.setVisibility(View.VISIBLE);

                                setEdit(true);
                            }
                        }
                        else
                        if(target_item instanceof MyPolyline)
                        {
                            MyPolyline polyline0 = (MyPolyline) target_item;

                            List<GeoPoint> points = polyline0.getActualPoints();
                            if(points.size() > 0) {
                                if (points.size() <= Tab_Map.map_max_points) {
                                    if(target_Marker != null) {
                                        navigation_mode = true;

                                        target_Marker.setEnabled(true);
                                        target_Marker.setId(Integer.toString(0));
                                        target_Marker.setPosition(points.get(0));
                                        target_Marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                                        target_Marker.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                                        target_Marker.setIcon(mv_utils.getDrawable(MainActivity.ctx, R.drawable.marker_target));
                                        target_Marker.setTitle(activity.getString(R.string.target));
                                        target_Marker.setInfo1("");
                                        target_Marker.setInfo2("");
                                        mapFinishMission.setVisibility(View.VISIBLE);

                                        setEdit(true);
                                    }
                                } else {
                                    new AlertDialog.Builder(activity)
                                        .setCancelable(false)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle(R.string.extra_points)
                                        .setMessage(R.string.are_you_sure_you_want_to_track_this_path)
                                        .setPositiveButton(R.string.yes_message, (dialog, which) -> {
                                            if(target_Marker != null) {
                                                navigation_mode = true;

                                                target_Marker.setEnabled(true);
                                                target_Marker.setId(Integer.toString(0));
                                                target_Marker.setPosition(points.get(0));
                                                target_Marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                                                target_Marker.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                                                target_Marker.setIcon(mv_utils.getDrawable(ctx, R.drawable.marker_target));
                                                target_Marker.setTitle(activity.getString(R.string.target));
                                                target_Marker.setInfo1("");
                                                target_Marker.setInfo2("");
                                                mapFinishMission.setVisibility(View.VISIBLE);

                                                setEdit(true);
                                            }
                                        })
                                        .setNegativeButton(R.string.no_message, (dialog, which) -> {
                                            MainActivity.set_fullscreen();
                                        })
                                        .show();
                                }
                            }
                        }
                        else
                        if(target_item instanceof MyPolygon)
                        {
                            MyPolygon polygon0 = (MyPolygon) target_item;

                            List<GeoPoint> points = polygon0.getActualPoints();
                            if(points.size() > 0) {
                                if (points.size() <= Tab_Map.map_max_points) {
                                    if(target_Marker != null) {
                                        navigation_mode = true;

                                        target_Marker.setEnabled(true);
                                        target_Marker.setId(Integer.toString(0));
                                        target_Marker.setPosition(points.get(0));
                                        target_Marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                                        target_Marker.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                                        target_Marker.setIcon(mv_utils.getDrawable(MainActivity.ctx, R.drawable.marker_target));
                                        target_Marker.setTitle(activity.getString(R.string.target));
                                        target_Marker.setInfo1("");
                                        target_Marker.setInfo2("");
                                        mapFinishMission.setVisibility(View.VISIBLE);

                                        setEdit(true);
                                    }
                                } else {
                                    new AlertDialog.Builder(activity)
                                            .setCancelable(false)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setTitle(R.string.extra_points)
                                            .setMessage(R.string.are_you_sure_you_want_to_track_this_path)
                                            .setPositiveButton(R.string.yes_message, (dialog, which) -> {
                                                if(target_Marker != null) {
                                                    navigation_mode = true;

                                                    target_Marker.setEnabled(true);
                                                    target_Marker.setId(Integer.toString(0));
                                                    target_Marker.setPosition(points.get(0));
                                                    target_Marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                                                    target_Marker.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                                                    target_Marker.setIcon(mv_utils.getDrawable(MainActivity.ctx, R.drawable.marker_target));
                                                    target_Marker.setTitle(activity.getString(R.string.target));
                                                    target_Marker.setInfo1("");
                                                    target_Marker.setInfo2("");
                                                    mapFinishMission.setVisibility(View.VISIBLE);

                                                    setEdit(true);
                                                }
                                            })
                                            .setNegativeButton(R.string.no_message, (dialog, which) -> {
                                                MainActivity.set_fullscreen();
                                            })
                                            .show();
                                }
                            }
                        }

                        MainActivity.hide_keyboard(null);
                        thisInfo.close();
                        map.postInvalidate();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }

//                    try {
//                        if(target_item instanceof MyMarker)
//                        {
//                            MyMarker marker0 = (MyMarker) target_item; //the marker on which you click to open the bubble
//                            marker0.setEdit(MainActivity.ctx, this.kmlPlacemark, true);
//                            map.postInvalidate();
//                        }
//                        else
//                        if(target_item instanceof MyPolyline)
//                        {
//                            MyPolyline polyline0 = (MyPolyline) target_item;
//                            polyline0.setEdit(MainActivity.ctx, this.kmlPlacemark, true);
//                            map.postInvalidate();
//                        }
//                        else
//                        if(target_item instanceof MyPolygon)
//                        {
//                            MyPolygon polygon0 = (MyPolygon) target_item;
//                            polygon0.setEdit(MainActivity.ctx, this.kmlPlacemark, true);
//                            map.postInvalidate();
//                        }
//
//                        MainActivity.hide_keyboard(null);
//                        thisInfo.close();
//                        map.postInvalidate();
//                    } catch (Throwable ex) {
//                        MainActivity.MyLog(ex);
//                    }
                });

                CheckBox cb_edit = mView.findViewById(R.id.cb_edit);
                cb_edit.setTextSize(Tab_Map.map_text_size);
                boolean edit = false;
                if(item instanceof MyMarker)
                {
                    MyMarker marker0 = (MyMarker) item; //the marker on which you click to open the bubble
                    edit = marker0.getEdit();
                }
                else
                if(item instanceof MyPolyline)
                {
                    MyPolyline polyline0 = (MyPolyline) item;
                    edit = polyline0.getEdit();
                }
                else
                if(item instanceof MyPolygon)
                {
                    MyPolygon polygon0 = (MyPolygon) item;
                    edit = polygon0.getEdit();
                }
                cb_edit.setChecked(edit);

                cb_edit.setOnClickListener(view -> {
                    try {
                        navigation_mode = false;
                        if(curr_object != null){
                            if(curr_object instanceof MyMarker)
                            {
                                MyMarker marker0 = (MyMarker) curr_object; //the marker on which you click to open the bubble
                                marker0.setEdit(MainActivity.ctx, this.kmlPlacemark, false);
                            }
                            else
                            if(curr_object instanceof MyPolyline)
                            {
                                MyPolyline polyline0 = (MyPolyline) curr_object;
                                polyline0.setEdit(MainActivity.ctx, this.kmlPlacemark, false);
                            }
                            else
                            if(curr_object instanceof MyPolygon)
                            {
                                MyPolygon polygon0 = (MyPolygon) curr_object;
                                polygon0.setEdit(MainActivity.ctx, this.kmlPlacemark, false);
                            }
                        }
                        curr_object = item;

                        if(item instanceof MyMarker)
                        {
                            if(cb_edit.isChecked())
                                mapEditButtons.setVisibility(View.VISIBLE);
                            else
                                mapEditButtons.setVisibility(View.GONE);

                            MyMarker marker0 = (MyMarker) item; //the marker on which you click to open the bubble
                            marker0.setEdit(MainActivity.ctx, this.kmlPlacemark, cb_edit.isChecked());
                            map.postInvalidate();
                        }
                        else
                        if(item instanceof MyPolyline)
                        {
                            MyPolyline polyline0 = (MyPolyline) item;
                            if(polyline0.getActualPoints().size() <= Tab_Map.map_max_points){
                                if(cb_edit.isChecked())
                                    mapEditButtons.setVisibility(View.VISIBLE);
                                else
                                    mapEditButtons.setVisibility(View.GONE);

                                polyline0.setEdit(ctx, this.kmlPlacemark, cb_edit.isChecked());
                                map.postInvalidate();
                            }else{
                                new AlertDialog.Builder(activity)
                                    .setCancelable(false)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle(R.string.extra_points)
                                    .setMessage(R.string.are_you_sure_you_want_to_edit_this_path)
                                    .setPositiveButton(R.string.yes_message, (dialog, which) -> {
                                        if(cb_edit.isChecked())
                                            mapEditButtons.setVisibility(View.VISIBLE);
                                        else
                                            mapEditButtons.setVisibility(View.GONE);

                                        polyline0.setEdit(ctx, this.kmlPlacemark, cb_edit.isChecked());
                                        map.postInvalidate();
                                    })
                                    .setNegativeButton(R.string.no_message, (dialog, which) -> {
                                        cb_edit.setChecked(false);
                                        MainActivity.set_fullscreen();
                                    })
                                    .show();
                            }
                        }
                        else
                        if(item instanceof MyPolygon)
                        {
                            MyPolygon polygon0 = (MyPolygon) item;
                            if(polygon0.getActualPoints().size() <= Tab_Map.map_max_points){
                                if(cb_edit.isChecked())
                                    mapEditButtons.setVisibility(View.VISIBLE);
                                else
                                    mapEditButtons.setVisibility(View.GONE);

                                polygon0.setEdit(MainActivity.ctx, this.kmlPlacemark, cb_edit.isChecked());
                                map.postInvalidate();
                            }else{
                                new AlertDialog.Builder(activity)
                                        .setCancelable(false)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle(R.string.extra_points)
                                        .setMessage(R.string.are_you_sure_you_want_to_edit_this_path)
                                        .setPositiveButton(R.string.yes_message, (dialog, which) -> {
                                            if(cb_edit.isChecked())
                                                mapEditButtons.setVisibility(View.VISIBLE);
                                            else
                                                mapEditButtons.setVisibility(View.GONE);

                                            polygon0.setEdit(MainActivity.ctx, this.kmlPlacemark, cb_edit.isChecked());
                                            map.postInvalidate();
                                        })
                                        .setNegativeButton(R.string.no_message, (dialog, which) -> {
                                            cb_edit.setChecked(false);
                                            MainActivity.set_fullscreen();
                                        })
                                        .show();
                            }
                        }

                        MainActivity.hide_keyboard(null);
                        thisInfo.close();
                        map.postInvalidate();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                });

                ImageView iv_send = mView.findViewById(R.id.iv_send);
                iv_send.setOnClickListener(view -> {
                    try {
                        sendMark();
                        MainActivity.hide_keyboard(null);
                        thisInfo.close();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                });

                ImageView b_mark_delete = mView.findViewById(R.id.b_mark_delete);
                if(city != null)
                    b_mark_delete.setVisibility(View.VISIBLE);
                else
                    b_mark_delete.setVisibility(View.GONE);
                b_mark_delete.setOnClickListener(view -> {
                    try {
                        new AlertDialog.Builder(activity)
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.delete)
                            .setMessage(R.string.are_you_sure_delete)
                            .setPositiveButton(R.string.yes_message, (dialog, which) -> {
                                MainActivity.tab_map.delete_city(city);
                                Tab_Messenger.showToast("["+city.strName +"] Deleted...");
                                MainActivity.hide_keyboard(null);
                            })
                            .setNegativeButton(R.string.no_message, (dialog, which) -> {
                                MainActivity.hide_keyboard(null);
                            })
                            .show();

                        thisInfo.close();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                });

//                if(city != null){
//                    Tab_Map.favorites_adapter.onItemSelected();
//                }
            }
            catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
            }
        }

        public void setEdit(boolean value){
            try {
                if(target_item instanceof MyMarker)
                {
                    MyMarker marker0 = (MyMarker) target_item; //the marker on which you click to open the bubble
                    marker0.setEdit(MainActivity.ctx, this.kmlPlacemark, value);
                    map.postInvalidate();
                }
                else
                if(target_item instanceof MyPolyline)
                {
                    MyPolyline polyline0 = (MyPolyline) target_item;
                    polyline0.setEdit(MainActivity.ctx, this.kmlPlacemark, value);
                    map.postInvalidate();
                }
                else
                if(target_item instanceof MyPolygon)
                {
                    MyPolygon polygon0 = (MyPolygon) target_item;
                    polygon0.setEdit(MainActivity.ctx, this.kmlPlacemark, value);
                    map.postInvalidate();
                }

                MainActivity.hide_keyboard(null);
                thisInfo.close();
                map.postInvalidate();
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        }
    }

    public static void resetSettings(Context ctx) {
        try
        {
            //delete all preference keys, if you're using this for your own application
            //you may want to consider some additional logic here (only clear osmdroid settings or
            //use something other than the default shared preferences map
//        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
//        edit.clear();
//        edit.apply();
            //this will repopulate the default settings
//        Configuration.setConfigurationProvider(new DefaultConfigurationProvider());
            //this will save the default along with the user agent (important for downloading tiles)
//        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    float prev_angle = -1;

    public void doRotate(float angle,boolean update_edit) {
        try
        {
            MainActivity.map_rot = angle;
//            if (Tab_Map.sw_broadcast_map.isChecked()) {
//                try {
//                    if(Math.abs(angle - prev_angle) > 0.1) {
//                        prev_angle = angle;
//                        Tab_Messenger.sendMessage("ROT:" + angle, true);
//                        //MainActivity.hide_keyboard(null);
//                    }
//                } catch (Throwable ex) {
//                    MainActivity.MyLog(ex);
//                }
//            }

            image_compass.setRotation(angle);
            char cDeg = (char)0x00B0;
//            text_compass.setText(String.format(Locale.ENGLISH, "%d",(Math.round(360+angle)%360)+cDeg));
//            if(update_edit) et_angle.setText(String.format(Locale.ENGLISH, "%d",(Math.round(360+angle)%360)+cDeg));
            text_compass.setText(String.format(Locale.ENGLISH, "%.01f"+cDeg,angle));
            if(update_edit) et_angle.setText(String.format(Locale.ENGLISH, "%.01f",angle));
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    private void setResultToToast(final String string){
        try
        {
            activity.runOnUiThread(() -> {
                try {
                    Toast.makeText(activity, string, Toast.LENGTH_SHORT).show();
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            });
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    private void initUI(View view) {
        try
        {
            Button b_locate = view.findViewById(R.id.b_locate);
            b_locate.setOnClickListener(this);
            if(MainActivity.bNavigation)
                b_locate.setVisibility(View.GONE);
            else
                b_locate.setVisibility(View.VISIBLE);

            Button b_home = view.findViewById(R.id.b_home);
            b_home.setOnClickListener(this);
            if(MainActivity.bNavigation)
                b_home.setVisibility(View.GONE);
            else
                b_home.setVisibility(View.VISIBLE);

            Button b_limits = view.findViewById(R.id.b_limits);
            b_limits.setOnClickListener(this);
            if(MainActivity.isDevelpoment()) {
                if (MainActivity.bNavigation)
                    b_limits.setVisibility(View.GONE);
                else
                    b_limits.setVisibility(View.VISIBLE);
            }else{
                b_limits.setVisibility(View.GONE);
            }

            cb_mission = view.findViewById(R.id.cb_mission);
            cb_mission.setOnClickListener(this);
            if(MainActivity.isDevelpoment()){
                if(!MainActivity.bNavigation)
                    cb_mission.setVisibility(View.VISIBLE);
                else
                    cb_mission.setVisibility(View.GONE);
            }else {
                cb_mission.setVisibility(View.GONE);
            }

            hsv_mission = view.findViewById(R.id.hsv_mission);
            hsv_mission.setVisibility(View.GONE);

            Button b_my_location = view.findViewById(R.id.b_my_location);
            b_my_location.setOnClickListener(this);

            Button b_virtual_gps = view.findViewById(R.id.b_virtual_gps);
            b_virtual_gps.setOnClickListener(this);

            ImageView b_projectile = view.findViewById(R.id.b_projectile);
            b_projectile.setOnClickListener(this);
//            if(MainActivity.bNavigation)
//                b_projectile.setVisibility(View.VISIBLE);
//            else
                b_projectile.setVisibility(View.GONE);

            ImageView b_projectile_chart = view.findViewById(R.id.b_projectile_chart);
            b_projectile_chart.setOnClickListener(this);
//            if(MainActivity.bNavigation)
//                b_projectile_chart.setVisibility(View.VISIBLE);
//            else
                b_projectile_chart.setVisibility(View.GONE);

            ImageView iv_insert_pos = view.findViewById(R.id.iv_insert_pos);
            iv_insert_pos.setOnClickListener(this);

            b_open_gps_settings = view.findViewById(R.id.b_open_gps_settings);
            b_open_gps_settings.setOnClickListener(this);

            sw_broadcast_map = view.findViewById(R.id.sw_broadcast_map);
            if(sw_broadcast_map != null)   sw_broadcast_map.setOnClickListener(this);

            mapButtons = view.findViewById(R.id.mapButtons);
            mapButtons.setVisibility(View.GONE);

            mapEditButtons = view.findViewById(R.id.mapEditButtons);
            mapEditButtons.setVisibility(View.GONE);

            mapFinishMission = view.findViewById(R.id.mapFinishMission);
            mapFinishMission.setVisibility(View.GONE);

            mapRuler = view.findViewById(R.id.mapRuler);
            mapRuler.setVisibility(View.GONE);

            mapCompass = view.findViewById(R.id.mapCompass);
            mapCompass.setVisibility(View.VISIBLE);

            image_compass = view.findViewById(R.id.image_compass);
            image_compass.setOnClickListener(this);

            text_compass = view.findViewById(R.id.text_compass);
            text_compass.setOnClickListener(this);
            if(MApplication.isRealDevice())
                text_compass.setVisibility(View.VISIBLE);
            else
                text_compass.setVisibility(View.GONE);

            et_angle = view.findViewById(R.id.et_angle);
            if(MApplication.isRealDevice())
                et_angle.setVisibility(View.GONE);
            else
                et_angle.setVisibility(View.VISIBLE);
            et_angle.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        try {
                            MainActivity.map_rot = Double.parseDouble(et_angle.getText().toString());
                            map.setMapOrientation((float)MainActivity.map_rot);
                            doRotate((float)MainActivity.map_rot, true);
                            MainActivity.hide_keyboard(null);
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                        return true;
                    }
                    return false;
                }
            });

            et_icons_scale = view.findViewById(R.id.et_icons_scale);
            et_icons_scale.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        try {
                            Tab_Map.map_icon_scale = Float.parseFloat(et_icons_scale.getText().toString());
                            map.postInvalidate();
                            SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putFloat("map_icon_scale", Tab_Map.map_icon_scale);
                            editor.apply();
                            MainActivity.hide_keyboard(null);
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                        return true;
                    }
                    return false;
                }
            });

            et_target_radius = view.findViewById(R.id.et_target_radius);
            et_target_radius.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        try {
                            Tab_Map.map_target_radius = Float.parseFloat(et_target_radius.getText().toString());
                            map.postInvalidate();
                            SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putFloat("map_target_radius", Tab_Map.map_target_radius);
                            editor.apply();
                            MainActivity.hide_keyboard(null);
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                        return true;
                    }
                    return false;
                }
            });

            et_image_transparency = view.findViewById(R.id.et_image_transparency);
            et_image_transparency.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        try {
                            Tab_Map.map_image_transparency = Float.parseFloat(et_image_transparency.getText().toString());
                            groundOverlay.setTransparency(map_image_transparency);
                            Tab_Map.map.postInvalidate();
                            SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putFloat("map_image_transparency", Tab_Map.map_image_transparency);
                            editor.apply();
                            MainActivity.hide_keyboard(null);
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                        return true;
                    }
                    return false;
                }
            });

            ll_image_transparency = view.findViewById(R.id.ll_image_transparency);
            if(MainActivity.isDevelpoment())
                ll_image_transparency.setVisibility(View.VISIBLE);
            else
                ll_image_transparency.setVisibility(View.GONE);

            tv_measurements2 = view.findViewById(R.id.tv_measurements2);
            tv_measurements2.setOnClickListener(this);
            tv_measurements2.setClickable(false);

            tv_measurements3 = view.findViewById(R.id.tv_measurements3);
            tv_measurements3.setOnClickListener(this);
            tv_measurements3.setClickable(false);

            b_enter = view.findViewById(R.id.b_enter);
            b_enter.setOnClickListener(this);

            b_finish = view.findViewById(R.id.b_finish);
            b_finish.setOnClickListener(this);
            b_finish.setVisibility(View.GONE);

            tv_geom_info = view.findViewById(R.id.tv_geom_info);

            b_edit_mode = view.findViewById(R.id.b_edit_mode);
            b_edit_mode.setOnClickListener(this);

            b_edit_finish = view.findViewById(R.id.b_edit_finish);
            b_edit_finish.setOnClickListener(this);

            b_finish_mission = view.findViewById(R.id.b_finish_mission);
            b_finish_mission.setOnClickListener(this);

            b_toggle_path_direction = view.findViewById(R.id.b_toggle_path_direction);
            b_toggle_path_direction.setOnClickListener(this);

            b_snapshot = view.findViewById(R.id.b_snapshot);
            b_snapshot.setOnClickListener(this);

            b_save = view.findViewById(R.id.b_save);
            b_save.setOnClickListener(this);

            b_test = view.findViewById(R.id.b_test);
            b_test.setOnClickListener(this);

            tv_add_start = view.findViewById(R.id.tv_add_start);
            tv_add_start.setOnClickListener(this);

            tv_add_end = view.findViewById(R.id.tv_add_end);
            tv_add_end.setOnClickListener(this);

            tv_update_ruler = view.findViewById(R.id.tv_update_ruler);
            tv_update_ruler.setOnClickListener(this);

            b_timeline_init = view.findViewById(R.id.b_timeline_init);
            b_timeline_init.setOnClickListener(this);

            b_timeline_simulate = view.findViewById(R.id.b_timeline_simulate);
            b_timeline_simulate.setOnClickListener(this);

            b_timeline_start = view.findViewById(R.id.b_timeline_start);
            b_timeline_start.setOnClickListener(this);

            b_timeline_stop = view.findViewById(R.id.b_timeline_stop);
            b_timeline_stop.setOnClickListener(this);

            b_timeline_pause = view.findViewById(R.id.b_timeline_pause);
            b_timeline_pause.setOnClickListener(this);

            b_timeline_resume = view.findViewById(R.id.b_timeline_resume);
            b_timeline_resume.setOnClickListener(this);

            cb_sensors = view.findViewById(R.id.cb_sensors);
            cb_sensors.setOnClickListener(this);
            if(MainActivity.isDevelpoment()) {
//            if(!MainActivity.bNavigation)
                cb_sensors.setVisibility(View.VISIBLE);
//            else
//                cb_sensors.setVisibility(View.GONE);
            }else{
                cb_sensors.setVisibility(View.GONE);
            }

            hsv_sensors = view.findViewById(R.id.hsv_sensors);
            hsv_sensors.setVisibility(View.GONE);

            mission_progress = view.findViewById(R.id.mission_progress);
            mission_progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mission_progress.setText("");
                }
            });
            if(MainActivity.isDevelpoment()) {
                if (!MainActivity.bNavigation)
                    mission_progress.setVisibility(View.VISIBLE);
                else
                    mission_progress.setVisibility(View.GONE);
            }else{
                mission_progress.setVisibility(View.GONE);
            }

            b_fake_uav_reset = view.findViewById(R.id.b_fake_uav_reset);
            b_fake_uav_reset.setOnClickListener(this);

            b_fake_gimbal_reset = view.findViewById(R.id.b_fake_gimbal_reset);
            b_fake_gimbal_reset.setOnClickListener(this);

            // UAV yaw
            tv_uav_yaw = view.findViewById(R.id.tv_uav_yaw);
            sb_uav_yaw = view.findViewById(R.id.sb_uav_yaw);
            sb_uav_yaw.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChangedValue = 0;

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChangedValue = progress;
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    MainActivity.uav_yaw = progressChangedValue - 180;
                    // bug fixed at 19/5/2024
//                    MainActivity.image_yaw_enc = (float)MainActivity.db_deg(MainActivity.uav_yaw + MainActivity.gimb_yaw);
                    MainActivity.image_yaw_enc = MainActivity.gimb_yaw;
                    MainActivity.image_yaw = (float)MainActivity.db_deg(MainActivity.image_yaw_enc + MainActivity.dYaw);

                    tv_uav_yaw.setText("yaw: "+ MainActivity.uav_yaw);
                    set_uav_yaw(MainActivity.uav_yaw,true);
                }
            });

            // UAV pitch
            tv_uav_pitch = view.findViewById(R.id.tv_uav_pitch);
            sb_uav_pitch = view.findViewById(R.id.sb_uav_pitch);
            sb_uav_pitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChangedValue = 0;

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChangedValue = progress;
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    MainActivity.uav_pitch = progressChangedValue - 90;
                    // bug fixed at 19/5/2024
//                    MainActivity.image_pitch_enc = MainActivity.uav_pitch + MainActivity.gimb_pitch;
                    MainActivity.image_pitch_enc = MainActivity.gimb_pitch;
                    MainActivity.image_pitch = MainActivity.image_pitch_enc + MainActivity.dPitch;

                    tv_uav_pitch.setText("pitch: "+ MainActivity.uav_pitch);
                }
            });

            // UAV roll
            tv_uav_roll = view.findViewById(R.id.tv_uav_roll);
            sb_uav_roll = view.findViewById(R.id.sb_uav_roll);
            sb_uav_roll.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChangedValue = 0;

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChangedValue = progress;
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    MainActivity.uav_roll = progressChangedValue - 90;
                    // bug fixed at 19/5/2024
//                    MainActivity.image_roll_enc = MainActivity.uav_roll + MainActivity.gimb_roll;
                    MainActivity.image_roll_enc = MainActivity.gimb_roll;
                    MainActivity.image_roll = MainActivity.image_roll_enc + MainActivity.dRoll;

                    tv_uav_roll.setText("roll: "+ MainActivity.uav_roll);
                }
            });

            // UAV alt
            tv_uav_alt = view.findViewById(R.id.tv_uav_alt);
            sb_uav_alt = view.findViewById(R.id.sb_uav_alt);
            sb_uav_alt.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChangedValue = 0;

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChangedValue = progress;
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    MainActivity.uav_alt_above_ground = progressChangedValue;
                    MainActivity.uav_ground_alt = MainActivity.GetHeightJNI(MainActivity.uav_lon,MainActivity.uav_lat);
                    MainActivity.uav_alt = MainActivity.uav_ground_alt + MainActivity.uav_alt_above_ground;

                    tv_uav_alt.setText("alt: "+ MainActivity.uav_alt_above_ground);
                }
            });

            // Gimbal yaw
            tv_gimb_yaw = view.findViewById(R.id.tv_gimb_yaw);
            sb_gimb_yaw = view.findViewById(R.id.sb_gimb_yaw);
            sb_gimb_yaw.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChangedValue = 0;

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChangedValue = progress;
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    MainActivity.gimb_yaw = progressChangedValue - 180;
                    // bug fixed at 19/5/2024
//                    MainActivity.image_yaw_enc = (float)MainActivity.db_deg(MainActivity.uav_yaw + MainActivity.gimb_yaw);
                    MainActivity.image_yaw_enc = MainActivity.gimb_yaw;
                    MainActivity.image_yaw = (float)MainActivity.db_deg(MainActivity.image_yaw_enc + MainActivity.dYaw);

                    tv_gimb_yaw.setText("yaw: "+ MainActivity.gimb_yaw);
                    set_camera_azi(MainActivity.image_yaw,true);
                }
            });

            // Gimbal pitch
            tv_gimb_pitch = view.findViewById(R.id.tv_gimb_pitch);
            sb_gimb_pitch = view.findViewById(R.id.sb_gimb_pitch);
            sb_gimb_pitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChangedValue = 0;

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChangedValue = progress;
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    MainActivity.gimb_pitch = progressChangedValue - 90;
                    // bug fixed at 19/5/2024
//                    MainActivity.image_pitch_enc = MainActivity.uav_pitch + MainActivity.gimb_pitch;
                    MainActivity.image_pitch_enc = MainActivity.gimb_pitch;
                    MainActivity.image_pitch = MainActivity.image_pitch_enc + MainActivity.dPitch;

                    tv_gimb_pitch.setText("pitch: "+ MainActivity.gimb_pitch);
                }
            });

            // Gimbal roll
            tv_gimb_roll = view.findViewById(R.id.tv_gimb_roll);
            sb_gimb_roll = view.findViewById(R.id.sb_gimb_roll);
            sb_gimb_roll.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChangedValue = 0;

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChangedValue = progress;
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    MainActivity.gimb_roll = progressChangedValue - 90;
                    // bug fixed at 19/5/2024
//                    MainActivity.image_roll_enc = MainActivity.uav_roll + MainActivity.gimb_roll;
                    MainActivity.image_roll_enc = MainActivity.gimb_roll;
                    MainActivity.image_roll = MainActivity.image_roll_enc + MainActivity.dRoll;

                    tv_gimb_roll.setText("roll: "+ MainActivity.gimb_roll);
                }
            });

            // GPS yaw
            tv_gps_yaw = view.findViewById(R.id.tv_gps_yaw);
            sb_gps_yaw = view.findViewById(R.id.sb_gps_yaw);
            sb_gps_yaw.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChangedValue = 0;

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChangedValue = progress;
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
//                    MainActivity.uav_yaw = progressChangedValue - 180;
//                    MainActivity.image_yaw_enc = (float)MainActivity.db_deg(MainActivity.uav_yaw + MainActivity.gimb_yaw);
//                    MainActivity.image_yaw = (float)MainActivity.db_deg(MainActivity.image_yaw_enc + MainActivity.dYaw);
//
                    tv_gps_yaw.setText("yaw: "+Float.toString(progressChangedValue));
//                    set_uav_yaw(MainActivity.uav_yaw,true);

                    IGeoPoint mapCenter = map.getMapCenter();
                    double fLon,fLat;
                    fLon = mapCenter.getLongitude();
                    fLat = mapCenter.getLatitude();
                    float fAlt = MainActivity.GetHeightJNI(fLon,fLat);

                    float bearing = sb_gps_yaw.getProgress();
                    float speed = sb_gps_speed.getProgress();
                    float alt = sb_gps_alt.getProgress();

                    Location location = SerializableLocation.getLocation(fLon,fLat,fAlt + alt,speed,bearing);
                    GpsmvLocationProvider locationProvider = (GpsmvLocationProvider) mLocationOverlay.getMyLocationProvider();
                    if(locationProvider != null){
                        Bundle extraBundle = new Bundle();
                        extraBundle.putBoolean("isMock",true);
                        extraBundle.putBoolean("isKalman",false);
                        extraBundle.putBoolean("isVirtual",true);
                        location.setExtras(extraBundle);

                        locationProvider.onLocationChanged(location);
                    }
                }
            });

            // GPS speed
            tv_gps_speed = view.findViewById(R.id.tv_gps_speed);
            sb_gps_speed = view.findViewById(R.id.sb_gps_speed);
            sb_gps_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChangedValue = 0;

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChangedValue = progress;
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
//                    MainActivity.uav_yaw = progressChangedValue - 180;
//                    MainActivity.image_yaw_enc = (float)MainActivity.db_deg(MainActivity.uav_yaw + MainActivity.gimb_yaw);
//                    MainActivity.image_yaw = (float)MainActivity.db_deg(MainActivity.image_yaw_enc + MainActivity.dYaw);
//
                    tv_gps_speed.setText("speed: "+Float.toString(progressChangedValue));
//                    set_uav_yaw(MainActivity.uav_yaw,true);

                    IGeoPoint mapCenter = map.getMapCenter();
                    double fLon,fLat;
                    fLon = mapCenter.getLongitude();
                    fLat = mapCenter.getLatitude();
                    float fAlt = MainActivity.GetHeightJNI(fLon,fLat);

                    float bearing = sb_gps_yaw.getProgress();
                    float speed = sb_gps_speed.getProgress();
                    float alt = sb_gps_alt.getProgress();

                    Location location = SerializableLocation.getLocation(fLon,fLat,fAlt + alt,speed,bearing);
                    GpsmvLocationProvider locationProvider = (GpsmvLocationProvider) mLocationOverlay.getMyLocationProvider();
                    if(locationProvider != null) {
                        Bundle extraBundle = new Bundle();
                        extraBundle.putBoolean("isMock",true);
                        extraBundle.putBoolean("isKalman",false);
                        extraBundle.putBoolean("isVirtual",true);
                        location.setExtras(extraBundle);

                        locationProvider.onLocationChanged(location);
                    }
                }
            });

            // GPS alt
            tv_gps_alt = view.findViewById(R.id.tv_gps_alt);
            sb_gps_alt = view.findViewById(R.id.sb_gps_alt);
            sb_gps_alt.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChangedValue = 0;

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChangedValue = progress;
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
//                    MainActivity.uav_yaw = progressChangedValue - 180;
//                    MainActivity.image_yaw_enc = (float)MainActivity.db_deg(MainActivity.uav_yaw + MainActivity.gimb_yaw);
//                    MainActivity.image_yaw = (float)MainActivity.db_deg(MainActivity.image_yaw_enc + MainActivity.dYaw);
//
                    tv_gps_alt.setText("alt: "+Float.toString(progressChangedValue));
//                    set_uav_yaw(MainActivity.uav_yaw,true);

                    IGeoPoint mapCenter = map.getMapCenter();
                    double fLon,fLat;
                    fLon = mapCenter.getLongitude();
                    fLat = mapCenter.getLatitude();
                    float fAlt = MainActivity.GetHeightJNI(fLon,fLat);

                    float bearing = sb_gps_yaw.getProgress();
                    float speed = sb_gps_speed.getProgress();
                    float alt = sb_gps_alt.getProgress();

                    Location location = SerializableLocation.getLocation(fLon,fLat,fAlt + alt,speed,bearing);
                    GpsmvLocationProvider locationProvider = (GpsmvLocationProvider) mLocationOverlay.getMyLocationProvider();
                    if(locationProvider != null) {
                        Bundle extraBundle = new Bundle();
                        extraBundle.putBoolean("isMock",true);
                        extraBundle.putBoolean("isKalman",false);
                        extraBundle.putBoolean("isVirtual",true);
                        location.setExtras(extraBundle);

                        locationProvider.onLocationChanged(location);
                    }
                }
            });
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    //Add Listener for WaypointMissionOperator
    private void addListener() {
        try
        {
            if(MApplication.isRealDevice()){
                if(eventNotificationListener == null) {
                    eventNotificationListener = new WaypointMissionOperatorListener() {
                        @Override
                        public void onDownloadUpdate(@NonNull WaypointMissionDownloadEvent downloadEvent) {
                            try {
                                final WaypointMissionDownloadEvent downloadEvent0 = downloadEvent;
                                activity.runOnUiThread(() -> {
                                    try {
                                        WaypointDownloadProgress download_progress = downloadEvent0.getProgress();
                                        if (download_progress != null) {
                                            int idx = download_progress.downloadedWaypointIndex;
                                            int count = download_progress.totalWaypointCount;

                                            String strText = Tab_Map.start_idx + idx + 1 + "/" + (Tab_Map.start_idx + count);// Downloading
                                            MainActivity.MyLogInfo(strText);
                                            mission_progress.setText(strText);
                                            MainActivity.tab_camera.tv_mission_progress.setText(strText);
                                        }
                                    } catch (Throwable ex) {
                                        MainActivity.MyLog(ex);
                                    }
                                });
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        }

                        @Override
                        public void onUploadUpdate(@NonNull WaypointMissionUploadEvent uploadEvent) {
                            try {
                                final WaypointMissionUploadEvent uploadEvent0 = uploadEvent;
                                activity.runOnUiThread(() -> {
                                    try {
                                        WaypointUploadProgress upload_progress = uploadEvent0.getProgress();
                                        if (upload_progress != null) {
                                            int idx = upload_progress.uploadedWaypointIndex;
                                            int count = upload_progress.totalWaypointCount;

//                                WaypointMissionState current_state = uploadEvent0.getCurrentState();
//                                WaypointMissionState previous_state = uploadEvent0.getPreviousState();

                                            String strText = Tab_Map.start_idx + idx + 1 + "/" + (Tab_Map.start_idx + count);// Uploading;
                                            MainActivity.MyLogInfo(strText);
                                            mission_progress.setText(strText);
                                            MainActivity.tab_camera.tv_mission_progress.setText(strText);
                                        }
                                    } catch (Throwable ex) {
                                        MainActivity.MyLog(ex);
                                    }
                                });
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        }

                        @Override
                        public void onExecutionUpdate(@NonNull WaypointMissionExecutionEvent executionEvent) {
                            try {
                                final WaypointMissionExecutionEvent executionEvent0 = executionEvent;
                                activity.runOnUiThread(() -> {
                                    try {
                                        WaypointExecutionProgress execution_progress = executionEvent0.getProgress();
                                        if (execution_progress != null) {
                                            int idx = execution_progress.targetWaypointIndex;
                                            int count = execution_progress.totalWaypointCount;

//                                WaypointMissionExecuteState execution_state = execution_progress.executeState;
//                                WaypointMissionState current_state = executionEvent0.getCurrentState();
//                                WaypointMissionState previous_state = executionEvent0.getPreviousState();

                                            String strText = Tab_Map.start_idx + idx + 1 + "/" + (Tab_Map.start_idx + count);// Executing
                                            MainActivity.MyLogInfo(strText);
                                            mission_progress.setText(strText);
                                            MainActivity.tab_camera.tv_mission_progress.setText(strText);
                                        }
                                    } catch (Throwable ex) {
                                        MainActivity.MyLog(ex);
                                    }
                                });
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        }

                        @Override
                        public void onExecutionStart() {
                            try {
                                setResultToToast("Execution started: ");
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        }

                        @Override
                        public void onExecutionFinish(@Nullable final DJIError error) {
                            try {
                                setResultToToast("Execution finished: " + (error == null ? "Success!" : error.getDescription()));
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        }
                    };

                    if (getWaypointMissionOperator() != null){
                        getWaypointMissionOperator().addListener(eventNotificationListener);
                    }
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    private void removeListener() {
        try
        {
            if(MApplication.isRealDevice()) {
                if (getWaypointMissionOperator() != null) {
                    getWaypointMissionOperator().removeListener(eventNotificationListener);
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }
    private WaypointMissionOperatorListener eventNotificationListener = null;
    public WaypointMissionOperator getWaypointMissionOperator() {
        try
        {
            if(MApplication.isRealDevice()) {
                if (instance == null) {
                    if (DJISDKManager.getInstance().getMissionControl() != null) {
                        instance = DJISDKManager.getInstance().getMissionControl().getWaypointMissionOperator();
                    }
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return instance;
    }
    public static final DecimalFormat df = new DecimalFormat("#.000000");
    public static final DecimalFormat zf = new DecimalFormat("#.0");
    public void updateInfo(boolean bUpdate){
        try
        {
            if(cross_overlay == null)   return;
            String strText;
            IGeoPoint mapCenter = map.getMapCenter();
            double fLon,fLat;
            fLon = mapCenter.getLongitude();
            fLat = mapCenter.getLatitude();
            float fAlt = MainActivity.GetHeightJNI(fLon,fLat);
            position.setLongitude(fLon);
            position.setLatitude(fLat);
            position.setAltitude(fAlt);
//            strText = "lon:"+df.format(fLon)+",lat:"+df.format(fLat)+",alt:"+zf.format(fAlt)+",zoom:"+zf.format(map.getZoomLevelDouble());
//            strText = "lon:"+String.format(Locale.ENGLISH, "%.06f", fLon)+",lat:"+String.format(Locale.ENGLISH, "%.06f", fLat)+",alt:"+String.format(Locale.ENGLISH, "%.01f", fAlt)+",zoom:"+String.format(Locale.ENGLISH, "%.01f", map.getZoomLevelDouble());
//            float alt = getAltitude((float)map.getZoomLevelDouble());
//            strText = " lon:"+String.format(Locale.ENGLISH, "%.06f", fLon)+",lat:"+String.format(Locale.ENGLISH, "%.06f", fLat)+",alt:"+String.format(Locale.ENGLISH, "%.01f", fAlt)+",zoom:"+String.format(Locale.ENGLISH, "%.01f", map.getZoomLevelDouble())+",height:"+String.format(Locale.ENGLISH, "%.01f", alt);
//            strText = " lon:"+String.format(Locale.ENGLISH, "%.06f", fLon)+",lat:"+String.format(Locale.ENGLISH, "%.06f", fLat)+",alt:"+String.format(Locale.ENGLISH, "%.01f", fAlt)+",zoom:"+String.format(Locale.ENGLISH, "%.01f", map.getZoomLevelDouble());
//            strText = " lon:"+String.format(Locale.ENGLISH, "%.06f", fLon)+",lat:"+String.format(Locale.ENGLISH, "%.06f", fLat)+",alt:"+String.format(Locale.ENGLISH, "%.01f", fAlt);
//            strText = " N:"+String.format(Locale.ENGLISH, "%.06f", fLat)+",E:"+String.format(Locale.ENGLISH, "%.06f", fLon)+",A:"+String.format(Locale.ENGLISH, "%.01f", fAlt);
//            strText = " "+MainActivity.CoordinatesToDMS(fLon,fLat,true)+",A:"+String.format(Locale.ENGLISH, "%d", Math.round(fAlt));
            strText = " "+Tab_Map.convert_coordinates(fLon,fLat,Tab_Map.map_coordinate_index,true,true)+",A:"+String.format(Locale.ENGLISH, "%d", Math.round(fAlt));
            if(cb_ruler.isChecked())
            {
//                ruler_overlay.setPos2(position);
//                strText += ","+ruler_overlay.getText();
//                et_ruler_distance.setText(ruler_overlay.getDistance());
//                et_ruler_azimuth.setText(ruler_overlay.getAzimuth());
//                tv_measurements2.setText(ruler_overlay.getText2());
//                tv_measurements3.setText(ruler_overlay.getText3());

                boolean bProfile = true;
                double lon1,lat1,alt1,lon2,lat2,alt2;
                int count = 50;
                GeoPoint p1 = ruler_overlay.getPos1();
                GeoPoint p2 = ruler_overlay.getPos2();
                lon1 = p1.getLongitude();
                lat1 = p1.getLatitude();
                alt1 = p1.getAltitude();
                lon2 = p2.getLongitude();
                lat2 = p2.getLatitude();
                alt2 = p2.getAltitude();
                MainActivity.is_strait_line2(true,lon1,lat1,alt1,lon2,lat2,alt2,count);

                float X1,X2,Y1,Y2;
                float[] Ts = MainActivity.GetTs();
                float[] Hs = MainActivity.GetHs();
                X1 = 10;
                X2 = 400;
                Y1 = 200;
                Y2 = 10;
                cross_overlay.updatePath(Ts,Hs,X1,X2,Y1,Y2);
                cross_overlay.setShowPath(true);
            }
            else
            {
                ruler_overlay.setPos1(position);
                ruler_overlay.setPos2(position);
                cross_overlay.setShowPath(false);
            }
            strText += ",Z:"+String.format(Locale.ENGLISH, "%.01f", map.getZoomLevelDouble());
            cross_overlay.setText(strText);
            if(bUpdate) map.postInvalidate();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    /** @return the icon, scaled and blended with the icon color, as specified in the IconStyle.
     * Assumes the icon is already loaded. */
    static public BitmapDrawable getFinalIcon(Context context, IconStyle style){
        if (style.mIcon == null)
            return null;
        int sizeX = Math.round(style.mIcon.getWidth() * style.mScale);
        int sizeY = Math.round(style.mIcon.getHeight() * style.mScale);
        if (sizeX == 0 || sizeY == 0) {
            Log.w(BonusPackHelper.LOG_TAG, "KML icon has size=0");
            return null;
        }
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(style.mIcon, sizeX, sizeY, true);
        BitmapDrawable finalIcon = new BitmapDrawable(context.getResources(), scaledBitmap);
        int color = style.getFinalColor();
        if (color != 0) //there is a real color to blend with:
            finalIcon.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        return finalIcon;
    }

    static public void update_placemark_style(Overlay marker, KmlPlacemark kmlPlacemark, String icon_name,int icon_color, float icon_scale, float icon_heading){
        String filename = "ylw-pushpin.png";
        int iconColor = Color.WHITE;
        float iconScale = 1.0f;
        float iconHeading = 0.0f;
        if(icon_name != null) {
            filename = icon_name;
            iconColor = icon_color;
            iconScale = icon_scale;
            iconHeading = icon_heading;
        }
        Style style;
        if(kmlPlacemark.mStyle != null) {
            style = kmlFavoritesDocument.getStyle(kmlPlacemark.mStyle);
            if(style != null){
                if(style.mIconStyle != null){
                    if(style.mIconStyle.mHref != null){
                        if(icon_name != null){
                            style.mIconStyle.mHref = icon_name;
                            style.mIconStyle.mColor = icon_color;
                            style.mIconStyle.mScale = icon_scale;
                            style.mIconStyle.mHeading = icon_heading;
                        }
                        File file = new File(MainActivity.strIconsPath+style.mIconStyle.mHref);
                        style.mIconStyle.mIcon = BitmapFactory.decodeFile(file.getAbsolutePath());
                        style.mIconStyle.mColorMode = 0;
                        style.mIconStyle.mHotSpot.mx = 0.5f;
                        style.mIconStyle.mHotSpot.my = 0.0f;
                        style.mIconStyle.mHotSpot.mXUnits = HotSpot.Units.fraction;
                        style.mIconStyle.mHotSpot.mYUnits = HotSpot.Units.fraction;

                        kmlFavoritesDocument.putStyle(kmlPlacemark.mStyle, style);
                    }else{
                        File file = new File(MainActivity.strIconsPath+filename);
                        Bitmap icon_bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                        style = new Style(icon_bmp, 0x901010AA, 3.0f, 0x2010AA10);
                        style.mIconStyle.mIcon = icon_bmp;
                        style.mIconStyle.mHref = filename;
                        style.mIconStyle.mHeading = iconHeading;
                        style.mIconStyle.mScale = iconScale;
                        style.mIconStyle.mColor = iconColor;
                        style.mIconStyle.mColorMode = 0;
                        style.mIconStyle.mHotSpot.mx = 0.5f;
                        style.mIconStyle.mHotSpot.my = 0.0f;
                        style.mIconStyle.mHotSpot.mXUnits = HotSpot.Units.fraction;
                        style.mIconStyle.mHotSpot.mYUnits = HotSpot.Units.fraction;

                        kmlPlacemark.mStyle = kmlFavoritesDocument.addStyle(style);
                    }
                }else{
                    File file = new File(MainActivity.strIconsPath+filename);
                    Bitmap icon_bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                    style = new Style(icon_bmp, 0x901010AA, 3.0f, 0x2010AA10);
                    style.mIconStyle.mIcon = icon_bmp;
                    style.mIconStyle.mHref = filename;
                    style.mIconStyle.mHeading = iconHeading;
                    style.mIconStyle.mScale = iconScale;
                    style.mIconStyle.mColor = iconColor;
                    style.mIconStyle.mColorMode = 0;
                    style.mIconStyle.mHotSpot.mx = 0.5f;
                    style.mIconStyle.mHotSpot.my = 0.0f;
                    style.mIconStyle.mHotSpot.mXUnits = HotSpot.Units.fraction;
                    style.mIconStyle.mHotSpot.mYUnits = HotSpot.Units.fraction;

                    kmlPlacemark.mStyle = kmlFavoritesDocument.addStyle(style);
                }
            }else{
                File file = new File(MainActivity.strIconsPath+filename);
                Bitmap icon_bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                style = new Style(icon_bmp, 0x901010AA, 3.0f, 0x2010AA10);
                style.mIconStyle.mIcon = icon_bmp;
                style.mIconStyle.mHref = filename;
                style.mIconStyle.mHeading = iconHeading;
                style.mIconStyle.mScale = iconScale;
                style.mIconStyle.mColor = iconColor;
                style.mIconStyle.mColorMode = 0;
                style.mIconStyle.mHotSpot.mx = 0.5f;
                style.mIconStyle.mHotSpot.my = 0.0f;
                style.mIconStyle.mHotSpot.mXUnits = HotSpot.Units.fraction;
                style.mIconStyle.mHotSpot.mYUnits = HotSpot.Units.fraction;

                kmlPlacemark.mStyle = kmlFavoritesDocument.addStyle(style);
            }
        }else{
            File file = new File(MainActivity.strIconsPath+filename);
            Bitmap icon_bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            style = new Style(icon_bmp, 0x901010AA, 3.0f, 0x2010AA10);
            style.mIconStyle.mIcon = icon_bmp;
            style.mIconStyle.mHref = filename;
            style.mIconStyle.mHeading = iconHeading;
            style.mIconStyle.mScale = iconScale;
            style.mIconStyle.mColor = iconColor;
            style.mIconStyle.mColorMode = 0;
            style.mIconStyle.mHotSpot.mx = 0.5f;
            style.mIconStyle.mHotSpot.my = 0.0f;
            style.mIconStyle.mHotSpot.mXUnits = HotSpot.Units.fraction;
            style.mIconStyle.mHotSpot.mYUnits = HotSpot.Units.fraction;

            kmlPlacemark.mStyle = kmlFavoritesDocument.addStyle(style);
        }

        if (marker instanceof MyMarker) {
            BitmapDrawable bmp = style.mIconStyle.getFinalIcon(MainActivity.ctx);
            MyMarker m = (MyMarker)marker;
            m.setRotation(style.mIconStyle.mHeading);
            m.setIcon(bmp);
        }
    }

    //13.2 Loading KML content - Advanced styling with Styler
    class MyKmlStyler implements KmlFeature.Styler {
        Style mDefaultStyle;

        MyKmlStyler(Style defaultStyle) {
            mDefaultStyle = defaultStyle;
        }

        @Override
        public void onLineString(MyPolyline polyline, KmlPlacemark kmlPlacemark, KmlLineString kmlLineString) {
//            String style = kmlPlacemark.getExtendedData("style");
//            if(style != null) {
//                File file = new File(MainActivity.strIconsPath+style);
//                String fname = file.getName();
//                Bitmap icon_bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
//                Style st = new Style(icon_bmp, 0x901010AA, 3.0f, 0x2010AA10);
//                kmlFavoritesDocument.putStyle(fname, st);
//                kmlPlacemark.mStyle = style;
//            }

            kmlLineString.applyDefaultStyling(polyline, mDefaultStyle, kmlPlacemark, kmlFavoritesDocument, map);

            //Custom styling:
//            polyline.getOutlinePaint().setColor(Color.GREEN);
//            polyline.getOutlinePaint().setStrokeWidth(Math.max(kmlLineString.mCoordinates.size() / 200.0f, 3.0f));

//            polyline.setColor(Color.GREEN);
//            polyline.setWidth(Math.max(kmlLineString.mCoordinates.size() / 200.0f, 3.0f));
//            polyline.setInfoWindow(new CustomInfoWindow(0, map, null, null, null));
//            polyline.setInfoWindow(new MarkerInfoWindow(R.layout.bonuspack_bubble, map));

//            String style = kmlPlacemark.getExtendedData("style");
//            if(style != null) {
//                File file = new File(MainActivity.strIconsPath+style);
//                String fname = file.getName();
//                Bitmap icon_bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
//                Style st = new Style(icon_bmp, 0x901010AA, 3.0f, 0x2010AA10);
//                kmlFavoritesDocument.putStyle(fname, st);
//                kmlPlacemark.mStyle = style;
//            }
//            kmlLineString.applyDefaultStyling(polyline, mDefaultStyle, kmlPlacemark, kmlFavoritesDocument, map);

            int typ = 0;
            String type = kmlPlacemark.getExtendedData("type");
            if(type != null) {
                typ = mv_utils.parseInt(type);
            }
            polyline.setInfoWindow(new CustomInfoWindow(typ, map, polyline, kmlPlacemark, null));
        }

        @Override
        public void onPolygon(MyPolygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon) {
//            String style = kmlPlacemark.getExtendedData("style");
//            if(style != null) {
//                File file = new File(MainActivity.strIconsPath+style);
//                String fname = file.getName();
//                Bitmap icon_bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
//                Style st = new Style(icon_bmp, 0x901010AA, 3.0f, 0x2010AA10);
//                kmlFavoritesDocument.putStyle(fname, st);
//                kmlPlacemark.mStyle = style;
//            }
//            kmlPolygon.applyDefaultStyling(polygon, mDefaultStyle, kmlPlacemark, kmlFavoritesDocument, map);

//            Drawable defaultMarker = ResourcesCompat.getDrawable(MainActivity.activity.getResources(), R.drawable.marker_icon, null);
//            Bitmap defaultBitmap = null;
//            if (defaultMarker != null) {
//                defaultBitmap = ((BitmapDrawable) defaultMarker).getBitmap();
//            }
//            Style defaultStyle = new Style(defaultBitmap, Color.GREEN, 3.0f, mv_utils.adjustAlpha(Color.YELLOW,64));
//            kmlPolygon.applyDefaultStyling(polygon, defaultStyle, kmlPlacemark, kmlFavoritesDocument, map);

//            Style style = kmlFavoritesDocument.getStyle(kmlPlacemark.mStyle);
//            if (style != null){
//                Paint outlinePaint = style.getOutlinePaint();
//                polygon.getOutlinePaint().setColor(outlinePaint.getColor());
//                polygon.getOutlinePaint().setStrokeWidth(outlinePaint.getStrokeWidth());
//                if (style.mPolyStyle != null){
//                    int fillColor = style.mPolyStyle.getFinalColor();
//                    polygon.getFillPaint().setColor(mv_utils.adjustAlpha(fillColor,64));
//                }
//            }

//            String style = kmlPlacemark.getExtendedData("style");
//            String style = kmlPlacemark.mStyle;
//            if(style != null) {
//                Style st = new Style(null, Color.GREEN, 3.0f, mv_utils.adjustAlpha(Color.YELLOW,64));
//                kmlFavoritesDocument.putStyle(style, st);
////                kmlPlacemark.mStyle = style;
//            }

//            String styleId;
//            if(kmlPlacemark.mStyle != null){
//                styleId = kmlPlacemark.mStyle;
//            }else{
//                if(kmlPlacemark.mId != null){
//                    styleId = kmlPlacemark.mId;
//                }else{
//                    styleId = UUID.randomUUID().toString();
//                }
//            }
//            Style style = new Style(null, finalLine_color, line_width, finalFill_color);
//            Tab_Map.kmlFavoritesDocument.putStyle(styleId, style);
//            kmlPlacemark.mStyle = styleId;

            kmlPolygon.applyDefaultStyling(polygon, mDefaultStyle, kmlPlacemark, kmlFavoritesDocument, map);

            //Keeping default styling:
//            kmlPolygon.applyDefaultStyling(polygon, mDefaultStyle, kmlPlacemark, kmlFavoritesDocument, map);
//            polygon.setInfoWindow(new CustomInfoWindow(0, map, null, null, null));
//            polygon.setInfoWindow(new MarkerInfoWindow(R.layout.bonuspack_bubble, map));

            int typ = 0;
            String type = kmlPlacemark.getExtendedData("type");
            if(type != null) {
                typ = mv_utils.parseInt(type);
            }
            polygon.setInfoWindow(new CustomInfoWindow(typ, map, polygon, kmlPlacemark, null));
        }

        @Override
        public void onTrack(MyPolyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack) {
            //Keeping default styling:
            kmlTrack.applyDefaultStyling(polyline, mDefaultStyle, kmlPlacemark, kmlFavoritesDocument, map);
//            polyline.setInfoWindow(new CustomInfoWindow(0, map, null, null, null));
//            polyline.setInfoWindow(new MarkerInfoWindow(R.layout.bonuspack_bubble, map));

            int typ = 0;
            String type = kmlPlacemark.getExtendedData("type");
            if(type != null) {
                typ = mv_utils.parseInt(type);
            }
            polyline.setInfoWindow(new CustomInfoWindow(typ, map, polyline, kmlPlacemark, null));
        }

        @Override
        public void onPoint(MyMarker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint) {
            update_placemark_style(marker, kmlPlacemark, null,Color.WHITE, 1.0f, 0.0f);
            kmlPoint.applyDefaultStyling(marker, mDefaultStyle, kmlPlacemark, kmlFavoritesDocument, map);

            int typ = 0;
            String type = kmlPlacemark.getExtendedData("type");
            if(type != null) {
                typ = mv_utils.parseInt(type);
            }
            marker.setInfoWindow(new CustomInfoWindow(typ, map, marker, kmlPlacemark, null));
        }

        @Override
        public void onFeature(Overlay overlay, KmlFeature kmlFeature) {
            //If nothing to do, do nothing.
        }
    }

    public void toggle_settings() {
        try
        {
            if(layout_map_toolbar.getVisibility() == View.GONE)
                layout_map_toolbar.setVisibility(View.VISIBLE);
            else
                layout_map_toolbar.setVisibility(View.GONE);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }
    private void load_KMLs() {
        try
        {
            String strKMLPath = MainActivity.GetKMLPathJNI();
            File directory = new File(strKMLPath);
            File[] files = directory.listFiles();
            if(files != null) {
                for (File value : files) {
                    String filename = strKMLPath + "/" + value.getName();
                    File file = new File(filename);
                    if (file.exists()) {
                        String ext = FileHelper.fileExt(filename);
                        if(ext != null) {
                            if (ext.contains("kml")) {
                                KmlDocument kml = new KmlDocument();
                                kml.parseKMLFile(file);
                                FolderOverlay overlay = (FolderOverlay) kml.mKmlRoot.buildOverlay(map, defaultStyle, styler, kml);
                                map.getOverlays().add(overlay);
                            }else
                            if (ext.contains("kmz")) {
                                KmlDocument kmz = new KmlDocument();
                                kmz.parseKMZFile(file);
                                FolderOverlay overlay = (FolderOverlay) kmz.mKmlRoot.buildOverlay(map, defaultStyle, styler, kmz);
                                map.getOverlays().add(overlay);
                            }else
                            if (ext.contains("geojson")) {
                                KmlDocument geojson = new KmlDocument();
                                geojson.parseGeoJSON(file);
                                FolderOverlay overlay = (FolderOverlay) geojson.mKmlRoot.buildOverlay(map, defaultStyle, styler, geojson);
                                map.getOverlays().add(overlay);
                            }
                        }
                    }
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

//    java.util.HashMap<Integer,String> HashMap = new HashMap<Integer,String>();
//    public FolderOverlay mvBuildOverlay(MapView map, Style defaultStyle, KmlFeature.Styler styler, KmlDocument kmlDocument){
//        KmlFolder kmlFolder = kmlDocument.mKmlRoot;
//        FolderOverlay folderOverlay = new FolderOverlay();
//        folderOverlay.setName(kmlFolder.mName);
//        folderOverlay.setDescription(kmlFolder.mDescription);
//        for (KmlFeature k:kmlFolder.mItems){
//            Overlay overlay = k.buildOverlay(map, defaultStyle, styler, kmlDocument);
//            if (overlay != null) {
//                folderOverlay.add(overlay);
//            }
//        }
//        if (styler == null)
//            folderOverlay.setEnabled(kmlFolder.mVisibility);
//        else
//            styler.onFeature(folderOverlay, kmlFolder);
//        return folderOverlay;
//    }

    public RadiusMarkerClusterer mvBuildOverlay(MapView map, Style defaultStyle, KmlFeature.Styler styler, KmlDocument kmlDocument){
        KmlFolder kmlFolder = kmlDocument.mKmlRoot;
        RadiusMarkerClusterer folderOverlay = new RadiusMarkerClusterer(ctx);
        folderOverlay.setName(kmlFolder.mName);
        folderOverlay.setDescription(kmlFolder.mDescription);
        Tab_Map.favorites_adapter.clear();
        int idx = -1;
        for (KmlFeature k:kmlFolder.mItems){
            idx++;
            KmlPlacemark placemark = null;
            if(k instanceof KmlPlacemark) {
                placemark = (KmlPlacemark) k;
            }
            if(placemark == null)   continue;
            int geometry_type;
            if(placemark.mGeometry instanceof KmlPoint) {
                geometry_type = City.POINT;
            }
            else
            if(placemark.mGeometry instanceof KmlLineString) {
                geometry_type = City.POLYLINE;
            }
            else {
                geometry_type = City.POLYGON;
            }

            placemark.overlay = placemark.buildOverlay(map, defaultStyle, styler, kmlDocument);
            if (placemark.overlay != null) {
                folderOverlay.add(placemark.overlay);

                GeoPoint p;
                int count = placemark.mGeometry.mCoordinates.size();
                if(count > 0)
                    p = placemark.mGeometry.mCoordinates.get(0);
                else
                    p = new GeoPoint(0.0,0.0);

                double fLon,fLat;
                float fAlt;
                fLon = p.getLongitude();
                fLat = p.getLatitude();
                fAlt = MainActivity.GetHeightJNI(fLon, fLat);

                // Add item to adapter
                City city = new City();
                city.strName = placemark.mName;
                city.fLon = fLon;
                city.fLat = fLat;
                city.fAlt = fAlt;
                city.geometry_type = geometry_type;
                city.index = idx;
                city.placemark = placemark;
                Tab_Map.favorites_adapter.add(city);

                int typ = 0;
                String type = placemark.getExtendedData("type");
                if(type != null) {
                    typ = mv_utils.parseInt(type);
                }
                if(placemark.overlay instanceof MyMarker){
                    MyMarker marker = (MyMarker)placemark.overlay;
                    marker.setInfoWindow(new CustomInfoWindow(typ, map, marker, placemark, city));
                }else if(placemark.overlay instanceof MyPolyline){
                    MyPolyline polyline = (MyPolyline)placemark.overlay;
                    polyline.setInfoWindow(new CustomInfoWindow(typ, map, polyline, placemark, city));
                }else if(placemark.overlay instanceof MyPolygon){
                    MyPolygon polygon = (MyPolygon)placemark.overlay;
                    polygon.setInfoWindow(new CustomInfoWindow(typ, map, polygon, placemark, city));
                }
            }
        }
        if (styler == null)
            folderOverlay.setEnabled(kmlFolder.mVisibility);
        else
            styler.onFeature(folderOverlay, kmlFolder);
        return folderOverlay;
    }

    public void fillFavoritesList(KmlDocument kmlDocument){
        KmlFolder kmlFolder = kmlDocument.mKmlRoot;
        Tab_Map.favorites_adapter.clear();
        int idx = -1;
        for (KmlFeature k:kmlFolder.mItems){
            idx++;
            KmlPlacemark placemark = null;
            if(k instanceof KmlPlacemark) {
                placemark = (KmlPlacemark) k;
            }
            if(placemark == null)   continue;
            if (placemark.overlay == null)  continue;

            int geometry_type;
            if(placemark.overlay instanceof MyMarker){
                geometry_type = City.POINT;
                MyMarker marker = (MyMarker)placemark.overlay;
                if(!cb_show_marks.isChecked()){
                    marker.setEnabled(false);
                    continue;
                }else {
                    marker.setEnabled(true);
                }
            }else if(placemark.overlay instanceof MyPolyline){
                geometry_type = City.POLYLINE;
                MyPolyline polyline = (MyPolyline)placemark.overlay;
                if(!cb_show_polylines.isChecked()){
                    polyline.setEnabled(false);
                    continue;
                }else {
                    polyline.setEnabled(true);
                }
            }else if(placemark.overlay instanceof MyPolygon){
                geometry_type = City.POLYGON;
                MyPolygon polygon = (MyPolygon)placemark.overlay;
                if(!cb_show_polygons.isChecked()){
                    polygon.setEnabled(false);
                    continue;
                }else {
                    polygon.setEnabled(true);
                }
            }else{
                continue;
            }

            GeoPoint p;
            int count = placemark.mGeometry.mCoordinates.size();
            if(count > 0)
                p = placemark.mGeometry.mCoordinates.get(0);
            else
                p = new GeoPoint(0.0,0.0);

            double fLon,fLat;
            float fAlt;
            fLon = p.getLongitude();
            fLat = p.getLatitude();
            fAlt = MainActivity.GetHeightJNI(fLon, fLat);

            // Add item to adapter
            City city = new City();
            city.strName = placemark.mName;
            city.fLon = fLon;
            city.fLat = fLat;
            city.fAlt = fAlt;
            city.geometry_type = geometry_type;
            city.index = idx;
            city.placemark = placemark;
            Tab_Map.favorites_adapter.add(city);
        }
    }

//    public FolderOverlay mvBuildOverlay(MapView map, Style defaultStyle, KmlFeature.Styler styler, KmlDocument kmlDocument){
//        KmlFolder kmlFolder = kmlDocument.mKmlRoot;
//        FolderOverlay folderOverlay = new FolderOverlay();
//        folderOverlay.setName(kmlFolder.mName);
//        folderOverlay.setDescription(kmlFolder.mDescription);
//        Tab_Map.favorites_adapter.clear();
//        int idx = -1;
//        for (KmlFeature k:kmlFolder.mItems){
//            idx++;
//            KmlPlacemark placemark = null;
//            if(k instanceof KmlPlacemark) {
//                placemark = (KmlPlacemark) k;
//            }
//            if(placemark == null)   continue;
//            Overlay overlay = placemark.buildOverlay(map, defaultStyle, styler, kmlDocument);
//            if (overlay != null) {
//                folderOverlay.add(overlay);
//
//                GeoPoint p;
//                int geometry_type;
//                if(placemark.mGeometry instanceof KmlPoint)
//                    geometry_type = City.POINT;
//                else
//                if(placemark.mGeometry instanceof KmlLineString)
//                    geometry_type = City.POLYLINE;
//                else
//                    geometry_type = City.POLYGON;
//
//                int count = placemark.mGeometry.mCoordinates.size();
//                if(count > 0)
//                    p = placemark.mGeometry.mCoordinates.get(0);
//                else
//                    p = new GeoPoint(0.0,0.0);
//
//                double fLon,fLat;
//                float fAlt;
//                fLon = p.getLongitude();
//                fLat = p.getLatitude();
//                fAlt = MainActivity.GetHeightJNI(fLon, fLat);
//
//                // Add item to adapter
//                City city = new City();
//                city.strName = placemark.mName;
//                city.fLon = fLon;
//                city.fLat = fLat;
//                city.fAlt = fAlt;
//                city.geometry_type = geometry_type;
//                city.index = idx;
//                city.overlay = overlay;
//                city.placemark = placemark;
//                Tab_Map.favorites_adapter.add(city);
//
//                int typ = 0;
//                String type = placemark.getExtendedData("type");
//                if(type != null) {
//                    typ = mv_utils.parseInt(type);
//                }
//                if(overlay instanceof MyMarker){
//                    MyMarker marker = (MyMarker)overlay;
//                    marker.setInfoWindow(new CustomInfoWindow(typ, map, marker, placemark, city));
//                }else if(overlay instanceof MyPolyline){
//                    MyPolyline polyline = (MyPolyline)overlay;
//                    polyline.setInfoWindow(new CustomInfoWindow(typ, map, polyline, placemark, city));
//                }else if(overlay instanceof MyPolygon){
//                    MyPolygon polygon = (MyPolygon)overlay;
//                    polygon.setInfoWindow(new CustomInfoWindow(typ, map, polygon, placemark, city));
//                }
//            }
//        }
//        if (styler == null)
//            folderOverlay.setEnabled(kmlFolder.mVisibility);
//        else
//            styler.onFeature(folderOverlay, kmlFolder);
//        return folderOverlay;
//    }

    public void delete_city(City city) {
        try {
            if(city == null)    return;

            KmlPlacemark placemark = city.placemark;
            if(placemark != null){
                InfoWindow.closeAllInfoWindowsOn(map);
                setOverlayVisible(placemark,false);

                Overlay overlay = city.placemark.overlay;
                if (overlay != null) {
                    kmlOverlay.remove(overlay);
                }

                KmlFolder kmlFolder = kmlFavoritesDocument.mKmlRoot;
                kmlFolder.mItems.remove(placemark);
                kmlFavoritesDocument.saveAsKML(favoritesFile);
            }

            favorites_adapter.remove(city);
            map.postInvalidate();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

//    public void delete_city0(int index) {
//        int count = kmlFavoritesDocument.mKmlRoot.mItems.size();
//        if(index < 0)   return;
//        if(index > count-1)   return;
//        try {
//            Overlay overlay = kmlOverlay.getItems().get(index);
//            if(overlay != null){
//                kmlOverlay.remove(overlay);
//                kmlFavoritesDocument.mKmlRoot.removeItem(index);
//                kmlFavoritesDocument.saveAsKML(favoritesFile);
//            }
//            map.postInvalidate();
//
//            City city = cities_adapter.getItem(index);
//            cities_adapter.remove(city);
//
////            String id = "";
////            KmlFeature feature = kmlFavoritesDocument.mKmlRoot.findFeatureId(id,true);
////            feature.mVisibility = false;
//
///*
////            for(int i=0;i<map.getOverlays().size();i++){
////                Overlay overlay = map.getOverlays().get(i);
////                if(overlay instanceof MyMarker && ((MyMarker) overlay).getId().equals("String")){
////                    map.getOverlays().remove(overlay);
////                }
////            }
//            kmlFavoritesDocument.mKmlRoot.mItems.remove(index);
//            kmlFavoritesDocument.saveAsKML(favoritesFile);
//
////            map.getOverlayManager().clear();
////            map.getOverlayManager().remove(kmlOverlay);
////            map.postInvalidate();
////            int overlay_index = map.getOverlayManager().indexOf(kmlOverlay);
//
//            map.getOverlayManager().remove(overlay_index);
//
////            Drawable defaultMarker = ResourcesCompat.getDrawable(MainActivity.activity.getResources(), R.drawable.marker_cluster, null);
////            Bitmap defaultBitmap = null;
////            if (defaultMarker != null) {
////                defaultBitmap = ((BitmapDrawable) defaultMarker).getBitmap();
////            }
////            Style defaultStyle = new Style(defaultBitmap, 0x901010AA, 3.0f, 0x20AA1010);
////            //13.2 Advanced styling with Styler
////            KmlFeature.Styler styler = new MyKmlStyler(defaultStyle);
//
//            kmlFavoritesDocument.parseKMLFile(favoritesFile);
//            kmlOverlay = kmlFavoritesDocument.mKmlRoot.buildOverlay(map, defaultStyle, styler, kmlFavoritesDocument);
//            map.getOverlays().add(kmlOverlay);
//            overlay_index = map.getOverlays().size() - 1;
//
////            map.getOverlays().set(overlay_index,kmlOverlay);
//
//            map.postInvalidate();
////            init(view);
//
//            parse_kml();
//*/
//        }
//        catch (Throwable ex)
//        {
//            MainActivity.MyLog(ex);
//        }
//    }

    private static boolean isEmptyString(String text) {
        return (text == null || text.trim().equals("null") || text.trim().length() <= 0);
    }

    static public float mission_altitude = 0.0f;
    public WaypointMission load_mission_kml_as_list(int start_idx,float altitude,float speed,int point_count,boolean bCustomAlt,boolean bMultiView,int nPitch) {
        try {
            map.getOverlays().remove(missionOverlay);

            int count = 0;
            File missionFile = new File(strMissionPath);
            KmlDocument missionDocument = new KmlDocument();
            if(missionFile.exists()) {
                KmlDocument tempDocument = new KmlDocument();

                missionDocument.parseKMLFile(missionFile);
                count = missionDocument.mKmlRoot.mItems.size();
                if(count > 0) {
                    for (int i = Math.max(0, start_idx); i < Math.min(start_idx + point_count, count); i++) {
                        tempDocument.mKmlRoot.mItems.add(missionDocument.mKmlRoot.mItems.get(i));
                    }
                    String strStyle = missionDocument.getStylesList()[0];
                    missionOverlay = tempDocument.mKmlRoot.buildOverlay(map, missionDocument.getStyle(strStyle), null, tempDocument);
                    map.getOverlays().add(0, missionOverlay);
                    map.postInvalidate();
                }
            }
            else {
                Tab_Messenger.showToast("[" + missionFile.getPath() + "] Not found.");
            }

            String str_Lon,str_Lat,str_Alt,str_START_TAKE_PHOTO,str_ROTATE_AIRCRAFT,str_GIMBAL_PITCH;
            double lon,lat,alt;
            int take_picture;
            int yaw,pitch;
            boolean bFirstPoint = true;

            WaypointMission.Builder builder = new WaypointMission.Builder();
            builder.autoFlightSpeed(speed);
            builder.maxFlightSpeed(10f);
            builder.setExitMissionOnRCSignalLostEnabled(false);
            builder.finishedAction(WaypointMissionFinishedAction.GO_HOME);
            builder.flightPathMode(WaypointMissionFlightPathMode.NORMAL);
            builder.gotoFirstWaypointMode(WaypointMissionGotoWaypointMode.SAFELY);
            builder.headingMode(WaypointMissionHeadingMode.USING_WAYPOINT_HEADING);
            builder.repeatTimes(1);
//            List<Waypoint> waypointList = new ArrayList<>();

            for (int i= Math.max(0,start_idx);i<Math.min(start_idx + point_count,count);i++) {
                KmlPlacemark placemark = (KmlPlacemark)missionDocument.mKmlRoot.mItems.get(i);
                if(placemark.mGeometry.mCoordinates.size() <= 0)    continue;
                GeoPoint p = placemark.mGeometry.mCoordinates.get(0);
                lon = p.getLongitude();
                lat = p.getLatitude();
                alt = p.getAltitude();
                yaw = 0;
                pitch = -90;
                take_picture = 1;

                str_Lon = placemark.getExtendedData("LON");
                str_Lat = placemark.getExtendedData("LAT");
                str_Alt = placemark.getExtendedData("ALT");
                str_START_TAKE_PHOTO = placemark.getExtendedData("START_TAKE_PHOTO");
                str_ROTATE_AIRCRAFT = placemark.getExtendedData("ROTATE_AIRCRAFT");
                str_GIMBAL_PITCH = placemark.getExtendedData("GIMBAL_PITCH");
                if(!isEmptyString(str_Lon)) lon = mv_utils.parseDouble(str_Lon);
                if(!isEmptyString(str_Lat)) lat = mv_utils.parseDouble(str_Lat);
                if(!isEmptyString(str_Alt)) alt = mv_utils.parseDouble(str_Alt);
                if(!isEmptyString(str_START_TAKE_PHOTO)) take_picture = mv_utils.parseInt(str_START_TAKE_PHOTO);
                if(!isEmptyString(str_ROTATE_AIRCRAFT)) yaw = (int)mv_utils.parseDouble(str_ROTATE_AIRCRAFT);
                if(!isEmptyString(str_GIMBAL_PITCH)) pitch = (int)mv_utils.parseDouble(str_GIMBAL_PITCH);
                if(yaw >= 180)  yaw -= 360;

                if(bFirstPoint)
                {
                    bFirstPoint = false;
                    mission_altitude = (float)alt;
                    MainActivity.start_lon = lon;
                    MainActivity.start_lat = lat;
                    set_poi_pos(lon, lat, alt, "Mission",true);
                    set_start_pos(lon, lat, true);
                }

                // TODO Use altitude of each waypoint in mission
                Waypoint mWaypoint;
                if (bCustomAlt) {
                    mWaypoint = new Waypoint(lat, lon, altitude);
                    mWaypoint.altitude = altitude;
                } else {
                    mWaypoint = new Waypoint(lat, lon, (float) alt);
                    mWaypoint.altitude = (float) alt;
                }

                // Add actions
                if(bMultiView)
                {
                    // action 0
                    int yaw0 = 0;
                    int pitch0 = -90;
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, yaw0));
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, pitch0));
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.START_TAKE_PHOTO, take_picture));

                    // action 1
                    pitch0 = nPitch;
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, pitch0));
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.START_TAKE_PHOTO, take_picture));

                    // action 2
                    yaw0 = 90;
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, yaw0));
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.START_TAKE_PHOTO, take_picture));

                    // action 3
                    yaw0 = 180;
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, yaw0));
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.START_TAKE_PHOTO, take_picture));

                    // action 4
                    yaw0 = -90;
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, yaw0));
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.START_TAKE_PHOTO, take_picture));

                    builder.addWaypoint(mWaypoint);
                }
                else {
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, yaw));
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, pitch));

                    mWaypoint.addAction(new WaypointAction(WaypointActionType.START_TAKE_PHOTO, take_picture));
                    builder.addWaypoint(mWaypoint);
                }
            }

            setResultToToast("Mission waypoint count: "+builder.getWaypointList().size());

            return builder.build();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
            return null;
        }
    }

    public WaypointMission create_mission_panorama_as_list(double lon,double lat,float alt,float speed)
    {
        try {
            WaypointMission.Builder builder = new WaypointMission.Builder();
            builder.autoFlightSpeed(speed);
            builder.maxFlightSpeed(10f);
            builder.setExitMissionOnRCSignalLostEnabled(false);
            builder.finishedAction(WaypointMissionFinishedAction.GO_HOME);
            builder.flightPathMode(WaypointMissionFlightPathMode.NORMAL);
            builder.gotoFirstWaypointMode(WaypointMissionGotoWaypointMode.SAFELY);
            builder.headingMode(WaypointMissionHeadingMode.USING_WAYPOINT_HEADING);
            builder.repeatTimes(1);

            double pitch_min = -90;
            double pitch_max = 0;
            double pitch_count = 4;
            double pitch_step = (pitch_max - pitch_min)/pitch_count;

            double yaw_min = 0;
            double yaw_max = 360;
            double yaw_count = 8;
            double yaw_step = (yaw_max - yaw_min)/yaw_count;

            int take_picture = 1;

            // oblique
            int pitch0 = -90;
            int yaw0 = 0;
            for(double pitch=pitch_min;pitch<=pitch_max;pitch += pitch_step)
            {
                pitch0 = (int)Math.round(pitch);

                if(pitch0 == -90)
                {
                    Waypoint mWaypoint;
                    mWaypoint = new Waypoint(lat, lon, alt);
                    mWaypoint.altitude = alt;

                    mWaypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, -90));
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, 0));
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.START_TAKE_PHOTO, take_picture));

                    builder.addWaypoint(mWaypoint);
                    continue;
                }

                for(double yaw=yaw_min;yaw<=yaw_max;yaw += yaw_step)
                {
                    Waypoint mWaypoint;
                    mWaypoint = new Waypoint(lat, lon, alt);
                    mWaypoint.altitude = alt;

                    yaw0 = (int)Math.round(yaw);
                    if(yaw0 >= 180)  yaw0 -= 360;
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, pitch0));
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, yaw0));
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.START_TAKE_PHOTO, take_picture));

                    builder.addWaypoint(mWaypoint);
                }
            }

            setResultToToast("Panorama actions count: "+builder.getWaypointList().size());

            return builder.build();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
            return null;
        }
    }

/*
    static public int mission_yaw = 0;
    static public int mission_pitch = -90;
    public List<WaypointMission> load_mission_kml_as_list(int n,float altitude,float speed) {
        try {
            map.getOverlays().remove(missionOverlay);

            KmlDocument missionDocument = new KmlDocument();
            File missionFile = new File(strMissionPath);
            if(missionFile.exists()) {
                missionDocument.parseKMLFile(missionFile);
                missionOverlay = (Overlay)missionDocument.mKmlRoot.buildOverlay(map, null, null, missionDocument);
                map.getOverlays().add(0,missionOverlay);
                map.postInvalidate();
            }
            else
                Toast.makeText(MainActivity.ctx, "["+missionFile.getPath()+"] Not found.", Toast.LENGTH_LONG).show();

            String str_Lon,str_Lat,str_START_TAKE_PHOTO,str_ROTATE_AIRCRAFT,str_GIMBAL_PITCH;
            double lon,lat,alt;
            int take_picture;
            int yaw,pitch,prev_yaw = 1000,prev_pitch = 1000;
            int count = missionDocument.mKmlRoot.mItems.size();
            int K = (int)Math.ceil((float)count / (float)n);
            int m;
            boolean bFirst = true;
            boolean bFirstPoint = true;
            List<WaypointMission> list = new ArrayList<>();
            for(int k=0;k<K;k++)
            {
                WaypointMission.Builder builder = new WaypointMission.Builder();
                builder.autoFlightSpeed(speed);
                builder.maxFlightSpeed(10f);
                builder.setExitMissionOnRCSignalLostEnabled(false);
                builder.finishedAction(WaypointMissionFinishedAction.NO_ACTION);
                builder.flightPathMode(WaypointMissionFlightPathMode.NORMAL);
                builder.gotoFirstWaypointMode(WaypointMissionGotoWaypointMode.SAFELY);
//                builder.gotoFirstWaypointMode(WaypointMissionGotoWaypointMode.POINT_TO_POINT);//AliSoft 2019.03.04
                builder.headingMode(WaypointMissionHeadingMode.USING_WAYPOINT_HEADING);
//                builder.headingMode(WaypointMissionHeadingMode.USING_INITIAL_DIRECTION);
                builder.repeatTimes(1);
                List<Waypoint> waypointList = new ArrayList<>();

                for (int i=0;i<n;i++) {
                    m = k*n+i;
                    if(m >= count)  break;
                    KmlPlacemark placemark = (KmlPlacemark)missionDocument.mKmlRoot.mItems.get(m);
                    if(placemark.mGeometry.mCoordinates.size() <= 0)    continue;
                    GeoPoint p = placemark.mGeometry.mCoordinates.get(0);
                    lon = p.getLongitude();
                    lat = p.getLatitude();
                    alt = p.getAltitude();
                    yaw = 0;
                    pitch = -90;
                    take_picture = 1;

                    if(bFirstPoint) set_poi_pos(lon, lat, alt, "Mission",true);
                    bFirstPoint = false;

                    str_Lon = placemark.getExtendedData("LON");
                    str_Lat = placemark.getExtendedData("LAT");
                    str_START_TAKE_PHOTO = placemark.getExtendedData("START_TAKE_PHOTO");
                    str_ROTATE_AIRCRAFT = placemark.getExtendedData("ROTATE_AIRCRAFT");
                    str_GIMBAL_PITCH = placemark.getExtendedData("GIMBAL_PITCH");
                    if(!isEmptyString(str_Lon)) lon = Double.parseDouble(str_Lon);
                    if(!isEmptyString(str_Lat)) lat = Double.parseDouble(str_Lat);
                    if(!isEmptyString(str_START_TAKE_PHOTO)) take_picture = Integer.parseInt(str_START_TAKE_PHOTO);
                    if(!isEmptyString(str_ROTATE_AIRCRAFT)) yaw = (int)Double.parseDouble(str_ROTATE_AIRCRAFT);
                    if(!isEmptyString(str_GIMBAL_PITCH)) pitch = (int)Double.parseDouble(str_GIMBAL_PITCH);

                    if(yaw >= 180)  yaw -= 360;

                    // TODO Use altitude of each waypoint in mission
                    final Waypoint mWaypoint = new Waypoint(lat, lon, (float)p.getAltitude());
                    mWaypoint.altitude = altitude;

//                    if(bFirst) {
//                        bFirst = false;
//                        mission_yaw = yaw;
//                        mission_pitch = pitch;
//
//                        // this for simulator only and unmovable gimbals like phantom3
//                        mWaypoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, yaw));
//                        mWaypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, pitch));
//                    }

                    // TODO test yaw/pitch update
//                    if(cb_smart_mission.isChecked()) {
////                        if(yaw != prev_yaw) mWaypoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, yaw));
////                        if(pitch != prev_pitch) mWaypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, pitch));
//                        if(yaw != prev_yaw) mWaypoint.heading = yaw;
//                        if(pitch != prev_pitch) mWaypoint.gimbalPitch = pitch;
//                    }
//                    else {
////                        mWaypoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, yaw));
////                        mWaypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, pitch));
//                        mWaypoint.heading = yaw;
//                        mWaypoint.gimbalPitch = pitch;
//                    }

                    mWaypoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, yaw));
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, pitch));

                    mWaypoint.addAction(new WaypointAction(WaypointActionType.START_TAKE_PHOTO, take_picture));
                    waypointList.add(mWaypoint);

                    prev_yaw = yaw;
                    prev_pitch = pitch;
                }

                builder.waypointList(waypointList).waypointCount(waypointList.size());
                setResultToToast("Mission #"+Integer.toString(k+1)+" - waypoint count: "+builder.getWaypointList().size());

                list.add(builder.build());
            }

            return list;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
            return null;
        }
    }
*/
public List<WaypointMission> load_favorites_kml_as_list_ex(int n,float altitude,int index) {
    try {
        KmlPlacemark placemark = (KmlPlacemark) kmlFavoritesDocument.mKmlRoot.mItems.get(index);

        // polyline simplification
        ArrayList<GeoPoint> mCoordinates;
        mCoordinates = PointReducer.reduceWithTolerance(placemark.mGeometry.mCoordinates,0.000001);
        int count = mCoordinates.size();

        String str_Lon,str_Lat;
        double lon,lat;
        int K = (int)Math.ceil((float)count / (float)n);
        int m;
        List<WaypointMission> list = new ArrayList<>();
        for(int k=0;k<K;k++)
        {
            WaypointMission.Builder builder = new WaypointMission.Builder();
            builder.autoFlightSpeed(5f);
            builder.maxFlightSpeed(10f);
            builder.setExitMissionOnRCSignalLostEnabled(false);
            builder.finishedAction(WaypointMissionFinishedAction.NO_ACTION);
            builder.flightPathMode(WaypointMissionFlightPathMode.NORMAL);
            builder.gotoFirstWaypointMode(WaypointMissionGotoWaypointMode.SAFELY);
            builder.headingMode(WaypointMissionHeadingMode.USING_WAYPOINT_HEADING);
            builder.repeatTimes(1);

            for (int i=0;i<n;i++) {
                m = k*n+i;
                if(m >= count)  break;
                GeoPoint p = mCoordinates.get(m);
                lon = p.getLongitude();
                lat = p.getLatitude();

                str_Lon = placemark.getExtendedData("LON");
                str_Lat = placemark.getExtendedData("LAT");
                if(!isEmptyString(str_Lon)) lon = mv_utils.parseDouble(str_Lon);
                if(!isEmptyString(str_Lat)) lat = mv_utils.parseDouble(str_Lat);

                final Waypoint mWaypoint = new Waypoint(lat, lon, (float)p.getAltitude());
                mWaypoint.altitude = altitude;
                mWaypoint.addAction(new WaypointAction(WaypointActionType.START_TAKE_PHOTO, 1));
//                    mWaypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, -90));
                if(i == 0)
                {
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.START_RECORD, 1));
                }
                else
                if(i == n-1)
                {
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.STOP_RECORD, 1));
                }
                builder.addWaypoint(mWaypoint);
            }

            setResultToToast(k + 1 +" - Mission waypoint count: "+builder.getWaypointList().size());

            list.add(builder.build());
        }

        return list;
    }
    catch (Throwable ex)
    {
        MainActivity.MyLog(ex);
        return null;
    }
}

//    public List<WaypointMission> load_favorites_kml_as_list_ex(int n,float altitude,int index) {
//        try {
//            KmlPlacemark placemark = (KmlPlacemark)kmlFavoritesDocument.mKmlRoot.mItems.get(index);
//
//            // polyline simplification
//            ArrayList<GeoPoint> mCoordinates;
//            mCoordinates = PointReducer.reduceWithTolerance(placemark.mGeometry.mCoordinates,0.000001);
//            int count = mCoordinates.size();
//
//            String str_Lon,str_Lat;
//            double lon,lat;
//            int K = (int)Math.ceil((float)count / (float)n);
//            int m;
//            List<WaypointMission> list = new ArrayList<>();
//            for(int k=0;k<K;k++)
//            {
//                WaypointMission.Builder builder = new WaypointMission.Builder();
//                builder.autoFlightSpeed(5f);
//                builder.maxFlightSpeed(10f);
//                builder.setExitMissionOnRCSignalLostEnabled(false);
//                builder.finishedAction(WaypointMissionFinishedAction.NO_ACTION);
//                builder.flightPathMode(WaypointMissionFlightPathMode.NORMAL);
//                builder.gotoFirstWaypointMode(WaypointMissionGotoWaypointMode.SAFELY);
//                builder.headingMode(WaypointMissionHeadingMode.USING_WAYPOINT_HEADING);
//                builder.repeatTimes(1);
//                List<Waypoint> waypointList = new ArrayList<>();
//
//                for (int i=0;i<n;i++) {
//                    m = k*n+i;
//                    if(m >= count)  break;
//                    GeoPoint p = mCoordinates.get(m);
//                    lon = p.getLongitude();
//                    lat = p.getLatitude();
//
//                    str_Lon = placemark.getExtendedData("LON");
//                    str_Lat = placemark.getExtendedData("LAT");
//                    if(!isEmptyString(str_Lon)) lon = Double.parseDouble(str_Lon);
//                    if(!isEmptyString(str_Lat)) lat = Double.parseDouble(str_Lat);
//
//                    final Waypoint mWaypoint = new Waypoint(lat, lon, (float)p.getAltitude());
//                    mWaypoint.altitude = altitude;
//                    mWaypoint.addAction(new WaypointAction(WaypointActionType.START_TAKE_PHOTO, 0));
////                    mWaypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, -90));
//                    waypointList.add(mWaypoint);
//                }
//
//                builder.waypointList(waypointList).waypointCount(waypointList.size());
//                setResultToToast(Integer.toString(k+1)+" - Mission waypoint count: "+builder.getWaypointList().size());
//
//                list.add(builder.build());
//            }
//
//            return list;
//        }
//        catch (Throwable ex)
//        {
//            MainActivity.MyLog(ex);
//            return null;
//        }
//    }

    public boolean stitch_path(int index,double fov_deg,int w,int h,double alt_above_ground,double Xpercent,double Ypercent,double ele_deg,int path_size) {
        try {
            KmlPlacemark placemark = (KmlPlacemark) kmlFavoritesDocument.mKmlRoot.mItems.get(index);

            // polyline simplification
            ArrayList<GeoPoint> mCoordinates;
            mCoordinates = PointReducer.reduceWithTolerance(placemark.mGeometry.mCoordinates,0.000001);
            int count = mCoordinates.size();

            double[] polygon = new double[2*count];
            double lon,lat;
            int k = 0;
            for (int i=0;i<count;i++) {
                GeoPoint p = mCoordinates.get(i);
                lon = p.getLongitude();
                lat = p.getLatitude();
                polygon[k++] = lon;
                polygon[k++] = lat;
            }
            return MainActivity.StitchPolygon(polygon,fov_deg,w,h,alt_above_ground,Xpercent,Ypercent,ele_deg,path_size);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
            return false;
        }
    }

    static boolean bProjectileCalculated = false;
    public static boolean projectile_line(double gun_lon0, double gun_lat0, double target_lon0, double target_lat0, int iterations, double z0, double time_step, double velocity0, double angle0, double diameter0, double mass0, double wind0, double error, double dencity0, double cofficient0, double temp0, double gravity0, boolean const_gravity0) {
        try {
                String strInfo1 = "";
                strInfo1 += "Alt ASL: "+String.format(Locale.ENGLISH, "%.01f", z0)+"\n";
                strInfo1 += "Velocity: "+String.format(Locale.ENGLISH, "%.01f", velocity0)+"\n";
                strInfo1 += "Angle: "+String.format(Locale.ENGLISH, "%.01f", angle0)+"\n";
                strInfo1 += "Wind: "+String.format(Locale.ENGLISH, "%.01f", wind0)+"\n";
                strInfo1 += "Mass: "+String.format(Locale.ENGLISH, "%.01f", mass0)+"\n";
                strInfo1 += "Diameter: "+String.format(Locale.ENGLISH, "%.01f", diameter0);

                int idx = 0;
                double[] res = MainActivity.Projectile(gun_lon0, gun_lat0, target_lon0, target_lat0, iterations, z0, time_step, velocity0, angle0, diameter0, mass0, wind0, error, dencity0, cofficient0, temp0, gravity0, const_gravity0);
                ProjectileSettings.gun_lon0 = res[idx++];
                ProjectileSettings.gun_lat0 = res[idx];

                if(bProjectileCalculated)
                    ProjectileSettings.color = Color.BLUE;
                else
                    ProjectileSettings.color = Color.YELLOW;
                bProjectileCalculated = !bProjectileCalculated;

                MainActivity.tab_map.gun_point.setLongitude(ProjectileSettings.gun_lon0);
                MainActivity.tab_map.gun_point.setLatitude(ProjectileSettings.gun_lat0);

                if(gun_Marker == null) {
                    gun_Marker = new MyMarker(map);
                    gun_Marker.setPosition(MainActivity.tab_map.gun_point);
                    gun_Marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                    gun_Marker.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
                    gun_Marker.setIcon(mv_utils.getDrawable(MainActivity.ctx, R.drawable.marker_projectile));
                    gun_Marker.setTitle("Gun");
                    gun_Marker.setInfo1("");
                    gun_Marker.setInfo2("");
                    gun_Marker.setInfoWindow(new CustomInfoWindow(0, map, null, null, null));
//                        gun_Marker.setOnMarkerClickListener((item, arg1) -> {
//                            item.showInfoWindow();
//                            return true;
//                        });
                    map.getOverlays().add(gun_Marker);
                }
                gun_Marker.setInfo1(strInfo1);
                gun_Marker.setPosition(MainActivity.tab_map.gun_point);

//                Location location = mv_LocationOverlay.curr_location;
//                if(location != null) {
//                    char cDeg = (char)0x00B0;
//                    String cMeter = " m";
//                    String cSeconds = " sec";
//
//                    float[] list = new float[2];
//                    Location.distanceBetween(location.getLatitude(), location.getLongitude(), gun_point.getLatitude(), gun_point.getLongitude(), list);
//                    double dist = Math.round(list[0]);
//                    double bearing = (float) MainActivity.db_deg(list[1]);
//                    String strInfo2 = "Distance: " + String.format(Locale.ENGLISH, "%.0f", dist) + cMeter + "\n";
//                    strInfo2 += "Heading: " + String.format(Locale.ENGLISH, "%.01f", bearing) + cDeg + "\n";
//                    long delay_s = Math.round(dist / location.getSpeed());
//                    strInfo2 += "Time to fire: " + String.format(Locale.ENGLISH, "%d", delay_s) + cSeconds;
//                    gun_Marker.setInfo2(strInfo2);
//                }

                MainActivity.SaveProjectile(MainActivity.strProjectileFile,ProjectileSettings.gun_lon0, ProjectileSettings.gun_lat0, target_lon0, target_lat0, z0, time_step, velocity0, angle0, diameter0, mass0, wind0, error, dencity0, cofficient0, temp0, gravity0, const_gravity0);

                map.postInvalidate();

//                // gun
//                GeoPoint gun_point = new GeoPoint(gun_lat0, gun_lon0);
//                gun_marker = new MyMarker(map);
//                gun_marker.setPosition(gun_point);
//                gun_marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
//                gun_marker.setIcon(MainActivity.activity.getResources().getDrawable(R.drawable.marker_projectile,null));
//                gun_marker.setTitle("Gun");
//                gun_marker.setInfo(strInfo);
//                gun_marker.setInfoWindow(new CustomInfoWindow(map));
////                        gun_marker.setOnMarkerClickListener((item, arg1) -> {
////                            item.showInfoWindow();
////                            return true;
////                        });
//                map.getOverlays().add(gun_marker);

//                        // target
//                GeoPoint target_point0 = new GeoPoint(target_lat0, target_lon0);
//                        MyMarker target_marker0 = new MyMarker(map);
//                        target_marker0.setPosition(target_point0);
//                        target_marker0.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
//                        target_marker0.setIcon(MainActivity.activity.getResources().getDrawable(R.drawable.marker_default,null));
//                        target_marker0.setTitle("Target"+ Integer.toString(i));
//                        target_marker0.setInfoWindow(new CustomInfoWindow(map));
////                        target_marker.setOnMarkerClickListener((item, arg1) -> {
////                            item.showInfoWindow();
////                            return true;
////                        });
//                        map.getOverlays().add(target_marker0);
            return true;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
            return false;
        }
    }

    public double path_area(int index)
    {
        try {
            if(kmlFavoritesDocument.mKmlRoot.mItems.size() <= 0)   return 0;
            if(index < 0)   return 0;
            if(index >= kmlFavoritesDocument.mKmlRoot.mItems.size())   return 0;
            KmlPlacemark placemark = (KmlPlacemark) kmlFavoritesDocument.mKmlRoot.mItems.get(index);
            if(placemark.mGeometry.mCoordinates.size() <= 2)    return 0.0;

            // polyline simplification
            ArrayList<GeoPoint> mCoordinates;
            mCoordinates = PointReducer.reduceWithTolerance(placemark.mGeometry.mCoordinates,0.000001);
            int count = mCoordinates.size();

            List<LatLng> latLngs = new ArrayList<>();
            for (int i=0;i<count;i++) {
                GeoPoint p = mCoordinates.get(i);
                latLngs.add(new LatLng(p.getLatitude(), p.getLongitude()));
            }
            return SphericalUtil.computeArea(latLngs);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
            return 0;
        }
    }

    public double path_length(int index)
    {
        try {
            if(kmlFavoritesDocument.mKmlRoot.mItems.size() <= 0)   return 0;
            if(index < 0)   return 0;
            if(index >= kmlFavoritesDocument.mKmlRoot.mItems.size())   return 0;
            KmlPlacemark placemark = (KmlPlacemark) kmlFavoritesDocument.mKmlRoot.mItems.get(index);
            if(placemark.mGeometry.mCoordinates.size() <= 1)    return 0.0;

            // polyline simplification
            ArrayList<GeoPoint> mCoordinates;
            mCoordinates = PointReducer.reduceWithTolerance(placemark.mGeometry.mCoordinates,0.000001);
            int count = mCoordinates.size();

            List<LatLng> latLngs = new ArrayList<>();
            for (int i=0;i<count;i++) {
                GeoPoint p = mCoordinates.get(i);
                latLngs.add(new LatLng(p.getLatitude(), p.getLongitude()));
            }
            return SphericalUtil.computeLength(latLngs);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
            return 0;
        }
    }

    public int path_count(int index)
    {
        try {
            if(kmlFavoritesDocument.mKmlRoot.mItems.size() <= 0)   return 0;
            if(index < 0)   return 0;
            if(index >= kmlFavoritesDocument.mKmlRoot.mItems.size())   return 0;
            KmlPlacemark placemark = (KmlPlacemark) kmlFavoritesDocument.mKmlRoot.mItems.get(index);
            if(placemark.mGeometry.mCoordinates.size() < 1)    return 0;
            return placemark.mGeometry.mCoordinates.size();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
            return 0;
        }
    }

    @Override
    public void onStop() {
        try {
            kmlFavoritesDocument.saveAsKML(favoritesFile);

            removeListener();
            gun_Marker = null;
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            //this will refresh the osmdroid configuration on resuming.
            //if you make changes to the configuration, use
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
//        Configuration.getInstance().save(ctx, prefs);
//        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

//        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
//        Configuration.getInstance().setOsmdroidBasePath(new File(Environment.getExternalStorageDirectory()+"/MapViewer/", "Maps"));
//        Configuration.getInstance().setOsmdroidTileCache(new File(Environment.getExternalStorageDirectory()+"/MapViewer/", "Tiles"));
//        Configuration.getInstance().setOsmdroidBasePath(new File(Environment.getExternalStorageDirectory(), "MapViewer"));
//        Configuration.getInstance().setOsmdroidTileCache(new File(Environment.getExternalStorageDirectory(), "MapViewer"));

            //this will refresh the osmdroid configuration on resuming.
            //if you make changes to the configuration, use
            //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
            if (map != null)    map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
            if(mLocationOverlay != null)    mLocationOverlay.onResume();

            addListener();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }
    @Override
    public void onPause() {
        try {
            //save the configuration
//        try {
//            if (tileDownloadThreads.getError() == null)
//                Configuration.getInstance().setTileDownloadThreads(Short.parseShort(tileDownloadThreads.getText().toString()));
//        } catch (Throwable ex) {
//            MainActivity.MyLog(ex);
//            ex.printStackTrace();
//        }
//        try {
//            if (tileDownloadMaxQueueSize.getError() == null)
//                Configuration.getInstance().setTileDownloadMaxQueueSize(Short.parseShort(tileDownloadMaxQueueSize.getText().toString()));
//        } catch (Throwable ex) {
//            MainActivity.MyLog(ex);
//            ex.printStackTrace();
//        }
//        try {
//            if (cacheMapTileCount.getError() == null)
//                Configuration.getInstance().setCacheMapTileCount(Short.parseShort(cacheMapTileCount.getText().toString()));
//        } catch (Throwable ex) {
//            MainActivity.MyLog(ex);
//            ex.printStackTrace();
//        }
//        try {
//            if (tileFileSystemThreads.getError() == null)
//                Configuration.getInstance().setTileFileSystemThreads(Short.parseShort(tileFileSystemThreads.getText().toString()));
//        } catch (Throwable ex) {
//            MainActivity.MyLog(ex);
//            ex.printStackTrace();
//        }
//        try {
//            if (tileFileSystemMaxQueueSize.getError() == null)
//                Configuration.getInstance().setTileFileSystemMaxQueueSize(Short.parseShort(tileFileSystemMaxQueueSize.getText().toString()));
//        } catch (Throwable ex) {
//            MainActivity.MyLog(ex);
//            ex.printStackTrace();
//        }
//        try {
//            if (gpsWaitTime.getError() == null)
//                Configuration.getInstance().setGpsWaitTime(Long.parseLong(gpsWaitTime.getText().toString()));
//        } catch (Throwable ex) {
//            MainActivity.MyLog(ex);
//            ex.printStackTrace();
//        }
//        try {
//            if (additionalExpirationTime.getError() == null)
//                Configuration.getInstance().setExpirationExtendedDuration(Long.parseLong(additionalExpirationTime.getText().toString()));
//        } catch (Throwable ex) {
//            MainActivity.MyLog(ex);
//            ex.printStackTrace();
//        }
//        try {
//            Long val=Long.parseLong(overrideExpirationTime.getText().toString());
//            if (val > 0)
//                Configuration.getInstance().setExpirationOverrideDuration(val);
//            else
//                Configuration.getInstance().setExpirationOverrideDuration(null);
//        } catch (Throwable ex) {
//            MainActivity.MyLog(ex);
//            ex.printStackTrace();
//            Configuration.getInstance().setExpirationOverrideDuration(null);
//        }
//
//        try {
//            Long val=Long.parseLong(cacheMaxSize.getText().toString());
//            if (val > 0)
//                Configuration.getInstance().setTileFileSystemCacheMaxBytes(val);
//        } catch (Throwable ex) {
//            MainActivity.MyLog(ex);
//            ex.printStackTrace();
//        }
//
//        try {
//            Long val=Long.parseLong(cacheTrimSize.getText().toString());
//            if (val > 0)
//                Configuration.getInstance().setTileFileSystemCacheTrimBytes(val);
//        } catch (Throwable ex) {
//            MainActivity.MyLog(ex);
//            ex.printStackTrace();
//        }
//
//        Configuration.getInstance().setUserAgentValue(httpUserAgent.getText().toString());
//        Configuration.getInstance().setDebugMapView(checkBoxMapViewDebug.isChecked());
//        Configuration.getInstance().setDebugMode(checkBoxDebugMode.isChecked());
//        Configuration.getInstance().setDebugTileProviders(checkBoxDebugTileProvider.isChecked());
//        Configuration.getInstance().setMapViewHardwareAccelerated(checkBoxHardwareAcceleration.isChecked());
//        Configuration.getInstance().setDebugMapTileDownloader(checkBoxDebugDownloading.isChecked());
//        Configuration.getInstance().setOsmdroidTileCache(new File(textViewCacheDirectory.getText().toString()));
//        Configuration.getInstance().setOsmdroidBasePath(new File(textViewBaseDirectory.getText().toString()));

//        String path = Environment.getExternalStorageDirectory().getPath()+"/MapViewer/Maps";
//        File file = new File(path);
//        Configuration.getInstance().setOsmdroidBasePath(file);

//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
//        Configuration.getInstance().save(ctx, prefs);

            //this will refresh the osmdroid configuration on resuming.
            //if you make changes to the configuration, use
            //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            //Configuration.getInstance().save(this, prefs);
            if (map != null)    map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
            if(mLocationOverlay != null)    mLocationOverlay.onPause();

            removeListener();
            gun_Marker = null;
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        try {
            removeListener();
            gun_Marker = null;
            if(timeline != null)    timeline.onDetachedFromWindow();
            customMapHandler.removeCallbacks(updateMapTimerThread);
            customHandler.removeCallbacks(updateTimerThread);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        super.onDestroy();
    }

    void set_cam_pos(double lon,double lat,double alt,boolean bUpdate) {
        try
        {
            if(!bIsCreated) return;
            if(MainActivity.bNavigation)    return;

            MainActivity.uav_lon = lon;
            MainActivity.uav_lat = lat;
            MainActivity.uav_alt = alt;

//            if(Tab_Camera.cb_target_lock.isChecked())
//            {
//                float[] res = MainActivity.CalculateAnglesJNI(MainActivity.uav_lon,MainActivity.uav_lat,MainActivity.uav_alt,MainActivity.target_lon,MainActivity.target_lat,MainActivity.target_alt);
//                crosshairView.lastYaw = (float)crosshairView.db_deg(Math.toDegrees((double)res[0])) - MainActivity.uav_yaw_enc;
//                if(crosshairView.lastYaw >= 180.0)   crosshairView.lastYaw -= 360.0f;
//                crosshairView.lastPitch = (float)Math.toDegrees((double)res[1]) - MainActivity.uav_pitch_enc;
//
//                MainActivity.tab_camera.rotateGimbal(crosshairView.lastYaw, crosshairView.lastPitch, 0.0f);
//
//                MainActivity.image_yaw = crosshairView.lastYaw;
//                MainActivity.image_pitch = crosshairView.lastPitch;
//                MainActivity.image_roll = 0.0f;
//
//                fov_overlay.setAzi(MainActivity.image_yaw);
//            }

            boolean bChanged = (Math.abs(cameraPoint.getLongitude() - lon) >= 0.000001) || (Math.abs(cameraPoint.getLatitude() - lat) >= 0.000001);
            if(bChanged){
                cameraPoint.setLongitude(lon);
                cameraPoint.setLatitude(lat);
                cameraPoint.setAltitude(alt);

                if(fov_overlay != null) fov_overlay.setCameraPos(cameraPoint);

                cam_Marker.setPosition(cameraPoint);
                String strText = String.format(Locale.ENGLISH,"%.06f", lon)+", "+String.format(Locale.ENGLISH,"%.06f", lat)+", "+String.format(Locale.ENGLISH,"%.01f", alt);
                cam_Marker.setTitle("Camera point: "+strText);

                if(bUpdate){
                    if(fov_overlay != null) fov_overlay.update_calculations();
                    map.postInvalidate();
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    void set_target_pos(double lon,double lat,double alt,boolean bUpdate) {
        try
        {
            if(!bIsCreated) return;
            if(target_Marker == null)   return;

            MainActivity.target_lon = lon;
            MainActivity.target_lat = lat;
            MainActivity.target_alt = (float)alt;

//            if(Tab_Camera.cb_target_lock.isChecked())
//            {
//                float[] res = MainActivity.CalculateAnglesJNI(MainActivity.uav_lon,MainActivity.uav_lat,MainActivity.uav_alt,MainActivity.target_lon,MainActivity.target_lat,MainActivity.target_alt);
//                crosshairView.lastYaw = (float)crosshairView.db_deg(Math.toDegrees((double)res[0])) - MainActivity.uav_yaw_enc;
//                if(crosshairView.lastYaw >= 180.0)   crosshairView.lastYaw -= 360.0f;
//                crosshairView.lastPitch = (float)Math.toDegrees((double)res[1]) - MainActivity.uav_pitch_enc;
//
//                MainActivity.tab_camera.rotateGimbal(crosshairView.lastYaw, crosshairView.lastPitch, 0.0f);
//
//                MainActivity.image_yaw = crosshairView.lastYaw;
//                MainActivity.image_pitch = crosshairView.lastPitch;
//                MainActivity.image_roll = 0.0f;
//
//                fov_overlay.setAzi(MainActivity.image_yaw);
//            }

            boolean bChanged = (Math.abs(targetPoint.getLongitude() - lon) >= 0.000001) || (Math.abs(targetPoint.getLatitude() - lat) >= 0.000001);
            if(bChanged){
                targetPoint.setLongitude(lon);
                targetPoint.setLatitude(lat);
                targetPoint.setAltitude(alt);
                if(fov_overlay != null) fov_overlay.setTargetPos(targetPoint);
                target_Marker.setPosition(targetPoint);
                String strText = String.format(Locale.ENGLISH,"%.06f", lon)+", "+String.format(Locale.ENGLISH,"%.06f", lat)+", "+String.format(Locale.ENGLISH,"%.01f", alt);
                target_Marker.setTitle(activity.getString(R.string.target)+"\n"+strText);

                if(bUpdate){
                    if(fov_overlay != null) fov_overlay.update_calculations();
                    map.postInvalidate();
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void set_poi_pos(double lon,double lat,double alt,String strName,boolean bUpdate) {
        try
        {
            if(!bIsCreated) return;

            boolean bChanged = (Math.abs(poiPoint.getLongitude() - lon) >= 0.000001) || (Math.abs(poiPoint.getLatitude() - lat) >= 0.000001);
            if(bChanged){
                poiPoint.setLongitude(lon);
                poiPoint.setLatitude(lat);
                poiPoint.setAltitude(alt);

                POI_Marker.setPosition(poiPoint);
                String strText = "Place Name: "+strName+"\n"+
                        String.format(Locale.ENGLISH,"%.06f", lon)+", "+String.format(Locale.ENGLISH,"%.06f", lat)+", "+String.format(Locale.ENGLISH,"%.01f", alt);
                POI_Marker.setTitle("POI point:\n"+strText);

                if(bUpdate) map.postInvalidate();
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    void set_home_pos(double lon,double lat,double alt,boolean bUpdate) {
        try
        {
            if(!bIsCreated) return;
            if(MainActivity.bNavigation)    return;
            MainActivity.home_lon = lon;
            MainActivity.home_lat = lat;
            MainActivity.home_alt = alt;

            boolean bChanged = (Math.abs(homePoint.getLongitude() - lon) >= 0.000001) || (Math.abs(homePoint.getLatitude() - lat) >= 0.000001);
            if(bChanged){
                homePoint.setLongitude(lon);
                homePoint.setLatitude(lat);
                homePoint.setAltitude(alt);
                if(fov_overlay != null) fov_overlay.setHomePos(homePoint);

                try
                {
                    home_Marker.setPosition(homePoint);
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLogInfo("homePoint: "+homePoint.toDoubleString());
                    MainActivity.MyLog(ex);
                }
                String strText = String.format(Locale.ENGLISH,"%.06f", lon)+", "+String.format(Locale.ENGLISH,"%.06f", lat);
                home_Marker.setTitle("Home point: "+strText);

                if(bUpdate){
                    if(fov_overlay != null) fov_overlay.update_calculations();
                    map.postInvalidate();
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    void set_start_pos(double lon,double lat,boolean bUpdate) {
        try
        {
            if(!bIsCreated) return;
            if(MainActivity.bNavigation)    return;

            boolean bChanged = (Math.abs(startPoint.getLongitude() - lon) >= 0.000001) || (Math.abs(startPoint.getLatitude() - lat) >= 0.000001);
            if(bChanged){
                startPoint.setLongitude(lon);
                startPoint.setLatitude(lat);
                startPoint.setAltitude(0.0);

                start_Marker.setPosition(startPoint);
                String strText = String.format(Locale.ENGLISH,"%.06f", lon)+", "+String.format(Locale.ENGLISH,"%.06f", lat);
                start_Marker.setTitle("Start point: "+strText);

                if(bUpdate) map.postInvalidate();
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    void set_camera_fov(float fov,boolean bUpdate) {
        try
        {
            if(!bIsCreated) return;
            if(MainActivity.bNavigation)    return;
            if(fov_overlay != null) {
                if (Math.abs(fov_overlay.getFOV() - fov) <= 0.01) return;
            }

            if(fov_overlay != null) fov_overlay.setFOV(fov);

            if(bUpdate){
                if(fov_overlay != null) fov_overlay.update_calculations();
                map.postInvalidate();
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    void set_camera_azi(float azi,boolean bUpdate)
    {
        try
        {
            if(!bIsCreated) return;
            if(MainActivity.bNavigation)    return;
            if(fov_overlay != null) {
                if (Math.abs(fov_overlay.getAzi() - azi) <= 0.01) return;
            }

            if(fov_overlay != null) fov_overlay.setAzi(azi);

            if(bUpdate){
                if(fov_overlay != null) fov_overlay.update_calculations();
                map.postInvalidate();
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    void set_uav_yaw(float yaw,boolean bUpdate)
    {
        try
        {
            if(!bIsCreated) return;
            if(MainActivity.bNavigation)    return;
            if(fov_overlay != null) {
                if (Math.abs(fov_overlay.getYaw() - yaw) <= 0.01) return;
            }

            if(fov_overlay != null) fov_overlay.setYaw(yaw);

            if(bUpdate){
                if(fov_overlay != null) fov_overlay.update_calculations();
                map.postInvalidate();
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public int get_files_count(String strPath)
    {
        int count = 0;
        File[] fileList = new File(strPath).listFiles();
        if(fileList != null)    count = fileList.length;
        return count;
    }

    public String get_files_count_text(String strPath)
    {
        int count = 0;
        File[] fileList = new File(strPath).listFiles();
        if(fileList != null)    count = fileList.length;
        return Integer.toString(count);
    }

    private void showLimitsDialog(){
        try
        {
            LinearLayout LimitsSettings = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.dialog_limits_setting, null);

            final TextView rth_altitude = LimitsSettings.findViewById(R.id.rth_altitude);
            final TextView max_flight_altitude = LimitsSettings.findViewById(R.id.max_flight_altitude);
            final TextView max_flight_radius = LimitsSettings.findViewById(R.id.max_flight_radius);
            final CheckBox cb_max_flight_radius_enabled = LimitsSettings.findViewById(R.id.cb_max_flight_radius_enabled);

            new AlertDialog.Builder(activity, R.style.CustomAlertDialog)
                    .setTitle("")
                    .setCancelable(false)
                    .setView(LimitsSettings)
                    .setPositiveButton(MainActivity.ctx.getString(R.string.ok), (dialog, id) -> {
                        try
                        {
                            MainActivity.home_altitude = mv_utils.parseInt(nulltoIntegerDefalt(rth_altitude.getText().toString()));
                            MainActivity.max_flight_height = mv_utils.parseInt(nulltoIntegerDefalt(max_flight_altitude.getText().toString()));
                            MainActivity.max_flight_radius = mv_utils.parseInt(nulltoIntegerDefalt(max_flight_radius.getText().toString()));
                            MainActivity.max_flight_radius_enabled = cb_max_flight_radius_enabled.isChecked();

                            Log.e(TAG,"return to home altitude: "+MainActivity.home_altitude);
                            Log.e(TAG,"max flight altitude: "+MainActivity.max_flight_height);
                            Log.e(TAG,"max flight radius: "+MainActivity.max_flight_radius);
                            Log.e(TAG,"max flight radius enabled: "+MainActivity.max_flight_radius_enabled);

                            MainActivity.tab_camera.setLimits();
                            MainActivity.hide_keyboard(LimitsSettings);
                        }
                        catch (Throwable ex)
                        {
                            MainActivity.MyLog(ex);
                        }
                    })
                    .setNegativeButton(MainActivity.ctx.getString(R.string.cancel), (dialog, id) -> {
                        try
                        {
                            MainActivity.hide_keyboard(LimitsSettings);
                            dialog.cancel();
                        }
                        catch (Throwable ex)
                        {
                            MainActivity.MyLog(ex);
                        }
                    })
                    .create()
                    .show();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    static public void update_coordinates(View view,double fLon,double fLat,int type)
    {
        double[] res;
        double X, Y;
        int zone;

        if(type != 0) {
            EditText pos_lon = view.findViewById(R.id.pos_lon);
            EditText pos_lat = view.findViewById(R.id.pos_lat);
            pos_lon.setTag(1);
            pos_lon.setText(String.format(Locale.ENGLISH,"%.06f", fLon));
            pos_lon.setTag(0);

            pos_lat.setTag(1);
            pos_lat.setText(String.format(Locale.ENGLISH,"%.06f", fLat));
            pos_lat.setTag(0);
        }

        if(type != 1) {
            double d,m,s;

            // Lon
            res = MainActivity.Decimal2DMS(fLon);
            d = res[0];
            m = res[1];
            s = res[2];

            EditText pos_lon_d = view.findViewById(R.id.pos_lon_d);
            pos_lon_d.setTag(1);
            pos_lon_d.setText(String.format(Locale.ENGLISH,"%.0f", d));
            pos_lon_d.setTag(0);

            EditText pos_lon_m = view.findViewById(R.id.pos_lon_m);
            pos_lon_m.setTag(1);
            pos_lon_m.setText(String.format(Locale.ENGLISH,"%.0f", m));
            pos_lon_m.setTag(0);

            EditText pos_lon_s = view.findViewById(R.id.pos_lon_s);
            pos_lon_s.setTag(1);
            pos_lon_s.setText(String.format(Locale.ENGLISH,"%.02f", s));
            pos_lon_s.setTag(0);

            // Lat
            res = MainActivity.Decimal2DMS(fLat);
            d = res[0];
            m = res[1];
            s = res[2];

            EditText pos_lat_d = view.findViewById(R.id.pos_lat_d);
            pos_lat_d.setTag(1);
            pos_lat_d.setText(String.format(Locale.ENGLISH,"%.0f", d));
            pos_lat_d.setTag(0);

            EditText pos_lat_m = view.findViewById(R.id.pos_lat_m);
            pos_lat_m.setTag(1);
            pos_lat_m.setText(String.format(Locale.ENGLISH,"%.0f", m));
            pos_lat_m.setTag(0);

            EditText pos_lat_s = view.findViewById(R.id.pos_lat_s);
            pos_lat_s.setTag(1);
            pos_lat_s.setText(String.format(Locale.ENGLISH,"%.02f", s));
            pos_lat_s.setTag(0);
        }

        if(type != 2) {
            int ZoneNumber;
            char UTMChar;
            res = MainActivity.LL2UTM(fLon, fLat);
            X = res[0];
            Y = res[1];
            ZoneNumber = (int)res[2];
            UTMChar = (char)res[3];

            EditText X_utm = view.findViewById(R.id.X_utm);
            X_utm.setTag(1);
            X_utm.setText(String.format(Locale.ENGLISH, "%.01f", X));
            X_utm.setTag(0);

            EditText Y_utm = view.findViewById(R.id.Y_utm);
            Y_utm.setTag(1);
            Y_utm.setText(String.format(Locale.ENGLISH, "%.01f", Y));
            Y_utm.setTag(0);

            EditText zone_utm1 = view.findViewById(R.id.zone_utm1);
            zone_utm1.setTag(1);
            zone_utm1.setText(String.format(Locale.ENGLISH, "%d", ZoneNumber));
            zone_utm1.setTag(0);

            EditText zone_utm2 = view.findViewById(R.id.zone_utm2);
            zone_utm2.setTag(1);
            zone_utm2.setText(Character.toString(UTMChar));
            zone_utm2.setTag(0);
        }

        if(type != 3) {
            res = MainActivity.LL2STM(fLon, fLat);
            X = res[0];
            Y = res[1];
            zone = (int) res[2];

            EditText X_stm = view.findViewById(R.id.X_stm);
            X_stm.setTag(1);
            X_stm.setText(String.format(Locale.ENGLISH, "%.01f", X));
            X_stm.setTag(0);

            EditText Y_stm = view.findViewById(R.id.Y_stm);
            Y_stm.setTag(1);
            Y_stm.setText(String.format(Locale.ENGLISH, "%.01f", Y));
            Y_stm.setTag(0);

            EditText zone_stm = view.findViewById(R.id.zone_stm);
            zone_stm.setTag(1);
            zone_stm.setText(String.format(Locale.ENGLISH, "%d", zone));
            zone_stm.setTag(0);
        }

        if(type != 4) {
            int d,m,s;

            // Lon
            res = MainActivity.Decimal2DMS(fLon);
            d = (int)res[0];
            m = (int)res[1];
            s = (int)res[2];

            NumberPicker pos_lon_degrees = view.findViewById(R.id.pos_lon_degrees);
            pos_lon_degrees.setMinValue(30);
            pos_lon_degrees.setMaxValue(50);
            pos_lon_degrees.setTag(1);
            pos_lon_degrees.setValue(d);
            pos_lon_degrees.setTag(0);

            NumberPicker pos_lon_minutes = view.findViewById(R.id.pos_lon_minutes);
            pos_lon_minutes.setMinValue(0);
            pos_lon_minutes.setMaxValue(59);
            pos_lon_minutes.setTag(1);
            pos_lon_minutes.setValue(m);
            pos_lon_minutes.setTag(0);

            NumberPicker pos_lon_seconds = view.findViewById(R.id.pos_lon_seconds);
            pos_lon_seconds.setMinValue(0);
            pos_lon_seconds.setMaxValue(59);
            pos_lon_seconds.setTag(1);
            pos_lon_seconds.setValue(s);
            pos_lon_seconds.setTag(0);

            // Lat
            res = MainActivity.Decimal2DMS(fLat);
            d = (int)res[0];
            m = (int)res[1];
            s = (int)res[2];

            NumberPicker pos_lat_degrees = view.findViewById(R.id.pos_lat_degrees);
            pos_lat_degrees.setMinValue(28);
            pos_lat_degrees.setMaxValue(38);
            pos_lat_degrees.setTag(1);
            pos_lat_degrees.setValue(d);
            pos_lat_degrees.setTag(0);

            NumberPicker pos_lat_minutes = view.findViewById(R.id.pos_lat_minutes);
            pos_lat_minutes.setMinValue(0);
            pos_lat_minutes.setMaxValue(59);
            pos_lat_minutes.setTag(1);
            pos_lat_minutes.setValue(m);
            pos_lat_minutes.setTag(0);

            NumberPicker pos_lat_seconds = view.findViewById(R.id.pos_lat_seconds);
            pos_lat_seconds.setMinValue(0);
            pos_lat_seconds.setMaxValue(59);
            pos_lat_seconds.setTag(1);
            pos_lat_seconds.setValue(s);
            pos_lat_seconds.setTag(0);
        }

        if(type != 5){
            ProjCoordinate result = Proj.wgs84_to_sk42(fLon, fLat);
            X = result.x;
            Y = result.y;

            EditText lon_sk42 = view.findViewById(R.id.lon_sk42);
            lon_sk42.setTag(1);
            lon_sk42.setText(String.format(Locale.ENGLISH, "%.06f", X));
            lon_sk42.setTag(0);

            EditText lat_sk42 = view.findViewById(R.id.lat_sk42);
            lat_sk42.setTag(1);
            lat_sk42.setText(String.format(Locale.ENGLISH, "%.06f", Y));
            lat_sk42.setTag(0);
        }

        if(type != 6){
            ProjCoordinate result = Proj.wgs84_to_gauss_kruger(fLon, fLat);
            X = result.x;
            Y = result.y;
            zone = (int)result.z;

            EditText X_sk42 = view.findViewById(R.id.X_sk42);
            X_sk42.setTag(1);
            X_sk42.setText(String.format(Locale.ENGLISH, "%.01f", X));
            X_sk42.setTag(0);

            EditText Y_sk42 = view.findViewById(R.id.Y_sk42);
            Y_sk42.setTag(1);
            Y_sk42.setText(String.format(Locale.ENGLISH, "%.01f", Y));
            Y_sk42.setTag(0);

            EditText zone_sk42 = view.findViewById(R.id.zone_sk42);
            zone_sk42.setTag(1);
            zone_sk42.setText(String.format(Locale.ENGLISH, "%d", zone));
            zone_sk42.setTag(0);
        }
    }

    private double fLon_pos,fLat_pos,fAlt_pos;
    private void showInsertPosDialog(){
        try
        {
            final LinearLayout InsertPosLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.dialog_insert_pos, null);
            final CheckBox sw_auto_convert = InsertPosLayout.findViewById(R.id.sw_auto_convert);

//            fLon_pos = 36.306947;
//            fLat_pos = 33.511725;
            IGeoPoint center = Tab_Map.map.getMapCenter();
            fLon_pos = center.getLongitude();
            fLat_pos = center.getLatitude();
            update_coordinates(InsertPosLayout,fLon_pos,fLat_pos,-1);

            // Geographic decimal coordinates
            final EditText pos_lon = InsertPosLayout.findViewById(R.id.pos_lon);
            final EditText pos_lat = InsertPosLayout.findViewById(R.id.pos_lat);
            final Button b_convert_decimal = InsertPosLayout.findViewById(R.id.b_convert_decimal);
            b_convert_decimal.setVisibility(View.GONE);
            b_convert_decimal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        fLon_pos = mv_utils.parseDouble(pos_lon.getText().toString());
                        fLat_pos = mv_utils.parseDouble(pos_lat.getText().toString());
                        update_coordinates(InsertPosLayout,fLon_pos,fLat_pos,0);
                        b_convert_decimal.setVisibility(View.GONE);
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                }
            });
            TextWatcher dec_watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try
                    {
                        if((int)pos_lon.getTag() == 1)   return;
                        if((int)pos_lat.getTag() == 1)   return;
                        if(sw_auto_convert.isChecked()){
                            b_convert_decimal.setVisibility(View.GONE);
                            fLon_pos = mv_utils.parseDouble(pos_lon.getText().toString());
                            fLat_pos = mv_utils.parseDouble(pos_lat.getText().toString());
                            update_coordinates(InsertPosLayout,fLon_pos,fLat_pos,0);
                        }else{
                            b_convert_decimal.setVisibility(View.VISIBLE);
                        }
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                }
            };
            pos_lon.addTextChangedListener(dec_watcher);
            pos_lat.addTextChangedListener(dec_watcher);

            // Geographic DMS coordinates
            final EditText pos_lon_d = InsertPosLayout.findViewById(R.id.pos_lon_d);
            final EditText pos_lon_m = InsertPosLayout.findViewById(R.id.pos_lon_m);
            final EditText pos_lon_s = InsertPosLayout.findViewById(R.id.pos_lon_s);
            final EditText pos_lat_d = InsertPosLayout.findViewById(R.id.pos_lat_d);
            final EditText pos_lat_m = InsertPosLayout.findViewById(R.id.pos_lat_m);
            final EditText pos_lat_s = InsertPosLayout.findViewById(R.id.pos_lat_s);
            final Button b_convert_dms = InsertPosLayout.findViewById(R.id.b_convert_dms);
            b_convert_dms.setVisibility(View.GONE);
            b_convert_dms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        double lon_d,lon_m,lon_s,lat_d,lat_m,lat_s;

                        lon_d = mv_utils.parseDouble(pos_lon_d.getText().toString());
                        lon_m = mv_utils.parseDouble(pos_lon_m.getText().toString());
                        lon_s = mv_utils.parseDouble(pos_lon_s.getText().toString());

                        lat_d = mv_utils.parseDouble(pos_lat_d.getText().toString());
                        lat_m = mv_utils.parseDouble(pos_lat_m.getText().toString());
                        lat_s = mv_utils.parseDouble(pos_lat_s.getText().toString());

                        fLon_pos = MainActivity.DMS2Decimal(lon_d,lon_m,lon_s);
                        fLat_pos = MainActivity.DMS2Decimal(lat_d,lat_m,lat_s);

                        update_coordinates(InsertPosLayout,fLon_pos,fLat_pos,1);
                        b_convert_dms.setVisibility(View.GONE);
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                }
            });
            TextWatcher dms_watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try
                    {
                        if((int)pos_lon_d.getTag() == 1)   return;
                        if((int)pos_lon_m.getTag() == 1)   return;
                        if((int)pos_lon_s.getTag() == 1)   return;
                        if((int)pos_lat_d.getTag() == 1)   return;
                        if((int)pos_lat_m.getTag() == 1)   return;
                        if((int)pos_lat_s.getTag() == 1)   return;
                        if(sw_auto_convert.isChecked()){
                            b_convert_dms.setVisibility(View.GONE);
                            double lon_d,lon_m,lon_s,lat_d,lat_m,lat_s;

                            lon_d = mv_utils.parseDouble(pos_lon_d.getText().toString());
                            lon_m = mv_utils.parseDouble(pos_lon_m.getText().toString());
                            lon_s = mv_utils.parseDouble(pos_lon_s.getText().toString());

                            lat_d = mv_utils.parseDouble(pos_lat_d.getText().toString());
                            lat_m = mv_utils.parseDouble(pos_lat_m.getText().toString());
                            lat_s = mv_utils.parseDouble(pos_lat_s.getText().toString());

                            fLon_pos = MainActivity.DMS2Decimal(lon_d,lon_m,lon_s);
                            fLat_pos = MainActivity.DMS2Decimal(lat_d,lat_m,lat_s);

                            update_coordinates(InsertPosLayout,fLon_pos,fLat_pos,1);
                        }else{
                            b_convert_dms.setVisibility(View.VISIBLE);
                        }
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                }
            };
            pos_lon_d.addTextChangedListener(dms_watcher);
            pos_lon_m.addTextChangedListener(dms_watcher);
            pos_lon_s.addTextChangedListener(dms_watcher);
            pos_lat_d.addTextChangedListener(dms_watcher);
            pos_lat_m.addTextChangedListener(dms_watcher);
            pos_lat_s.addTextChangedListener(dms_watcher);

            // Geographic DMS coordinates Spinners
            final NumberPicker pos_lon_degrees = InsertPosLayout.findViewById(R.id.pos_lon_degrees);
            final NumberPicker pos_lon_minutes = InsertPosLayout.findViewById(R.id.pos_lon_minutes);
            final NumberPicker pos_lon_seconds = InsertPosLayout.findViewById(R.id.pos_lon_seconds);
            final NumberPicker pos_lat_degrees = InsertPosLayout.findViewById(R.id.pos_lat_degrees);
            final NumberPicker pos_lat_minutes = InsertPosLayout.findViewById(R.id.pos_lat_minutes);
            final NumberPicker pos_lat_seconds = InsertPosLayout.findViewById(R.id.pos_lat_seconds);
            final Button b_convert_dms2 = InsertPosLayout.findViewById(R.id.b_convert_dms2);
            b_convert_dms2.setVisibility(View.GONE);
            b_convert_dms2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        double lon_d,lon_m,lon_s,lat_d,lat_m,lat_s;

                        lon_d = pos_lon_degrees.getValue();
                        lon_m = pos_lon_minutes.getValue();
                        lon_s = pos_lon_seconds.getValue();

                        lat_d = pos_lat_degrees.getValue();
                        lat_m = pos_lat_minutes.getValue();
                        lat_s = pos_lat_seconds.getValue();

                        fLon_pos = MainActivity.DMS2Decimal(lon_d,lon_m,lon_s);
                        fLat_pos = MainActivity.DMS2Decimal(lat_d,lat_m,lat_s);

                        update_coordinates(InsertPosLayout,fLon_pos,fLat_pos,4);
                        b_convert_dms2.setVisibility(View.GONE);
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                }
            });

            final NumberPicker.OnValueChangeListener onValueChangedListener = new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    try
                    {
                        if((int)pos_lon_degrees.getTag() == 1)   return;
                        if((int)pos_lon_minutes.getTag() == 1)   return;
                        if((int)pos_lon_seconds.getTag() == 1)   return;
                        if((int)pos_lat_degrees.getTag() == 1)   return;
                        if((int)pos_lat_minutes.getTag() == 1)   return;
                        if((int)pos_lat_seconds.getTag() == 1)   return;
                        if(sw_auto_convert.isChecked()) {
                            b_convert_dms2.setVisibility(View.GONE);
                            double lon_d, lon_m, lon_s, lat_d, lat_m, lat_s;

                            lon_d = pos_lon_degrees.getValue();
                            lon_m = pos_lon_minutes.getValue();
                            lon_s = pos_lon_seconds.getValue();

                            lat_d = pos_lat_degrees.getValue();
                            lat_m = pos_lat_minutes.getValue();
                            lat_s = pos_lat_seconds.getValue();

                            fLon_pos = MainActivity.DMS2Decimal(lon_d, lon_m, lon_s);
                            fLat_pos = MainActivity.DMS2Decimal(lat_d, lat_m, lat_s);

                            update_coordinates(InsertPosLayout, fLon_pos, fLat_pos, 4);
                        }else{
                            b_convert_dms2.setVisibility(View.VISIBLE);
                        }
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                }
            };

            pos_lon_degrees.setOnValueChangedListener(onValueChangedListener);
            pos_lon_minutes.setOnValueChangedListener(onValueChangedListener);
            pos_lon_seconds.setOnValueChangedListener(onValueChangedListener);
            pos_lat_degrees.setOnValueChangedListener(onValueChangedListener);
            pos_lat_minutes.setOnValueChangedListener(onValueChangedListener);
            pos_lat_seconds.setOnValueChangedListener(onValueChangedListener);

            // UTM coordinates
            final EditText X_utm = InsertPosLayout.findViewById(R.id.X_utm);
            final EditText Y_utm = InsertPosLayout.findViewById(R.id.Y_utm);
            final EditText zone_utm1 = InsertPosLayout.findViewById(R.id.zone_utm1);
            final EditText zone_utm2 = InsertPosLayout.findViewById(R.id.zone_utm2);
            final Button b_convert_utm = InsertPosLayout.findViewById(R.id.b_convert_utm);
            b_convert_utm.setVisibility(View.GONE);
            b_convert_utm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        double fX,fY;
                        fX = mv_utils.parseDouble(X_utm.getText().toString());
                        fY = mv_utils.parseDouble(Y_utm.getText().toString());
                        String strZone1 = zone_utm1.getText().toString();
                        String strZone2 = zone_utm2.getText().toString();
                        String strZone = strZone1+strZone2.charAt(0);
                        double[] res = MainActivity.UTM2LL(fX, fY, strZone);
                        fLon_pos = res[0];
                        fLat_pos = res[1];
                        update_coordinates(InsertPosLayout,fLon_pos,fLat_pos,2);
                        b_convert_utm.setVisibility(View.GONE);
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                }
            });

            InputFilter[] digitsfilters = new InputFilter[1];
            digitsfilters[0] = (source, start, end, dest, dstart, dend) -> {
                if (end > start) {

//                        char[] acceptedChars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
                    char[] acceptedChars = new char[]{'C','D','E','F','G','H','J','K','L','M','N','P','Q','R','S','T','U','V','W','X'};

                    for (int index = start; index < end; index++) {
                        if (!new String(acceptedChars).contains(String.valueOf(source.charAt(index)))) {
                            return "";
                        }
                    }
                }
                return null;
            };
            zone_utm2.setFilters(digitsfilters);

            TextWatcher utm_watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try
                    {
                        if((int)X_utm.getTag() == 1)   return;
                        if((int)Y_utm.getTag() == 1)   return;
                        if((int)zone_utm1.getTag() == 1)   return;
                        if((int)zone_utm2.getTag() == 1)   return;
                        if(sw_auto_convert.isChecked()) {
                            b_convert_utm.setVisibility(View.GONE);
                            double fX, fY;
                            fX = mv_utils.parseDouble(X_utm.getText().toString());
                            fY = mv_utils.parseDouble(Y_utm.getText().toString());
                            String strZone1 = zone_utm1.getText().toString();
                            String strZone2 = zone_utm2.getText().toString();
                            String strZone = strZone1 + strZone2.charAt(0);
                            double[] res = MainActivity.UTM2LL(fX, fY, strZone);
                            fLon_pos = res[0];
                            fLat_pos = res[1];
                            update_coordinates(InsertPosLayout, fLon_pos, fLat_pos, 2);
                        }else{
                            b_convert_utm.setVisibility(View.VISIBLE);
                        }
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                }
            };
            X_utm.addTextChangedListener(utm_watcher);
            Y_utm.addTextChangedListener(utm_watcher);
            zone_utm1.addTextChangedListener(utm_watcher);
            zone_utm2.addTextChangedListener(utm_watcher);

            // STM coordinates
            final EditText X_stm = InsertPosLayout.findViewById(R.id.X_stm);
            final EditText Y_stm = InsertPosLayout.findViewById(R.id.Y_stm);
            final EditText zone_stm = InsertPosLayout.findViewById(R.id.zone_stm);
            final Button b_convert_stm = InsertPosLayout.findViewById(R.id.b_convert_stm);
            b_convert_stm.setVisibility(View.GONE);
            b_convert_stm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        double fX,fY;
                        fX = mv_utils.parseDouble(X_stm.getText().toString());
                        fY = mv_utils.parseDouble(Y_stm.getText().toString());
                        String strZone = zone_stm.getText().toString();
                        int zone = mv_utils.parseInt(strZone);
                        double[] res = MainActivity.STM2LL(fX, fY, zone);
                        fLon_pos = res[0];
                        fLat_pos = res[1];
                        update_coordinates(InsertPosLayout,fLon_pos,fLat_pos,3);
                        b_convert_stm.setVisibility(View.GONE);
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                }
            });
            TextWatcher stm_watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try
                    {
                        if((int)X_stm.getTag() == 1)   return;
                        if((int)Y_stm.getTag() == 1)   return;
                        if((int)zone_stm.getTag() == 1)   return;
                        if(sw_auto_convert.isChecked()) {
                            b_convert_stm.setVisibility(View.GONE);
                            double fX, fY;
                            fX = mv_utils.parseDouble(X_stm.getText().toString());
                            fY = mv_utils.parseDouble(Y_stm.getText().toString());
                            String strZone = zone_stm.getText().toString();
                            int zone = mv_utils.parseInt(strZone);
                            double[] res = MainActivity.STM2LL(fX, fY, zone);
                            fLon_pos = res[0];
                            fLat_pos = res[1];
                            update_coordinates(InsertPosLayout, fLon_pos, fLat_pos, 3);
                        }else{
                            b_convert_stm.setVisibility(View.VISIBLE);
                        }
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                }
            };
            X_stm.addTextChangedListener(stm_watcher);
            Y_stm.addTextChangedListener(stm_watcher);
            zone_stm.addTextChangedListener(stm_watcher);

            // SK42 coordinates
            final EditText lon_sk42 = InsertPosLayout.findViewById(R.id.lon_sk42);
            final EditText lat_sk42 = InsertPosLayout.findViewById(R.id.lat_sk42);
            final Button b_convert_sk42 = InsertPosLayout.findViewById(R.id.b_convert_sk42);
            b_convert_sk42.setVisibility(View.GONE);
            b_convert_sk42.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        double fX,fY;
                        fX = mv_utils.parseDouble(lon_sk42.getText().toString());
                        fY = mv_utils.parseDouble(lat_sk42.getText().toString());

                        ProjCoordinate result = Proj.sk42_to_wgs84(fX, fY);
                        fLon_pos = result.x;
                        fLat_pos = result.y;

                        update_coordinates(InsertPosLayout,fLon_pos,fLat_pos,5);
                        b_convert_sk42.setVisibility(View.GONE);
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                }
            });
            TextWatcher sk42_watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try
                    {
                        if((int)lon_sk42.getTag() == 1)   return;
                        if((int)lat_sk42.getTag() == 1)   return;
                        if(sw_auto_convert.isChecked()) {
                            b_convert_sk42.setVisibility(View.GONE);
                            double fX, fY;
                            fX = mv_utils.parseDouble(lon_sk42.getText().toString());
                            fY = mv_utils.parseDouble(lat_sk42.getText().toString());

                            ProjCoordinate result = Proj.sk42_to_wgs84(fX, fY);
                            fLon_pos = result.x;
                            fLat_pos = result.y;

                            update_coordinates(InsertPosLayout, fLon_pos, fLat_pos, 5);
                        }else{
                            b_convert_sk42.setVisibility(View.VISIBLE);
                        }
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                }
            };
            lon_sk42.addTextChangedListener(sk42_watcher);
            lat_sk42.addTextChangedListener(sk42_watcher);

            // SK42 Gauss Krueger (6 degrees zones) coordinates
            final EditText X_sk42 = InsertPosLayout.findViewById(R.id.X_sk42);
            final EditText Y_sk42 = InsertPosLayout.findViewById(R.id.Y_sk42);
            final EditText zone_sk42 = InsertPosLayout.findViewById(R.id.zone_sk42);
            final Button b_convert_sk42GK = InsertPosLayout.findViewById(R.id.b_convert_sk42GK);
            b_convert_sk42GK.setVisibility(View.GONE);
            b_convert_sk42GK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        double fX,fY;
                        fX = mv_utils.parseDouble(X_sk42.getText().toString());
                        fY = mv_utils.parseDouble(Y_sk42.getText().toString());
                        String strZone = zone_sk42.getText().toString();
                        int zone = mv_utils.parseInt(strZone);

                        ProjCoordinate result = Proj.gauss_kruger_to_wgs84(fX, fY, zone, fLat_pos > 0);

                        fLon_pos = result.x;
                        fLat_pos = result.y;

                        update_coordinates(InsertPosLayout,fLon_pos,fLat_pos,6);
                        b_convert_sk42GK.setVisibility(View.GONE);
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                }
            });
            TextWatcher sk42_gk_watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try
                    {
                        if((int)X_sk42.getTag() == 1)   return;
                        if((int)Y_sk42.getTag() == 1)   return;
                        if((int)zone_sk42.getTag() == 1)   return;
                        if(sw_auto_convert.isChecked()) {
                            b_convert_sk42GK.setVisibility(View.GONE);
                            double fX, fY;
                            fX = mv_utils.parseDouble(X_sk42.getText().toString());
                            fY = mv_utils.parseDouble(Y_sk42.getText().toString());
                            String strZone = zone_sk42.getText().toString();
                            int zone = mv_utils.parseInt(strZone);

                            ProjCoordinate result = Proj.gauss_kruger_to_wgs84(fX, fY, zone, fLat_pos > 0);

                            fLon_pos = result.x;
                            fLat_pos = result.y;

                            update_coordinates(InsertPosLayout, fLon_pos, fLat_pos, 6);
                        }else{
                            b_convert_sk42GK.setVisibility(View.VISIBLE);
                        }
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                }
            };
            X_sk42.addTextChangedListener(sk42_gk_watcher);
            Y_sk42.addTextChangedListener(sk42_gk_watcher);
            zone_sk42.addTextChangedListener(sk42_gk_watcher);

            sw_auto_convert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        if(sw_auto_convert.isChecked()){
                            b_convert_decimal.setVisibility(View.GONE);
                            b_convert_dms.setVisibility(View.GONE);
                            b_convert_dms2.setVisibility(View.GONE);
                            b_convert_utm.setVisibility(View.GONE);
                            b_convert_stm.setVisibility(View.GONE);
                            b_convert_sk42.setVisibility(View.GONE);
                            b_convert_sk42GK.setVisibility(View.GONE);
                        }
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                }
            });

            new AlertDialog.Builder(activity, R.style.CustomAlertDialog)
                .setTitle("")
                .setCancelable(false)
                .setView(InsertPosLayout)
                .setPositiveButton(MainActivity.ctx.getString(R.string.ok), (dialog, id) -> {
                    try
                    {
                        fLon_pos = mv_utils.parseDouble(pos_lon.getText().toString());
                        fLat_pos = mv_utils.parseDouble(pos_lat.getText().toString());
                        fAlt_pos = MainActivity.GetHeightJNI(fLon_pos,fLat_pos);
//                        set_poi_pos(fLon_pos, fLat_pos, fAlt_pos, "",true);
//                        mapController.setCenter(poiPoint);
                        GeoPoint p = new GeoPoint(fLat_pos,fLon_pos,fAlt_pos);
                        enter_point(p);
                        mapController.setCenter(p);
                        mapController.setZoom(17.0);
                        MainActivity.hide_keyboard(InsertPosLayout);
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                })
                .setNegativeButton(MainActivity.ctx.getString(R.string.cancel), (dialog, id) -> {
                    try
                    {
                        MainActivity.hide_keyboard(InsertPosLayout);
                        dialog.cancel();
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                })
                .create()
                .show();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    private String nulltoIntegerDefalt(String value){
        if(!isIntValue(value)) value="0";
        return value;
    }

    private boolean isIntValue(String val) {
        try {
            val=val.replace(" ","");
            mv_utils.parseInt(val);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
            return false;
        }
        return true;
    }

    private void showResultToast(DJIError djiError) {
        try
        {
            setResultToToast(djiError == null ? "Action started!" : djiError.getDescription());
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public static class ProjectileSettings
    {
        static double gun_lon0, gun_lat0, target_lon0, target_lat0;
        static int iterations;
        static double z0, time_step, velocity0 = 80, angle0 = 0, diameter0 = 0.1, mass0 = 10, wind0 = 0, error, dencity0, cofficient0, temp0, gravity0, alt0 = 5000;
        static boolean const_gravity0;
        static boolean auto_calculate, auto_update;
        static int color;
    }

    public static class ProjectileLineDialogFragment extends DialogFragment {
        /** The system calls this only when creating the layout in a dialog. */
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
            // Get the layout inflater
            LayoutInflater inflater = requireActivity().getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            View mission_dialog = inflater.inflate(R.layout.projectile_settings, null);

            EditText e_projectile_altitude,e_projectile_velocity,e_projectile_angle,e_projectile_wind,e_projectile_mass,e_projectile_diameter;
            CheckBox cb_auto_projectile,cb_auto_update;
            e_projectile_altitude = mission_dialog.findViewById(R.id.e_projectile_altitude);
            e_projectile_velocity = mission_dialog.findViewById(R.id.e_projectile_velocity);
            e_projectile_angle = mission_dialog.findViewById(R.id.e_projectile_angle);
            e_projectile_wind = mission_dialog.findViewById(R.id.e_projectile_wind);
            e_projectile_mass = mission_dialog.findViewById(R.id.e_projectile_mass);
            e_projectile_diameter = mission_dialog.findViewById(R.id.e_projectile_diameter);
            cb_auto_projectile = mission_dialog.findViewById(R.id.cb_auto_projectile);
            cb_auto_update = mission_dialog.findViewById(R.id.cb_auto_update);

            e_projectile_altitude.setText(String.valueOf(ProjectileSettings.alt0));
            e_projectile_velocity.setText(String.valueOf(ProjectileSettings.velocity0));
            e_projectile_angle.setText(String.valueOf(ProjectileSettings.angle0));
            e_projectile_wind.setText(String.valueOf(ProjectileSettings.wind0));
            e_projectile_mass.setText(String.valueOf(ProjectileSettings.mass0));
            e_projectile_diameter.setText(String.valueOf(ProjectileSettings.diameter0));
            cb_auto_projectile.setChecked(ProjectileSettings.auto_calculate);
            cb_auto_update.setChecked(Tab_Map.ProjectileSettings.auto_update);

            Button b_ok = mission_dialog.findViewById(R.id.b_ok);
            b_ok.setOnClickListener(v -> {
                try
                {
                    try {
                        if(target_Marker != null){
                            GeoPoint target_point = target_Marker.getPosition();
                            Location location = mv_LocationOverlay.curr_location;
                            if(location != null) {
                                ProjectileSettings.iterations = 10;
                                ProjectileSettings.time_step = 0.1;
                                ProjectileSettings.error = 1.0;
                                ProjectileSettings.dencity0 = 1.293;
                                ProjectileSettings.cofficient0 = 0.1;
                                ProjectileSettings.temp0 = 300;
                                ProjectileSettings.gravity0 = 9.82;
                                ProjectileSettings.const_gravity0 = true;

                                ProjectileSettings.gun_lon0 = location.getLongitude();
                                ProjectileSettings.gun_lat0 = location.getLatitude();
                                ProjectileSettings.target_lon0 = target_point.getLongitude();
                                ProjectileSettings.target_lat0 = target_point.getLatitude();

                                ProjectileSettings.z0 = mv_utils.parseDouble(e_projectile_altitude.getText().toString());
                                ProjectileSettings.alt0 = ProjectileSettings.z0;
                                ProjectileSettings.velocity0 = mv_utils.parseDouble(e_projectile_velocity.getText().toString());
                                ProjectileSettings.angle0 = mv_utils.parseDouble(e_projectile_angle.getText().toString());
                                ProjectileSettings.wind0 = mv_utils.parseDouble(e_projectile_wind.getText().toString());
                                ProjectileSettings.mass0 = mv_utils.parseDouble(e_projectile_mass.getText().toString());
                                ProjectileSettings.diameter0 = mv_utils.parseDouble(e_projectile_diameter.getText().toString());
                                ProjectileSettings.auto_calculate = cb_auto_projectile.isChecked();
                                ProjectileSettings.auto_update = cb_auto_update.isChecked();
                                ProjectileSettings.color = Color.BLUE;

                                if(projectile_line(ProjectileSettings.gun_lon0, ProjectileSettings.gun_lat0, ProjectileSettings.target_lon0, ProjectileSettings.target_lat0,ProjectileSettings.iterations,ProjectileSettings.z0,ProjectileSettings.time_step,ProjectileSettings.velocity0,ProjectileSettings.angle0,ProjectileSettings.diameter0,ProjectileSettings.mass0,ProjectileSettings.wind0,ProjectileSettings.error,ProjectileSettings.dencity0,ProjectileSettings.cofficient0,ProjectileSettings.temp0,ProjectileSettings.gravity0,ProjectileSettings.const_gravity0))
                                    Tab_Messenger.showToast("Projectile calculated...");
                                else
                                    Tab_Messenger.showToast("Projectile not calculated...!");
                            }
                        }
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }

                    // Hide both the navigation bar and the status bar.
                    MainActivity.hide_keyboard(mission_dialog);

                    Objects.requireNonNull(ProjectileLineDialogFragment.this.getDialog()).dismiss();
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            });

            Button b_cancel = mission_dialog.findViewById(R.id.b_cancel);
            b_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Hide both the navigation bar and the status bar.
                    MainActivity.hide_keyboard(mission_dialog);

                    Objects.requireNonNull(ProjectileLineDialogFragment.this.getDialog()).dismiss();
                }
            });

            builder.setView(mission_dialog);
            return builder.create();
        }
    }

    private void show_projectile_settings()
    {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        ProjectileLineDialogFragment newFragment = new ProjectileLineDialogFragment();

        // The device is using a large layout, so show the fragment as a dialog
        newFragment.show(fragmentManager, "dialog");
    }

    public static class ProjectileChartFragment extends DialogFragment {
        LineChart lineChart;
        LineData lineData;
        LineDataSet lineDataSet;
        ArrayList<Entry> lineEntries = null;

        private void getEntries() {
            lineEntries = new ArrayList<>();
            try {
                File csvfile = new File(MainActivity.strProjectileFile);
                CSVReader reader = new CSVReader(new FileReader(csvfile.getAbsolutePath()));
                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    lineEntries.add(new Entry(Float.parseFloat(nextLine[0]), Float.parseFloat(nextLine[1])));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Tab_Messenger.showToast("["+MainActivity.strProjectileFile+"] Not found.");
            }
        }

        /** The system calls this only when creating the layout in a dialog. */
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
            // Get the layout inflater
            LayoutInflater inflater = requireActivity().getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            View mission_dialog = inflater.inflate(R.layout.projectile_chart, null);

            lineChart = mission_dialog.findViewById(R.id.lineChart);
            getEntries();
            lineDataSet = new LineDataSet(lineEntries, "");
            lineData = new LineData(lineDataSet);
            lineChart.setData(lineData);
            lineDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            lineDataSet.setValueTextColor(Color.BLACK);

            lineDataSet.setValueTextSize(18f);

            Button b_ok = mission_dialog.findViewById(R.id.b_ok);
            b_ok.setOnClickListener(v -> {
                try
                {
                    // Hide both the navigation bar and the status bar.
                    MainActivity.hide_keyboard(mission_dialog);

                    Objects.requireNonNull(ProjectileChartFragment.this.getDialog()).dismiss();
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            });

            builder.setView(mission_dialog);
            return builder.create();
        }
    }

    private void show_projectile_chart()
    {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        ProjectileChartFragment newFragment = new ProjectileChartFragment();

        // The device is using a large layout, so show the fragment as a dialog
        newFragment.show(fragmentManager, "dialog");
    }

    // isVirtual: true => from Virtual Sensors
    // isVirtual: false => from Simulating
    public void update_gps(IGeoPoint p, boolean autoCalculation, boolean isVirtual){
//        GpsmvLocationProvider locationProvider = (GpsmvLocationProvider) mLocationOverlay.getMyLocationProvider();
//        Location location0 = locationProvider.getLastKnownLocation();

        Location location0 = mv_LocationOverlay.curr_location;

        double fLon,fLat;
//        IGeoPoint mapCenter = map.getMapCenter();
//        fLon = mapCenter.getLongitude();
//        fLat = mapCenter.getLatitude();
        fLon = p.getLongitude();
        fLat = p.getLatitude();
        float fAlt = MainActivity.GetHeightJNI(fLon,fLat);
        long time = System.currentTimeMillis();
        float bearing,speed;
        if(location0 != null){
            if(autoCalculation || cb_auto.isChecked()){
                float[] list = new float[2];
                Location.distanceBetween(location0.getLatitude(), location0.getLongitude(), fLat, fLon, list);
                float dist = list[0];
                if(dist > 0){
                    bearing = (float) MainActivity.db_deg(list[1]);
                    float dt = (time - location0.getTime())/1000.0f;
                    speed = dist/dt;
                }else{
                    bearing = location0.getBearing();
                    speed = location0.getSpeed();
                }
            }else{
                bearing = sb_gps_yaw.getProgress();
                speed = sb_gps_speed.getProgress();
            }
        }else{
            bearing = sb_gps_yaw.getProgress();
            speed = sb_gps_speed.getProgress();
        }
        float alt = sb_gps_alt.getProgress();

        Location location = SerializableLocation.getLocation(fLon,fLat,fAlt + alt,speed,bearing);
        location.setTime(time);
        GpsmvLocationProvider locationProvider = (GpsmvLocationProvider) mLocationOverlay.getMyLocationProvider();
        if(locationProvider != null) {
            Bundle extraBundle = new Bundle();
            extraBundle.putBoolean("isMock",true);
            extraBundle.putBoolean("isKalman",false);
            extraBundle.putBoolean("isVirtual",isVirtual);
            location.setExtras(extraBundle);

            locationProvider.onLocationChanged(location);
        }
    }

    private final Handler customHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));
    private final Runnable updateTimerThread = new Runnable() {
        public void run() {
            try
            {
                if (cb_auto.isChecked()) {
                    update_gps(map.getMapCenter(), false, true);
                }

                customHandler.postDelayed(this, mv_utils.parseInt(e_update_interval_sec.getText().toString())* 1000L);
            }
            catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
                customHandler.postDelayed(this, mv_utils.parseInt(e_update_interval_sec.getText().toString())* 1000L);
            }
        }
    };

    private void finish_polyline(){
        if((g_points != null) && (g_points.size() > 0)) {
            LinearLayout newPlacemarkLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.new_placemark_dialog, null);
            EditText et_name = newPlacemarkLayout.findViewById(R.id.et_name);
            RadioButton rb_general = newPlacemarkLayout.findViewById(R.id.rb_general);
            RadioButton rb_special = newPlacemarkLayout.findViewById(R.id.rb_special);
            RadioButton rb_weapon = newPlacemarkLayout.findViewById(R.id.rb_weapon);

            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialog);
            builder.setTitle(activity.getString(R.string.add_path));
            builder.setCancelable(false);
            builder.setView(newPlacemarkLayout);
            builder.setPositiveButton(MainActivity.ctx.getString(R.string.ok), (dialog, which) -> {
                try
                {
                    // polyline simplification
                    g_points = PointReducer.reduceWithTolerance(g_points,0.000001);
                    g_polyline.setPoints(g_points);

                    int type = -1;
                    if(rb_general.isChecked())  type = 0;
                    if(rb_special.isChecked())  type = 1;
                    if(rb_weapon.isChecked())  type = 2;

                    KmlPlacemark placemark = new KmlPlacemark(g_polyline, kmlFavoritesDocument);
                    placemark.mName = et_name.getText().toString();
                    placemark.setExtendedData("type",String.valueOf(type));

                    // Add item to adapter
                    GeoPoint p = g_points.get(0);
                    City city = new City();
                    city.strName = placemark.mName;
                    city.fLon = p.getLongitude();
                    city.fLat = p.getLatitude();
                    city.fAlt = (float)p.getAltitude();
                    city.geometry_type = City.POLYLINE;// Path
                    city.index = Tab_Map.favorites_adapter.getCount();
                    city.placemark = placemark;
                    city.placemark.overlay = g_polyline;
                    Tab_Map.favorites_adapter.add(city);

                    g_polyline.setInfoWindow(new CustomInfoWindow(type, map, g_polyline, placemark, city));
                    kmlFavoritesDocument.mKmlRoot.mItems.add(placemark);
                    kmlFavoritesDocument.saveAsKML(favoritesFile);

                    g_points.clear();
                    map.postInvalidate();
                    g_polyline = null;

                    MainActivity.hide_keyboard(newPlacemarkLayout);
                    Tab_Messenger.showToast(activity.getString(R.string.path_added_to_favorites));
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            });
            builder.setNegativeButton(MainActivity.ctx.getString(R.string.cancel), (dialog, which) -> {
                try
                {
                    g_points.clear();
                    g_polyline.setPoints(g_points);
                    kmlOverlay.invalidate();
                    map.postInvalidate();
                    g_polyline = null;

                    MainActivity.hide_keyboard(newPlacemarkLayout);
                    dialog.cancel();
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            });
            builder.show();
        }
    }

    private void finish_polygon(){
        if((g_points != null) && (g_points.size() > 0)) {
            LinearLayout newPlacemarkLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.new_placemark_dialog, null);
            EditText et_name = newPlacemarkLayout.findViewById(R.id.et_name);
            RadioButton rb_general = newPlacemarkLayout.findViewById(R.id.rb_general);
            RadioButton rb_special = newPlacemarkLayout.findViewById(R.id.rb_special);
            RadioButton rb_weapon = newPlacemarkLayout.findViewById(R.id.rb_weapon);

            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialog);
            builder.setTitle(activity.getString(R.string.add_polygon));
            builder.setCancelable(false);
            builder.setView(newPlacemarkLayout);
            builder.setPositiveButton(MainActivity.ctx.getString(R.string.ok), (dialog, which) -> {
                try
                {
                    // polyline simplification
                    g_points = PointReducer.reduceWithTolerance(g_points,0.000001);

                    // Close polygon
                    g_points.add(g_points.get(0));
                    g_polygon.setPoints(g_points);

                    int type = -1;
                    if(rb_general.isChecked())  type = 0;
                    if(rb_special.isChecked())  type = 1;
                    if(rb_weapon.isChecked())  type = 2;

                    KmlPlacemark placemark = new KmlPlacemark(g_polygon, kmlFavoritesDocument);
                    placemark.mName = et_name.getText().toString();
                    placemark.setExtendedData("type",String.valueOf(type));

//                    String style = placemark.getExtendedData("style");
//                    String style = "ylw-pushpin.png";
//                    if(style != null) {
//                        File file = new File(MainActivity.strIconsPath+style);
//                        String fname = file.getName();
//                        Bitmap icon_bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
//                        Style st = new Style(icon_bmp, g_polygon.getOutlinePaint().getColor(), g_polygon.getOutlinePaint().getStrokeWidth(), g_polygon.getFillPaint().getColor());
//                        kmlFavoritesDocument.putStyle(fname, st);
//                        placemark.mStyle = style;
//                    }
//                    kmlPolygon.applyDefaultStyling(polygon, mDefaultStyle, placemark, kmlFavoritesDocument, map);

//                    Style style = new Style(null, g_polygon.getOutlinePaint().getColor(), g_polygon.getOutlinePaint().getStrokeWidth(), mv_utils.adjustAlpha(g_polygon.getFillPaint().getColor(),64));
//                    placemark.mStyle = kmlFavoritesDocument.addStyle(style);

                    // Add item to adapter
                    GeoPoint p = g_points.get(0);
                    City city = new City();
                    city.strName = placemark.mName;
                    city.fLon = p.getLongitude();
                    city.fLat = p.getLatitude();
                    city.fAlt = (float)p.getAltitude();
                    city.geometry_type = City.POLYGON;// Polygon
                    city.index = Tab_Map.favorites_adapter.getCount();
                    city.placemark = placemark;
                    city.placemark.overlay = g_polygon;
                    Tab_Map.favorites_adapter.add(city);

                    g_polygon.setInfoWindow(new CustomInfoWindow(type, map, g_polygon, placemark, city));
                    kmlFavoritesDocument.mKmlRoot.mItems.add(placemark);
                    kmlFavoritesDocument.saveAsKML(favoritesFile);

//                    map.getOverlays().remove(g_polygon);

                    g_points.clear();
                    map.postInvalidate();
                    g_polygon = null;

                    MainActivity.hide_keyboard(newPlacemarkLayout);
                    Tab_Messenger.showToast(activity.getString(R.string.polygon_added_to_favorites));
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            });
            builder.setNegativeButton(MainActivity.ctx.getString(R.string.cancel), (dialog, which) -> {
                try
                {
                    g_points.clear();
                    g_polygon.setPoints(g_points);
                    kmlOverlay.invalidate();
                    map.postInvalidate();
                    g_polygon = null;

                    MainActivity.hide_keyboard(newPlacemarkLayout);
                    dialog.cancel();
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            });
            builder.show();
        }
    }

    static GeoPoint s_prev_p = new GeoPoint(0.0,0.0);
    public boolean add_point_to_track(GeoPoint p){
        if(track_points != null){
            boolean bChanged = (Math.abs(s_prev_p.getLongitude() - p.getLongitude()) >= 0.000001) || (Math.abs(s_prev_p.getLatitude() - p.getLatitude()) >= 0.000001);
            if(bChanged){
                track_points.add(p);
                s_prev_p = p.clone();
                tv_geom_info.setText(track_points.size()+" "+getString(R.string.points));
                return true;
            }
        }
        return false;
    }

    static void clear_map_cache(){
        SqlTileWriter sqlTileWriter = new SqlTileWriter();
        boolean isCleared = sqlTileWriter.purgeCache(map.getTileProvider().getTileSource().name());
        if(isCleared){
            Tab_Messenger.showToast("Map cache cleared...");
        }else{
            Tab_Messenger.showToast("Map cache not cleared...");
        }
    }

    void start_ruler(boolean enable){
        try
        {
            cb_ruler.setChecked(enable);
            if(enable)
            {
                ruler_overlay.setPos1(position);
                ruler_overlay.setPos2(position);
                ruler_overlay.setEnabled(true);
                ruler_overlay.setEdit(true);
                mapRuler.setVisibility(View.VISIBLE);
            }
            else
            {
                ruler_overlay.setPos1(position);
                ruler_overlay.setPos2(position);
                ruler_overlay.setEnabled(false);
                ruler_overlay.setEdit(false);
                mapRuler.setVisibility(View.GONE);
            }
            updateInfo(true);
            map.postInvalidate();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    @Override
    public void onClick(View view) {
        try
        {
            switch (view.getId()) {
                case R.id.b_locate:{
                    MainActivity.tab_camera.getLimits();
                    set_cam_pos(MainActivity.uav_lon,MainActivity.uav_lat, MainActivity.uav_alt,true);
                    set_home_pos(MainActivity.home_lon,MainActivity.home_lat,MainActivity.home_alt,false);
                    mapController.setCenter(cameraPoint);
                    mapController.setZoom(17.0);

//                    map.getDrawingCache();
//                    map
                    break;
                }
                case R.id.b_home:{
                    MainActivity.tab_camera.getHomeLocation();
                    set_home_pos(MainActivity.home_lon,MainActivity.home_lat,MainActivity.home_alt,false);
                    mapController.setCenter(homePoint);
                    mapController.setZoom(17.0);
                    break;
                }
//                case R.id.b_start_point:{
//                    set_start_pos(MainActivity.start_lon,MainActivity.start_lat,false);
//                    mapController.setCenter(startPoint);
//                    mapController.setZoom(17.0);
//                    break;
//                }
                case R.id.cb_target_lock: {
                    set_target_pos(MainActivity.target_lon,MainActivity.target_lat,MainActivity.target_alt,true);
                    mapController.setCenter(targetPoint);
                    mapController.setZoom(17.0);
                    break;
                }
                case R.id.b_limits:{
                    showLimitsDialog();
                    break;
                }
                case R.id.cb_map_status: {
                    map_status = cb_map_status.isChecked();

                    SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("map_status", map_status);
                    editor.apply();

                    map.invalidate();
                    break;
                }
                case R.id.cb_map_clustering: {
                    map_clustering = cb_map_clustering.isChecked();

                    if(map_clustering)
                        kmlOverlay.setRadius(100);
                    else
                        kmlOverlay.setRadius(-1);
                    kmlOverlay.invalidate();

                    SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("map_clustering", map_clustering);
                    editor.apply();

                    map.invalidate();
                    break;
                }
                case R.id.cb_kalman: {
                    is_kalman = cb_kalman.isChecked();

                    SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("is_kalman", is_kalman);
                    editor.apply();

                    map.invalidate();
                    break;
                }
                case R.id.cb_map_satellites: {
                    map_satellites = cb_map_satellites.isChecked();

                    SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("map_satellites", map_satellites);
                    editor.apply();

                    map.invalidate();
                    break;
                }
                case R.id.cb_gps_coordinates: {
                    gps_coordinates = cb_gps_coordinates.isChecked();

                    SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("gps_coordinates", gps_coordinates);
                    editor.apply();

                    map.invalidate();
                    break;
                }
                case R.id.cb_gps_egm96: {
                    gps_egm96 = cb_gps_egm96.isChecked();

                    SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("gps_egm96", gps_egm96);
                    editor.apply();

                    map.invalidate();
                    break;
                }
                case R.id.cb_auto_select_target: {
                    auto_select_target = cb_auto_select_target.isChecked();

                    SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("auto_select_target", auto_select_target);
                    editor.apply();

                    map.invalidate();
                    break;
                }
                case R.id.sw_auto_rotate_map: {
                    auto_rotate_map = sw_auto_rotate_map.isChecked();

                    SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("auto_rotate_map", auto_rotate_map);
                    editor.apply();

                    map.invalidate();
                    break;
                }
                case R.id.sw_broadcast_map: {
                    try
                    {
                        if(sw_broadcast_map.isChecked()) {
                            activity.runOnUiThread(() -> {
                                try {
                                    MapViewerView.set_tab(0, true);
                                } catch (Throwable ex) {
                                    MainActivity.MyLog(ex);
                                }
                            });
                        }
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_open_gps_settings:
                {
                    try {
                        LocationUtils.openGpsSettings();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.cb_add_favorite:{
                    if(cb_add_favorite.isChecked()) {
                        mapButtons.setVisibility(View.VISIBLE);
                        b_finish.setVisibility(View.VISIBLE);
                        b_enter.setVisibility(View.VISIBLE);
                        tv_geom_info.setVisibility(View.GONE);
                    }
                    else {
                        mapButtons.setVisibility(View.GONE);
                    }
                    break;
                }
                case R.id.cb_change_cam_pos:{
                    if(cb_change_cam_pos.isChecked()) {
                        mapButtons.setVisibility(View.VISIBLE);
                        b_finish.setVisibility(View.GONE);
                        b_enter.setVisibility(View.VISIBLE);
                        tv_geom_info.setVisibility(View.GONE);
                    }
                    else
                        mapButtons.setVisibility(View.GONE);
                    break;
                }
                case R.id.cb_change_target_pos:{
                    if(cb_change_target_pos.isChecked()) {
                        mapButtons.setVisibility(View.VISIBLE);
                        b_finish.setVisibility(View.GONE);
                        b_enter.setVisibility(View.VISIBLE);
                        tv_geom_info.setVisibility(View.GONE);
                    }
                    else
                        mapButtons.setVisibility(View.GONE);
                    break;
                }
                case R.id.cb_change_home_pos:{
                    if(cb_change_home_pos.isChecked()) {
                        try {
                            new AlertDialog.Builder(activity)
                                    .setCancelable(false)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Change UAV Home Position")
                                    .setMessage("Are you sure you want to change uav home position?")
                                    .setPositiveButton(R.string.yes_message, (dialog, which) -> {
                                        mapButtons.setVisibility(View.VISIBLE);
                                        b_finish.setVisibility(View.GONE);
                                        b_enter.setVisibility(View.VISIBLE);
                                        tv_geom_info.setVisibility(View.GONE);
                                        MainActivity.hide_keyboard(null);
                                    })
                                    .setNegativeButton(R.string.no_message, (dialog, which) -> {
                                        cb_change_home_pos.setChecked(false);
                                        MainActivity.hide_keyboard(null);
                                    })
                                    .show();
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    }
                    else
                        mapButtons.setVisibility(View.GONE);
                    break;
                }
                case R.id.cb_look_at:{
                    if(cb_look_at.isChecked()) {
                        mapButtons.setVisibility(View.VISIBLE);
                        b_finish.setVisibility(View.GONE);
                        b_enter.setVisibility(View.VISIBLE);
                        tv_geom_info.setVisibility(View.GONE);
                    }
                    else
                        mapButtons.setVisibility(View.GONE);
                    break;
                }
                case R.id.cb_goto:{
                    if(cb_goto.isChecked()) {
                        mapButtons.setVisibility(View.VISIBLE);
                        b_finish.setVisibility(View.GONE);
                        b_enter.setVisibility(View.VISIBLE);
                        tv_geom_info.setVisibility(View.GONE);
                    }
                    else
                        mapButtons.setVisibility(View.GONE);
                    break;
                }
                case R.id.cb_auto:{
                    if(cb_auto.isChecked()) {
                        update_gps(map.getMapCenter(), false, true);
                        customHandler.postDelayed(updateTimerThread, mv_utils.parseInt(e_update_interval_sec.getText().toString())* 1000L);
                    }
                    break;
                }
                case R.id.cb_mission:{
                    if(cb_mission.isChecked())
                        hsv_mission.setVisibility(View.VISIBLE);
                    else
                        hsv_mission.setVisibility(View.GONE);
                    break;
                }
                case R.id.b_my_location:{
                    GeoPoint myLocation = mLocationOverlay.getMyLocation();
                    if(myLocation != null) {
                        mapController.setCenter(myLocation);
                        mapController.setZoom(17.0);
                    }
                    break;
                }
                case R.id.b_virtual_gps:{
                    update_gps(map.getMapCenter(), false, true);
                    customHandler.postDelayed(updateTimerThread, mv_utils.parseInt(e_update_interval_sec.getText().toString())* 1000L);
                    break;
                }
                case R.id.b_projectile:{
                    show_projectile_settings();
                    break;
                }
                case R.id.b_projectile_chart:{
                    show_projectile_chart();
                    break;
                }
                case R.id.iv_insert_pos:{
                    showInsertPosDialog();
                    break;
                }
                case R.id.b_snapshot:{
                    try {
                        map.setMapOrientation(0.0f);
                        doRotate(map.getMapOrientation(), true);
                        InfoWindow.closeAllInfoWindowsOn(map);
                        kmlOverlay.invalidate();
                        map.invalidate();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }

                    showMessage("");
//                    looperThread.handler.sendEmptyMessage(0);
//                    mv_utils.playResource(MainActivity.ctx, R.raw.ding_dong);

                    try {
                        String strTitl = "Scale";
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle(strTitl);
                        builder.setCancelable(false);

                        // Set up the input
                        final EditText input = new EditText(activity);
                        input.setText("1");
                        input.setInputType(InputType.TYPE_CLASS_NUMBER);
                        builder.setView(input);

                        // Set up the buttons
                        builder.setPositiveButton(MainActivity.ctx.getString(R.string.ok), (dialog, which) -> {
                            try {
                                String strValue = input.getText().toString();
                                double scale = Double.parseDouble(strValue);

                                final TileSystem mTileSystem = new TileSystemWebMercator();
//                                final MapTileProviderBase mapTileProvider = new MapTileProviderBasic(MainActivity.activity);
                                int mBorderSize = 100;
                                final DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
//                                int screenWidth = (int)Math.round(scale*map.getWidth());
//                                int screenHeight = (int)Math.round(scale*map.getHeight());
                                int screenWidth = (int)Math.round(scale*dm.widthPixels);
                                int screenHeight = (int)Math.round(scale*dm.heightPixels);
                                final double zoom = mTileSystem.getBoundingBoxZoom(
                                        map.getBoundingBox(), screenWidth - 2 * mBorderSize, screenHeight - 2 * mBorderSize);
//                                final double zoom = map.getZoomLevelDouble();// + 1;
                                GeoPoint center = new GeoPoint(map.getMapCenter());
//                                Projection projection = new Projection(zoom + (scale - 1), (int)Math.round(scale*map.getWidth()), (int)Math.round(scale*map.getHeight()), center, 0, true, true, 0, 0);
                                Projection projection = new Projection(zoom, screenWidth, screenHeight, center, 0, true, true, 0, 0);
                                final MapSnapshot mapSnapshot = new MapSnapshot(new MapSnapshot.MapSnapshotable() {
                                    @Override
                                    public void callback(final MapSnapshot pMapSnapshot) {
                                        if (pMapSnapshot.getStatus() != MapSnapshot.Status.CANVAS_OK) {
                                            Tab_Messenger.showToast("Map not Captured...");
                                            return;
                                        }
                                        showMessage("Capturing Map...");
//                                        looperThread.handler.sendEmptyMessage(1);
//                                        mv_utils.playResource(MainActivity.ctx, R.raw.ding_dong);

//                            pMapSnapshot.save(new File(MainActivity.strMapCaptured));

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                activity.runOnUiThread(() -> {
                                                    try {
                                                        Bitmap bitmap = Bitmap.createBitmap(pMapSnapshot.getBitmap());

                                                        Canvas canvas = new Canvas(bitmap);

                                                        if(mScaleBarOverlay != null){
                                                            mScaleBarOverlay.mMapWidth = screenWidth;
                                                            mScaleBarOverlay.mMapHeight = screenHeight;

//                                                            mScaleBarOverlay.setAlignBottom(true);
//                                                            mScaleBarOverlay.setAlignRight(true);
//                                                            mScaleBarOverlay.setUnitsOfMeasure(MyScaleBarOverlay.UnitsOfMeasure.metric);
//                                                            mScaleBarOverlay.setScaleBarOffset(10, 10);
//                                                            mScaleBarOverlay.setMinZoom(0);

                                                            mScaleBarOverlay.draw(canvas, projection);
                                                            mScaleBarOverlay.mMapWidth = -1;
                                                            mScaleBarOverlay.mMapHeight = -1;
                                                            mScaleBarOverlay.setEnabled(true);
                                                        }

//                                                        if((mapCompass != null) && (mapCompass.getVisibility() != View.GONE))  mapCompass.draw(canvas);
                                                        if(kmlOverlay != null)   kmlOverlay.draw(canvas, map, false, projection);
                                                        if(cross_overlay != null) {
                                                            cross_overlay.setMessage("");
                                                            cross_overlay.draw(canvas, map, false, projection);
                                                        }
                                                        if(fov_overlay != null){
                                                            fov_overlay.update_calculations();
                                                            fov_overlay.draw(canvas, map, false, projection);
                                                        }
                                                        if(ruler_overlay != null)   ruler_overlay.draw(canvas, map, false, projection);

                                                        Bitmap oghab = BitmapFactory.decodeResource(activity.getResources(), R.drawable.oghab_mapviewer2);
                                                        canvas.drawBitmap(oghab,0,(projection.getHeight() - oghab.getHeight())/2.0f, null);

                                                        final String new_filename = FileHelper.saveImage(bitmap);
                                                        showMessage("Map saved to: ["+new_filename+"]");
//                                            looperThread.handler.sendEmptyMessage(2);
//                                                        mv_utils.playResource(MainActivity.ctx, R.raw.ding_dong);
                                                    } catch (Throwable ex) {
                                                        MainActivity.MyLog(ex);
                                                    }
                                                });
                                            }
                                        }).start();

//                            FileHelper.save_image_as_jpg(new File(MainActivity.strMapCaptured), bitmap);
//                            Tab_Messenger.sendFile(MainActivity.strMapCaptured, true, "Map sending...");

//                                        activity.runOnUiThread(() -> {
//                                            try {
//                                                Tab_Messenger.showToast("Map Captured...");
//                                                MapViewerView.process_click(R.id.radio_messenger);
//                                            } catch (Throwable ex) {
//                                                MainActivity.MyLog(ex);
//                                            }
//                                        });

//                                        try{
//                                            final String new_filename = FileHelper.saveImage(null);
//                                            FileHelper.saveImage(bitmap);
////                                            showMessage("Map saved to: ["+new_filename+"]");
////                                            looperThread.handler.sendEmptyMessage(2);
//                                            mv_utils.playResource(MainActivity.ctx, R.raw.ding_dong);
//
////                                String new_filename = FileHelper.saveFile(MainActivity.strMapCaptured);
////                                            activity.runOnUiThread(() -> {
////                                                try {
////                                                    Tab_Messenger.showToast("Map Saved...");
////                                                    MainActivity.MyLogInfo("Map saved to: ["+new_filename+"]");
////                                                    Tab_Messenger.addError("Map saved to: ["+new_filename+"]");
////                                                } catch (Throwable ex) {
////                                                    MainActivity.MyLog(ex);
////                                                }
////                                            });
//                                        } catch (Throwable ex) {
//                                            MainActivity.MyLog(ex);
//                                        }
                                    }
//                    }, MapSnapshot.INCLUDE_FLAG_UPTODATE, map);
//                    }, MapSnapshot.INCLUDE_FLAGS_ALL, map);
//                    }, MapSnapshot.INCLUDE_FLAGS_ALL, mapTileProvider, map.getOverlays(),
//                    }, MapSnapshot.INCLUDE_FLAGS_ALL, map.getTileProvider(), map.getOverlays(),
//                                }, MapSnapshot.INCLUDE_FLAG_UPTODATE + MapSnapshot.INCLUDE_FLAG_EXPIRED + MapSnapshot.INCLUDE_FLAG_SCALED, map.getTileProvider(), map.getOverlays(),
                                }, MapSnapshot.INCLUDE_FLAG_UPTODATE + MapSnapshot.INCLUDE_FLAG_EXPIRED + MapSnapshot.INCLUDE_FLAG_SCALED, map.getTileProvider(), map.getOverlays(),
//                            }, MapSnapshot.INCLUDE_FLAG_UPTODATE, map.getTileProvider(), map.getOverlays(),
//                            }, MapSnapshot.INCLUDE_FLAGS_ALL, map.getTileProvider(), map.getOverlays(),
//                            new Projection(zoom, (int)Math.round(1.0*map.getWidth()), (int)Math.round(1.0*map.getHeight()), center, 0, false, false, 0, 0));
//                                        new Projection(zoom+(scale - 1), (int)Math.round(scale*map.getWidth()), (int)Math.round(scale*map.getHeight()), center, -map.getMapOrientation(), false, false, 0, 0));
                                        projection);
//                            map.getProjection());
//                    mapSnapshot.run();
//                    mapSnapshot.refreshASAP();
                                mScaleBarOverlay.setEnabled(false);
                                new Thread(mapSnapshot).start();
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                            MainActivity.hide_keyboard(input);
                        });
                        builder.setNegativeButton(MainActivity.ctx.getString(R.string.cancel), (dialog, which) -> {
                            try {
                                MainActivity.hide_keyboard(input);
                                dialog.cancel();
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        });

                        builder.setCancelable(false);
                        builder.show();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.image_compass:
                case R.id.text_compass:{
                    map.setMapOrientation(0.0f);
                    doRotate(map.getMapOrientation(), true);
                    InfoWindow.closeAllInfoWindowsOn(map);
                    text_compass.setText("");
                    text_compass.invalidate();
                    if(cross_overlay != null) {
                        cross_overlay.setMessage("");
                        map.invalidate();
                    }
                    MainActivity.set_fullscreen();
                    break;
                }
                case R.id.tv_measurements2:
                case R.id.tv_measurements3:
                {
//                    start_ruler(false);
                    break;
                }
                case R.id.b_enter:{
                    GeoPoint p = new GeoPoint(33.517792, 36.342728);
                    IGeoPoint mapCenter = map.getMapCenter();
                    double fLon,fLat;
                    fLon = mapCenter.getLongitude();
                    fLat = mapCenter.getLatitude();
                    float fAlt = MainActivity.GetHeightJNI(fLon,fLat);
                    p.setLongitude(fLon);
                    p.setLatitude(fLat);
                    p.setAltitude(fAlt);

                    enter_point(p);
                    break;
                }
                case R.id.b_finish:{
                    if(cb_ruler.isChecked()){
                        cb_ruler.setChecked(false);
                        mapButtons.setVisibility(View.GONE);
                        start_ruler(cb_ruler.isChecked());
                    }

                    if(cb_track.isChecked()){
                        cb_track.setChecked(false);
                        mapButtons.setVisibility(View.GONE);
                        enter_track();
                    }

                    if(cb_add_favorite.isChecked()){
                        cb_add_favorite.setChecked(false);
                        mapButtons.setVisibility(View.GONE);
                    }

                    if(cb_add_path.isChecked()){
                        cb_add_path.setChecked(false);
                        mapButtons.setVisibility(View.GONE);
                        finish_polyline();
                    }

                    if(cb_add_polygon.isChecked()){
                        cb_add_polygon.setChecked(false);
                        mapButtons.setVisibility(View.GONE);
                        finish_polygon();
                    }
                    break;
                }
                case R.id.b_edit_mode:{
                    edit_mode = !edit_mode;
                    if(edit_mode)
                        b_edit_mode.setText(R.string.add_delete);
                    else
                        b_edit_mode.setText(R.string.drag);

                    if(curr_object instanceof MyMarker)
                    {
                        MyMarker marker0 = (MyMarker) curr_object; //the marker on which you click to open the bubble
                        marker0.setEdit(MainActivity.ctx, null, false);
                        map.postInvalidate();
                        marker0.setEdit(MainActivity.ctx, null, true);
                        map.postInvalidate();
                    }
                    else
                    if(curr_object instanceof MyPolyline)
                    {
                        MyPolyline polyline0 = (MyPolyline) curr_object;
                        polyline0.setEdit(MainActivity.ctx, null, false);
                        map.postInvalidate();
                        polyline0.setEdit(MainActivity.ctx, null, true);
                        map.postInvalidate();
                    }
                    else
                    if(curr_object instanceof MyPolygon)
                    {
                        MyPolygon polygon0 = (MyPolygon) curr_object;
                        polygon0.setEdit(MainActivity.ctx, null, false);
                        map.postInvalidate();
                        polygon0.setEdit(MainActivity.ctx, null, true);
                        map.postInvalidate();
                    }

                    map.postInvalidate();
                    break;
                }
                case R.id.b_edit_finish:{
                    mapEditButtons.setVisibility(View.GONE);
                    if(curr_object instanceof MyMarker)
                    {
                        MyMarker marker0 = (MyMarker) curr_object; //the marker on which you click to open the bubble
                        marker0.setEdit(MainActivity.ctx, null, false);
                        map.postInvalidate();
                    }
                    else
                    if(curr_object instanceof MyPolyline)
                    {
                        MyPolyline polyline0 = (MyPolyline) curr_object;
                        polyline0.setEdit(MainActivity.ctx, null, false);
                        map.postInvalidate();
                    }
                    else
                    if(curr_object instanceof MyPolygon)
                    {
                        MyPolygon polygon0 = (MyPolygon) curr_object;
                        polygon0.setEdit(MainActivity.ctx, null, false);
                        map.postInvalidate();
                    }
                    break;
                }
                case R.id.b_finish_mission:{
                    mapFinishMission.setVisibility(View.GONE);
                    if(Tab_Map.target_item instanceof MyMarker)
                    {
                        MyMarker marker0 = (MyMarker) Tab_Map.target_item;

                        if(Tab_Map.target_Marker != null) {
                            int idx = Integer.parseInt(Tab_Map.target_Marker.getId());
                            if(idx >= 0) {
                                Tab_Map.navigation_mode = false;
                                Tab_Map.target_Marker.setEnabled(false);
                                Tab_Map.target_Marker.setId(Integer.toString(-1));
                                if(marker0 != null){
                                    Tab_Map.CustomInfoWindow customInfoWindow = (Tab_Map.CustomInfoWindow) marker0.getInfoWindow();
                                    if(customInfoWindow != null) {
                                        customInfoWindow.setEdit(false);
                                    }
                                }
                                MainActivity.mission_finished(false);
                            }
                        }
                    }
                    else
                    if(Tab_Map.target_item instanceof MyPolyline)
                    {
                        MyPolyline polyline0 = (MyPolyline) Tab_Map.target_item;

                        Tab_Map.navigation_mode = false;
                        Tab_Map.target_Marker.setEnabled(false);
                        Tab_Map.target_Marker.setId(Integer.toString(-1));
                        if(polyline0 != null) {
                            Tab_Map.CustomInfoWindow customInfoWindow = (Tab_Map.CustomInfoWindow) polyline0.getInfoWindow();
                            if (customInfoWindow != null) {
                                customInfoWindow.setEdit(false);
                            }
                        }
                        MainActivity.mission_finished(false);
                    }
                    else
                    if(Tab_Map.target_item instanceof MyPolygon)
                    {
                        MyPolygon polygon0 = (MyPolygon) Tab_Map.target_item;

                        Tab_Map.navigation_mode = false;
                        Tab_Map.target_Marker.setEnabled(false);
                        Tab_Map.target_Marker.setId(Integer.toString(-1));
                        if(polygon0 != null) {
                            Tab_Map.CustomInfoWindow customInfoWindow = (Tab_Map.CustomInfoWindow) polygon0.getInfoWindow();
                            if (customInfoWindow != null) {
                                customInfoWindow.setEdit(false);
                            }
                        }
                        MainActivity.mission_finished(false);
                    }
                    break;
                }
                case R.id.b_toggle_path_direction:{
                    if(Tab_Map.target_item instanceof MyPolyline)
                    {
                        IGeoPoint mapCenter = map.getMapCenter();
                        MyPolyline polyline0 = (MyPolyline) Tab_Map.target_item;
                        polyline0.toggle_direction();
                        polyline0.setEdit(MainActivity.ctx, null, false);
                        polyline0.setEdit(MainActivity.ctx, null, true);
                        mapController.setCenter(mapCenter);
                        Tab_Map.map.invalidate();
                    }
                    else
                    if(Tab_Map.target_item instanceof MyPolygon)
                    {
                        IGeoPoint mapCenter = map.getMapCenter();
                        MyPolygon polygon0 = (MyPolygon) Tab_Map.target_item;
                        polygon0.toggle_direction();
                        polygon0.setEdit(MainActivity.ctx, null, false);
                        polygon0.setEdit(MainActivity.ctx, null, true);
                        mapController.setCenter(mapCenter);
                        Tab_Map.map.invalidate();
                    }
                    break;
                }
                case R.id.b_timeline_init:{
//                    MissionsAdapter.strPath = Environment.getExternalStorageDirectory().getPath();
//                    Tab_Map.strMissionPath = Tab_Map.strMissionDir + MissionsAdapter.strPath;
                    showMissionsDialog();
                    break;
                }
                case R.id.b_timeline_simulate:{
                    if(timeline != null)    timeline.simulateTimeline();
                    break;
                }
                case R.id.b_timeline_start:{
                    if(MainActivity.IsDemoVersionJNI())
                        Tab_Messenger.showToast("Mission planning is not allowed in Demo Version!");
                    else {
                        if(timeline != null)    timeline.startTimeline();
                    }
                    break;
                }
                case R.id.b_timeline_stop:{
                    if(timeline != null)    timeline.stopTimeline();
                    break;
                }
                case R.id.b_timeline_pause:{
                    Tab_Messenger.showToast("Timeline just supports the pause on the pausable elements, such as hotpoint mission, waypoint mission");
                    if(timeline != null)    timeline.pauseTimeline();
                    break;
                }
                case R.id.b_timeline_resume:{
                    Tab_Messenger.showToast("Timeline just supports the pause on the pausable elements, such as hotpoint mission, waypoint mission");
                    if(timeline != null)    timeline.resumeTimeline();
                    break;
                }
//                case R.id.b_timeline_clean:{
//                    timeline.cleanTimelineDataAndLog();
//                    break;
//                }
                case R.id.cb_search:
                {
                    MainActivity.set_fullscreen();
                    if(cb_search.isChecked()) {
                        String strText = et_search.getText().toString();
                        int count = MainActivity.search(strText);
                        hsv_search.setVisibility(View.VISIBLE);
                        tv_search_count.setText(Integer.toString(count));
                    }
                    else
                        hsv_search.setVisibility(View.GONE);
                    view.invalidate();
                    map.postInvalidate();
                    break;
                }
                case R.id.b_hide_search:
                {
                    hide_search();
                    break;
                }
                case R.id.b_hide_favorites:
                {
                    hide_favorites();
                    break;
                }
                case R.id.b_send_all_marks:
                {
                    Tab_Messenger.sendFile(favoritesFile.getPath(), true, "Favorites sending...", true, new tcp_io_handler.SendCallback() {
                        @Override
                        public void onFinish(int error) {
                            if(error != tcp_io_handler.TCP_OK) {
                                if (MainActivity.IsDebugJNI()) {
                                    MainActivity.MyLogInfoSilent("The Current Line Number is " + Arrays.toString(new Throwable().getStackTrace()));
                                }
                            }
                            MapViewerView.process_click(R.id.radio_messenger);
                        }
                    });
                    break;
                }
                case R.id.b_hide_sensors:
                {
                    cb_sensors.setChecked(false);
                    hsv_sensors.setVisibility(View.GONE);
                    view.invalidate();
                    map.postInvalidate();
                    break;
                }
                case R.id.cb_favorites:
                {
                    if(cb_favorites.isChecked()) {
                        hsv_favorites.setVisibility(View.VISIBLE);
                    }
                    else
                        hsv_favorites.setVisibility(View.GONE);

                    fillFavoritesList(kmlFavoritesDocument);

                    view.invalidate();
                    map.postInvalidate();
                    break;
                }
                case R.id.cb_show_marks:
                {
                    SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("cb_show_marks", cb_show_marks.isChecked());
                    editor.apply();

                    fillFavoritesList(kmlFavoritesDocument);

                    view.invalidate();
                    map.postInvalidate();
                    break;
                }
                case R.id.cb_show_polylines:
                {
                    SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("cb_show_polylines", cb_show_polylines.isChecked());
                    editor.apply();

                    fillFavoritesList(kmlFavoritesDocument);

                    view.invalidate();
                    map.postInvalidate();
                    break;
                }
                case R.id.cb_show_polygons:
                {
                    SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("cb_show_polygons", cb_show_polygons.isChecked());
                    editor.apply();

                    fillFavoritesList(kmlFavoritesDocument);

                    view.invalidate();
                    map.postInvalidate();
                    break;
                }
                case R.id.cb_follow:
                {
                    if(cb_follow.isChecked()) {
//                        mLocationOverlay.enableMyLocation();
//                        mLocationOverlay.enableFollowLocation();
                    }
                    else {
//                        mLocationOverlay.disableMyLocation();
//                        mLocationOverlay.disableFollowLocation();
                    }
                    map.postInvalidate();
                    break;
                }
                case R.id.cb_ruler:
                {
                    if(cb_ruler.isChecked()) {
                        mapButtons.setVisibility(View.VISIBLE);
                        b_finish.setVisibility(View.VISIBLE);
                        b_enter.setVisibility(View.GONE);
                        tv_geom_info.setVisibility(View.GONE);
                    }
                    else {
                        mapButtons.setVisibility(View.GONE);
                    }

                    start_ruler(cb_ruler.isChecked());
                    break;
                }
                case R.id.cb_latlon_grid:
                {
                    gridlineOverlay2.setEnabled(cb_utm_grid.isChecked());
                    map.postInvalidate();
                    break;
                }
                case R.id.cb_sensors:
                {
                    if(cb_sensors.isChecked())
                        hsv_sensors.setVisibility(View.VISIBLE);
                    else
                        hsv_sensors.setVisibility(View.GONE);
                    break;
                }
                case R.id.b_save:
                {
                    kmlFavoritesDocument.saveAsKML(favoritesFile);
                    setResultToToast("Changes saved...");
                    text_compass.setText("");
                    text_compass.invalidate();
                    if(cross_overlay != null) {
                        cross_overlay.setMessage("");
                        map.invalidate();
                    }

                    // Hide both the navigation bar and the status bar.
                    MainActivity.set_fullscreen();

                    InfoWindow.closeAllInfoWindowsOn(map);
                    break;
                }
                case R.id.b_test:
                {
                    MyPolyline lines = new MyPolyline(map);
                    lines.getOutlinePaint().setColor(Color.BLUE);
                    lines.getOutlinePaint().setStrokeWidth(5.0f);
                    List<GeoPoint> points = new ArrayList<>();
                    double lat,lon;
                    for(int i=0;i<5;i++){
                        lat = 2*Math.random()+33;
                        lon = 2*Math.random()+35;
                        points.add(new GeoPoint(lat,lon));
                    }
                    lines.setPoints(points);
                    lines.setInfoWindow(new CustomInfoWindow(0, map, lines, null, null));
                    map.getOverlays().add(lines);
                    map.postInvalidate();
                    System.out.println("size: "+lines.getActualPoints().size());
                    break;
                }
                case R.id.tv_add_start:
                {
                    GeoPoint p1 = ruler_overlay.getPos1();
                    p1.setAltitude(MainActivity.GetHeightJNI(p1.getLongitude(),p1.getLatitude()));
                    if(cb_add_favorite.isChecked() || cb_add_path.isChecked() || cb_add_polygon.isChecked())
                        enter_point(p1);
                    else
                        add_point(p1, "Start", 0);
                    break;
                }
                case R.id.tv_add_end:
                {
                    GeoPoint p2 = ruler_overlay.getPos2();
                    p2.setAltitude(MainActivity.GetHeightJNI(p2.getLongitude(),p2.getLatitude()));
                    if(cb_add_favorite.isChecked() || cb_add_path.isChecked() || cb_add_polygon.isChecked())
                        enter_point(p2);
                    else
                        add_point(p2, "End", 0);
                    break;
                }
                case R.id.tv_update_ruler:
                {
                    double dist = Double.parseDouble(et_ruler_distance.getText().toString());
                    double azi = Double.parseDouble(et_ruler_azimuth.getText().toString());
                    ruler_overlay.update2(dist,azi);
                    update_ruler();
                    break;
                }
                case R.id.b_fake_uav_reset:
                {
                    // uav yaw
                    MainActivity.uav_yaw = 0;
                    // bug fixed at 19/5/2024
//                    MainActivity.image_yaw_enc = (float)MainActivity.db_deg(MainActivity.uav_yaw + MainActivity.gimb_yaw);
                    MainActivity.image_yaw_enc = MainActivity.gimb_yaw;
                    MainActivity.image_yaw = (float)MainActivity.db_deg(MainActivity.image_yaw_enc + MainActivity.dYaw);

                    tv_uav_yaw.setText("yaw: "+ MainActivity.uav_yaw);
                    set_uav_yaw(MainActivity.uav_yaw,true);
                    sb_uav_yaw.setProgress((int)MainActivity.uav_yaw + 180);

                    // uav pitch
                    MainActivity.uav_pitch = 0;
                    // bug fixed at 19/5/2024
//                    MainActivity.image_pitch_enc = MainActivity.uav_pitch + MainActivity.gimb_pitch;
                    MainActivity.image_pitch_enc = MainActivity.gimb_pitch;
                    MainActivity.image_pitch = MainActivity.image_pitch_enc + MainActivity.dPitch;

                    tv_uav_pitch.setText("pitch: "+ MainActivity.uav_pitch);
                    sb_uav_pitch.setProgress((int)MainActivity.uav_pitch + 90);

                    // uav roll
                    MainActivity.uav_roll = 0;
                    // bug fixed at 19/5/2024
//                    MainActivity.image_roll_enc = MainActivity.uav_roll + MainActivity.gimb_roll;
                    MainActivity.image_roll_enc = MainActivity.gimb_roll;
                    MainActivity.image_roll = MainActivity.image_roll_enc + MainActivity.dRoll;

                    tv_uav_roll.setText("roll: "+ MainActivity.uav_roll);
                    sb_uav_roll.setProgress((int)MainActivity.uav_roll + 90);

                    // uav alt
                    MainActivity.uav_alt_above_ground = 0;
                    MainActivity.uav_ground_alt = MainActivity.GetHeightJNI(MainActivity.uav_lon,MainActivity.uav_lat);
                    MainActivity.uav_alt = MainActivity.uav_ground_alt + MainActivity.uav_alt_above_ground;

                    tv_uav_alt.setText("alt: "+ MainActivity.uav_alt_above_ground);
                    sb_uav_alt.setProgress((int)MainActivity.uav_alt_above_ground);

                    setResultToToast("UAV orientation reset...");
                    break;
                }
                case R.id.b_fake_gimbal_reset:
                {
                    // gimbal yaw
                    MainActivity.gimb_yaw = 0;
                    // bug fixed at 19/5/2024
//                    MainActivity.image_yaw_enc = (float)MainActivity.db_deg(MainActivity.uav_yaw + MainActivity.gimb_yaw);
                    MainActivity.image_yaw_enc = MainActivity.gimb_yaw;
                    MainActivity.image_yaw = (float)MainActivity.db_deg(MainActivity.image_yaw_enc + MainActivity.dYaw);

                    tv_gimb_yaw.setText("yaw: "+ MainActivity.gimb_yaw);
                    set_camera_azi(MainActivity.image_yaw,true);
                    sb_gimb_yaw.setProgress((int)MainActivity.gimb_yaw + 180);

                    // gimbal pitch
                    MainActivity.gimb_pitch = 0;
                    // bug fixed at 19/5/2024
//                    MainActivity.image_pitch_enc = MainActivity.uav_pitch + MainActivity.gimb_pitch;
                    MainActivity.image_pitch_enc = MainActivity.gimb_pitch;
                    MainActivity.image_pitch = MainActivity.image_pitch_enc + MainActivity.dPitch;

                    tv_gimb_pitch.setText("pitch: "+ MainActivity.gimb_pitch);
                    sb_gimb_pitch.setProgress((int)MainActivity.gimb_pitch + 90);

                    // gimbal roll
                    MainActivity.gimb_roll = 0;
                    // bug fixed at 19/5/2024
//                    MainActivity.image_roll_enc = MainActivity.uav_roll + MainActivity.gimb_roll;
                    MainActivity.image_roll_enc = MainActivity.gimb_roll;
                    MainActivity.image_roll = MainActivity.image_roll_enc + MainActivity.dRoll;

                    tv_gimb_roll.setText("roll: "+ MainActivity.gimb_roll);
                    sb_gimb_roll.setProgress((int)MainActivity.gimb_roll + 90);

                    setResultToToast("Gimbal orientation reset...");
                    break;
                }
                case R.id.cb_add_path:
                {
                    if(cb_add_path.isChecked()) {
                        mapButtons.setVisibility(View.VISIBLE);
                        b_finish.setVisibility(View.VISIBLE);
                        b_enter.setVisibility(View.VISIBLE);
                        tv_geom_info.setVisibility(View.VISIBLE);
                    }
                    else
                        mapButtons.setVisibility(View.GONE);
                    if(cb_add_path.isChecked())
                    {
                        if(g_points == null)
                        {
                            g_points = new ArrayList<>();
                        }
                        g_points.clear();
                        if(g_polyline == null) {
                            g_polyline = new MyPolyline(map);
                            g_polyline.getOutlinePaint().setColor(Color.GREEN);
                            g_polyline.getOutlinePaint().setStrokeWidth(10.0f);
//                            polyline.setInfoWindow(new CustomInfoWindow(0, map, null, null, null));
//                            polyline.setInfoWindow(new MarkerInfoWindow(R.layout.bonuspack_bubble, map));
                            kmlOverlay.add(g_polyline);
//                            map.getOverlays().add(g_polyline);
                            map.postInvalidate();
                            tv_geom_info.setText(g_points.size()+" "+getString(R.string.points));
                        }
//                    path_overlay.update_points(points);
//                    map.postInvalidate();
                    }
                    else
                    {
                        finish_polyline();
                    }
                    break;
                }
                case R.id.cb_add_polygon:
                {
                    if(cb_add_polygon.isChecked()) {
                        mapButtons.setVisibility(View.VISIBLE);
                        b_finish.setVisibility(View.VISIBLE);
                        b_enter.setVisibility(View.VISIBLE);
                        tv_geom_info.setVisibility(View.VISIBLE);
                    }
                    else
                        mapButtons.setVisibility(View.GONE);
                    if(cb_add_polygon.isChecked())
                    {
                        if(g_points == null)
                        {
                            g_points = new ArrayList<>();
                        }
                        g_points.clear();
                        if(g_polygon == null) {
                            g_polygon = new MyPolygon(map);
                            g_polygon.getOutlinePaint().setColor(Color.GREEN);
                            g_polygon.getOutlinePaint().setStrokeWidth(10.0f);
                            g_polygon.getFillPaint().setColor(Color.YELLOW);
                            g_polygon.getFillPaint().setAlpha(64);
//                            polyline.setInfoWindow(new CustomInfoWindow(0, map, null, null, null));
//                            polyline.setInfoWindow(new MarkerInfoWindow(R.layout.bonuspack_bubble, map));
                            kmlOverlay.add(g_polygon);
//                            map.getOverlays().add(g_polygon);
                            map.postInvalidate();
                            tv_geom_info.setText(g_points.size()+" "+getString(R.string.points));
                        }
//                    path_overlay.update_points(points);
//                    map.postInvalidate();
                    }
                    else
                    {
                        finish_polygon();
                    }
                    break;
                }
                case R.id.cb_track:
                {
                    if(cb_track.isChecked()) {
                        mapButtons.setVisibility(View.VISIBLE);
                        b_finish.setVisibility(View.VISIBLE);
                        b_enter.setVisibility(View.GONE);
                        tv_geom_info.setVisibility(View.VISIBLE);
                    }
                    else {
                        mapButtons.setVisibility(View.GONE);
                    }

                    enter_track();
                    break;
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void enter_track(){
        if(cb_track.isChecked())
        {
            if(track_points == null)
            {
                track_points = new ArrayList<>();
            }
            track_points.clear();
            if(track_polyline == null) {
                track_polyline = new MyPolyline(map);
                track_polyline.getOutlinePaint().setColor(Color.BLUE);
                track_polyline.getOutlinePaint().setStrokeWidth(5.0f);
//                            track_polyline.setInfoWindow(new CustomInfoWindow(0, map, null, null, null));
//                            track_polyline.setInfoWindow(new MarkerInfoWindow(R.layout.bonuspack_bubble, map));
                map.getOverlays().add(track_polyline);
                map.postInvalidate();
                tv_geom_info.setText(track_points.size()+" "+getString(R.string.points));
            }
//                    path_overlay.update_points(track_points);
//                    map.postInvalidate();
        }
        else
        {
            if(track_points.size() > 1){
                AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialog);
                builder.setTitle(activity.getString(R.string.track_name));
                builder.setCancelable(false);
                final EditText input = new EditText(activity);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton(MainActivity.ctx.getString(R.string.ok), (dialog, which) -> {
                    try
                    {
                        // polyline simplification
                        track_points = PointReducer.reduceWithTolerance(track_points,0.000001);
                        track_polyline.setPoints(track_points);

                        int type = 0;
                        KmlPlacemark placemark = new KmlPlacemark(track_polyline,kmlFavoritesDocument);
                        placemark.mName = input.getText().toString();
                        placemark.setExtendedData("type",String.valueOf(type));

                        // Add item to adapter
                        GeoPoint p = track_points.get(0);
                        City city = new City();
                        city.strName = placemark.mName;
                        city.fLon = p.getLongitude();
                        city.fLat = p.getLatitude();
                        city.fAlt = (float)p.getAltitude();
                        city.geometry_type = City.POLYLINE;// Path
                        city.index = Tab_Map.favorites_adapter.getCount();
                        city.placemark = placemark;
                        city.placemark.overlay = track_polyline;
                        Tab_Map.favorites_adapter.add(city);

                        track_polyline.setInfoWindow(new CustomInfoWindow(type, map, track_polyline, placemark, city));

                        kmlFavoritesDocument.mKmlRoot.mItems.add(placemark);
                        kmlFavoritesDocument.saveAsKML(favoritesFile);

                        map.postInvalidate();
                        track_polyline = null;

                        MainActivity.hide_keyboard(input);
                        Tab_Messenger.showToast(activity.getString(R.string.track_added_to_favorites));
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                });
                builder.setNegativeButton(MainActivity.ctx.getString(R.string.cancel), (dialog, which) -> {
                    try
                    {
                        track_points.clear();
                        track_polyline.setPoints(track_points);
                        kmlOverlay.invalidate();
                        map.postInvalidate();
                        track_polyline = null;

                        MainActivity.hide_keyboard(input);
                        dialog.cancel();
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                });
                builder.show();
            }
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        try
        {
            if (view.getId() == R.id.et_search) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    MainActivity.set_fullscreen();
                    String strText = et_search.getText().toString();
                    int count = MainActivity.search(strText);
                    cb_search.setChecked(true);
                    hsv_search.setVisibility(View.VISIBLE);
                    tv_search_count.setText(Integer.toString(count));
                    view.invalidate();
                    map.postInvalidate();
                    return true;
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    public float getAltitude(float mapzoom){
        //this equation is a transformation of the angular size equation solving for D. See: http://en.wikipedia.org/wiki/Forced_perspective
        float googleearthaltitude;
        float firstPartOfEq= (float)(.05 * ((591657550.5/(Math.pow(2,(mapzoom-1))))/2));//amount displayed is .05 meters and map scale =591657550.5/(Math.pow(2,(mapzoom-1))))
        //this bit ^ essentially gets the h value in the angular size eq then divides it by 2
        googleearthaltitude =(firstPartOfEq) * ((float) (Math.cos(Math.toRadians(85.362/2)))/(float) (Math.sin(Math.toRadians(85.362/2))));//85.362 is angle which google maps displays on a 5cm wide screen
        return googleearthaltitude;
    }

    public int convertRangeToZoom(double range) {
        //see: google.maps.v3.all.debug.js
        int zoom = (int) Math.round(Math.log(35200000 / range) / Math.log(2));
        if (zoom < 0) zoom = 0;
        else if (zoom > 19) zoom = 19;
        return zoom;
    }

    public int convertZoomToRange(double zoom){
        //see: google.maps.v3.all.debug.js
        int range = (int)(35200000/(Math.pow(2, zoom)));
        if (range < 300) range = 300;
        return range;
    }

    public void add_point(GeoPoint p,String title,int type) {
        try
        {
            MyMarker marker = new MyMarker(map);
            marker.setPosition(p);
            marker.setAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
            marker.setInfoWindowAnchor(MyMarker.ANCHOR_CENTER, MyMarker.ANCHOR_BOTTOM);
            marker.setIcon(mv_utils.getDrawable(MainActivity.ctx, R.drawable.marker_icon));
            marker.setTitle(title);
            kmlOverlay.add(marker);
//                        map.getOverlays().add(marker);

            KmlPlacemark placemark = new KmlPlacemark(marker);
            placemark.setExtendedData("type",String.valueOf(type));
            Tab_Map.update_placemark_style(marker, placemark, null,Color.WHITE, 1.0f, 0.0f);

            // Add item to adapter
            City city = new City();
            city.strName = placemark.mName;
            city.fLon = p.getLongitude();
            city.fLat = p.getLatitude();
            city.fAlt = (float)p.getAltitude();
            city.geometry_type = City.POINT;// point
            city.index = Tab_Map.favorites_adapter.getCount();
            city.placemark = placemark;
            city.placemark.overlay = marker;
            Tab_Map.favorites_adapter.add(city);

            marker.setInfoWindow(new CustomInfoWindow(type, map, marker, placemark, city));
            kmlFavoritesDocument.mKmlRoot.mItems.add(placemark);
            kmlFavoritesDocument.saveAsKML(favoritesFile);
            kmlOverlay.invalidate();
            map.postInvalidate();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public boolean enter_point(GeoPoint p) {
        try
        {
            if(cb_add_favorite.isChecked()) {
                cb_add_favorite.setChecked(false);
                mapButtons.setVisibility(View.GONE);

                LinearLayout newPlacemarkLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.new_placemark_dialog, null);
                EditText et_name = newPlacemarkLayout.findViewById(R.id.et_name);
                RadioButton rb_general = newPlacemarkLayout.findViewById(R.id.rb_general);
                RadioButton rb_special = newPlacemarkLayout.findViewById(R.id.rb_special);
                RadioButton rb_weapon = newPlacemarkLayout.findViewById(R.id.rb_weapon);

                AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialog);
                builder.setTitle(activity.getString(R.string.add_placemark));
                builder.setCancelable(false);
                builder.setView(newPlacemarkLayout);
                builder.setPositiveButton(MainActivity.ctx.getString(R.string.ok), (dialog, which) -> {
                    int type = -1;
                    if(rb_general.isChecked())  type = 0;
                    if(rb_special.isChecked())  type = 1;
                    if(rb_weapon.isChecked())  type = 2;
                    add_point(p,et_name.getText().toString(),type);

                    MainActivity.hide_keyboard(newPlacemarkLayout);
//                    Tab_Messenger.showToast("Point added to favorites...");
                });
                builder.setNegativeButton(MainActivity.ctx.getString(R.string.cancel), (dialog, which) -> {
                    try
                    {
                        MainActivity.hide_keyboard(newPlacemarkLayout);
                        dialog.cancel();
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                });
                builder.show();
            }
            else
            if(cb_add_path.isChecked()) {
                g_points.add(p);
                g_polyline.setPoints(g_points);
                //                    path_overlay.update_points(points);
                kmlOverlay.invalidate();
                map.postInvalidate();
//                Tab_Messenger.showToast("Path point add...");
                tv_geom_info.setText(g_points.size()+" "+getString(R.string.points));
            }
            else
            if(cb_add_polygon.isChecked()) {
                g_points.add(p);
                g_polygon.setPoints(g_points);
                //                    path_overlay.update_points(points);
                kmlOverlay.invalidate();
                map.postInvalidate();
//                Tab_Messenger.showToast("Polygon point add...");
                tv_geom_info.setText(g_points.size()+" "+getString(R.string.points));
            }
            else
            if(cb_change_cam_pos.isChecked()) {
                cb_change_cam_pos.setChecked(false);
                mapButtons.setVisibility(View.GONE);
                double fLon,fLat;
                float fAlt;

                String strText = e_altitude.getText().toString();
                float altitude = Float.parseFloat(strText);

                fLon = p.getLongitude();
                fLat = p.getLatitude();
                fAlt = MainActivity.GetHeightJNI(fLon,fLat) + altitude;
//                MainActivity.uav_lon = fLon;
//                MainActivity.uav_lat = fLat;
//                MainActivity.uav_alt = fAlt;
                set_cam_pos(fLon, fLat, fAlt,true);
                Tab_Camera.crosshairView.invalidate();

                MainActivity.save_settings();
//                Tab_Messenger.showToast("Camera position changed...");
            }
            else
            if(cb_change_home_pos.isChecked()) {
                cb_change_home_pos.setChecked(false);
                mapButtons.setVisibility(View.GONE);
                double fLon,fLat,fAlt;
                fLon = p.getLongitude();
                fLat = p.getLatitude();
                fAlt = MainActivity.GetHeightJNI(fLon,fLat);
//                MainActivity.home_lon = fLon;
//                MainActivity.home_lat = fLat;
//                MainActivity.home_alt = fAlt;
                set_home_pos(fLon, fLat,fAlt,true);
                MainActivity.tab_camera.setHomeLocation();

                MainActivity.save_settings();
//                Tab_Messenger.showToast("Home position changed...");
            }
            else
            if(cb_change_target_pos.isChecked()) {
                cb_change_target_pos.setChecked(false);
                mapButtons.setVisibility(View.GONE);
                double fLon,fLat;
                float fAlt;
                fLon = p.getLongitude();
                fLat = p.getLatitude();
                fAlt = MainActivity.GetHeightJNI(fLon,fLat);
//                MainActivity.target_lon = fLon;
//                MainActivity.target_lat = fLat;
//                MainActivity.target_alt = fAlt;

//                targetPoint.setLongitude(fLon);
//                targetPoint.setLatitude(fLat);
//                targetPoint.setAltitude(fAlt);
//                fov_overlay.setTargetPos(targetPoint);
//                target_Marker.setPosition(targetPoint);
//                mapController.setZoom(17.0);
//                mapController.setCenter(targetPoint);
//                String strText = String.format(Locale.ENGLISH,"%.06f", fLon)+", "+String.format(Locale.ENGLISH,"%.06f", fLat)+", "+String.format(Locale.ENGLISH,"%.01f", fAlt);
//                target_Marker.setTitle("Target point:\n"+strText);
//
//                map.postInvalidate();

                set_target_pos(fLon, fLat, fAlt,true);
                mapController.setCenter(targetPoint);
                mapController.setZoom(17.0);
                if(Tab_Camera.crosshairView != null)   Tab_Camera.crosshairView.invalidate();

                MainActivity.save_settings();
//                Tab_Messenger.showToast("Target position changed...");
            }
            else
            if(cb_look_at.isChecked()) {
                cb_look_at.setChecked(false);
                mapButtons.setVisibility(View.GONE);
                double fLon,fLat;
                float fAlt;
                fLon = p.getLongitude();
                fLat = p.getLatitude();
                fAlt = MainActivity.GetHeightJNI(fLon,fLat);
//                MainActivity.target_lon = fLon;
//                MainActivity.target_lat = fLat;
//                MainActivity.target_alt = fAlt;
                set_target_pos(fLon, fLat, fAlt,true);
                mapController.setCenter(targetPoint);
                mapController.setZoom(17.0);
                Tab_Camera.crosshairView.invalidate();

                float[] res = MainActivity.CalculateAngles(MainActivity.uav_lon,MainActivity.uav_lat,MainActivity.uav_alt,MainActivity.target_lon,MainActivity.target_lat,MainActivity.target_alt);
                MainActivity.lastYaw = (float)MainActivity.db_deg(Math.toDegrees(res[0]));
                if(MainActivity.lastYaw >= 180.0)   MainActivity.lastYaw -= 360.0f;
                MainActivity.lastPitch = (float)Math.toDegrees(res[1]);
                if(timeline != null)    timeline.ChangeUAV_YawByTimeline(MainActivity.lastYaw,MainActivity.lastPitch);

                MainActivity.save_settings();
//                Tab_Messenger.showToast("Look at..."+Float.toString(MainActivity.lastPitch));
            }
            else
            if(cb_goto.isChecked()) {
                cb_goto.setChecked(false);
                mapButtons.setVisibility(View.GONE);
                double fLon,fLat;
                fLon = p.getLongitude();
                fLat = p.getLatitude();

                String strText = e_altitude.getText().toString();
                float altitude = Float.parseFloat(strText);

                if(timeline != null)    timeline.GotoByTimeline(fLon,fLat,altitude);
//                Tab_Messenger.showToast("Goto...");
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return true;
    }
    public double getAltitudeEGM96Correction(double Latitude, double Longitude){
        double _AltitudeEGM96Correction = 0;
        if(Tab_Map.gps_egm96){
            EGM96 egm96 = EGM96.getInstance();
            if (egm96 != null) {
                if (egm96.isEGMGridLoaded()) _AltitudeEGM96Correction = egm96.getEGMCorrection(Latitude, Longitude);
            }
        }
        return _AltitudeEGM96Correction;
    }

    static class LooperThread extends Thread {
        public Handler handler;
        @Override
        public void run() {
            Looper.prepare();
            handler = new Handler(Looper.getMainLooper()) {
                @SuppressLint("HandlerLeak")
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    MapViewerView.tv_active_name.setText("received: " + msg.what);
//            showMessage("received: " + msg.what);
                }
            };
            Looper.loop();
        }
    }

//    Handler handler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(@NonNull android.os.Message msg) {
//            super.handleMessage(msg);
//            MapViewerView.tv_active_name.setText("received: " + msg.what);
////            showMessage("received: " + msg.what);
//        };
//    };

    public void showMessage(String message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                activity.runOnUiThread(() -> {
                    try {
                        mv_utils.playResource(MainActivity.ctx, R.raw.ding_dong);
//                            text_compass.setText(message);
//                            text_compass.invalidate();
                        if(cross_overlay != null) {
                            cross_overlay.setMessage(message);
                            map.invalidate();
//                            MapViewerView.process_click(R.id.radio_map);
//                            MapViewerView.showMessageDialog();
                        }
                        mv_utils.refresh_fragment(tab_map);
                        view.invalidate();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                });
            }
        }).start();
    }

    public void hide_favorites(){
        cb_favorites.setChecked(false);
        cb_search.setChecked(false);
        MainActivity.hide_keyboard(hsv_favorites);
        hsv_favorites.setVisibility(View.GONE);
        view.invalidate();
        map.postInvalidate();
    }

    public void hide_search(){
        cb_favorites.setChecked(false);
        cb_search.setChecked(false);
        MainActivity.hide_keyboard(hsv_search);
        hsv_search.setVisibility(View.GONE);
        view.invalidate();
        map.postInvalidate();
    }

    public void setRulerTextSize(float size){
        tv_update_ruler.setTextSize(size);
        tv_distance.setTextSize(size);
        et_ruler_distance.setTextSize(size);
        tv_heading.setTextSize(size);
        et_ruler_azimuth.setTextSize(size);
        tv_add_start.setTextSize(size);
        tv_measurements2.setTextSize(size);
        tv_add_end.setTextSize(size);
        tv_measurements3.setTextSize(size);
        et_angle.setTextSize(size);
    }

    public void setOverlayVisible(KmlPlacemark placemark, boolean value){
        if(placemark == null)   return;
        if(placemark.overlay == null)   return;
        if(placemark.overlay instanceof MyMarker){
            MyMarker marker = (MyMarker)placemark.overlay;
            marker.setEnabled(value);
        }else if(placemark.overlay instanceof MyPolyline){
            MyPolyline polyline = (MyPolyline)placemark.overlay;
            polyline.setEnabled(value);
        }else if(placemark.overlay instanceof MyPolygon){
            MyPolygon polygon = (MyPolygon)placemark.overlay;
            polygon.setEnabled(value);
        }
    }
}
