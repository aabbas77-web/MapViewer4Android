package com.oghab.mapviewer.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;

import dji.common.error.DJIError;

/**
 * Created by dji on 2/3/16.
 */
public class DialogUtils {
    public static void showDialog(Context ctx, String str) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.set_dialog);
            builder.setMessage(str);
            builder.setNeutralButton(android.R.string.ok, (dialog, which) -> {
                try {
                    MainActivity.hide_keyboard(null);
                    dialog.dismiss();
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            });
            builder.create().show();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public static void showDialog(Context ctx, int strId) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.set_dialog);
            builder.setMessage(strId);
            builder.setNeutralButton(android.R.string.ok, (dialog, which) -> {
                try {
                    MainActivity.hide_keyboard(null);
                    dialog.dismiss();
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            });
            builder.create().show();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public static void showConfirmationDialog(Context ctx, int strId,
                                              DialogInterface.OnClickListener onClickListener) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.set_dialog);
            builder.setMessage(strId);
            builder.setCancelable(false);
            builder.setPositiveButton(android.R.string.ok, onClickListener);
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                try {
                    MainActivity.hide_keyboard(null);
                    dialog.dismiss();
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            });
            builder.create().show();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public static void showDialogBasedOnError(Context ctx, DJIError djiError) {
        try {
            if (null == djiError) {
                showDialog(ctx, R.string.success);
            } else {
                showDialog(ctx, djiError.getDescription());
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }
}
