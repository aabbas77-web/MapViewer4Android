package com.oghab.mapviewer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
//import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import com.anggrayudi.storage.file.PublicDirectory;
import com.balda.flipper.DocumentFileCompat;
import com.balda.flipper.Root;
import com.balda.flipper.StorageManagerCompat;
//import com.developer.filepicker.controller.DialogSelectionListener;
//import com.developer.filepicker.model.DialogConfigs;
//import com.developer.filepicker.model.DialogProperties;
//import com.developer.filepicker.view.FilePickerDialog;
import com.blankj.utilcode.util.UriUtils;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.imageview.ShapeableImageView;
import com.maltaisn.icondialog.IconDialog;
import com.maltaisn.icondialog.data.Icon;
import com.maltaisn.icondialog.pack.IconPack;
import com.maltaisn.icondialog.pack.IconPackLoader;
import com.maltaisn.iconpack.defaultpack.IconPackDefault;
import com.oghab.mapviewer.launcher.Actions;
import com.oghab.mapviewer.launcher.MapViewerService;
import com.oghab.mapviewer.launcher.ServiceTracker;
import com.oghab.mapviewer.launcher.log;
import com.oghab.mapviewer.mapviewer.City;
import com.oghab.mapviewer.mapviewer.CustomAdapter;
import com.oghab.mapviewer.mapviewer.MapViewerView;
import com.oghab.mapviewer.mapviewer.StringsAdapter;
import com.oghab.mapviewer.mapviewer.SerializableLocation;
import com.oghab.mapviewer.mapviewer.Tab_Camera;
import com.oghab.mapviewer.mapviewer.Tab_Main;
import com.oghab.mapviewer.mapviewer.Tab_Map;
import com.oghab.mapviewer.mapviewer.Tab_Messenger;
import com.oghab.mapviewer.mapviewer.mv_Kalman;
import com.oghab.mapviewer.mapviewer.mv_LocationOverlay;
import com.oghab.mapviewer.mapviewer.tcp_client;
import com.oghab.mapviewer.mapviewer.tcp_io_handler;
import com.oghab.mapviewer.mapviewer.tcp_server;
import com.oghab.mapviewer.mapviewer.tcp_user;
import com.oghab.mapviewer.utils.FileHelper;
import com.oghab.mapviewer.utils.IntentHelper;
import com.oghab.mapviewer.utils.mv_utils;
import com.otaliastudios.cameraview.CameraLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

//import dji.common.error.DJIError;
//import dji.common.error.DJISDKError;
//import dji.common.realname.AppActivationState;
//import dji.common.useraccount.UserAccountState;
//import dji.common.util.CommonCallbacks;
//import dji.sdk.base.BaseComponent;
//import dji.sdk.base.BaseProduct;
//import dji.sdk.realname.AppActivationManager;
//import dji.sdk.sdkmanager.DJISDKInitEvent;
//import dji.sdk.sdkmanager.DJISDKManager;
//import dji.sdk.useraccount.UserAccountManager;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.common.realname.AppActivationState;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.realname.AppActivationManager;
import dji.sdk.sdkmanager.DJISDKInitEvent;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.useraccount.UserAccountManager;
import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.color.SimpleColorDialog;
import eltos.simpledialogfragment.list.CustomListDialog;
//import ir.androidexception.filepicker.dialog.SingleFilePickerDialog;
//import ir.androidexception.filepicker.interfaces.OnCancelPickerDialogListener;
//import ir.androidexception.filepicker.interfaces.OnConfirmDialogListener;
//import ir.androidexception.filepicker.utility.Util;

import static android.media.ExifInterface.TAG_DATETIME;
import static android.media.ExifInterface.TAG_GPS_LATITUDE;
import static android.media.ExifInterface.TAG_GPS_LATITUDE_REF;
import static android.media.ExifInterface.TAG_GPS_LONGITUDE;
import static android.media.ExifInterface.TAG_GPS_LONGITUDE_REF;
import static android.media.ExifInterface.TAG_GPS_PROCESSING_METHOD;
import static android.media.ExifInterface.TAG_IMAGE_LENGTH;
import static android.media.ExifInterface.TAG_IMAGE_WIDTH;
import static android.media.ExifInterface.TAG_MAKE;
import static android.media.ExifInterface.TAG_MODEL;
import static android.media.ExifInterface.TAG_ORIENTATION;
import static android.media.ExifInterface.TAG_WHITE_BALANCE;
import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

import org.json.JSONException;
import org.json.JSONObject;

import com.oghab.mapviewer.bonuspack.kml.Style;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.modules.OfflineTileProvider;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;

import com.oghab.mapviewer.mapviewer.MyMarker;
import com.oghab.mapviewer.mapviewer.MyPolygon;
import com.oghab.mapviewer.mapviewer.MyPolyline;

/** Main activity that displays three choices to user */
//public class MainActivity extends AppCompatActivity implements View.OnClickListener, IconDialog.Callback {
//@RequiresApi(api = Build.VERSION_CODES.Q)
public class MainActivity extends AppCompatActivity implements View.OnClickListener, IconDialog.Callback, SimpleDialog.OnDialogResultListener {
    private static final String TAG = "MainActivity";
    static public String password = "";
    static public String dev_password = "MapViewer.123456";
    public static final String ICON_DIALOG_TAG = "icon-dialog";
    private static final String MY_MARK_COLOR_DIALOG_TAG = "My Mark Color Dialog";
    private static final String MY_LINE_COLOR_DIALOG_TAG = "My Line Color Dialog";
    private static final String MY_FILL_COLOR_DIALOG_TAG = "My Fill Color Dialog";
    private static boolean isAppStarted = false;
    static public boolean bIsSimulating = false;
    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
//        Manifest.permission.READ_EXTERNAL_STORAGE,
//        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//        Manifest.permission.READ_MEDIA_IMAGES,
//        Manifest.permission.READ_MEDIA_VIDEO,
//        Manifest.permission.READ_MEDIA_AUDIO,
//        Manifest.permission.MANAGE_EXTERNAL_STORAGE,
//        Manifest.permission.ACCESS_MEDIA_LOCATION,

            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.VIBRATE,

//            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
//            Manifest.permission.SYSTEM_ALERT_WINDOW,

            Manifest.permission.INTERNET,
            Manifest.permission.WAKE_LOCK,
//            Manifest.permission.MANAGE_EXTERNAL_STORAGE,

//            Manifest.permission.READ_CALL_LOG,
//            Manifest.permission.PROCESS_OUTGOING_CALLS,
//            Manifest.permission.MODIFY_PHONE_STATE,

//            Manifest.permission.ACTIVITY_RECOGNITION,

            //2024.06.03
//            Manifest.permission.BLUETOOTH,
//            Manifest.permission.BLUETOOTH_ADMIN,
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.SEND_SMS,
//            Manifest.permission.RECEIVE_SMS,
//            Manifest.permission.READ_SMS,
//            Manifest.permission.POST_NOTIFICATIONS,
//            Manifest.permission.RECORD_AUDIO,
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private static final int REQUEST_PERMISSION_CODE = 12345;
    private final List<String> missingPermission = new ArrayList<>();
    static public tcp_server server = new tcp_server();
    static public tcp_client client = new tcp_client();
    static public mv_Kalman kalman = null;

    // Used to load the 'native-lib' library on application startup.
//    static {
//        System.loadLibrary("native-lib");
//    }
    static {
        try {
            System.loadLibrary("native-lib");
//        } catch (Error | Exception ignore){
        } catch (Throwable ex){
            MainActivity.MyLog(ex);
        }
    }
    public static boolean isStarted() {
        return isAppStarted;
    }
    static public MainActivity activity = null;
    static public Context ctx = null;
    static public Tab_Main tab_main = null;
    static public Tab_Messenger tab_messenger = null;
    static public Tab_Camera tab_camera = null;
    static public Tab_Map tab_map = null;

    static public StorageManagerCompat storageManager;

    static public String strMapViewer = "";
    static public String strImagePath = "";
    //    static public String strAttrFile = "";
    static public String strDefaultFile = "";
    static public String strSettingsFile = "";
    static public String strLogFile = "";
    static public String strProjectileFile = "";
    static public Uri uriCMDFile = null;

    static public String strName;
    static public String strDBPath;

    //  The attitude of the aircraft where the pitch, roll, and yaw values will be in the range of [-180, 180] degrees.
//  If its pitch, roll, and yaw values are 0, the aircraft will be hovering level with a True North heading.
    static public float uav_yaw = 0.0f;
    static public float uav_pitch = 0.0f;
    static public float uav_roll = 0.0f;

    static public float gimb_yaw = 0.0f;
    static public float gimb_pitch = 0.0f;
    static public float gimb_roll = 0.0f;

    static public float image_yaw_enc = 0.0f;
    static public float image_pitch_enc = 0.0f;
    static public float image_roll_enc = 0.0f;

    static public float image_yaw = 0.0f;
    static public float image_pitch = 0.0f;
    static public float image_roll = 0.0f;

    static public float dYaw = 0.0f;
    static public float dPitch = 0.0f;
    static public float dRoll = 0.0f;
    static public boolean bIsCalibrated = false;

    static public String productName = "";
    static public int w = 4000, h = 3000;
    static public int target_x = 0, target_y = 0;
    static public float fov_d = 94.0f, fov_h = 72.0f, fov_v = 72.0f;
    static public double uav_lon = 36.27663955210181, uav_lat = 33.51385796848336, uav_alt_above_ground = 100.0, uav_ground_alt = 693.0, uav_alt = uav_ground_alt + uav_alt_above_ground;
    static public double max_dist = 0.0, step = 10.0;
    static public double target_lon = 0.0, target_lat = 0.0;
    static public float target_alt = 0.0f, laser_dist = 0.0f;
    //    static public double home_lon = 36.27663955210181, home_lat = 33.51385796848336;
    static public double home_lon = 0.0, home_lat = 0.0, home_alt = 0.0;
    static public double map_lon = 0.0, map_lat = 0.0, map_zoom = 1.0, map_rot = 0.0;
    static public double start_lon = 0.0, start_lat = 0.0;
    static public int home_altitude = 50;
    static public double ratio_w_h = 4.0 / 3.0;

    static public int nCapturedFrames = 0;

    static public int max_flight_height = 300;
    static public int max_flight_radius = 2000;
    static public boolean max_flight_radius_enabled = false;

    static public float lastYaw = 0.0f;
    static public float lastPitch = 0.0f;

    static public boolean bDJIExists = true;
    static public boolean bCanExit = true;
    static public int def_server_port = 6000;

    private static MediaPlayer mp = null;
    static boolean is_playing = false;

    static public boolean bNavigation = true;
    static public boolean bAutoStartService = true;
    static public boolean is_encoded = true;
    static public double dMarkersZoomLevel = 13.0;

    @Nullable
    private IconPack iconPack;

    @SuppressLint("RestrictedApi")
    private void play_sound(String strFileName) {
        try {
            if (is_playing) return;

            if (FileHelper.FileExists(strFileName)) {
                mp = MediaPlayer.create(this, Uri.fromFile(new File(strFileName)));
                if(mp == null)   return;
                if (mp.isPlaying()) {
                    mp.stop();
                    mp.release();
                    mp = MediaPlayer.create(this, Uri.fromFile(new File(strFileName)));
                }
                mp.setOnCompletionListener(m -> {
                    try {
                        is_playing = false;
                        m.stop();
                        m.release();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                });
                mp.setVolume(1, 1);
                mp.start();
                is_playing = true;
            } else {
                MainActivity.writeLineToLog("File [" + strFileName + "] not found");
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static void stop_sound() {
        try {
//            mp = MediaPlayer.create(this, Uri.fromFile(new File(strFileName)));
            if ((mp != null) && mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
            mp = null;
            is_playing = false;
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static MediaPlayer ring = null;

    public static void playCameraShutterSound() {
        try {
            ring = MediaPlayer.create(MainActivity.ctx, R.raw.camera_shutter_click_01);
            if(ring == null)   return;
            if (ring.isPlaying()) {
                ring.stop();
                ring.release();
                ring = MediaPlayer.create(MainActivity.ctx, R.raw.camera_shutter_click_01);
            }
            ring.setOnCompletionListener(m -> {
                try {
                    is_playing = false;
                    ring.stop();
                    ring.release();
                } catch (Throwable ex) {
                    MainActivity.MyLog(ex);
                }
            });
            ring.setVolume(1, 1);
            ring.start();

        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    public static void playAssetSound(Context context, String soundFileName) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();

            AssetFileDescriptor descriptor = context.getAssets().openFd(soundFileName);
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mediaPlayer.prepare();
            mediaPlayer.setVolume(1f, 1f);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public IconPack getIconDialogIconPack() {
        return getIconPack();
    }

    @Override
    public void onIconDialogCancelled() {

    }

    @Override
    public void onIconDialogIconsSelected(@NonNull IconDialog iconDialog, @NonNull List<Icon> icons) {
        try {
            // Show a toast with the list of selected icon IDs.
            StringBuilder sb = new StringBuilder();
            for (Icon icon : icons) {
                sb.append(icon.getId());
                sb.append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
//            Toast.makeText(MainActivity.ctx, "Icons selected: " + sb, Toast.LENGTH_SHORT).show();

            Drawable drawable = icons.get(0).getDrawable();
            Tab_Messenger.et_message.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if (MY_MARK_COLOR_DIALOG_TAG.equals(dialogTag)){
            if (which == BUTTON_POSITIVE){
                @ColorInt int color = extras.getInt(SimpleColorDialog.COLOR);  // The color chosen
                et_icon_color.setText(mv_utils.colorToString(color));
            }
            return true;  // dialog result was handled
        }
        if (MY_LINE_COLOR_DIALOG_TAG.equals(dialogTag)){
            if (which == BUTTON_POSITIVE){
                @ColorInt int color = extras.getInt(SimpleColorDialog.COLOR);  // The color chosen
                et_line_color.setText(mv_utils.colorToString(color));
            }
            return true;  // dialog result was handled
        }
        if (MY_FILL_COLOR_DIALOG_TAG.equals(dialogTag)){
            if (which == BUTTON_POSITIVE){
                @ColorInt int color = extras.getInt(SimpleColorDialog.COLOR);  // The color chosen
                et_fill_color.setText(mv_utils.colorToString(color));
            }
            return true;  // dialog result was handled
        }
        return false;
    }

    public static class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {
        Activity activity;

        public DefaultExceptionHandler(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void uncaughtException(@NonNull Thread thread, @NonNull final Throwable ex) {
            try {
                MyLog(ex);
                MyLogInfo("uncaughtException: System.exit(0);");
            }
            catch(Exception e){
                Log.i("MapViewer", String.format("Megaunhandled exception : %s, %s, %s", e.toString(), ex.toString(), Arrays.toString(ex.getStackTrace())));
            }

//            Tab_Messenger.showToast(Arrays.toString(ex.getStackTrace()));
//            Toast.makeText(getApplicationContext(), Arrays.toString(ex.getStackTrace()), Toast.LENGTH_LONG).show();

//            System.exit(0);
        }

    }

//    private fun setupCrashHandler() {
//        // 1. Get the system handler.
//        val systemHandler = Thread.getDefaultUncaughtExceptionHandler()
//
//        // 2. Set the default handler as a dummy (so that crashlytics fallbacks to this one, once set)
//        Thread.setDefaultUncaughtExceptionHandler { t, e -> /* do nothing */ }
//
//        // 3. Setup crashlytics so that it becomes the default handler (and fallbacking to our dummy handler)
//        Fabric.with(this, Crashlytics())
//
//        val fabricExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
//
//        // 4. Setup our handler, which tries to restart the app.
//        Thread.setDefaultUncaughtExceptionHandler(AppExceptionHandler(systemHandler, fabricExceptionHandler, this))
//    }

    public static native void processData(ByteBuffer buf, int len);

    public static native boolean is_strait_line(boolean bProfile, double lon1, double lat1, double alt1, double lon2, double lat2, double alt2, ByteBuffer buf, int count);

    public static native boolean is_strait_line2(boolean bProfile, double lon1, double lat1, double alt1, double lon2, double lat2, double alt2, int count);

    public static native float[] GetTs();

    public static native float[] GetHs();

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public static native double[] LocalizeJNI(int target_x,
                                              int target_y, double fov_h,
                                              double fov_v, int w, int h,
                                              double uav_lon, double uav_lat,
                                              double uav_alt, double uav_yaw,
                                              double uav_pitch, double uav_roll,
                                              double gimb_azi, double gimb_ele,
                                              double max_dist, double step,
                                              double laser_dist);

    public static native void SaveSettingsJNI(int target_x,
                                              int target_y, double fov_h,
                                              double fov_v, int w, int h,
                                              double uav_lon, double uav_lat,
                                              double uav_alt, double uav_yaw,
                                              double uav_pitch, double uav_roll,
                                              double gimb_azi, double gimb_ele,
                                              double max_dist, double step,
                                              double home_lon, double home_lat,
                                              double map_lon, double map_lat, double map_zoom, double map_rot,
                                              double target_lon, double target_lat, double target_alt,
                                              double laser_dist);

    public static native void SaveCurrSettingsJNI();

    public static native float[] CalculateAnglesJNI(
            double lon1, double lat1,
            double alt1, double lon2,
            double lat2, double alt2);

    public static float[] CalculateAngles(
            double lon1, double lat1,
            double alt1, double lon2,
            double lat2, double alt2){
        float[] res = null;
        try
        {
            res = CalculateAnglesJNI(lon1, lat1, alt1, lon2, lat2, alt2);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        if(res == null){
            res = new float[2];
            res[0] = 0;
            res[1] = 0;
        }
        return res;
    }

    public static native String GetOutputJNI();

    public static native String DecodeMessageJNI(String message);

    public static native String GetAppPathJNI();

    public static native String GetMapsPathJNI();

    public static native void SetMapsUriJNI(String uri);
    public static native String GetMapsUriJNI();

    public static native String GetEmulatorPathJNI();

    public static native String GetDevelopmentPathJNI();

    public static native String GetDarkModePathJNI();

    public static native String GetLogPathJNI();

    public static native String GetProjectilePathJNI();

    public static native String GetCachePathJNI();

    public static native String GetKMLPathJNI();

    public static native String GetFavoritesPathJNI();

    public static native String GetDBPathJNI();

    public static native String GetMissionPathJNI();

    public static native String GetMissionsPathJNI();

    public static native void SetFlatModelJNI(boolean bFlat, float fAlt);

    public static native void UpdateEncryptionKeyJNI(String key);
    public static native String GetEncryptionKeyJNI();
    public static native void SaveEncryptionKeyJNI();
    public static native void LoadEncryptionKeyJNI();

    public static native void RegisterJNI(long sn);

    public static native long GetSystemSNJNI();

    public static native void LoadSettingsJNI(String dir_internal, String dir_data);

    public static native void LoadDefaultJNI();
    public static native boolean SetDevelopmentJNI(boolean value);
    public static native boolean SetEmulatorJNI(boolean value);

    public static native void SaveCalibrationData(float yaw, float pitch, float roll);

    public static native float[] GetCalibrationDataJNI();
    public static native float[] GetDeviceCalibrationDataJNI();

    public static native double[] GetSettingsJNI();

    public static native boolean IsDemoVersionJNI();

    public static native boolean IsDebugJNI();

    public static native void SetDebugJNI(boolean debug);

    public static native float GetHeightJNI(double lon, double lat);

    public static native long GetSystemIdJNI();

    public static native void LeaveJNI();

    public static native void FinalizeDem();

    public static native double[] LL2STM(double lon, double lat);

    public static native double[] STM2LL(double lon, double lat, int zone);

    public static native double[] LL2UTM(double lon, double lat);

    public static native double[] UTM2LL(double lon, double lat, String Zone);

    public static native String GetUTMZone();

    public static native double[] Decimal2DMS(double coord);

    public static native double DMS2Decimal(double d, double m, double s);

//    public static native String CoordinateToDMS(double LL);

//    public static native String CoordinatesToDMS(double lon, double lat);

    public static native String CoordinatesToDMSText(double coordinate);

    // polygon consists of pairs of doubles for each point (lon1,lat1,lon2,lat2,...,lonN,latN)
    public static native boolean StitchPolygon(double[] polygon, double fov_deg, int w, int h, double alt_above_ground, double Xpercent, double Ypercent, double ele_deg, int path_size);

    public static native double[] Projectile(double gun_lon0, double gun_lat0, double target_lon0, double target_lat0, int iterations, double z0, double time_step, double velocity0, double angle0, double diameter0, double mass0, double wind0, double error, double dencity0, double cofficient0, double temp0, double gravity0, boolean const_gravity0);

    public static native void SaveProjectile(String filename0, double gun_lon0, double gun_lat0, double target_lon0, double target_lat0, double z0, double time_step, double velocity0, double angle0, double diameter0, double mass0, double wind0, double error, double dencity0, double cofficient0, double temp0, double gravity0, boolean const_gravity0);

    public static String CoordinateToDMS(double LL){
        double[] res = Decimal2DMS(Math.abs(LL));
        if(LL >= 0)
            return "+"+String.format(Locale.ENGLISH, "%02d", (int)res[0])+"°"+String.format(Locale.ENGLISH, "%02d", (int)res[1])+"′"+String.format(Locale.ENGLISH, "%02.02f", res[2])+"″";
        else
            return "-"+String.format(Locale.ENGLISH, "%02d", (int)res[0])+"°"+String.format(Locale.ENGLISH, "%02d", (int)res[1])+"′"+String.format(Locale.ENGLISH, "%02.02f", res[2])+"″";
    }

    public static String CoordinatesToDMS(double lon, double lat,boolean oneLine){
        String strText0 = "";

        // latitude
        if(oneLine)
            strText0 += CoordinateToDMS(lat)+(lat>=0?" N":" S");
        else
            strText0 += " LAT: "+CoordinateToDMS(lat)+(lat>=0?" N":" S");

        if(oneLine)
            strText0 += ",";
        else
            strText0 += "\n";

        // longitude
        if(oneLine)
            strText0 += CoordinateToDMS(lon)+(lon>=0?" E":" W");
        else
            strText0 += " LON: "+CoordinateToDMS(lon)+(lon>=0?" E":" W");
        return strText0;
    }

    static  public InputMethodManager imm = null;
    static public void hide_keyboard(View view) {
        if (view == null) view = MainActivity.activity.getCurrentFocus();
        if (view != null) {
            if(imm == null){
                imm = (InputMethodManager) MainActivity.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            }
            if(imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    static public void writeLineToLog(String data) {
        try {
            File file;
            file = new File(MainActivity.strLogFile);
            FileOutputStream fOut = new FileOutputStream(file, true);
            data += "\n";
            fOut.write(data.getBytes());
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    static public void writeToLog(String data) {
        try {
            File file;
            file = new File(MainActivity.strLogFile);
            FileOutputStream fOut = new FileOutputStream(file, true);
            fOut.write(data.getBytes());
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    @SuppressLint("SimpleDateFormat")
    static public SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
    @SuppressLint("SimpleDateFormat")
    static public SimpleDateFormat sdf_filename = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");

    static public void MyLog(Throwable ex) {
        try {
            String currentDateTime = sdf.format(new Date());
            String strMessage = "<MapViewer Exception>\nTime: " + currentDateTime + "\nCause: " + ex.getCause() + "\nMessage: " + ex.getMessage() + "\nStackTrace: " + Arrays.toString(ex.getStackTrace()) + "\n<MapViewer Exception/>\n";
            Log.d(TAG, strMessage);
            writeLineToLog(strMessage);
            if (IsDebugJNI())
                Tab_Messenger.addError("Message: " + ex.getMessage() + "\nStackTrace: " + Arrays.toString(ex.getStackTrace()));
            else
                Tab_Messenger.addError(ex.getMessage());
        } catch (Throwable e) {
            Log.d(TAG, e.toString());
        }
    }

    static public void MyLogInfo(String strMessage) {
        try {
            String currentDateTime = sdf.format(new Date());
            String strNewMessage = "Info: " + currentDateTime + "\n" + strMessage;
            Log.d(TAG, strNewMessage);
            writeLineToLog(strNewMessage);
            if (IsDebugJNI())   Tab_Messenger.addError(strNewMessage);
        } catch (Throwable ex) {
            Log.d(TAG, ex.toString());
        }
    }

    static public void MyLogInfoEx(Throwable ex) {
        try {
            String currentDateTime = sdf.format(new Date());
            String strMessage = "<MapViewer Exception>\nTime: " + currentDateTime + "\nCause: " + ex.getCause() + "\nMessage: " + ex.getMessage() + "\nStackTrace: " + Arrays.toString(ex.getStackTrace()) + "\n<MapViewer Exception/>\n";
            Log.d(TAG, strMessage);
            writeLineToLog(strMessage);
            if (IsDebugJNI())   Tab_Messenger.addError("Message: " + ex.getMessage() + "\nStackTrace: " + Arrays.toString(ex.getStackTrace()));
        } catch (Throwable ex0) {
            Log.d(TAG, ex0.toString());
        }
    }

    static public void MyLogInfoSilent(String strMessage) {
        try {
            String currentDateTime = sdf.format(new Date());
            String strNewMessage = "Info: " + currentDateTime + "\n" + strMessage;
            Log.d(TAG, strNewMessage);
            writeLineToLog(strNewMessage);
        } catch (Throwable ex) {
            Log.d(TAG, ex.toString());
        }
    }

    public String get_files_count_text(String strPath) {
        int count = 0;
        File fileList[] = new File(strPath).listFiles();
        if (fileList != null) count = fileList.length;
        return Integer.toString(count);
    }

    private String nulltoIntegerDefalt(String value) {
        if (!isIntValue(value)) value = "0";
        return value;
    }

    private boolean isIntValue(String val) {
        try {
            val = val.replace(" ", "");
            mv_utils.parseInt(val);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
            return false;
        }
        return true;
    }

    /**
     * @return True if the path is writable. False otherwise.
     */
    public static boolean isWritable(File path) {
        try {
            File tmp = new File(path.getAbsolutePath() + File.separator + UUID.randomUUID().toString());
            FileOutputStream fos = new FileOutputStream(tmp);
            fos.write("hi".getBytes());
            fos.close();
            //noinspection ResultOfMethodCallIgnored
            tmp.delete();
            Log.i(TAG, path.getAbsolutePath() + " is writable");
            return true;
        } catch (Throwable ex) {
            Log.i(TAG, path.getAbsolutePath() + " is NOT writable");
            return false;
        }
    }

    static public AlertDialog pathsDialog;

    public void init() {
        try {
            setContentView(R.layout.activity_main);

            findViewById(R.id.mapviewer_drone_controller).setOnClickListener(this);
            findViewById(R.id.mapviewer_gps_navigation).setOnClickListener(this);
            findViewById(R.id.b_exit).setOnClickListener(this);
            findViewById(R.id.b_dark_mode).setOnClickListener(this);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

//    public static File commonDocumentDirPath(String FolderName) {
//        File dir = null;
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
//            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + FolderName);
//        } else {
//            dir = new File(Environment.getExternalStorageDirectory() + "/" + FolderName);
//        }
//
//        // Make sure the path directory exists.
//        if (!dir.exists()) {
//            // Make it, if it doesn't exit
//            boolean success = dir.mkdirs();
//            if (!success) {
//                dir = null;
//            }
//        }
//        return dir;
//    }

    //    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        ctx = getApplicationContext();

        SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
        MainActivity.def_server_port = settings.getInt("nPort", 6000);
        MainActivity.bAutoStartService = settings.getBoolean("bAutoStartService", bAutoStartService);
        MainActivity.is_encoded = settings.getBoolean("is_encoded", is_encoded);
        MainActivity.password = settings.getString("password", "");

        if(!Objects.equals(password, "")){
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.activity, R.style.CustomAlertDialog);
                builder.setTitle("Password");
                builder.setCancelable(false);

                // Set up the input
                final EditText input = new EditText(MainActivity.activity);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                //        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                //            input.setInputType(InputType.TYPE_CLASS_TEXT);
                //            input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton(MainActivity.ctx.getString(R.string.ok), (dialog, which) -> {
                    try
                    {
                        String new_password = input.getText().toString();
                        if(!new_password.equals(password)){
                            Tab_Messenger.showToast(getString(R.string.incorrect_password));
                            this.finishAndRemoveTask();
                        }
                        MainActivity.hide_keyboard(input);
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                });
                builder.setNegativeButton(MainActivity.ctx.getString(R.string.cancel), (dialog, which) -> {
                    try
                    {
                        this.finishAndRemoveTask();
                        MainActivity.hide_keyboard(input);
                        dialog.cancel();
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                });

                builder.setCancelable(false);
                builder.show();
            }
            catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
            }
        }

        if (isDrakMode())
            setTheme(R.style.AppTheme_Dark_NoActionBar);
//        else
//            setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        try {
            isAppStarted = true;

            check_notification_permission();
            if (!permissionGranted()) {
                requestPermission();
            }
            checkFileAccessPermission();
            if (MainActivity.isDevelpoment()){
                checkDisplayOverOtherAppsPermission();
                checkBatteryOptimizationPermission();
            }

            dir_internal = Objects.requireNonNull(Objects.requireNonNull(ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)).getParentFile()).getAbsolutePath();
//            dir_internal = (Objects.requireNonNull(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getParentFile())).getAbsolutePath();

//            dir_internal = Objects.requireNonNull(ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)).getAbsolutePath();
//            File dir = ctx.getExternalFilesDir(null);
//            if(dir != null)
//                dir_internal = dir.getAbsolutePath();
//            else
//                dir_internal = "";

            // this for tcp client/server communication
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // this for uri
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            // Disable Navigation Bar
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            // This work only for android 4.4+
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                getWindow().getDecorView().setSystemUiVisibility(flags);

                // Code below is to handle presses of Volume up or Volume down.
                // Without this, after pressing volume buttons, the navigation bar will
                // show up and won't hide
                final View decorView = getWindow().getDecorView();
                decorView
                        .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                        {
                            @Override
                            public void onSystemUiVisibilityChange(int visibility)
                            {
                                if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                                {
                                    decorView.setSystemUiVisibility(flags);
                                }
                            }
                        });
            }
            // Prevent automatic screen lock on android by code
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            //Remove title bar
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //Remove notification bar
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            // Prevent the keyboard from displaying on activity start
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
            CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE);

            checkAndRequestPermissions();

            init();

            // Load the icon pack on application start.
            loadIconPack();

//            createExternalStoragePublicPicture();
//            createExternalStoragePrivateFile();
//            writeAppSpecificExternalFile(ctx, true, "oghab.txt", "Hello world...");

            // MapViewer
            init_settings();

            if(!MainActivity.IsDemoVersionJNI()) {
                MainActivity.server.start(MainActivity.def_server_port);
            }

            // 2024.06.04
//            PhoneCallListener phoneListener = new PhoneCallListener();
//            TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
//
//            class ThreadPerTaskExecutor implements Executor {
//                public void execute(Runnable r) {
//                    new Thread(r).start();
//
//                }
//            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                telephonyManager.registerTelephonyCallback(new ThreadPerTaskExecutor(),new TelephonyCallback(){
//
//                });
//            }else{
//                telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
//            }

            kalman = new mv_Kalman(this);

            if(MainActivity.bAutoStartService){
                try{
                    new log("START THE FOREGROUND SERVICE ON DEMAND");
                    actionOnService(Actions.START);
                } catch (Throwable ex) {
                    MainActivity.MyLog(ex);
                }
            }

            // For Android 11+
//            init_settings();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if((android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) && hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Nullable
    public IconPack getIconPack() {
        return iconPack != null ? iconPack : loadIconPack();
    }

    private IconPack loadIconPack() {
        try {
            // Create an icon pack loader with application context.
            IconPackLoader loader = new IconPackLoader(this);

            // Create an icon pack and load all drawables.
            iconPack = IconPackDefault.createDefaultIconPack(loader);
            iconPack.loadDrawables(loader.getDrawableLoader());

            return iconPack;
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
            return null;
        }
    }

    static public String dir_internal;
    static public String dir_data;
    static public String strMapsPath;
    static public String strMapsUri;
    static public String strIconsPath;
    static public String strCachePath;
    static public String strTCPPath;
    static public String strVoiceName;
    static public String strVoice;
    static public String strImageCaptured;
    static public String strScreenShot;
    static public String strFrameCapturedName;
    static public String strFrameCaptured;
    static public String strVideoCaptured;
    static public String strMapCaptured;
    static public String strPlacemarkCaptured;
    static public String strIPsListName;
    static public String strIPsList;

    static public void delete_roots(){
        storageManager.deleteRoot(StorageManagerCompat.DEF_MAIN_ROOT);
        storageManager.deleteRoot(StorageManagerCompat.DEF_SD_ROOT);
        storageManager.deleteRoot(StorageManagerCompat.DEF_DATA_ROOT);
    }

    static final public int ROOT_MAIN = 100;
    static final public int ROOT_SD = 200;
    static final public int ROOT_DATA = 300;
    static final public int MV_ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION = 500;
    static public Root root_main = null;
    static public Root root_sd = null;
    static public Root root_data = null;

    static public void set_map_files(File[] files){
        if((files != null) && (files.length > 0)) {
            for(File file:files){
                MainActivity.MyLogInfo("Maps File["+file.getPath()+"]");
            }

            final IRegisterReceiver registerReceiver = new SimpleRegisterReceiver(MainActivity.ctx);
            OfflineTileProvider provider = new OfflineTileProvider(registerReceiver, files);
            Tab_Map.map.setTileProvider(provider);
        }else{
            MainActivity.MyLogInfo("Empty file list.");
        }
    }

    static public void set_maps_list(){
        root_data = storageManager.getRoot(StorageManagerCompat.DEF_DATA_ROOT);
        if (root_data == null || !root_data.isAccessGranted(ctx)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent intent = storageManager.requireExternalAccess(ctx);
                if(intent != null) {
                    Bundle options = new Bundle();
                    ActivityCompat.startActivityForResult(activity, intent, ROOT_DATA, options);
                }else{
                    MainActivity.MyLogInfo("Invalid Main Root.");
                }
            }else{
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                Bundle options = new Bundle();
                ActivityCompat.startActivityForResult(activity, intent, ROOT_DATA, options);
//                root_data = storageManager.addRoot(ctx, StorageManagerCompat.DEF_MAIN_ROOT, new File(strMapViewer));
//                if (root_data != null) {
//                    return list_files(root_data, "add_main_root - ROOT_MAIN");
//                }else{
//                    MainActivity.MyLogInfo("Main Root is NULL.");
//                }
            }
        }else{
            try {
                set_map_files(list_files(root_data, "add_data_root - ROOT_DATA"));
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        }
    }

    static public void add_main_root(){
        root_main = storageManager.getRoot(StorageManagerCompat.DEF_MAIN_ROOT);
        if (root_main == null || !root_main.isAccessGranted(ctx)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent intent = storageManager.requireExternalAccess(ctx);
                if(intent != null) {
                    Bundle options = new Bundle();
                    ActivityCompat.startActivityForResult(activity, intent, ROOT_MAIN, options);
                }else{
                    MainActivity.MyLogInfo("Invalid Main Root.");
                }
            }else{
                root_main = storageManager.addRoot(ctx, StorageManagerCompat.DEF_MAIN_ROOT, new File(strMapViewer));
                if (root_main != null) {
                    list_files(root_main, "add_main_root - ROOT_MAIN");
                }else{
                    MainActivity.MyLogInfo("Main Root is NULL.");
                }
            }
        }else{
            try {
                list_files(root_main, "add_main_root - ROOT_MAIN");
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        }
    }

    static public void add_sd_root(){
        root_sd = storageManager.getRoot(StorageManagerCompat.DEF_SD_ROOT);
        if (root_sd == null || !root_sd.isAccessGranted(ctx)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent intent = storageManager.requireExternalAccess(ctx);
                if (intent != null) {
                    Bundle options = new Bundle();
                    ActivityCompat.startActivityForResult(activity, intent, ROOT_SD, options);
                }
            }
        }else{
            try {
                list_files(root_sd, "add_sd_root - ROOT_SD");
//                DocumentFile f = root_sd.toRootDirectory(ctx);
//                if (f != null){
//                    DocumentFile subFolder = DocumentFileCompat.getSubFolder(f, "mysub");
//                    DocumentFile myFile = DocumentFileCompat.getFile(subFolder, "myfile", "image/png");
//                    OutputStream os = ctx.getContentResolver().openOutputStream(myFile.getUri());
//                    if(os != null) {
//                        os.write(1);
//                        os.flush();
//                        os.close();
//                    }
//                    MainActivity.MyLogInfo("[add_sd_root - ROOT_SD] myfile: "+myFile.getUri().getPath());
//                }
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        }
    }

    static public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = ctx.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }

    static public String getDocReadableFilePath(DocumentFile mDocFile, Context context) {
        if (mDocFile != null && mDocFile.isFile()) {
            try {
                ParcelFileDescriptor parcelFileDescriptor =
                        context.getContentResolver().openFileDescriptor(mDocFile.getUri(), "r"); // gets FileNotFoundException here, if file we used to have was deleted
                if (parcelFileDescriptor != null) {
                    int fd = parcelFileDescriptor.detachFd(); // if we want to close in native code
                    return "/proc/self/fd/" + fd;
                }
            }
            catch (FileNotFoundException fne) {
                return null;
            }
        }
        return null;
    }

    static public class DocumentFileWalker {
        public String str = "";
        public ArrayList<File> files = new ArrayList<File>();
        public void walk(DocumentFile root) {
            DocumentFile[] list = root.listFiles();
            for (DocumentFile f : list) {
                if (f.isDirectory()) {
                    str += "Folder: [" + f.getUri().getPath() + "]\n";
                    walk(f);
                } else {
                    str += "File: [" + f.getUri().getPath() + "]\n";

                    Uri uri = f.getUri();
                    File file = UriUtils.uri2File(uri);
                    if (file != null) {
                        files.add(file);
                    }
                }
            }
        }
    }

    static public File[] list_files(Root root, String title){
        try {
            DocumentFile f = root.toRootDirectory(ctx);
            if (f != null){
                DocumentFileWalker fw = new DocumentFileWalker();
                fw.walk(f);
                MainActivity.MyLogInfo("[list_files]: "+title+"\n"+fw.str);

                if(fw.files.size() > 0) {
                    File[] tmp = new File[fw.files.size()];
                    fw.files.toArray(tmp);
                    return tmp;
                }else{
                    MainActivity.MyLogInfo("fw.files.size() == 0");
                }
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return null;
    }

    public ArrayList<String> getAllPaths(){
        ArrayList<String> rootPaths = new ArrayList<>();
        File[] rootsStorage = ContextCompat.getExternalFilesDirs(ctx, null);
        for (File file : rootsStorage) {
            MainActivity.MyLogInfo(file.getAbsolutePath());
            String root = file.getAbsolutePath().replace("/Android/data/" + ctx.getPackageName() + "/files", "");
            rootPaths.add(root);
            MainActivity.MyLogInfo(root);
        }
        return rootPaths;
    }

    public void init_settings() {
        try {
            storageManager = new StorageManagerCompat(ctx);

            dir_data = dir_internal;

            LoadSettingsJNI(dir_internal, dir_data);
//            LoadSettingsJNI(dir_internal, dir_data);

            String strPath = dir_internal + File.separator;
            strMapViewer = strPath + "MapViewer" + File.separator;

            strTCPPath = strMapViewer + "TCP" + File.separator;
            File dir = new File(strTCPPath);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    MyLogInfo(strTCPPath + " not created...");
                }
            }
            strVoiceName = "voice.mp3";
            strVoice = strTCPPath + strVoiceName;
            strImageCaptured = strTCPPath + "image.jpg";
            strScreenShot = strTCPPath + "screenshot.jpg";
            strFrameCapturedName = "frame.jpg";
            strFrameCaptured = strTCPPath + strFrameCapturedName;
            strVideoCaptured = strTCPPath + "video.mp4";
            strMapCaptured = strTCPPath + "map.jpg";
            strPlacemarkCaptured = strTCPPath + "placemark.kml";
            strIPsListName = "IPs.csv";
            strIPsList = strTCPPath + strIPsListName;

//            Toast.makeText(ctx,dir_internal , Toast.LENGTH_LONG).show();

            strMapsPath = GetMapsPathJNI();
            strMapsUri = GetMapsUriJNI();
//            strMapsPath = dir_internal + "/MapViewer/Maps";
            strCachePath = GetCachePathJNI();
            strDBPath = GetDBPathJNI();
            strLogFile = GetLogPathJNI();
            strProjectileFile = GetProjectilePathJNI();

            strImagePath = dir_internal + "/MapViewer/MapViewer.jpg";
//            strAttrFile = dir_internal + "/MapViewer/MapViewer.txt";
            strDefaultFile = dir_internal + "/MapViewer/default.txt";
            strSettingsFile = dir_internal + "/MapViewer/Settings.txt";
            strLogFile = dir_internal + "/MapViewer/MapViewerLog.txt";
            strProjectileFile = dir_internal + "/MapViewer/projectile.csv";
            strIconsPath = dir_internal + "/MapViewer/icons/";

//            Toast.makeText(ctx,strLogFile , Toast.LENGTH_LONG).show();

//            File file = new File(strLogFile);
//            if(file.exists())   file.delete();

//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss", Locale.ENGLISH);
//            String currentDateTime = sdf.format(new Date());
//            MyLogInfo("MapViewer Log: " + currentDateTime);
//            MyLogInfo("MapViewer Info: ");
            MyLogInfo(GetOutputJNI());

//            MainActivity.MyLogInfo("MAPVIEWER_TYPE: "+ BuildConfig.MAPVIEWER_TYPE);

            if (!FileHelper.FileExists(strSettingsFile)) {
                save_settings();
            }

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            // load settings
            load_settings();

//            setContentView(new MapViewerView(ctx));
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public void set_fullscreen() {
//        set_fullscreen0();
        hide_keyboard(null);
    }

    static public void set_fullscreen0() {
        MainActivity.activity.runOnUiThread(() -> {
            try {
                View decorView = activity.getWindow().getDecorView();
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

                decorView.setOnSystemUiVisibilityChangeListener
                        (new View.OnSystemUiVisibilityChangeListener() {
                            @Override
                            public void onSystemUiVisibilityChange(int visibility) {
                                // Note that system bars will only be "visible" if none of the
                                // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                    // TODO: The system bars are visible. Make any desired
                                    // adjustments to your UI, such as showing the action bar or
                                    // other navigational controls.
                                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                                } else {
                                    // TODO: The system bars are NOT visible. Make any desired
                                    // adjustments to your UI, such as hiding the action bar or
                                    // other navigational controls.
                                }
                            }
                        });

                ActionBar actionBar = activity.getActionBar();
                if(actionBar != null)   actionBar.hide();
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        });
    }

    @Override
    protected void onStart() {
        try {
            super.onStart();
            if(kalman != null)  kalman.start_k();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    @Override
    protected void onStop() {
        try {
            MainActivity.save_settings();
            if(kalman != null)  kalman.stop_k();
            super.onStop();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                //bundle contains all info of "data" field of the notification
                String ip = bundle.getString("notification_ip");
                if(ip  != null){
                    call_navigation();
                }
            }
            set_fullscreen0();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            FinalizeDem();
            Tab_Messenger.close();

            isAppStarted = false;

            // Force Stop Entire Application
//            int id = android.os.Process.myPid();
//            android.os.Process.killProcess(id);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    public static boolean bDark = false;

    public void set_dark(boolean dark) {
        bDark = dark;
        File file = new File(GetDarkModePathJNI());
        if (bDark) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            file.delete();
        }
//        if(bDark)
//            setTheme(R.style.AppTheme_Dark_NoActionBar);
//        else
//            setTheme(R.style.AppTheme_NoActionBar);
    }

    public void exit_app() {
        save_settings();
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.closing_activity)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.yes_message, (dialog, which) -> {
//                MainActivity.set_fullscreen();
//                    MainActivity.super.onBackPressed();
//                    this.finish();
                    Tab_Messenger.close();
                    this.finishAndRemoveTask();
//                MainActivity.LeaveJNI();
                })
                .setNegativeButton(R.string.no_message, (dialog, which) -> {
                    MainActivity.set_fullscreen();
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            if (bCanExit) {
                exit_app();
            } else {
                bCanExit = true;
                init();
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    void call_drone_controller(){
        bCanExit = false;
        bNavigation = false;
        setContentView(new MapViewerView(ctx));
    }

    void call_navigation(){
        bCanExit = false;
        bNavigation = true;
        setContentView(new MapViewerView(ctx));
    }

    @Override
    public void onClick(View view) {
        try {
            int id = view.getId();
            if (id == R.id.mapviewer_drone_controller) {
                call_drone_controller();
            } else if (id == R.id.mapviewer_gps_navigation) {
                call_navigation();
            } else if (id == R.id.b_exit) {
                MainActivity.activity.exit_app();
            } else if (id == R.id.b_dark_mode) {
                MainActivity.bDark = isDrakMode();
                MainActivity.bDark = !MainActivity.bDark;
                MainActivity.activity.set_dark(MainActivity.bDark);
                Tab_Messenger.showToast("bDark: " + MainActivity.bDark);
                MainActivity.activity.exit_app();
//                recreate();
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

//    public static boolean isHereMapsSupported() {
//        String abi;
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            abi = Build.CPU_ABI;
//        } else {
//            abi = Build.SUPPORTED_ABIS[0];
//        }
//        DJILog.d(TAG, "abi=" + abi);
//
//        //The possible values are armeabi, armeabi-v7a, arm64-v8a, x86, x86_64, mips, mips64.
//        return abi.contains("arm");
//    }

//    public static boolean isGoogleMapsSupported(Context context) {
//        int resultCode = ConnectionResult.SERVICE_INVALID;
//        try {
//            GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
//            resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
//        } catch (Throwable ex) {
//            MainActivity.MyLog(ex);
//        }
//        return resultCode == ConnectionResult.SUCCESS;
//    }

    private void openScreenshot(File imageFile) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(imageFile);
            intent.setDataAndType(uri, "image/*");
            startActivity(intent);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    public static void writeStringAsFile(Context context, final String fileContents, String fileName) {
        if (fileContents == null) return;
        try {
            FileWriter out = new FileWriter(new File(fileName));
            out.write(fileContents);
            out.close();
        } catch (IOException e) {
        }
    }

    public static String readFileAsString(Context context, String fileName) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            BufferedReader in;

            try {
                in = new BufferedReader(new FileReader(new File(fileName)));
                while ((line = in.readLine()) != null) stringBuilder.append(line);

            } catch (IOException e) {
            }

            return stringBuilder.toString();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
            return "";
        }
    }

    /**
     * @param fileNameWithPath
     * @return all image exif attributes in bundle
     */
    public static Bundle getImageAttributes(String fileNameWithPath) {
        Bundle exifBundle = new Bundle();
        try {
            String latitudeRef, longitudeRef;
            Float latitude, longitude;

            ExifInterface exifInterface = new ExifInterface(fileNameWithPath);

            latitudeRef = exifInterface.getAttribute(TAG_GPS_LATITUDE_REF);

            assert latitudeRef != null;
            if (latitudeRef.equals("N")) {
                latitude = convertFromDegreeMinuteSeconds(exifInterface.getAttribute(TAG_GPS_LATITUDE));
            } else {
                latitude = 0 - convertFromDegreeMinuteSeconds(exifInterface.getAttribute(TAG_GPS_LATITUDE));
            }

            longitudeRef = exifInterface.getAttribute(TAG_GPS_LONGITUDE_REF);
            assert longitudeRef != null;
            if (longitudeRef.equals("E")) {
                longitude = convertFromDegreeMinuteSeconds(exifInterface.getAttribute(TAG_GPS_LONGITUDE));
            } else {
                longitude = 0 - convertFromDegreeMinuteSeconds(exifInterface.getAttribute(TAG_GPS_LONGITUDE));
            }

            exifBundle.putString(TAG_IMAGE_LENGTH, exifInterface.getAttribute(TAG_IMAGE_LENGTH));
            exifBundle.putString(TAG_IMAGE_WIDTH, exifInterface.getAttribute(TAG_IMAGE_WIDTH));
            exifBundle.putString(TAG_DATETIME, exifInterface.getAttribute(TAG_DATETIME));
            exifBundle.putString(TAG_MAKE, exifInterface.getAttribute(TAG_MAKE));
            exifBundle.putString(TAG_MODEL, exifInterface.getAttribute(TAG_MODEL));
            exifBundle.putString(TAG_ORIENTATION, exifInterface.getAttribute(TAG_ORIENTATION));
            exifBundle.putString(TAG_WHITE_BALANCE, exifInterface.getAttribute(TAG_WHITE_BALANCE));
            exifBundle.putString(ExifInterface.TAG_FOCAL_LENGTH, exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH));
            exifBundle.putString(ExifInterface.TAG_FLASH, exifInterface.getAttribute(ExifInterface.TAG_FLASH));
            exifBundle.putString(TAG_GPS_PROCESSING_METHOD, exifInterface.getAttribute(TAG_GPS_PROCESSING_METHOD));
            exifBundle.putString(ExifInterface.TAG_GPS_DATESTAMP, exifInterface.getAttribute(ExifInterface.TAG_GPS_DATESTAMP));
            exifBundle.putString(ExifInterface.TAG_GPS_TIMESTAMP, exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP));
            exifBundle.putString(TAG_GPS_LATITUDE, String.valueOf(latitude));
            exifBundle.putString(TAG_GPS_LATITUDE_REF, latitudeRef);
            exifBundle.putString(TAG_GPS_LONGITUDE, String.valueOf(longitude));
            exifBundle.putString(TAG_GPS_LONGITUDE_REF, longitudeRef);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return exifBundle;
    }

    private static Float convertFromDegreeMinuteSeconds(String stringDMS) {
        Float result = null;
        try {
            String[] DMS = stringDMS.split(",", 3);

            String[] stringD = DMS[0].split("/", 2);
            Double D0 = Double.valueOf(stringD[0]);
            Double D1 = Double.valueOf(stringD[1]);
            Double FloatD = D0 / D1;

            String[] stringM = DMS[1].split("/", 2);
            Double M0 = Double.valueOf(stringM[0]);
            Double M1 = Double.valueOf(stringM[1]);
            Double FloatM = M0 / M1;

            String[] stringS = DMS[2].split("/", 2);
            Double S0 = Double.valueOf(stringS[0]);
            Double S1 = Double.valueOf(stringS[1]);
            Double FloatS = S0 / S1;

            result = (float) (FloatD + (FloatM / 60) + (FloatS / 3600));
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return result;
    }

    public static void saveExif(String fileNameWithPath, Bundle extraInfo) throws IOException {
        try
        {
            if (extraInfo != null) {
                ExifInterface exif = new ExifInterface(fileNameWithPath);

                for (String info : extraInfo.keySet()) {
                    Object obj = extraInfo.get(info);
                    if (obj instanceof Location) {
                        Location location = (Location) obj;
                        exif.setAttribute(TAG_GPS_LATITUDE, convertToDegreeMinuteSeconds(location.getLatitude()));
                        exif.setAttribute(TAG_GPS_LATITUDE_REF, getLatitudeRef(location.getLatitude()));
                        exif.setAttribute(TAG_GPS_LONGITUDE, convertToDegreeMinuteSeconds(location.getLongitude()));
                        exif.setAttribute(TAG_GPS_LONGITUDE_REF, getLongitudeRef(location.getLongitude()));
                        break;
                    }
                }

                JSONObject json = new JSONObject();
                Set<String> keys = extraInfo.keySet();
                for (String info : keys) {
                    try {
                        json.put(info, extraInfo.get(info));
                    } catch (JSONException e) {
                        //Handle exception here
                    }
                }

                exif.setAttribute("UserComment", json.toString());
                exif.saveAttributes();
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    private static String SECRET_KEY = "TesH.sj73656dn@!#5hdj";
    private static String INIT_VECTOR = "KJgh7hd3dg%^G#gjsddd";

    public static String encrypt(String value) {
        try {
//            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
//            SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
//
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
//            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
//
//            byte[] encrypted = cipher.doFinal(value.getBytes());
//            return Base64.encodeToString(encrypted, Base64.DEFAULT);
            return Base64.encodeToString(value.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String value) {
        try {
//            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
//            SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
//
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
//            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
//            byte[] original = cipher.doFinal(Base64.decode(value, Base64.DEFAULT));
//
//            return new String(original);
            return new String(Base64.decode(value, Base64.DEFAULT), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static void saveExif(String fileNameWithPath, String text) {
        try
        {
            if (text == null)   return;
            if(!FileHelper.FileExists(fileNameWithPath)) return;
            ExifInterface exif = new ExifInterface(fileNameWithPath);
            if(exif != null) {
                exif.setAttribute("UserComment", encrypt(text));
                exif.saveAttributes();
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public static String getExif(String fileNameWithPath) {
        if(!FileHelper.FileExists(fileNameWithPath)) return null;
        String text = null;
        try
        {
            ExifInterface exif = new ExifInterface(fileNameWithPath);
            text = exif.getAttribute("UserComment");
            if(text != null) {
                text = decrypt(text);
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return text;
    }

    /**
     * returns ref for latitude which is S or N.
     *
     * @param latitude
     * @return S or N
     */
    private static String getLatitudeRef(double latitude) {
        return latitude < 0.0d ? "S" : "N";
    }

    /**
     * returns ref for latitude which is S or N.
     *
     * @param longitude
     * @return W or E
     */
    private static String getLongitudeRef(double longitude) {
        return longitude < 0.0d ? "W" : "E";
    }

    /**
     * convert latitude into DMS (degree minute second) format. For instance<br/>
     * -79.948862 becomes<br/>
     * 79/1,56/1,55903/1000<br/>
     * It works for latitude and longitude<br/>
     *
     * @param latitude could be longitude.
     * @return
     */
    private static String convertToDegreeMinuteSeconds(double latitude) {
        try {
            latitude = Math.abs(latitude);
            int degree = (int) latitude;
            latitude *= 60;
            latitude -= (degree * 60.0d);
            int minute = (int) latitude;
            latitude *= 60;
            latitude -= (minute * 60.0d);
            int second = (int) (latitude * 1000.0d);

            return degree +
                    "/1," +
                    minute +
                    "/1," +
                    second +
                    "/1000,";
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
            return "";
        }
    }

    static public void find(double fLon, double fLat) {
        try {
            strName = "";

            // database
            SQLiteDatabase db = SQLiteDatabase.openDatabase(strDBPath, null,
                    SQLiteDatabase.OPEN_READONLY, null);
            String strLon, strLat;
            strLon = String.format(Locale.ENGLISH, "%.06f", fLon);
            strLat = String.format(Locale.ENGLISH, "%.06f", fLat);

            // select * from syria where (ABS(LAT - 33)<=0.001 AND ABS(LONG - 36)<=0.001)
            // select * from syria where ((ABS((LAT - 33)*(LAT - 33)+(LONG - 36)*(LONG - 36))<=0.00001) AND (NT='NS'))
            // select * from syria where (ABS((LAT - 33)*(LAT - 33)+(LONG - 36)*(LONG - 36))<=0.00001)
            // select ABS((LAT - 33)*(LAT - 33)+(LONG - 36)*(LONG - 36)), ID, LAT, LONG, FULL_NAME_RO from syria ORDER BY 1 ASC
//            String strQuery = "select * from syria where (ID='"+ID+"')";
//            String strQuery = "select ABS((LAT - 33)*(LAT - 33)+(LONG - 36)*(LONG - 36)), ID, LAT, LONG, FULL_NAME_RO from syria ORDER BY 1 ASC";
            String strQuery = "select ABS((LAT - " + strLat + ")*(LAT - " + strLat + ")+(LONG - " + strLon + ")*(LONG - " + strLon + ")), ID, LAT, LONG, FULL_NAME_RO from syria ORDER BY 1 ASC";
//            strQuery = strQuery.toLowerCase(Locale.ENGLISH);
            strName = strQuery;
            Cursor res = db.rawQuery(strQuery, null);
            if (res.getCount() > 0) {
                res.moveToFirst();
                strName = res.getString(res.getColumnIndexOrThrow("FULL_NAME_RO"));
                strLon = res.getString(res.getColumnIndexOrThrow("LONG"));
                strLat = res.getString(res.getColumnIndexOrThrow("LAT"));
                fLon = mv_utils.parseDouble(strLon);
                fLat = mv_utils.parseDouble(strLat);
                double fAlt = GetHeightJNI(fLon, fLat);

                tab_map.set_poi_pos(fLon, fLat, fAlt, strName, true);
                MainActivity.tab_map.mapController.setCenter(MainActivity.tab_map.poiPoint);
                MainActivity.tab_map.mapController.setZoom(17.0);
            }
            db.close();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public int search(String strText) {
        try {
            if (strText.isEmpty()) return 0;
            strName = "";

            // database
            SQLiteDatabase db = SQLiteDatabase.openDatabase(strDBPath, null, SQLiteDatabase.OPEN_READONLY, null);

            String strQuery = "select * from syria where (FULL_NAME_RO like '%" + strText + "%')";
            strName = strQuery;
            Cursor citiesCursor = db.rawQuery(strQuery, null);

            Tab_Map.cities_adapter.clear();
            if (citiesCursor.getCount() > 0) {
                citiesCursor.moveToFirst();
                while (true) {
                    strName = citiesCursor.getString(citiesCursor.getColumnIndexOrThrow("FULL_NAME_RO"));

                    String strLon, strLat;
                    double fLon, fLat;
                    float fAlt;
                    strLon = citiesCursor.getString(citiesCursor.getColumnIndexOrThrow("LONG"));
                    strLat = citiesCursor.getString(citiesCursor.getColumnIndexOrThrow("LAT"));
                    fLon = mv_utils.parseDouble(strLon);
                    fLat = mv_utils.parseDouble(strLat);
                    fAlt = GetHeightJNI(fLon, fLat);

                    // Add item to adapter
                    City city = new City();
                    city.strName = strName;
                    city.fLon = fLon;
                    city.fLat = fLat;
                    city.fAlt = fAlt;
                    city.geometry_type = City.POINT;// Point
                    Tab_Map.cities_adapter.add(city);

                    if (citiesCursor.isLast()) break;
                    citiesCursor.moveToNext();
                }
                db.close();
                citiesCursor.close();
                return Tab_Map.cities_adapter.getCount();
            }

            db.close();
            citiesCursor.close();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return 0;
    }

    static public int searchMarks(String strText) {
        try {
            if (strText.isEmpty()) return 0;
            strName = "";
            Tab_Map.cities_adapter.clear();
            for(int i=0;i<Tab_Map.favorites_adapter.getCount();i++){
                City city = Tab_Map.favorites_adapter.getItem(i);
                if(city != null){
                    if(city.strName.toLowerCase().contains(strText)){
                        Tab_Map.cities_adapter.add(city);
                    }
                }
            }
            return Tab_Map.cities_adapter.getCount();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return 0;
    }

    public static void save_settings() {
        try {
            if ((MainActivity.tab_map != null) && (Tab_Map.map != null)) {
                MainActivity.map_lon = Tab_Map.map.getMapCenter().getLongitude();
                MainActivity.map_lat = Tab_Map.map.getMapCenter().getLatitude();
                MainActivity.map_zoom = Tab_Map.map.getZoomLevelDouble();
                MainActivity.map_rot = Tab_Map.map.getMapOrientation();
                SaveSettingsJNI(target_x, target_y, fov_h, fov_v, w, h, uav_lon, uav_lat, uav_alt, uav_yaw, uav_pitch, uav_roll, gimb_yaw, gimb_pitch, max_dist, step, home_lon, home_lat, map_lon, map_lat, map_zoom, map_rot, target_lon, target_lat, target_alt, laser_dist);
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    public static void load_settings() {
        try {
            int idx = 0;
            double[] res = GetSettingsJNI();
            w = (int) res[idx++];
            h = (int) res[idx++];
            target_x = (int) res[idx++];
            target_y = (int) res[idx++];
            fov_h = (float) res[idx++];
            fov_v = (float) res[idx++];
            uav_lon = res[idx++];
            uav_lat = res[idx++];
            uav_alt = res[idx++];
            uav_yaw = (float) res[idx++];
            uav_pitch = (float) res[idx++];
            uav_roll = (float) res[idx++];
            gimb_yaw = (float) res[idx++];
            gimb_pitch = (float) res[idx++];
            max_dist = res[idx++];
            step = res[idx++];

            laser_dist = (float) res[idx++];
            target_lon = res[idx++];
            target_lat = res[idx++];
            target_alt = (float) res[idx++];

            home_lon = res[idx++];
            home_lat = res[idx++];

            map_lon = res[idx++];
            map_lat = res[idx++];
            map_zoom = res[idx++];
            map_rot = res[idx++];
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    public static class InputQueryResult {
        public final String result;
        public final boolean ok;

        public InputQueryResult(String result, boolean ok) {
            this.result = result;
            this.ok = ok;
        }
    }

    static private String strValue = "1000";
    private boolean is_ok = false;

    public InputQueryResult InputQuery(String strTitl, int type, String strDefault) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(strTitl);
            builder.setCancelable(false);

            // Set up the input
            strValue = strDefault;
            final EditText input = new EditText(activity);
            input.setText(strDefault);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            //        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            //            input.setInputType(InputType.TYPE_CLASS_TEXT);
            //            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setInputType(type);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton(MainActivity.ctx.getString(R.string.ok), (dialog, which) -> {
                try {
                    is_ok = true;
                    strValue = input.getText().toString();
                    MainActivity.hide_keyboard(input);
                } catch (Throwable ex) {
                    MainActivity.MyLog(ex);
                }
            });
            builder.setNegativeButton(MainActivity.ctx.getString(R.string.cancel), (dialog, which) -> {
                try {
                    is_ok = false;
                    MainActivity.hide_keyboard(input);
                    dialog.cancel();
                } catch (Throwable ex) {
                    MainActivity.MyLog(ex);
                }
            });

            builder.setCancelable(false);
            builder.show();
            Tab_Messenger.showToast("Dialogue");
//            Toast.makeText(getApplicationContext(), "Dialogue", Toast.LENGTH_LONG).show();
            return new InputQueryResult(strValue, is_ok);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
            return new InputQueryResult(null, false);
        }
    }

    static public double db_mod(double x, double y) {
        int n;
        double r = 0;
        try {
            n = (int) (x / y);
            r = x - n * y;
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return r;
    }

    static public double db_deg(double x) {
        return db_mod(x + 100.0 * 360.0, 360.0);
    }

//    static public double db_deg2(double x)
//    {
//        return db_mod(x + 100.0*360.0,360.0) - 180.0;
//    }

    static public int findScreenOrientation() {
        int rotation;
        try {
            String windowSrvc = Context.WINDOW_SERVICE;
            WindowManager wm = ((WindowManager) MainActivity.activity.getSystemService(windowSrvc));
            assert wm != null;
            Display display = wm.getDefaultDisplay();
            rotation = display.getRotation();
            switch (rotation) {
                case (Surface.ROTATION_0):
                    break; // Natural
                case (Surface.ROTATION_90):
                    break; // On its left side
                case (Surface.ROTATION_180):
                    break; // Updside down
                case (Surface.ROTATION_270):
                    break; // On its right side
                default:
                    break;
            }
        } catch (NullPointerException e) {
            rotation = Surface.ROTATION_0;
        }
        return rotation;
    }

    public static boolean checkGpsCoordinates(double latitude, double longitude) {
        return ((latitude > -90) && (latitude < 90) && (longitude > -180) && (longitude < 180) && (latitude != 0) && (longitude != 0));
    }

    static public boolean isDrakMode() {
        return FileHelper.FileExists(GetDarkModePathJNI());
    }

    static public boolean isDevelpoment() {
//        return FileHelper.FileExists(GetDevelopmentPathJNI());
        return true;
    }

    // Below, forked from https://github.com/oguzhantopgul/Android-Emulator-Detection/blob/master/src/com/ouz/evasion/EvasionMainActivity.java
// (IMO) "ro.secure", "ro.kernel.qemu" cann't work with jailbreak.
// This properties mean adb shell as root. So not only virtual device, also jailbreak too.
//    static public Boolean isEmulator(Context paramContext)
//    {
//        Boolean isEmulator = false;
//        try {
//            Class SystemProperties = Class.forName("android.os.SystemProperties");
//            TelephonyManager localTelephonyManager = (TelephonyManager)paramContext.getSystemService(TELEPHONY_SERVICE);
//            if (getProperty(SystemProperties, "ro.secure").equalsIgnoreCase("0"))
//                isEmulator =  Boolean.valueOf(true);
//            else if (getProperty(SystemProperties, "ro.kernel.qemu").equalsIgnoreCase("1"))
//                isEmulator =  Boolean.valueOf(true);
//            else if (Build.PRODUCT.contains("sdk"))
//                isEmulator =  Boolean.valueOf(true);
//            else if (Build.MODEL.contains("sdk"))
//                isEmulator =  Boolean.valueOf(true);
//            else if(localTelephonyManager.getSimOperatorName().equals("Android"))
//                isEmulator =  Boolean.valueOf(true);
//            else if(localTelephonyManager.getNetworkOperatorName().equals("Android"))
//                isEmulator =  Boolean.valueOf(true);
//            else
//                isEmulator =  Boolean.valueOf(false);
//
//            String msg = "ro.secure = " + getProperty(SystemProperties, "ro.secure") +
//                    "\nro.kernel.qemu = " + getProperty(SystemProperties, "ro.kernel.qemu") +
//                    "\nSimOperatorName = " + localTelephonyManager.getSimOperatorName() +
//                    "\nNetworkOperatorName = " + localTelephonyManager.getNetworkOperatorName();
//            Log.i("adb", msg);
//            AlertDialog.Builder alertDialog = new AlertDialog.Builder(paramContext);
//            alertDialog.setTitle("info");
//            alertDialog.setMessage(msg);
//            alertDialog.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return isEmulator;
//    }

//    private static String getProperty(Class myClass, String propertyName) throws Exception {
//        return (String) myClass.getMethod("get", String.class).invoke(myClass, propertyName);
//    }

    static public Bitmap addColor(Bitmap src, int color) {
        if (src == null) {
            return null;
        }
        // Source image size
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        //get pixels
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        int r0 = Color.red(color);
        int g0 = Color.green(color);
        int b0 = Color.blue(color);

        for (int x = 0; x < pixels.length; ++x) {
            int c = pixels[x];
            int r1 = Color.red(c);
            int g1 = Color.green(c);
            int b1 = Color.blue(c);
            int a = Color.alpha(c);

//            int r2 = (int) ((r0 + r1)/1.0);
//            int g2 = (int) ((g0 + g1)/1.0);
//            int b2 = (int) ((b0 + b1)/1.0);

            int r2 = fix(r0 + r1);
            int g2 = fix(g0 + g1);
            int b2 = fix(b0 + b1);

            pixels[x] = Color.argb(a, r2, g2, b2);
        }

        // create result bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
        //set pixels
        result.setPixels(pixels, 0, width, 0, 0, width, height);

        return result;
    }

    static int fix(int c) {
        if (c > 255) c = 255;
        if (c < 0) c = 0;
        return c;
    }

    static public @NonNull
    Rect getTextBackgroundSize(float x, float y, @NonNull String text, @NonNull Paint paint) {
        try {
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float halfTextLength = paint.measureText(text) / 2 + 5;
            //        return new Rect((int) (x - halfTextLength), (int) (y + fontMetrics.top), (int) (x + halfTextLength), (int) (y + fontMetrics.bottom));
            return new Rect((int) (x), (int) (y), (int) (x + 2 * halfTextLength), (int) (y - (fontMetrics.bottom - fontMetrics.top)));
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
            return new Rect(0, 0, 0, 0);
        }
    }

    private boolean permissionGranted() {
        return ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 18767);
    }

    /**
     * Result of runtime permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        try {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            // Check for granted permission and remove from missing list
            if (requestCode == REQUEST_PERMISSION_CODE) {
                for (int i = grantResults.length - 1; i >= 0; i--) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        missingPermission.remove(permissions[i]);
                    }
                }
            }
            // If there is enough permission, we will start the registration
            if (missingPermission.isEmpty()) {
//                startSDKRegistration();
            } else {
                String message = "Missing permissions! Will not register SDK to connect to aircraft.\n" + missingPermission;
                MainActivity.MyLogInfo(message);
                Tab_Messenger.showToast(message);
//                Toast.makeText(getApplicationContext(), "Missing permissions! Will not register SDK to connect to aircraft.", Toast.LENGTH_LONG).show();
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    private void loginAccount() {
        if (AppActivationManager.getInstance().getAppActivationState() == AppActivationState.LOGIN_REQUIRED) {
            UserAccountManager.getInstance().logIntoDJIUserAccount(MainActivity.ctx,
                    new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
                        @Override
                        public void onSuccess(final UserAccountState userAccountState) {
                            Tab_Messenger.showToast("Login Success!");
//                            Toast.makeText(getApplicationContext(),
//                                    "Login Success!",
//                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(DJIError error) {
                            Tab_Messenger.showToast("Login Error!");
//                            Toast.makeText(getApplicationContext(),
//                                    "Login Error!",
//                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private final AtomicBoolean isRegistrationInProgress = new AtomicBoolean(false);
    DJISDKManager.SDKManagerCallback registrationCallback = null;
    public void startSDKRegistration() {
        try {
            if (MApplication.isRealDevice()) {
                if (isRegistrationInProgress.compareAndSet(false, true)) {
                    // 2021.01.13
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        try {
                            if (MApplication.isRealDevice()) {
                                if(registrationCallback == null){
                                    registrationCallback = new DJISDKManager.SDKManagerCallback() {
                                        @Override
                                        public void onRegister(DJIError error) {
                                            try {
                                                isRegistrationInProgress.set(false);
                                                if (error == DJISDKError.REGISTRATION_SUCCESS) {
                                                    loginAccount();
                                                    if (MApplication.isRealDevice()) {
                                                        DJISDKManager.getInstance().startConnectionToProduct();
                                                        Tab_Messenger.showToast("SDK registration succeeded!");
                                                        try {
                                                            MainActivity.activity.runOnUiThread(() -> {
                                                                try {
                                                                    if(MainActivity.tab_camera != null) {
                                                                        MainActivity.tab_camera.update_camera();
                                                                        MainActivity.tab_camera.notifyStatusChange();
                                                                    }
                                                                } catch (Throwable ex) {
                                                                    MainActivity.MyLog(ex);
                                                                }
                                                            });
                                                        } catch (Throwable ex) {
                                                            MainActivity.MyLog(ex);
                                                        }
                                                    }
                                                } else {
                                                    Tab_Messenger.showToast("SDK registration failed, check network and retry!");
                                                }
                                            } catch (Throwable ex) {
                                                MainActivity.MyLog(ex);
                                            }
                                        }

                                        @Override
                                        public void onProductDisconnect() {
                                            try {
                                                Tab_Messenger.showToast("product disconnect!");
                                            } catch (Throwable ex) {
                                                MainActivity.MyLog(ex);
                                            }
                                        }

                                        @Override
                                        public void onProductConnect(BaseProduct product) {
                                            try {
                                                product.getName(new CommonCallbacks.CompletionCallbackWith<String>() {
                                                    @Override
                                                    public void onSuccess(String s) {
                                                        MainActivity.productName = s;
                                                        Tab_Messenger.showToast("product name ["+MainActivity.productName+"]");
                                                    }

                                                    @Override
                                                    public void onFailure(DJIError djiError) {

                                                    }
                                                });
                                                Tab_Messenger.showToast("product connect! ["+MainActivity.productName+"]");
                                            } catch (Throwable ex) {
                                                MainActivity.MyLog(ex);
                                            }
                                        }

                                        @Override
                                        public void onProductChanged(BaseProduct baseProduct) {
//                                            try {
//                                                Tab_Messenger.showToast("product changed!");
//                                            } catch (Throwable ex) {
//                                                MainActivity.MyLog(ex);
//                                            }
                                        }

                                        @Override
                                        public void onComponentChange(BaseProduct.ComponentKey key,
                                                                      BaseComponent oldComponent,
                                                                      BaseComponent newComponent) {
//                                            try {
//                                                Tab_Messenger.showToast("Component changed!");
//                                            } catch (Throwable ex) {
//                                                MainActivity.MyLog(ex);
//                                            }
                                        }

                                        @Override
                                        public void onInitProcess(DJISDKInitEvent djisdkInitEvent, int i) {
                                            //AliSoft 2019.11.07
                                        }

                                        @Override
                                        public void onDatabaseDownloadProgress(long l, long l1) {
                                            //AliSoft 2019.11.07
                                        }
                                    };
                                }
                                DJISDKManager.getInstance().registerApp(MainActivity.ctx, registrationCallback);
                            }
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    });
                }
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    /**
     * Checks if there is any missing permissions, and
     * requests runtime permission if needed.
     */
    public void checkAndRequestPermissions() {
        try {
            // Check for permissions
            for (String eachPermission : REQUIRED_PERMISSION_LIST) {
                if (eachPermission.equals(Manifest.permission.ACTIVITY_RECOGNITION)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ContextCompat.checkSelfPermission(MainActivity.ctx, eachPermission) != PackageManager.PERMISSION_GRANTED) {
                            missingPermission.add(eachPermission);
                        }
                    }
                } else if (eachPermission.equals(Manifest.permission.ACCESS_MEDIA_LOCATION)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ContextCompat.checkSelfPermission(MainActivity.ctx, eachPermission) != PackageManager.PERMISSION_GRANTED) {
                            missingPermission.add(eachPermission);
                        }
                    }
                } else if (eachPermission.equals(Manifest.permission.POST_NOTIFICATIONS)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ContextCompat.checkSelfPermission(MainActivity.ctx, eachPermission) != PackageManager.PERMISSION_GRANTED) {
                            missingPermission.add(eachPermission);
                        }
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(MainActivity.ctx, eachPermission) != PackageManager.PERMISSION_GRANTED) {
                        missingPermission.add(eachPermission);
                    }
                }
            }
            // Request for missing permissions
            if (missingPermission.isEmpty()) {
//                startSDKRegistration();
            } else {
                ActivityCompat.requestPermissions(MainActivity.activity,
                        missingPermission.toArray(new String[0]),
                        REQUEST_PERMISSION_CODE);
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    public void send_voice_to_server() {
        Tab_Messenger.sendFile(strVoice, true, "Voice sending...", true, new tcp_io_handler.SendCallback() {
            @Override
            public void onFinish(int error) {
                if(error != tcp_io_handler.TCP_OK) {
                    if (MainActivity.IsDebugJNI()) {
                        MainActivity.MyLogInfoSilent("The Current Line Number is " + Arrays.toString(new Throwable().getStackTrace()));
                    }
                }
            }
        });
    }

    public boolean send_location_to_server() {
        Location location = mv_LocationOverlay.curr_location;
        if (location != null) {
            SerializableLocation serializable = new SerializableLocation(location);
            return Tab_Messenger.sendLocation(serializable);
        } else {
            Tab_Messenger.addError("Please turn on your GPS.");
        }
        return false;
    }

//    private ActivityResultLauncher launcher =
//            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                    new ActivityResultCallback<ActivityResult>() {
//                        @Override
//                        public void onActivityResult(ActivityResult result) {
//                            if (result.getResultCode() == Activity.RESULT_OK) {
//                                // Use the uri to load the image
//                                Uri uri = result.getData().getData();
//                                // Use the file path to set image or upload
//                                String filePath = result.getData().getStringExtra(Const.BundleExtras.FILE_PATH);
//                                //...
//
//                                // for Multiple picks
//                                // first item
//                                Uri first = result.getData().getData();
//                                // other items
//                                ClipData clipData = result.getData().getClipData();
//                                // Multiple file paths list
//                                ArrayList<String> filePaths = result.getData().getStringArrayListExtra(Const.BundleExtras.FILE_PATH_LIST);
//                                //...
//                            }
//                        }
//                    });

    //Instead of onActivityResult() method use this one
    static public String mv_activity_type;
    static public String mv_activity_ip;
    ActivityResultLauncher<Intent> myActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    try {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                            if(intent != null){
                                Uri uri = intent.getData();
                                if(uri != null){
//                                    MainActivity.MyLogInfo(uri.toString());
//                                    MainActivity.MyLogInfo("mv_activity_type == "+mv_activity_type+" line_number: "+new Throwable().getStackTrace()[0].getLineNumber());
                                    if(Objects.equals(mv_activity_type, "send_file_to_server")){
                                        try {
                                            Thread thread = new Thread() {
                                                @Override
                                                public void run() {
                                                    MainActivity.activity.runOnUiThread(() -> {
                                                        try {
                                                            File file = UriUtils.uri2File(uri);
                                                            String path = file.getAbsolutePath();
                                                            Tab_Messenger.sendFile(path, true, "File sending...", true, new tcp_io_handler.SendCallback() {
                                                                @Override
                                                                public void onFinish(int error) {
                                                                    if(error != tcp_io_handler.TCP_OK) {
                                                                        if (MainActivity.IsDebugJNI()) {
                                                                            MainActivity.MyLogInfoSilent("The Current Line Number is " + Arrays.toString(new Throwable().getStackTrace()));
                                                                        }
                                                                    }
//                                                                    MainActivity.set_fullscreen();
                                                                }
                                                            });
                                                        } catch (Throwable ex) {
                                                            MainActivity.MyLog(ex);
                                                        }
                                                    });
                                                }
                                            };
                                            thread.start();
                                        } catch (Throwable ex) {
                                            MainActivity.MyLog(ex);
                                        }
                                    }else if(Objects.equals(mv_activity_type, "select_avatar")){
                                        try {
                                            String ip = mv_activity_ip;
                                            if(ip != null){
                                                Thread thread = new Thread() {
                                                    @Override
                                                    public void run() {
                                                        MainActivity.activity.runOnUiThread(() -> {
                                                            try {
                                                                File file = UriUtils.uri2File(uri);
                                                                String avatar_path = file.getAbsolutePath();
                                                                Bitmap resized = resizeBitmap(avatar_path, 48, 48);
                                                                Tab_Messenger.update_ip_avatar(ip, resized, true);

                                                                // update view
                                                                for (int i = 0; i < Tab_Messenger.users.size(); i++) {
                                                                    tcp_user user = Tab_Messenger.users.get(i);
                                                                    if (user.ip.equals(Tab_Messenger.active_ip)) {
                                                                        user.setAvatarPath(avatar_path);
                                                                        Tab_Messenger.ipAdapter.notifyItemChanged(i);
                                                                        break;
                                                                    }
                                                                }
                                                            } catch (Throwable ex) {
                                                                MainActivity.MyLog(ex);
                                                            }
                                                        });
                                                    }
                                                };
                                                thread.start();

                                                MainActivity.set_fullscreen();
                                            }
                                        } catch (Throwable ex) {
                                            MainActivity.MyLog(ex);
                                        }
                                    }
                                }
//                                else{
//                                    MainActivity.MyLogInfo("uri == null"+" line_number: "+new Throwable().getStackTrace()[0].getLineNumber());
//                                }
                            }
//                            else{
//                                MainActivity.MyLogInfo("data == null"+" line_number: "+new Throwable().getStackTrace()[0].getLineNumber());
//                            }
                        }
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                }
            });

    public void send_file_to_server() {
        try {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                intent = storageManager.requireExternalAccess(ctx);
            }else{
                intent = new Intent();
            }
            if(intent == null)  return;
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            mv_activity_type = "send_file_to_server";
            myActivityResultLauncher.launch(intent);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    public void send_file_to_server0() {
//        if(permissionGranted()) {
//            ArrayList<String> docs = new ArrayList<>();
//            docs.add(DocPicker.DocTypes.PDF);
//            docs.add(DocPicker.DocTypes.MS_WORD);
//            docs.add(DocPicker.DocTypes.MS_POWERPOINT);
//            docs.add(DocPicker.DocTypes.MS_EXCEL);
//            docs.add(DocPicker.DocTypes.TEXT);
//            docs.add(DocPicker.DocTypes.IMAGE);
//            docs.add(DocPicker.DocTypes.AUDIO);
//            docs.add(DocPicker.DocTypes.VIDEO);
//
//            DocPickerConfig pickerConfig = new DocPickerConfig()
//                .setAllowMultiSelection(false)
//                .setShowConfirmationDialog(true)
//                .setExtArgs(docs);
//
//            DocPicker.with(this)
//                .setConfig(pickerConfig)
//                .onResult()
//                .subscribe(new Observer<ArrayList<Uri>>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) { }
//
//                    @Override
//                    public void onNext(@NonNull ArrayList<Uri> uris) {
//                        //uris: list of uri
//                        try {
//                            if(uris.size() > 0) {
////                                Tab_Messenger.addError(uris.get(0).getPath());
////                                Tab_Messenger.sendFile(uris.get(0).getPath(), true, "File sending...");
////                                MainActivity.set_fullscreen();
//                            }
//                        } catch (Throwable ex) {
//                            MainActivity.MyLog(ex);
//                        }
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) { }
//
//                    @Override
//                    public void onComplete() { }
//                });
//        }
//        else{
//            requestPermission();
//        }

//        if (permissionGranted()) {
//            Util.searchCategory.clear();
//            Util.searchCategory.addAll(Util.searchCategoryAll);
//            SingleFilePickerDialog singleFilePickerDialog = new SingleFilePickerDialog(this,
//                    new OnCancelPickerDialogListener() {
//                        @Override
//                        public void onCanceled() {
//                            Toast.makeText(ctx, "Canceled!!", Toast.LENGTH_SHORT).show();
//                            MainActivity.set_fullscreen();
//                        }
//                    },
//                    new OnConfirmDialogListener() {
//                        @Override
//                        public void onConfirmed(File... files) {
//                            try {
//                                Thread thread = new Thread() {
//                                    @Override
//                                    public void run() {
//                                        MainActivity.activity.runOnUiThread(() -> {
//                                            try {
//                                                Tab_Messenger.sendFile(files[0].getPath(), true, "File sending...", true, new tcp_io_handler.SendCallback() {
//                                                    @Override
//                                                    public void onFinish(int error) {
//                                                        if(error != tcp_io_handler.TCP_OK) {
//                                                            if (MainActivity.IsDebugJNI()) {
//                                                                MainActivity.MyLogInfoSilent("The Current Line Number is " + Arrays.toString(new Throwable().getStackTrace()));
//                                                            }
//                                                        }
//                                                        MainActivity.set_fullscreen();
//                                                    }
//                                                });
//                                            } catch (Throwable ex) {
//                                                MainActivity.MyLog(ex);
//                                            }
//                                        });
//                                    }
//                                };
//                                thread.start();
//                            } catch (Throwable ex) {
//                                MainActivity.MyLog(ex);
//                            }
//                        }
//                    }
//            );
//            singleFilePickerDialog.show();
//
////            MultiFilePickerDialog multiFilePickerDialog = new MultiFilePickerDialog(MainActivity.this,
////                new OnCancelPickerDialogListener() {
////                    @Override
////                    public void onCanceled() {
////                        Toast.makeText(MainActivity.this, "Canceled!!", Toast.LENGTH_SHORT).show();
////                    }
////                },
////                new OnConfirmDialogListener() {
////                    @Override
////                    public void onConfirmed(File... files) {
////                        for(File file : files){
////                            Tab_Messenger.client.handler.sendFile(file.getAbsolutePath());
////                        }
////                    }
////                }
////            );
////            multiFilePickerDialog.show();
//
////            DirectoryPickerDialog directoryPickerDialog = new DirectoryPickerDialog(this,
////                    new OnCancelPickerDialogListener() {
////                        @Override
////                        public void onCanceled() {
////                            Toast.makeText(MainActivity.this, "Canceled!!", Toast.LENGTH_SHORT).show();
////                        }
////                    },
////                    new OnConfirmDialogListener() {
////                        @Override
////                        public void onConfirmed(File... files) {
////                            Tab_Messenger.server.handler.sendFileToAll(files[0].getPath());
////                        }
////                    }
////            );
////            directoryPickerDialog.show();
//        } else {
//            requestPermission();
//        }
    }

    public void import_ip_list() {
        if (permissionGranted()) {
//            Util.searchCategory.clear();
//            Util.searchCategory.add(Util.CSV_CATEGORY);
//            SingleFilePickerDialog singleFilePickerDialog = new SingleFilePickerDialog(this,
//                    new OnCancelPickerDialogListener() {
//                        @Override
//                        public void onCanceled() {
//                            Toast.makeText(ctx, "Canceled!!", Toast.LENGTH_SHORT).show();
//                            MainActivity.set_fullscreen();
//                        }
//                    },
//                    new OnConfirmDialogListener() {
//                        @Override
//                        public void onConfirmed(File... files) {
//                            try {
//                                Thread thread = new Thread() {
//                                    @Override
//                                    public void run() {
//                                        MainActivity.activity.runOnUiThread(() -> {
//                                            try {
//                                                Tab_Messenger.import_ip_list(files[0].getPath());
//                                            } catch (Throwable ex) {
//                                                MainActivity.MyLog(ex);
//                                            }
//                                        });
//                                    }
//                                };
//                                thread.start();
//                            } catch (Throwable ex) {
//                                MainActivity.MyLog(ex);
//                            }
//                        }
//                    }
//            );
//            singleFilePickerDialog.show();
        } else {
            requestPermission();
        }
    }

    public Bitmap resizeBitmap(String photoPath, int targetW, int targetH) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true; //Deprecated API 21

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }

    public void delete_user(String ip) {
        try {
            new AlertDialog.Builder(MainActivity.activity)
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.delete)
                    .setMessage(R.string.are_you_sure_delete)
                    .setPositiveButton(R.string.yes_message, (dialog, which) -> {
                        Tab_Messenger.delete_ip_user(ip);
                        Tab_Messenger.showToast("[" + ip + "] Deleted...");
                        MainActivity.hide_keyboard(null);
                    })
                    .setNegativeButton(R.string.no_message, (dialog, which) -> {
                        MainActivity.hide_keyboard(null);
                    })
                    .show();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    public void delete_all() {
        try {
            new AlertDialog.Builder(MainActivity.activity)
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.delete)
                    .setMessage(R.string.are_you_sure_delete)
                    .setPositiveButton(R.string.yes_message, (dialog, which) -> {
                        Tab_Messenger.delete_all_ips();
                        Tab_Messenger.showToast("All Users Deleted...");
                        MainActivity.hide_keyboard(null);
                    })
                    .setNegativeButton(R.string.no_message, (dialog, which) -> {
                        MainActivity.hide_keyboard(null);
                    })
                    .show();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    public void select_avatar(String ip) {
        try {
            try {
                Intent intent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    intent = storageManager.requireExternalAccess(ctx);
                }else{
                    intent = new Intent();
                }
                if(intent == null)  return;
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                // set MIME type for image
                intent.setType("image/*");
                mv_activity_type = "select_avatar";
                mv_activity_ip = ip;
                myActivityResultLauncher.launch(intent);
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }

//        if (permissionGranted()) {
//            Util.searchCategory.clear();
//            Util.searchCategory.add(Util.IMAGE_CATEGORY);
//            SingleFilePickerDialog singleFilePickerDialog = new SingleFilePickerDialog(this,
//                    new OnCancelPickerDialogListener() {
//                        @Override
//                        public void onCanceled() {
//                            Toast.makeText(MainActivity.this, "Canceled!!", Toast.LENGTH_SHORT).show();
//                            MainActivity.set_fullscreen();
//                        }
//                    },
//                    new OnConfirmDialogListener() {
//                        @Override
//                        public void onConfirmed(File... files) {
//                            try {
//                                Thread thread = new Thread() {
//                                    @Override
//                                    public void run() {
//                                        MainActivity.activity.runOnUiThread(() -> {
//                                            try {
//                                                String avatar_path = files[0].getPath();
//                                                Bitmap resized = resizeBitmap(avatar_path, 48, 48);
//                                                Tab_Messenger.update_ip_avatar(ip, resized, true);
//
//                                                // update view
//                                                for (int i = 0; i < Tab_Messenger.users.size(); i++) {
//                                                    tcp_user user = Tab_Messenger.users.get(i);
//                                                    if (user.ip.equals(Tab_Messenger.active_ip)) {
//                                                        user.setAvatarPath(avatar_path);
//                                                        Tab_Messenger.ipAdapter.notifyItemChanged(i);
//                                                        break;
//                                                    }
//                                                }
//                                            } catch (Throwable ex) {
//                                                MainActivity.MyLog(ex);
//                                            }
//                                        });
//                                    }
//                                };
//                                thread.start();
//
//                                MainActivity.set_fullscreen();
//                            } catch (Throwable ex) {
//                                MainActivity.MyLog(ex);
//                            }
//                        }
//                    }
//            );
//            singleFilePickerDialog.show();
//        } else {
//            requestPermission();
//        }
    }

    public void test_document_file(){
        MainActivity.MyLogInfo(com.anggrayudi.storage.file.FileUtils.getDataDirectory(ctx).getAbsolutePath());
        MainActivity.MyLogInfo(com.anggrayudi.storage.file.FileUtils.getWritableDirs(ctx).toString());
        DocumentFile documentFile = com.anggrayudi.storage.file.DocumentFileCompat.fromPublicFolder(ctx, PublicDirectory.DOWNLOADS);
        if(documentFile == null){
            MainActivity.MyLogInfo("documentFile == null");
            return;
        }
        MainActivity.MyLogInfo(documentFile.toString());
    }

    private void myMethod() {
        Root root = storageManager.getRoot(StorageManagerCompat.DEF_MAIN_ROOT);
        if (root == null){
            MainActivity.MyLogInfo("root == null");
            return;
        }
        DocumentFile f = root.toRootDirectory(ctx);
        if (f == null){
            MainActivity.MyLogInfo("f == null");
            return;
        }
        DocumentFile subFolder = DocumentFile.fromSingleUri(ctx,Uri.parse("content://downloads/public_downloads"));
//        DocumentFile subFolder = DocumentFileCompat.peekSubFolder(f, "content://downloads/public_downloads");
//        DocumentFile subFolder = DocumentFileCompat.getSubFolder(f, "mysub");
        if (subFolder == null){
            MainActivity.MyLogInfo("subFolder == null");
            return; //directory doesn't exist yet
        }
//        DocumentFile myFile = DocumentFileCompat.peekFile(subFolder, "myfile.txt", "image/png");
        DocumentFile myFile = DocumentFileCompat.getFile(subFolder, "myfile.txt", "text/plain");
        if (myFile == null){
            MainActivity.MyLogInfo("myFile == null");
            return; //file doesn't exist yet
        }

        try {
            OutputStream os = getContentResolver().openOutputStream(myFile.getUri());
            if (os != null) {
                String s = "Hello";
                os.write(s.getBytes());
                os.close();
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }

//        try {
//            InputStream is = getContentResolver().openInputStream(myFile.getUri());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    private static final int REQUEST_ID = 1;

//    public void requestRole() {
//        RoleManager roleManager = (RoleManager)getSystemService(ROLE_SERVICE);
//        Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
////        ActivityCompat.startActivityForResult(intent, REQUEST_ID);
//        if(intent != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                Bundle options = new Bundle();
//                ActivityCompat.startActivityForResult(activity, intent, REQUEST_ID, options);
//            } else {
//                Bundle options = new Bundle();
//                ActivityCompat.startActivityForResult(activity, intent, REQUEST_ID, options);
//            }
//        }else{
//            MainActivity.MyLogInfo("Invalid Main Root.");
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == REQUEST_ID) {
                if (resultCode == android.app.Activity.RESULT_OK) {
                    // Your app is now the call screening app
                } else {
                    // Your app is not the call screening app
                }
            }

            // The ACTION_OPEN_DOCUMENT intent was sent with the request code OPEN_DIRECTORY_REQUEST_CODE.
            // If the request code seen here doesn't match, it's the response to some other intent,
            // and the below code shouldn't run at all.
            if (requestCode == OPEN_DIRECTORY_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    // The document selected by the user won't be returned in the intent.
                    // Instead, a URI to that document will be contained in the return intent
                    // provided to this method as a parameter.  Pull that uri using "resultData.getData()"
                    if (data != null && data.getData() != null) {
                        new CopyFileToAppDirTask().executeOnExecutor(THREAD_POOL_EXECUTOR, data.getData());
                    } else {
                        Log.d(TAG, "File uri not found {}");
                    }
                } else {
                    Log.d(TAG, "User cancelled file browsing {}");
                }
            }

            if(resultCode == RESULT_OK){
                try {
                    if (requestCode == ROOT_MAIN) {
                        root_main = storageManager.addRoot(this, StorageManagerCompat.DEF_MAIN_ROOT, data);
                        if (root_main != null) {
                            list_files(root_main, "onActivityResult - ROOT_MAIN");
                        }
                    }else if (requestCode == ROOT_SD) {
                        root_sd = storageManager.addRoot(this, StorageManagerCompat.DEF_SD_ROOT, data);
                        if (root_sd != null){
                            list_files(root_sd, "onActivityResult - ROOT_SD");
                        }
                    }else if (requestCode == ROOT_DATA) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            root_data = storageManager.addRoot(this, StorageManagerCompat.DEF_DATA_ROOT, data);
                            if (root_data != null) {
                                set_map_files(list_files(root_data, "onActivityResult - ROOT_DATA"));
                            }
                        }else{
                            root_data = storageManager.addRoot(this, StorageManagerCompat.DEF_DATA_ROOT, data);
                            if (root_data != null) {
                                set_map_files(list_files(root_data, "onActivityResult - ROOT_DATA"));
                            }
                        }
                    } else if (resultCode == MV_ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION){

                    }
                } catch (Throwable ex) {
                    MainActivity.MyLog(ex);
                }
            }

//        switch (requestCode) {
//            case FilePickerConst.REQUEST_CODE_PHOTO:
//                if (resultCode == Activity.RESULT_OK && data != null) {
//                    photoPaths = new ArrayList<>();
//                    photoPaths.addAll(data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
//                    for(int i=0;i<photoPaths.size();i++){
//                        Tab_Messenger.addImage(photoPaths.get(i).getPath());
//                    }
//                }
//                break;
//            case FilePickerConst.REQUEST_CODE_DOC:
//                if (resultCode == Activity.RESULT_OK && data != null) {
//                    docPaths = new ArrayList<>();
//                    docPaths.addAll(data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
//                }
//                break;
//        }
////        addThemToView(photoPaths, docPaths);

//        if (requestCode == Constants.REQ_UNICORN_FILE && resultCode == RESULT_OK) {
//            ArrayList<String> files = data.getStringArrayListExtra("filePaths");
//            for(String file : files){
////                Tab_Messenger.addImage(file);
//                Tab_Messenger.addMessage(file);
//            }
//        }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    ImageView iv_icon_preview = null;
    TextView tv_icon_name = null;
    List<File> files = null;
    String icon_name = null;
    EditText et_icon_color;
    public void showIconsDialog(Tab_Map.CustomInfoWindow infoWindow) {
        try {
            EditText et_icon_alpha, et_icon_scale, et_icon_heading;
            ImageView b_icon_color;

            LinearLayout iconsLayout = (LinearLayout) MainActivity.activity.getLayoutInflater().inflate(R.layout.icons_dialog, null);

            GridView simpleGridView = iconsLayout.findViewById(R.id.simpleGridView);
            iv_icon_preview = iconsLayout.findViewById(R.id.iv_icon_preview);
            tv_icon_name = iconsLayout.findViewById(R.id.tv_icon_name);
            et_icon_color = iconsLayout.findViewById(R.id.et_icon_color);
            et_icon_alpha = iconsLayout.findViewById(R.id.et_icon_alpha);
            b_icon_color = iconsLayout.findViewById(R.id.b_icon_color);
            et_icon_scale = iconsLayout.findViewById(R.id.et_icon_scale);
            et_icon_heading = iconsLayout.findViewById(R.id.et_icon_heading);

//            if (infoWindow.marker != null) {
//                int line_color = Color.YELLOW;
//                int fill_color = Color.YELLOW;
//                if (infoWindow.marker instanceof MyMarker) {
//                    MyMarker marker = (MyMarker) infoWindow.marker;
//
//                    line_color = polygon.getOutlinePaint().getColor();
//                    int line_alpha = polygon.getOutlinePaint().getAlpha();
//                    float line_width = polygon.getOutlinePaint().getStrokeWidth();
//                    fill_color = polygon.getFillPaint().getColor();
//                    int fill_alpha = polygon.getFillPaint().getAlpha();
//
//                    et_line_color.setText(mv_utils.colorToString(line_color));
//                    et_line_alpha.setText(String.valueOf(line_alpha));
//                    et_line_width.setText(String.valueOf(line_width));
//                    et_fill_color.setText(mv_utils.colorToString(fill_color));
//                    et_fill_alpha.setText(String.valueOf(fill_alpha));
//
//                    fill_layout.setVisibility(View.VISIBLE);
//                }
//            }

            String strDir = strIconsPath;
            File dir = new File(strDir);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    MainActivity.MyLogInfo(strDir + " not created...");
                }
            }
            File path = new File(strDir);
            File[] Fs = path.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return (file.getName().endsWith(".png"));
                }
            });
            if (Fs != null) files = new ArrayList<>(Arrays.asList(Fs));

            // Create an object of CustomAdapter and set Adapter to GirdView
            CustomAdapter customAdapter = new CustomAdapter(ctx, files, simpleGridView);
            simpleGridView.setAdapter(customAdapter);

//            for (int i = 0; i < files.size(); i++) {
//                if (files.get(i).getName().equals(infoWindow.kmlPlacemark.getExtendedData("style"))) {
//                    customAdapter.setSelectedPosition(i);
//                    Bitmap bmp = BitmapFactory.decodeFile(files.get(i).getAbsolutePath());
//                    iv_icon_preview.setImageBitmap(bmp);
//                    break;
//                }
//            }

            if((infoWindow.kmlPlacemark != null) && (infoWindow.kmlPlacemark.mStyle != null)){
                Style style = Tab_Map.kmlFavoritesDocument.getStyle(infoWindow.kmlPlacemark.mStyle);
                if(style != null){
                    icon_name = style.mIconStyle.mHref;
                    for (int i = 0; i < files.size(); i++) {
                        if (files.get(i).getName().equals(icon_name)) {
                            customAdapter.setSelectedPosition(i);
                            Bitmap bmp = BitmapFactory.decodeFile(files.get(i).getAbsolutePath());
                            iv_icon_preview.setImageBitmap(bmp);
                            tv_icon_name.setText(mv_utils.remove_extension(icon_name));
                            break;
                        }
                    }

                    int finalIcon_color = style.mIconStyle.mColor;
                    int finalIcon_alpha = Color.alpha(finalIcon_color);
                    float finalIcon_scale = style.mIconStyle.mScale;
                    float finalIcon_heading = style.mIconStyle.mHeading;
                    et_icon_color.setText(mv_utils.colorToString(finalIcon_color));
                    et_icon_alpha.setText(String.valueOf(finalIcon_alpha));
                    et_icon_scale.setText(String.valueOf(finalIcon_scale));
                    et_icon_heading.setText(String.valueOf(finalIcon_heading));
                    b_icon_color.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            int defaultColorR,defaultColorG,defaultColorB;
//                            defaultColorR = Color.red(finalIcon_color);
//                            defaultColorG = Color.green(finalIcon_color);
//                            defaultColorB = Color.blue(finalIcon_color);
//
//                            final ColorPicker cp = new ColorPicker(MainActivity.this, defaultColorR, defaultColorG, defaultColorB);
//                            cp.show();
//                            Button okColor = (Button)cp.findViewById(R.id.okColorButton);
//                            okColor.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    et_icon_color.setText(mv_utils.colorToString(cp.getColor()));
//                                    cp.dismiss();
//                                }
//                            });

                            SimpleColorDialog.build()
                                    .title(R.string.pick_a_color)
                                    .choiceMode(CustomListDialog.SINGLE_CHOICE)
                                    .colors(MainActivity.ctx, SimpleColorDialog.MATERIAL_COLOR_PALLET)
                                    .colorPreset(finalIcon_color)
                                    .allowCustom(true)
                                    .showOutline(SimpleColorDialog.AUTO)
                                    .theme(R.style.Base_Theme_AppCompat_Dialog)
                                    .show(MainActivity.activity, MY_MARK_COLOR_DIALOG_TAG);

//                            SimpleDialog.build()
//                                    .title("HTML styled text sample")
//                                    .msgHtml("<h1>Header 1</h1><h2>Header 2</h2>" +
//                                            "This is an HTML text with <b>bold</b>, " +
//                                            "<span style=\"color:red;\">colored</span> " +
//                                            "and <strike>other</strike> formatting.")
//                                    .show(MainActivity.activity, MY_COLOR_DIALOG_TAG);
                        }
                    });
                }
            }

            // implement setOnItemClickListener event on GridView
            simpleGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        // set an Intent to Another Activity
//                        Intent intent = new Intent(MainActivity.this, IconActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.putExtra("image", logos[position]); // put image data in Intent
//                        startActivity(intent); // start Intent
//                        files
//                        Bitmap bmp = BitmapFactory.decodeResource(MainActivity.activity.getResources(), logos[position]);
//                        Drawable icon = new BitmapDrawable(MainActivity.activity.getResources(), bmp);
//                        Drawable icon = AppCompatResources.getDrawable(MainActivity.ctx, logos[position]);
//                        marker.setIcon(icon);
//                        marker.setTitle(icon_name);

                        icon_name = files.get(position).getName();
                        Bitmap icon_bmp = BitmapFactory.decodeFile(files.get(position).getAbsolutePath());
                        iv_icon_preview.setImageBitmap(icon_bmp);
                        tv_icon_name.setText(mv_utils.remove_extension(icon_name));
//                        if(infoWindow.marker != null){
//                            Drawable icon = new BitmapDrawable(MainActivity.activity.getResources(), bmp);
//                            infoWindow.marker.setIcon(icon);
//                        }
//                        if(infoWindow.kmlPlacemark != null){
//                            infoWindow.kmlPlacemark.setExtendedData("style", icon_name);
//                        }
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                }
            });

            new AlertDialog.Builder(MainActivity.activity, R.style.CustomAlertDialog)
                    .setCancelable(false)
                    .setView(iconsLayout)
                    .setPositiveButton(MainActivity.ctx.getString(R.string.ok), (dialog, id) -> {
                        try {
                            if (infoWindow.kmlPlacemark != null) {
                                int icon_color = mv_utils.parseColor(et_icon_color.getText().toString());
                                int icon_alpha = mv_utils.parseInt(et_icon_alpha.getText().toString());
                                float icon_scale = mv_utils.parseFloat(et_icon_scale.getText().toString());
                                float icon_heading = mv_utils.parseFloat(et_icon_heading.getText().toString());
                                icon_color = mv_utils.adjustAlpha(icon_color, icon_alpha);
                                Tab_Map.update_placemark_style(infoWindow.marker, infoWindow.kmlPlacemark, icon_name, icon_color, icon_scale, icon_heading);
                            }

                            // Hide both the navigation bar and the status bar.
                            MainActivity.hide_keyboard(iconsLayout);
                            dialog.dismiss();
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    })
                    .setNegativeButton(MainActivity.ctx.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                MainActivity.hide_keyboard(iconsLayout);
                                dialog.cancel();
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        }

                    })
                    .create()
                    .show();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    EditText et_line_color;
    EditText et_fill_color;
    int line_color0 = Color.YELLOW;
    int fill_color0 = Color.RED;
    public void showGeometrySettingsDialog(Tab_Map.CustomInfoWindow infoWindow) {
        if (infoWindow.marker == null)  return;
        try {
            LinearLayout geometrySettingsLayout = (LinearLayout) MainActivity.activity.getLayoutInflater().inflate(R.layout.geometry_settings_dialog, null);

            et_line_color = geometrySettingsLayout.findViewById(R.id.et_line_color);
            EditText et_line_width = geometrySettingsLayout.findViewById(R.id.et_line_width);
            EditText et_line_alpha = geometrySettingsLayout.findViewById(R.id.et_line_alpha);
            et_fill_color = geometrySettingsLayout.findViewById(R.id.et_fill_color);
            EditText et_fill_alpha = geometrySettingsLayout.findViewById(R.id.et_fill_alpha);
            LinearLayout fill_layout = geometrySettingsLayout.findViewById(R.id.fill_layout);
            ImageView b_line_color = geometrySettingsLayout.findViewById(R.id.b_line_color);
            ImageView b_fill_color = geometrySettingsLayout.findViewById(R.id.b_fill_color);

            if(infoWindow.marker instanceof MyPolygon){
                MyPolygon polygon = (MyPolygon)infoWindow.marker;

//                line_color0 = polygon.getOutlinePaint().getColor();
                int line_alpha = polygon.getOutlinePaint().getAlpha();
                line_color0 = mv_utils.adjustAlpha(polygon.getOutlinePaint().getColor(),255);
                float line_width = polygon.getOutlinePaint().getStrokeWidth();
//                fill_color0 = polygon.getFillPaint().getColor();
                int fill_alpha = polygon.getFillPaint().getAlpha();
                fill_color0 = mv_utils.adjustAlpha(polygon.getFillPaint().getColor(),255);

                et_line_color.setText(mv_utils.colorToString(line_color0));
                et_line_alpha.setText(String.valueOf(line_alpha));
                et_line_width.setText(String.valueOf(line_width));
                et_fill_color.setText(mv_utils.colorToString(fill_color0));
                et_fill_alpha.setText(String.valueOf(fill_alpha));

                fill_layout.setVisibility(View.VISIBLE);
            }else if(infoWindow.marker instanceof MyPolyline){
                MyPolyline polyline = (MyPolyline)infoWindow.marker;

//                line_color0 = polyline.getOutlinePaint().getColor();
                int line_alpha = polyline.getOutlinePaint().getAlpha();
                line_color0 = mv_utils.adjustAlpha(polyline.getOutlinePaint().getColor(),255);
                float line_width = polyline.getOutlinePaint().getStrokeWidth();

                et_line_color.setText(mv_utils.colorToString(line_color0));
                et_line_alpha.setText(String.valueOf(line_alpha));
                et_line_width.setText(String.valueOf(line_width));

                fill_layout.setVisibility(View.GONE);
            }

            b_line_color.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                        int defaultColorR,defaultColorG,defaultColorB;
//                        defaultColorR = Color.red(`finalLine_color`);
//                        defaultColorG = Color.green(finalLine_color);
//                        defaultColorB = Color.blue(finalLine_color);
//
//                        final ColorPicker cp = new ColorPicker(MainActivity.this, defaultColorR, defaultColorG, defaultColorB);
//                        cp.show();
//                        Button okColor = (Button)cp.findViewById(R.id.okColorButton);
//                        okColor.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                et_line_color.setText(mv_utils.colorToString(cp.getColor()));
//                                cp.dismiss();
//                            }
//                        });

                    SimpleColorDialog.build()
                            .title(R.string.pick_a_color)
                            .choiceMode(CustomListDialog.SINGLE_CHOICE)
                            .colors(MainActivity.ctx, SimpleColorDialog.MATERIAL_COLOR_PALLET)
                            .allowCustom(true)
                            .showOutline(SimpleColorDialog.AUTO)
                            .theme(R.style.Base_Theme_AppCompat_Dialog)
                            .colorPreset(line_color0)
                            .show(MainActivity.activity, MY_LINE_COLOR_DIALOG_TAG);
                }
            });

            b_fill_color.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SimpleColorDialog.build()
                            .title(R.string.pick_a_color)
                            .choiceMode(CustomListDialog.SINGLE_CHOICE)
                            .colors(MainActivity.ctx, SimpleColorDialog.MATERIAL_COLOR_PALLET)
                            .allowCustom(true)
                            .showOutline(SimpleColorDialog.AUTO)
                            .theme(R.style.Base_Theme_AppCompat_Dialog)
                            .colorPreset(fill_color0)
                            .show(MainActivity.activity, MY_FILL_COLOR_DIALOG_TAG);
                }
            });

            new AlertDialog.Builder(MainActivity.activity, R.style.CustomAlertDialog)
                    .setCancelable(false)
                    .setView(geometrySettingsLayout)
                    .setPositiveButton(MainActivity.ctx.getString(R.string.ok), (dialog, id) -> {
                        try {
                            int line_color = mv_utils.parseColor(et_line_color.getText().toString());
                            int line_alpha = mv_utils.parseInt(et_line_alpha.getText().toString());
                            float line_width = mv_utils.parseFloat(et_line_width.getText().toString());
                            int fill_color = mv_utils.parseColor(et_fill_color.getText().toString());
                            int fill_alpha = mv_utils.parseInt(et_fill_alpha.getText().toString());
                            int line_color_alpha = mv_utils.adjustAlpha(line_color, line_alpha);
                            int fill_color_alpha = mv_utils.adjustAlpha(fill_color, fill_alpha);

                            if (infoWindow.kmlPlacemark != null) {
                                Style style;
                                style = Tab_Map.kmlFavoritesDocument.getStyle(infoWindow.kmlPlacemark.mStyle);
                                if(style == null){
                                    style = new Style(null, line_color_alpha, line_width, fill_color_alpha);
                                    infoWindow.kmlPlacemark.mStyle = Tab_Map.kmlFavoritesDocument.addStyle(style);
                                }else{
                                    style.mLineStyle.mColor = line_color_alpha;
                                    style.mLineStyle.mWidth = line_width;
                                    if(style.mPolyStyle != null) {
                                        style.mPolyStyle.mColor = fill_color_alpha;
                                    }
                                    Tab_Map.kmlFavoritesDocument.putStyle(infoWindow.kmlPlacemark.mStyle, style);
                                }
                                Tab_Map.kmlFavoritesDocument.saveAsKML(Tab_Map.favoritesFile);
                            }

                            if (infoWindow.marker != null) {
                                if(infoWindow.marker instanceof MyPolygon){
                                    MyPolygon polygon = (MyPolygon)infoWindow.marker;
                                    polygon.getOutlinePaint().setColor(line_color);
                                    polygon.getOutlinePaint().setAlpha(line_alpha);
                                    polygon.getOutlinePaint().setStrokeWidth(line_width);
                                    polygon.getFillPaint().setColor(fill_color);
                                    polygon.getFillPaint().setAlpha(fill_alpha);
                                }else if(infoWindow.marker instanceof MyPolyline){
                                    MyPolyline polyline = (MyPolyline)infoWindow.marker;
                                    polyline.getOutlinePaint().setColor(line_color);
                                    polyline.getOutlinePaint().setAlpha(line_alpha);
                                    polyline.getOutlinePaint().setStrokeWidth(line_width);
                                }
                            }

                            Tab_Map.map.invalidate();

                            // Hide both the navigation bar and the status bar.
                            MainActivity.hide_keyboard(geometrySettingsLayout);
                            dialog.dismiss();
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    })
                    .setNegativeButton(MainActivity.ctx.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                MainActivity.hide_keyboard(geometrySettingsLayout);
                                dialog.cancel();
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        }

                    })
                    .create()
                    .show();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    public void showMark1Dialog(Tab_Map.CustomInfoWindow infoWindow) {
        try {
            LinearLayout iconsLayout = (LinearLayout) MainActivity.activity.getLayoutInflater().inflate(R.layout.mark1_dialog, null);

            EditText et_name = iconsLayout.findViewById(R.id.et_name);
            EditText et_comment = iconsLayout.findViewById(R.id.et_comment);
            EditText et_details = iconsLayout.findViewById(R.id.et_details);
            if (infoWindow.kmlPlacemark != null) {
                et_name.setText(infoWindow.kmlPlacemark.mName);
                et_comment.setText(infoWindow.kmlPlacemark.getExtendedData("comment"));
                et_details.setText(infoWindow.kmlPlacemark.getExtendedData("details"));
            }

            new AlertDialog.Builder(MainActivity.activity, R.style.CustomAlertDialog)
                    .setCancelable(false)
                    .setView(iconsLayout)
                    .setPositiveButton(MainActivity.ctx.getString(R.string.ok), (dialog, id) -> {
                        try {
                            if (infoWindow.kmlPlacemark != null) {
                                infoWindow.kmlPlacemark.mName = et_name.getText().toString();
                                if(infoWindow.marker instanceof MyMarker)
                                {
                                    MyMarker marker0 = (MyMarker) infoWindow.marker; //the marker on which you click to open the bubble
                                    marker0.setTitle(et_name.getText().toString());
                                }
                                else
                                if(infoWindow.marker instanceof MyPolyline)
                                {
                                    MyPolyline polyline0 = (MyPolyline) infoWindow.marker;
                                    polyline0.setTitle(et_name.getText().toString());
                                }
                                else
                                if(infoWindow.marker instanceof MyPolygon)
                                {
                                    MyPolygon polygon0 = (MyPolygon) infoWindow.marker;
                                    polygon0.setTitle(et_name.getText().toString());
                                }

                                infoWindow.kmlPlacemark.setExtendedData("comment", et_comment.getText().toString());
                                infoWindow.kmlPlacemark.setExtendedData("details", et_details.getText().toString());
                            }

                            // Hide both the navigation bar and the status bar.
                            MainActivity.hide_keyboard(iconsLayout);
                            dialog.dismiss();
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    })
                    .setNeutralButton(MainActivity.ctx.getString(R.string.send), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                // save data
                                if (infoWindow.kmlPlacemark != null) {
                                    infoWindow.kmlPlacemark.mName = et_name.getText().toString();
                                    if(infoWindow.marker instanceof MyMarker)
                                    {
                                        MyMarker marker0 = (MyMarker) infoWindow.marker; //the marker on which you click to open the bubble
                                        marker0.setTitle(et_name.getText().toString());
                                    }
                                    else
                                    if(infoWindow.marker instanceof MyPolyline)
                                    {
                                        MyPolyline polyline0 = (MyPolyline) infoWindow.marker;
                                        polyline0.setTitle(et_name.getText().toString());
                                    }
                                    else
                                    if(infoWindow.marker instanceof MyPolygon)
                                    {
                                        MyPolygon polygon0 = (MyPolygon) infoWindow.marker;
                                        polygon0.setTitle(et_name.getText().toString());
                                    }

                                    infoWindow.kmlPlacemark.setExtendedData("comment", et_comment.getText().toString());
                                    infoWindow.kmlPlacemark.setExtendedData("details", et_details.getText().toString());
                                }

                                // send data
                                infoWindow.sendMark();
                                MainActivity.hide_keyboard(iconsLayout);
                                dialog.dismiss();
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        }
                    })
                    .setNegativeButton(MainActivity.ctx.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                MainActivity.hide_keyboard(iconsLayout);
                                dialog.cancel();
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        }

                    })
                    .create()
                    .show();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    StringsAdapter firepowersAdapter = null;
    ArrayList<String> firepowers = null;
    StringsAdapter soldiersAdapter = null;
    ArrayList<String> soldiers = null;
    StringsAdapter absencesAdapter = null;
    ArrayList<String> absences = null;
    public void showMark2Dialog(Tab_Map.CustomInfoWindow infoWindow) {
        try {
            LinearLayout iconsLayout = (LinearLayout) MainActivity.activity.getLayoutInflater().inflate(R.layout.mark2_dialog, null);

            EditText et_name = iconsLayout.findViewById(R.id.et_name);
            if (infoWindow.kmlPlacemark != null) {
                et_name.setText(infoWindow.kmlPlacemark.mName);
            }

            LinearLayout ll_firepowers = iconsLayout.findViewById(R.id.ll_firepowers);
            LinearLayout ll_soldiers = iconsLayout.findViewById(R.id.ll_soldiers);
            LinearLayout ll_counting = iconsLayout.findViewById(R.id.ll_counting);
            LinearLayout ll_absences = iconsLayout.findViewById(R.id.ll_absences);
            LinearLayout ll_details = iconsLayout.findViewById(R.id.ll_details);

            ll_firepowers.setVisibility(View.VISIBLE);
            ll_soldiers.setVisibility(View.GONE);
            ll_counting.setVisibility(View.GONE);
            ll_absences.setVisibility(View.GONE);
            ll_details.setVisibility(View.GONE);

            RadioButton rb_firepower = iconsLayout.findViewById(R.id.rb_firepower);
            rb_firepower.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_firepowers.setVisibility(View.VISIBLE);
                    ll_soldiers.setVisibility(View.GONE);
                    ll_counting.setVisibility(View.GONE);
                    ll_absences.setVisibility(View.GONE);
                    ll_details.setVisibility(View.GONE);
                }
            });

            RadioButton rb_soldiers = iconsLayout.findViewById(R.id.rb_soldiers);
            rb_soldiers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_firepowers.setVisibility(View.GONE);
                    ll_soldiers.setVisibility(View.VISIBLE);
                    ll_counting.setVisibility(View.GONE);
                    ll_absences.setVisibility(View.GONE);
                    ll_details.setVisibility(View.GONE);
                }
            });

            RadioButton rb_counting = iconsLayout.findViewById(R.id.rb_counting);
            rb_counting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_firepowers.setVisibility(View.GONE);
                    ll_soldiers.setVisibility(View.GONE);
                    ll_counting.setVisibility(View.VISIBLE);
                    ll_absences.setVisibility(View.GONE);
                    ll_details.setVisibility(View.GONE);
                }
            });

            EditText et_total_count = iconsLayout.findViewById(R.id.et_total_count);
            EditText et_officers_count = iconsLayout.findViewById(R.id.et_officers_count);
            EditText et_sub_officers_count = iconsLayout.findViewById(R.id.et_sub_officers_count);
            EditText et_singles_count = iconsLayout.findViewById(R.id.et_singles_count);
            EditText et_appendices_count = iconsLayout.findViewById(R.id.et_appendices_count);

            EditText et_vacation_count = iconsLayout.findViewById(R.id.et_vacation_count);
            EditText et_hospital_count = iconsLayout.findViewById(R.id.et_hospital_count);
            EditText et_recovery_count = iconsLayout.findViewById(R.id.et_recovery_count);
            EditText et_mission_count = iconsLayout.findViewById(R.id.et_mission_count);
            EditText et_leaving_count = iconsLayout.findViewById(R.id.et_leaving_count);
            EditText et_prison_count = iconsLayout.findViewById(R.id.et_prison_count);
            EditText et_unjustified_absence_count = iconsLayout.findViewById(R.id.et_unjustified_absence_count);
            EditText et_total_absence_count = iconsLayout.findViewById(R.id.et_total_absence_count);
            EditText et_under_gun_count = iconsLayout.findViewById(R.id.et_under_gun_count);

            RadioButton rb_absences = iconsLayout.findViewById(R.id.rb_absences);
            rb_absences.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_firepowers.setVisibility(View.GONE);
                    ll_soldiers.setVisibility(View.GONE);
                    ll_counting.setVisibility(View.GONE);
                    ll_absences.setVisibility(View.VISIBLE);
                    ll_details.setVisibility(View.GONE);
                }
            });

            RadioButton rb_details = iconsLayout.findViewById(R.id.rb_details);
            rb_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_firepowers.setVisibility(View.GONE);
                    ll_soldiers.setVisibility(View.GONE);
                    ll_counting.setVisibility(View.GONE);
                    ll_absences.setVisibility(View.GONE);
                    ll_details.setVisibility(View.VISIBLE);
                }
            });

            EditText et_firepower_name = iconsLayout.findViewById(R.id.et_firepower_name);
            Button b_add_firepower = iconsLayout.findViewById(R.id.b_add_firepower);
            b_add_firepower.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(firepowersAdapter != null)  firepowersAdapter.add(et_firepower_name.getText().toString());
                    et_firepower_name.setText("");
                }
            });

            EditText et_soldier_name = iconsLayout.findViewById(R.id.et_soldier_name);
            Button b_add_soldier = iconsLayout.findViewById(R.id.b_add_soldier);
            b_add_soldier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(soldiersAdapter != null)  soldiersAdapter.add(et_soldier_name.getText().toString());
                    et_soldier_name.setText("");
                }
            });

            EditText et_absence_name = iconsLayout.findViewById(R.id.et_absence_name);
            Button b_add_absence = iconsLayout.findViewById(R.id.b_add_absence);
            b_add_absence.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(absencesAdapter != null)  absencesAdapter.add(et_absence_name.getText().toString());
                    et_absence_name.setText("");
                }
            });

            EditText et_details = iconsLayout.findViewById(R.id.et_details);

            ListView lv_firepowers = iconsLayout.findViewById(R.id.lv_firepowers);
            ListView lv_soldiers = iconsLayout.findViewById(R.id.lv_soldiers);
            ListView lv_absences = iconsLayout.findViewById(R.id.lv_absences);
            if (infoWindow.kmlPlacemark != null) {
                String strFirepowers = infoWindow.kmlPlacemark.getExtendedData("firepowers");
                if(strFirepowers != null){
                    firepowers = new ArrayList<String>(Arrays.asList(strFirepowers.split(",")));
                }else{
                    firepowers = new ArrayList<String>();
                }
                firepowersAdapter = new StringsAdapter(MainActivity.ctx, firepowers);
                lv_firepowers.setAdapter(firepowersAdapter);

                String strSoldiers = infoWindow.kmlPlacemark.getExtendedData("soldiers");
                if(strSoldiers != null){
                    soldiers = new ArrayList<String>(Arrays.asList(strSoldiers.split(",")));
                }else{
                    soldiers = new ArrayList<String>();
                }
                soldiersAdapter = new StringsAdapter(MainActivity.ctx, soldiers);
                lv_soldiers.setAdapter(soldiersAdapter);

//                et_total_count.setText(infoWindow.kmlPlacemark.getExtendedData("total_count"));
                et_officers_count.setText(infoWindow.kmlPlacemark.getExtendedData("officers_count"));
                et_sub_officers_count.setText(infoWindow.kmlPlacemark.getExtendedData("sub_officers_count"));
                et_singles_count.setText(infoWindow.kmlPlacemark.getExtendedData("singles_count"));
                et_appendices_count.setText(infoWindow.kmlPlacemark.getExtendedData("appendices_count"));

                et_vacation_count.setText(infoWindow.kmlPlacemark.getExtendedData("vacation_count"));
                et_hospital_count.setText(infoWindow.kmlPlacemark.getExtendedData("hospital_count"));
                et_recovery_count.setText(infoWindow.kmlPlacemark.getExtendedData("recovery_count"));
                et_mission_count.setText(infoWindow.kmlPlacemark.getExtendedData("mission_count"));
                et_leaving_count.setText(infoWindow.kmlPlacemark.getExtendedData("leaving_count"));
                et_prison_count.setText(infoWindow.kmlPlacemark.getExtendedData("prison_count"));
                et_unjustified_absence_count.setText(infoWindow.kmlPlacemark.getExtendedData("unjustified_absence_count"));
//                et_total_absence_count.setText(infoWindow.kmlPlacemark.getExtendedData("total_absence_count"));
//                et_under_gun_count.setText(infoWindow.kmlPlacemark.getExtendedData("under_gun_count"));

                TextWatcher attenencesTextWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        int attendances_sum = mv_utils.parseInt(et_officers_count.getText().toString())+
                                mv_utils.parseInt(et_sub_officers_count.getText().toString())+
                                mv_utils.parseInt(et_singles_count.getText().toString())+
                                mv_utils.parseInt(et_appendices_count.getText().toString());
                        et_total_count.setText(String.valueOf(attendances_sum));
                        int under_gun_count = mv_utils.parseInt(et_total_count.getText().toString()) -
                                mv_utils.parseInt(et_total_absence_count.getText().toString());
                        et_under_gun_count.setText(String.valueOf(under_gun_count));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                };
                et_officers_count.addTextChangedListener(attenencesTextWatcher);
                et_sub_officers_count.addTextChangedListener(attenencesTextWatcher);
                et_singles_count.addTextChangedListener(attenencesTextWatcher);
                et_appendices_count.addTextChangedListener(attenencesTextWatcher);

                int attendances_sum = mv_utils.parseInt(et_officers_count.getText().toString())+
                        mv_utils.parseInt(et_sub_officers_count.getText().toString())+
                        mv_utils.parseInt(et_singles_count.getText().toString())+
                        mv_utils.parseInt(et_appendices_count.getText().toString());
                et_total_count.setText(String.valueOf(attendances_sum));

                TextWatcher absencesTextWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        int absences_sum = mv_utils.parseInt(et_vacation_count.getText().toString())+
                                mv_utils.parseInt(et_hospital_count.getText().toString())+
                                mv_utils.parseInt(et_recovery_count.getText().toString())+
                                mv_utils.parseInt(et_mission_count.getText().toString())+
                                mv_utils.parseInt(et_leaving_count.getText().toString())+
                                mv_utils.parseInt(et_prison_count.getText().toString())+
                                mv_utils.parseInt(et_unjustified_absence_count.getText().toString());
                        et_total_absence_count.setText(String.valueOf(absences_sum));
                        int under_gun_count = mv_utils.parseInt(et_total_count.getText().toString()) -
                                mv_utils.parseInt(et_total_absence_count.getText().toString());
                        et_under_gun_count.setText(String.valueOf(under_gun_count));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                };
                et_vacation_count.addTextChangedListener(absencesTextWatcher);
                et_hospital_count.addTextChangedListener(absencesTextWatcher);
                et_recovery_count.addTextChangedListener(absencesTextWatcher);
                et_mission_count.addTextChangedListener(absencesTextWatcher);
                et_leaving_count.addTextChangedListener(absencesTextWatcher);
                et_prison_count.addTextChangedListener(absencesTextWatcher);
                et_unjustified_absence_count.addTextChangedListener(absencesTextWatcher);

                int absences_sum = mv_utils.parseInt(et_vacation_count.getText().toString())+
                        mv_utils.parseInt(et_hospital_count.getText().toString())+
                        mv_utils.parseInt(et_recovery_count.getText().toString())+
                        mv_utils.parseInt(et_mission_count.getText().toString())+
                        mv_utils.parseInt(et_leaving_count.getText().toString())+
                        mv_utils.parseInt(et_prison_count.getText().toString())+
                        mv_utils.parseInt(et_unjustified_absence_count.getText().toString());
                et_total_absence_count.setText(String.valueOf(absences_sum));

                int under_gun_count = mv_utils.parseInt(et_total_count.getText().toString()) -
                        mv_utils.parseInt(et_total_absence_count.getText().toString());
                et_under_gun_count.setText(String.valueOf(under_gun_count));

                String strAbsences = infoWindow.kmlPlacemark.getExtendedData("absences");
                if(strAbsences != null){
                    absences = new ArrayList<String>(Arrays.asList(strAbsences.split(",")));
                }else{
                    absences = new ArrayList<String>();
                }
                absencesAdapter = new StringsAdapter(MainActivity.ctx, absences);
                lv_absences.setAdapter(absencesAdapter);

                et_details.setText(infoWindow.kmlPlacemark.getExtendedData("details"));
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.activity, R.style.CustomAlertDialog)
                    .setCancelable(false)
                    .setView(iconsLayout)
                    .setPositiveButton(MainActivity.ctx.getString(R.string.ok), (dialog, id) -> {
                        try {
                            if (infoWindow.kmlPlacemark != null) {
                                infoWindow.kmlPlacemark.mName = et_name.getText().toString();
                                if(infoWindow.marker instanceof MyMarker)
                                {
                                    MyMarker marker0 = (MyMarker) infoWindow.marker; //the marker on which you click to open the bubble
                                    marker0.setTitle(et_name.getText().toString());
                                }
                                else
                                if(infoWindow.marker instanceof MyPolyline)
                                {
                                    MyPolyline polyline0 = (MyPolyline) infoWindow.marker;
                                    polyline0.setTitle(et_name.getText().toString());
                                }
                                else
                                if(infoWindow.marker instanceof MyPolygon)
                                {
                                    MyPolygon polygon0 = (MyPolygon) infoWindow.marker;
                                    polygon0.setTitle(et_name.getText().toString());
                                }

                                StringBuilder strFirepowers = new StringBuilder();
                                for(int i=0;i<firepowersAdapter.getCount();i++){
                                    if(i == 0)
                                        strFirepowers.append(firepowersAdapter.getItem(i));
                                    else
                                        strFirepowers.append(",").append(firepowersAdapter.getItem(i));
                                }
                                infoWindow.kmlPlacemark.setExtendedData("firepowers", strFirepowers.toString());

                                StringBuilder strSoldiers = new StringBuilder();
                                for(int i=0;i<soldiersAdapter.getCount();i++){
                                    if(i == 0)
                                        strSoldiers.append(soldiersAdapter.getItem(i));
                                    else
                                        strSoldiers.append(",").append(soldiersAdapter.getItem(i));
                                }
                                infoWindow.kmlPlacemark.setExtendedData("soldiers", strSoldiers.toString());

                                infoWindow.kmlPlacemark.setExtendedData("total_count", et_total_count.getText().toString());
                                infoWindow.kmlPlacemark.setExtendedData("officers_count", et_officers_count.getText().toString());
                                infoWindow.kmlPlacemark.setExtendedData("sub_officers_count", et_sub_officers_count.getText().toString());
                                infoWindow.kmlPlacemark.setExtendedData("singles_count", et_singles_count.getText().toString());
                                infoWindow.kmlPlacemark.setExtendedData("appendices_count", et_appendices_count.getText().toString());

                                infoWindow.kmlPlacemark.setExtendedData("vacation_count", et_vacation_count.getText().toString());
                                infoWindow.kmlPlacemark.setExtendedData("hospital_count", et_hospital_count.getText().toString());
                                infoWindow.kmlPlacemark.setExtendedData("recovery_count", et_recovery_count.getText().toString());
                                infoWindow.kmlPlacemark.setExtendedData("mission_count", et_mission_count.getText().toString());
                                infoWindow.kmlPlacemark.setExtendedData("leaving_count", et_leaving_count.getText().toString());
                                infoWindow.kmlPlacemark.setExtendedData("prison_count", et_prison_count.getText().toString());
                                infoWindow.kmlPlacemark.setExtendedData("unjustified_absence_count", et_unjustified_absence_count.getText().toString());
                                infoWindow.kmlPlacemark.setExtendedData("total_absence_count", et_total_absence_count.getText().toString());
                                infoWindow.kmlPlacemark.setExtendedData("under_gun_count", et_under_gun_count.getText().toString());

                                StringBuilder strAbsences = new StringBuilder();
                                for(int i=0;i<absencesAdapter.getCount();i++){
                                    if(i == 0)
                                        strAbsences.append(absencesAdapter.getItem(i));
                                    else
                                        strAbsences.append(",").append(absencesAdapter.getItem(i));
                                }
                                infoWindow.kmlPlacemark.setExtendedData("absences", strAbsences.toString());

                                infoWindow.kmlPlacemark.setExtendedData("details", et_details.getText().toString());
                            }

                            // Hide both the navigation bar and the status bar.
                            MainActivity.hide_keyboard(iconsLayout);
                            dialog.dismiss();
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    })
                    .setNeutralButton(MainActivity.ctx.getString(R.string.send), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                // save data
                                if (infoWindow.kmlPlacemark != null) {
                                    infoWindow.kmlPlacemark.mName = et_name.getText().toString();
                                    if(infoWindow.marker instanceof MyMarker)
                                    {
                                        MyMarker marker0 = (MyMarker) infoWindow.marker; //the marker on which you click to open the bubble
                                        marker0.setTitle(et_name.getText().toString());
                                    }
                                    else
                                    if(infoWindow.marker instanceof MyPolyline)
                                    {
                                        MyPolyline polyline0 = (MyPolyline) infoWindow.marker;
                                        polyline0.setTitle(et_name.getText().toString());
                                    }
                                    else
                                    if(infoWindow.marker instanceof MyPolygon)
                                    {
                                        MyPolygon polygon0 = (MyPolygon) infoWindow.marker;
                                        polygon0.setTitle(et_name.getText().toString());
                                    }

                                    StringBuilder strFirepowers = new StringBuilder();
                                    for(int i=0;i<firepowersAdapter.getCount();i++){
                                        if(i == 0)
                                            strFirepowers.append(firepowersAdapter.getItem(i));
                                        else
                                            strFirepowers.append(",").append(firepowersAdapter.getItem(i));
                                    }
                                    infoWindow.kmlPlacemark.setExtendedData("firepowers", strFirepowers.toString());

                                    StringBuilder strSoldiers = new StringBuilder();
                                    for(int i=0;i<soldiersAdapter.getCount();i++){
                                        if(i == 0)
                                            strSoldiers.append(soldiersAdapter.getItem(i));
                                        else
                                            strSoldiers.append(",").append(soldiersAdapter.getItem(i));
                                    }
                                    infoWindow.kmlPlacemark.setExtendedData("soldiers", strSoldiers.toString());

                                    infoWindow.kmlPlacemark.setExtendedData("total_count", et_total_count.getText().toString());
                                    infoWindow.kmlPlacemark.setExtendedData("officers_count", et_officers_count.getText().toString());
                                    infoWindow.kmlPlacemark.setExtendedData("sub_officers_count", et_sub_officers_count.getText().toString());
                                    infoWindow.kmlPlacemark.setExtendedData("singles_count", et_singles_count.getText().toString());
                                    infoWindow.kmlPlacemark.setExtendedData("appendices_count", et_appendices_count.getText().toString());

                                    infoWindow.kmlPlacemark.setExtendedData("vacation_count", et_vacation_count.getText().toString());
                                    infoWindow.kmlPlacemark.setExtendedData("hospital_count", et_hospital_count.getText().toString());
                                    infoWindow.kmlPlacemark.setExtendedData("recovery_count", et_recovery_count.getText().toString());
                                    infoWindow.kmlPlacemark.setExtendedData("mission_count", et_mission_count.getText().toString());
                                    infoWindow.kmlPlacemark.setExtendedData("leaving_count", et_leaving_count.getText().toString());
                                    infoWindow.kmlPlacemark.setExtendedData("prison_count", et_prison_count.getText().toString());
                                    infoWindow.kmlPlacemark.setExtendedData("unjustified_absence_count", et_unjustified_absence_count.getText().toString());
                                    infoWindow.kmlPlacemark.setExtendedData("total_absence_count", et_total_absence_count.getText().toString());
                                    infoWindow.kmlPlacemark.setExtendedData("under_gun_count", et_under_gun_count.getText().toString());

                                    StringBuilder strAbsences = new StringBuilder();
                                    for(int i=0;i<absencesAdapter.getCount();i++){
                                        if(i == 0)
                                            strAbsences.append(absencesAdapter.getItem(i));
                                        else
                                            strAbsences.append(",").append(absencesAdapter.getItem(i));
                                    }
                                    infoWindow.kmlPlacemark.setExtendedData("absences", strAbsences.toString());

                                    infoWindow.kmlPlacemark.setExtendedData("details", et_details.getText().toString());
                                }

                                // send data
                                infoWindow.sendMark();
                                MainActivity.hide_keyboard(iconsLayout);
                                dialog.dismiss();
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        }
                    })
                    .setNegativeButton(MainActivity.ctx.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                MainActivity.hide_keyboard(iconsLayout);
                                dialog.cancel();
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        }

                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            Window window = alertDialog.getWindow();
            if(window != null) {
                Display display = MainActivity.activity.getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getRealSize(size);
                window.setLayout(size.x, size.y);
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    StringsAdapter membersAdapter = null;
    ArrayList<String> members = null;
    public void showMark3Dialog(Tab_Map.CustomInfoWindow infoWindow) {
        try {
            LinearLayout iconsLayout = (LinearLayout) MainActivity.activity.getLayoutInflater().inflate(R.layout.mark3_dialog, null);

            EditText et_name = iconsLayout.findViewById(R.id.et_name);
            if (infoWindow.kmlPlacemark != null) {
                et_name.setText(infoWindow.kmlPlacemark.mName);
            }

            LinearLayout ll_weapon_type = iconsLayout.findViewById(R.id.ll_weapon_type);
            LinearLayout ll_details = iconsLayout.findViewById(R.id.ll_details);
            LinearLayout ll_members = iconsLayout.findViewById(R.id.ll_members);

            ll_weapon_type.setVisibility(View.VISIBLE);
            ll_details.setVisibility(View.GONE);
            ll_members.setVisibility(View.GONE);

            RadioButton rb_weapon_type = iconsLayout.findViewById(R.id.rb_weapon_type);
            rb_weapon_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_weapon_type.setVisibility(View.VISIBLE);
                    ll_details.setVisibility(View.GONE);
                    ll_members.setVisibility(View.GONE);
                }
            });

            RadioButton rb_details = iconsLayout.findViewById(R.id.rb_details);
            rb_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_weapon_type.setVisibility(View.GONE);
                    ll_details.setVisibility(View.VISIBLE);
                    ll_members.setVisibility(View.GONE);
                }
            });

            RadioButton rb_members = iconsLayout.findViewById(R.id.rb_members);
            rb_members.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_weapon_type.setVisibility(View.GONE);
                    ll_details.setVisibility(View.GONE);
                    ll_members.setVisibility(View.VISIBLE);
                }
            });

            EditText et_weapon_type = iconsLayout.findViewById(R.id.et_weapon_type);
            ListView lv_members = iconsLayout.findViewById(R.id.lv_members);

            EditText et_details = iconsLayout.findViewById(R.id.et_details);
            EditText et_member_name = iconsLayout.findViewById(R.id.et_member_name);
            Button b_add_member = iconsLayout.findViewById(R.id.b_add_member);
            b_add_member.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(membersAdapter != null)  membersAdapter.add(et_member_name.getText().toString());
                    et_member_name.setText("");
                }
            });

            if (infoWindow.kmlPlacemark != null) {
                et_weapon_type.setText(infoWindow.kmlPlacemark.getExtendedData("weapon_type"));
                et_details.setText(infoWindow.kmlPlacemark.getExtendedData("details"));
                String strMembers = infoWindow.kmlPlacemark.getExtendedData("members");
                if(strMembers != null){
                    members = new ArrayList<String>(Arrays.asList(strMembers.split(",")));
                }else{
                    members = new ArrayList<String>();
                }
//                ArrayList members = new ArrayList<String>(Arrays.asList("111,222,333,444,555,666".split(",")));
                membersAdapter = new StringsAdapter(MainActivity.ctx, members);
                lv_members.setAdapter(membersAdapter);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.activity, R.style.CustomAlertDialog)
                    .setCancelable(false)
                    .setView(iconsLayout)
                    .setPositiveButton(MainActivity.ctx.getString(R.string.ok), (dialog, id) -> {
                        try {
                            if (infoWindow.kmlPlacemark != null) {
                                infoWindow.kmlPlacemark.mName = et_name.getText().toString();
                                if(infoWindow.marker instanceof MyMarker)
                                {
                                    MyMarker marker0 = (MyMarker) infoWindow.marker; //the marker on which you click to open the bubble
                                    marker0.setTitle(et_name.getText().toString());
                                }
                                else
                                if(infoWindow.marker instanceof MyPolyline)
                                {
                                    MyPolyline polyline0 = (MyPolyline) infoWindow.marker;
                                    polyline0.setTitle(et_name.getText().toString());
                                }
                                else
                                if(infoWindow.marker instanceof MyPolygon)
                                {
                                    MyPolygon polygon0 = (MyPolygon) infoWindow.marker;
                                    polygon0.setTitle(et_name.getText().toString());
                                }

                                infoWindow.kmlPlacemark.setExtendedData("weapon_type", et_weapon_type.getText().toString());
                                infoWindow.kmlPlacemark.setExtendedData("details", et_details.getText().toString());
                                StringBuilder strMembers = new StringBuilder();
                                for(int i=0;i<membersAdapter.getCount();i++){
                                    if(i == 0)
                                        strMembers.append(membersAdapter.getItem(i));
                                    else
                                        strMembers.append(",").append(membersAdapter.getItem(i));
                                }
                                infoWindow.kmlPlacemark.setExtendedData("members", strMembers.toString());
                            }

                            // Hide both the navigation bar and the status bar.
                            MainActivity.hide_keyboard(iconsLayout);
                            dialog.dismiss();
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    })
                    .setNeutralButton(MainActivity.ctx.getString(R.string.send), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                // save data
                                if (infoWindow.kmlPlacemark != null) {
                                    infoWindow.kmlPlacemark.mName = et_name.getText().toString();
                                    if(infoWindow.marker instanceof MyMarker)
                                    {
                                        MyMarker marker0 = (MyMarker) infoWindow.marker; //the marker on which you click to open the bubble
                                        marker0.setTitle(et_name.getText().toString());
                                    }
                                    else
                                    if(infoWindow.marker instanceof MyPolyline)
                                    {
                                        MyPolyline polyline0 = (MyPolyline) infoWindow.marker;
                                        polyline0.setTitle(et_name.getText().toString());
                                    }
                                    else
                                    if(infoWindow.marker instanceof MyPolygon)
                                    {
                                        MyPolygon polygon0 = (MyPolygon) infoWindow.marker;
                                        polygon0.setTitle(et_name.getText().toString());
                                    }

                                    infoWindow.kmlPlacemark.setExtendedData("weapon_type", et_weapon_type.getText().toString());
                                    infoWindow.kmlPlacemark.setExtendedData("details", et_details.getText().toString());
                                    StringBuilder strMembers = new StringBuilder();
                                    for(int i=0;i<membersAdapter.getCount();i++){
                                        if(i == 0)
                                            strMembers.append(membersAdapter.getItem(i));
                                        else
                                            strMembers.append(",").append(membersAdapter.getItem(i));
                                    }
                                    infoWindow.kmlPlacemark.setExtendedData("members", strMembers.toString());
                                }

                                // send data
                                infoWindow.sendMark();
                                MainActivity.hide_keyboard(iconsLayout);
                                dialog.dismiss();
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        }
                    })
                    .setNegativeButton(MainActivity.ctx.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                MainActivity.hide_keyboard(iconsLayout);
                                dialog.cancel();
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        }

                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            Window window = alertDialog.getWindow();
            if(window != null) {
                Display display = MainActivity.activity.getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getRealSize(size);
                window.setLayout(size.x, size.y);
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }
    static public void showFileViewDialog(String filename){
        try
        {
            LinearLayout view = (LinearLayout)MainActivity.activity.getLayoutInflater().inflate(R.layout.layout_file_viewer, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.activity, R.style.CustomAlertDialog)
                    .setCancelable(false)
                    .setView(view);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            Window window = alertDialog.getWindow();
            if(window != null) {
                Display display = MainActivity.activity.getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getRealSize(size);
                window.setLayout(size.x, size.y);
            }

            ShapeableImageView siv_close =  view.findViewById(R.id.siv_close);
            siv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.hide_keyboard(view);
                    alertDialog.dismiss();
                }
            });

            ShapeableImageView siv_open =  view.findViewById(R.id.siv_open);
            siv_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentHelper.openFile(filename);
                    MainActivity.hide_keyboard(view);
                }
            });

            TextView tv_filename =  view.findViewById(R.id.tv_filename);
            tv_filename.setText(filename);

            TextView tv_text =  view.findViewById(R.id.tv_text);
            tv_text.setVisibility(View.GONE);
            ImageView iv_image =  view.findViewById(R.id.iv_image);
            iv_image.setVisibility(View.GONE);
            VideoView vv_video =  view.findViewById(R.id.vv_video);
            vv_video.setVisibility(View.GONE);

            LinearLayout ll_video =  view.findViewById(R.id.ll_video);
            ll_video.setVisibility(View.GONE);

            ShapeableImageView siv_play =  view.findViewById(R.id.siv_play);
            siv_play.setImageResource(R.drawable.pause_icon);
            siv_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(vv_video.isPlaying()){
                        vv_video.pause();
                        siv_play.setImageResource(R.drawable.play_icon);
                    }else{
                        vv_video.start();
                        siv_play.setImageResource(R.drawable.pause_icon);
                    }
                }
            });

            vv_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    siv_play.setImageResource(R.drawable.play_icon);
                }
            });

            ShapeableImageView b_import_ips =  view.findViewById(R.id.b_import_ips);
            b_import_ips.setVisibility(View.GONE);
            b_import_ips.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        new AlertDialog.Builder(MainActivity.activity)
                                .setCancelable(false)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Import IPs")
                                .setMessage("Are you sure you want to import IPs?")
                                .setPositiveButton(R.string.yes_message, (dialog, which) -> {
                                    Tab_Messenger.import_ip_list(filename);
                                    MainActivity.hide_keyboard(null);
                                })
                                .setNegativeButton(R.string.no_message, (dialog, which) -> {
                                    MainActivity.hide_keyboard(null);
                                })
                                .show();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                }
            });

            String ext = FileHelper.fileExt(filename);
            if(ext != null) {
                if (ext.contains("bmp") || ext.contains("png") || ext.contains("jpg") || ext.contains("jpeg") || ext.contains("gif") || ext.contains("webp") || ext.contains("heic") || ext.contains("heif")) {
                    String exif = MainActivity.getExif(filename);
                    if(exif != null){
                        tv_text.setVisibility(View.VISIBLE);

                        String[] lines = exif.split("\n");
                        String line;
                        int idx;
                        double lon = 0.0,lat = 0.0;
                        for(int i=0;i<lines.length;i++) {
                            line = lines[i].trim();
                            idx = line.indexOf("GEO:");
                            if (idx >= 0) {
                                line = line.replace("GEO:", "").trim();
                                String[] coordinates = line.split(",");
                                lon = mv_utils.parseDouble(coordinates[0].trim());
                                lat = mv_utils.parseDouble(coordinates[1].trim());
                                break;
                            }
                        }
                        exif += "lon: "+String.valueOf(lon)+"\n";
                        exif += "lat: "+String.valueOf(lat)+"\n";
                        tv_text.setText(exif);
                    }

                    iv_image.setVisibility(View.VISIBLE);
                    iv_image.setImageURI(Uri.fromFile(new File(filename)));
                    iv_image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)); // value is in pixels
                }
                else
                if (ext.contains("3gp") || ext.contains("mp4") || ext.contains("mkv") || ext.contains("ts") || ext.contains("webm")) {
                    vv_video.setVisibility(View.VISIBLE);
                    ll_video.setVisibility(View.VISIBLE);
                    vv_video.setVideoURI(Uri.fromFile(new File(filename)));
                    vv_video.setLayoutParams(new LinearLayout.LayoutParams(256, 256)); // value is in pixels

                    vv_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            vv_video.setTag(mp);
                            vv_video.setLayoutParams(new LinearLayout.LayoutParams(mp.getVideoWidth(),mp.getVideoHeight())); // value is in pixels
                        }
                    });

                    vv_video.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                MediaPlayer mMediaPlayer = (MediaPlayer) v.getTag();
                                if (mMediaPlayer != null) {
                                    if (mMediaPlayer.isPlaying()) {
                                        mMediaPlayer.pause();
                                        siv_play.setImageResource(R.drawable.play_icon);
                                    }else {
                                        mMediaPlayer.start();
                                        siv_play.setImageResource(R.drawable.pause_icon);
                                    }
                                }
                                vv_video.invalidate();
                            }
                            return false;
                        }
                    });

                    vv_video.start();
                }
                else
                if (ext.contains("mp3") || ext.contains("m4a") || ext.contains("aac") || ext.contains("amr") || ext.contains("flac") || ext.contains("mid") || ext.contains("xmf") || ext.contains("mxmf") || ext.contains("rtttl") || ext.contains("rtx") || ext.contains("ota") || ext.contains("imy") || ext.contains("ogg") || ext.contains("wav")){
                    vv_video.setVisibility(View.VISIBLE);
                    ll_video.setVisibility(View.VISIBLE);
                    vv_video.setVideoURI(Uri.fromFile(new File(filename)));
                    vv_video.setLayoutParams(new LinearLayout.LayoutParams(128, 128)); // value is in pixels
                    vv_video.setBackgroundResource(R.drawable.crystal_audio_white);

                    vv_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            vv_video.setTag(mp);
                        }
                    });

                    vv_video.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                MediaPlayer mMediaPlayer = (MediaPlayer) v.getTag();
                                if (mMediaPlayer != null) {
                                    if (mMediaPlayer.isPlaying()) {
                                        mMediaPlayer.pause();
                                        siv_play.setImageResource(R.drawable.play_icon);
                                    }else {
                                        mMediaPlayer.start();
                                        siv_play.setImageResource(R.drawable.pause_icon);
                                    }
                                }
                            }
                            return false;
                        }
                    });

                    vv_video.start();
                }
                else
                if (ext.contains("loc")){
                    tv_text.setVisibility(View.VISIBLE);
                    tv_text.setText(FileHelper.readTextFile(filename, ""));
                }
                else
                if (ext.contains("kml")){
                    tv_text.setVisibility(View.VISIBLE);
                    tv_text.setText(FileHelper.readTextFile(filename, ""));
                }
                else
                if (ext.contains("lin")) {
                    tv_text.setVisibility(View.VISIBLE);
                    tv_text.setText(FileHelper.readTextFile(filename, ""));
                }
                else
                if (ext.contains("csv")) {
                    if (filename.contains(MainActivity.strIPsListName)) {
                        b_import_ips.setVisibility(View.VISIBLE);
                    }
                    tv_text.setVisibility(View.VISIBLE);
                    tv_text.setText(FileHelper.readTextFile(filename, ""));
                }
                else
                if (ext.contains("sta")) {
                    tv_text.setVisibility(View.VISIBLE);
                    tv_text.setText(FileHelper.readTextFile(filename, ""));
                }
                else {
//                    tv_text.setVisibility(View.VISIBLE);
//                    tv_text.setText(FileHelper.readTextFile(filename, ""));

                    MainActivity.hide_keyboard(view);
                    alertDialog.dismiss();
                    IntentHelper.openFile(filename);
                }
            }
            else {
//                tv_text.setVisibility(View.VISIBLE);
//                tv_text.setText(FileHelper.readTextFile(filename, ""));

                MainActivity.hide_keyboard(view);
                alertDialog.dismiss();
                IntentHelper.openFile(filename);
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    static public void set_view_visibility(View view, boolean visibility){
        if(visibility)
            view.setVisibility(View.VISIBLE);
        else
            view.setVisibility(View.GONE);
    }

//    static public String get_sim_phone_number() {
//        TelephonyManager teleManger = (TelephonyManager) MainActivity.ctx.getSystemService(Context.TELEPHONY_SERVICE);
//        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE}, 1);
//            return "";
//        }
//        String getSimNumber = teleManger.getLine1Number();
//        return getSimNumber;
//    }

//    static public String get_sim_serial_number(){
//        TelephonyManager teleManger = (TelephonyManager) MainActivity.ctx.getSystemService(Context.TELEPHONY_SERVICE);
//        String getSimSerialNumber = teleManger.getSimSerialNumber();
//        return getSimSerialNumber;
//    }

    static public void next_point(){
        mv_utils.playResource(MainActivity.ctx, R.raw.missile);
//        Tab_Messenger.showToast(activity.getString(R.string.next_point));
    }

    static public void mission_finished(boolean finished){
        if(finished) {
            mv_utils.playResource(MainActivity.ctx, R.raw.explosion);
            Tab_Messenger.showToast(activity.getString(R.string.mission_finished));
        }else{
            mv_utils.playResource(MainActivity.ctx, R.raw.prize);
            Tab_Messenger.showToast(activity.getString(R.string.mission_canceled));
        }
        Tab_Map.mapFinishMission.setVisibility(View.GONE);
    }

    public static final String FILE_BROWSER_CACHE_DIR = "CertCache";

    @SuppressLint("StaticFieldLeak")
    private class CopyFileToAppDirTask extends AsyncTask<Uri, Void, String> {
        private ProgressDialog mProgressDialog;

        private CopyFileToAppDirTask() {
            mProgressDialog = new ProgressDialog(MainActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setMessage("Please Wait..");
            mProgressDialog.show();
        }

        protected String doInBackground(Uri... uris) {
            try {
                return writeFileContent(uris[0]);
            } catch (IOException e) {
                Log.d(TAG, "Failed to copy file {}" + e.getMessage());
                return null;
            }
        }

        protected void onPostExecute(String cachedFilePath) {
            mProgressDialog.dismiss();
            if (cachedFilePath != null) {
                Log.d(TAG, "Cached file path {}" + cachedFilePath);
            } else {
                Log.d(TAG, "Writing failed {}");
            }

        }
    }

    private String writeFileContent(final Uri uri) throws IOException {
        InputStream selectedFileInputStream =
                getContentResolver().openInputStream(uri);
        if (selectedFileInputStream != null) {
            final File certCacheDir = new File(getExternalFilesDir(null), FILE_BROWSER_CACHE_DIR);
            boolean isCertCacheDirExists = certCacheDir.exists();
            if (!isCertCacheDirExists) {
                isCertCacheDirExists = certCacheDir.mkdirs();
            }
            if (isCertCacheDirExists) {
                String filePath = certCacheDir.getAbsolutePath() + "/" + getFileDisplayName(uri);
                OutputStream selectedFileOutPutStream = new FileOutputStream(filePath);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = selectedFileInputStream.read(buffer)) > 0) {
                    selectedFileOutPutStream.write(buffer, 0, length);
                }
                selectedFileOutPutStream.flush();
                selectedFileOutPutStream.close();
                return filePath;
            }
            selectedFileInputStream.close();
        }
        return null;
    }

    // Returns file display name.
    @Nullable
    private String getFileDisplayName(final Uri uri) {
        String displayName = null;
        try (Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if(idx >= 0) {
                    displayName = cursor.getString(idx);
                    Log.i(TAG, "Display Name {}" + displayName);
                }
            }
        }
        return displayName;
    }

//    image/jpeg
//    audio/mpeg4-generic
//    text/html
//    audio/mpeg
//    audio/aac
//    audio/wav
//    audio/ogg
//    audio/midi
//    audio/x-ms-wma
//    video/mp4
//    video/x-msvideo
//    video/x-ms-wmv
//    image/png
//    image/jpeg
//    image/gif
//    .xml ->text/xml
//    .txt -> text/plain
//    .cfg -> text/plain
//    .csv -> text/plain
//    .conf -> text/plain
//    .rc -> text/plain
//    .htm -> text/html
//    .html -> text/html
//    .pdf -> application/pdf
//    .apk -> application/vnd.android.package-archive

    int OPEN_DIRECTORY_REQUEST_CODE = 648393;
    public void performFileSearch(String messageTitle) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
//        intent.setType("application/*");
//        intent.setType("application/pdf");
        intent.setType("image/png");
        String[] mimeTypes = new String[]{"application/x-binary,application/octet-stream"};
        if (mimeTypes.length > 0) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(Intent.createChooser(intent, messageTitle), OPEN_DIRECTORY_REQUEST_CODE);
//            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        MainActivity.MyLogInfo(result.toString());
//                        // Use the uri to load the image
////                                Uri uri = result.getData().getData();
//                        // Use the file path to set image or upload
////                                String filePath = result.getData().getStringExtra(Const.BundleExtras.FILE_PATH);
//                        //...
//
//                        // for Multiple picks
//                        // first item
////                                Uri first = result.getData().getData();
//                        // other items
////                                ClipData clipData = result.getData().getClipData();
//                        // Multiple file paths list
////                                ArrayList<String> filePaths = result.getData().getStringArrayListExtra(Const.BundleExtras.FILE_PATH_LIST);
//                        //...
//                    }
//                }
//            });
        } else {
            Log.d(TAG, "Unable to resolve Intent.ACTION_OPEN_DOCUMENT {}");
        }
    }

    public static List<File> getAllMediaFilesOnDevice(Context context) {
        List<File> files = new ArrayList<>();
        try {
            final String[] columns = { MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DATE_ADDED,
                    MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

            MergeCursor cursor = new MergeCursor(new Cursor[]{context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null),
                    context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null, null, null),
                    context.getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, columns, null, null, null),
                    context.getContentResolver().query(MediaStore.Video.Media.INTERNAL_CONTENT_URI, columns, null, null, null)
            });
            cursor.moveToFirst();
            files.clear();
            while (!cursor.isAfterLast()){
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                int lastPoint = path.lastIndexOf(".");
                path = path.substring(0, lastPoint) + path.substring(lastPoint).toLowerCase();
                files.add(new File(path));
                cursor.moveToNext();
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return files;
    }

    public void checkFileAccessPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                return;
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            Intent getPermission = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
            Bundle options = new Bundle();
            ActivityCompat.startActivityForResult(activity, getPermission, MV_ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION, options);
        }
    }

    public void checkDisplayOverOtherAppsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this))    return;
            Intent getPermission = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
            Bundle options = new Bundle();
            ActivityCompat.startActivityForResult(activity, getPermission, MV_ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION, options);
        }
    }

    public void checkBatteryOptimizationPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (pm.isIgnoringBatteryOptimizations(getPackageName())) return;

            @SuppressLint("BatteryLife") Intent getPermission = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
            Bundle options = new Bundle();
            ActivityCompat.startActivityForResult(activity, getPermission, MV_ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION, options);
        }
    }

    static public void check_notification_permission(){
        try
        {
            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    static public void check_record_audio_permission(){
        try
        {
            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    static public void check_location_permission(){
        try
        {
            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }

        try
        {
            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    static public void check_sms_permission(){
        try
        {
            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }

        try
        {
            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }

        try
        {
            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_SMS}, 1);
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    /**
     * Start service if the service is not running.
     *
     * @param action : Enum of Action.
     */
    static public void actionOnService(Actions action) {
        check_notification_permission();
        try{
            Intent intent = new Intent(MainActivity.ctx, MapViewerService.class);
            if(intent != null){
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(action.name());
                if (((new ServiceTracker().getServiceState(MainActivity.ctx)) == ServiceTracker.ServiceState.STOPPED) && (action == Actions.STOP)){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        new log("Starting the service in >=26 Mode");
                        MainActivity.ctx.startForegroundService(intent);
                        return;
                    }
                }
                new log("Starting the service in < 26 Mode");
                MainActivity.ctx.startService(intent);
            }else{
                MainActivity.MyLogInfo("actionOnService: " + Arrays.toString(new Throwable().getStackTrace()));
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }
}
