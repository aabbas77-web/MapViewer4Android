package com.oghab.mapviewer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.oghab.mapviewer.mapviewer.Tab_Messenger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChangeCallStateListener extends BroadcastReceiver {
    private static String lastState = TelephonyManager.EXTRA_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;  //because the passed incoming is only valid in ringing

    @Override
    public void onReceive(Context context, Intent intent) {
        Tab_Messenger.showToast("MapViewer: CallReceiver is starting ....");

//        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
//        telephony.listen(new PhoneStateListener(){
//            @Override
//            public void onCallStateChanged(int state, String incomingNumber) {
//                super.onCallStateChanged(state, incomingNumber);
//                System.out.println("incomingNumber : "+incomingNumber);
//            }
//        },PhoneStateListener.LISTEN_CALL_STATE);

        List<String> keyList = new ArrayList<>();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            keyList = new ArrayList<>(bundle.keySet());
            Log.e("CallObserver", "keys : " + keyList);
        }

        if (keyList.contains("incoming_number")) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String phoneIncomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            String phoneOutgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            String phoneNumber = phoneOutgoingNumber != null ? phoneOutgoingNumber : (phoneIncomingNumber != null ? phoneIncomingNumber : "");

            if (phoneState != null) {
                if (lastState.equals(phoneState)) {
                    //No change, debounce extras
                    return;
                }
                Log.e("CallObserver", "phoneState = " + phoneState);
                if (TelephonyManager.EXTRA_STATE_RINGING.equals(phoneState)) {
                    isIncoming = true;
                    callStartTime = new Date();
                    lastState = TelephonyManager.EXTRA_STATE_RINGING;
                    savedNumber = phoneNumber;
                    onIncomingCallStarted(context, savedNumber, callStartTime);
                } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(phoneState)) {
                    if (lastState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        lastState = TelephonyManager.EXTRA_STATE_IDLE;
                        onMissedCall(context, savedNumber, callStartTime);
                    } else {
                        if (isIncoming) {
                            lastState = TelephonyManager.EXTRA_STATE_IDLE;
                            onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                        } else {
                            lastState = TelephonyManager.EXTRA_STATE_IDLE;
                            Log.d("CallObserver", "onOutgoingCallEnded called !! : ");
                            onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                        }
                    }
                } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(phoneState)) {
                    isIncoming = lastState.equals(TelephonyManager.EXTRA_STATE_RINGING);
                    callStartTime = new Date();
                    savedNumber = phoneNumber;
                    lastState = TelephonyManager.EXTRA_STATE_OFFHOOK;
                    onOutgoingCallStarted(context, savedNumber, callStartTime);
                }
            }
        }
    }

    protected void onIncomingCallStarted(Context context, String number, Date start) {
        Tab_Messenger.showToast("MapViewer: onIncomingCallStarted number is " + number +",Started at "+start);
    }

    protected void onOutgoingCallStarted(Context context, String number, Date start) {
        Tab_Messenger.showToast("MapViewer: onOutgoingCallStarted number is " + number +",Started at "+start);
    }

    protected void onIncomingCallEnded(Context context, String number, Date start, Date end) {
        Tab_Messenger.showToast("MapViewer: onIncomingCallEnded number is " + number +",Started at "+start+", Finished at "+end);
    }

    protected void onOutgoingCallEnded(Context context , String number, Date start, Date end) {
        Tab_Messenger.showToast("MapViewer: onOutgoingCallEnded number is " + number +",Started at "+start+", Finished at "+end);
    }

    protected void onMissedCall(Context context, String number, Date start) {
        Tab_Messenger.showToast("MapViewer: onMissedCall number is " + number +",Started at "+start);
    }
}
