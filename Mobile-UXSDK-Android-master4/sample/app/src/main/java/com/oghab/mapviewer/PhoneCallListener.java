package com.oghab.mapviewer;

import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneCallListener extends PhoneStateListener {
    String LOG_TAG = "MapViewer";
    private boolean isPhoneCalling = false;

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {


        if (TelephonyManager.CALL_STATE_RINGING == state) {
            // phone ringing
            Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
        }

        if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
            // active
            Log.i(LOG_TAG, "OFFHOOK");

            isPhoneCalling = true;
        }

        if (TelephonyManager.CALL_STATE_IDLE == state) {
            // run when class initial and phone call ended, need detect flag
            // from CALL_STATE_OFFHOOK
            Log.i(LOG_TAG, "IDLE number");

            if (isPhoneCalling) {

                Handler handler = new Handler();

                //Put in delay because call log is not updated immediately when state changed
                // The dialler takes a little bit of time to write to it 500ms seems to be enough
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // get start of cursor
                        Log.i("CallLogDetailsActivity", "Getting Log activity...");
//                        String[] projection = new String[]{CallLog.Calls.NUMBER};
//                        Cursor cur = getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, null, null, CallLog.Calls.DATE +" desc");
//                        cur.moveToFirst();
//                        String lastCallnumber = cur.getString(0);
                    }
                },500);

                isPhoneCalling = false;
            }

        }
    }
}
