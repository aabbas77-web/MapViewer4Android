package com.oghab.mapviewer.mapviewer;

/**
 * @author Ali Abbas
 */

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.imageview.ShapeableImageView;
import com.maltaisn.icondialog.IconDialog;
import com.maltaisn.icondialog.IconDialogSettings;
import com.mohammedalaa.valuecounterlib.ValueCounterView;
import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;
import com.oghab.mapviewer.bonuspack.clustering.RadiusMarkerClusterer;
import com.oghab.mapviewer.bonuspack.kml.StyleSelector;
import com.oghab.mapviewer.utils.FileHelper;
import com.oghab.mapviewer.utils.FileUtil;
import com.oghab.mapviewer.utils.mv_utils;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import com.oghab.mapviewer.bonuspack.kml.KmlDocument;
import com.oghab.mapviewer.bonuspack.kml.KmlLineString;
import com.oghab.mapviewer.bonuspack.kml.KmlPlacemark;
import com.oghab.mapviewer.bonuspack.kml.KmlPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Overlay;

import com.oghab.mapviewer.mapviewer.MyMarker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;

//import ir.androidexception.filepicker.utility.Util;

/**
 * Created by MapViewer on 27/03/2017.
 */

public class Tab_Messenger extends Fragment implements View.OnClickListener {
    private static final String TAG = "Messenger";

//    static public tcp_server server = new tcp_server();
//    static public tcp_client client = new tcp_client();

    static public ShapeableImageView b_connect, b_disconnect;
    static public EditText et_server_ip;
    static public EditText et_server_port;
    static public EditText et_server_name, et_server_phone_number, et_connect_timeout;
    static public Button b_clear, b_clear2;
    static public ShapeableImageView b_send_to_server, b_send_location_to_server, b_send_file_to_server, b_active_connections, b_users_refresh, b_call, b_call_server, b_send_buzz_to_server;
    static public LinearLayout ll_user_list;
//    static public ShapeableImageView b_send_voice_to_server_messenger;
    static public CheckBox cb_send_voice_to_server,cb_user_list;
    static public EditText et_message;
    static public TextView tv_server_ip, tv_server_name, tv_server_phone_number, tv_status, tv_ip_phone_number, tv_connect_timeout;
    static public LinearLayout layout_sys_id,ll_connection_timeout,ll_server_port;
    static public CheckBox sw_listen, sw_this_range_only, sw_audio_auto_play, sw_received_location_as_gps, sw_encode;
    static public LinearLayout ll_messages = null;
    static public RecyclerView rv_ips = null;
    static private LinearLayout ll_settings, ll_user_settings;
    static private LinearLayout main_layout, ll_phone_number;
    static private LinearLayout ll_send_to_server;
    ShapeableImageView b_avatar, b_delete_user, b_delete_all, b_import_ips, b_export_ips;
    static ValueCounterView pageCounter;
    private HorizontalScrollView hsv_settings, hsv_status;
    LinearLayout ll_messages_view;
    TextView tv_version_status;

    static public String active_ip = null;
    static private MediaRecorder mRecorder = null;
    static int connect_timeout = 1000;
    static boolean isListen = true;

    public static native int tcp_server_listen(int port);
    public static native int tcp_server_stop();

    public static native int tcp_client_connect(String ip, int port);
    public static native int tcp_client_stop();

    public static native void set_jni_callbacks(tcp_callbacks callbacks_object);
    public static native void remove_jni_callbacks();
    public static native void JNIMethodWithParameter(tcp_callbacks mainJavaClass, int param);

    public static class tcp_callbacks{
        void javaMethodTobeCalledInJNI(){
            try {
                Tab_Messenger.showToast("Hi From C++");
            }
            catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
            }
        }
        void javaMethodTobeCalledInJNIWithParameter(int param){
            try {
                Tab_Messenger.showToast("Hi From C++, ["+param+"]");
            }
            catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
            }
        }
        void addError(String msg){
            try {
                Tab_Messenger.addError(msg);
            }
            catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        set_jni_callbacks(callbacks_object);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * Inflate the layout for this fragment
         */
        View view = null;
        try {
            view = inflater.inflate(R.layout.tab_messenger, container, false);
            init(view);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return view;
    }

    static public boolean is_audio_recording = false;
    static public void startRecording() {
        try {
            if(is_audio_recording)  stopRecording(false);

            is_audio_recording = false;
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mRecorder.setOutputFile(MainActivity.strVoice);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.prepare();
            mRecorder.start();
            is_audio_recording = true;
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        if(!is_audio_recording){
            try{
                cb_send_voice_to_server.setChecked(false);
                if(MapViewerView.cb_send_voice_to_server_main != null)  MapViewerView.cb_send_voice_to_server_main.setChecked(false);
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
            return;
        }

        try {
            recording_time = 0;
            myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    MainActivity.activity.runOnUiThread(() -> {
                        try {
                            recording_time += recording_step;
                            showStatus(String.format(Locale.ENGLISH, "Recording %.3f Seconds.", recording_time/1000.0), Color.GREEN);
                            if(recording_time >= 10000) {
                                if(is_audio_recording)  stopRecording(true);
                            }
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    });
                }
            }, 100, recording_step);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }

        try{
            cb_send_voice_to_server.setChecked(true);
            if(MapViewerView.cb_send_voice_to_server_main != null)  MapViewerView.cb_send_voice_to_server_main.setChecked(true);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public void stopRecording(boolean bSend) {
        if(!is_audio_recording) return;
        try {
            if(myTimer != null) {
                myTimer.cancel();
                myTimer = null;
            }
        } catch (Throwable ex) {
            myTimer = null;
            MainActivity.MyLog(ex);
        }

        try {
            if(mRecorder != null) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }
        } catch (Throwable ex) {
            mRecorder = null;
            MainActivity.MyLog(ex);
        }

        try {
            if(bSend) {
                if (recording_time >= 200) {
                    MainActivity.activity.send_voice_to_server();
                } else {
                    addError("Short Recording not sent!");
                }
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }

        try{
            cb_send_voice_to_server.setChecked(false);
            if(MapViewerView.cb_send_voice_to_server_main != null)  MapViewerView.cb_send_voice_to_server_main.setChecked(false);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public void close_preview(){
        try{
            if(Tab_Camera.iv_preview == null)   return;
            Tab_Camera.iv_preview.setVisibility(View.GONE);
            File file = new File(Tab_Camera.iv_preview.get_filename());
            if(file.exists()){
                if(!file.delete()){
                    addError("Couldn't delete file["+Tab_Camera.iv_preview.get_filename()+"]");
                }
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
   }

//   static public View.OnTouchListener onVoiceTouchListener = null;

    static public Timer myTimer = null;
    static public int recording_time = 0;
    static public int recording_step = 100;
    private void init(View view) {
        try {
            hsv_settings = view.findViewById(R.id.hsv_settings);
            MainActivity.set_view_visibility(hsv_settings,!MainActivity.IsDemoVersionJNI());

            hsv_status = view.findViewById(R.id.hsv_status);
            MainActivity.set_view_visibility(hsv_status,!MainActivity.IsDemoVersionJNI());

            ll_messages_view = view.findViewById(R.id.ll_messages_view);
            MainActivity.set_view_visibility(ll_messages_view,!MainActivity.IsDemoVersionJNI());

            tv_version_status = view.findViewById(R.id.tv_version_status);
            MainActivity.set_view_visibility(tv_version_status,MainActivity.IsDemoVersionJNI());
            tv_version_status.setText(R.string.demo_version);

            main_layout = view.findViewById(R.id.main_layout);

            ll_send_to_server = view.findViewById(R.id.ll_send_to_server);
            ll_send_to_server.setVisibility(View.GONE);

            tv_server_ip = view.findViewById(R.id.tv_server_ip);
            tv_server_ip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Tab_Messenger.connect_with_waiting(tv_server_ip.getText().toString());
                    MainActivity.hide_keyboard(null);
                }
            });

            tv_ip_phone_number = view.findViewById(R.id.tv_ip_phone_number);
            ll_phone_number = view.findViewById(R.id.ll_phone_number);
            if(tv_ip_phone_number.getText().toString().isEmpty())
                ll_phone_number.setVisibility(View.GONE);
            else
                ll_phone_number.setVisibility(View.VISIBLE);

            tv_server_name = view.findViewById(R.id.tv_server_name);
            tv_server_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String name = et_server_name.getText().toString();
                        update_ip_name(active_ip, name, true);
                        int idx = get_ip_index(active_ip);
                        if(idx >= 0){
                            tcp_user user = users.get(idx);
                            user.setName(name);
                            MainActivity.activity.runOnUiThread(() -> {
                                try {
                                    ipAdapter.notifyItemChanged(idx);
                                } catch (Throwable ex) {
                                    MainActivity.MyLog(ex);
                                }
                            });
                        }
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                }
            });

            tv_server_phone_number = view.findViewById(R.id.tv_server_phone_number);
            tv_server_phone_number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String phone_number = et_server_phone_number.getText().toString();
                        update_ip_phone_number(active_ip, phone_number, true);
                        int idx = get_ip_index(active_ip);
                        if(idx >= 0){
                            tcp_user user = users.get(idx);
                            user.setPhoneNumber(phone_number);
                            MainActivity.activity.runOnUiThread(() -> {
                                try {
                                    ipAdapter.notifyItemChanged(idx);
                                } catch (Throwable ex) {
                                    MainActivity.MyLog(ex);
                                }
                            });
                        }
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                }
            });

            sw_this_range_only = view.findViewById(R.id.sw_this_range_only);
            sw_this_range_only.setOnClickListener(this);

            sw_audio_auto_play = view.findViewById(R.id.sw_audio_auto_play);
            sw_audio_auto_play.setOnClickListener(this);

            sw_received_location_as_gps = view.findViewById(R.id.sw_received_location_as_gps);
            sw_received_location_as_gps.setOnClickListener(this);

            sw_encode = view.findViewById(R.id.sw_encode);
            sw_encode.setOnClickListener(this);
            sw_encode.setChecked(MainActivity.is_encoded);
            if(MainActivity.isDevelpoment())
                sw_encode.setVisibility(View.VISIBLE);
            else
                sw_encode.setVisibility(View.GONE);

            layout_sys_id = view.findViewById(R.id.layout_sys_id);
            layout_sys_id.setVisibility(View.VISIBLE);

            pageCounter = view.findViewById(R.id.pageCounter);
            pageCounter.setOnMyClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pageCounter.setMinValue(1);
                    pageCounter.setMaxValue(10000);
                    showServerMessages(active_ip);
                }
            });
            pageCounter.setOnResetListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pageCounter.setMinValue(1);
                    pageCounter.setMaxValue(10000);
                    pageCounter.setValue(1);
                    showServerMessages(active_ip);
                }
            });

            et_server_ip = view.findViewById(R.id.et_server_ip);
            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end,
                                           android.text.Spanned dest, int dstart, int dend) {
                    if (end > start) {
                        String destTxt = dest.toString();
                        String resultingTxt = destTxt.substring(0, dstart)
                                + source.subSequence(start, end)
                                + destTxt.substring(dend);
                        if (!resultingTxt
                                .matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                            return "";
                        } else {
                            String[] splits = resultingTxt.split("\\.");
                            for (int i = 0; i < splits.length; i++) {
                                if (Integer.valueOf(splits[i]) > 255) {
                                    return "";
                                }
                            }
                        }
                    }
                    return null;
                }

            };
            et_server_ip.setFilters(filters);

            et_server_port = view.findViewById(R.id.et_server_port);
            et_server_port.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        try {
                            SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt("nPort", Integer.parseInt(et_server_port.getText().toString()));
                            editor.apply();
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                        return true;
                    }
                    return false;
                }
            });

            et_server_name = view.findViewById(R.id.et_server_name);
            et_server_phone_number = view.findViewById(R.id.et_server_phone_number);

            et_connect_timeout = view.findViewById(R.id.et_connect_timeout);

            tv_connect_timeout = view.findViewById(R.id.tv_connect_timeout);
            tv_connect_timeout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String timeout = et_connect_timeout.getText().toString();
                        connect_timeout = mv_utils.parseInt(timeout);

                        SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt("connect_timeout", connect_timeout);
                        editor.apply();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                }
            });

            ll_connection_timeout = view.findViewById(R.id.ll_connection_timeout);
            if(MainActivity.isDevelpoment())
                ll_connection_timeout.setVisibility(View.VISIBLE);
            else
                ll_connection_timeout.setVisibility(View.GONE);

            ll_server_port = view.findViewById(R.id.ll_server_port);
            if(MainActivity.isDevelpoment())
                ll_server_port.setVisibility(View.VISIBLE);
            else
                ll_server_port.setVisibility(View.GONE);

            b_avatar = view.findViewById(R.id.b_avatar);
            b_avatar.setOnClickListener(this);

            b_delete_user = view.findViewById(R.id.b_delete_user);
            b_delete_user.setOnClickListener(this);

            b_delete_all = view.findViewById(R.id.b_delete_all);
            b_delete_all.setOnClickListener(this);

            b_import_ips = view.findViewById(R.id.b_import_ips);
            b_import_ips.setOnClickListener(this);

            b_export_ips = view.findViewById(R.id.b_export_ips);
            b_export_ips.setOnClickListener(this);

            et_message = view.findViewById(R.id.et_message);
            et_message.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        try {
                            MainActivity.client.ioHandler.sendMessage(et_message.getText().toString(), false);
                            et_message.setText("");
                            MainActivity.hide_keyboard(null);
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                        return true;
                    }
                    return false;
                }
            });

            tv_status = view.findViewById(R.id.tv_status);
            tv_status.setOnClickListener(this);

            ll_messages = view.findViewById(R.id.ll_messages);
            ll_messages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.set_fullscreen();
                }
            });

            b_connect = view.findViewById(R.id.b_connect);
            b_connect.setOnClickListener(this);

            b_disconnect = view.findViewById(R.id.b_disconnect);
            b_disconnect.setOnClickListener(this);

            b_active_connections = view.findViewById(R.id.b_active_connections);
            b_active_connections.setOnClickListener(this);

            b_users_refresh = view.findViewById(R.id.b_users_refresh);
            b_users_refresh.setOnClickListener(this);

            b_send_to_server = view.findViewById(R.id.b_send_to_server);
            b_send_to_server.setOnClickListener(this);

            b_send_location_to_server = view.findViewById(R.id.b_send_location_to_server);
            b_send_location_to_server.setOnClickListener(this);
            if(MainActivity.bNavigation)
                b_send_location_to_server.setVisibility(View.VISIBLE);
            else
                b_send_location_to_server.setVisibility(View.GONE);

            b_send_buzz_to_server = view.findViewById(R.id.b_send_buzz_to_server);
            b_send_buzz_to_server.setOnClickListener(this);

            b_send_file_to_server = view.findViewById(R.id.b_send_file_to_server);
            b_send_file_to_server.setOnClickListener(this);

//            onVoiceTouchListener = new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        startRecording();
//                        return true;
//                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                        stopRecording(true);
//                        return true;
//                    }
//                    return false;
//                }
//            };
//
//            b_send_voice_to_server_messenger = view.findViewById(R.id.siv_send_voice_to_server_messenger);
//            b_send_voice_to_server_messenger.setOnTouchListener(onVoiceTouchListener);

            cb_send_voice_to_server = view.findViewById(R.id.cb_send_voice_to_server);
            cb_send_voice_to_server.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.activity.check_record_audio_permission();
                    if(cb_send_voice_to_server.isChecked()){
                        startRecording();
                    }else{
                        if(is_audio_recording)  stopRecording(true);
                    }
                }
            });

            b_call = view.findViewById(R.id.b_call);
            b_call.setOnClickListener(this);

            b_call_server = view.findViewById(R.id.b_call_server);
            b_call_server.setOnClickListener(this);

            cb_user_list = view.findViewById(R.id.cb_user_list);
            cb_user_list.setOnClickListener(this);

            ll_user_list = view.findViewById(R.id.ll_user_list);
            ll_user_list.setOnClickListener(this);
            ll_user_list.setVisibility(View.GONE);
            if(cb_user_list.isChecked())
                ll_user_list.setVisibility(View.VISIBLE);
            else
                ll_user_list.setVisibility(View.GONE);

            b_clear = view.findViewById(R.id.b_clear);
            b_clear.setOnClickListener(this);

            b_clear2 = view.findViewById(R.id.b_clear2);
            b_clear2.setOnClickListener(this);

            rv_ips = view.findViewById(R.id.rv_ips);
            rv_ips.setFocusable(true);
            rv_ips.setFocusableInTouchMode(true);

            ll_settings = view.findViewById(R.id.ll_settings);
            ll_settings.setVisibility(View.GONE);

            ll_user_settings = view.findViewById(R.id.ll_user_settings);
            ll_user_settings.setVisibility(View.GONE);

            users = new ArrayList<>();
            ipAdapter = new IPsAdapter(R.layout.simple_ip_row, users);
            llManager = new LinearLayoutManager(MainActivity.ctx);
            rv_ips.setLayoutManager(llManager);
            rv_ips.setItemAnimator(new DefaultItemAnimator());
            rv_ips.setAdapter(ipAdapter);
            rv_ips.invalidate();

            SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
            String strIP = settings.getString("strIP", "133.100.100.100");
            String strPort = Integer.toString(settings.getInt("nPort", 6000));
            isListen = settings.getBoolean("isListen", isListen);
            boolean isAudioAutoPlay = settings.getBoolean("isAudioAutoPlay", true);
            boolean isReceivedLocationAsGPS = settings.getBoolean("isReceivedLocationAsGPS", false);
            connect_timeout = settings.getInt("connect_timeout", 1000);
            et_server_port.setText(strPort);
            et_connect_timeout.setText(String.valueOf(connect_timeout));
            sw_audio_auto_play.setChecked(isAudioAutoPlay);
            sw_received_location_as_gps.setChecked(isReceivedLocationAsGPS);

            sw_listen = view.findViewById(R.id.sw_listen);
            sw_listen.setOnClickListener(this);
            sw_listen.setChecked(isListen);

            try
            {
                // If dialog is already added to fragment manager, get it. If not, create a new instance.
                IconDialog dialog = (IconDialog) MainActivity.activity.getSupportFragmentManager().findFragmentByTag(MainActivity.ICON_DIALOG_TAG);
                IconDialog iconDialog = dialog != null ? dialog
                        : IconDialog.newInstance(new IconDialogSettings.Builder().build());

                ShapeableImageView btn = view.findViewById(R.id.btn_open_dialog);
                btn.setOnClickListener(v -> {
                    try
                    {
                        // Open icon dialog
                        iconDialog.show(MainActivity.activity.getSupportFragmentManager(), MainActivity.ICON_DIALOG_TAG);
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                });
                MainActivity.set_fullscreen();
            }
            catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
            }

            MainActivity.activity.runOnUiThread(() -> {
                try
                {
                    tcp_server.server_ip = Tab_Messenger.getIPAddress(true);
                    if(Patterns.IP_ADDRESS.matcher(tcp_server.server_ip).matches()) {
                        Tab_Messenger.tv_server_ip.setText(tcp_server.server_ip);
                        Tab_Messenger.et_server_ip.setText(tcp_server.server_ip);
                        Tab_Messenger.et_server_name.setText(Tab_Messenger.get_ip_name(tcp_server.server_ip));
                        Tab_Messenger.et_server_phone_number.setText(Tab_Messenger.get_ip_phone_number(tcp_server.server_ip));
                        Tab_Messenger.create_ip_folder(tcp_server.server_ip, false);
                        Tab_Messenger.update_ip_list();

                        Bundle bundle = MainActivity.activity.getIntent().getExtras();
                        if (bundle != null) {
                            //bundle contains all info of "data" field of the notification
                            String ip = bundle.getString("notification_ip");
                            if(ip  != null){
                                Tab_Messenger.showToast("From ip: "+ip);
                                MapViewerView.process_click(R.id.radio_messenger);
                                Tab_Messenger.connect_with_waiting(ip);
                            }else{
                                Tab_Messenger.connect_with_waiting(tcp_server.server_ip);
                            }
                        }else{
                            Tab_Messenger.connect_with_waiting(tcp_server.server_ip);
                        }
                    }else{
                        terminated = true;
                        Tab_Messenger.addError("Invalid IP Address: "+tcp_server.server_ip);
                    }
                }
                catch (Throwable ex)
                {
                    terminated = true;
                    MainActivity.MyLog(ex);
                }
            });

            MainActivity.check_record_audio_permission();


//            if (isListen) {
//                if(!MainActivity.IsDemoVersionJNI()) {
//                    MainActivity.server.start(MainActivity.def_server_port);
//                }
//            } else {
//                MainActivity.server.stop();
//            }

//            if(!terminated){
//                MainActivity.activity.runOnUiThread(() -> {
//                    try
//                    {
//                        if(MainActivity.server.serverHandler != null){
//                            if(tcp_utils.is_server_socket_connected(MainActivity.server.serverHandler.serverSocket)){
////                            mv_utils.playResource(MainActivity.ctx, R.raw.connect);
//                                if(Tab_Messenger.sw_listen != null){
//                                    Tab_Messenger.sw_listen.setChecked(true);
//
////                            SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
//                                    SharedPreferences.Editor editor = settings.edit();
//                                    editor.putBoolean("isListen", Tab_Messenger.sw_listen.isChecked());
//                                    editor.apply();
//                                }
//                            }
//                        }
//                    }
//                    catch (Throwable ex)
//                    {
//                        terminated = true;
//                        MainActivity.MyLog(ex);
//                    }
//                });
//            }

//            server = new tcp_server();
//            client = new tcp_client();
//            if(sw_listen.isChecked())
//            {
//                if(!MainActivity.IsDemoVersionJNI()) {
//                    MainActivity.server.start(mv_utils.parseInt(et_server_port.getText().toString()));
//                }
//            }

            MainActivity.check_sms_permission();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }
    static private final tcp_callbacks callbacks_object = new tcp_callbacks();

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

    //    static public ArrayAdapter<String> dataAdapter;
    static public ArrayList<tcp_user> users;
    static public IPsAdapter ipAdapter = null;
    static public LinearLayoutManager llManager = null;

    static public void showStatus(String msg, int color) {
        if(msg == null) return;
        MainActivity.activity.runOnUiThread(() -> {
            try {
                if(tv_status != null) {
                    tv_status.setText(msg);
                    tv_status.setTextColor(color);
                }
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        });
    }

    static public void clearMessages() {
        MainActivity.activity.runOnUiThread(() -> {
            try {
                if((Tab_Messenger.ll_messages != null) && (ll_user_settings != null)) {
                    Tab_Messenger.ll_messages.removeAllViews();
                    ll_user_settings.setVisibility(View.GONE);
                }
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        });
    }

    static public void showServerMessages(String ip) {
        MainActivity.activity.runOnUiThread(() -> {
            try {
                active_ip = ip;
                String name = get_ip_name(active_ip);
                if(et_server_name != null)  et_server_name.setText(name);
                if(et_server_phone_number != null)  et_server_phone_number.setText(get_ip_phone_number(active_ip));
                if(MapViewerView.tv_active_name != null)    MapViewerView.tv_active_name.setText(name);
                String strDir = MainActivity.strTCPPath+ip+File.separator;
                File dir = new File(strDir);
                if (!dir.exists()){
                    if(!dir.mkdirs()){
                        MainActivity.MyLogInfo(strDir + " not created...");
                    }
                }

                File path = new File(strDir);
                final File[] sortedByFileName = path.listFiles();
                if ((sortedByFileName != null) && (sortedByFileName.length > 1)) {
                    Arrays.sort(sortedByFileName, new Comparator<File>() {
                        @Override
                        public int compare(File object1, File object2) {
                            return object1.getName().compareTo(object2.getName());
                        }
                    });

                    List<File> files = new ArrayList<>(Arrays.asList(sortedByFileName));
//                    for (File file : files){
//                        String strNewFile = file.getAbsolutePath();
//                        boolean bSent = !strNewFile.contains("_received");
//                        Tab_Messenger.addFile(strNewFile, bSent, false);
//                    }

                    if(Tab_Messenger.ll_messages != null)   Tab_Messenger.ll_messages.removeAllViews();
                    int page = (pageCounter != null)?pageCounter.getValue():1;
                    int count = files.size();
                    int n = 9;// 9 -> 10 per page
                    int end = Math.min(count-1, (count-1) - n*(page - 1));
                    int start = Math.max(0, end - n);
                    for (int i=start;i<=end;i++){
                        File file = files.get(i);
                        String strNewFile = file.getAbsolutePath();
                        boolean bSent = !strNewFile.contains("_received");
                        Tab_Messenger.addFile(strNewFile, bSent, false);
                    }
//                    Toast.makeText(MainActivity.ctx,"Page: "+page+", from "+start+" to "+end,Toast.LENGTH_SHORT).show();
                }
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        });
    }

    static public void update_ip_list() {
        MainActivity.activity.runOnUiThread(() -> {
            try {
                users.clear();

                String strDir = MainActivity.strTCPPath;
                File dir = new File(strDir);
                if (!dir.exists()){
                    if(!dir.mkdirs()){
                        MainActivity.MyLogInfo(strDir + " not created...");
                    }
                }

                File path = new File(strDir);
                String server_ip = MainActivity.server.getIP();
                List<File> files = null;
                if(server_ip != null){
                    if(sw_this_range_only.isChecked()){
                        String prefix = server_ip.substring(0, server_ip.lastIndexOf(".") + 1);
                        File[] Fs = path.listFiles(new FileFilter() {
                            @Override
                            public boolean accept(File file)
                            {
                                String fname = file.getName();
                                return (fname.startsWith(prefix));
                            }
                        });
                        if(Fs != null)  files = new ArrayList<>(Arrays.asList(Fs));
                    }else{
                        File[] Fs = path.listFiles();
                        if(Fs != null)  files = new ArrayList<>(Arrays.asList(Fs));
                    }
                }else{
                    File[] Fs = path.listFiles();
                    if(Fs != null)  files = new ArrayList<>(Arrays.asList(Fs));
                }

                if(files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            String ip = file.getName();
                            String name = get_ip_name(ip);
                            String phone_number = get_ip_phone_number(ip);
                            String avatarPath = get_ip_avatar(ip);
                            users.add(new tcp_user(ip, name, phone_number, avatarPath));
                        }
                    }
                }

                MainActivity.activity.runOnUiThread(() -> {
                    try {
                        ipAdapter.notifyDataSetChanged();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                });
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        });
    }

    static public void export_ip_list(String filename) {
        try {
            String strDir = MainActivity.strTCPPath;
            File path = new File(strDir);
            List<File> files = null;
            File[] Fs = path.listFiles();
            if(Fs != null)  files = new ArrayList<>(Arrays.asList(Fs));
            if(files != null) {
                File csvfile = new File(filename);
                CSVWriter writer = new CSVWriter(new FileWriter(csvfile.getAbsolutePath()));
                for (File file : files) {
                    if (file.isDirectory()) {
                        String ip = file.getName();
                        String name = get_ip_name(ip);
                        String phone_number = get_ip_phone_number(ip);
                        String[] fields = {ip, name, phone_number};
                        writer.writeNext(fields);
                    }
                }
                writer.flush();
                writer.close();
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public void import_ip_list(String filename) {
        try {
            File csvfile = new File(filename);
            CSVReader reader = new CSVReader(new FileReader(csvfile.getAbsolutePath()));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                String ip = nextLine[0];
                String name = nextLine[1];
                String phone_number = nextLine[2];

                create_ip_folder(ip, false);
                update_ip_name(ip,name,true);
                update_ip_phone_number(ip,phone_number,true);
            }
            MainActivity.activity.runOnUiThread(() -> {
                try {
                    ipAdapter.notifyDataSetChanged();
                } catch (Throwable ex) {
                    MainActivity.MyLog(ex);
                }
            });
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public void showToast(String msg) {
        if(msg == null) return;
        try {
            MainActivity.activity.runOnUiThread(() -> {
                try {
                    Toast.makeText(MainActivity.ctx,msg,Toast.LENGTH_SHORT).show();
                } catch (Throwable ex) {
                    MainActivity.MyLog(ex);
                }
            });
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public void addError(String msg) {
        if(msg == null) return;
        MainActivity.activity.runOnUiThread(() -> {
            try {
                if(MainActivity.ctx == null)    return;
                if(Tab_Messenger.ll_messages == null)   return;

                View view = MainActivity.activity.getLayoutInflater().inflate(R.layout.messenger_error, Tab_Messenger.ll_messages, false);
                LinearLayout ll_view1 = view.findViewById(R.id.ll_view1);
                LinearLayout ll_view2 = view.findViewById(R.id.ll_view2);
                TextView textView = view.findViewById(R.id.textView);
                textView.setText(msg.trim());
                textView.setTag(msg.trim());
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = (String) v.getTag();

                        ClipboardManager clipboard = (ClipboardManager) MainActivity.ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("message", text);
                        clipboard.setPrimaryClip(clip);

                        Tab_Messenger.showToast("Message copied to clipboard...");
                    }
                });

                Tab_Messenger.ll_messages.addView(ll_view1);
                ll_view2.setFocusable(true);
                ll_view2.setFocusableInTouchMode(true);
                ll_view2.requestFocus();
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        });
    }

    static public void addMessage(String filename, boolean bSent) {
        MainActivity.activity.runOnUiThread(() -> {
            try {
                String msg = read_line_from_file(filename).trim();
                if((msg == null) || (msg.length() == 0))   return;

                View view = MainActivity.activity.getLayoutInflater().inflate(R.layout.messenger_message, Tab_Messenger.ll_messages, false);
                LinearLayout ll_view1 = view.findViewById(R.id.ll_view1);
                LinearLayout ll_view2 = view.findViewById(R.id.ll_view2);
                TextView textView = view.findViewById(R.id.textView);
                ShapeableImageView avatar = view.findViewById(R.id.avatar);
                avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMessageActionsDialog(ll_view1, filename);
                    }
                });
                textView.setTag(msg);
                textView.setText(msg);
                if(bSent) {
                    ll_view1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    ll_view2.setBackgroundResource(R.drawable.dialog_sent);
                    textView.setTextColor(MainActivity.activity.getResources().getColor(R.color.sent_text_color));
                    avatar.setImageURI(Uri.fromFile(new File(get_ip_avatar(MainActivity.server.getIP()))));
                } else {
                    ll_view1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    ll_view2.setBackgroundResource(R.drawable.dialog_received);
                    textView.setTextColor(MainActivity.activity.getResources().getColor(R.color.received_text_color));
                    avatar.setImageURI(Uri.fromFile(new File(get_ip_avatar(active_ip))));
                }
                Tab_Messenger.ll_messages.addView(ll_view1);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = (String) v.getTag();

                        ClipboardManager clipboard = (ClipboardManager) MainActivity.ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("message", text);
                        clipboard.setPrimaryClip(clip);

                        Tab_Messenger.showToast("Message copied to clipboard...");
                    }
                });
                ll_view2.setFocusable(true);
                ll_view2.setFocusableInTouchMode(true);
                ll_view2.requestFocus();
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        });
    }

    static public void addLocation(String filename, boolean bSent) {
        MainActivity.activity.runOnUiThread(() -> {
            try {
//                FileInputStream fis = MainActivity.ctx.openFileInput(filename);
                FileInputStream fis = new FileInputStream(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                SerializableLocation serializable = (SerializableLocation) is.readObject();
                is.close();
                fis.close();

                Location location = serializable.toLocation();
                if(sw_received_location_as_gps.isChecked()) {
                    if(MainActivity.bNavigation)
                    {
                        GpsmvLocationProvider locationProvider = (GpsmvLocationProvider)MainActivity.tab_map.mLocationOverlay.getMyLocationProvider();
                        if(locationProvider != null) {
                            Bundle extraBundle = new Bundle();
                            extraBundle.putBoolean("isMock",true);
                            extraBundle.putBoolean("isKalman",false);
                            extraBundle.putBoolean("isVirtual",true);
                            location.setExtras(extraBundle);

                            locationProvider.onLocationChanged(location);
                        }

////                        mv_LocationOverlay.curr_location = location;
//                        mv_LocationOverlay.prev_location.set(mv_LocationOverlay.curr_location);
//                        mv_LocationOverlay.curr_location.set(location);
//                        MainActivity.tab_map.mLocationOverlay.setLocation(location);
//                        MainActivity.tab_map.mapController.setZoom(17.0);
//                        MainActivity.tab_map.mapController.setCenter(new GeoPoint(location.getLatitude(), location.getLongitude(), location.getAltitude()));
//                        Tab_Map.map.postInvalidate();
//                        if (MainActivity.checkGpsCoordinates(mv_LocationOverlay.curr_location.getLatitude(), mv_LocationOverlay.curr_location.getLongitude())) {
//                            double alt = Double.parseDouble(MainActivity.tab_sensors.et_alt.getText().toString());
//                            MainActivity.uav_lon = mv_LocationOverlay.curr_location.getLongitude();
//                            MainActivity.uav_lat = mv_LocationOverlay.curr_location.getLatitude();
////                MainActivity.uav_alt_above_ground = location.getAltitude();
//                            MainActivity.uav_alt_above_ground = alt;
//                            MainActivity.uav_ground_alt = MainActivity.GetHeightJNI(MainActivity.uav_lon, MainActivity.uav_lat);
//                            MainActivity.uav_alt = MainActivity.uav_ground_alt + MainActivity.uav_alt_above_ground;
//
//                            MainActivity.tab_sensors.et_lon.setText(String.format(Locale.ENGLISH, "%.06f", MainActivity.uav_lon));
//                            MainActivity.tab_sensors.et_lat.setText(String.format(Locale.ENGLISH, "%.06f", MainActivity.uav_lat));
////                MainActivity.tab_sensors.et_alt.setText(String.format(Locale.ENGLISH,"%.01f", MainActivity.uav_alt));
//                            MainActivity.tab_map.set_cam_pos(MainActivity.uav_lon, MainActivity.uav_lat, MainActivity.uav_alt, true);
//                            MainActivity.tab_camera.update_status(false);
//                        }
                    }
//                    else{
//                        if ((MainActivity.tab_camera.cb_gps != null) && (!MainActivity.tab_camera.cb_gps.isChecked())) {
//                            GpsmvLocationProvider locationProvider = (GpsmvLocationProvider)MainActivity.tab_map.mLocationOverlay.getMyLocationProvider();
//                            if(locationProvider != null) {
//                                Bundle extraBundle = new Bundle();
//                                extraBundle.putBoolean("isMock",true);
//                                extraBundle.putBoolean("isKalman",false);
//                                extraBundle.putBoolean("isVirtual",true);
//                                location.setExtras(extraBundle);
//
//                                locationProvider.onLocationChanged(location);
//                            }
//
////                            mv_LocationOverlay.prev_location.set(mv_LocationOverlay.curr_location);
////                            mv_LocationOverlay.curr_location.set(location);
////                            MainActivity.tab_map.mLocationOverlay.setLocation(location);
////                            Tab_Map.map.postInvalidate();
////                            if (MainActivity.checkGpsCoordinates(mv_LocationOverlay.curr_location.getLatitude(), mv_LocationOverlay.curr_location.getLongitude())) {
////                                double alt = Double.parseDouble(MainActivity.tab_sensors.et_alt.getText().toString());
////                                MainActivity.uav_lon = mv_LocationOverlay.curr_location.getLongitude();
////                                MainActivity.uav_lat = mv_LocationOverlay.curr_location.getLatitude();
//////                MainActivity.uav_alt_above_ground = location.getAltitude();
////                                MainActivity.uav_alt_above_ground = alt;
////                                MainActivity.uav_ground_alt = MainActivity.GetHeightJNI(MainActivity.uav_lon, MainActivity.uav_lat);
////                                MainActivity.uav_alt = MainActivity.uav_ground_alt + MainActivity.uav_alt_above_ground;
////
////                                MainActivity.tab_sensors.et_lon.setText(String.format(Locale.ENGLISH, "%.06f", MainActivity.uav_lon));
////                                MainActivity.tab_sensors.et_lat.setText(String.format(Locale.ENGLISH, "%.06f", MainActivity.uav_lat));
//////                MainActivity.tab_sensors.et_alt.setText(String.format(Locale.ENGLISH,"%.01f", MainActivity.uav_alt));
////                                MainActivity.tab_map.set_cam_pos(MainActivity.uav_lon, MainActivity.uav_lat, MainActivity.uav_alt, true);
////                                MainActivity.tab_camera.update_status(false);
////                            }
//                        }
//                    }
                }else {
                    if(MainActivity.bNavigation){
                        MainActivity.tab_map.set_target_pos(location.getLongitude(), location.getLatitude(), location.getAltitude(), false);
                        MainActivity.tab_map.mapController.setCenter(MainActivity.tab_map.targetPoint);
                    }else{
                        MainActivity.tab_map.set_cam_pos(location.getLongitude(), location.getLatitude(), location.getAltitude(), false);
                        MainActivity.tab_map.mapController.setCenter(MainActivity.tab_map.cameraPoint);
                    }
                    MainActivity.tab_map.mapController.setZoom(17.0);
                    MainActivity.tab_map.map.postInvalidate();
                }
                String strLocation = "";
                strLocation += String.format(Locale.ENGLISH, "%.06f", location.getLongitude())+", "+String.format(Locale.ENGLISH, "%.06f", location.getLatitude())+", "+String.format(Locale.ENGLISH, "%.03f", location.getAltitude());

                View view = MainActivity.activity.getLayoutInflater().inflate(R.layout.messenger_image, Tab_Messenger.ll_messages, false);
                LinearLayout ll_view1 = view.findViewById(R.id.ll_view1);
                LinearLayout ll_view2 = view.findViewById(R.id.ll_view2);
                Tab_Messenger.ll_messages.addView(ll_view1);

                ImageView imageView = view.findViewById(R.id.imageView);
                imageView.setTag(location);
                imageView.setImageResource(R.drawable.location_icon);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(48, 48)); // value is in pixels
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Location location = (Location) v.getTag();
                        MainActivity.tab_map.set_target_pos(location.getLongitude(), location.getLatitude(), location.getAltitude(), true);
                        MainActivity.tab_map.mapController.setCenter(MainActivity.tab_map.targetPoint);
                        MainActivity.tab_map.mapController.setZoom(17.0);
                        MapViewerView.process_click(R.id.radio_map);
                    }
                });

                TextView textView = view.findViewById(R.id.textView);
                textView.setTag(location);
                textView.setText(strLocation);
                ShapeableImageView avatar = view.findViewById(R.id.avatar);
                avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMessageActionsDialog(ll_view1, filename);
                    }
                });
                if(bSent) {
                    ll_view1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    ll_view2.setBackgroundResource(R.drawable.dialog_sent);
                    textView.setTextColor(MainActivity.activity.getResources().getColor(R.color.sent_text_color));
                    textView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    avatar.setImageURI(Uri.fromFile(new File(get_ip_avatar(MainActivity.server.getIP()))));
                } else {
                    ll_view1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    ll_view2.setBackgroundResource(R.drawable.dialog_received);
                    textView.setTextColor(MainActivity.activity.getResources().getColor(R.color.received_text_color));
                    textView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    avatar.setImageURI(Uri.fromFile(new File(get_ip_avatar(active_ip))));
                }
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Location location = (Location) v.getTag();
                        MainActivity.tab_map.set_target_pos(location.getLongitude(), location.getLatitude(), location.getAltitude(), true);
                        MainActivity.tab_map.mapController.setCenter(MainActivity.tab_map.targetPoint);
                        MainActivity.tab_map.mapController.setZoom(17.0);
                        MapViewerView.process_click(R.id.radio_map);
                    }
                });
                ll_view2.setFocusable(true);
                ll_view2.setFocusableInTouchMode(true);
                ll_view2.requestFocus();
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        });
    }

    static public void addPlacemark(String filename, boolean bSent) {
        MainActivity.activity.runOnUiThread(() -> {
            try {
                KmlPlacemark placemark = null;
                GeoPoint placemarkPos = null;
                int geometry_type = City.POINT;

                KmlDocument kmlDocument = new KmlDocument();
                File file = new File(filename);
                kmlDocument.parseKMLFile(file);

                if(kmlDocument.mKmlRoot.mItems.size() > 0) {
                    placemark = (KmlPlacemark) kmlDocument.mKmlRoot.mItems.get(0);
                    if ((placemark != null) && (placemark.mGeometry.mCoordinates.size() > 0)) {
                        placemarkPos = placemark.mGeometry.mCoordinates.get(0);
                        if(placemark.mGeometry instanceof KmlPoint)
                            geometry_type = City.POINT;
                        else
                        if(placemark.mGeometry instanceof KmlLineString)
                            geometry_type = City.POLYLINE;
                        else
                            geometry_type = City.POLYGON;
                    }
                }
                if((placemark == null) || (placemarkPos == null)){
                    Tab_Messenger.addOtherFile(filename, bSent);
//                    addError("Invalid Placemark...");
                    return;
                }
                if(!Objects.equals(placemark.getExtendedData("App"), "MapViewer")){
                    Tab_Messenger.addOtherFile(filename, bSent);
//                    addError("Invalid Placemark...");
                    return;
                }
                final int geometry_type0  = geometry_type;
                final KmlPlacemark placemark0 = placemark;
                final GeoPoint placemarkPos0 = placemarkPos;

                String strLocation = placemark.mName+"\n";
                strLocation += String.format(Locale.ENGLISH, "%.06f", placemarkPos.getLongitude())+", "+String.format(Locale.ENGLISH, "%.06f", placemarkPos.getLatitude())+", "+String.format(Locale.ENGLISH, "%.03f", placemarkPos.getAltitude());

                View view = MainActivity.activity.getLayoutInflater().inflate(R.layout.messenger_image, Tab_Messenger.ll_messages, false);
                LinearLayout ll_view1 = view.findViewById(R.id.ll_view1);
                LinearLayout ll_view2 = view.findViewById(R.id.ll_view2);
                Tab_Messenger.ll_messages.addView(ll_view1);

                ImageView imageView = view.findViewById(R.id.imageView);
                imageView.setTag(placemark);
                imageView.setImageResource(R.drawable.kml_icon);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(48, 48)); // value is in pixels
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // update placemark info with the same coordinates
                        boolean bFound = false;
                        for(int i=0;i<Tab_Map.favorites_adapter.getCount();i++){
                            City city = Tab_Map.favorites_adapter.getItem(i);
                            if(city != null){
                                if(city.placemark.mGeometry.hashCode() == placemark0.mGeometry.hashCode()){
                                    try{
                                        if(city.placemark.overlay instanceof MyMarker){
                                            MyMarker marker = (MyMarker)city.placemark.overlay;
                                            marker.setInfoWindow(new Tab_Map.CustomInfoWindow(geometry_type0, Tab_Map.map, marker, city.placemark, city));
                                            Tab_Map.map.invalidate();
                                            bFound = true;
                                            break;
                                        }else if(city.placemark.overlay instanceof MyPolyline){
                                            MyPolyline polyline = (MyPolyline)city.placemark.overlay;
                                            polyline.setInfoWindow(new Tab_Map.CustomInfoWindow(geometry_type0, Tab_Map.map, polyline, city.placemark, city));
                                            Tab_Map.map.invalidate();
                                            bFound = true;
                                            break;
                                        }else if(city.placemark.overlay instanceof MyPolygon){
                                            MyPolygon polygon = (MyPolygon)city.placemark.overlay;
                                            polygon.setInfoWindow(new Tab_Map.CustomInfoWindow(geometry_type0, Tab_Map.map, polygon, city.placemark, city));
                                            Tab_Map.map.invalidate();
                                            bFound = true;
                                            break;
                                        }
                                    } catch (Throwable ex){
                                        MainActivity.MyLog(ex);
                                    }
                                }

//                                if(mv_utils.hashCode(city.fLon, city.fLat) == mv_utils.hashCode(placemarkPos0.getLongitude(), placemarkPos0.getLatitude())){
//                                    try{
//                                        if(city.placemark.overlay instanceof MyMarker){
//                                            MyMarker marker = (MyMarker) city.placemark.overlay;
//                                            Tab_Map.CustomInfoWindow customInfoWindow = (Tab_Map.CustomInfoWindow) marker.getInfoWindow();
//                                            customInfoWindow.kmlPlacemark = placemark0.clone();
//                                            Tab_Map.map.invalidate();
//                                            bFound = true;
//                                        }
//                                    } catch (Throwable ex){
//                                        MainActivity.MyLog(ex);
//                                    }
//                                    break;
//                                }
                            }
                        }
                        if(bFound){
                            new AlertDialog.Builder(MainActivity.activity)
                                .setCancelable(false)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(R.string.element_exists)
                                .setMessage(R.string.are_you_sure_you_want_to_add_this_element)
                                .setPositiveButton(R.string.yes_message, (dialog, which) -> {
                                    try{
                                        Tab_Map.kmlFavoritesDocument.mKmlRoot.mItems.add(placemark0);
                                        for(String style_id:kmlDocument.getStylesList()){
                                            Tab_Map.kmlFavoritesDocument.putStyle(style_id,kmlDocument.getStyle(style_id));
                                        }
                                        Tab_Map.kmlFavoritesDocument.saveAsKML(Tab_Map.favoritesFile);

                                        placemark0.overlay = placemark0.buildOverlay(Tab_Map.map, MainActivity.tab_map.defaultStyle, MainActivity.tab_map.styler, Tab_Map.kmlFavoritesDocument);
                                        Tab_Map.kmlOverlay.add(placemark0.overlay);

                                        GeoPoint p = new GeoPoint(placemarkPos0.getLatitude(), placemarkPos0.getLongitude(), placemarkPos0.getAltitude());
                                        MainActivity.tab_map.mapController.setZoom(17.0);
                                        MainActivity.tab_map.mapController.setCenter(p);
                                        Tab_Map.map.invalidate();

                                        // Add item to adapter
                                        City city2 = new City();
                                        city2.strName = placemark0.mName;
                                        city2.fLon = placemarkPos0.getLongitude();
                                        city2.fLat = placemarkPos0.getLatitude();
                                        city2.fAlt = (float)placemarkPos0.getAltitude();
                                        city2.geometry_type = geometry_type0;
                                        city2.index = Tab_Map.favorites_adapter.getCount();
                                        city2.placemark = placemark0;
                                        Tab_Map.favorites_adapter.add(city2);

                                        int typ = 0;
                                        String type = placemark0.getExtendedData("type");
                                        if(type != null) {
                                            typ = mv_utils.parseInt(type);
                                        }

                                        if(placemark0.overlay instanceof MyMarker){
                                            MyMarker marker = (MyMarker)placemark0.overlay;
                                            marker.setInfoWindow(new Tab_Map.CustomInfoWindow(typ, Tab_Map.map, marker, placemark0, city2));
                                        }else if(placemark0.overlay instanceof MyPolyline){
                                            MyPolyline polyline = (MyPolyline)placemark0.overlay;
                                            polyline.setInfoWindow(new Tab_Map.CustomInfoWindow(typ, Tab_Map.map, polyline, placemark0, city2));
                                        }else if(placemark0.overlay instanceof MyPolygon){
                                            MyPolygon polygon = (MyPolygon)placemark0.overlay;
                                            polygon.setInfoWindow(new Tab_Map.CustomInfoWindow(typ, Tab_Map.map, polygon, placemark0, city2));
                                        }
                                    } catch (Throwable ex){
                                        MainActivity.MyLog(ex);
                                    }
                                })
                                .setNegativeButton(R.string.no_message, (dialog, which) -> {
                                    MainActivity.set_fullscreen();
                                })
                                .show();
                        }else{
                            try{
                                Tab_Map.kmlFavoritesDocument.mKmlRoot.mItems.add(placemark0);
                                for(String style_id:kmlDocument.getStylesList()){
                                    Tab_Map.kmlFavoritesDocument.putStyle(style_id,kmlDocument.getStyle(style_id));
                                }
                                Tab_Map.kmlFavoritesDocument.saveAsKML(Tab_Map.favoritesFile);

                                placemark0.overlay = placemark0.buildOverlay(Tab_Map.map, MainActivity.tab_map.defaultStyle, MainActivity.tab_map.styler, Tab_Map.kmlFavoritesDocument);
                                Tab_Map.kmlOverlay.add(placemark0.overlay);

                                GeoPoint p = new GeoPoint(placemarkPos0.getLatitude(), placemarkPos0.getLongitude(), placemarkPos0.getAltitude());
                                MainActivity.tab_map.mapController.setZoom(17.0);
                                MainActivity.tab_map.mapController.setCenter(p);
                                Tab_Map.map.invalidate();

                                // Add item to adapter
                                City city2 = new City();
                                city2.strName = placemark0.mName;
                                city2.fLon = placemarkPos0.getLongitude();
                                city2.fLat = placemarkPos0.getLatitude();
                                city2.fAlt = (float)placemarkPos0.getAltitude();
                                city2.geometry_type = geometry_type0;
                                city2.index = Tab_Map.favorites_adapter.getCount();
                                city2.placemark = placemark0;
                                Tab_Map.favorites_adapter.add(city2);

                                int typ = 0;
                                String type = placemark0.getExtendedData("type");
                                if(type != null) {
                                    typ = mv_utils.parseInt(type);
                                }

                                if(placemark0.overlay instanceof MyMarker){
                                    MyMarker marker = (MyMarker)placemark0.overlay;
                                    marker.setInfoWindow(new Tab_Map.CustomInfoWindow(typ, Tab_Map.map, marker, placemark0, city2));
                                }else if(placemark0.overlay instanceof MyPolyline){
                                    MyPolyline polyline = (MyPolyline)placemark0.overlay;
                                    polyline.setInfoWindow(new Tab_Map.CustomInfoWindow(typ, Tab_Map.map, polyline, placemark0, city2));
                                }else if(placemark0.overlay instanceof MyPolygon){
                                    MyPolygon polygon = (MyPolygon)placemark0.overlay;
                                    polygon.setInfoWindow(new Tab_Map.CustomInfoWindow(typ, Tab_Map.map, polygon, placemark0, city2));
                                }
                            } catch (Throwable ex){
                                MainActivity.MyLog(ex);
                            }
                        }
                        MapViewerView.process_click(R.id.radio_map);
                    }
                });

                TextView textView = view.findViewById(R.id.textView);
                textView.setTag(placemark);
                textView.setText(strLocation);
                ShapeableImageView avatar = view.findViewById(R.id.avatar);
                avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMessageActionsDialog(ll_view1, filename);
                    }
                });
                if(bSent) {
                    ll_view1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    ll_view2.setBackgroundResource(R.drawable.dialog_sent);
                    textView.setTextColor(MainActivity.activity.getResources().getColor(R.color.sent_text_color));
                    textView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    avatar.setImageURI(Uri.fromFile(new File(get_ip_avatar(MainActivity.server.getIP()))));
                } else {
                    ll_view1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    ll_view2.setBackgroundResource(R.drawable.dialog_received);
                    textView.setTextColor(MainActivity.activity.getResources().getColor(R.color.received_text_color));
                    textView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    avatar.setImageURI(Uri.fromFile(new File(get_ip_avatar(active_ip))));
                }
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        KmlPlacemark placemark = (KmlPlacemark) v.getTag();

//                        Overlay kmlOverlay = kmlDocument.mKmlRoot.buildOverlay(Tab_Map.map, MainActivity.tab_map.defaultStyle, MainActivity.tab_map.styler, kmlDocument);
//                        Tab_Map.map.getOverlays().add(kmlOverlay);
//                        Tab_Map.overlay_index = Tab_Map.map.getOverlays().size() - 1;
//
//                        MainActivity.tab_map.set_target_pos(placemarkPos.getLongitude(), placemarkPos.getLatitude(), placemarkPos.getAltitude(), true);
//                        MapViewerView.process_click(R.id.radio_map);
                    }
                });
                ll_view2.setFocusable(true);
                ll_view2.setFocusableInTouchMode(true);
                ll_view2.requestFocus();
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        });
    }

    static public void addImage(String filename, boolean bSent) {
        MainActivity.activity.runOnUiThread(() -> {
            try {
                View view = MainActivity.activity.getLayoutInflater().inflate(R.layout.messenger_image, Tab_Messenger.ll_messages, false);
                LinearLayout ll_view1 = view.findViewById(R.id.ll_view1);
                LinearLayout ll_view2 = view.findViewById(R.id.ll_view2);
                Tab_Messenger.ll_messages.addView(ll_view1);

                ImageView imageView = view.findViewById(R.id.imageView);
                imageView.setImageURI(Uri.fromFile(new File(filename)));
                imageView.setLayoutParams(new LinearLayout.LayoutParams(256, 256)); // value is in pixels
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.showFileViewDialog(filename);
                    }
                });

                TextView textView = view.findViewById(R.id.textView);
                textView.setText(new File(filename).getName());
                ShapeableImageView avatar = view.findViewById(R.id.avatar);
                avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMessageActionsDialog(ll_view1, filename);
                    }
                });
                if(bSent) {
                    ll_view1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    ll_view2.setBackgroundResource(R.drawable.dialog_sent);
                    textView.setTextColor(MainActivity.activity.getResources().getColor(R.color.sent_text_color));
                    avatar.setImageURI(Uri.fromFile(new File(get_ip_avatar(MainActivity.server.getIP()))));
                } else {
                    ll_view1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    ll_view2.setBackgroundResource(R.drawable.dialog_received);
                    textView.setTextColor(MainActivity.activity.getResources().getColor(R.color.received_text_color));
                    avatar.setImageURI(Uri.fromFile(new File(get_ip_avatar(active_ip))));
                }
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.showFileViewDialog(filename);
                    }
                });
                ll_view2.setFocusable(true);
                ll_view2.setFocusableInTouchMode(true);
                ll_view2.requestFocus();
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        });
    }

    static public void addVideo(String filename, boolean bVideo, boolean bSent, boolean bAutoPlay) {
        MainActivity.activity.runOnUiThread(() -> {
            try {
                View view = MainActivity.activity.getLayoutInflater().inflate(R.layout.messenger_video, Tab_Messenger.ll_messages, false);
                LinearLayout ll_view1 = view.findViewById(R.id.ll_view1);
                LinearLayout ll_view2 = view.findViewById(R.id.ll_view2);
                Tab_Messenger.ll_messages.addView(ll_view1);

                VideoView videoView = view.findViewById(R.id.videoView);
                videoView.setVideoURI(Uri.fromFile(new File(filename)));
                videoView.requestFocus();
                if (bVideo) {
                    videoView.setBackground(new BitmapDrawable(null, FileUtil.fetchVideoPreview(new File(filename))));
                    videoView.setLayoutParams(new LinearLayout.LayoutParams(256, 256)); // value is in pixels
                } else {
                    Bitmap thumb = FileUtil.fetchMusicCover(new File(filename));
                    if(thumb != null) {
                        videoView.setLayoutParams(new LinearLayout.LayoutParams(128, 128)); // value is in pixels
                        videoView.setBackground(new BitmapDrawable(null, thumb));
                    } else {
                        videoView.setLayoutParams(new LinearLayout.LayoutParams(96, 96)); // value is in pixels
                        videoView.setBackgroundResource(R.drawable.crystal_audio_white);
                    }
                }

                videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        return true;// return true to cancel error message
                    }
                });

//                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        try{
//                            videoView.setTag(mp);
//                            if(bAutoPlay) {
//                                if (sw_audio_auto_play.isChecked()) {
//                                    if (!bVideo) {
//                                        mp.start();
//                                    }
//                                }
//                            }
//                        } catch (Throwable ex) {
//                            MainActivity.MyLog(ex);
//                        }
//                    }
//                });

                videoView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            MainActivity.showFileViewDialog(filename);

//                            MediaPlayer mMediaPlayer = (MediaPlayer) v.getTag();
//                            if (mMediaPlayer != null) {
//                                if (mMediaPlayer.isPlaying())
//                                    mMediaPlayer.pause();
//                                else
//                                    mMediaPlayer.start();
//                            }
//                            if (bVideo) {
//                                videoView.setBackground(null);
//                            }
//                            videoView.invalidate();
                        }
                        return false;
                    }
                });

                TextView textView = view.findViewById(R.id.textView);
                textView.setTag(filename);
                textView.setText(new File(filename).getName());
                ShapeableImageView avatar = view.findViewById(R.id.avatar);
                avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMessageActionsDialog(ll_view1, filename);
                    }
                });
                if(bSent) {
                    ll_view1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    ll_view2.setBackgroundResource(R.drawable.dialog_sent);
                    textView.setTextColor(MainActivity.activity.getResources().getColor(R.color.sent_text_color));
                    avatar.setImageURI(Uri.fromFile(new File(get_ip_avatar(MainActivity.server.getIP()))));
                } else {
                    ll_view1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    ll_view2.setBackgroundResource(R.drawable.dialog_received);
                    textView.setTextColor(MainActivity.activity.getResources().getColor(R.color.received_text_color));
                    avatar.setImageURI(Uri.fromFile(new File(get_ip_avatar(active_ip))));
                }
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.showFileViewDialog(filename);
                    }
                });
                ll_view2.setFocusable(true);
                ll_view2.setFocusableInTouchMode(true);
                ll_view2.requestFocus();

                if(bAutoPlay) {
                    if (sw_audio_auto_play.isChecked()) {
                        if (!bVideo) {
//                            videoView.start();
                            mv_utils.playVoice(MainActivity.ctx, filename);
                        }
                    }
                }

//                Toast.makeText(MainActivity.ctx,textView.getText(),Toast.LENGTH_SHORT).show();
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        });
    }

    static public void addOtherFile(String filename, boolean bSent) {
        MainActivity.activity.runOnUiThread(() -> {
            try {
                View view = MainActivity.activity.getLayoutInflater().inflate(R.layout.messenger_file, Tab_Messenger.ll_messages, false);
                LinearLayout ll_view1 = view.findViewById(R.id.ll_view1);
                LinearLayout ll_view2 = view.findViewById(R.id.ll_view2);
                Tab_Messenger.ll_messages.addView(ll_view1);

                ImageView imageView = view.findViewById(R.id.imageView);
                imageView.setImageResource(R.drawable.unknown);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(96, 96)); // value is in pixels
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.showFileViewDialog(filename);
                    }
                });

                TextView textView = view.findViewById(R.id.textView);
                textView.setText("["+new File(filename).getName()+"]");
                ShapeableImageView avatar = view.findViewById(R.id.avatar);
                avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMessageActionsDialog(ll_view1, filename);
                    }
                });
                if(bSent) {
                    ll_view1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    ll_view2.setBackgroundResource(R.drawable.dialog_sent);
                    textView.setTextColor(MainActivity.activity.getResources().getColor(R.color.sent_text_color));
                    avatar.setImageURI(Uri.fromFile(new File(get_ip_avatar(MainActivity.server.getIP()))));
                } else {
                    ll_view1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    ll_view2.setBackgroundResource(R.drawable.dialog_received);
                    textView.setTextColor(MainActivity.activity.getResources().getColor(R.color.received_text_color));
                    avatar.setImageURI(Uri.fromFile(new File(get_ip_avatar(active_ip))));
                }
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.showFileViewDialog(filename);
                    }
                });
                ll_view2.setFocusable(true);
                ll_view2.setFocusableInTouchMode(true);
                ll_view2.requestFocus();
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        });
    }

    static public String strAvatarFilename = "0_avatar.png";
    static public String strNameFilename = "0_name.txt";
    static public String strPhoneNumberFilename = "0_phone_number.txt";
    static public String strMessagesCountFilename = "0_messages.count";
    static public String strIPConnectedFilename = "0_ip.connected";
//    static boolean isDrawingFinished = true;
//    static Bitmap bitmap = null;
    static public void addFile(String filename, boolean bSent, boolean bAutoPlay) {
        try
        {
            if(!FileHelper.FileExists(filename)){
                if(MainActivity.IsDebugJNI()){
                    if (MainActivity.IsDebugJNI()) {
                        MainActivity.MyLogInfoSilent("File not exists [" + filename+"] "+ Arrays.toString(new Throwable().getStackTrace()));
                    }
                }
                return;
            }
            if(FileHelper.file_size(filename) <= 0){
                FileHelper.delete_file(filename);
                return;
            }
            if (filename.contains(strAvatarFilename)) return;
            if (filename.contains(strNameFilename)) return;
            if (filename.contains(strPhoneNumberFilename)) return;
            if (filename.contains(strMessagesCountFilename)) return;
            if (filename.contains(strIPConnectedFilename))  return;
            if (filename.contains(MainActivity.strVoiceName)){
                Tab_Messenger.addVideo(filename, false, bSent, bAutoPlay);
                return;
            }
            if (filename.contains(MainActivity.strFrameCapturedName)){
                try
                {
//            if(!isDrawingFinished)   return;
//            isDrawingFinished = false;
                    MainActivity.activity.runOnUiThread(() -> {
                        try {
                            if(MapViewerView.sw_receive != null) {
                                MapViewerView.sw_receive.setVisibility(View.VISIBLE);
                                if (!MapViewerView.sw_receive.isChecked()) return;
                            }

                            if(Tab_Camera.iv_preview != null) {
                                Tab_Camera.iv_preview.setVisibility(View.VISIBLE);
                                Tab_Camera.iv_preview.set_filename(filename);
                            }

                            String exif = MainActivity.getExif(filename);
                            if(exif != null){
                                try {
                                    if(Tab_Camera.iv_preview != null)   Tab_Camera.iv_preview.set_text(exif);
                                    String[] lines = exif.split("\n");
                                    String line;
                                    for (String s : lines) {
                                        line = s.trim();
                                        if (line.contains("GPS:")) {
                                            line = line.replace("GPS:", "").trim();
                                            String[] coordinates = line.split(",");
                                            if(coordinates.length >= 5){
                                                double lat = mv_utils.parseDouble(coordinates[0].trim());
                                                double lon = mv_utils.parseDouble(coordinates[1].trim());
                                                double alt = mv_utils.parseDouble(coordinates[2].trim());
                                                float bearing = (float)mv_utils.parseDouble(coordinates[3].trim());
                                                float speed = (float)mv_utils.parseDouble(coordinates[4].trim());

                                                Location location = SerializableLocation.getLocation(lon, lat, alt, speed, bearing);
                                                if (sw_received_location_as_gps.isChecked()) {
                                                    if (MainActivity.bNavigation) {
                                                        GpsmvLocationProvider locationProvider = (GpsmvLocationProvider) MainActivity.tab_map.mLocationOverlay.getMyLocationProvider();
                                                        if (locationProvider != null) {
                                                            Bundle extraBundle = new Bundle();
                                                            extraBundle.putBoolean("isMock", true);
                                                            extraBundle.putBoolean("isKalman", false);
                                                            extraBundle.putBoolean("isVirtual", true);
                                                            location.setExtras(extraBundle);

                                                            locationProvider.onLocationChanged(location);
                                                        }
                                                    }
                                                } else {
                                                    if (MainActivity.bNavigation) {
                                                        MainActivity.tab_map.set_target_pos(location.getLongitude(), location.getLatitude(), location.getAltitude(), true);
                                                    } else {
                                                        MainActivity.tab_map.set_cam_pos(location.getLongitude(), location.getLatitude(), location.getAltitude(), true);
                                                    }
                                                }
                                            }
                                        } else
                                        if (line.contains("MAP:")) {
                                            line = line.replace("MAP:", "").trim();
                                            String[] coordinates = line.split(",");
                                            if(coordinates.length >= 4){
                                                MainActivity.activity.runOnUiThread(() -> {
                                                    try {
                                                        MainActivity.map_lat = mv_utils.parseDouble(coordinates[0].trim());
                                                        MainActivity.map_lon = mv_utils.parseDouble(coordinates[1].trim());
                                                        MainActivity.map_zoom = mv_utils.parseDouble(coordinates[2].trim());
                                                        MainActivity.map_rot = mv_utils.parseDouble(coordinates[3].trim());

                                                        GeoPoint p = new GeoPoint(MainActivity.map_lat, MainActivity.map_lon);
                                                        Tab_Map.mapController.setCenter(p);
                                                        Tab_Map.mapController.setZoom(MainActivity.map_zoom);
                                                        Tab_Map.map.setMapOrientation((float)MainActivity.map_rot);
                                                        MainActivity.tab_map.doRotate((float)MainActivity.map_rot, true);
                                                        Tab_Map.map.postInvalidate();
                                                    } catch (Throwable ex) {
                                                        MainActivity.MyLog(ex);
                                                    }
                                                });
                                            }
                                        }
                                        else if (line.contains("Yaw:")) {
                                            try {
                                                line = line.replace("Yaw:", "").trim();
                                                MainActivity.image_yaw = (float) mv_utils.parseDouble(line);
                                                showStatus(String.valueOf(MainActivity.image_yaw) + "[" + line + "]", Color.BLUE);
                                                MainActivity.tab_camera.update_status(true);
                                            } catch (Throwable ex) {
                                                MainActivity.MyLog(ex);
                                            }
                                        }
                                    }
                                } catch (Throwable ex) {
                                    MainActivity.MyLog(ex);
                                }
                            }
//                    isDrawingFinished = true;
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
//                    isDrawingFinished = true;
                        }
                    });
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
                return;
            }

//            try
//            {
//                MainActivity.activity.runOnUiThread(() -> {
//                    try {
//                        if(Tab_Camera.iv_preview != null)   Tab_Camera.iv_preview.setVisibility(View.GONE);
////                if(MapViewerView.sw_show != null) {
////                    MapViewerView.sw_show.setVisibility(View.GONE);
////                }
//                    } catch (Throwable ex) {
//                        MainActivity.MyLog(ex);
//                    }
//                });
//            }
//            catch (Throwable ex)
//            {
//                MainActivity.MyLog(ex);
//            }

            try
            {
                String ext = FileHelper.fileExt(filename);
                if(ext != null) {
                    if (ext.contains("bmp") || ext.contains("png") || ext.contains("jpg") || ext.contains("jpeg") || ext.contains("gif") || ext.contains("webp") || ext.contains("heic") || ext.contains("heif")) {
                        Tab_Messenger.addImage(filename, bSent);
                    }
                    else
                    if (ext.contains("3gp") || ext.contains("mp4") || ext.contains("mkv") || ext.contains("ts") || ext.contains("webm")) {
                        Tab_Messenger.addVideo(filename, true, bSent, false);
                    }
                    else
                    if (ext.contains("mp3") || ext.contains("m4a") || ext.contains("aac") || ext.contains("amr") || ext.contains("flac") || ext.contains("mid") || ext.contains("xmf") || ext.contains("mxmf") || ext.contains("rtttl") || ext.contains("rtx") || ext.contains("ota") || ext.contains("imy") || ext.contains("ogg") || ext.contains("wav")){
//                Tab_Messenger.addVideo(filename, false, bSent, bAutoPlay);
                        Tab_Messenger.addVideo(filename, false, bSent, false);
                    }
                    else
                    if (ext.contains("loc")){
                        Tab_Messenger.addLocation(filename, bSent);
                    }
                    else
                    if (ext.contains("kml")){
                        Tab_Messenger.addPlacemark(filename, bSent);
                    }
                    else
                    if (ext.contains("lin")) {
                        Tab_Messenger.addMessage(filename, bSent);
                    }
                    else
                    if (ext.contains("sta")) {
                        try {
                            String line = read_line_from_file(filename);
                            FileHelper.delete_file(filename);
                            if((line != null) && (line.length() > 0)){
                                line = line.trim();
                                if(line.contains("TAB:")) {
                                    try
                                    {
                                        if ((MapViewerView.sw_receive != null) && MapViewerView.sw_receive.isChecked()) {
                                            line = line.replace("TAB:", "").trim();
                                            int tab_index = mv_utils.parseInt(line);
                                            MainActivity.activity.runOnUiThread(() -> {
                                                try {
                                                    MapViewerView.set_tab(tab_index, false);
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
                                }else if(line.contains("BUZZ:")){
                                    try
                                    {
                                        if ((MapViewerView.sw_receive != null) && MapViewerView.sw_receive.isChecked()) {
                                            mv_utils.playResource(MainActivity.ctx, R.raw.ding_dong);
                                        }
                                    }
                                    catch (Throwable ex)
                                    {
                                        MainActivity.MyLog(ex);
                                    }
                                }else if(line.contains("ZOOM:")){
                                    try
                                    {
                                        if ((MapViewerView.sw_receive != null) && MapViewerView.sw_receive.isChecked()) {
                                            line = line.replace("ZOOM:", "").trim();
                                            MainActivity.map_zoom = mv_utils.parseDouble(line);
                                            MainActivity.activity.runOnUiThread(() -> {
                                                try {
                                                    MainActivity.tab_map.mapController.setZoom(MainActivity.map_zoom);
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
                                }else if(line.contains("ROT:")) {
                                    try
                                    {
                                        if ((MapViewerView.sw_receive != null) && MapViewerView.sw_receive.isChecked()) {
                                            line = line.replace("ROT:", "").trim();
                                            MainActivity.map_rot = mv_utils.parseFloat(line);
                                            MainActivity.activity.runOnUiThread(() -> {
                                                try {
                                                    MainActivity.tab_map.map.setMapOrientation((float)MainActivity.map_rot);
                                                    MainActivity.tab_map.doRotate((float)MainActivity.map_rot, true);
                                                    Tab_Map.map.postInvalidate();
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
                                }else if(line.contains("CENTER:")) {
                                    try
                                    {
                                        if ((MapViewerView.sw_receive != null) && MapViewerView.sw_receive.isChecked()){
                                            line = line.replace("CENTER:", "").trim();
                                            String[] coordinates = line.split(",");
                                            if(coordinates.length >= 2){
                                                MainActivity.activity.runOnUiThread(() -> {
                                                    try {
                                                        MainActivity.map_lon = mv_utils.parseDouble(coordinates[0].trim());
                                                        MainActivity.map_lat = mv_utils.parseDouble(coordinates[1].trim());
                                                        GeoPoint p = new GeoPoint(MainActivity.map_lat, MainActivity.map_lon);
                                                        MainActivity.tab_map.mapController.setCenter(p);
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
                                }else if(line.contains("MAP_STATUS:")) {
                                    try
                                    {
                                        if ((MapViewerView.sw_receive != null) && MapViewerView.sw_receive.isChecked()){
                                            line = line.replace("MAP_STATUS:", "").trim();
                                            String[] coordinates = line.split(",");
                                            if(coordinates.length >= 4){
                                                MainActivity.activity.runOnUiThread(() -> {
                                                    try {
                                                        MainActivity.map_lat = mv_utils.parseDouble(coordinates[0].trim());
                                                        MainActivity.map_lon = mv_utils.parseDouble(coordinates[1].trim());
                                                        MainActivity.map_zoom = mv_utils.parseDouble(coordinates[2].trim());
                                                        MainActivity.map_rot = mv_utils.parseDouble(coordinates[3].trim());

                                                        GeoPoint p = new GeoPoint(MainActivity.map_lat, MainActivity.map_lon);
                                                        Tab_Map.mapController.setCenter(p);
                                                        Tab_Map.mapController.setZoom(MainActivity.map_zoom);
                                                        Tab_Map.map.setMapOrientation((float)MainActivity.map_rot);
                                                        MainActivity.tab_map.doRotate((float)MainActivity.map_rot, true);
                                                        Tab_Map.map.postInvalidate();
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
                                }
                            }
                            Tab_Messenger.showStatus(line, Color.GREEN);
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    }
                    else {
                        if(ext.length() > 0) {
                            Tab_Messenger.addOtherFile(filename, bSent);
//                    MainActivity.MyLogInfo("Other File: "+filename);
                        }else{
                            FileHelper.delete_file(filename);
                            MainActivity.MyLogInfo("Invalid File: "+filename);
                        }
                    }
                }
                else {
                    FileHelper.delete_file(filename);
                    MainActivity.MyLogInfo("Invalid File NULL: "+filename);
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
    }

    public class MyCustomAdapter extends ArrayAdapter<String> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               String[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_custom_text_view, parent, false);
            RelativeLayout relMain = (RelativeLayout) row.findViewById(R.id.relMain);
            TextView label = (TextView) row.findViewById(R.id.spinnerText);
            ImageView imgSelected = (ImageView) row.findViewById(R.id.imgSelected);
//            label.setText(countryArray[position]);
//
//            if(countryArray.length == 1){ //only 1 item
//                relMain.setBackground(getResources().getDrawable(R.drawable.all_corners_rounder));
//            } else {
//                if(0 == position){ //first item
//                    relMain.setBackground(getResources().getDrawable(R.drawable.rounded_top));
//                } else if((countryArray.length -1) == position){  //last item
//                    relMain.setBackground(getResources().getDrawable(R.drawable.rounded_bottom));
//                } else { //other item
//                    relMain.setBackground(getResources().getDrawable(R.drawable.corners_not_rounded));
//                }
//            }
//
//            if(selectedPosition == position){
//                imgSelected.setVisibility(View.VISIBLE);
//            } else {
//                imgSelected.setVisibility(View.GONE);
//            }

            return row;
        }
    }

    @Override
    public void onStop() {
//        try {
//            client.stopConnection();
//            server.stop();
//        } catch (Throwable ex) {
//            MainActivity.MyLog(ex);
//        }
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
//        setFilters();  // Start listening notifications from UsbService
//        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
//            if(!isFirstLoad)
//            {
//                ClientTCPCommunicator.closeStreams();
//                ConnectToServer();
//            }
//            else
//                isFirstLoad = false;
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
        try {
//            close();
//            client.stopConnection();
//            server.stop();
//            remove_jni_callbacks();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        super.onDestroy();
    }

    static public void close(){
        try {
            if(Tab_Camera.sw_broadcast_camera != null) Tab_Camera.sw_broadcast_camera.setChecked(false);
            if(Tab_Map.sw_broadcast_map != null) Tab_Map.sw_broadcast_map.setChecked(false);
            MainActivity.client.stopConnection();
            MainActivity.server.stop();
            remove_jni_callbacks();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static boolean terminated = false;
    static boolean update_ips = false;
    static public class ConnectingThread extends Thread {
        private final String ip;
        public ConnectingThread(String ip){
            this.ip = ip;
        }

        public void run(){
            try{
                //disconnect();
//                connect(ip);
                connect_to_new_ip(ip);
            } catch (Throwable ex) {
                showStatus("Not connected....", Color.RED);
//                b_connect.setEnabled(true);
//                b_disconnect.setEnabled(false);
                MainActivity.MyLog(ex);
            }

            if(dialog_connecting != null) {
                dialog_connecting.dismiss();
                dialog_connecting = null;
            }
            MainActivity.set_fullscreen();
        }
    }

    static ProgressDialog dialog_connecting = null;
    static public void connect_with_waiting(String ip){
        MainActivity.activity.runOnUiThread(() -> {
            try{
                disconnect();
                dialog_connecting = ProgressDialog.show(MainActivity.activity, "Connecting", "Please wait...", false, false);

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(dialog_connecting != null) {
                            dialog_connecting.dismiss();
                            dialog_connecting = null;
                        }
                    }
                }, connect_timeout);

                ConnectingThread connectingThread = new ConnectingThread(ip);
                connectingThread.start();
            } catch (Throwable ex) {
                showStatus("Not connected....", Color.RED);
                MainActivity.MyLog(ex);
            }
        });
    }

    static public void connect_to_new_ip(String ip){
        if(et_server_port == null)  return;
        try{
            clearMessages();
            MainActivity.hide_keyboard(null);

            InetAddress address = InetAddress.getByName(ip);
            if(address.isReachable(connect_timeout)){
                if (MainActivity.client.startConnection(ip, mv_utils.parseInt(et_server_port.getText().toString()), connect_timeout)) {
                    showStatus("Connected....", Color.GREEN);
                    if(dialog_connecting != null)  dialog_connecting.dismiss();
                    MainActivity.activity.runOnUiThread(() -> {
                        try {
                            active_ip = ip;
                            create_ip_folder(ip, false);
                            set_ip_messages_count(ip,0);
                            update_ip_list();
                            if(pageCounter != null) pageCounter.setValue(1);

                            int idx = get_ip_index(ip);
                            if(idx >= 0) {
                                MainActivity.activity.runOnUiThread(() -> {
                                    try {
                                        IPsAdapter.selectedPos = idx;
                                        if(ipAdapter != null)   ipAdapter.notifyItemChanged(idx);
                                    } catch (Throwable ex) {
                                        MainActivity.MyLog(ex);
                                    }
                                });
                            }

                            if(ll_user_settings != null)    ll_user_settings.setVisibility(View.VISIBLE);
                            if(ll_send_to_server != null)   ll_send_to_server.setVisibility(View.VISIBLE);
                            if(MapViewerView.cb_send_voice_to_server_main != null)  MapViewerView.cb_send_voice_to_server_main.setVisibility(View.VISIBLE);
                            if(tv_ip_phone_number != null) {
                                tv_ip_phone_number.setText(get_ip_phone_number(ip));
                                if (tv_ip_phone_number.getText().toString().isEmpty())
                                    ll_phone_number.setVisibility(View.GONE);
                                else
                                    ll_phone_number.setVisibility(View.VISIBLE);
                            }
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    });
                    showServerMessages(ip);
                } else {
                    showStatus("Not connected....", Color.RED);
                    if(dialog_connecting != null)  dialog_connecting.dismiss();
                    MainActivity.activity.runOnUiThread(() -> {
                        try {
                            if(ll_user_settings != null)    ll_user_settings.setVisibility(View.VISIBLE);
                            if(ll_send_to_server != null)    ll_send_to_server.setVisibility(View.GONE);
                            if(MapViewerView.cb_send_voice_to_server_main != null)  MapViewerView.cb_send_voice_to_server_main.setVisibility(View.GONE);
                            if(tv_ip_phone_number != null) {
                                tv_ip_phone_number.setText(get_ip_phone_number(ip));
                                if (tv_ip_phone_number.getText().toString().isEmpty())
                                    ll_phone_number.setVisibility(View.GONE);
                                else
                                    ll_phone_number.setVisibility(View.VISIBLE);
                            }
                            if(pageCounter != null) pageCounter.setValue(1);
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    });
                    showServerMessages(ip);
                }
            }else{
                showStatus("Not reachable....", Color.RED);
                if(dialog_connecting != null)  dialog_connecting.dismiss();
                MainActivity.activity.runOnUiThread(() -> {
                    try {
                        if(ll_user_settings != null)    ll_user_settings.setVisibility(View.VISIBLE);
                        if(ll_send_to_server != null)    ll_send_to_server.setVisibility(View.GONE);
                        if(MapViewerView.cb_send_voice_to_server_main != null)  MapViewerView.cb_send_voice_to_server_main.setVisibility(View.GONE);
                        if(tv_ip_phone_number != null) {
                            tv_ip_phone_number.setText(get_ip_phone_number(ip));
                            if (tv_ip_phone_number.getText().toString().isEmpty())
                                ll_phone_number.setVisibility(View.GONE);
                            else
                                ll_phone_number.setVisibility(View.VISIBLE);
                        }
                        if(pageCounter != null) pageCounter.setValue(1);
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                });
                showServerMessages(ip);
            }
            MainActivity.set_fullscreen();
        } catch (Throwable ex) {
            showStatus("Not connected....", Color.RED);
            if(dialog_connecting != null)  dialog_connecting.dismiss();
            MainActivity.activity.runOnUiThread(() -> {
                try {
                    if(ll_user_settings != null)    ll_user_settings.setVisibility(View.VISIBLE);
                    if(ll_send_to_server != null)   ll_send_to_server.setVisibility(View.GONE);
                    if(MapViewerView.cb_send_voice_to_server_main != null)  MapViewerView.cb_send_voice_to_server_main.setVisibility(View.GONE);
                    if(pageCounter != null) pageCounter.setValue(1);
                } catch (Throwable ex2) {
                    MainActivity.MyLog(ex2);
                }
            });
            showServerMessages(ip);
            MainActivity.MyLog(ex);
        }
    }

//    static public void connect(String ip){
//        try{
//            MainActivity.activity.runOnUiThread(() -> {
//                try {
//                    clearMessages();
//                    showServerMessages(ip);
//
//                    create_ip_folder(ip, false);
//                    pageCounter.setValue(1);
//                    tv_ip_phone_number.setText(get_ip_phone_number(ip));
//                    if(tv_ip_phone_number.getText().toString().isEmpty())
//                        ll_phone_number.setVisibility(View.GONE);
//                    else
//                        ll_phone_number.setVisibility(View.VISIBLE);
//                    ll_user_settings.setVisibility(View.VISIBLE);
//                    ll_send_to_server.setVisibility(View.GONE);
//                    MapViewerView.cb_send_voice_to_server_main.setVisibility(View.GONE);
//
//                    ipAdapter.selectedPos = get_ip_index(ip);
//                } catch (Throwable ex) {
//                    MainActivity.MyLog(ex);
//                }
//            });
//
//            if (client.startConnection(ip, mv_utils.parseInt(et_server_port.getText().toString()), connect_timeout)) {
//                showStatus("Connected....", Color.GREEN);
//                if(dialog_connecting != null)  dialog_connecting.dismiss();
//                MainActivity.activity.runOnUiThread(() -> {
//                    try {
//                        ll_send_to_server.setVisibility(View.VISIBLE);
//                        MapViewerView.cb_send_voice_to_server_main.setVisibility(View.VISIBLE);
//                    } catch (Throwable ex) {
//                        MainActivity.MyLog(ex);
//                    }
//                });
//            } else {
//                showStatus("Not connected....", Color.RED);
//                if(dialog_connecting != null)  dialog_connecting.dismiss();
//            }
//        } catch (Throwable ex) {
//            showStatus("Connection error....", Color.RED);
//            if(dialog_connecting != null)  dialog_connecting.dismiss();
//            MainActivity.MyLog(ex);
//        }
//
//        MainActivity.set_fullscreen();
//    }

    static public void disconnect(){
        try {
            if(MainActivity.client != null)  MainActivity.client.stopConnection();
            showStatus("Not connected....", Color.RED);

            if(dialog_connecting != null) {
                dialog_connecting.dismiss();
                dialog_connecting = null;
            }

//            b_connect.setEnabled(true);
//            b_disconnect.setEnabled(false);
        } catch (Throwable ex) {
            showStatus("Not connected....", Color.RED);
//            b_connect.setEnabled(true);
//            b_disconnect.setEnabled(false);
            MainActivity.MyLog(ex);
        }
    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.b_avatar: {
                    try {
                        MainActivity.activity.select_avatar(active_ip);
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_delete_user: {
                    try {
                        MainActivity.activity.delete_user(active_ip);
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_delete_all: {
                    try {
                        MainActivity.activity.delete_all();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_import_ips: {
                    try {
                        new AlertDialog.Builder(MainActivity.activity)
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Import IPs")
                            .setMessage("Are you sure you want to import IPs?")
                            .setPositiveButton(R.string.yes_message, (dialog, which) -> {
                                MainActivity.activity.import_ip_list();
                                MainActivity.hide_keyboard(null);
                            })
                            .setNegativeButton(R.string.no_message, (dialog, which) -> {
                                MainActivity.hide_keyboard(null);
                            })
                            .show();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_export_ips: {
                    try {
                        new AlertDialog.Builder(MainActivity.activity)
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Export IPs")
                            .setMessage("Are you sure you want to export IPs?")
                            .setPositiveButton(R.string.yes_message, (dialog, which) -> {
                                export_ip_list(MainActivity.strIPsList);
                                Tab_Messenger.sendFile(MainActivity.strIPsList, true, "IP list sending...", true, new tcp_io_handler.SendCallback() {
                                    @Override
                                    public void onFinish(int error) {
                                        if(error != tcp_io_handler.TCP_OK) {
                                            if (MainActivity.IsDebugJNI()) {
                                                MainActivity.MyLogInfoSilent("The Current Line Number is " + Arrays.toString(new Throwable().getStackTrace()));
                                            }
                                        }
//                                        MainActivity.hide_keyboard(null);
                                        Tab_Messenger.showToast("IP list sent...");
                                    }
                                });
                            })
                            .setNegativeButton(R.string.no_message, (dialog, which) -> {
                                MainActivity.hide_keyboard(null);
                            })
                            .show();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.tv_status: {
                    tv_status.setText("");
                    tcp_io_handler.is_sending = false;
//                    MainActivity.client.ioHandler.ping("close");
                    if(update_ips) {
                        new AlertDialog.Builder(MainActivity.activity)
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.stop_operation)
                            .setMessage(R.string.are_you_sure_stop)
                            .setPositiveButton(R.string.yes_message, (dialog, which) -> {
                                terminated = true;
                                MainActivity.hide_keyboard(null);
                            })
                            .setNegativeButton(R.string.no_message, (dialog, which) -> {
                                MainActivity.hide_keyboard(null);
                            })
                            .show();
                    }
                    break;
                }
                case R.id.b_connect: {
                    try {
                        String server_ip = et_server_ip.getText().toString();
                        if(Patterns.IP_ADDRESS.matcher(server_ip).matches()) {
//                            connect_to_new_ip(server_ip);
                            Tab_Messenger.connect_with_waiting(server_ip);

                            SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("strIP", server_ip);
                            editor.putInt("nPort", Integer.parseInt(et_server_port.getText().toString()));
                            editor.apply();
                        }else{
                            addError("Invalid IP Address: "+server_ip);
                        }
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_disconnect: {
                    try {
                        disconnect();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_active_connections: {
                    try {
                        findActiveIPs();

//                        LocationManager locationmanager;
//                        locationmanager = (LocationManager) MainActivity.ctx.getSystemService(Context.LOCATION_SERVICE);
////                        if (ActivityCompat.checkSelfPermission(MainActivity.ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////                            // TODO: Consider calling
////                            //    ActivityCompat#requestPermissions
////                            // here to request the missing permissions, and then overriding
////                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
////                            //                                          int[] grantResults)
////                            // to handle the case where the user grants the permission. See the documentation
////                            // for ActivityCompat#requestPermissions for more details.
////                        }else {
//                            Location location = locationmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                            if(location != null)    addMessage(location.toString());
////                        }
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                }
                case R.id.b_users_refresh: {
                    check_ips_connections(MainActivity.def_server_port);
                    break;
                }
                case R.id.b_send_to_server: {
                    try {
                        MainActivity.client.ioHandler.sendMessage(et_message.getText().toString(), false);
                        et_message.setText("");
                        MainActivity.hide_keyboard(null);
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_send_location_to_server: {
                    try {
                        if(MainActivity.activity.send_location_to_server()) {
                            MapViewerView.process_click(R.id.radio_map);
                        }
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_send_buzz_to_server: {
                    try {
                        try {
                            Tab_Messenger.sendMessage("BUZZ:", true);
                            mv_utils.playResource(MainActivity.ctx, R.raw.ding_dong);
                            MainActivity.hide_keyboard(null);
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_send_file_to_server: {
                    try {
                        MainActivity.activity.send_file_to_server();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_call: {
//                        String number = "0930118496";// white tab
//                        String number = "0935765045";// black tab /
                    String number = et_server_phone_number.getText().toString();
//                    sendSms(number,"mapviewer");
                    sendSms(number,"mapviewer,"+MainActivity.server.getIP());
                    Tab_Messenger.showToast("Trying to send message...");

//                    addError("SIM Serial number: "+MainActivity.get_sim_serial_number());
//                    addError("Phone number: "+MainActivity.get_sim_phone_number());

//                        sendLongSMS(number,"mapviewer");
//                        sendSMSwithReport(number,"mapviewer");
//                        String number = tv_active_name.getText().toString();
//                        sendSMS(number,"mapviewer");
                    break;
                }
                case R.id.b_call_server: {
                    if(active_ip != null){
                        tcp_client.ping(active_ip,2555,Tab_Messenger.connect_timeout,"mapviewer");
//                        tcp_client client = new tcp_client();
//                        if(client.startConnection(active_ip,2555,1000)) {
//                            client.stopConnection();
//                        }
                    }
                    break;
                }
                case R.id.cb_user_list: {
                    if(cb_user_list.isChecked())
                        ll_user_list.setVisibility(View.VISIBLE);
                    else
                        ll_user_list.setVisibility(View.GONE);
                    break;
                }
                case R.id.b_clear: {
                    try {
//                        String number = "0930118496";// white tab
//                        String number = "0935765045";// black tab /
//                        String number = et_server_phone_number.getText().toString();

//                        sendLongSMS(number,"mapviewer");
//                        sendSMSwithReport(number,"mapviewer");

//                        String number = tv_active_name.getText().toString();
//                        sendSMS(number,"mapviewer");
//                        sendSms(number,"mapviewer");
//                        Toast.makeText(MainActivity.ctx, "Message Sent", Toast.LENGTH_LONG).show();

//                        clearMessages();
//                        tcp_server_listen(5555);
//                        tcp_io_handler.encode_file(MainActivity.strSettingsFile,MainActivity.dir_internal + "/MapViewer/Settings_enc.txt");
//                        tcp_io_handler.encode_file(MainActivity.dir_internal + "/MapViewer/test.jpg",MainActivity.dir_internal + "/MapViewer/test_enc.jpg");
//                        if(tcp_io_handler.testEncoding() == 0){
//                            Toast.makeText(MainActivity.ctx,"Tests passed (successful)",Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(MainActivity.ctx,"Tests failed",Toast.LENGTH_SHORT).show();
//                        }
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.b_clear2: {
                    try {
//                        mv_utils.addNotification(MainActivity.ctx);
//                        mv_utils.displayNotification(MainActivity.ctx);
//                        mv_utils.showNotification(MainActivity.ctx, "Please open Oghab Mapviewer V4.0");
                        mv_utils.showNotification(MainActivity.ctx, "Please open Oghab MapViewer V4.0, You have a MapViewer call from number: 09xxxxxxxx", "", active_ip, true);
//                        IncomingSms.playSyrianArmyAnthem();

//                        clearMessages();
//                        tcp_client_connect("192.168.1.5",5555);
//                        tcp_io_handler.decode_file(MainActivity.dir_internal + "/MapViewer/Settings_enc.txt", MainActivity.dir_internal + "/MapViewer/Settings_dec.txt");
//                        tcp_io_handler.decode_file(MainActivity.dir_internal + "/MapViewer/test_enc.jpg", MainActivity.dir_internal + "/MapViewer/test_dec.jpg");

//                        tcp_callbacks object = new tcp_callbacks();
//                        JNIMethod(object);

//                        tcp_callbacks object = new tcp_callbacks();
//                        JNIMethodWithParameter(object, 5);
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.sw_listen: {
                    try {
                        isListen = sw_listen.isChecked();
                        if (isListen) {
                            MainActivity.server.start(mv_utils.parseInt(et_server_port.getText().toString()));
                        } else {
                            MainActivity.server.stop();
                        }

                        SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt("nPort", Integer.parseInt(et_server_port.getText().toString()));
                        editor.putBoolean("isListen", isListen);
                        editor.apply();

                        MainActivity.set_fullscreen();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.sw_this_range_only: {
                    try {
                        update_ip_list();
                        MainActivity.set_fullscreen();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.sw_audio_auto_play: {
                    try {
                        MainActivity.set_fullscreen();
                        SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("isAudioAutoPlay", sw_audio_auto_play.isChecked());
                        editor.apply();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.sw_received_location_as_gps: {
                    try {
                        MainActivity.set_fullscreen();
                        SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("isReceivedLocationAsGPS", sw_received_location_as_gps.isChecked());
                        editor.apply();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
                case R.id.sw_encode: {
                    try {
                        MainActivity.is_encoded = sw_encode.isChecked();

                        SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("is_encoded", MainActivity.is_encoded);
                        editor.apply();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                    break;
                }
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public void update_ip_avatar(String ip, Bitmap bmp,boolean overwrite){
        try{
            String strDir = MainActivity.strTCPPath+ip;
            File file = new File(strDir, strAvatarFilename);
            if(file.exists()){
                if(overwrite){
                    FileHelper.save_image_as_png(file,bmp);
                }
            }
            else{
                FileHelper.save_image_as_png(file,bmp);
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public String get_ip_avatar(String ip){
        String strDir = MainActivity.strTCPPath+ip;
        String avatar = strDir+File.separator+strAvatarFilename;
        try{
            File file = new File(avatar);
            if(!file.exists()){
                Bitmap bmp = BitmapFactory.decodeResource(MainActivity.activity.getResources(), R.drawable.avatar48);
                update_ip_avatar(ip, bmp, true);
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return avatar;
    }

    static public int get_ip_index(String ip){
        int idx = -1;
        for(int i=0;i<users.size();i++){
            tcp_user user = users.get(i);
            if(user.ip.equals(ip)) {
                idx = i;
                break;
            }
        }
        return idx;
    }

    static public tcp_user get_ip_user(String ip){
        int idx = -1;
        for(int i=0;i<users.size();i++){
            tcp_user user = users.get(i);
            if(user.ip.equals(ip)) {
                idx = i;
                return user;
            }
        }
        return null;
    }

    static public void refresh_ip_status(String ip){
        if(users == null)   return;
        for(int i=0;i<users.size();i++){
            tcp_user user = users.get(i);
            if(user.ip.equals(ip)) {
                final int idx = i;
                MainActivity.activity.runOnUiThread(() -> {
                    try {
                        ipAdapter.notifyItemChanged(idx);
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                });
                break;
            }
        }
    }

    static public void delete_ip_user(String ip){
        try{
            String strDir = MainActivity.strTCPPath+ip;
            File file = new File(strDir);
            if(file.exists()){
                FileHelper.deleteRecursive(file);
            }

            int idx = get_ip_index(ip);
            if(idx >= 0){
                users.remove(idx);
                MainActivity.activity.runOnUiThread(() -> {
                    try {
                        ipAdapter.notifyDataSetChanged();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                });
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public void delete_all_ips(){
        try{
            String strDir = MainActivity.strTCPPath;
            File file = new File(strDir);
            if(file.exists()){
                FileHelper.deleteRecursive(file);
            }

            users.clear();
            MainActivity.activity.runOnUiThread(() -> {
                try {
                    ipAdapter.notifyDataSetChanged();
                    Tab_Messenger.ll_messages.removeAllViews();
                } catch (Throwable ex) {
                    MainActivity.MyLog(ex);
                }
            });
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public void set_ip_messages_count(String ip, int count){
        try{
            String strDir = MainActivity.strTCPPath+ip;
            File file = new File(strDir+File.separator+strMessagesCountFilename);
            FileHelper.writeTextFile(file.getPath(),String.valueOf(count));
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public void set_ip_connected(String ip, boolean connected){
        try{
            String strDir = MainActivity.strTCPPath+ip;
            File file = new File(strDir+File.separator+strIPConnectedFilename);
            FileHelper.writeTextFile(file.getPath(),String.valueOf(connected?1:0));
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public void increment_ip_messages_count(String ip){
        try{
            int count = get_ip_messages_count(ip);
            count++;
            set_ip_messages_count(ip, count);
            MainActivity.activity.runOnUiThread(() -> {
                try {
                    int idx = get_ip_index(ip);
                    if(idx >= 0){
                        MainActivity.activity.runOnUiThread(() -> {
                            try {
                                ipAdapter.notifyItemChanged(idx);
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        });
                    }
                } catch (Throwable ex) {
                    MainActivity.MyLog(ex);
                }
            });
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public int get_ip_messages_count(String ip){
        int count = 0;
        try{
            String strDir = MainActivity.strTCPPath+ip;
            String messages_count = FileHelper.readTextFile(strDir+File.separator+strMessagesCountFilename, "0");
            count = mv_utils.parseInt(messages_count);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return count;
    }

    static public boolean is_ip_connected(String ip){
        boolean connected = false;
        try{
            String strDir = MainActivity.strTCPPath+ip;
            String ip_connected = FileHelper.readTextFile(strDir+File.separator+strIPConnectedFilename, "0");
            connected = (mv_utils.parseInt(ip_connected) == 1);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return connected;
    }

    static public void update_ip_name(String ip, String name, boolean overwrite){
        try{
            String strDir = MainActivity.strTCPPath+ip;
            File file = new File(strDir+File.separator+strNameFilename);
            if(file.exists()){
                if(overwrite) {
                    FileHelper.writeTextFile(file.getPath(), name);
                }
            }
            else{
                FileHelper.writeTextFile(file.getPath(),name);
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public String get_ip_name(String ip){
        String name = "Guest";
        try{
            String strDir = MainActivity.strTCPPath+ip;
            name = FileHelper.readTextFile(strDir+File.separator+strNameFilename, "Guest");
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return name.trim();
    }

    static public void update_ip_phone_number(String ip, String phone_number, boolean overwrite){
        try{
            String strDir = MainActivity.strTCPPath+ip;
            File file = new File(strDir+File.separator+strPhoneNumberFilename);
            if(file.exists()){
                if(overwrite) {
                    FileHelper.writeTextFile(file.getPath(), phone_number);
                }
            }
            else{
                FileHelper.writeTextFile(file.getPath(),phone_number);
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public String get_ip_phone_number(String ip){
        String phone_number = "";
        try{
            String strDir = MainActivity.strTCPPath+ip;
            phone_number = FileHelper.readTextFile(strDir+File.separator+strPhoneNumberFilename, "");
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return phone_number.trim();
    }

    static public String create_ip_folder(String ip, boolean overwrite){
        try{
            boolean bCreated = true;
            String strDir = MainActivity.strTCPPath + ip + File.separator;
            File dir = new File(strDir);
            if (!dir.exists()){
                if(!dir.mkdirs()){
                    MainActivity.MyLogInfo(strDir + " not created...");
                }
                bCreated = false;
            }
            Bitmap bmp;
            if(ip.equals(MainActivity.server.getIP()))
                bmp = BitmapFactory.decodeResource( MainActivity.activity.getResources(), R.drawable.administrator_icon);
            else
                bmp = BitmapFactory.decodeResource( MainActivity.activity.getResources(), R.drawable.avatar48);
            update_ip_avatar(ip, bmp, overwrite);

            if(ip.equals(MainActivity.server.getIP()))
                update_ip_name(ip, "Localhost", overwrite);
            else
                update_ip_name(ip, "Guest", overwrite);

            set_ip_messages_count(ip, 0);

            if(!bCreated)   update_ip_list();
            return strDir;
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
            return null;
        }
    }

    public void check_ips_connections(int port){
        try {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        for(tcp_user user:users){
                            Tab_Messenger.set_ip_connected(user.ip, tcp_client.check_connection(user.ip, port, Tab_Messenger.connect_timeout, true));
                        }

                        MainActivity.activity.runOnUiThread(() -> {
                            try {
                                if(ipAdapter != null)   ipAdapter.notifyDataSetChanged();
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        });
                    } catch (Throwable ex){
                        MainActivity.MyLog(ex);
                    }
                }
            };
            thread.start();
        } catch (Throwable ex){
            MainActivity.MyLog(ex);
        }
    }

    public void findActiveIPs(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.activity);
        builder.setTitle("Timeout [ms] ?");
        builder.setCancelable(false);

        // Set up the input
        final EditText input = new EditText(MainActivity.activity);
        input.setText(String.valueOf(connect_timeout));
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(MainActivity.ctx.getString(R.string.ok), (dialog, which) -> {
            try
            {
                int timeout = mv_utils.parseInt(input.getText().toString());
//                new NetworkSniffTask(MainActivity.activity, timeout).execute();
                new NetworkSniffThread(MainActivity.activity, timeout).start();

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

    static public NetworkInterface currNetworkInterface =  null;
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                currNetworkInterface = intf;
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = "";
                        try{
                            sAddr = addr.getHostAddress();
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                        if(sAddr == null)   continue;
                        if(sAddr.length() == 0)    continue;
                        boolean isIPv4 = sAddr.indexOf(':') < 0;
                        if (useIPv4) {
                            if (isIPv4){
                                return sAddr;
                            }
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return "";
    }
    public static String getIPAddressByIndex(boolean useIPv4,int index) {
        try {
            int idx = 0;
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = "";
                        try{
                            sAddr = addr.getHostAddress();
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                        if(sAddr == null)   continue;
                        if(sAddr.length() == 0)    continue;
                        boolean isIPv4 = sAddr.indexOf(':') < 0;
                        if (useIPv4) {
                            if (isIPv4){
                                if(idx == index){
                                    currNetworkInterface = intf;
                                    return sAddr;
                                }
                                idx++;
                            }
                        } else {
                            if (!isIPv4) {
                                if(idx == index){
                                    currNetworkInterface = intf;
                                    int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                    return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                                }
                                idx++;
                            }
                        }
                    }
                }
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return "";
    }
    public static String getIPAddresses(boolean useIPv4) {
        StringBuilder addresses = new StringBuilder();
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = "";
                        try{
                            sAddr = addr.getHostAddress();
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                        if(sAddr == null)   continue;
                        if(sAddr.length() == 0)    continue;
                        boolean isIPv4 = sAddr.indexOf(':') < 0;
                        if (useIPv4) {
                            if (isIPv4) addresses.append(sAddr).append(";");
                        } else {
                            if (!isIPv4) {
                                int delimiter = sAddr.indexOf('%'); // drop ip6 zone suffix
                                addresses.append(delimiter<0 ? sAddr.toUpperCase() : sAddr.substring(0, delimiter).toUpperCase()).append(";");
                            }
                        }
                    }
                }
            }
        } catch (Throwable ex) {
            addresses.append(ex.getMessage());
            MainActivity.MyLog(ex);
        }
        return addresses.toString();
    }

    static public class NetworkSniffThread extends Thread{
        private final WeakReference<Context> mContextRef;
        private final int timeout;

        public NetworkSniffThread(Context context, int timeout) {
            mContextRef = new WeakReference<Context>(context);
            this.timeout = timeout;
        }

        public void run() {
            try {
                Context context = mContextRef.get();
                if (context != null) {
                    try {
                        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                        String ipString;
                        ipString = getIPAddress(true);

                        addError("Active Network: " + activeNetwork);
                        addError("Active Network Interface: " + currNetworkInterface);
                        addError("Type Name: " + Objects.requireNonNull(activeNetwork).getTypeName());
                        addError("Device IP Address: " + ipString);

                        String prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1);
//                        addMessage0("prefix: " + prefix);

                        // find my time
//                        double light_speed = 299792458.0;
//                        long startTime;
//                        double difference0 = 0.0;
//                        int N = 10;
//                        InetAddress.getByName(ipString).isReachable(timeout);// for initializing
//                        for(int i=0;i<N;i++){
//                            startTime = System.currentTimeMillis();
//                            InetAddress.getByName(ipString).isReachable(timeout);
//                            difference0 += (System.currentTimeMillis() - startTime)/2000.0;
//                        }
//                        difference0 /= N;

                        int n = 0;
                        terminated = false;
                        update_ips = true;
                        for (int i = 0; i < 256; i++) {
                            String testIp = prefix + i;
//                            startTime = System.currentTimeMillis();
                            InetAddress address = InetAddress.getByName(testIp);
                            if(address.isReachable(timeout)) {
//                                double difference = (System.currentTimeMillis() - startTime)/2000.0;
//                                double dist = light_speed * Math.abs(difference - difference0);

//                                String hostName = address.getCanonicalHostName();
//                                String hostName = address.getHostName();
//                                addMessage("Host: (" + testIp + ") is Active!, dist: "+dist+" meters from here.");
//                                addMessage("Host: " + "(" + testIp + ") {"+ hostName +"} is Active!");
                                n++;
                                addError("Host "+n+": (" + testIp + ") is Active!");
//                                users.add(new tcp_user(testIp,String.valueOf(i)));

                                create_ip_folder(testIp, false);
                            }else{
                                showStatus("Host: ("+ testIp +"), ["+ n +"] is Active!, <Click Here to Stop>", Color.BLUE);
                            }
                            if(terminated)  break;
                        }
                        update_ips = false;
                        addError("Active Connections: [" + n + "] found...");

                        MainActivity.activity.runOnUiThread(() -> {
                            try {
                                update_ip_list();
//                        synchronized(ipAdapter){
//                                ipAdapter.notifyDataSetChanged();
//                                llManager.scrollToPosition(users.size()-1);
//                                rv_ips.invalidate();
//                        }
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        });
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                }
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        }
    }

//    static class NetworkSniffTask extends AsyncTask<Void, Void, Void> {
//        private final WeakReference<Context> mContextRef;
//        private final int timeout;
//
//        public NetworkSniffTask(Context context, int timeout) {
//            mContextRef = new WeakReference<Context>(context);
//            this.timeout = timeout;
//        }
//
//        @SuppressLint("NotifyDataSetChanged")
//        @Override
//        protected Void doInBackground(Void... voids) {
//            try {
//                Context context = mContextRef.get();
//                if (context != null) {
//                    try {
//                        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//
//                        String ipString;
//                        ipString = getIPAddress(true);
//
//                        addError("Active Network: " + activeNetwork);
//                        addError("Active Network Interface: " + currNetworkInterface);
//                        addError("Type Name: " + Objects.requireNonNull(activeNetwork).getTypeName());
//                        addError("Device IP Address: " + ipString);
//
//                        String prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1);
////                        addMessage0("prefix: " + prefix);
//
//                        // find my time
////                        double light_speed = 299792458.0;
////                        long startTime;
////                        double difference0 = 0.0;
////                        int N = 10;
////                        InetAddress.getByName(ipString).isReachable(timeout);// for initializing
////                        for(int i=0;i<N;i++){
////                            startTime = System.currentTimeMillis();
////                            InetAddress.getByName(ipString).isReachable(timeout);
////                            difference0 += (System.currentTimeMillis() - startTime)/2000.0;
////                        }
////                        difference0 /= N;
//
//                        int n = 0;
//                        terminated = false;
//                        update_ips = true;
//                        for (int i = 0; i < 256; i++) {
//                            String testIp = prefix + i;
////                            startTime = System.currentTimeMillis();
//                            InetAddress address = InetAddress.getByName(testIp);
//                            if(address.isReachable(timeout)) {
////                                double difference = (System.currentTimeMillis() - startTime)/2000.0;
////                                double dist = light_speed * Math.abs(difference - difference0);
//
////                                String hostName = address.getCanonicalHostName();
////                                String hostName = address.getHostName();
////                                addMessage("Host: (" + testIp + ") is Active!, dist: "+dist+" meters from here.");
////                                addMessage("Host: " + "(" + testIp + ") {"+ hostName +"} is Active!");
//                                n++;
//                                addError("Host "+n+": (" + testIp + ") is Active!");
////                                users.add(new tcp_user(testIp,String.valueOf(i)));
//
//                                create_ip_folder(testIp, false);
//                            }else{
//                                showStatus("Host: ("+ testIp +"), ["+ n +"] is Active!, <Click Here to Stop>", Color.BLUE);
//                            }
//                            if(terminated)  break;
//                        }
//                        update_ips = false;
//                        addError("Active Connections: [" + n + "] found...");
//
//                        MainActivity.activity.runOnUiThread(() -> {
//                            try {
//                                update_ip_list();
////                        synchronized(ipAdapter){
////                                ipAdapter.notifyDataSetChanged();
////                                llManager.scrollToPosition(users.size()-1);
////                                rv_ips.invalidate();
////                        }
//                            } catch (Throwable ex) {
//                                MainActivity.MyLog(ex);
//                            }
//                        });
//                    } catch (Throwable ex) {
//                        MainActivity.MyLog(ex);
//                    }
//                }
//            } catch (Throwable ex) {
//                MainActivity.MyLog(ex);
//            }
//
//            return null;
//        }
//    }

    public static boolean isOnline() {
        try {
            return InetAddress.getByName("google.com").isReachable(3);
        } catch (UnknownHostException e){
            return false;
        } catch (Throwable ex){
            MainActivity.MyLog(ex);
            return false;
        }
    }

    static public void write_line_to_file(String filename, String line) {
        try {
            FileHelper.delete_file(filename);

            FileOutputStream outputStream = new FileOutputStream(filename);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            PrintWriter out = new PrintWriter(outputStreamWriter);
            out.println(line);
            out.flush();
            out.close();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public String read_line_from_file(String filename) {
        try {
            FileReader reader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            reader.close();
            return line;
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
            return null;
        }
    }

    static private void showMessageActionsDialog(View view,String filename){
        try
        {
            LinearLayout messageActionsLayout = (LinearLayout)MainActivity.activity.getLayoutInflater().inflate(R.layout.messenger_message_actions, null);

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.activity)
                .setCancelable(true)
                .setView(messageActionsLayout)
                .create();
            alertDialog.show();

            ShapeableImageView siv_delete_message = messageActionsLayout.findViewById(R.id.siv_delete_message);
            siv_delete_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(MainActivity.activity)
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.delete_message)
                        .setMessage(R.string.are_you_sure_delete_message)
                        .setPositiveButton(R.string.yes_message, (dialog, which) -> {
                            FileHelper.delete_file(filename);
                            Tab_Messenger.ll_messages.removeView(view);
                            alertDialog.dismiss();
                            MainActivity.hide_keyboard(messageActionsLayout);
                        })
                        .setNegativeButton(R.string.no_message, (dialog, which) -> {
                            alertDialog.dismiss();
                            MainActivity.hide_keyboard(messageActionsLayout);
                            dialog.cancel();
                        })
                        .show();
                }
            });

            ShapeableImageView siv_save_message = messageActionsLayout.findViewById(R.id.siv_save_message);
            siv_save_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Bitmap bmp = BitmapFactory.decodeResource(MainActivity.activity.getResources(), R.drawable.avatar48);
//                    Bitmap bmp = BitmapFactory.decodeFile(filename);
//                    String strFilename = FileHelper.saveImage(bmp);
//                    String strFilename = FileHelper.saveFile(filename);
//                    addError("File saved to: ["+strFilename+"]");
//                    FileHelper.copy_file(filename, new_filename);
                    new FileHelper.SavingThread(filename).start();
                    alertDialog.dismiss();
                }
            });

            ShapeableImageView siv_replay_message = messageActionsLayout.findViewById(R.id.siv_replay_message);
            siv_replay_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Tab_Messenger.sendFile(filename, true, "Message sending...", true, new tcp_io_handler.SendCallback() {
                        @Override
                        public void onFinish(int error) {
                            if(error != tcp_io_handler.TCP_OK) {
                                if (MainActivity.IsDebugJNI()) {
                                    MainActivity.MyLogInfoSilent("The Current Line Number is " + Arrays.toString(new Throwable().getStackTrace()));
                                }
                            }
                            Tab_Messenger.showToast("Message sent...");
                        }
                    });
                    alertDialog.dismiss();
                }
            });

        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Tab_Messenger.showToast("Message Sent");
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    public void sendLongSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(msg);
            smsManager.sendMultipartTextMessage(phoneNo, null, parts, null, null);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

//    void sendSmsMsgFnc(String mblNumVar, String smsMsgVar)
//    {
//        if (ActivityCompat.checkSelfPermission(MainActivity.ctx, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
//        {
//            try
//            {
//                SmsManager smsMgrVar = SmsManager.getDefault();
//                smsMgrVar.sendTextMessage(mblNumVar, null, smsMsgVar, null, null);
//                Toast.makeText(MainActivity.ctx, "Message Sent", Toast.LENGTH_LONG).show();
//            }
//            catch (Exception ErrVar)
//            {
//                Toast.makeText(MainActivity.ctx,ErrVar.getMessage().toString(),
//                        Toast.LENGTH_LONG).show();
//                ErrVar.printStackTrace();
//            }
//        }
//        else
//        {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//            {
//                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 10);
//            }
//        }
//
//    }

    //sent sms
//    private void sendSMSwithReport(String phoneNumber, String message) {
//        String SENT = "SMS_SENT";
//        String DELIVERED = "SMS_DELIVERED";
//
//        PendingIntent sentPI = PendingIntent.getBroadcast(MainActivity.ctx, 0, new Intent(SENT), 0);
//        PendingIntent deliveredPI = PendingIntent.getBroadcast(MainActivity.ctx, 0, new Intent(DELIVERED), 0);
//
//        // ---when the SMS has been sent---
//        MainActivity.activity.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//
//                switch (getResultCode()) {
//
//                    case Activity.RESULT_OK:
//
//                        Toast.makeText(MainActivity.ctx, "SMS sent", Toast.LENGTH_SHORT).show();
//                        break;
//
//                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//
//                        Toast.makeText(MainActivity.ctx, "Generic failure", Toast.LENGTH_SHORT).show();
//                        break;
//
//                    case SmsManager.RESULT_ERROR_NO_SERVICE:
//
//                        Toast.makeText(MainActivity.ctx, "No service", Toast.LENGTH_SHORT).show();
//                        break;
//
//                    case SmsManager.RESULT_ERROR_NULL_PDU:
//
//                        Toast.makeText(MainActivity.ctx, "Null PDU", Toast.LENGTH_SHORT).show();
//                        break;
//
//                    case SmsManager.RESULT_ERROR_RADIO_OFF:
//
//                        Toast.makeText(MainActivity.ctx, "Radio off", Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(SENT));
//
//        // ---when the SMS has been delivered---
//        MainActivity.activity.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//
//                switch (getResultCode()) {
//
//                    case Activity.RESULT_OK:
//
//                        Toast.makeText(MainActivity.ctx, "SMS delivered", Toast.LENGTH_SHORT).show();
//                        break;
//
//                    case Activity.RESULT_CANCELED:
//
//                        Toast.makeText(MainActivity.ctx, "SMS not delivered", Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(DELIVERED));
//
//        SmsManager sms = SmsManager.getDefault();
//        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
//    }

    private static final int MAX_SMS_MESSAGE_LENGTH = 160;
    public static final int SMS_PORT = 5555;
    private static final String SMS_DELIVERED = "SMS_DELIVERED";
    private static final String SMS_SENT = "SMS_SENT";
    private static String s_phoneNumber = "";

    private void sendSms(String phoneNumber,String message) {
        try{
            s_phoneNumber = phoneNumber;
            SmsManager manager = SmsManager.getDefault();
            PendingIntent piSend = PendingIntent.getBroadcast(MainActivity.ctx, 0, new Intent(SMS_SENT), PendingIntent.FLAG_IMMUTABLE);
            PendingIntent piDelivered = PendingIntent.getBroadcast(MainActivity.ctx, 0, new Intent(SMS_DELIVERED), PendingIntent.FLAG_IMMUTABLE);

            byte[] data = new byte[message.length()];
            for(int index=0; index<message.length() && index < MAX_SMS_MESSAGE_LENGTH; ++index)
            {
                data[index] = (byte)message.charAt(index);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {//AliSoft
                MainActivity.activity.registerReceiver(sendreceiver, new IntentFilter(SMS_SENT), Context.RECEIVER_EXPORTED);
            }else{
                MainActivity.activity.registerReceiver(sendreceiver, new IntentFilter(SMS_SENT));
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {//AliSoft
                MainActivity.activity.registerReceiver(deliveredreceiver, new IntentFilter(SMS_DELIVERED), Context.RECEIVER_EXPORTED);
            }else{
                MainActivity.activity.registerReceiver(deliveredreceiver, new IntentFilter(SMS_DELIVERED));
            }

            manager.sendDataMessage(phoneNumber, null, (short) SMS_PORT, data,piSend, piDelivered);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    private final BroadcastReceiver sendreceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String info = "Send information to "+s_phoneNumber+": ";
            switch(getResultCode())
            {
                case Activity.RESULT_OK: info += "sent successfully"; break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE: info += "send failed, generic failure"; break;
                case SmsManager.RESULT_ERROR_NO_SERVICE: info += "send failed, no service"; break;
                case SmsManager.RESULT_ERROR_NULL_PDU: info += "send failed, null pdu"; break;
                case SmsManager.RESULT_ERROR_RADIO_OFF: info += "send failed, radio is off"; break;
            }
//            Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
            addError(info);
        }
    };

    private final BroadcastReceiver deliveredreceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String info = "Delivery information to "+s_phoneNumber+": ";
            switch(getResultCode())
            {
                case Activity.RESULT_OK: info += "delivered"; break;
                case Activity.RESULT_CANCELED: info += "not delivered"; break;
            }
//            Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
            addError(info);
        }
    };

    public void get_connection_info(){
//        TelephonyManager telephonyManager = new TelephonyManager(MainActivity.ctx, 1); // 1 = SIM slot
        TelephonyManager  telephonyManager = (TelephonyManager)MainActivity.ctx.getSystemService(Context.TELEPHONY_SERVICE);
//        telephonyManager.setPreferredNetworkType(1, newNetworkMode); // 1 = SIM slot, newNetworkMode = the desired network mode defined in RILConstants.java
//        telephonyManager.setPreferredOpportunisticDataSubscription();
    }

    static public void sendFile(String filename, boolean save, String status, boolean bAppendTime, tcp_io_handler.SendCallback sendCallback) {
        try {
            if(MainActivity.client != null){
                if(MainActivity.client.ioHandler != null){
                    MainActivity.client.ioHandler.sendFile(filename, save, status, bAppendTime, sendCallback);
                }else{
                    if(MainActivity.IsDebugJNI()) {
                        MainActivity.MyLogInfoSilent("sendFile: " + Arrays.toString(new Throwable().getStackTrace()));
                    }
                }
            }else{
                if(MainActivity.IsDebugJNI()) {
                    MainActivity.MyLogInfoSilent("sendFile: " + Arrays.toString(new Throwable().getStackTrace()));
                }
            }
        } catch (Throwable ex){
            MainActivity.MyLog(ex);
        }
    }

    static public void sendMessage(String msg, boolean status) {
        try {
            if(MainActivity.client != null){
                if(MainActivity.client.ioHandler != null){
                    MainActivity.client.ioHandler.sendMessage(msg, status);
                }else{
                    if(MainActivity.IsDebugJNI()) {
                        MainActivity.MyLogInfoSilent("sendMessage: " + Arrays.toString(new Throwable().getStackTrace()));
                    }
                }
            }else{
                if(MainActivity.IsDebugJNI()) {
                    MainActivity.MyLogInfoSilent("sendMessage: " + Arrays.toString(new Throwable().getStackTrace()));
                }
            }
        } catch (Throwable ex){
            MainActivity.MyLog(ex);
        }
    }

    static public boolean sendLocation(SerializableLocation location) {
        boolean res = false;
        try {
            if(MainActivity.client != null){
                if(MainActivity.client.ioHandler != null){
                    res = MainActivity.client.ioHandler.sendLocation(location);
                }else{
                    if(MainActivity.IsDebugJNI()) {
                        MainActivity.MyLogInfoSilent("sendLocation: " + Arrays.toString(new Throwable().getStackTrace()));
                    }
                }
            }else{
                if(MainActivity.IsDebugJNI()) {
                    MainActivity.MyLogInfoSilent("sendLocation: " + Arrays.toString(new Throwable().getStackTrace()));
                }
            }
        } catch (Throwable ex){
            MainActivity.MyLog(ex);
        }
        return res;
    }

    private void checkNetworkConnectionStatus() {
        boolean wifiConnected;
        boolean mobileDataConnected;
        ConnectivityManager cm = (ConnectivityManager)MainActivity.ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo!=null && networkInfo.isConnected()){
            wifiConnected = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileDataConnected = networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            if (wifiConnected){
//                ivConStatus.setImageResource(R.drawable.ic_wifi);
//                tvconStatus.setText("WIFI is Connected");
            } else if (mobileDataConnected){
//                ivConStatus.setImageResource(R.drawable.ic_mobiledata);
//                tvconStatus.setText("Mobile data is Connected");
            }
        } else {
//            ivConStatus.setImageResource(R.drawable.ic_dnd);
//            tvconStatus.setText("No Connections are available");
        }
    }

}
