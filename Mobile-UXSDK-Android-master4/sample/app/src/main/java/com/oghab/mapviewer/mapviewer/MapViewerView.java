package com.oghab.mapviewer.mapviewer;

import android.app.AlertDialog;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
//import android.app.FragmentManager;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.google.android.material.imageview.ShapeableImageView;
import com.oghab.mapviewer.MApplication;
import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;
import com.oghab.mapviewer.utils.FileHelper;
import com.oghab.mapviewer.utils.ModuleVerificationUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Locale;

import dji.sdk.sdkmanager.DJISDKManager;

//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import org.json.JSONException;
//import org.json.JSONObject;

public class MapViewerView extends LinearLayout {
    private static final String LAST_USED_BRIDGE_IP = "bridgeip";

    static public RadioButton radio_main;
    static public RadioButton radio_messenger;
    static public RadioButton radio_camera;
    static public RadioButton radio_map;
    static public ViewFlipper VF;
    static ImageView b_settings;
    static public CheckBox cb_send_voice_to_server_main;
    ImageView b_screen_shot,b_back,b_refresh;
    ImageView b_exit,b_about;
    static public int tab_index = 0;
    private EditText bridgeModeEditText;
    static public TextView tv_active_name = null;
    static public CheckBox sw_receive = null;

    public MapViewerView(Context context) {
        super(context);
        try
        {
            init(context);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public MapViewerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        try
        {
            init(context);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public MapViewerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        try
        {
            init(context);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    private void init(Context context) {
        try
        {
            setOrientation(HORIZONTAL);
            setClickable(true);

            inflate(context, R.layout.view_mapviewer, this);
//            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
//            layoutInflater.inflate(R.layout.view_mapviewer, this, true);

            tv_active_name = findViewById(R.id.tv_active_name);
            sw_receive = findViewById(R.id.sw_receive);
            sw_receive.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!sw_receive.isChecked()){
                        Tab_Messenger.close_preview();
                    }
                }
            });
//            sw_show.setVisibility(GONE);

            VF = findViewById(R.id.ViewFlipper01);

            radio_main = findViewById(R.id.radio_main);
            radio_main.setOnClickListener(radio_listener);
            radio_main.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(radio_main.isChecked())    MainActivity.tab_main.toggle_settings();
                    return true;
                }
            });

            radio_messenger = findViewById(R.id.radio_messenger);
            radio_messenger.setOnClickListener(radio_listener);
            radio_messenger.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(radio_messenger.isChecked())    MainActivity.tab_messenger.toggle_settings();
                    return true;
                }
            });

            radio_camera = findViewById(R.id.radio_camera);
            radio_camera.setOnClickListener(radio_listener);
            radio_camera.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(radio_camera.isChecked())    MainActivity.tab_camera.toggle_settings();
                    return true;
                }
            });

            radio_map = findViewById(R.id.radio_map);
            radio_map.setOnClickListener(radio_listener);
            radio_map.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(radio_map.isChecked())    MainActivity.tab_map.toggle_settings();
                    return true;
                }
            });

            b_back = findViewById(R.id.b_back);
            b_back.setOnClickListener(radio_listener);

            b_settings = findViewById(R.id.b_settings);
            b_settings.setOnClickListener(radio_listener);

            cb_send_voice_to_server_main = findViewById(R.id.cb_send_voice_to_server_main);
            cb_send_voice_to_server_main.setVisibility(GONE);
            cb_send_voice_to_server_main.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.activity.check_record_audio_permission();
                    if(cb_send_voice_to_server_main.isChecked()){
                        Tab_Messenger.startRecording();
                    }else{
                        Tab_Messenger.stopRecording(true);
                    }
                }
            });

            b_screen_shot = findViewById(R.id.b_screen_shot);
            b_screen_shot.setOnClickListener(radio_listener);

            b_refresh = findViewById(R.id.b_refresh);
            b_refresh.setOnClickListener(radio_listener);

            b_about = findViewById(R.id.b_about);
            b_about.setOnClickListener(radio_listener);

            b_exit = findViewById(R.id.b_exit);
            b_exit.setOnClickListener(radio_listener);

//            MainActivity.tab_main = (com.oghab.mapviewer.mapviewer.Tab_Main)findViewById(R.id.id_tab_main);
//            MainActivity.tab_map = (com.oghab.mapviewer.mapviewer.Tab_Map)findViewById(R.id.id_tab_map);
//            MainActivity.tab_camera = (com.oghab.mapviewer.mapviewer.Tab_Camera)findViewById(R.id.id_tab_camera);
            MainActivity.tab_camera = new Tab_Camera();
            MainActivity.tab_map = new Tab_Map();
            MainActivity.tab_main = new Tab_Main();
            MainActivity.tab_messenger = new Tab_Messenger();
//            MainActivity.activity.loadFragment(MainActivity.tab_camera,R.id.id_tab_camera);

            // create a FragmentManager
            FragmentManager fm = MainActivity.activity.getSupportFragmentManager();
            // create a FragmentTransaction to begin the transaction and replace the Fragment
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            // replace the FrameLayout with new Fragment
            fragmentTransaction.replace(R.id.id_tab_map, MainActivity.tab_map);
            fragmentTransaction.replace(R.id.id_tab_camera, MainActivity.tab_camera);
            fragmentTransaction.replace(R.id.id_tab_main, MainActivity.tab_main);
            fragmentTransaction.replace(R.id.id_tab_messenger, MainActivity.tab_messenger);
            fragmentTransaction.commit(); // save the changes

            if(!MainActivity.bNavigation)
            {
                MainActivity.activity.checkAndRequestPermissions();
                MainActivity.activity.startSDKRegistration();
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    static public void set_tab(int idx, boolean resend)
    {
        try
        {
            tab_index = idx;
            VF.setDisplayedChild(tab_index);
            switch(tab_index)
            {
                case 3:
                {
                    if(!radio_main.isChecked())  radio_main.setChecked(true);
                    break;
                }
                case 2:
                {
                    if(!radio_messenger.isChecked())  radio_messenger.setChecked(true);
                    break;
                }
                case 1:
                {
                    if(!radio_camera.isChecked())  radio_camera.setChecked(true);
                    break;
                }
                case 0:
                {
                    if(!radio_map.isChecked())  radio_map.setChecked(true);
                    break;
                }
            }

            if(resend) {
                if (((Tab_Camera.sw_broadcast_camera != null) && Tab_Camera.sw_broadcast_camera.isChecked()) || ((Tab_Map.sw_broadcast_map != null) && Tab_Map.sw_broadcast_map.isChecked())) {
                    try {
                        Tab_Messenger.sendMessage("TAB:" + idx, true);
                        MainActivity.hide_keyboard(null);
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                }
            }

            Tab_Messenger.close_preview();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    static public void refresh(){
        try
        {
//            MainActivity.activity.moveTaskToBack(true);
//            MainActivity.activity.moveTaskToBack(false);

            if (MApplication.isRealDevice()) {
                if (ModuleVerificationUtil.isGimbalModuleAvailable()) {
                    MApplication.getProductInstance().getGimbal().setStateCallback(null);
                }
            }

//            if(MainActivity.tab_camera.cameraView != null)  MainActivity.tab_camera.cameraView.close();

            MainActivity.activity.checkAndRequestPermissions();

            boolean bPrev = MainActivity.bDJIExists;

            MainActivity.bDJIExists = true;
            MainActivity.tab_camera.update_camera();

            MainActivity.bDJIExists = false;
            MainActivity.tab_camera.update_camera();

            MainActivity.bDJIExists = bPrev;
            MainActivity.tab_camera.update_camera();

//                            MainActivity.tab_camera.initPreviewerTextureView();// AliSoft 2019.03.05
            if(MainActivity.tab_camera != null) {
                if (MApplication.isRealDevice()) {
                    MainActivity.tab_camera.notifyStatusChange();// AliSoft 2019.03.05
                }

                MainActivity.tab_camera.update_camera();
            }

            if(!MainActivity.bNavigation)
            {
                if (MApplication.isRealDevice()) {
                    MainActivity.activity.checkAndRequestPermissions();
                    MainActivity.activity.startSDKRegistration();
                }
            }

            MainActivity.tab_map.mLocationOverlay.enableMyLocation();
            MainActivity.set_fullscreen();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    static public void process_click(int res_id){
        MainActivity.activity.runOnUiThread(() -> {
            try {
                switch (res_id) {
                    case R.id.radio_map:
                        set_tab(0, true);
                        break;
                    case R.id.radio_camera:
                        set_tab(1, true);
                        break;
                    case R.id.radio_messenger:
                        set_tab(2, true);
                        break;
                    case R.id.radio_main:
                        set_tab(3, true);
                        break;
                    case R.id.b_back: {
                        try
                        {
                            MainActivity.activity.init();
                            MainActivity.set_fullscreen();
                        }
                        catch (Throwable ex)
                        {
                            MainActivity.MyLog(ex);
                        }
                        break;
                    }
                    case R.id.b_settings: {
                        try
                        {
                            if(radio_camera.isChecked())
                                MainActivity.tab_camera.toggle_settings();
                            else
                            if(radio_map.isChecked())
                                MainActivity.tab_map.toggle_settings();
                            else
                            if(radio_messenger.isChecked())
                                MainActivity.tab_messenger.toggle_settings();
                            else
                            if(radio_main.isChecked())
                                MainActivity.tab_main.toggle_settings();
                            MainActivity.set_fullscreen();
                        }
                        catch (Throwable ex)
                        {
                            MainActivity.MyLog(ex);
                        }
                        break;
                    }
                    case R.id.b_screen_shot: {
                        try
                        {
                            Bitmap bmp = ScreenUtils.screenShot(MainActivity.activity);
                            FileHelper.save_image_as_jpg(new File(MainActivity.strScreenShot), bmp);
                            Tab_Messenger.sendFile(MainActivity.strScreenShot, true, "ScreenShot sending...", true, new tcp_io_handler.SendCallback() {
                                @Override
                                public void onFinish(int error) {
                                    if(error != tcp_io_handler.TCP_OK) {
                                        if (MainActivity.IsDebugJNI()) {
                                            MainActivity.MyLogInfoSilent("The Current Line Number is " + Arrays.toString(new Throwable().getStackTrace()));
                                        }
                                    }
                                    Tab_Messenger.showToast("ScreenShot Sent...");
                                    MapViewerView.process_click(R.id.radio_messenger);
                                }
                            });
                        }
                        catch (Throwable ex)
                        {
                            MainActivity.MyLog(ex);
                        }
                        break;
                    }
                    case R.id.b_refresh: {
                        try
                        {
                            refresh();
                        }
                        catch (Throwable ex)
                        {
                            MainActivity.MyLog(ex);
                        }
                        break;
                    }
                    case R.id.b_about: {
                        try
                        {
                            showHelpDialog();
                        }
                        catch (Throwable ex)
                        {
                            MainActivity.MyLog(ex);
                        }
                        break;
                    }
                    case R.id.b_exit: {
                        try
                        {
                            MainActivity.activity.exit_app();
                        }
                        catch (Throwable ex)
                        {
                            MainActivity.MyLog(ex);
                        }
                        break;
                    }
                }
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        });
    }

    /*
     * Define a OnClickListener that will change which view that is displayed by
     * the ViewFlipper
     */
    private View.OnClickListener radio_listener = new View.OnClickListener() {
        public void onClick(View v) {
            try
            {
                process_click(v.getId());
            }
            catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
            }
        }
    };

    public static void copy_file(String oldFile,String newFile) throws IOException {
        try
        {
            File src = new File(oldFile);
            File dst = new File(newFile);
            try (InputStream in = new FileInputStream(src)) {
                try (OutputStream out = new FileOutputStream(dst)) {
                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        try
        {
            super.onAttachedToWindow();
//            MApplication.getEventBus().post(new MainActivity.RequestStartFullScreenEvent());
            if(!MainActivity.bNavigation)
            {
                MainActivity.activity.checkAndRequestPermissions();
                MainActivity.activity.startSDKRegistration();
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        try
        {
//            MApplication.getEventBus().post(new MainActivity.RequestEndFullScreenEvent());
            if(MApplication.isRealDevice()) DJISDKManager.getInstance().destroy();
            super.onDetachedFromWindow();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

//    private void handleBridgeIPTextChange() {
//        try
//        {
//            // the user is done typing.
//            final String bridgeIP = bridgeModeEditText.getText().toString();
//
//            if (!TextUtils.isEmpty(bridgeIP)) {
//                if(MainActivity.isRealDevice()) DJISDKManager.getInstance().enableBridgeModeWithBridgeAppIP(bridgeIP);
//                Tab_Messenger.showToast("BridgeMode ON!\nIP: " + bridgeIP);
////                Toast.makeText(getApplicationContext(),"BridgeMode ON!\nIP: " + bridgeIP,Toast.LENGTH_SHORT).show();
//                PreferenceManager.getDefaultSharedPreferences(MainActivity.ctx).edit().putString(LAST_USED_BRIDGE_IP,bridgeIP).apply();
//            }
//        }
//        catch (Throwable ex)
//        {
//            MainActivity.MyLog(ex);
//        }
//    }

    static private void showHelpDialog(){
        try
        {
            LinearLayout helpLayout = (LinearLayout)MainActivity.activity.getLayoutInflater().inflate(R.layout.view_mapviewer_help, null);

            ImageButton ib_oghab_mapviewer =  helpLayout.findViewById(R.id.ib_oghab_mapviewer);
            ib_oghab_mapviewer.setOnClickListener(v -> {
//                Intent i = new Intent();
//                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                i.setData(Uri.parse("package:" + MainActivity.activity.getPackageName()));
//                i.addCategory(Intent.CATEGORY_DEFAULT);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                MainActivity.activity.startActivity(i);
            });

            Button b_activate_sdk = helpLayout.findViewById(R.id.b_activate_sdk);
            TextView versionText = helpLayout.findViewById(R.id.version);
            TextView abiText = helpLayout.findViewById(R.id.os_abi);
            TextView sdk_version = helpLayout.findViewById(R.id.sdk_version);
            TextView mapviewer_version = helpLayout.findViewById(R.id.mapviewer_version);
            String ABI = "ABIs"+Arrays.toString(Build.SUPPORTED_ABIS);
            abiText.setText(ABI);

            sdk_version.setText("SDK Version: [Android " + DeviceUtils.getSDKVersionName() + "], [API " + DeviceUtils.getSDKVersionCode() + "]");

            String strText = "";
            if(!MApplication.isRealDevice()) strText += "Emulator";
            if (MainActivity.IsDemoVersionJNI())
                strText += "[Demo]";
            else
                strText += "[Registered]";
            mapviewer_version.setText(strText);

//            bridgeModeEditText = LimitsSettings.findViewById(R.id.edittext_bridge_ip);
            if(!MainActivity.bNavigation) {
                b_activate_sdk.setVisibility(VISIBLE);
                versionText.setVisibility(VISIBLE);
//                bridgeModeEditText.setVisibility(VISIBLE);

                b_activate_sdk.setOnClickListener(v -> {
                    if(!MainActivity.bNavigation)
                    {
                        MainActivity.activity.checkAndRequestPermissions();
                        MainActivity.activity.startSDKRegistration();
                    }
                });

                try {
//                    if(MainActivity.isRealDevice()) versionText.setText(getResources().getString(R.string.sdk_version, DJISDKManager.getInstance().getSDKVersion()));
                    if(MApplication.isRealDevice()) versionText.setText(String.format(Locale.ENGLISH,"DJI Mobile SDK Version: %1$s",DJISDKManager.getInstance().getSDKVersion()));
                } catch (Throwable ex) {
                    MainActivity.MyLog(ex);
                }

//            try
//            {
//                bridgeModeEditText = LimitsSettings.findViewById(R.id.edittext_bridge_ip);
//                bridgeModeEditText.setText(PreferenceManager.getDefaultSharedPreferences(MainActivity.ctx).getString(LAST_USED_BRIDGE_IP,""));
//                bridgeModeEditText.setOnEditorActionListener((v, actionId, event) -> {
//                    try
//                    {
//                        if (actionId == EditorInfo.IME_ACTION_SEARCH
//                                || actionId == EditorInfo.IME_ACTION_DONE
//                                || event != null
//                                && event.getAction() == KeyEvent.ACTION_DOWN
//                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                            if (event != null && event.isShiftPressed()) {
//                                return false;
//                            } else {
//                                // the user is done typing.
//                                handleBridgeIPTextChange();
//                            }
//                        }
//                    }
//                    catch (Throwable ex)
//                    {
//                        MainActivity.MyLog(ex);
//                    }
//                    return false; // pass on to other listeners.
//                });
//                bridgeModeEditText.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//                        try
//                        {
//                            if (s != null && s.toString().contains("\n")) {
//                                // the user is done typing.
//                                // remove new line characcter
//                                final String currentText = bridgeModeEditText.getText().toString();
//                                bridgeModeEditText.setText(currentText.substring(0, currentText.indexOf('\n')));
//                                handleBridgeIPTextChange();
//                            }
//                        }
//                        catch (Throwable ex)
//                        {
//                            MainActivity.MyLog(ex);
//                        }
//                    }
//                });
//            }
//            catch (Throwable ex)
//            {
//                MainActivity.MyLog(ex);
//            }
            }
            else
            {
                b_activate_sdk.setVisibility(GONE);
                versionText.setVisibility(GONE);
//                bridgeModeEditText.setVisibility(GONE);
            }

            new AlertDialog.Builder(MainActivity.activity, R.style.CustomAlertDialog)
//            new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.activity, R.style.AppTheme_NoActionBar))
//            new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.activity, R.style.Theme_AppCompat_NoActionBar))
//            new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.activity, R.style.AppTheme_Dark_NoActionBar))
//                    .setTitle(getResources().getString(R.string.help))
                    .setCancelable(false)
                    .setView(helpLayout)
                    .setPositiveButton(MainActivity.ctx.getString(R.string.ok), (dialog, id) -> {
                        try
                        {
                            // Hide both the navigation bar and the status bar.
                            MainActivity.hide_keyboard(helpLayout);

                            dialog.dismiss();
                        }
                        catch (Throwable ex)
                        {
                            MainActivity.MyLog(ex);
                        }
                    })
//                    .setNegativeButton(MainActivity.ctx.getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            try
//                            {
//                                dialog.cancel();
//                            }
//                            catch (Throwable ex)
//                            {
//                                MainActivity.MyLog(ex);
//                            }
//                        }
//
//                    })
                    .create()
                    .show();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    static public void showMessageDialog(){
        try
        {
            LinearLayout helpLayout = (LinearLayout)MainActivity.activity.getLayoutInflater().inflate(R.layout.view_mapviewer_help, null);

            ImageButton ib_oghab_mapviewer =  helpLayout.findViewById(R.id.ib_oghab_mapviewer);
            ib_oghab_mapviewer.setOnClickListener(v -> {
//                Intent i = new Intent();
//                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                i.setData(Uri.parse("package:" + MainActivity.activity.getPackageName()));
//                i.addCategory(Intent.CATEGORY_DEFAULT);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                MainActivity.activity.startActivity(i);
            });

            Button b_activate_sdk = helpLayout.findViewById(R.id.b_activate_sdk);
            TextView versionText = helpLayout.findViewById(R.id.version);
            TextView abiText = helpLayout.findViewById(R.id.os_abi);
            TextView sdk_version = helpLayout.findViewById(R.id.sdk_version);
            TextView mapviewer_version = helpLayout.findViewById(R.id.mapviewer_version);
            String ABI = "ABIs"+Arrays.toString(Build.SUPPORTED_ABIS);
            abiText.setText(ABI);

            sdk_version.setText("SDK Version: [Android " + DeviceUtils.getSDKVersionName() + "], [API " + DeviceUtils.getSDKVersionCode() + "]");

            String strText = "";
            if(!MApplication.isRealDevice()) strText += "Emulator";
            if (MainActivity.IsDemoVersionJNI())
                strText += "[Demo]";
            else
                strText += "[Registered]";
            mapviewer_version.setText(strText);

//            bridgeModeEditText = LimitsSettings.findViewById(R.id.edittext_bridge_ip);
            if(!MainActivity.bNavigation) {
                b_activate_sdk.setVisibility(VISIBLE);
                versionText.setVisibility(VISIBLE);
//                bridgeModeEditText.setVisibility(VISIBLE);

                b_activate_sdk.setOnClickListener(v -> {
                    if(!MainActivity.bNavigation)
                    {
                        MainActivity.activity.checkAndRequestPermissions();
                        MainActivity.activity.startSDKRegistration();
                    }
                });

                try {
//                    if(MainActivity.isRealDevice()) versionText.setText(getResources().getString(R.string.sdk_version, DJISDKManager.getInstance().getSDKVersion()));
                    if(MApplication.isRealDevice()) versionText.setText(String.format(Locale.ENGLISH,"Version: %1$s",DJISDKManager.getInstance().getSDKVersion()));
                } catch (Throwable ex) {
                    MainActivity.MyLog(ex);
                }
            }
            else
            {
                b_activate_sdk.setVisibility(GONE);
                versionText.setVisibility(GONE);
//                bridgeModeEditText.setVisibility(GONE);
            }

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.activity, R.style.CustomAlertDialog)
                .setCancelable(false)
                .setView(helpLayout)
                .setPositiveButton(MainActivity.ctx.getString(R.string.ok), (dialog, id) -> {
                    try
                    {
                        // Hide both the navigation bar and the status bar.
                        MainActivity.hide_keyboard(helpLayout);

                        dialog.dismiss();
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                })
                .create();
            alertDialog.show();
//            alertDialog.cancel();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }
}
