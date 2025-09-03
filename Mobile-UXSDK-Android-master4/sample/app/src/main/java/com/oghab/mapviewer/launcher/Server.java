package com.oghab.mapviewer.launcher;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;
import com.oghab.mapviewer.mapviewer.Tab_Messenger;
import com.oghab.mapviewer.utils.mv_utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class Server {
    Context ctx;

//    public static final String TAG_NOTIFICATION = "NOTIFICATION_MESSAGE2222";
//    public static final String CHANNEL_ID = "channel_11112222";
//    public static final int NOTIFICATION_ID = 1111112222;
//    public static final String STR_NOTIFICATION_ID = "1111112222";

    private boolean terminated = false;
    public ServerHandler serverHandler = null;
    static public String server_ip = null;

    Server(Context context){
        this.ctx = context;
        MainActivity.MyLogInfo("MapViewer Launcher");

//        IntentFilter intentFilter=new IntentFilter();
//        intentFilter.addAction("com.oghab.mapviewer.ACTION_PAUSE_MUSIC");
//        context.registerReceiver(receiver,intentFilter);
    }

    public String getIP(){
        return server_ip;
    }

    public void start(int port) {
        try {
            stop();
            terminated = false;
            serverHandler = new ServerHandler(port);
            serverHandler.start();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    public void stop() {
        try
        {
            terminated = true;

            if(serverHandler != null){
                serverHandler.close();
                serverHandler = null;
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public class ServerHandler extends Thread {
        public ServerSocket serverSocket = null;
        int port;

        public ServerHandler(int port) {
            this.port = port;
            try{
                if(serverSocket == null) {
                    serverSocket = new ServerSocket(); // <-- create an unbound socket first
                    serverSocket.setReuseAddress(true);
                    serverSocket.bind(new InetSocketAddress(port)); // <-- now bind it
                }
            }
            catch (Throwable ex)
            {
                terminated = true;
                MainActivity.MyLog(ex);
            }
        }

        public void close(){
            try{
                if(!is_server_socket_closed(serverSocket)){
                    if(serverSocket != null)    serverSocket.close();
                    serverSocket = null;
                }
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        }

        public void run() {
            try {
                terminated = false;
//                try{
//                    if(serverSocket == null) {
//                        serverSocket = new ServerSocket(); // <-- create an unbound socket first
//                        serverSocket.setReuseAddress(true);
//                        serverSocket.bind(new InetSocketAddress(port)); // <-- now bind it
//                    }
//                }
//                catch (Throwable ex)
//                {
//                    terminated = true;
//                    MainActivity.MyLog(ex);
//                }

                int idx = 0;
                while (!terminated){
                    try
                    {
                        Log.d("Service_Server",Integer.toString(idx++));
                        if(is_server_socket_connected(serverSocket)){
                            if(serverSocket != null){
                                try{
                                    Socket socket = serverSocket.accept();
                                    if(socket != null){
                                        String client_ip = socket.getInetAddress().getHostAddress();
                                        socket.close();
                                        mv_utils.call_mapviewer(ctx, client_ip, "MapViewer Server");
//                                        playSyrianArmyAnthem(ctx);
                                    }else{
                                        terminated = true;
                                        break;
                                    }
                                }
                                catch (Throwable ex)
                                {
                                    // MyLog(ex);// silent exception
                                    terminated = true;
                                    break;
                                }
                            }else{
                                terminated = true;
                                break;
                            }
                        }else{
                            terminated = true;
                            break;
                        }
                        if(!MapViewerService.isServiceStarted){
                            terminated = true;
                            break;
                        }
                    }
                    catch (Throwable ex)
                    {
                        terminated = true;
                        MainActivity.MyLog(ex);
                    }
                }
                if(serverSocket != null){
                    serverSocket.close();
                    serverSocket = null;
                }
            }
            catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
            }
        }
    }

    static public boolean is_server_socket_connected(ServerSocket socket){
        return (socket != null) && socket.isBound() && (!socket.isClosed());
    }

    static public boolean is_server_socket_closed(ServerSocket socket){
        return (socket != null) && socket.isBound() && socket.isClosed();
    }

    // notification method to support opening activities on Android 10
//    public static void startActivityNotification(Context context, int notificationID,
//                                                 String title, String message) {
//        NotificationManager mNotificationManager =
//                (NotificationManager)
//                        context.getSystemService(Context.NOTIFICATION_SERVICE);
//        //Create GPSNotification builder
//        NotificationCompat.Builder mBuilder;
//
//        //Initialise ContentIntent
//        PackageManager pm = context.getPackageManager();
//        Intent ContentIntent = pm.getLaunchIntentForPackage("com.oghab.mapviewer");
//
////        Intent ContentIntent = new Intent(context, ExampleActivity.class);
//        ContentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
//                Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent ContentPendingIntent = PendingIntent.getActivity(context,
//                0,
//                ContentIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        mBuilder = new NotificationCompat.Builder(context, STR_NOTIFICATION_ID)
//                .setSmallIcon(R.drawable.oghab_mapviewer2)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setColor(context.getResources().getColor(R.color.colorPrimaryDark))
//                .setAutoCancel(true)
//                .setContentIntent(ContentPendingIntent)
//                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID,
//                    "Activity Opening Notification",
//                    NotificationManager.IMPORTANCE_HIGH);
//            mChannel.enableLights(true);
//            mChannel.enableVibration(true);
//            mChannel.setDescription("Activity opening notification");
//
//            mBuilder.setChannelId(CHANNEL_ID);
//
//            Objects.requireNonNull(mNotificationManager).createNotificationChannel(mChannel);
//        }
//
//        Objects.requireNonNull(mNotificationManager).notify(TAG_NOTIFICATION,notificationID,
//                mBuilder.build());
//    }

//    private final BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if(action == null)  return;
//            if(action.equals("com.oghab.mapviewer.ACTION_PAUSE_MUSIC")){
//                try
//                {
//                    if(syrian_army_ring != null){
//                        syrian_army_ring.stop();
//                        syrian_army_ring.release();
//                        syrian_army_ring = null;
//                    }
//                    Toast.makeText(context, "Sound Stopped", Toast.LENGTH_LONG).show();
//                }
//                catch (Throwable ex)
//                {
//                    MainActivity.MyLog(ex);
//                }
//            }
//        }
//    };

}
