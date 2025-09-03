package com.oghab.mapviewer.launcher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class MapViewerService extends Service {
    private static final String TAG = "MapViewerService";

    /**
     * Initialize PowerManager.WakeLock
     * So that our service will not be impacted by Doze Mode.
     */
    private PowerManager.WakeLock wakeLock = null;
    /**
     * Boolean if our service is started or not.
     */
    static public boolean isServiceStarted = false;

    Server server = null;

    Context ctx;
    Activity activity;

    static private void writeToLog(String data) {
        try {
            File file;
            file = new File(strLogFile);
            FileOutputStream fOut = new FileOutputStream(file, true);
            data += "\n";
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
            writeToLog(strMessage);
        } catch (Throwable e) {
            Log.d(TAG, e.toString());
        }
    }

    static public void MyLogInfo(String strMessage) {
        try {
            String currentDateTime = sdf.format(new Date());
            String strNewMessage = "Info: " + currentDateTime + "\n" + strMessage;
            Log.d(TAG, strNewMessage);
            writeToLog(strNewMessage);
        } catch (Throwable ex) {
            Log.d(TAG, ex.toString());
        }
    }

    static public void MyLogInfoEx(Throwable ex) {
        try {
            String currentDateTime = sdf.format(new Date());
            String strMessage = "<MapViewer Exception>\nTime: " + currentDateTime + "\nCause: " + ex.getCause() + "\nMessage: " + ex.getMessage() + "\nStackTrace: " + Arrays.toString(ex.getStackTrace()) + "\n<MapViewer Exception/>\n";
            Log.d(TAG, strMessage);
            writeToLog(strMessage);
        } catch (Throwable ex0) {
            Log.d(TAG, ex0.toString());
        }
    }

    static public void MyLogInfoSilent(String strMessage) {
        try {
            String currentDateTime = sdf.format(new Date());
            String strNewMessage = "Info: " + currentDateTime + "\n" + strMessage;
            Log.d(TAG, strNewMessage);
            writeToLog(strNewMessage);
        } catch (Throwable ex) {
            Log.d(TAG, ex.toString());
        }
    }

    /**
     * Override onBind method.
     *
     * @param intent : Intent.
     * @return always null.
     */
    @Override
    public IBinder onBind(Intent intent) {
        try{
            new log("Some component want to bind with the service");
        } catch (Throwable ex) {
            MyLog(ex);
        }
        return null;
    }

    /**
     * Override onStartCommand method.
     *
     * @param intent  : Intent.
     * @param flags   : Flags.
     * @param startId : startId.
     * @return START_STICKY.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{
            new log("onStartCommand executed with startId: " + startId);
            if (intent != null) {
                String action = intent.getAction();
                new log("using an intent with action " + action);
                if (action != null) {
                    if (action.equals(Actions.START.name())) startService();
                    else if (action.equals(Actions.STOP.name())) stopService();
                    else new log("This should never happen. No action in the received intent");
                }
            } else {
                new log("with a null intent. It has been probably restarted by the system.");
            }
        } catch (Throwable ex) {
            MyLog(ex);
        }
        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY;
    }

    static public String dir_internal;
    static public String strLogFile = "";

    /**
     * Override onCreate method.
     * Create the service in foreground.
     */
    @Override
    public void onCreate() {
        try{
            super.onCreate();
            new log("The service has been created".toUpperCase());

            ctx = this.getApplicationContext();
            dir_internal = Objects.requireNonNull(Objects.requireNonNull(ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)).getParentFile()).getAbsolutePath();
//            strLogFile = dir_internal + "/MapViewer/MapViewerLauncherLog.txt";
            strLogFile = dir_internal + "/MapViewerLauncherLog.txt";

            Notification notification = createNotification();
            if(notification != null){
                notification.flags = Notification.FLAG_ONGOING_EVENT;
                startForeground(notificationID, notification);
//            startForeground(1, null);
            }
        } catch (Throwable ex) {
            MyLog(ex);
        }
    }

    /**
     * Override onDestroy method.
     * Destroy the running service.
     */
    @Override
    public void onDestroy() {
        try{
            super.onDestroy();
            new log("The service has been destroyed".toUpperCase());
            Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();
        } catch (Throwable ex) {
            MyLog(ex);
        }
    }

    /**
     * Method executed when the service is running.
     */
    private void startService() {
        try{
            // If the service already running, do nothing.
            if (isServiceStarted) return;
            new log("Starting the foreground service task");
            isServiceStarted = true;
            new ServiceTracker().setServiceState(this, ServiceTracker.ServiceState.STARTED);

            // we need this lock so our service gets not affected by Doze Mode
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (pm != null) {
                wakeLock = pm.newWakeLock(1, "MapViewerService::lock");
                wakeLock.acquire(60 * 1000L /*1 minutes*/);
            }

            // Create a thread and loop while the service is running.
            Thread thread = new Thread(() -> {
                try{
//                    Server server = new Server(this.getBaseContext());
                    if(server == null) {
                        server = new Server(ctx);
                        server.start(2555);
                    }else{
                        new log("Server created previously.");
                    }
                } catch (Throwable ex) {
                    MyLog(ex);
                }
            });
            // Start thread.
            thread.start();
            Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show();
        } catch (Throwable ex) {
            MyLog(ex);
        }
    }

    /**
     * Method executed to stop the running service.
     */
    private void stopService() {
        try{
            new log("Stopping the foreground service");
            try {
                if(server != null){
                    server.stop();
                }
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
                stopForeground(true);
                stopSelf();
                Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                new log("Service stopped without being started: ${e.message}");
            }
            isServiceStarted = false;
            new ServiceTracker().setServiceState(this, ServiceTracker.ServiceState.STOPPED);
        } catch (Throwable ex) {
            MyLog(ex);
        }
    }

    /**
     * Method executed while the service is running.
     */
    private void pingFakeServer() {
        try{
            new log("Ping MapViewer Server");
        } catch (Throwable ex) {
            MyLog(ex);
        }
    }

    /**
     * Method to create the notification show to the user.
     *
     * @return Notification with all params.
     */
    static int notificationID = 1234567890;
    private Notification createNotification() {
        try{
            String notificationChannelId = "MAPVIEWER SERVICE CHANNEL";

            NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

            //For API 26+ you need to put some additional code like below:
            NotificationChannel mChannel;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mChannel = new NotificationChannel(notificationChannelId, "Oghab MapViewer V4.0", NotificationManager.IMPORTANCE_HIGH);
                mChannel.setLightColor(Color.GRAY);
                mChannel.enableLights(true);
                mChannel.setDescription("Oghab MapViewer V4.0");
//                AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                        .build();
//                mChannel.setSound(soundUri, audioAttributes);

                if (mNotificationManager != null) {
                    mNotificationManager.createNotificationChannel( mChannel );
                }
            }

            Intent notificationIntent = new Intent(ctx, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

//            PackageManager pm = ctx.getPackageManager();
//            Intent notificationIntent = pm.getLaunchIntentForPackage("com.oghab.mapviewer");
////            notificationIntent.putExtra("notification_ip",ip);
//            notificationIntent.putExtra("notification_ip","127.0.0.1");
//            PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//            String avatarPath = Tab_Messenger.get_ip_avatar(ip);
//            Bitmap bmp = BitmapFactory.decodeFile(avatarPath);
            Bitmap bmp = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.oghab_mapviewer2);

            //General code:
            NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx,notificationChannelId);
            builder.setAutoCancel(false)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setOngoing(true)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.oghab_mapviewer2)
                    .setLargeIcon(bmp)
                    .setOnlyAlertOnce(true)
                    .setContentTitle("MapViewer Service")
                    .setContentText("MapViewer service working...")
                    .setVibrate(new long[]{0, 500, 1000})
                    .setDefaults(Notification.DEFAULT_LIGHTS)
//                    .setSound(soundUri)
                    .setContentIntent(contentIntent);
//                    .setContent(views);

//            mNotificationManager.notify(notificationID, builder.build());
            return builder.build();
        } catch (Throwable ex) {
            MyLog(ex);
            return null;
        }
    }
//    private Notification createNotification() {
//        try{
//            String notificationChannelId = "MAPVIEWER SERVICE CHANNEL";
//
//            // depending on the Android API that we're dealing with we will have
//            // to use a specific method to create the notification
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                NotificationChannel channel = new NotificationChannel(
//                        notificationChannelId,
//                        "MapViewer Service notifications channel",
//                        NotificationManager.IMPORTANCE_HIGH
//                );
//                channel.setDescription("MapViewer Service channel");
//                channel.enableLights(true);
//                channel.setLightColor(Color.RED);
//                channel.enableVibration(true);
//                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//                if (notificationManager != null) {
//                    notificationManager.createNotificationChannel(channel);
//                }
//            }
//
//            Notification.Builder builder;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                builder = new Notification.Builder(
//                        this,
//                        notificationChannelId
//                );
//            } else {
//                builder = new Notification.Builder(this);
//            }
//
//            Intent notificationIntent = new Intent(this, MainActivity.class);
//            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            return builder
//                    .setContentTitle("MapViewer Service")
//                    .setContentText("MapViewer service working...")
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setTicker("MapViewer Ticker Text")
//                    .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
//                    .setContentIntent(contentIntent)
//                    .build();
//        } catch (Throwable ex) {
//            MyLog(ex);
//            return null;
//        }
//    }
}
