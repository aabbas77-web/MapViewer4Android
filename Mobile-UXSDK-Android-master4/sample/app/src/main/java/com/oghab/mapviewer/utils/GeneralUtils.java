package com.oghab.mapviewer.utils;

import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.mapviewer.Tab_Messenger;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;

/**
 * Created by dji on 15/12/18.
 */

public class GeneralUtils {
    public static final double ONE_METER_OFFSET = 0.00000899322;
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        try {
            long time = System.currentTimeMillis();
            long timeD = time - lastClickTime;
            if (0 < timeD && timeD < 800) {
                return true;
            }
            lastClickTime = time;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    public static boolean checkGpsCoordinate(double latitude, double longitude) {
        try {
            return (latitude > -90 && latitude < 90 && longitude > -180 && longitude < 180) && (latitude != 0f
                    && longitude != 0f);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    public static double toRadian(double x) {
        try {
            return x * Math.PI / 180.0;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return 0;
    }

    public static double toDegree(double x) {
        try {
            return x * 180 / Math.PI;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return 0;
    }

    public static double cosForDegree(double degree) {
        try {
            return Math.cos(degree * Math.PI / 180.0f);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return 0;
    }

    public static double calcLongitudeOffset(double latitude) {
        try {
            return ONE_METER_OFFSET / cosForDegree(latitude);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return 0;
    }

    public static void addLineToSB(StringBuffer sb, String name, Object value) {
        try {
            if (sb == null) return;
            sb.
                    append(name == null ? "" : name + ": ").
                    append(value == null ? "" : value + "").
                    append("\n");
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public static CommonCallbacks.CompletionCallback getCommonCompletionCallback() {
        return djiError -> {
            try {
                Tab_Messenger.showToast(djiError == null ? "Succeed!" : "failed!" + djiError.getDescription());
            }
            catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
            }
        };
    }
}
