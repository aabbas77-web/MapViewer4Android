package com.oghab.mapviewer.mapviewer;

/**
 * @author Ali Abbas
 */

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balda.flipper.Root;
import com.blankj.subutil.util.DangerousUtils;
import com.blankj.subutil.util.LocationUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.RomUtils;
import com.blankj.utilcode.util.SDCardUtils;
//import com.oghab.mapviewer.BuildConfig;
import com.oghab.mapviewer.MApplication;
import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;
import com.oghab.mapviewer.launcher.Actions;
import com.oghab.mapviewer.launcher.MapViewerService;
import com.oghab.mapviewer.launcher.ServiceTracker;
import com.oghab.mapviewer.launcher.log;
import com.oghab.mapviewer.utils.FileHelper;
import com.oghab.mapviewer.utils.IntentHelper;
import com.oghab.mapviewer.utils.UriUtil;

import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.modules.OfflineTileProvider;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.tileprovider.util.StorageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

import dji.sdk.sdkmanager.DJISDKManager;

//import dji.sdk.sdkmanager.DJISDKManager;

/**
 * Created by MapViewer on 27/03/2017.
 */

public class Tab_Main extends Fragment
        implements View.OnClickListener {

    static public TextView tv_Text;
    static public Button b_register, b_change_password, b_change_encryption_key, b_load, b_show_log, b_send_log, b_delete_log, b_information, b_maps_path, b_clear_cache, b_delete_roots, b_main_root, b_sd_root, b_file_server;
    CheckBox cb_enable_data_sim,cb_development,cb_emulator,cb_auto_start_service;
    static public EditText et_sn, et_password, et_encryption_key;
    HorizontalScrollView hsv_development;
    static public TextView tv_sys_id;
    static public LinearLayout layout_sys_id;
    private LinearLayout ll_settings;
    static public CheckBox sw_debug;

    private static final String TAG = "Main";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * Inflate the layout for this fragment
         */
        View view = null;
        try
        {
            view = inflater.inflate(R.layout.tab_main, container, false);
            init(view);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return view;
    }

//    public static final String PREFS_NAME = "my_prefs";

    public void show_log(){
//        IntentHelper.openFile(MainActivity.strLogFile);
        final String[] text = {FileHelper.readTextFile(MainActivity.strLogFile, "")+"\n"};

//        try{
////            Context maxlink_ctx = MainActivity.ctx.createPackageContext("com.autel.maxlink",Context.CONTEXT_IGNORE_SECURITY);
////            Context maxlink_ctx = MainActivity.ctx.createPackageContext("com.autel.maxlink",Context.CONTEXT_IGNORE_SECURITY);
//            Context maxlink_ctx = MainActivity.ctx.createPackageContext("com.autel.maxlink",0);
//            if(maxlink_ctx != null) {
////                SharedPreferences sharedPreferences = maxlink_ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//                SharedPreferences sharedPreferences = maxlink_ctx.getSharedPreferences(PREFS_NAME, Context.MODE_WORLD_READABLE);
//                if(sharedPreferences != null){
//                    String is_activate = sharedPreferences.getString("is_activate","error");
//                    MainActivity.MyLogInfo("is_activate: "+is_activate);
//                    text[0] += "is_activate: "+is_activate+"\n";
//
//                    text[0] += "all: "+sharedPreferences.getAll().toString()+"\n";
//
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    if(editor != null) {
//                        editor.putString("is_activate", "True");
////                        editor.apply();
//                        if(editor.commit()){
//                            MainActivity.MyLogInfo("commit ok");
//                        }else{
//                            MainActivity.MyLogInfo("commit failed");
//                        }
//                    }else{
//                        MainActivity.MyLogInfo("editor == null");
//                    }
//                }else{
//                    MainActivity.MyLogInfo("sharedPreferences == null");
//                }
//            }else{
//                MainActivity.MyLogInfo("maxlink_ctx == null");
//            }
//        }
//        catch (Throwable ex)
//        {
//            MainActivity.MyLog(ex);
//        }

        tv_Text.setText(text[0]);
    }

    String str = "";
    public class FileWalker {
        public void walk(File root) {
            File[] list = root.listFiles();
            if((list != null) && (list.length > 0)) {
                for (File f : list) {
                    if (f.isDirectory()) {
                        str += "Folder: [" + f.getAbsoluteFile() + "]\n";
                        walk(f);
                    } else {
                        str += "File: [" + f.getAbsoluteFile() + "]\n";
                    }
                }
            }
        }
    }

//    void listFiles(Path path) throws IOException {
//        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
//            for (Path entry : stream) {
//                if(Files.isWritable(entry)) {
//                    if (Files.isDirectory(entry)) {
//                        str += "Folder: [" + entry + "]\n";
//                        listFiles(entry);
//                    } else {
//                        str += "File: [" + entry + "]\n";
//                    }
//                }
//            }
//        }
//    }

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

// Android 11 osmdroid search mbtiles
//    private void findArchiveFiles() {
//        clearArcives();
//
//        // path should be optionally configurable
//        File cachePaths = Configuration.getInstance().getOsmdroidBasePath();
//        if (cachePaths != null) {
//            final File[] files = cachePaths.listFiles();
//            if (files != null) {
//                for (final File file : files) {
//                    final IArchiveFile archiveFile = ArchiveFileFactory.getArchiveFile(file);
//                    if (archiveFile != null) {
//                        archiveFile.setIgnoreTileSource(ignoreTileSource);
//                        mArchiveFiles.add(archiveFile);
//                    }
//                }
//            }
//        }
//    }

    static public File[] list_files(DocumentFile f){
        try {
            if (f != null){
                MainActivity.DocumentFileWalker fw = new MainActivity.DocumentFileWalker();
                fw.walk(f);
                MainActivity.MyLogInfo("[list_files]: \n"+fw.str);

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

    private void change_maps_path(){
        try {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                intent = MainActivity.storageManager.requireExternalAccess(MainActivity.ctx);
            }else{
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            }
            if(intent == null)  return;
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT_TREE);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
//            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
//            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //            intent.addCategory(Intent.CATEGORY_OPENABLE);
            // set MIME type for image
//            intent.setType("image/*");
//            intent.setType("*/*");
//        startActivityForResult(intent, RQS_OPEN_IMAGE);

            //Instead of startActivityForResult use this one
//        Intent intent = new Intent(this,OtherActivity.class);
            someActivityResultLauncher.launch(intent);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    //Instead of onActivityResult() method use this one
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                try {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        Intent data = result.getData();
                        Uri uri = Objects.requireNonNull(data).getData();
                        MainActivity.MyLogInfo(Objects.requireNonNull(uri).toString());

                        MainActivity.ctx.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                        MainActivity.strMapsUri = uri.toString();
                        MainActivity.SetMapsUriJNI(MainActivity.strMapsUri);
                        MainActivity.SaveCurrSettingsJNI();

                        try {
                            FileHelper.FileScanner fileScanner = new FileHelper.FileScanner("mbtiles");
                            Uri uri0 = Uri.parse(MainActivity.strMapsUri);
                            if(uri0 != null){
                                DocumentFile documentFile = DocumentFile.fromTreeUri(MainActivity.ctx,uri0);
                                if(documentFile != null){
                                    fileScanner.scan(documentFile);
                                    if(fileScanner.files.size() > 0) {
                                        File[] tmp = new File[fileScanner.files.size()];
                                        fileScanner.files.toArray(tmp);

                                        FileHelper.FileScanner.str = "Main Maps: "+MainActivity.strMapsUri+"\n";
                                        for(File f:tmp){
                                            FileHelper.FileScanner.str += f.getAbsolutePath()+"\n";
                                        }

                                        final IRegisterReceiver registerReceiver = new SimpleRegisterReceiver(MainActivity.ctx);
                                        OfflineTileProvider provider = new OfflineTileProvider(registerReceiver, tmp);
                                        Tab_Map.map.setTileProvider(provider);
                                        Tab_Map.map.invalidate();
                                    }else{
                                        MainActivity.MyLogInfo("fileScanner.files.size() <= 0");
                                    }
                                }else{
                                    MainActivity.MyLogInfo("documentFile == null");
                                }
                            }else{
                                MainActivity.MyLogInfo("uri0 == null");
                            }
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }

//                        File[] files = list_files(DocumentFile.fromTreeUri(MainActivity.ctx,uri));
//                        str = "";
//                        for(File file:files){
//                            str += "Path: [" + file.getAbsolutePath() + "]\n";
//                        }
//                        tv_Text.setText(str);
                    }
                } catch (Throwable ex) {
                    MainActivity.MyLog(ex);
                }
            }
        });

    private void show_information(){
        try
        {
            str = "";

//            File[] externalDirs = MainActivity.ctx.getExternalFilesDirs( null);
//            if((externalDirs != null) && (externalDirs.length > 0)){
//                str = "Available Storage:\n";
//                long size = 0;
//                for (File externalDir : externalDirs) {
//                    // "Returned paths may be null if a storage device is unavailable."
//                    if (externalDir == null)    continue;
////                File file = new File(externalDir.getAbsoluteFile()+"/Download");
//                    File file = new File(externalDir.getAbsoluteFile()+"/Download/MapViewer/Maps");
//
//                    File map = new File(externalDir.getAbsoluteFile()+"/Download/MapViewer/Maps/01_World1_8.mbtiles");
//                    size = map.length();
//
//                    String state = Environment.getExternalStorageState(file);
//                    if (Environment.MEDIA_MOUNTED.equals(state)) {
//                        File[] files = file.listFiles();
//                        if(files != null) {
//                            int length = files.length;
//                            if (isWritable(file))
//                                str += "Writable [" + file + "] {" + length + " files}\n";
//                            else
//                                str += "Read Only [" + file + "] {" + length + " files}\n";
//                            str += file + ", 01_World1_8.mbtiles size: [" + size + "]\n";
//                        }
//                    }
//                }
//            }

            str += "~~~~~~~~~~~~~~~~~~~~~~Start~~~~~~~~~~~~~~~~~~~~~~~~\n";
            str += "Maps Path: ["+MainActivity.strMapsPath + "]\n";
            str += "Maps Cache Path: ["+MainActivity.strCachePath + "]\n";

            File[] directory;
            directory = MainActivity.ctx.getExternalMediaDirs();
            for (File file : directory) {
                if (file.getName().contains(MainActivity.ctx.getPackageName())) {
                    str += "Path: [" + file.getAbsolutePath() + "]\n";
                }
            }
//            str += "All Media Files~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
//            List<File> files = MainActivity.getAllMediaFilesOnDevice(MainActivity.ctx);
//            for(File file:files){
//                str += "Path: [" + file.getAbsolutePath() + "]\n";
//            }
            str += "IDs~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
            str += "IP Address (IPv4): ["+Tab_Messenger.getIPAddress(true) + "]\n";
            str += "IP Addresses (IPv4): ["+Tab_Messenger.getIPAddresses(true) + "]\n";
            str += "IP Address (IPv6): ["+Tab_Messenger.getIPAddress(false) + "]\n";
            str += "IP Addresses (IPv6): ["+Tab_Messenger.getIPAddresses(false) + "]\n";
            str += "Device~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
            str += "MacAddress: [" + DeviceUtils.getMacAddress() + "]\n";
            str += "AndroidID: [" + DeviceUtils.getAndroidID() + "]\n";
            str += "UniqueDeviceId: [" + DeviceUtils.getUniqueDeviceId() + "]\n";
            str += "Manufacturer: [" + DeviceUtils.getManufacturer() + "]\n";
            str += "Model: [" + DeviceUtils.getModel() + "]\n";
            str += "SDKVersionName: [" + DeviceUtils.getSDKVersionName() + "]\n";
            str += "SDKVersionCode: [" + DeviceUtils.getSDKVersionCode() + "]\n";

            str += "isAdbEnabled: [" + DeviceUtils.isAdbEnabled() + "]\n";
            str += "isDeviceRooted: [" + DeviceUtils.isDeviceRooted() + "]\n";
            str += "isDevelopmentSettingsEnabled: [" + DeviceUtils.isDevelopmentSettingsEnabled() + "]\n";
            str += "isEmulator: [" + DeviceUtils.isEmulator() + "]\n";
            str += "isTablet: [" + DeviceUtils.isTablet() + "]\n";
            str += "Network~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
            str += "BroadcastIpAddress: [" + NetworkUtils.getBroadcastIpAddress() + "]\n";
            str += "IPAddress V4: [" + NetworkUtils.getIPAddress(true) + "]\n";
            str += "IPAddress V6: [" + NetworkUtils.getIPAddress(false) + "]\n";
            str += "NetworkOperatorName: [" + NetworkUtils.getNetworkOperatorName() + "]\n";
            str += "IpAddressByWifi: [" + NetworkUtils.getIpAddressByWifi() + "]\n";
            str += "NetMaskByWifi: [" + NetworkUtils.getNetMaskByWifi() + "]\n";
            str += "GatewayByWifi: [" + NetworkUtils.getGatewayByWifi() + "]\n";
            str += "ServerAddressByWifi: [" + NetworkUtils.getServerAddressByWifi() + "]\n";
            str += "SSID: [" + NetworkUtils.getSSID() + "]\n";
            str += "isMobileDataEnabled: [" + NetworkUtils.getMobileDataEnabled() + "]\n";
            str += "NetworkType: [" + NetworkUtils.getNetworkType() + "]\n";
            str += "isWifiEnabled: [" + NetworkUtils.getWifiEnabled() + "]\n";
            str += "is4G: [" + NetworkUtils.is4G() + "]\n";
            str += "is5G: [" + NetworkUtils.is5G() + "]\n";
            str += "isAvailable: [" + NetworkUtils.isAvailable() + "]\n";
            str += "isAvailableByDns: [" + NetworkUtils.isAvailableByDns() + "]\n";
            str += "isAvailableByPing: [" + NetworkUtils.isAvailableByPing() + "]\n";
            str += "isConnected: [" + NetworkUtils.isConnected() + "]\n";
            str += "isMobileData: [" + NetworkUtils.isMobileData() + "]\n";
            str += "isWifiAvailable: [" + NetworkUtils.isWifiAvailable() + "]\n";
            str += "isWifiConnected: [" + NetworkUtils.isWifiConnected() + "]\n";
            str += "Phone~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
    //        str += "IMEI: [" + PhoneUtils.getIMEI() + "]\n";
    //        str += "DeviceId: [" + PhoneUtils.getDeviceId() + "]\n";
    //        str += "Imei: [" + PhoneUtils.getImeiOrMeid(true) + "]\n";
    //        str += "Meid: [" + PhoneUtils.getImeiOrMeid(false) + "]\n";
    //        str += "IMSI: [" + PhoneUtils.getIMSI() + "]\n";
    //        str += "MEID: [" + PhoneUtils.getMEID() + "]\n";
    //        str += "Serial: [" + PhoneUtils.getSerial() + "]\n";
            str += "SimOperatorByMnc: [" + PhoneUtils.getSimOperatorByMnc() + "]\n";
            str += "SimOperatorName: [" + PhoneUtils.getSimOperatorName() + "]\n";
            int nPhoneType = PhoneUtils.getPhoneType();
            String strPhoneType = "None";
            switch(nPhoneType){
                case TelephonyManager.PHONE_TYPE_SIP:{
                    strPhoneType = "SIP";
                    break;
                }
                case TelephonyManager.PHONE_TYPE_GSM:{
                    strPhoneType = "GSM";
                    break;
                }
                case TelephonyManager.PHONE_TYPE_CDMA:{
                    strPhoneType = "CDMA";
                    break;
                }
            }
            str += "PhoneType: [" + strPhoneType + "]\n";
            str += "isPhone: [" + PhoneUtils.isPhone() + "]\n";
            str += "isSimCardReady: [" + PhoneUtils.isSimCardReady() + "]\n";
            str += "Rom~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
            str += "ROM Name: [" + RomUtils.getRomInfo().getName() + "]\n";
            str += "ROM Version: [" + RomUtils.getRomInfo().getVersion() + "]\n";
            str += "is360: [" + RomUtils.is360() + "]\n";
            str += "isSamsung: [" + RomUtils.isSamsung() + "]\n";
            str += "isCoolpad: [" + RomUtils.isCoolpad() + "]\n";
            str += "isGionee: [" + RomUtils.isGionee() + "]\n";
            str += "isGoogle: [" + RomUtils.isGoogle() + "]\n";
            str += "isHtc: [" + RomUtils.isHtc() + "]\n";
            str += "isHuawei: [" + RomUtils.isHuawei() + "]\n";
            str += "isLeeco: [" + RomUtils.isLeeco() + "]\n";
            str += "isLenovo: [" + RomUtils.isLenovo() + "]\n";
            str += "isLg: [" + RomUtils.isLg() + "]\n";
            str += "isMeizu: [" + RomUtils.isMeizu() + "]\n";
            str += "isMotorola: [" + RomUtils.isMotorola() + "]\n";
            str += "isNubia: [" + RomUtils.isNubia() + "]\n";
            str += "isOneplus: [" + RomUtils.isOneplus() + "]\n";
            str += "isOppo: [" + RomUtils.isOppo() + "]\n";
            str += "isSmartisan: [" + RomUtils.isSmartisan() + "]\n";
            str += "isSony: [" + RomUtils.isSony() + "]\n";
            str += "isVivo: [" + RomUtils.isVivo() + "]\n";
            str += "isXiaomi: [" + RomUtils.isXiaomi() + "]\n";
            str += "isZte: [" + RomUtils.isZte() + "]\n";
            str += "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
            str += "SDCardPathByEnvironment: [" + SDCardUtils.getSDCardPathByEnvironment() + "]\n";
            str += "ExternalAvailableSize: [" + SDCardUtils.getExternalAvailableSize() + "]\n";
            str += "ExternalTotalSize: [" + SDCardUtils.getExternalTotalSize() + "]\n";
            str += "InternalAvailableSize: [" + SDCardUtils.getInternalAvailableSize() + "]\n";
            str += "InternalTotalSize: [" + SDCardUtils.getInternalTotalSize() + "]\n";
            str += "isSDCardEnableByEnvironment: [" + SDCardUtils.isSDCardEnableByEnvironment() + "]\n";
            str += "MountedSDCardPath:\n";
            for(String s:SDCardUtils.getMountedSDCardPath()){
                str += "        [" + s + "]\n";
            }
            str += "SDCardInfo:\n";
            for(SDCardUtils.SDCardInfo i:SDCardUtils.getSDCardInfo()){
                str += "        [" + i + "]\n";
            }
            str += "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
            str += "isLocationEnabled: [" + LocationUtils.isLocationEnabled() + "]\n";
            str += "isGpsEnabled: [" + LocationUtils.isGpsEnabled() + "]\n";
            //LocationUtils.getAddress()
            str += "MapsPath~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
            //DangerousUtils.installAppSilent("test.apk");
            //DangerousUtils.uninstallAppSilent()
            //DangerousUtils.reboot();
            //DangerousUtils.shutdown();
            //DangerousUtils.sendSmsSilent();

            FileWalker fw = new FileWalker();
//            fw.walk(Objects.requireNonNull(new File(MainActivity.strMapsPath).getParentFile()));
            fw.walk(new File(MainActivity.strMapsPath));
            if(MApplication.isRealDevice() && MainActivity.isDevelpoment()) {
                str += "DJI~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
                DJISDKManager.setAppKey("");

                str += "LDM_PLUS_LICENSE_ROOT_PATH: " + DJISDKManager.LDM_PLUS_LICENSE_ROOT_PATH + "\n";
                str += "USB_ACCESSORY_ATTACHED: " + DJISDKManager.USB_ACCESSORY_ATTACHED + "\n";
                str += "getAppID: " + DJISDKManager.getAppID(getContext()) + "\n";

                String LDM_PLUS_LICENSE_FILE_EXTENSION = com.dji.megatronking.stringfog.lib.gfd.fdd("dFx7bAMyPw==");
                String LDM_PLUS_LICENSE_FILE_EXTENSION_OLD = com.dji.megatronking.stringfog.lib.gfd.fdd("d04lJA==");
                str += "LDM_PLUS_LICENSE_FILE_EXTENSION: " + LDM_PLUS_LICENSE_FILE_EXTENSION + "\n";
                str += "LDM_PLUS_LICENSE_FILE_EXTENSION_OLD: " + LDM_PLUS_LICENSE_FILE_EXTENSION_OLD + "\n";

                String SILENT_MODE_LICENSE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DJI/license.dlf";
                str += "SILENT_MODE_LICENSE_PATH: " + SILENT_MODE_LICENSE_PATH + "\n";
            }
            str += "~~~~~~~~~~~~~~~~~~~~~~End~~~~~~~~~~~~~~~~~~~~~~~~~~\n";

        // path should be optionally configurable
//                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//                builder.detectAll();
//                StrictMode.setVmPolicy(builder.build());

//                long size = 0;
//                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(MainActivity.strMapsPath));
//                for (final Path path : directoryStream) {
//                    str += "Archive File Path: ["+ path.getFileName() + "]\n";
////                        if (size == 0) {
////                            size = Files.size(path);
////                        } else {
////                            final long fileSize = Files.size(path);
////                            assertTrue("Expected size: " + size + " Size of " + path.getFileName() + ": " + fileSize,
////                                    size == fileSize);
////                        }
//                }

//                Uri contentUri = MediaStore.Files.getContentUri("external");
//                Uri contentUri = MediaStore.Files.getContentUri(MainActivity.strMapsPath);
//                str += contentUri;

//                ContentResolver resolver = MainActivity.ctx.getContentResolver();
//                //Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                // to handle hidden videos
//                Uri videoUri = MediaStore.Files.getContentUri("external");
//                Cursor cursor = resolver.query(videoUri,
//                        new String[]{BaseColumns._ID},
//                        MediaStore.MediaColumns.DATA + " = ?",
//                        new String[]{MainActivity.strMapsPath}, null);
//                cursor.moveToFirst();

//                if (cursor.isAfterLast()) {
//                    cursor.close();
//                    // insert system media db
//                    ContentValues values = new ContentValues();
//                    values.put(MediaStore.Video.Media.DATA, path);
//                    values.put(MediaStore.Video.Media.MIME_TYPE, MediaType.getMimeType(path));
//                    return context.getContentResolver().insert(videoUri, values);
//                } else {
//                    int imageId = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
//                    Uri uri = ContentUris.withAppendedId(videoUri, imageId);
//                    cursor.close();
//                    return uri;
//                }

//            File cachePaths = Configuration.getInstance().getOsmdroidBasePath();
//                File cachePaths = new File(MainActivity.strMapsPath);
//                File cachePaths = new File(MainActivity.strMapsPath).getParentFile();
//                if (cachePaths != null) {
//                    final File[] files = cachePaths.listFiles();
//                    if (files != null) {
//                        for (final File file : files) {
//                            if(file.isDirectory())
//                                str += "Directory: ["+ file.getAbsolutePath() + "]\n";
//                            else
//                                str += "File: ["+ file.getAbsolutePath() + "]\n";
//                        }
//                    }
//                }

//                listFiles(Paths.get(MainActivity.strMapsPath).getParent());

//                Path path= Paths.get(MainActivity.strMapsPath).getParent();
//                try {
//                    Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
//                        @Override
//                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                            str += "File: ["+ file + "]\n";
//                            return FileVisitResult.CONTINUE;
//                        }
//
//                        @Override
//                        public FileVisitResult preVisitDirectory(Path file, BasicFileAttributes attrs) throws IOException {
//                            str += "Folder: ["+ file + "]\n";
//                            return FileVisitResult.CONTINUE;
//                        }
//                    });
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }

        tv_Text.setText(str);
    }

    private void init(View view) {
        try
        {
//            SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
//            MainActivity.LoadEncryptionKeyJNI();

            tv_Text = view.findViewById(R.id.tv_Text);
            tv_Text.setText(FileHelper.FileScanner.str);

//            String dir_internal = StorageUtils.getBestWritableStorage().path;
//            tv_Text.setText("dir_internal: "+dir_internal);
/*
//            File file = new File(MainActivity.dir_internal,"MapViewer");
            File file = new File(dir_internal,"MapViewer");
            if(file.exists()){
//                tv_Text.setText(file.getAbsolutePath()+", Exists.");

                File mapsFile = new File(file.getAbsolutePath(),"Maps");
//                tv_Text.setText(Arrays.toString(mapsFile.list()));
                if(mapsFile.exists()){
                    tv_Text.setText(mapsFile.getAbsolutePath()+", Exists.");
                }else{
                    tv_Text.setText(mapsFile.getAbsolutePath()+", not Exists.");
                }
            }else{
                tv_Text.setText(file.getAbsolutePath()+", not Exists.");
            }
*/
//            if(!file.mkdirs()){
//                Tab_Messenger.showToast(file.getAbsolutePath()+", Folder not created...");
//            }
//            tv_Text.setText("dir_internal: "+MainActivity.dir_internal);
//            tv_Text.setText("dir_internal: "+file.getAbsolutePath());
//            tv_Text.setText("dir_internal: "+MainActivity.dir_internal);

            tv_sys_id = view.findViewById(R.id.tv_sys_id);

            layout_sys_id = view.findViewById(R.id.layout_sys_id);
            layout_sys_id.setVisibility(View.VISIBLE);
//            layout_sys_id.setVisibility(View.GONE);

            ll_settings = view.findViewById(R.id.ll_settings);
            ll_settings.setVisibility(View.GONE);

            sw_debug = view.findViewById(R.id.sw_debug);
            sw_debug.setOnClickListener(this);
            sw_debug.setChecked(MainActivity.IsDebugJNI());

            et_sn = view.findViewById(R.id.et_sn);
            et_sn.setText(String.valueOf(MainActivity.GetSystemSNJNI()));

            et_password = view.findViewById(R.id.et_password);
            et_password.setText(MainActivity.password);

            et_encryption_key = view.findViewById(R.id.et_encryption_key);
            et_encryption_key.setText(MainActivity.GetEncryptionKeyJNI());

            hsv_development = view.findViewById(R.id.hsv_development);
            hsv_development.setVisibility(View.GONE);

            b_register = view.findViewById(R.id.b_register);
            b_register.setOnClickListener(this);

            b_change_password = view.findViewById(R.id.b_change_password);
            b_change_password.setOnClickListener(this);

            b_change_encryption_key = view.findViewById(R.id.b_change_encryption_key);
            b_change_encryption_key.setOnClickListener(this);

            b_load = view.findViewById(R.id.b_load);
            b_load.setOnClickListener(this);

            b_show_log = view.findViewById(R.id.b_show_log);
            b_show_log.setOnClickListener(this);

            b_send_log = view.findViewById(R.id.b_send_log);
            b_send_log.setOnClickListener(this);

            b_delete_log = view.findViewById(R.id.b_delete_log);
            b_delete_log.setOnClickListener(this);

            b_information = view.findViewById(R.id.b_information);
            b_information.setOnClickListener(this);

            b_maps_path = view.findViewById(R.id.b_maps_path);
            b_maps_path.setOnClickListener(this);

            b_clear_cache = view.findViewById(R.id.b_clear_cache);
            b_clear_cache.setOnClickListener(this);

            b_delete_roots = view.findViewById(R.id.b_delete_roots);
            b_delete_roots.setOnClickListener(this);

            b_main_root = view.findViewById(R.id.b_main_root);
            b_main_root.setOnClickListener(this);

            b_sd_root = view.findViewById(R.id.b_sd_root);
            b_sd_root.setOnClickListener(this);

            b_file_server = view.findViewById(R.id.b_file_server);
            b_file_server.setOnClickListener(this);

            cb_enable_data_sim = view.findViewById(R.id.cb_enable_data_sim);
            cb_enable_data_sim.setOnClickListener(this);

            cb_development = view.findViewById(R.id.cb_development);
            cb_development.setOnClickListener(this);
            cb_development.setChecked(MainActivity.isDevelpoment());

            cb_emulator = view.findViewById(R.id.cb_emulator);
            cb_emulator.setOnClickListener(this);
            cb_emulator.setChecked(MApplication.isEmulator());

            cb_auto_start_service = view.findViewById(R.id.cb_auto_start_service);
            cb_auto_start_service.setOnClickListener(this);
            cb_auto_start_service.setChecked(MainActivity.bAutoStartService);

            // initialize localization
            try {
                tv_sys_id.setText("System ID: "+Long.toString(MainActivity.GetSystemIdJNI()));
                if(MainActivity.IsDemoVersionJNI())
                {
                    layout_sys_id.setVisibility(View.VISIBLE);
                }
            } catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
            }

            MainActivity.set_fullscreen();

//            String dir_internal = StorageUtils.getBestWritableStorage().path;
//            tv_Text.setText("dir_internal: "+dir_internal);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void toggle_settings() {
        try
        {
            if(ll_settings.getVisibility() == View.GONE)
                ll_settings.setVisibility(View.VISIBLE);
            else
                ll_settings.setVisibility(View.GONE);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    static public void update_message(final String message)
    {
        MainActivity.activity.runOnUiThread(() -> {
            try
            {
                Tab_Main.tv_Text.setText(message);
            }
            catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
            }
        });
    }

//    /*
//     * Notifications from UsbService will be received here.
//     */
//    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            switch (intent.getAction()) {
//                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
//                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
//                    break;
//                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
//                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
//                    break;
//                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
//                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
//                    break;
//                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
//                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
//                    break;
//                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
//                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
//    };
//    private UsbService usbService;
//    private MyHandler mHandler;
//    private final ServiceConnection usbConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
//            usbService = ((UsbService.UsbBinder) arg1).getService();
//            usbService.setHandler(mHandler);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            usbService = null;
//        }
//    };

    @Override
    public void onStop() {
        try {
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
//        setFilters();  // Start listening notifications from UsbService
//        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    @Override
    public void onPause() {
        try {
//        activity.unregisterReceiver(mUsbReceiver);
//        activity.unbindService(usbConnection);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        try
        {
            switch (view.getId()) {
                case R.id.b_register:
                {
                    try {
                        long sn = Long.parseLong((et_sn.getText().toString()));
                        MainActivity.RegisterJNI(sn);

                        SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putLong("sn", sn);
                        editor.apply();

                        MainActivity.LeaveJNI();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_change_password:{
                    try {
                        MainActivity.password = et_password.getText().toString();

                        SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("password", MainActivity.password);
                        editor.apply();

                        Tab_Messenger.showToast("Password changed...");
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_change_encryption_key:{
                    try {
                        String encryption_key = et_encryption_key.getText().toString();
                        if(encryption_key.length() == 16) {
                            MainActivity.UpdateEncryptionKeyJNI(encryption_key);
                            MainActivity.SaveEncryptionKeyJNI();

                            Tab_Messenger.showToast("Encryption Key changed...");
                        }else{
                            Tab_Messenger.showToast("Error: Encryption Key must be 16 Characters...");
                        }
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_load:
                {
                    try {
                        SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                        long sn = settings.getLong("sn", 0);
                        et_sn.setText(String.valueOf(sn));
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_show_log:
                {
                    try {
                        show_log();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_send_log:
                {
                    try {
                        Tab_Messenger.sendFile(MainActivity.strLogFile, true, "Log sending...", true, new tcp_io_handler.SendCallback() {
                            @Override
                            public void onFinish(int error) {
                                if(error != tcp_io_handler.TCP_OK) {
                                    if (MainActivity.IsDebugJNI()) {
                                        MainActivity.MyLogInfoSilent("The Current Line Number is " + Arrays.toString(new Throwable().getStackTrace()));
                                    }
                                }
                                Tab_Messenger.showToast("Log sent...");
                            }
                        });
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_delete_log:
                {
                    try {
                        File file = new File(MainActivity.strLogFile);
                        if(file.exists()){
                            file.delete();
                            show_log();
                        }
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_information:
                {
                    try {
                        show_information();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_maps_path:
                {
                    try {
                        change_maps_path();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_clear_cache:
                {
                    try {
                        Tab_Map.clear_map_cache();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_delete_roots:
                {
                    try {
                        MainActivity.delete_roots();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_main_root:
                {
                    try {
                        MainActivity.add_main_root();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_sd_root:
                {
                    try {
                        MainActivity.add_sd_root();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_file_server:
                {
                    try {
                        Intent intent = new Intent();
                        intent.setClassName(MainActivity.ctx,"com.hyperionics.wdserverlib.ServerSettingsActivity");
                        startActivity(intent);
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.cb_development:
                {
                    try {
                        if(cb_development.isChecked()){
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
                                    if(new_password.equals(MainActivity.dev_password)){
                                        try {
                                            if(MainActivity.SetDevelopmentJNI(cb_development.isChecked())){
                                                hsv_development.setVisibility(View.VISIBLE);
                                                Tab_Messenger.showToast("Ok");
                                            }else{
                                                cb_development.setChecked(false);
                                                hsv_development.setVisibility(View.GONE);
                                                Tab_Messenger.showToast("Error");
                                            }
                                        } catch (Throwable ex) {
                                            MainActivity.MyLog(ex);
                                        }
                                    }else{
                                        cb_development.setChecked(false);
                                        hsv_development.setVisibility(View.GONE);
                                        Tab_Messenger.showToast(getString(R.string.incorrect_password));
                                        MainActivity.activity.finishAndRemoveTask();
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
                                    cb_development.setChecked(false);
                                    hsv_development.setVisibility(View.GONE);
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
                        }else{
                            hsv_development.setVisibility(View.GONE);
                            try {
                                if(MainActivity.SetDevelopmentJNI(cb_development.isChecked())){
                                    Tab_Messenger.showToast("Ok");
                                }else{
                                    Tab_Messenger.showToast("Error");
                                }
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        }
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.cb_emulator:
                {
                    try {
                        MApplication.is_emulator = cb_emulator.isChecked();
                        SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("is_emulator", MApplication.is_emulator);
                        editor.apply();

//                        if(MainActivity.SetEmulatorJNI(cb_emulator.isChecked())){
//                            Tab_Messenger.showToast("Ok");
//                        }else{
//                            Tab_Messenger.showToast("Error");
//                        }
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.cb_auto_start_service: {
                    MainActivity.bAutoStartService = cb_auto_start_service.isChecked();

                    SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("bAutoStartService", MainActivity.bAutoStartService);
                    editor.apply();

                    if(MainActivity.bAutoStartService){
                        try{
                            new log("START THE FOREGROUND SERVICE ON DEMAND");
                            MainActivity.actionOnService(Actions.START);
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    }else{
                        try{
                            new log("STOP THE FOREGROUND SERVICE ON DEMAND");
                            MainActivity.actionOnService(Actions.STOP);
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    }
                    break;
                }
                case R.id.cb_enable_data_sim:
                {
                    try {
                        //DangerousUtils.setMobileDataEnabled(cb_enable_data_sim.isChecked());
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.sw_debug:
                {
                    try {
                        MainActivity.SetDebugJNI(sw_debug.isChecked());
                    } catch (Throwable ex) {
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
}
