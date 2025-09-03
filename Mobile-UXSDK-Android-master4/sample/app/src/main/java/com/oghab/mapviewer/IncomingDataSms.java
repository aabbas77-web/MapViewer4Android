package com.oghab.mapviewer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.oghab.mapviewer.mapviewer.Tab_Messenger;
import com.oghab.mapviewer.utils.mv_utils;

import java.util.ArrayList;
import java.util.Arrays;

public class IncomingDataSms extends BroadcastReceiver {

    String get_message(SmsMessage currentMessage){
        String message;
        StringBuilder messageData = new StringBuilder();
        byte[] data = currentMessage.getUserData();
        for (byte datum : data) {
            messageData.append((char)datum);
        }
        message = messageData.toString();
        return message;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action == null)  return;
        if (action.equals("android.intent.action.DATA_SMS_RECEIVED")) {
            try {
                Log.d("mapviewer","IncomingDataSms.onReceive");
                Log.d("mapviewer","intent.getAction(): "+intent.getAction());
                final Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    if (pdusObj != null) {
                        for (Object o : pdusObj) {
                            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) o);
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
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        }

//        Log.d("mapviewer","IncomingSms.onReceive");
//        Log.d("mapviewer","intent.getAction(): "+intent.getAction());
//        Log.d("mapviewer","intent.getExtras().toString(): "+intent.getExtras().toString());
//        mv_utils.call_mapviewer(context, "127.0.0.1", "IncomingDataSms");
//        if (intent.getAction().equals("android.intent.action.DATA_SMS_RECEIVED")) {
//
////            int port = 0;
////            Uri uri = intent.getData();
////            if(uri != null) {
////                port = uri.getPort();// Data SMS Port
////            }
////            if(port == Tab_Messenger.SMS_PORT)
////            {
////                final Bundle bundle = intent.getExtras();
////                try {
////                    if (bundle != null) {
////                        final Object[] pdusObj = (Object[])bundle.get("pdus");
////                        if(pdusObj != null){
////                            for (Object o : pdusObj) {
////                                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) o);
////                                String senderNumber = currentMessage.getDisplayOriginatingAddress();
////                                String message = get_message(currentMessage);
////                                if(message != null){
////                                    if (message.contains("mapviewer")) {
////                                        ArrayList<String> parameters = new ArrayList<String>(Arrays.asList(message.split(",")));
////                                        if(parameters.size() > 1) {
////                                            String ip = parameters.get(1);
////                                            String name = Tab_Messenger.get_ip_name(ip);
////                                            mv_utils.showNotification(context, "You have a MapViewer call from number: "+senderNumber, "With ip: "+ip+", and name: "+name, ip, false);
////                                        }else{
////                                            mv_utils.showNotification(context, "You have a MapViewer call from number: "+senderNumber, "", "", false);
////                                        }
////                                        mv_utils.playSyrianArmyAnthem(context);
////                                    }
////                                }
////                            }
////                        }
////                    }
////                } catch (Throwable ex) {
////                    MainActivity.MyLog(ex);
////                }
////            }
////            return;
//        }
    }
}
