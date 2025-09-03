package com.oghab.mapviewer;

import static android.telephony.TelephonyManager.PHONE_TYPE_CDMA;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.oghab.mapviewer.mapviewer.Tab_Messenger;
import com.oghab.mapviewer.utils.mv_utils;

import java.util.ArrayList;
import java.util.Arrays;

public class IncomingSms extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action == null)  return;
        if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            try {
                Log.d("mapviewer","IncomingSms.onReceive");
                Log.d("mapviewer","intent.getAction(): "+intent.getAction());
                final Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    if (pdusObj != null) {
                        for (Object o : pdusObj) {
                            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) o);
                            if(currentMessage != null){
                                String senderNumber = currentMessage.getDisplayOriginatingAddress();
                                String message = currentMessage.getDisplayMessageBody();
                                Log.d("mapviewer","senderNumber: "+senderNumber);
                                Log.d("mapviewer","message: "+message);
                                if(message != null) {
                                    if (message.toLowerCase().contains("mapviewer")) {
                                        String ip = "";
                                        String name = "";
                                        String[] items = message.split(",");
                                        if(items.length >= 2) {
                                            ip = items[1].trim();
                                            name = Tab_Messenger.get_ip_name(ip);
                                        }
                                        Log.d("mapviewer","ip: "+ip);
                                        Log.d("mapviewer","name: "+name);
                                        mv_utils.call_mapviewer(context, ip, name);
                                        this.abortBroadcast();
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        }
    }
}
