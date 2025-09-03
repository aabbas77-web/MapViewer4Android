package com.oghab.mapviewer.mapviewer;

/**
 * @author Ali Abbas
 */

import android.app.AlertDialog;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SizeF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.oghab.mapviewer.MApplication;
import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;
import com.oghab.mapviewer.utils.FileHelper;
import com.oghab.mapviewer.utils.ModuleVerificationUtil;
import com.oghab.mapviewer.utils.mv_utils;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Preview;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.otaliastudios.cameraview.size.Size;

import org.osmdroid.util.GeoPoint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.gimbal.CapabilityKey;
import dji.common.gimbal.Rotation;
import dji.common.gimbal.RotationMode;
import dji.common.model.LocationCoordinate2D;
import dji.common.util.CommonCallbacks;
import dji.common.util.DJIParamCapability;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.KeyManager;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.products.Aircraft;
import dji.ux.widget.FPVWidget;

import static dji.keysdk.FlightControllerKey.HOME_LOCATION_LATITUDE;
import static dji.keysdk.FlightControllerKey.HOME_LOCATION_LONGITUDE;
import static dji.keysdk.FlightControllerKey.HOME_POINT_ALTITUDE;
import static dji.keysdk.FlightControllerKey.MAX_FLIGHT_HEIGHT;
import static dji.keysdk.FlightControllerKey.MAX_FLIGHT_RADIUS;
import static dji.keysdk.FlightControllerKey.MAX_FLIGHT_RADIUS_ENABLED;

//import android.arch.lifecycle.LifecycleOwner;
//import android.support.design.widget.TabLayout;
//import android.support.annotation.NonNull;

public class Tab_Camera extends Fragment
        implements View.OnClickListener {
    private static final String TAG = "Camera";

    private dji.ux.widget.FPVWidget fpv_camera = null;
    //2020.03.04
    private dji.ux.widget.FPVOverlayWidget fpv_overlay = null;
//2020.03.04
//    private dji.ux.widget.VisionWidget vision_view = null;
//2020.03.04
//    private dji.ux.widget.BatteryWidget battery_view = null;
//    private dji.ux.widget.ConnectionWidget connection_view = null;
    //2020.03.04
//    private dji.ux.panel.CameraSettingExposurePanel camera_setting_exposure_view = null;
//2020.03.04
//    private dji.ux.widget.FocusModeWidget focus_mode_view = null;
//2020.03.04
//    private dji.ux.widget.AccessLockerWidget access_locker_view = null;

//    private DJICodecManager mCodecManager;
    protected FlightController flightController;

    static public int idx = 0;

    static CheckBox cb_LookAt;
    static public LinearLayout table_layout;
    static public FrameLayout frame_layout;
    CheckBox cb_status, cb_grayscale;
    static public CheckBox sw_cam_show;
    static public CheckBox sw_broadcast_camera,sw_broadcast_map_status, cb_project_on_map, cb_auto_localize;
    private View view;
    TextView tv_mission_progress;
    ImageView b_send_image_snapshot_to_server, b_send_video_snapshot_to_server, b_switch_camera;
    TextView tv_broadcast_interval, tv_broadcast_scale;
    Spinner s_broadcast_scale, s_broadcast_interval;
    static FrameView iv_preview;
    Spinner s_cam_text_size;

    static public long broadcast_interval = 100;
    static public float broadcast_scale = 0.25f;

    static public ColorCrosshairView crosshairView = null;
    static public ColorCrosshairView crosshairView2 = null;
    ImageView iv_localize;

    Button b_camera_settings,b_gimbal_reset,cam_localize,cam_calibrate,b_cam_reset;

    public CameraView cameraView = null;
    static public boolean isTakingFrame = false;

    public void update_status(boolean bUpdate)
    {
        update_status0(bUpdate);
    }

    public void update_status0(boolean bUpdate)
    {
        MainActivity.activity.runOnUiThread(() -> {
            try
            {
                if((MainActivity.tab_map != null) && (Tab_Map.map != null)) {
                    MainActivity.tab_map.set_cam_pos(MainActivity.uav_lon, MainActivity.uav_lat, MainActivity.uav_alt, false);
                    MainActivity.tab_map.set_uav_yaw(MainActivity.uav_yaw, false);
                    MainActivity.tab_map.set_camera_azi(MainActivity.image_yaw, false);
                    MainActivity.tab_map.set_home_pos(MainActivity.home_lon, MainActivity.home_lat, MainActivity.home_alt, false);

                    if ((MainActivity.tab_map.cb_follow != null) && MainActivity.tab_map.cb_follow.isChecked()) {
                        if(MainActivity.bNavigation) {
                            if(mv_LocationOverlay.curr_location != null) {
                                if (MainActivity.checkGpsCoordinates(mv_LocationOverlay.curr_location.getLatitude(), mv_LocationOverlay.curr_location.getLongitude())) {
                                    GeoPoint p = new GeoPoint(mv_LocationOverlay.curr_location.getLatitude(), mv_LocationOverlay.curr_location.getLongitude(), mv_LocationOverlay.curr_location.getAltitude());
                                    Tab_Map.map.getController().setCenter(p);

                                    // Auto Rotate Map
                                    if (Tab_Map.auto_rotate_map) {
                                        Tab_Map.map.setMapOrientation(-mv_LocationOverlay.curr_location.getBearing());
                                    }
                                }
                            }
                        }else{
                            if (MainActivity.checkGpsCoordinates(MainActivity.uav_lat, MainActivity.uav_lon)) {
                                GeoPoint p = new GeoPoint(MainActivity.uav_lat, MainActivity.uav_lon, MainActivity.uav_alt);
                                Tab_Map.map.getController().setCenter(p);
                            }
                        }
                    }

                    if ((MainActivity.tab_map.cb_track != null) && MainActivity.tab_map.cb_track.isChecked()) {
                        if((MainActivity.tab_map.track_points != null) && (MainActivity.tab_map.track_polyline != null)){
                            if(MainActivity.bNavigation)
                            {
                                if(mv_LocationOverlay.curr_location != null) {
                                    GeoPoint p = new GeoPoint(mv_LocationOverlay.curr_location.getLatitude(), mv_LocationOverlay.curr_location.getLongitude(),mv_LocationOverlay.curr_location.getAltitude());
                                    if(MainActivity.tab_map.add_point_to_track(p)){
                                        MainActivity.tab_map.track_polyline.setPoints(MainActivity.tab_map.track_points);
                                    }
                                }
                            }else{
                                GeoPoint p = new GeoPoint(MainActivity.uav_lat, MainActivity.uav_lon, MainActivity.uav_alt);
                                if(MainActivity.tab_map.add_point_to_track(p)){
                                    MainActivity.tab_map.track_polyline.setPoints(MainActivity.tab_map.track_points);
                                }
                            }
                        }
                    }

//                                if(Tab_Map.cb_target_lock.isChecked())
//                                {
//                                    float[] res = MainActivity.CalculateAnglesJNI(MainActivity.uav_lon,MainActivity.uav_lat,MainActivity.uav_alt,MainActivity.target_lon,MainActivity.target_lat,MainActivity.target_alt);
//                                    MainActivity.lastYaw = (float)MainActivity.db_deg(Math.toDegrees((double)res[0]));
//                                    if(MainActivity.lastYaw >= 180.0)   MainActivity.lastYaw -= 360.0f;
//                                    MainActivity.lastPitch = (float)Math.toDegrees((double)res[1]);
//                                    MainActivity.tab_map.timeline.ChangeUAV_YawByTimeline(MainActivity.lastYaw,MainActivity.lastPitch);
//
//                                    MainActivity.image_yaw = MainActivity.lastYaw;
//                                    MainActivity.image_pitch = MainActivity.lastPitch;
//                                    MainActivity.image_roll = 0.0f;
//
//                                    MainActivity.tab_map.set_camera_azi(MainActivity.image_yaw,false);
//                                }

                    if (crosshairView != null) crosshairView.invalidate();
                    if (!MainActivity.bIsSimulating) {
                        MainActivity.tab_map.updateInfo(false);
                    }

//                    if((fpv_camera != null) && (fpv_camera.getVisibility() == View.VISIBLE)) {
//                        Bitmap bmp = fpv_camera.getBitmap();
//                        Tab_Map.groundOverlay.setImage(bmp);
//                    }else
//                    if((cameraView != null) && (cameraView.getVisibility() == View.VISIBLE)) {
////                        cameraView.takePictureSnapshot();
//                        Bitmap bmp = cameraView.getDrawingCache();
//                        Tab_Map.groundOverlay.setImage(bmp);
//                    }

                    if(bUpdate) Tab_Map.map.postInvalidate();
                }
            }
            catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
            }
        });
    }

    private final Handler customHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));

    private final Runnable updateTimerThread = new Runnable() {
        public void run() {
            try
            {
                update_status0(false);

                customHandler.postDelayed(this, 1000);
            }
            catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
                customHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        try
        {
            if(MApplication.isRealDevice()) {
                view = inflater.inflate(R.layout.tab_camera, container, false);
            }
            else
            {
                view = inflater.inflate(R.layout.tab_camera_emulator, container, false);
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        if(view != null)    init(view);

        customHandler.postDelayed(updateTimerThread, 50);

//            initPreviewerTextureView();
        notifyStatusChange();

        return view;
    }

//    public Tab_Camera(Context context) {
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
//    public Tab_Camera(Context context, AttributeSet attrs) {
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
//    public Tab_Camera(Context context, AttributeSet attrs, int defStyle) {
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
//
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        onResume();
////        DJISampleApplication.getEventBus().post(new MainActivity.RequestStartFullScreenEvent());
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        onPause();
////        DJISampleApplication.getEventBus().post(new MainActivity.RequestEndFullScreenEvent());
////        tearDownListeners();
//        super.onDetachedFromWindow();
//    }

    String[] scales = { "0.125", "0.25", "0.5", "0.75", "1.0"};
    String[] intervals = { "1", "10", "20", "30", "40", "50", "100", "200", "250", "500", "1000"};
    int previous_size = 0;

    String[] cam_text_sizes = {"2","4","6","8","10","12","14","16","18","20","22","24","26","28","30","32"};
    static public float cam_text_size = 10;
    static public int cam_text_size_index = 4;
    static public int cameraSettingsIdx = 0;

    private void init(View view) {
        try
        {
            this.view = view;
            if(view == null)
            {
                MainActivity.MyLogInfo("view == null");
                return;
            }

//            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
//            layoutInflater.inflate(R.layout.activity_default_widgets, this, true);
//            inflate(getContext(), R.layout.activity_default_widgets, this);
//            inflate(context, R.layout.tab_camera, this);

            // color id view
            crosshairView = view.findViewById(R.id.crosshair);
            crosshairView2 = new ColorCrosshairView(MainActivity.ctx, null);
            crosshairView2.camera_status = false;

            b_camera_settings = view.findViewById(R.id.b_camera_settings);
            b_camera_settings.setOnClickListener(this);
            if(MainActivity.bNavigation)
                b_camera_settings.setVisibility(View.GONE);
            else
                b_camera_settings.setVisibility(View.VISIBLE);

            b_gimbal_reset = view.findViewById(R.id.b_gimbal_reset);
            b_gimbal_reset.setOnClickListener(this);
            if(MainActivity.isDevelpoment()) {
                if (MainActivity.bNavigation)
                    b_gimbal_reset.setVisibility(View.GONE);
                else
                    b_gimbal_reset.setVisibility(View.VISIBLE);
            }else{
                b_gimbal_reset.setVisibility(View.GONE);
            }

            cam_localize = view.findViewById(R.id.cam_localize);
            cam_localize.setOnClickListener(this);

            cam_calibrate = view.findViewById(R.id.cam_calibrate);
            cam_calibrate.setOnClickListener(this);
            if(MainActivity.isDevelpoment())
                cam_calibrate.setVisibility(View.VISIBLE);
            else
                cam_calibrate.setVisibility(View.GONE);

            b_cam_reset = view.findViewById(R.id.b_cam_reset);
            b_cam_reset.setOnClickListener(this);
            if(MainActivity.isDevelpoment())
                b_cam_reset.setVisibility(View.VISIBLE);
            else
                b_cam_reset.setVisibility(View.GONE);

            fpv_camera = view.findViewById(R.id.fpv_camera);// ok
            if(fpv_camera != null){
                fpv_camera.setVideoSource(FPVWidget.VideoSource.AUTO);
                if(MainActivity.bNavigation)
                    fpv_camera.setVisibility(View.GONE);
                else
                    fpv_camera.setVisibility(View.VISIBLE);
            }

//            if(MainActivity.isRealDevice()) {
//                if (fpv_camera == null) {
//                    fpv_camera = new dji.ux.widget.FPVWidget(MainActivity.ctx);
//                    fpv_camera.setVideoSource(FPVWidget.VideoSource.AUTO);
//                    FrameLayout fpv_camera_placeholder = view.findViewById(R.id.fpv_camera_placeholder);
//                    fpv_camera_placeholder.addView(fpv_camera);
//                }
//            }

//2020.03.04
//            if(fpv_overlay == null)
//            {
//                fpv_overlay = new dji.ux.widget.FPVOverlayWidget(MainActivity.ctx);
//                LinearLayout fpv_overlay_placeholder = view.findViewById(R.id.fpv_overlay_placeholder);
//                fpv_overlay_placeholder.addView(fpv_overlay);
            fpv_overlay = view.findViewById(R.id.fpv_overlay);
            if(fpv_overlay != null){
                if(MainActivity.bNavigation)
                    fpv_overlay.setVisibility(View.GONE);
                else
                    fpv_overlay.setVisibility(View.VISIBLE);
                fpv_overlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.set_fullscreen();
                        Tab_Messenger.showToast("Ok");
                    }
                });
            }
//            }

//2020.03.04
//            if(vision_view == null)
//            {
//                vision_view = new dji.ux.widget.VisionWidget(MainActivity.ctx,null);
//                LinearLayout vision_placeholder = view.findViewById(R.id.vision_placeholder);
//                vision_placeholder.addView(vision_view);
//            }

//            if(battery_view == null)
//            {
//                battery_view = new dji.ux.widget.BatteryWidget(MainActivity.ctx);
//                LinearLayout battery_view_placeholder = view.findViewById(R.id.battery_placeholder);
//                battery_view_placeholder.addView(battery_view);
//            }

            // bug: exception
//            if(connection_view == null)
//            {
//                connection_view = new dji.ux.widget.ConnectionWidget(MainActivity.ctx);
//                LinearLayout connection_view_placeholder = view.findViewById(R.id.connection_placeholder);
//                connection_view_placeholder.addView(connection_view);
//            }

//            if(camera_setting_exposure_view == null)
//            {
//                camera_setting_exposure_view = new dji.ux.panel.CameraSettingExposurePanel(MainActivity.ctx);
//                LinearLayout camera_setting_exposure_placeholder = view.findViewById(R.id.camera_setting_exposure_placeholder);
//                camera_setting_exposure_placeholder.addView(camera_setting_exposure_view);
//            }

//            if(focus_mode_view == null)
//            {
//                focus_mode_view = new dji.ux.widget.FocusModeWidget(MainActivity.ctx);
//                LinearLayout focus_mode_placeholder = view.findViewById(R.id.focus_mode_placeholder);
//                focus_mode_placeholder.addView(focus_mode_view);
//            }

//            if(access_locker_view == null)
//            {
//                access_locker_view = new dji.ux.widget.AccessLockerWidget(MainActivity.ctx);
//                LinearLayout access_locker_placeholder = view.findViewById(R.id.access_locker_placeholder);
//                access_locker_placeholder.addView(access_locker_view);
//            }

            cb_LookAt = view.findViewById(R.id.cb_LookAt);
            if(cb_LookAt != null)   cb_LookAt.setOnClickListener(this);

            cb_status = view.findViewById(R.id.cb_status);
            if(cb_status != null){
                cb_status.setOnClickListener(this);

                SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                crosshairView.camera_status = settings.getBoolean("camera_status", true);
                cb_status.setChecked(crosshairView.camera_status);
            }

            cb_grayscale = view.findViewById(R.id.cb_grayscale);
            if(cb_grayscale != null){
                cb_grayscale.setOnClickListener(this);

                SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                boolean grayscale = settings.getBoolean("grayscale", false);
                cb_grayscale.setChecked(grayscale);
            }

            sw_cam_show = view.findViewById(R.id.sw_cam_show);
            sw_cam_show.setOnClickListener(this);
            if(!MainActivity.bNavigation) {
                sw_cam_show.setVisibility(View.GONE);
            }

            sw_broadcast_camera = view.findViewById(R.id.sw_broadcast_camera);
            sw_broadcast_camera.setOnClickListener(this);

            sw_broadcast_map_status = view.findViewById(R.id.sw_broadcast_map_status);
            sw_broadcast_map_status.setOnClickListener(this);

            cb_project_on_map = view.findViewById(R.id.cb_project_on_map);
            cb_project_on_map.setOnClickListener(this);
            if(MainActivity.isDevelpoment())
                cb_project_on_map.setVisibility(View.VISIBLE);
            else
                cb_project_on_map.setVisibility(View.GONE);

            cb_auto_localize = view.findViewById(R.id.cb_auto_localize);
            cb_auto_localize.setOnClickListener(this);

            table_layout = view.findViewById(R.id.table_layout);
            if(table_layout != null)   table_layout.setVisibility(View.GONE);

            frame_layout = view.findViewById(R.id.frame_layout);

            iv_localize = view.findViewById(R.id.iv_localize);
            iv_localize.setOnClickListener(this);

            tv_mission_progress = view.findViewById(R.id.tv_mission_progress);

            b_send_image_snapshot_to_server = view.findViewById(R.id.b_send_image_snapshot_to_server);
            b_send_image_snapshot_to_server.setOnClickListener(this);

            b_send_video_snapshot_to_server = view.findViewById(R.id.b_send_video_snapshot_to_server);
            b_send_video_snapshot_to_server.setOnClickListener(this);

            b_switch_camera = view.findViewById(R.id.b_switch_camera);
            b_switch_camera.setOnClickListener(this);

            s_cam_text_size = view.findViewById(R.id.s_cam_text_size);

            tv_broadcast_scale = view.findViewById(R.id.tv_broadcast_scale);
            s_broadcast_scale = view.findViewById(R.id.s_broadcast_scale);
            ArrayAdapter scalesAdapter = new ArrayAdapter(MainActivity.ctx,android.R.layout.simple_spinner_item, scales);
            scalesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            s_broadcast_scale.setAdapter(scalesAdapter);
            s_broadcast_scale.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    broadcast_scale = Float.parseFloat(scales[position]);
                    SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("broadcast_scale_index", position);
                    editor.apply();
                    MainActivity.set_fullscreen();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    MainActivity.set_fullscreen();
                }
            });

            iv_preview = view.findViewById(R.id.iv_preview);
            iv_preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Tab_Messenger.close_preview();
                }
            });

            tv_broadcast_interval = view.findViewById(R.id.tv_broadcast_interval);
            s_broadcast_interval = view.findViewById(R.id.s_broadcast_interval);
            ArrayAdapter intervalsAdapter = new ArrayAdapter(MainActivity.ctx,android.R.layout.simple_spinner_item, intervals);
            intervalsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            s_broadcast_interval.setAdapter(intervalsAdapter);
            s_broadcast_interval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    broadcast_interval = mv_utils.parseInt(intervals[position]);
                    SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("broadcast_interval_index", position);
                    editor.apply();
                    MainActivity.set_fullscreen();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    MainActivity.set_fullscreen();
                }
            });

            SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
            MainActivity.bIsCalibrated = settings.getBoolean("bIsCalibrated", MainActivity.bIsCalibrated);
            int index;
            index = settings.getInt("broadcast_scale_index", 1);
            s_broadcast_scale.setSelection(index);
            index = settings.getInt("broadcast_interval_index", 6);
            s_broadcast_interval.setSelection(index);

            cam_text_size_index = settings.getInt("cam_text_size_index", cam_text_size_index);
            cameraSettingsIdx = settings.getInt("cameraSettingsIdx", cameraSettingsIdx);
            if(!MainActivity.bNavigation) {
                CitiesAdapter.select_item(cameraSettingsIdx);
            }

            ArrayAdapter camScalesAdapter = new ArrayAdapter(MainActivity.ctx,android.R.layout.simple_spinner_item, cam_text_sizes);
            camScalesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            s_cam_text_size.setAdapter(camScalesAdapter);
            s_cam_text_size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    cam_text_size = Float.parseFloat(cam_text_sizes[position]);
                    SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("cam_text_size_index", position);
                    editor.apply();
                    MainActivity.set_fullscreen();
                    iv_preview.setTextSize(cam_text_size);
                    iv_preview.invalidate();
                    crosshairView.setTextSize(cam_text_size);
                    crosshairView.invalidate();
                    crosshairView2.setTextSize(cam_text_size);
                    crosshairView2.invalidate();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    MainActivity.set_fullscreen();
                }
            });

            // load settings
            // TODO test calibration
            float[] res = MainActivity.GetCalibrationDataJNI();
            MainActivity.dYaw = res[0];
            MainActivity.dPitch = res[1];
            MainActivity.dRoll = res[2];

            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
//                ViewScreenshot viewScreenshot = null;
                @Override
                public void run() {
                    MainActivity.activity.runOnUiThread(() -> {
                        try {
                            if(sw_broadcast_camera.isChecked() || cb_project_on_map.isChecked() || cb_auto_localize.isChecked()){
                                try
                                {
                                    if(!isTakingFrame){
                                        if(fpv_camera != null){
                                            if(fpv_camera.getVisibility() == View.VISIBLE){
                                                try {
                                                    isTakingFrame = true;
                                                    MainActivity.activity.runOnUiThread(() -> {
                                                        try {
                                                            Bitmap bmp = fpv_camera.getBitmap();
                                                            if(bmp != null){
                                                                Canvas canvas = new Canvas(bmp);
                                                                Bitmap icon = BitmapFactory.decodeResource(MainActivity.activity.getResources(), R.drawable.oghab_oti_h64);
                                                                canvas.drawBitmap(icon,bmp.getWidth() - icon.getWidth(),bmp.getHeight() - icon.getHeight(),null);
                                                                int size = bmp.getWidth() * bmp.getHeight();
                                                                if(size != previous_size) {
                                                                    crosshairView2.updatePath(bmp.getWidth(), bmp.getHeight());
                                                                }
                                                                previous_size = size;
                                                                crosshairView2.my_draw(canvas);
                                                                if(cb_grayscale.isChecked()){
                                                                    bmp = FileHelper.toGrayscale(bmp);
                                                                }
                                                                if(broadcast_scale != 1.0) {
                                                                    bmp = Bitmap.createScaledBitmap(bmp, (int) Math.round(broadcast_scale * bmp.getWidth()), (int) Math.round(broadcast_scale * bmp.getHeight()), true);
                                                                }

                                                                if(cb_project_on_map.isChecked()){
                                                                    MainActivity.w = bmp.getWidth();
                                                                    MainActivity.h = bmp.getHeight();
                                                                    if(Localize4()) {
                                                                        Tab_Map.groundOverlay.setImage(bmp);
                                                                        Tab_Map.groundOverlay.setPosition(new GeoPoint(Tab_Map.top_left.getLatitude(), Tab_Map.top_left.getLongitude())
                                                                                , new GeoPoint(Tab_Map.top_right.getLatitude(), Tab_Map.top_right.getLongitude())
                                                                                , new GeoPoint(Tab_Map.bottom_right.getLatitude(), Tab_Map.bottom_right.getLongitude())
                                                                                , new GeoPoint(Tab_Map.bottom_left.getLatitude(), Tab_Map.bottom_left.getLongitude())
                                                                        );
                                                                        Tab_Map.groundOverlay.setEnabled(true);
                                                                        Tab_Map.map.postInvalidate();
                                                                    }else{
                                                                        Tab_Map.groundOverlay.setEnabled(false);
                                                                    }
                                                                    if(!sw_broadcast_camera.isChecked()) {
                                                                        isTakingFrame = false;
                                                                    }
                                                                }else{
                                                                    Tab_Map.groundOverlay.setEnabled(false);
                                                                }

                                                                if(cb_auto_localize.isChecked()){
                                                                    MainActivity.w = bmp.getWidth();
                                                                    MainActivity.h = bmp.getHeight();
                                                                    if(Localize(false)) {
                                                                        Tab_Map.map.postInvalidate();
                                                                    }
                                                                    if(!sw_broadcast_camera.isChecked()) {
                                                                        isTakingFrame = false;
                                                                    }
                                                                }

                                                                if(sw_broadcast_camera.isChecked()){
                                                                    File file = new File(MainActivity.strFrameCaptured);
                                                                    FileHelper.save_image_as_jpg(file, bmp);
                                                                    MainActivity.saveExif(MainActivity.strFrameCaptured,ColorCrosshairView.get_status(false));
                                                                    Tab_Messenger.sendFile(MainActivity.strFrameCaptured, false, "Frame sending...", false, new tcp_io_handler.SendCallback() {
                                                                        @Override
                                                                        public void onFinish(int error) {
                                                                            if(error != tcp_io_handler.TCP_OK) {
                                                                                if (MainActivity.IsDebugJNI()) {
                                                                                    MainActivity.MyLogInfoSilent("The Current Line Number is " + Arrays.toString(new Throwable().getStackTrace()));
                                                                                }
                                                                            }
                                                                            isTakingFrame = false;
                                                                        }
                                                                    });
                                                                }
                                                            }else{
                                                                isTakingFrame = false;
                                                            }
                                                        } catch (Throwable ex){
                                                            isTakingFrame = false;
                                                            MainActivity.MyLog(ex);
                                                        }
                                                    });
                                                } catch (Throwable ex){
                                                    isTakingFrame = false;
                                                    MainActivity.MyLog(ex);
                                                }
                                            }
                                        }
                                    }
                                }
                                catch (Throwable ex)
                                {
                                    MainActivity.MyLog(ex);
                                }

//                                try {
//                                    if(viewScreenshot == null){
//                                        viewScreenshot = new ViewScreenshot();
//                                        if(Build.VERSION.SDK_INT < 26) {
//                                            Bitmap bitmap = viewScreenshot.getBitmapFromView(MainActivity.activity.getWindow().getDecorView());
//                                            File file = new File(MainActivity.strFrameCaptured);
//                                            FileHelper.save_image_as_jpg(file, bitmap);
//                                MainActivity.saveExif(MainActivity.strFrameCaptured,ColorCrosshairView.get_status());
//                                            Tab_Messenger.sendFile(MainActivity.strFrameCaptured, true, "Frame sending...", false, new tcp_io_handler.SendCallback() {
//                                                    @Override
//                                                    public void onFinish(int error) {
//                                                        viewScreenshot = null;
//                                                    }
//                                                }
//                                            );
//                                        }else{
//                                            viewScreenshot.take(cameraView, MainActivity.activity, new ViewScreenshot.PostTake() {
//                                                @Override
//                                                public void onSuccess(Bitmap bitmap) {
//                                                    File file = new File(MainActivity.strFrameCaptured);
//                                                    FileHelper.save_image_as_jpg(file, bitmap);
//                                MainActivity.saveExif(MainActivity.strFrameCaptured,ColorCrosshairView.get_status());
//                                                    Tab_Messenger.sendFile(MainActivity.strFrameCaptured, true, "Frame sending...", false, new tcp_io_handler.SendCallback() {
//                                                        @Override
//                                                        public void onFinish(int error) {
//                                                            viewScreenshot = null;
//                                                        }
//                                                    });
//                                                }
//                                                @Override
//                                                public void onFailure(int error) {
//                                                    viewScreenshot = null;
//                                                }
//                                            });
//                                        }
//                                    }
//                                } catch (Throwable ex){
//                                    MainActivity.MyLog(ex);
//                                }
                            }
                            handler.postDelayed(this, broadcast_interval);
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    });
                }
            };
            handler.postDelayed(runnable, 1);

//            if(MainActivity.isRealDevice()) {
                cameraView = view.findViewById(R.id.cameraView);
//                cameraView.setPreview(Preview.TEXTURE);// ok for screen capture but not for video capture
//                cameraView.setPreview(Preview.SURFACE);// inv for screen capture but ok for video capture
                cameraView.setPreview(Preview.GL_SURFACE);// inv

//                cameraView.setFrameProcessingMaxWidth(maxWidth);
//                cameraView.setFrameProcessingMaxHeight(maxWidth);

                cameraView.addFrameProcessor(new FrameProcessor() {
                    @Override
                    public void process(@NonNull Frame frame) {
                        if(!(sw_broadcast_camera.isChecked() || cb_project_on_map.isChecked() || cb_auto_localize.isChecked())){
                            isTakingFrame = false;
                            return;
                        }
                        if(cameraView.getVisibility() != View.VISIBLE){
                            return;
                        }
                        if(isTakingFrame)   return;
                        isTakingFrame = true;
                        long start_time = System.currentTimeMillis();

//                        long startTime = System.nanoTime();
//                        // ... the code being measured ...
//                        long estimatedTime = System.nanoTime() - startTime;

                        long time = frame.getTime();
                        int format = frame.getFormat();
                        int userRotation = frame.getRotationToUser();
                        int viewRotation = frame.getRotationToView();
                        if (frame.getDataClass() == byte[].class) {
                            try {
                                byte[] data = frame.getData();
                                Size size = frame.getSize();
                                YuvImage yuv = new YuvImage(data, ImageFormat.NV21, size.getWidth(), size.getHeight(), null);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                yuv.compressToJpeg(new Rect(0, 0, size.getWidth(), size.getHeight()), 95, stream);
                                byte[] buf = stream.toByteArray();
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inMutable = true;
                                Bitmap bmp = BitmapFactory.decodeByteArray(buf, 0, buf.length, options);
                                if(bmp != null){
                                    Canvas canvas = new Canvas(bmp);
                                    Paint paint = new Paint();
                                    paint.setStyle(Paint.Style.STROKE);
                                    paint.setStrokeWidth(3);
                                    paint.setColor(Color.RED);
                                    canvas.drawRect(0,0,bmp.getWidth(),bmp.getHeight(),paint);
//                                    Bitmap icon = BitmapFactory.decodeResource(MainActivity.activity.getResources(), R.drawable.oghab_mapviewer2);
                                    Bitmap icon = BitmapFactory.decodeResource(MainActivity.activity.getResources(), R.drawable.oghab_oti_h64);
//                                    canvas.drawBitmap(icon,bmp.getWidth() - icon.getWidth(),0,null);
                                    canvas.drawBitmap(icon,bmp.getWidth() - icon.getWidth(),bmp.getHeight() - icon.getHeight(),null);
                                    crosshairView2.updatePath(bmp.getWidth(),bmp.getHeight());
                                    crosshairView2.my_draw(canvas);
                                    if(cb_grayscale.isChecked()){
                                        bmp = FileHelper.toGrayscale(bmp);
                                    }
                                    if(broadcast_scale != 1.0) {
                                        bmp = Bitmap.createScaledBitmap(bmp, (int) Math.round(broadcast_scale * bmp.getWidth()), (int) Math.round(broadcast_scale * bmp.getHeight()), true);
                                    }

                                    if(cb_project_on_map.isChecked()){
                                        MainActivity.w = bmp.getWidth();
                                        MainActivity.h = bmp.getHeight();
                                        if(Localize4()) {
                                            Tab_Map.groundOverlay.setImage(bmp);
                                            Tab_Map.groundOverlay.setPosition(new GeoPoint(Tab_Map.top_left.getLatitude(), Tab_Map.top_left.getLongitude())
                                                    , new GeoPoint(Tab_Map.top_right.getLatitude(), Tab_Map.top_right.getLongitude())
                                                    , new GeoPoint(Tab_Map.bottom_right.getLatitude(), Tab_Map.bottom_right.getLongitude())
                                                    , new GeoPoint(Tab_Map.bottom_left.getLatitude(), Tab_Map.bottom_left.getLongitude())
                                            );
                                            Tab_Map.groundOverlay.setEnabled(true);
                                            Tab_Map.map.postInvalidate();
                                        }else{
                                            Tab_Map.groundOverlay.setEnabled(false);
                                        }
                                        if(!sw_broadcast_camera.isChecked()) {
                                            isTakingFrame = false;
                                        }
                                    }else{
                                        Tab_Map.groundOverlay.setEnabled(false);
                                    }

                                    if(cb_auto_localize.isChecked()){
                                        MainActivity.w = bmp.getWidth();
                                        MainActivity.h = bmp.getHeight();
                                        if(Localize(false)) {
                                            Tab_Map.map.postInvalidate();
                                        }
                                        if(!sw_broadcast_camera.isChecked()) {
                                            isTakingFrame = false;
                                        }
                                    }

                                    if(sw_broadcast_camera.isChecked()){
                                        FileHelper.save_image_as_jpg(new File(MainActivity.strFrameCaptured), bmp);
                                        MainActivity.saveExif(MainActivity.strFrameCaptured,ColorCrosshairView.get_status(false));
                                        Tab_Messenger.sendFile(MainActivity.strFrameCaptured, false, "Frame sending...", false, new tcp_io_handler.SendCallback() {
                                            @Override
                                            public void onFinish(int error) {
                                                if(error != tcp_io_handler.TCP_OK) {
                                                    if (MainActivity.IsDebugJNI()) {
                                                        MainActivity.MyLogInfoSilent("The Current Line Number is " + Arrays.toString(new Throwable().getStackTrace()));
                                                    }
                                                }

                                                // waiting...
                                                long end_time = System.currentTimeMillis();
                                                double execution_time = end_time - start_time;
                                                double delay = broadcast_interval - execution_time;
                                                if(delay > 0){
                                                    try {
                                                        Timer myTimer = new Timer();
                                                        myTimer.schedule(new TimerTask() {
                                                            @Override
                                                            public void run() {
                                                                MainActivity.activity.runOnUiThread(() -> {
                                                                    try {
                                                                        isTakingFrame = false;
                                                                        myTimer.cancel();
                                                                    } catch (Throwable ex) {
                                                                        MainActivity.MyLog(ex);
                                                                    }
                                                                });
                                                            }
                                                        }, (int)delay, (int)delay);
                                                    } catch (Throwable ex) {
                                                        MainActivity.MyLog(ex);
                                                    }
                                                }else{
                                                    isTakingFrame = false;
                                                }
                                            }
                                        });
                                    }else{
                                        isTakingFrame = false;
                                    }
                                }else{
                                    isTakingFrame = false;
                                }
                            } catch (Throwable ex) {
                                isTakingFrame = false;
                                MainActivity.MyLog(ex);
                            }
                        } else if (frame.getDataClass() == Image.class) {
                            try {
                                Tab_Messenger.addError("Image.class: "+frame.getDataClass().getName());
                                Image data = frame.getData();

//                                FileHelper.ImageSaver imageSaver = new FileHelper.ImageSaver(data, new File(MainActivity.strFrameCaptured));
//                                imageSaver.run();
//                                MainActivity.activity.runOnUiThread(imageSaver);

                                ByteBuffer buffer = data.getPlanes()[0].getBuffer();
                                byte[] bytes = new byte[buffer.capacity()];
                                buffer.get(bytes);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inMutable = true;
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                                if(bmp != null){
                                    Canvas canvas = new Canvas(bmp);
                                    Bitmap icon = BitmapFactory.decodeResource(MainActivity.activity.getResources(), R.drawable.oghab_mapviewer2);
                                    canvas.drawBitmap(icon,0,0,null);
                                    if(cb_grayscale.isChecked()){
                                        bmp = FileHelper.toGrayscale(bmp);
                                    }
                                    if(broadcast_scale != 1.0) {
                                        bmp = Bitmap.createScaledBitmap(bmp, (int) Math.round(broadcast_scale * bmp.getWidth()), (int) Math.round(broadcast_scale * bmp.getHeight()), true);
                                    }
                                    FileHelper.save_image_as_jpg(new File(MainActivity.strFrameCaptured), bmp);
                                    MainActivity.saveExif(MainActivity.strFrameCaptured,ColorCrosshairView.get_status(false));
                                    Tab_Messenger.sendFile(MainActivity.strFrameCaptured, false, "Frame sending...", false, new tcp_io_handler.SendCallback() {
                                        @Override
                                        public void onFinish(int error) {
                                            if(error != tcp_io_handler.TCP_OK) {
                                                if (MainActivity.IsDebugJNI()) {
                                                    MainActivity.MyLogInfoSilent("The Current Line Number is " + Arrays.toString(new Throwable().getStackTrace()));
                                                }
                                            }

                                            // waiting...
                                            long end_time = System.currentTimeMillis();
                                            double execution_time = end_time - start_time;
                                            double delay = broadcast_interval - execution_time;
                                            if(delay > 0){
                                                try {
                                                    Timer myTimer = new Timer();
                                                    myTimer.schedule(new TimerTask() {
                                                        @Override
                                                        public void run() {
                                                            MainActivity.activity.runOnUiThread(() -> {
                                                                try {
                                                                    isTakingFrame = false;
                                                                    myTimer.cancel();
                                                                } catch (Throwable ex) {
                                                                    MainActivity.MyLog(ex);
                                                                }
                                                            });
                                                        }
                                                    }, (int)delay, (int)delay);
                                                } catch (Throwable ex) {
                                                    MainActivity.MyLog(ex);
                                                }
                                            }else{
                                                isTakingFrame = false;
                                            }
                                        }
                                    });
                                }else{
                                    isTakingFrame = false;
                                }
                            } catch (Throwable ex) {
                                isTakingFrame = false;
                                MainActivity.MyLog(ex);
                            }
                        }
                    }
                });

                cameraView.addCameraListener(new CameraListener() {
                    @Override
                    public void onCameraOpened(@NonNull CameraOptions options) {
                        super.onCameraOpened(options);
                    }

                    @Override
                    public void onCameraClosed() {
                        super.onCameraClosed();
                    }

                    @Override
                    public void onCameraError(@NonNull CameraException exception) {
                        super.onCameraError(exception);
                    }

                    @Override
                    public void onPictureTaken(@NonNull PictureResult result) {
                        super.onPictureTaken(result);
                        try {
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                MainActivity.activity.runOnUiThread(() -> {
                                    try {
                                        if(result.isSnapshot()){
                                            if(isOneShotOnly){
                                                isOneShotOnly = false;
                                                result.toFile(new File(MainActivity.strImageCaptured), new FileCallback() {
                                                    @Override
                                                    public void onFileReady(@Nullable @org.jetbrains.annotations.Nullable File file) {
                                                        if(file == null){
                                                            Tab_Messenger.showToast("Image not Captured...");
                                                            return;
                                                        }
                                                        MainActivity.saveExif(file.getAbsolutePath(),ColorCrosshairView.get_status(false));
                                                        Tab_Messenger.sendFile(file.getAbsolutePath(), true, "Photo sending...", true, new tcp_io_handler.SendCallback() {
                                                            @Override
                                                            public void onFinish(int error) {
                                                                if(error != tcp_io_handler.TCP_OK) {
                                                                    if (MainActivity.IsDebugJNI()) {
                                                                        MainActivity.MyLogInfoSilent("The Current Line Number is " + Arrays.toString(new Throwable().getStackTrace()));
                                                                    }
                                                                }
                                                                Tab_Messenger.showToast("Image Captured...");
                                                            }
                                                        });
                                                    }
                                                });
                                            }else{
//                                                result.toFile(new File(MainActivity.strFrameCaptured), new FileCallback() {
//                                                    @Override
//                                                    public void onFileReady(@Nullable @org.jetbrains.annotations.Nullable File file) {
//                                                        try{
//                                                            if(file == null){
//                                                                isTakingFrame = false;
//                                                                return;
//                                                            }
//                                                MainActivity.saveExif(MainActivity.strFrameCaptured,ColorCrosshairView.get_status());
//                                                            Tab_Messenger.sendFile(MainActivity.strFrameCaptured, false, "Frame sending...", false, new tcp_io_handler.SendCallback() {
//                                                                @Override
//                                                                public void onFinish(int error) {
//                                                                    isTakingFrame = false;
//                                                                }
//                                                            });
//                                                        } catch (Throwable ex){
//                                                            isTakingFrame = false;
//                                                            MainActivity.MyLog(ex);
//                                                        }
//                                                    }
//                                                });
                                            }
                                        }
                                    } catch (Throwable ex){
                                        MainActivity.MyLog(ex);
                                    }
                                });
                                }
                            };
                            thread.start();
                        } catch (Throwable ex){
                            MainActivity.MyLog(ex);
                        }
                    }

                    @Override
                    public void onVideoTaken(@NonNull VideoResult result) {
                        super.onVideoTaken(result);
                        try {
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    MainActivity.activity.runOnUiThread(() -> {
                                        try {
                                            if(result.isSnapshot()){
                                                File file = result.getFile();
                                                Tab_Messenger.sendFile(file.getAbsolutePath(), true, "Video sending...", true, new tcp_io_handler.SendCallback() {
                                                    @Override
                                                    public void onFinish(int error) {
                                                        if(error != tcp_io_handler.TCP_OK) {
                                                            if (MainActivity.IsDebugJNI()) {
                                                                MainActivity.MyLogInfoSilent("The Current Line Number is " + Arrays.toString(new Throwable().getStackTrace()));
                                                            }
                                                        }
                                                        Tab_Messenger.showToast("Video Captured...");
                                                    }
                                                });
                                            }
                                        } catch (Throwable ex){
                                            MainActivity.MyLog(ex);
                                        }
                                    });
                                }
                            };
                            thread.start();
                        } catch (Throwable ex){
                            MainActivity.MyLog(ex);
                        }
                    }

                    @Override
                    public void onOrientationChanged(int orientation) {
                        super.onOrientationChanged(orientation);
                    }

                    @Override
                    public void onAutoFocusStart(@NonNull PointF point) {
                        super.onAutoFocusStart(point);
                    }

                    @Override
                    public void onAutoFocusEnd(boolean successful, @NonNull PointF point) {
                        super.onAutoFocusEnd(successful, point);
                    }

                    @Override
                    public void onZoomChanged(float newValue, @NonNull float[] bounds, @Nullable @org.jetbrains.annotations.Nullable PointF[] fingers) {
                        super.onZoomChanged(newValue, bounds, fingers);
                    }

                    @Override
                    public void onExposureCorrectionChanged(float newValue, @NonNull float[] bounds, @Nullable @org.jetbrains.annotations.Nullable PointF[] fingers) {
                        super.onExposureCorrectionChanged(newValue, bounds, fingers);
                    }

                    @Override
                    public void onVideoRecordingStart() {
                        super.onVideoRecordingStart();
                    }

                    @Override
                    public void onVideoRecordingEnd() {
                        super.onVideoRecordingEnd();
                    }

                    @Override
                    public void onPictureShutter() {
                        super.onPictureShutter();
                    }
                });
//            }

            s_cam_text_size.setSelection(cam_text_size_index);
            cam_text_size = Float.parseFloat(cam_text_sizes[cam_text_size_index]);
            iv_preview.setTextSize(cam_text_size);
            iv_preview.invalidate();
            crosshairView.setTextSize(cam_text_size);
            crosshairView.invalidate();
            crosshairView2.setTextSize(cam_text_size);
            crosshairView2.invalidate();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void update_camera()
    {
        if(view == null)
        {
            MainActivity.MyLogInfo("view == null");
            return;
        }
        if(!MApplication.isRealDevice()) {
            return;
        }
        try
        {
            int nDJIVisible, nCameraVisible;
            if (MainActivity.bDJIExists) {
                nDJIVisible = View.VISIBLE;
                nCameraVisible = View.GONE;
            } else {
                nDJIVisible = View.GONE;
                nCameraVisible = View.VISIBLE;
            }

//                if(MainActivity.isRealDevice()) {
            if (cameraView != null){
                cameraView.setVisibility(nCameraVisible);
//                        b_send_image_snapshot_to_server.setVisibility(nCameraVisible);
                b_send_video_snapshot_to_server.setVisibility(nCameraVisible);
                b_switch_camera.setVisibility(nCameraVisible);
            }
//                }

            if (fpv_camera != null) fpv_camera.setVisibility(nDJIVisible);

//            view.findViewById(R.id.fpv_overlay).setVisibility(nDJIVisible);// ok
//2020.03.04
//            fpv_overlay.setVisibility(nDJIVisible);

//                view.findViewById(R.id.radar_widget).setVisibility(nDJIVisible);
//                view.findViewById(R.id.map_widget).setVisibility(nDJIVisible);

//                view.findViewById(R.id.compass_calibrating).setVisibility(nDJIVisible);
            view.findViewById(R.id.camera_sensor_cleaning).setVisibility(nDJIVisible);
            view.findViewById(R.id.signal).setVisibility(nDJIVisible);
//            view.findViewById(R.id.remaining_flight_time).setVisibility(nDJIVisible);// ok

//            view.findViewById(R.id.camera).setVisibility(nDJIVisible);// ok
            view.findViewById(R.id.camera).setVisibility(View.GONE);

            view.findViewById(R.id.camera2).setVisibility(nDJIVisible);// ok

            view.findViewById(R.id.manual_focus).setVisibility(nDJIVisible);// ok

            view.findViewById(R.id.dashboard_widget).setVisibility(nDJIVisible);
            view.findViewById(R.id.TakeOffReturnPanel).setVisibility(nDJIVisible);
            view.findViewById(R.id.CameraCapturePanel).setVisibility(nDJIVisible);

//            view.findViewById(R.id.histogram).setVisibility(nDJIVisible);// ok
//            view.findViewById(R.id.histogram).setVisibility(View.GONE);// ok

//            view.findViewById(R.id.camera_setting_exposure).setVisibility(View.GONE);// ok
//2020.03.04
//            camera_setting_exposure_view.setVisibility(View.GONE);

//            view.findViewById(R.id.camera_setting_advanced).setVisibility(View.GONE);// ok
            view.findViewById(R.id.rtk_status).setVisibility(View.GONE);

//            view.findViewById(R.id.color_waveform).setVisibility(View.GONE);// ok

//            view.findViewById(R.id.preflight_check_list).setVisibility(View.GONE);// ok

//            view.findViewById(R.id.spotlight).setVisibility(View.GONE);// ok

//            view.findViewById(R.id.speaker).setVisibility(View.GONE);// ok

//            view.findViewById(R.id.table_layout).setVisibility(View.GONE);
//            view.findViewById(R.id.camera_setting_exposure).setVisibility(nDJIVisible);
//            view.findViewById(R.id.camera_setting_advanced).setVisibility(nDJIVisible);
//            view.findViewById(R.id.rtk_status).setVisibility(nDJIVisible);
//            view.findViewById(R.id.color_waveform).setVisibility(nDJIVisible);
//            view.findViewById(R.id.preflight_check_list).setVisibility(nDJIVisible);
//            view.findViewById(R.id.spotlight).setVisibility(nDJIVisible);
//            view.findViewById(R.id.speaker).setVisibility(nDJIVisible);
//            view.findViewById(R.id.table_layout).setVisibility(nDJIVisible);

//            if(MainActivity.bDJIExists) {
//                if(cameraView != null)  cameraView.setVisibility(View.GONE);
//            }
//            else {
//                if(cameraView != null)  cameraView.setVisibility(View.VISIBLE);
//
//                if(fpv_camera != null)    fpv_camera.setVisibility(View.GONE);
//                view.findViewById(R.id.fpv_overlay).setVisibility(View.GONE);
////                view.findViewById(R.id.radar_widget).setVisibility(View.GONE);
////                view.findViewById(R.id.map_widget).setVisibility(View.GONE);
//                view.findViewById(R.id.compass_calibrating).setVisibility(View.GONE);
//                view.findViewById(R.id.camera_sensor_cleaning).setVisibility(View.GONE);
//                view.findViewById(R.id.signal).setVisibility(View.GONE);
//                view.findViewById(R.id.remaining_flight_time).setVisibility(View.GONE);
//                view.findViewById(R.id.camera).setVisibility(View.GONE);
//                view.findViewById(R.id.camera2).setVisibility(View.GONE);
//                view.findViewById(R.id.manual_focus).setVisibility(View.GONE);
//                view.findViewById(R.id.dashboard_widget).setVisibility(View.GONE);
//                view.findViewById(R.id.TakeOffReturnPanel).setVisibility(View.GONE);
//                view.findViewById(R.id.CameraCapturePanel).setVisibility(View.GONE);
//                view.findViewById(R.id.histogram).setVisibility(View.GONE);
//                view.findViewById(R.id.camera_setting_exposure).setVisibility(View.GONE);
//                view.findViewById(R.id.camera_setting_advanced).setVisibility(View.GONE);
//                view.findViewById(R.id.rtk_status).setVisibility(View.GONE);
//                view.findViewById(R.id.color_waveform).setVisibility(View.GONE);
//                view.findViewById(R.id.preflight_check_list).setVisibility(View.GONE);
//                view.findViewById(R.id.spotlight).setVisibility(View.GONE);
//                view.findViewById(R.id.speaker).setVisibility(View.GONE);
//                view.findViewById(R.id.table_layout).setVisibility(View.GONE);
//            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void toggle_settings() {
        try
        {
            if(table_layout.getVisibility() == View.GONE)
                table_layout.setVisibility(View.VISIBLE);
            else
                table_layout.setVisibility(View.GONE);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    @Override
    public void onStop() {
        try
        {

        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        try
        {
//            initPreviewerTextureView();
            notifyStatusChange();
            if((sw_cam_show != null) && sw_cam_show.isChecked()) {
                if ((cameraView != null) && (!cameraView.isOpened())) {
                    cameraView.open();

                    if(!calculateDeviceCameraFOV()){
                        MainActivity.MyLogInfo("Error in calculateFOV");
                    }
                }
                update_camera();
            }
//            if(MainActivity.isRealDevice()) {
//                if ((cameraView != null) && (!cameraView.isOpened())){
////                    cameraView.setEngine(Engine.CAMERA2);
//                    cameraView.open();
//                }
//                update_camera();
//            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    @Override
    public void onPause() {
        try
        {
            if (ModuleVerificationUtil.isGimbalModuleAvailable()) {
                MApplication.getProductInstance().getGimbal().setStateCallback(null);
            }
//            if(MainActivity.isRealDevice()) {
                if ((cameraView != null) && (cameraView.isOpened())) cameraView.close();
//            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        try
        {
//            if (mCodecManager != null) {
//                mCodecManager.destroyCodec();
//            }
            customHandler.removeCallbacks(updateTimerThread);
//            if(MainActivity.isRealDevice()) {
                if ((cameraView != null) && (cameraView.isOpened())) cameraView.close();
                if (cameraView != null) cameraView.destroy();
//            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        super.onDestroy();
    }

    static public boolean isOneShotOnly = false;

    void localize(){
        try
        {
            if(MainActivity.IsDemoVersionJNI())
                Tab_Messenger.showToast("Localization is not allowed in Demo Version!");
            else {
                if(!Localize(true)){
                    Tab_Messenger.showToast("Localization failed...");
                }
            }

//            Tab_Main.tv_sys_id.setText("System ID: "+Long.toString(MainActivity.GetSystemIdJNI()));
//            if(MainActivity.IsDemoVersionJNI())
//            {
//                Tab_Main.layout_sys_id.setVisibility(View.VISIBLE);
//            }
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
                case R.id.b_send_image_snapshot_to_server: {
                    try
                    {
                        isOneShotOnly = true;
                        if((fpv_camera != null) && (fpv_camera.getVisibility() == View.VISIBLE)){
                            try {
                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        MainActivity.activity.runOnUiThread(() -> {
                                            try {
                                                File file = new File(MainActivity.strImageCaptured);
                                                Bitmap bmp = fpv_camera.getBitmap();
                                                if(bmp != null){
                                                    Canvas canvas = new Canvas(bmp);
                                                    Bitmap icon = BitmapFactory.decodeResource(MainActivity.activity.getResources(), R.drawable.oghab_mapviewer2);
                                                    canvas.drawBitmap(icon,0,0,null);
                                                    FileHelper.save_image_as_jpg(file, bmp);
                                                    MainActivity.saveExif(MainActivity.strImageCaptured,ColorCrosshairView.get_status(false));
                                                    Tab_Messenger.sendFile(MainActivity.strImageCaptured, true, "Photo sending...", true, new tcp_io_handler.SendCallback() {
                                                        @Override
                                                        public void onFinish(int error) {
                                                            if(error != tcp_io_handler.TCP_OK) {
                                                                if (MainActivity.IsDebugJNI()) {
                                                                    MainActivity.MyLogInfoSilent("The Current Line Number is " + Arrays.toString(new Throwable().getStackTrace()));
                                                                }
                                                            }
                                                            Tab_Messenger.showToast("Image Captured...");
                                                        }
                                                    });
                                                }
                                            } catch (Throwable ex){
                                                MainActivity.MyLog(ex);
                                            }
                                        });
                                    }
                                };
                                thread.start();

                                MainActivity.set_fullscreen();
                            } catch (Throwable ex){
                                MainActivity.MyLog(ex);
                            }
                        }else{
                            if((cameraView != null) && (cameraView.getVisibility() == View.VISIBLE)) {
                                cameraView.takePictureSnapshot();
                            }
                        }
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_send_video_snapshot_to_server: {
                    try
                    {
                        if(cameraView.getVisibility() == View.VISIBLE){
                            if(cameraView.isTakingVideo()){
                                cameraView.stopVideo();
                                b_send_video_snapshot_to_server.setImageResource(R.drawable.video_camera_icon);
                            }else{
                                cameraView.takeVideoSnapshot(new File(MainActivity.strVideoCaptured));
                                b_send_video_snapshot_to_server.setImageResource(R.drawable.button_stop_icon);
                            }
                        }
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_switch_camera: {
                    try
                    {
                        if(cameraView.getVisibility() == View.VISIBLE) {
                            cameraView.toggleFacing();
                        }
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.cam_calibrate: {
                    try
                    {
                        MainActivity.target_lon = MainActivity.tab_map.targetPoint.getLongitude();
                        MainActivity.target_lat = MainActivity.tab_map.targetPoint.getLatitude();
                        MainActivity.target_alt = (float)MainActivity.tab_map.targetPoint.getAltitude();

                        float azi,ele;
                        float[] res = MainActivity.CalculateAngles(MainActivity.uav_lon,MainActivity.uav_lat,MainActivity.uav_alt,MainActivity.target_lon,MainActivity.target_lat,MainActivity.target_alt);
                        azi = (float)Math.toDegrees(res[0]);
                        ele = (float)Math.toDegrees(res[1]);
                        MainActivity.dYaw = azi - MainActivity.image_yaw_enc;
                        MainActivity.dPitch = ele - MainActivity.image_pitch_enc;
                        MainActivity.dRoll = 0.0f;

                        // save settings
                        MainActivity.SaveCalibrationData(MainActivity.dYaw,MainActivity.dPitch,MainActivity.dRoll);

                        SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        MainActivity.bIsCalibrated = true;
                        editor.putBoolean("bIsCalibrated", true);
                        editor.apply();
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_cam_reset: {
                    try
                    {
                        MainActivity.dYaw = 0.0f;
                        MainActivity.dPitch = 0.0f;
                        MainActivity.dRoll = 0.0f;

                        // save settings
                        MainActivity.SaveCalibrationData(MainActivity.dYaw,MainActivity.dPitch,MainActivity.dRoll);

                        SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        MainActivity.bIsCalibrated = false;
                        editor.putBoolean("bIsCalibrated", false);
                        editor.apply();
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.sw_broadcast_camera: {
                    try
                    {
                        if(sw_broadcast_camera.isChecked()) {
                            MainActivity.activity.runOnUiThread(() -> {
                                try {
                                    MapViewerView.set_tab(1, true);
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
                case R.id.sw_broadcast_map_status:{
                    break;
                }
                case R.id.iv_localize:
                {
                    try
                    {
                        localize();
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.cam_localize: {
                    try
                    {
                        localize();
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.cb_project_on_map:
                {
                    if(Tab_Map.groundOverlay != null) {
                        Tab_Map.groundOverlay.setEnabled(cb_project_on_map.isChecked());
                    }
                    break;
                }
                case R.id.cb_auto_localize:
                {
                    break;
                }
                case R.id.sw_cam_show:
                {
                    try
                    {
                        MainActivity.bDJIExists = !sw_cam_show.isChecked();

                        if(sw_cam_show.isChecked()) {
                            if ((cameraView != null) && (!cameraView.isOpened())) {
                                cameraView.open();

                                if(!calculateDeviceCameraFOV()){
                                    MainActivity.MyLogInfo("Error in calculateFOV");
                                }
                            }
                            update_camera();
                        }else{
                            if ((cameraView != null) && (cameraView.isOpened())) {
                                cameraView.close();
                            }
                        }

                        MapViewerView.refresh();
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.cb_status: {
                    try
                    {
                        if(crosshairView != null) {
                            crosshairView.camera_status = cb_status.isChecked();

                            SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("camera_status", crosshairView.camera_status);
                            editor.apply();

                            crosshairView.invalidate();
                        }
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.cb_grayscale: {
                    try
                    {
                        SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("grayscale", cb_grayscale.isChecked());
                        editor.apply();
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_gimbal_reset: {
                    try
                    {
//                    crosshairView.lastYaw = MainActivity.uav_yaw_enc;
//                    crosshairView.lastPitch = MainActivity.uav_pitch_enc;
//                    rotateGimbal(crosshairView.lastYaw, crosshairView.lastPitch, 0.0f);
                        resetGimbal();
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_camera_settings: {
                    try
                    {
                        Tab_Camera.calculateUAVCameraFOV();
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public boolean Localize(boolean bChangeTab) {
        try
        {
            MainActivity.target_x = 0;
            MainActivity.target_y = 0;

            MainActivity.max_dist = 100000;
            MainActivity.step = 10.0;
            MainActivity.SetFlatModelJNI(false, 700.0f);
            MainActivity.laser_dist = -1.0f;

            double[] res = MainActivity.LocalizeJNI(MainActivity.target_x,MainActivity.target_y,MainActivity.fov_h,MainActivity.fov_v,MainActivity.w,MainActivity.h,
                    MainActivity.uav_lon,MainActivity.uav_lat,MainActivity.uav_alt,MainActivity.image_yaw,MainActivity.image_pitch,MainActivity.image_roll,0.0f,0.0f,MainActivity.max_dist,MainActivity.step,
                    MainActivity.laser_dist);
            if(res != null) {
                MainActivity.target_lon = res[0];
                MainActivity.target_lat = res[1];
                MainActivity.target_alt = (float) res[2];
                MainActivity.laser_dist = (float) res[3];
                if(MainActivity.laser_dist <= 0)    return false;
                if(MainActivity.laser_dist >= MainActivity.max_dist)    return false;

                //MainActivity.find(MainActivity.target_lon, MainActivity.target_lat);
            }else{
                return false;
            }

            String strText = "";
            strText += "----------------------------------------\n";
            strText += MainActivity.GetOutputJNI();
            strText += "----------------------------------------\n";
            strText += "lon : " + String.format(Locale.ENGLISH,"%.06f", MainActivity.target_lon) + "\n";
            strText += "lat : " + String.format(Locale.ENGLISH,"%.06f", MainActivity.target_lat) + "\n";
            strText += "alt : " + String.format(Locale.ENGLISH,"%.01f", MainActivity.target_alt) + "\n";
            strText += "dist: " + String.format(Locale.ENGLISH,"%.01f", MainActivity.laser_dist) + "\n";
            strText += "----------------------------------------\n";
//        strText += MainActivity.GetSysInfoJNI()+"\n";
//        strText += "----------------------------------------\n";
            if(MainActivity.IsDemoVersionJNI())
                strText += "Localization not allowed in Demo Version!\n";
            else
                strText += "Registered Version.\n";
            strText += "----------------------------------------\n";
            strText += MainActivity.strMapsPath+"\n";
            strText += MainActivity.strCachePath+"\n";
            strText += "----------------------------------------\n";
//            strText += "Place Name: "+MainActivity.strName+"\n";
//            strText += "----------------------------------------\n";
            strText += "dYaw: " + String.format(Locale.ENGLISH,"%.02f", MainActivity.dYaw) + "\n";
            strText += "dPitch: " + String.format(Locale.ENGLISH,"%.02f", MainActivity.dPitch) + "\n";
            strText += "dRoll: " + String.format(Locale.ENGLISH,"%.02f", MainActivity.dRoll) + "\n";
            strText += "bIsCalibrated: " + MainActivity.bIsCalibrated + "\n";
            strText += "----------------------------------------\n";

            try {
                String finalStrText = strText;
                MainActivity.activity.runOnUiThread(() -> {
                    try {
                        Tab_Main.tv_Text.setText(finalStrText);
                        if(crosshairView != null)   crosshairView.invalidate();
                        if(MainActivity.tab_map != null){
                            MainActivity.tab_map.set_target_pos(MainActivity.target_lon,MainActivity.target_lat,MainActivity.target_alt,true);
                            MainActivity.tab_map.mapController.setCenter(MainActivity.tab_map.targetPoint);
                            MainActivity.tab_map.mapController.setZoom(17.0);
                            Tab_Map.map.postInvalidate();
                        }
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                });
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }

            // show target position on map
            if(bChangeTab){
                if(MainActivity.laser_dist > 0)
                {
                    MainActivity.activity.runOnUiThread(() -> {
                        try {
                            MapViewerView.set_tab(0, false);
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    });
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return true;
    }

    public boolean Localize4() {
        try
        {
            int w2 = MainActivity.w/2;
            int h2 = MainActivity.h/2;
            MainActivity.target_x = 0;
            MainActivity.target_y = 0;
            MainActivity.max_dist = 100000;
            MainActivity.step = 10.0;
            MainActivity.SetFlatModelJNI(false, 700.0f);

            double target_lon, target_lat,max_dist;
            float target_alt,laser_dist;
            max_dist = MainActivity.max_dist;

            // top_left
            MainActivity.laser_dist = -1.0f;
            double[] res1 = MainActivity.LocalizeJNI(MainActivity.target_x-w2,MainActivity.target_y-h2,MainActivity.fov_h,MainActivity.fov_v,MainActivity.w,MainActivity.h,
                    MainActivity.uav_lon,MainActivity.uav_lat,MainActivity.uav_alt,MainActivity.image_yaw,MainActivity.image_pitch,MainActivity.image_roll,0.0f,0.0f,MainActivity.max_dist,MainActivity.step,
                    MainActivity.laser_dist);
            if(res1 != null) {
                target_lon = res1[0];
                target_lat = res1[1];
                target_alt = (float) res1[2];
                laser_dist = (float) res1[3];
                if(laser_dist <= 0)    return false;
                if(laser_dist >= max_dist)    return false;
                Tab_Map.top_left.setLongitude(target_lon);
                Tab_Map.top_left.setLatitude(target_lat);
            }else{
                return false;
            }

            // top_right
            MainActivity.laser_dist = -1.0f;
            double[] res2 = MainActivity.LocalizeJNI(MainActivity.target_x+w2,MainActivity.target_y-h2,MainActivity.fov_h,MainActivity.fov_v,MainActivity.w,MainActivity.h,
                    MainActivity.uav_lon,MainActivity.uav_lat,MainActivity.uav_alt,MainActivity.image_yaw,MainActivity.image_pitch,MainActivity.image_roll,0.0f,0.0f,MainActivity.max_dist,MainActivity.step,
                    MainActivity.laser_dist);
            if(res2 != null) {
                target_lon = res2[0];
                target_lat = res2[1];
                target_alt = (float) res2[2];
                laser_dist = (float) res2[3];
                if(laser_dist <= 0)    return false;
                if(laser_dist >= max_dist)    return false;
                Tab_Map.top_right.setLongitude(target_lon);
                Tab_Map.top_right.setLatitude(target_lat);
            }else{
                return false;
            }

            // bottom_right
            MainActivity.laser_dist = -1.0f;
            double[] res3 = MainActivity.LocalizeJNI(MainActivity.target_x+w2,MainActivity.target_y+h2,MainActivity.fov_h,MainActivity.fov_v,MainActivity.w,MainActivity.h,
                    MainActivity.uav_lon,MainActivity.uav_lat,MainActivity.uav_alt,MainActivity.image_yaw,MainActivity.image_pitch,MainActivity.image_roll,0.0f,0.0f,MainActivity.max_dist,MainActivity.step,
                    MainActivity.laser_dist);
            if(res3 != null) {
                target_lon = res3[0];
                target_lat = res3[1];
                target_alt = (float) res3[2];
                laser_dist = (float) res3[3];
                if(laser_dist <= 0)    return false;
                if(laser_dist >= max_dist)    return false;
                Tab_Map.bottom_right.setLongitude(target_lon);
                Tab_Map.bottom_right.setLatitude(target_lat);
            }else{
                return false;
            }

            // bottom_left
            MainActivity.laser_dist = -1.0f;
            double[] res4 = MainActivity.LocalizeJNI(MainActivity.target_x-w2,MainActivity.target_y+h2,MainActivity.fov_h,MainActivity.fov_v,MainActivity.w,MainActivity.h,
                    MainActivity.uav_lon,MainActivity.uav_lat,MainActivity.uav_alt,MainActivity.image_yaw,MainActivity.image_pitch,MainActivity.image_roll,0.0f,0.0f,MainActivity.max_dist,MainActivity.step,
                    MainActivity.laser_dist);
            if(res4 != null) {
                target_lon = res4[0];
                target_lat = res4[1];
                target_alt = (float) res4[2];
                laser_dist = (float) res4[3];
                if(laser_dist <= 0)    return false;
                if(laser_dist >= max_dist)    return false;
                Tab_Map.bottom_left.setLongitude(target_lon);
                Tab_Map.bottom_left.setLatitude(target_lat);
            }else{
                return false;
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return true;
    }

    public void notifyStatusChange() {
        try
        {
            final BaseProduct product = MApplication.getProductInstance();
            if(product != null)
            {
//                product.getCamera().getLens(0).getOpticalZoomFocalLength(new CommonCallbacks.CompletionCallbackWith<Integer>() {
//                    @Override
//                    public void onSuccess(Integer integer) {
//
//                    }
//
//                    @Override
//                    public void onFailure(DJIError djiError) {
//
//                    }
//                });

                getHomeLocation();

                if (ModuleVerificationUtil.isGimbalModuleAvailable()) {
                    if(product.getGimbal() != null)
                    {
                        product.getGimbal().setStateCallback(gimbalState -> {
                            try
                            {
//                                        float yaw = gimbalState.getAttitudeInDegrees().getYaw();
//                                        float pitch = gimbalState.getAttitudeInDegrees().getPitch();
//                                        float roll = gimbalState.getAttitudeInDegrees().getRoll();
//                                        if(yaw == NO_ROTATION)
//                                            MainActivity.gimb_yaw = 0;
//                                        else
//                                            MainActivity.gimb_yaw = yaw;
//
//                                        if(pitch == NO_ROTATION)
//                                            MainActivity.gimb_pitch = 0;
//                                        else
//                                            MainActivity.gimb_pitch = gimbalState.getAttitudeInDegrees().getPitch();
//
//                                        if(roll == NO_ROTATION)
//                                            MainActivity.gimb_roll = 0;
//                                        else
//                                            MainActivity.gimb_roll = gimbalState.getAttitudeInDegrees().getRoll();

                                // This is just for the phantom 3/4 pro - which only has the pitch. The phantom 3/4 pro does not have the roll or yaw values.

                                MainActivity.gimb_yaw = gimbalState.getAttitudeInDegrees().getYaw();
                                MainActivity.gimb_pitch = gimbalState.getAttitudeInDegrees().getPitch();
                                MainActivity.gimb_roll = gimbalState.getAttitudeInDegrees().getRoll();

//                                    MainActivity.image_yaw_enc = (float)MainActivity.db_deg(MainActivity.uav_yaw + MainActivity.gimb_yaw);
//                                    MainActivity.image_pitch_enc = MainActivity.uav_pitch + MainActivity.gimb_pitch;
//                                    MainActivity.image_roll_enc = MainActivity.uav_roll + MainActivity.gimb_roll;

                                // bug fixed at 19/5/2024
                                MainActivity.image_yaw_enc = MainActivity.gimb_yaw;
                                MainActivity.image_pitch_enc = MainActivity.gimb_pitch;
                                MainActivity.image_roll_enc = MainActivity.gimb_roll;

                                MainActivity.image_yaw = (float)MainActivity.db_deg(MainActivity.image_yaw_enc + MainActivity.dYaw);
                                MainActivity.image_pitch = MainActivity.image_pitch_enc + MainActivity.dPitch;
                                MainActivity.image_roll = MainActivity.image_roll_enc + MainActivity.dRoll;

                                update_status(false);
                            }
                            catch (Throwable ex)
                            {
                                MainActivity.MyLog(ex);
                            }
                        });
                    }
                }

                Camera camera = product.getCamera();
                if(camera != null)
                {
                    camera.setSystemStateCallback(systemState -> {
                        assert systemState != null;
                        if(systemState.isShootingSinglePhoto() || systemState.isShootingSinglePhotoInRAWFormat() || systemState.isShootingBurstPhoto() || systemState.isShootingIntervalPhoto() || systemState.isShootingPanoramaPhoto() || systemState.isShootingRAWBurstPhoto() || systemState.isShootingShallowFocusPhoto())
                        {
                            MainActivity.nCapturedFrames++;
//                                setResultToToast("nCapturedFrames: "+Integer.toString(MainActivity.nCapturedFrames));
                        }
                    });

                    camera.getPhotoAspectRatio(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.PhotoAspectRatio>() {
                        @Override
                        public void onSuccess(SettingsDefinitions.PhotoAspectRatio photoAspectRatio) {
                            switch(photoAspectRatio)
                            {
                                case RATIO_3_2:
                                {
                                    MainActivity.ratio_w_h = 3.0/2.0;
                                    break;
                                }
                                case RATIO_4_3:
                                {
                                    MainActivity.ratio_w_h = 4.0/3.0;
                                    break;
                                }
                                case RATIO_16_9:
                                {
                                    MainActivity.ratio_w_h = 16.0/9.0;
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onFailure(DJIError djiError) {
                        }
                    });
                }
            }

            Aircraft aircraft = MApplication.getAircraftInstance();
            if(aircraft != null)
            {
                FlightController flightController = aircraft.getFlightController();
                if(flightController != null)
                {
                    // Failsafe RTH: If the wireless link is lost between the remote controller and aircraft
//                    flightController.setConnectionFailSafeBehavior(GO_HOME, new CommonCallbacks.CompletionCallback() {
//                        @Override
//                        public void onResult(DJIError djiError) {
//
//                        }
//                    });

                    flightController.setStateCallback(state -> {
                        try
                        {
                            try
                            {
                                if (MainActivity.checkGpsCoordinates(state.getHomeLocation().getLatitude(), state.getHomeLocation().getLongitude())) {
                                    MainActivity.home_lon = state.getHomeLocation().getLongitude();
                                    MainActivity.home_lat = state.getHomeLocation().getLatitude();
                                    MainActivity.home_alt = MainActivity.GetHeightJNI(MainActivity.home_lon,MainActivity.home_lat);
                                }

                                MainActivity.uav_yaw = (float)state.getAttitude().yaw;
                                MainActivity.uav_pitch = (float)state.getAttitude().pitch;
                                MainActivity.uav_roll = (float)state.getAttitude().roll;
// bug fixed at 19/5/2024
//                                    MainActivity.image_yaw_enc = (float)MainActivity.db_deg(MainActivity.uav_yaw + MainActivity.gimb_yaw);
//                                    MainActivity.image_pitch_enc = MainActivity.uav_pitch + MainActivity.gimb_pitch;
//                                    MainActivity.image_roll_enc = MainActivity.uav_roll + MainActivity.gimb_roll;
//
//                                    MainActivity.image_yaw = (float)MainActivity.db_deg(MainActivity.image_yaw_enc + MainActivity.dYaw);
//                                    MainActivity.image_pitch = MainActivity.image_pitch_enc + MainActivity.dPitch;
//                                    MainActivity.image_roll = MainActivity.image_roll_enc + MainActivity.dRoll;

                                update_status(false);
                            }
                            catch (Throwable ex)
                            {
                                MainActivity.MyLog(ex);
                            }

                            try
                            {
                                if(MainActivity.checkGpsCoordinates(state.getAircraftLocation().getLatitude(), state.getAircraftLocation().getLongitude()))
                                {
                                    MainActivity.uav_lon = state.getAircraftLocation().getLongitude();
                                    MainActivity.uav_lat = state.getAircraftLocation().getLatitude();
                                    MainActivity.uav_alt_above_ground = state.getAircraftLocation().getAltitude();
                                    MainActivity.uav_ground_alt = MainActivity.GetHeightJNI(MainActivity.uav_lon,MainActivity.uav_lat);
                                    MainActivity.uav_alt = MainActivity.uav_ground_alt + MainActivity.uav_alt_above_ground;
//                                        MainActivity.uav_alt = MainActivity.home_alt + MainActivity.uav_alt_above_ground;

                                    update_status(false);
                                }
                            }
                            catch (Throwable ex)
                            {
                                MainActivity.MyLog(ex);
                            }
                        }
                        catch (Throwable ex)
                        {
                            MainActivity.MyLog(ex);
                        }
                    });
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    //region Internal Helper Methods
    private FlightController getFlightController(){
        try
        {
            if (flightController == null) {
                BaseProduct product = MApplication.getProductInstance();
                if (product instanceof Aircraft) {
                    flightController = ((Aircraft) product).getFlightController();
                } else {
                    Tab_Messenger.showToast("Product is disconnected!");
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return flightController;
    }

    public void setHomeLocation()
    {
        try
        {
            Aircraft aircraft = MApplication.getAircraftInstance();
            if(aircraft != null) {
                FlightController flightController = aircraft.getFlightController();
                if (flightController != null) {
                    flightController.setHomeLocation(new LocationCoordinate2D(MainActivity.home_lon, MainActivity.home_lat), djiError -> {
                        try {
                            if(djiError != null)    Tab_Messenger.showToast("setHomeLocation error: " + djiError.getDescription());
                        }
                        catch (Throwable ex)
                        {
                            MainActivity.MyLog(ex);
                        }
                    });
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void getLimits()
    {
        try
        {
            KeyManager keymgr = KeyManager.getInstance();
            if(keymgr != null) {
                Object latitudeValue = keymgr.getValue((FlightControllerKey.create(HOME_LOCATION_LATITUDE)));
                Object longitudeValue = keymgr.getValue((FlightControllerKey.create(HOME_LOCATION_LONGITUDE)));
                Object altitudeValue = keymgr.getValue((FlightControllerKey.create(HOME_POINT_ALTITUDE)));
//                if (latitudeValue instanceof Double) {
//                    MainActivity.home_lat = (double) latitudeValue;
//                }
//                if (longitudeValue instanceof Double) {
//                    MainActivity.home_lon = (double) longitudeValue;
//                }
                if((latitudeValue instanceof Double) && (longitudeValue instanceof Double)) {
                    if (MainActivity.checkGpsCoordinates((double) latitudeValue, (double) longitudeValue)) {
                        MainActivity.home_lat = (double) latitudeValue;
                        MainActivity.home_lon = (double) longitudeValue;
                        MainActivity.home_alt = MainActivity.GetHeightJNI(MainActivity.home_lon,MainActivity.home_lat);
                    }
                }
                if (altitudeValue instanceof Integer) {
                    MainActivity.home_altitude = (int) altitudeValue;
                    Tab_Messenger.showToast("Home Altitude is set to " + altitudeValue.toString() + "m!");
                }

                Object max_flight_height_value = keymgr.getValue((FlightControllerKey.create(MAX_FLIGHT_HEIGHT)));
                if (max_flight_height_value != null) {
                    Tab_Messenger.showToast("Max Flight Height is set to " + max_flight_height_value.toString() + "m!");
                }

                Object max_flight_radius_value = keymgr.getValue((FlightControllerKey.create(MAX_FLIGHT_RADIUS)));
                if (max_flight_radius_value != null) {
                    Tab_Messenger.showToast("Max Flight Radius is set to " + max_flight_radius_value.toString() + "m!");
                }

                Object max_flight_radius_enabled_value = keymgr.getValue((FlightControllerKey.create(MAX_FLIGHT_RADIUS_ENABLED)));
                if (max_flight_radius_enabled_value != null) {
                    Tab_Messenger.showToast("Max Flight Radius Enabled is set to " + max_flight_radius_enabled_value.toString());
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void getHomeLocation()
    {
        try
        {
            KeyManager keymgr = KeyManager.getInstance();
            if(keymgr != null) {
                Object latitudeValue = keymgr.getValue((FlightControllerKey.create(HOME_LOCATION_LATITUDE)));
                Object longitudeValue = keymgr.getValue((FlightControllerKey.create(HOME_LOCATION_LONGITUDE)));
//                if (latitudeValue instanceof Double) {
//                    MainActivity.home_lat = (double) latitudeValue;
//                }
//                if (longitudeValue instanceof Double) {
//                    MainActivity.home_lon = (double) longitudeValue;
//                }
                if((latitudeValue instanceof Double) && (longitudeValue instanceof Double)) {
                    if (MainActivity.checkGpsCoordinates((double) latitudeValue, (double) longitudeValue)) {
                        MainActivity.home_lat = (double) latitudeValue;
                        MainActivity.home_lon = (double) longitudeValue;
                        MainActivity.home_alt = MainActivity.GetHeightJNI(MainActivity.home_lon,MainActivity.home_lat);
                    }
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
            MainActivity.home_lat = 0.0;
            MainActivity.home_lon = 0.0;
            MainActivity.home_alt = 0.0;
        }
    }

    public void setLimits()
    {
        try
        {
            if (getFlightController() != null) {
                getHomeLocation();

                flightController.setGoHomeHeightInMeters(MainActivity.home_altitude, djiError -> {
                    try
                    {
                        Tab_Messenger.showToast(djiError == null ? "Home altitude is set to "+Integer.toString(MainActivity.home_altitude) : djiError.getDescription());
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                });

                flightController.setMaxFlightHeight(MainActivity.max_flight_height, djiError -> {
                    try
                    {
                        Tab_Messenger.showToast(djiError == null ? "Max Flight Height is set to "+Integer.toString(MainActivity.max_flight_height)+"m!" : djiError.getDescription());
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                });

                flightController.setMaxFlightRadius(MainActivity.max_flight_radius, djiError -> {
                    try
                    {
                        Tab_Messenger.showToast(djiError == null ? "Max Flight Radius is set to "+Integer.toString(MainActivity.max_flight_radius)+"m!" : djiError.getDescription());
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                });

                flightController.setMaxFlightRadiusLimitationEnabled(MainActivity.max_flight_radius_enabled, djiError -> {
                    try
                    {
                        Tab_Messenger.showToast(djiError == null ? "Max Flight Radius Enabled is set to "+Boolean.toString(MainActivity.max_flight_radius_enabled) : djiError.getDescription());
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                });
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    /**
     * Init a fake texture view to for the codec manager, so that the video raw data can be received
     * by the camera
     */
//    public void initPreviewerTextureView() {
//        try
//        {
//            if(fpv_camera != null) fpv_camera.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
//
//                @Override
//                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//                    try
//                    {
//                        Log.v(TAG, "onSurfaceTextureAvailable");
//                        final BaseProduct product = MApplication.getProductInstance();
//                        if (product != null && product.isConnected() && product.getModel() != null) {
//                            dji.sdk.camera.Camera camera = product.getCamera();
//                            if (mCodecManager == null && surface != null && camera != null) {
//                                //Normal init for the surface
//                                mCodecManager = new DJICodecManager(MainActivity.ctx, surface, width, height);
//                                Log.v(TAG, "Initialized CodecManager");
//                            }
//                        }
//                    }
//                    catch (Throwable ex)
//                    {
//                        MainActivity.MyLog(ex);
//                    }
//                }
//
//                @Override
//                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//                    try
//                    {
//                        Log.v(TAG, "onSurfaceTextureSizeChanged");
//                        if (mCodecManager != null) {//AliSoft 2018.04.09
//                            mCodecManager.cleanSurface();
//                            mCodecManager.destroyCodec();
//                            mCodecManager = null;
//                            final BaseProduct product = MApplication.getProductInstance();
//                            if (product != null && product.isConnected() && product.getModel() != null) {
//                                dji.sdk.camera.Camera camera = product.getCamera();
//                                if (mCodecManager == null && surface != null && camera != null) {
//                                    //Normal init for the surface
//                                    mCodecManager = new DJICodecManager(MainActivity.ctx, surface, width, height);
//                                    Log.v(TAG, "Initialized CodecManager");
//                                }
//                            }
//                        }
//                    }
//                    catch (Throwable ex)
//                    {
//                        MainActivity.MyLog(ex);
//                    }
//                }
//
//                @Override
//                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//                    try
//                    {
//                        Log.v(TAG, "onSurfaceTextureDestroyed");
//                        if (mCodecManager != null) {
//                            mCodecManager.cleanSurface();
//                            mCodecManager.destroyCodec();
//                            mCodecManager = null;
//                        }
//                    }
//                    catch (Throwable ex)
//                    {
//                        MainActivity.MyLog(ex);
//                    }
//                    return false;
//                }
//
//                public File SaveBitmap(Bitmap bmp,String filename) throws IOException {
//                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                    bmp.compress(Bitmap.CompressFormat.JPEG, 95, bytes);
//                    File f = new File(filename);
//                    f.createNewFile();
//                    FileOutputStream fo = new FileOutputStream(f);
//                    fo.write(bytes.toByteArray());
//                    fo.close();
//                    return f;
//                }
//
//                @Override
//                public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//                    try {
//                        if(crosshairView != null) {
//                            MainActivity.w = crosshairView.getWidth();
//                            MainActivity.h = crosshairView.getHeight();
//
//                            //                fov_h = 72.0f;// Zenmuse X5 Camera
////                    MainActivity.fov_v = (float)Math.toDegrees(2.0 * atan(tan(Math.toRadians(MainActivity.fov_h)/2.0) * (double)MainActivity.h / (double)MainActivity.w));
//
////                        MainActivity.tab_map.set_camera_fov(MainActivity.fov_h);
//                            crosshairView.invalidate();
//
////                        final String filename;
////                        filename = Environment.getExternalStorageDirectory() + File.separator + "Image" + Integer.toString(idx) + ".jpg";
//                            //                    SaveBitmap(image,filename);
//                            idx++;
//                        }
//                    }
//                    catch (Throwable ex)
//                    {
//                        MainActivity.MyLog(ex);
//                    }
//                }
//            });
//        }
//        catch (Throwable ex)
//        {
//            MainActivity.MyLog(ex);
//        }
//    }

    static private float s_yaw = 0.0f;
    static private float s_pitch = 0.0f;
    static private float s_roll = 0.0f;
    public void rotateGimbal(float yaw,float pitch,float roll) {
        try
        {
            boolean bOk = (Math.abs(yaw - s_yaw) > 0.01) || (Math.abs(pitch - s_pitch) > 0.01) || (Math.abs(roll - s_roll) > 0.01);
            if(!bOk)    return;
            if (ModuleVerificationUtil.isGimbalModuleAvailable()) {
                Gimbal gimbal = MApplication.getProductInstance().getGimbal();
                if (gimbal == null) {
                    return;
                }
                float yaw0 = yaw;
                if(yaw0 >= 180)  yaw0 -= 360;// TODO test yaw0 values
                Rotation.Builder builder = new Rotation.Builder();
                builder.mode(RotationMode.ABSOLUTE_ANGLE);
                builder.time(0);

                //                gimbal.rotate(builder.yaw(yaw).pitch(pitch).roll(roll).build(), new CommonCallbacks.CompletionCallback() {
//                gimbal.rotate(builder.roll(roll).pitch(pitch).yaw(yaw).build(), new CommonCallbacks.CompletionCallback() {
                gimbal.rotate(builder.pitch(pitch).yaw(yaw0).roll(roll).build(), djiError -> {
                    try {
                        if(djiError != null)    Tab_Messenger.showToast("rotateGimbal error: " + djiError.getDescription());
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                });

                s_yaw = yaw;
                s_pitch = pitch;
                s_roll = roll;
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void rotate_gimbal_pitch(float pitch) {
        try
        {
            if (ModuleVerificationUtil.isGimbalModuleAvailable()) {
                Gimbal gimbal = MApplication.getProductInstance().getGimbal();
                if (gimbal == null) {
                    return;
                }
                Rotation.Builder builder = new Rotation.Builder();
                builder.mode(RotationMode.ABSOLUTE_ANGLE);
                builder.time(1);

                gimbal.rotate(builder.pitch(pitch).yaw(Rotation.NO_ROTATION).roll(Rotation.NO_ROTATION).build(), djiError -> {
                    try {
                        if(djiError != null)    Tab_Messenger.showToast("rotateGimbal error: " + djiError.getDescription());
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                });
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void resetGimbal() {
        try
        {
            if (ModuleVerificationUtil.isGimbalModuleAvailable()) {
                Gimbal gimbal = MApplication.getProductInstance().getGimbal();
                if (gimbal == null) {
                    return;
                }
//                gimbal.reset(null);
                gimbal.resetYaw(null);
                rotate_gimbal_pitch(0);
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    /*
     * Check if The Gimbal Capability is supported
     */
    public boolean isGimbalFeatureSupported(CapabilityKey key) {
        if (!ModuleVerificationUtil.isGimbalModuleAvailable())  return false;
        Gimbal gimbal = MApplication.getProductInstance().getGimbal();
        if (gimbal == null) {
            return false;
        }

        DJIParamCapability capability = null;
        if (gimbal.getCapabilities() != null) {
            capability = gimbal.getCapabilities().get(key);
        }

        if (capability != null) {
            return capability.isSupported();
        }
        return false;
    }

    private boolean calculateDeviceCameraFOV() {
        try {
            CameraManager manager = (CameraManager) MainActivity.ctx.getSystemService(Context.CAMERA_SERVICE);
            if(manager == null) return false;
            for (final String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (cOrientation == CameraCharacteristics.LENS_FACING_BACK) {
                    SizeF size_mm = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
                    if(size_mm != null){
                        float w_mm = size_mm.getWidth();
                        float h_mm = size_mm.getHeight();
                        float[] maxFocus = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
                        if(maxFocus != null){
                            MainActivity.fov_h = (float) Math.toDegrees(2*Math.atan(w_mm/(maxFocus[0]*2)));
                            MainActivity.fov_v = (float) Math.toDegrees(2*Math.atan(h_mm/(maxFocus[0]*2)));
                            MainActivity.MyLogInfo("Device Camera fov_h: "+MainActivity.fov_h);
                            MainActivity.MyLogInfo("Device Camera fov_v: "+MainActivity.fov_v);
                        }else{
                            return false;
                        }
                    }else{
                        return false;
                    }
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return true;
    }

    static public boolean calculateUAVCameraFOV() {
        try {
            FragmentManager fragmentManager = MainActivity.activity.getSupportFragmentManager();
            MissinDialogFragment newFragment = new MissinDialogFragment();

            // The device is using a large layout, so show the fragment as a dialog
            newFragment.show(fragmentManager, "dialog");
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return true;
    }

    public static class MissinDialogFragment extends DialogFragment {
        /** The system calls this only when creating the layout in a dialog. */
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
            // Get the layout inflater
            LayoutInflater inflater = requireActivity().getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            View camera_dialog = inflater.inflate(R.layout.camera_settings, null);

            Spinner spinner = camera_dialog.findViewById(R.id.fov_spinner);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    CitiesAdapter.select_item(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.ctx,
                    R.array.fov_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
            spinner.setSelection(Tab_Camera.cameraSettingsIdx);

            Button b_ok = camera_dialog.findViewById(R.id.b_ok);
            b_ok.setOnClickListener(v -> {
                try
                {
                    // Hide both the navigation bar and the status bar.
                    MainActivity.hide_keyboard(camera_dialog);

                    Objects.requireNonNull(MissinDialogFragment.this.getDialog()).dismiss();
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            });

            Button b_cancel = camera_dialog.findViewById(R.id.b_cancel);
            b_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Hide both the navigation bar and the status bar.
                    MainActivity.hide_keyboard(camera_dialog);

                    Objects.requireNonNull(MissinDialogFragment.this.getDialog()).dismiss();
                }
            });

            builder.setView(camera_dialog);
            return builder.create();
        }
    }

}
