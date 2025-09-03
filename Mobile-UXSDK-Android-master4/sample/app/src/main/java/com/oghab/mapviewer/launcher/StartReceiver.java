package com.oghab.mapviewer.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.oghab.mapviewer.MainActivity;

import java.util.Objects;

/**
 * To start the service when phone wake up.
 */
public class StartReceiver extends BroadcastReceiver {

    /**
     * Override onReceive method.
     * Start the service if boot completed and if the service was running before.
     *
     * @param context : App context.
     * @param intent0  : Intent.
     */
    @Override
    public void onReceive(Context context, Intent intent0) {
        try{
//            if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED) && (new ServiceTracker().getServiceState(context) == ServiceTracker.ServiceState.STARTED)) {
//            if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
            if (Objects.equals(intent0.getAction(), Intent.ACTION_BOOT_COMPLETED) && ((new ServiceTracker().getServiceState(context)) == ServiceTracker.ServiceState.STARTED)) {
                Intent intent = new Intent(context, MapViewerService.class);
                intent.setAction(Actions.START.name());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    new log("Starting the service in >= 26 Mode");
                    context.startForegroundService(intent);
                    return;
                }
                new log("Starting the service in < 26 Mode");
                context.startService(intent);
                Toast.makeText(context, "StartReceiver onReceive successes...", android.widget.Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context, "StartReceiver onReceive failed...", android.widget.Toast.LENGTH_LONG).show();
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }
}
