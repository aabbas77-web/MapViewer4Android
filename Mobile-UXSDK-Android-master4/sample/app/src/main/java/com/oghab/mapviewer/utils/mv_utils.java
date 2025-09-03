package com.oghab.mapviewer.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.NotificationView;
import com.oghab.mapviewer.R;
import com.oghab.mapviewer.mapviewer.MyMarker;
import com.oghab.mapviewer.mapviewer.MyPolygon;
import com.oghab.mapviewer.mapviewer.MyPolyline;
import com.oghab.mapviewer.mapviewer.Tab_Messenger;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayWithIW;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class mv_utils {
    public static int multi_line_text_max_length(String input) {
        String[] arrOfstr = input.split("\n");
        int max_length = 0;
        int len;
        for (String s : arrOfstr) {
            len = s.length();
            if (len > max_length) {
                max_length = len;
            }
        }
        return max_length;
    }

    public static String multi_line_text_max_line(String input) {
        String[] arrOfstr = input.split("\n");
        int max_length = 0;
        int len;
        String max_line = "";
        for (String s : arrOfstr) {
            len = s.length();
            if (len > max_length) {
                max_length = len;
                max_line = s;
            }
        }
        return max_line;
    }

    static public String remove_extension(String filename){
        try{
            if (filename.indexOf(".") > 0) {
                filename = filename.substring(0, filename.lastIndexOf("."));
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return filename;
    }

    @ColorInt
    public static int adjustAlpha(@ColorInt int color, int alpha) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    static public int hashCode(double fLon, double fLat){
        return (String.format(Locale.ENGLISH,"%.06f", fLon)+","+String.format(Locale.ENGLISH,"%.06f", fLat)).hashCode();
    }

    static public String colorToString(int color){
        return String.format(Locale.ENGLISH,"#%06X", (0xFFFFFF & color));
    }

    static public String colorAlphaToString(int color){
        return String.format(Locale.ENGLISH,"#%08X", color);
    }

    static public int parseColor(String text){
        int value = 0;
        try {
            value = Color.parseColor(text);
        }
        catch (NumberFormatException ignored)
        {

        }
        return value;
    }

    static public int parseInt(String text){
        int value = 0;
        try {
            value = Integer.parseInt(text);
        }
        catch (NumberFormatException ignored)
        {

        }
        return value;
    }

    static public float parseFloat(String text){
        float value = 0.0f;
        try {
            value = Float.parseFloat(text);
        }
        catch (NumberFormatException ignored)
        {

        }
        return value;
    }

    static public double parseDouble(String text){
        double value = 0.0;
        try {
            value = Double.parseDouble(text);
        }
        catch (NumberFormatException ignored)
        {

        }
        return value;
    }

//    static MediaPlayer syrian_army_ring = null;
//    public static void playSyrianArmyAnthem(Context context) {
//        try {
//            if(syrian_army_ring == null){
//                syrian_army_ring = MediaPlayer.create(context, R.raw.syrian_army);
//            }
//            if (syrian_army_ring.isPlaying()) {
//                syrian_army_ring.stop();
//                syrian_army_ring.release();
//                syrian_army_ring = null;
//                syrian_army_ring = MediaPlayer.create(context, R.raw.syrian_army);
//            }
//            syrian_army_ring.setOnCompletionListener(m -> {
//                try
//                {
//                    if(syrian_army_ring != null){
//                        syrian_army_ring.stop();
//                        syrian_army_ring.release();
//                        syrian_army_ring = null;
//                    }
//                }
//                catch (Throwable ex)
//                {
//                    MainActivity.MyLog(ex);
//                }
//            });
//            syrian_army_ring.start();
//        }
//        catch (Throwable ex)
//        {
//            MainActivity.MyLog(ex);
//        }
//    }

    static MediaPlayer resource_ring = null;
    public static void playResource(Context context, int resId) {
        try {
            if(resource_ring == null){
                resource_ring = MediaPlayer.create(context, resId);
            }
            if(resource_ring == null)   return;
            if (resource_ring.isPlaying()) {
                resource_ring.stop();
                resource_ring.release();
                resource_ring = null;
                resource_ring = MediaPlayer.create(context, resId);
            }
            resource_ring.setOnCompletionListener(m -> {
                try
                {
                    if(resource_ring != null){
                        resource_ring.stop();
                        resource_ring.release();
                        resource_ring = null;
                    }
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            });
            resource_ring.start();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    static MediaPlayer voice = null;
    public static void playVoice(Context context, String filename) {
        try {
            if(voice == null){
                voice = MediaPlayer.create(context, Uri.fromFile(new File(filename)));
            }
            if(voice == null)   return;
            if (voice.isPlaying()) {
                voice.stop();
                voice.release();
                voice = null;
                voice = MediaPlayer.create(context, Uri.fromFile(new File(filename)));
            }
            voice.setOnCompletionListener(m -> {
                try
                {
                    if(voice != null){
                        voice.stop();
                        voice.release();
                        voice = null;
                    }
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            });
            voice.start();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

//    static int notificationID = 123456;
//    static public void addNotification(Context context) {
//        try{
//            NotificationCompat.Builder builder =
//                    new NotificationCompat.Builder(context, "channelID_mapviewer")
//                            .setSmallIcon(R.drawable.oghab_mapviewer2)
//                            .setContentTitle("Notifications Example")
//                            .setContentText("This is a test notification");
//
//            Intent notificationIntent = new Intent(context, MainActivity.class);
//            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//            builder.setContentIntent(contentIntent);
//
//            // Add as notification
//            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            manager.notify(notificationID, builder.build());
//        } catch (Throwable ex) {
//            MainActivity.MyLog(ex);
//        }
//    }

//    static int numMessages = 0;
//    static public void displayNotification(Context context) {
////        Log.i("Start", "notification");
//
//        /* Invoking the default notification service */
//        NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(context, "channelID_mapviewer");
//
//        mBuilder.setContentTitle("New Message");
//        mBuilder.setContentText("You've received new message.");
//        mBuilder.setTicker("New Message Alert!");
//        mBuilder.setSmallIcon(R.drawable.oghab_mapviewer2);
//
//        /* Increase notification number every time a new notification arrives */
//        mBuilder.setNumber(++numMessages);
//
//        /* Add Big View Specific Configuration */
//        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
//
//        String[] events = new String[6];
//        events[0] = new String("This is first line....");
//        events[1] = new String("This is second line...");
//        events[2] = new String("This is third line...");
//        events[3] = new String("This is 4th line...");
//        events[4] = new String("This is 5th line...");
//        events[5] = new String("This is 6th line...");
//
//        // Sets a title for the Inbox style big view
//        inboxStyle.setBigContentTitle("Big Title Details:");
//
//        // Moves events into the big view
//        for (int i=0; i < events.length; i++) {
//            inboxStyle.addLine(events[i]);
//        }
//
//        mBuilder.setStyle(inboxStyle);
//
//        /* Creates an explicit intent for an Activity in your app */
//        Intent resultIntent = new Intent(context, NotificationView.class);
//
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(NotificationView.class);
//
//        /* Adds the Intent that starts the Activity to the top of the stack */
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
//
//        mBuilder.setContentIntent(resultPendingIntent);
//
//        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // notificationID allows you to update the notification later on.
//        mNotificationManager.notify(notificationID, mBuilder.build());
//    }

//    static public void showNotification(Context context, String title, String contents, String ip, boolean withSound){
//        try{
////            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "notify_001");
////            Intent ii = new Intent(context, MainActivity.class);
////            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, 0);
////
//////            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
//////            bigText.bigText("notificationsTextDetailMode"); //detail mode is the "expanded" notification
//////            bigText.setBigContentTitle("notificationTitleDetailMode");
//////            bigText.setSummaryText("usuallyAppVersionOrNumberOfNotifications"); //small text under notification
////
////            mBuilder.setContentIntent(pendingIntent);
////            mBuilder.setSmallIcon(R.mipmap.ic_launcher); //notification icon
////            mBuilder.setContentTitle("Oghab MapViewer V4.0"); //main title
////            mBuilder.setContentText(contents); //main text when you "haven't expanded" the notification yet
////            mBuilder.setPriority(Notification.PRIORITY_MAX);
//////            mBuilder.setStyle(bigText);
////
//////            Uri alarmSound = Uri.parse("android.resource://com.oghab.mapviewer/raw/syrian_army");
//////            Uri alarmSound = Uri.parse("android.resource://com.oghab.mapviewer/" + R.raw.syrian_army);
//////            Uri alarmSound = Uri.parse("android.resource://" + context.getApplicationContext() .getPackageName() + "/" + R.raw.syrian_army);
////            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//////            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
////            mBuilder.setSound(alarmSound);
////
////            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
////
////            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
////                NotificationChannel channel = new NotificationChannel("notify_001",
////                        "Oghab MapViewer V4.0",
////                        NotificationManager.IMPORTANCE_HIGH);
////                AudioAttributes audioAttributes = new AudioAttributes.Builder()
////                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
////                        .setUsage(AudioAttributes.USAGE_ALARM)
////                        .build();
////                channel.setSound(alarmSound, audioAttributes);
////                if (mNotificationManager != null) {
////                    mNotificationManager.createNotificationChannel(channel);
////                }
////            }
////
////            if (mNotificationManager != null) {
////                mNotificationManager.notify(notificationID, mBuilder.build());
////            }
//
//            String CHANNEL_ID="1234";
//
////            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ context.getApplicationContext().getPackageName() + "/" + R.raw.syrian_army);
////            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" +context.getPackageName()+"/"+R.raw.syrian_army);
//            Uri soundUri = null;
//            if(withSound)   soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" +context.getPackageName()+"/"+R.raw.syrian_army);
//
//            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//            //For API 26+ you need to put some additional code like below:
//            NotificationChannel mChannel;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                mChannel = new NotificationChannel(CHANNEL_ID, "Oghab MapViewer V4.0", NotificationManager.IMPORTANCE_HIGH);
//                mChannel.setLightColor(Color.GRAY);
//                mChannel.enableLights(true);
//                mChannel.setDescription("Oghab MapViewer V4.0");
//                AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                        .build();
//                mChannel.setSound(soundUri, audioAttributes);
//
//                if (mNotificationManager != null) {
//                    mNotificationManager.createNotificationChannel( mChannel );
//                }
//            }
//
//            Intent notificationIntent = new Intent(context, MainActivity.class);
//            notificationIntent.putExtra("notification_ip",ip);
//            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            String avatarPath = Tab_Messenger.get_ip_avatar(ip);
//            Bitmap bmp = BitmapFactory.decodeFile(avatarPath);
//
//            //General code:
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID);
//            builder.setAutoCancel(false)
//                    .setWhen(System.currentTimeMillis())
//                    .setSmallIcon(R.drawable.oghab_mapviewer2)
//                    .setLargeIcon(bmp)
//                    //.setOnlyAlertOnce(true)
//                    .setContentTitle(title)
//                    .setContentText(contents)
//                    .setVibrate(new long[]{0, 500, 1000})
//                    .setDefaults(Notification.DEFAULT_LIGHTS)
//                    .setSound(soundUri)
//                    .setContentIntent(contentIntent);
////                    .setContent(views);
//
//            mNotificationManager.notify(notificationID, builder.build());
//        } catch (Throwable ex) {
//            MainActivity.MyLog(ex);
//        }
//    }

    static public String append_chars(String text,char c,int length,boolean right)
    {
        if(right)
            return append_chars_from_right(text,c,length);
        else
            return append_chars_from_left(text,c,length);
    }

    static public String append_chars_from_right(String text,char c,int length)
    {
        StringBuilder res = new StringBuilder();
        try {
            int n = text.length();
            if(n < length) {
                res.append(text);
                for (int i = 0; i < length-n; i++) {
                    res.append(c);
                }
            }
            else
            {
                for (int i = 0; i < length; i++) {
                    res.append(text.charAt(i));
                }
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return res.toString();
    }

    static public String append_chars_from_left(String text,char c,int length)
    {
        StringBuilder res = new StringBuilder();
        try {
            int n = text.length();
            if(n < length) {
                for (int i = 0; i < length-n; i++) {
                    res.append(c);
                }
                res.append(text);
            }
            else
            {
                for (int i = 0; i < length; i++) {
                    res.append(text.charAt(i));
                }
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return res.toString();
    }

    static public Drawable getDrawable(Context context, int id){
        Drawable d = null;
        try {
            if (Build.VERSION.SDK_INT < 21) {
                // Old method, drawables cannot contain theme references.
                d = context.getResources().getDrawable(id);
            } else {
                // Drawables on API 21 can contain theme attribute references.
                // Context#getDrawable only exists since API 21.
                d = context.getDrawable(id);
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return d;
    }

    static public GeoPoint getPosition(Overlay item){
        GeoPoint p = new GeoPoint(0.0,0.0);
        try {
            if(item instanceof MyMarker)
            {
                MyMarker marker0 = (MyMarker) item; //the marker on which you click to open the bubble
                p = marker0.getPosition();
            }
            else
            if(item instanceof MyPolyline)
            {
                MyPolyline polyline0 = (MyPolyline) item;
                p = polyline0.getPosition();
            }
            else
            if(item instanceof MyPolygon)
            {
                MyPolygon polygon0 = (MyPolygon) item;
                p = polygon0.getPosition();
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return p;
    }

    static public void refresh_fragment(Fragment fragment){
        try {
            FragmentManager fm = MainActivity.activity.getSupportFragmentManager();
            // create a FragmentTransaction to begin the transaction and replace the Fragment
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.detach(fragment);
            fragmentTransaction.attach(fragment);
            fragmentTransaction.commit();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }

// Reload current fragment
//        Fragment frg = null;
//        frg = getSupportFragmentManager().findFragmentByTag("Your_Fragment_TAG");
//        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.detach(frg);
//        ft.attach(frg);
//        ft.commit();
    }

    static int notificationID = 12345;
    static public void showNotification(Context context, String title, String contents, String ip, boolean withSound){
        try{
            String CHANNEL_ID="12345sdfsdfd";

            Uri soundUri = null;
            if(withSound){
                soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + com.oghab.mapviewer.R.raw.syrian_army);
                Log.d("MapViewer", "soundUri: "+soundUri.toString());
            }

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            //For API 26+ you need to put some additional code like below:
            NotificationChannel mChannel;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mChannel = new NotificationChannel(CHANNEL_ID, "Oghab MapViewer V4.0", NotificationManager.IMPORTANCE_HIGH);
                mChannel.setLightColor(Color.GRAY);
                mChannel.enableLights(true);
                mChannel.setDescription("Oghab MapViewer V4.0");
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                mChannel.setSound(soundUri, audioAttributes);// for Android >= 8 (Oreo)

                if (mNotificationManager != null) {
                    mNotificationManager.createNotificationChannel( mChannel );
                }
            }

            PendingIntent contentIntent = null;
//            Intent notificationIntent = new Intent(context, MainActivity.class);
            PackageManager pm = context.getPackageManager();
            Intent notificationIntent = pm.getLaunchIntentForPackage("com.oghab.mapviewer");
            if(notificationIntent != null){
                notificationIntent.putExtra("notification_ip",ip);
                contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
            }

            String avatarPath = Tab_Messenger.get_ip_avatar(ip);
            Bitmap bmp = BitmapFactory.decodeFile(avatarPath);
//            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.oghab_mapviewer2);

//            Intent pauseIntent = new Intent("com.oghab.mapviewer.ACTION_PAUSE_MUSIC");
//            PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 1, pauseIntent, PendingIntent.FLAG_IMMUTABLE);

            //General code:
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID);
            builder.setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.oghab_mapviewer2)
                    .setLargeIcon(bmp)
                    //.setOnlyAlertOnce(true)
                    .setContentTitle(title)
//                    .addAction(R.drawable.pause_icon,"pause",pausePendingIntent)
                    .setContentText(contents)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVibrate(new long[]{0, 500, 1000})
                    .setLights(0xff0000ff, 300, 1000) // blue color
//                    .setDefaults(Notification.DEFAULT_LIGHTS)
//                    .setSound(soundUri)
                    .setContentIntent(contentIntent);
//                    .setContent(views);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder.setSound(alarmSound);
            }

            if (mNotificationManager != null) {
                Notification notification = builder.build();
                if(withSound) {// for Android < 8 (Oreo)
                    notification.sound = soundUri;

                    AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
                    if(manager != null) {
                        int notificationMaxVol = manager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
                        manager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, notificationMaxVol/2, AudioManager.FLAG_SHOW_UI);
                    }
                }
                mNotificationManager.notify(notificationID, notification);
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static MediaPlayer syrian_army_ring = null;
    public static void playSyrianArmyAnthem(Context context) {
        try {
            if(syrian_army_ring == null){
                syrian_army_ring = MediaPlayer.create(context, R.raw.syrian_army);
            }
            if(syrian_army_ring == null)   return;
            if (syrian_army_ring.isPlaying()) {
                syrian_army_ring.stop();
                syrian_army_ring.release();
                syrian_army_ring = null;
                syrian_army_ring = MediaPlayer.create(context, R.raw.syrian_army);
            }
            syrian_army_ring.setOnCompletionListener(m -> {
                try
                {
                    if(syrian_army_ring != null){
                        syrian_army_ring.stop();
                        syrian_army_ring.release();
                        syrian_army_ring = null;
                    }
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            });
            syrian_army_ring.start();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    static public void call_mapviewer(Context context, String client_ip, String name){
        try {
            // If android 10 or higher
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
//            {
//                startActivityNotification(ctx,NOTIFICATION_ID,ctx.getResources().getString(R.string.open_app), ctx.getResources().getString(R.string.click_app));
            showNotification(context, context.getString(R.string.you_have_a_mapviewer_call_from_ip)+client_ip+" - "+name, context.getString(R.string.how_are_you), client_ip, true);
//            }
//            else
//            {
//                // If lower than Android 10, we use the normal method ever.
//                PackageManager pm = ctx.getPackageManager();
//                Intent activity = pm.getLaunchIntentForPackage("com.oghab.mapviewer");
//                if(activity != null) {
//                    activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    ctx.startActivity(activity);
//                }
//            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }
}
