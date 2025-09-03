package com.oghab.mapviewer;

import static com.oghab.mapviewer.DJIConnectionControlActivity.ACCESSORY_ATTACHED;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;

import com.secneo.sdk.Helper;

import dji.sdk.base.BaseProduct;
import dji.sdk.products.Aircraft;
import dji.sdk.products.HandHeld;
import dji.sdk.sdkmanager.BluetoothProductConnector;
import dji.sdk.sdkmanager.DJISDKManager;

public class MApplication extends Application {
    static public boolean is_emulator = false;

    private static BaseProduct product = null;
    private static BluetoothProductConnector bluetoothConnector = null;
    private static Application app = null;

    /**
     * Gets instance of the specific product connected after the
     * API KEY is successfully validated. Please make sure the
     * API_KEY has been added in the Manifest
     */
    public static synchronized BaseProduct getProductInstance() {
        try {
            if(MApplication.isRealDevice()) {
                product = DJISDKManager.getInstance().getProduct();
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return product;
    }

    public static synchronized BluetoothProductConnector getBluetoothProductConnector() {
        try {
            if(MApplication.isRealDevice()) {
                bluetoothConnector = DJISDKManager.getInstance().getBluetoothProductConnector();
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return bluetoothConnector;
    }

    public static boolean isAircraftConnected() {
        try {
            return getProductInstance() != null && getProductInstance() instanceof Aircraft;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    public static boolean isHandHeldConnected() {
        try {
            return getProductInstance() != null && getProductInstance() instanceof HandHeld;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    public static synchronized Aircraft getAircraftInstance() {
        try {
            if (!isAircraftConnected()) {
                return null;
            }
            return (Aircraft) getProductInstance();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return null;
    }

    public static synchronized HandHeld getHandHeldInstance() {
        try {
            if (!isHandHeldConnected()) {
                return null;
            }
            return (HandHeld) getProductInstance();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return null;
    }

    public static Application getInstance() {
        try {
            return MApplication.app;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return null;
    }

    @Override
    public void onCreate() {
        try
        {
            super.onCreate();
            BroadcastReceiver br = new OnDJIUSBAttachedReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACCESSORY_ATTACHED);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {//AliSoft
                registerReceiver(br, filter, RECEIVER_EXPORTED);
            }else{
                registerReceiver(br, filter);
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    static public boolean isEmulator() {
        return MApplication.is_emulator;

//        return FileExists(GetEmulatorPathJNI());

//        return (Build.FINGERPRINT.startsWith("google/sdk_gphone_")
//                && Build.FINGERPRINT.endsWith(":user/release-keys")
//                && Build.MANUFACTURER == "Google" && Build.PRODUCT.startsWith("sdk_gphone_") && Build.BRAND == "google"
//                && Build.MODEL.startsWith("sdk_gphone_")) // Android SDK emulator
//                || Build.FINGERPRINT.startsWith("generic")
//                || Build.FINGERPRINT.startsWith("unknown")
//                || Build.MODEL.contains("google_sdk")
//                || Build.MODEL.contains("Emulator")
//                || Build.MODEL.contains("Android SDK built for x86")
//                || "QC_Reference_Phone" == Build.BOARD  //bluestacks
//                || Build.MANUFACTURER.contains("Genymotion")
//                || Build.HOST.startsWith("Build") //MSI App Player
//                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
//                || Build.PRODUCT == "google_sdk";
    }

    static public boolean isRealDevice() {
        return !isEmulator();
    }

    @Override
    protected void attachBaseContext(Context context) {
        try
        {
            super.attachBaseContext(context);

            SharedPreferences settings = context.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
            MApplication.is_emulator = settings.getBoolean("is_emulator", false);

            if(MApplication.isRealDevice()) {
                Helper.install(MApplication.this);
            }

            app = this;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

}
