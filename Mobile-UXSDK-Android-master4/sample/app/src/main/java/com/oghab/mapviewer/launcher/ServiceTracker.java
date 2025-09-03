package com.oghab.mapviewer.launcher;

import android.content.Context;
import android.content.SharedPreferences;

import com.oghab.mapviewer.MainActivity;

/**
 * Save the state of the service with SharedPreference.
 */
public class ServiceTracker {
    /**
     * Enum of the state of the service.
     */
    public enum ServiceState {
        STARTED,
        STOPPED,
    }

    /**
     * SharedPreference key.
     */
    private String key = "MAPVIEWER_SERVICE_STATE";

    /**
     * Saved Service state with shared preference.
     *
     * @param context : App context.
     * @param state   : State of the service.
     */
    void setServiceState(Context context, ServiceState state) {
        try{
            SharedPreferences settings = getPreferences(context);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(key, state.name());
            editor.apply();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    /**
     * Get service state with SharedPreference.
     *
     * @param context : App context.
     * @return ServiceState : STARTED or STOPPED
     */
    public ServiceState getServiceState(Context context) {
        try{
            SharedPreferences settings = getPreferences(context);
            return ServiceState.valueOf(settings.getString(key, ServiceState.STOPPED.name()));
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
            return null;
        }
    }

    /**
     * To simplify the use of SharedPreference.
     *
     * @param context : App context.
     * @return SharedPreferences.
     */
    private SharedPreferences getPreferences(Context context) {
        try{
            // SharedPreference name.
            String name = "MAPVIEWER_SERVICE_KEY";
            return context.getSharedPreferences(name, 0);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
            return null;
        }
    }
}