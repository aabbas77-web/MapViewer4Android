package com.oghab.mapviewer.mapviewer;

import android.graphics.Color;
import android.location.Location;
import android.os.Handler;

import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.utils.FileHelper;
import com.oghab.mapviewer.utils.mv_utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class tcp_io_handler extends Thread{
    public Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    public String socket_ip;
    private boolean terminated = false;
    private String strDir;
    private tcp_client client = null;

    static public int TCP_OK = 0;
    static public int TCP_ERROR = 1;
    static public boolean is_sending = false;
    static public boolean is_receiving = false;

//    static public Queue<Thread> threads = new LinkedList<>();

    public interface SendCallback {
        void onFinish(int error);
    }

    static public native void encode_file(String inFile, String outFile);
    static public native void decode_file(String inFile, String outFile);

    public tcp_io_handler(Socket socket, tcp_client client) {
        try {
            this.close();
            this.socket = socket;
            this.client = client;
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socket_ip = socket.getInetAddress().getHostAddress();
            strDir = Tab_Messenger.create_ip_folder(socket_ip, false);

            // update user status
            try{
                Tab_Messenger.set_ip_connected(socket_ip, true);
                Tab_Messenger.refresh_ip_status(socket_ip);

//                tcp_user user1 = Tab_Messenger.get_ip_user(socket_ip);
//                if(user1 != null){
//                    if(user1.setConnected(true)) {
//                        Tab_Messenger.refresh_ip_status(socket_ip);
//                    }
//                }
            }catch (Throwable ex){
                MainActivity.MyLog(ex);
            }

            // send thread
//            try {
//                Thread thread = new Thread() {
//                    @Override
//                    public void run() {
//                        while (!terminated){
//                            try {
//                                while (!threads.isEmpty()){
//                                    if (is_sending) continue;
//                                    Thread th = threads.remove();
//                                    if(th != null) {
//                                        th.start();
//                                    }
//                                }
//                            } catch (Throwable ex){
//                                MainActivity.MyLog(ex);
//                            }
//                        }
//                    }
//                };
//                thread.start();
//            } catch (Throwable ex){
//                MainActivity.MyLog(ex);
//            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void close(){
        try {
            terminated = true;

//            sendFileNative_Protected("close", "", "", false, MainActivity.is_encoded);

//            tcp_client.ping(socket_ip, mv_utils.parseInt(Tab_Messenger.et_server_port.getText().toString()),Tab_Messenger.connect_timeout);
//            tcp_client client = new tcp_client();
//            client.ping(socket_ip, mv_utils.parseInt(Tab_Messenger.et_server_port.getText().toString()),1000);

//            if(in != null){
//                in.close();
//                in = null;
//            }
//            if(out != null){
//                out.close();
//                out = null;
//            }
            if(!tcp_utils.is_socket_closed(socket)){
                if(socket != null)  socket.close();
                socket = null;
            }
//            if(socket != null){
//                if(!socket.isClosed())  socket.close();
//                socket = null;
//            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void sendMessage(String msg, boolean status) {
//        if(is_sending && status) return;
        try {
            if((msg == null) || (msg.isEmpty())) return;
            String strDir = MainActivity.strTCPPath;
            File dir = new File(strDir);
            if (!dir.exists()){
                if(!dir.mkdirs()){
                    MainActivity.MyLogInfo(strDir + " not created...");
                }
            }
            String strNewFile;
            if(status)
                strNewFile = strDir + "temp.sta";
            else
                strNewFile = strDir + "temp.lin";
            Tab_Messenger.write_line_to_file(strNewFile, msg);

            if(status) {
                sendFile(strNewFile, false, null, true, new SendCallback() {
                    @Override
                    public void onFinish(int error) {
                        if (error != TCP_OK) {
                            if (MainActivity.IsDebugJNI()) {
                                MainActivity.MyLogInfoSilent("The Current Line Number is " + Arrays.toString(new Throwable().getStackTrace()));
                            }
//                            MainActivity.set_fullscreen();
                        }
                    }
                });
            }
            else {
                sendFile(strNewFile, true, "Message sending...", true, new SendCallback() {
                    @Override
                    public void onFinish(int error) {
                        if (error != TCP_OK) {
                            if (MainActivity.IsDebugJNI()) {
                                MainActivity.MyLogInfoSilent("The Current Line Number is " + Arrays.toString(new Throwable().getStackTrace()));
                            }
//                            MainActivity.set_fullscreen();
                        }
                    }
                });
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public boolean sendLocation(SerializableLocation location) {
//        if(is_sending) return false;
        try {
            if(location == null) return false;
            Location loc = location.toLocation();
            if(loc == null) return false;
            if((loc.getLongitude() == 0) && (loc.getLatitude() == 0)){
                MainActivity.MyLogInfo("(0,0,0) is not invalid location.");
                return false;
            }
            String strDir = MainActivity.strTCPPath;
            File dir = new File(strDir);
            if (!dir.exists()){
                if(!dir.mkdirs()){
                    MainActivity.MyLogInfo(strDir + " not created...");
                }
            }
            String strNewFile = strDir + "temp.loc";

//            FileOutputStream fos = MainActivity.ctx.openFileOutput(strNewFile, Context.MODE_PRIVATE);
            FileOutputStream fos = new FileOutputStream(strNewFile);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(location);
            os.close();
            fos.close();

            sendFile(strNewFile, true, "Location sending...", true, new SendCallback() {
                @Override
                public void onFinish(int error) {
                    if(error != TCP_OK) {
                        if (MainActivity.IsDebugJNI()) {
                            MainActivity.MyLogInfoSilent("The Current Line Number is " + Arrays.toString(new Throwable().getStackTrace()));
                        }
//                        MainActivity.set_fullscreen();
                    }
                }
            });
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return true;
    }

    public native boolean sendFileNative(String command, String filename, String new_filename, String file_name, long filesize, boolean save, boolean is_encoded);

    public void sendFileNative_Protected(String command, String filename, String new_filename, boolean save, boolean is_encoded){
//        if(is_sending)  return;
        if(FileHelper.file_size(filename) <= 0){
            is_sending = false;
            if (((Tab_Camera.sw_broadcast_camera != null) && Tab_Camera.sw_broadcast_camera.isChecked()) || ((Tab_Map.sw_broadcast_map != null) && Tab_Map.sw_broadcast_map.isChecked())) {
                try {
                    Tab_Camera.isTakingFrame = false;
                } catch (Throwable ex) {
                    MainActivity.MyLog(ex);
                }
            }
            return;
        }
        is_sending = true;
        try{
            if(client != null){// Client
                client.checkConnection();
            }
            String file_name = FileHelper.file_name(filename);
            long filesize = FileHelper.file_size(filename);
            if(!sendFileNative(command, filename, new_filename, file_name, filesize, save, is_encoded)){
                try {
                    Tab_Messenger.addError("sendFileNative failed 1...");

                    if (((Tab_Camera.sw_broadcast_camera != null) && Tab_Camera.sw_broadcast_camera.isChecked()) || ((Tab_Map.sw_broadcast_map != null) && Tab_Map.sw_broadcast_map.isChecked())) {
                        try {
                            Tab_Camera.isTakingFrame = false;
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    }
                } catch (Throwable ex) {
                    MainActivity.MyLog(ex);
                }
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        is_sending = false;
        if (((Tab_Camera.sw_broadcast_camera != null) && Tab_Camera.sw_broadcast_camera.isChecked()) || ((Tab_Map.sw_broadcast_map != null) && Tab_Map.sw_broadcast_map.isChecked())) {
            try {
                Tab_Camera.isTakingFrame = false;
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        }
    }

    public void sendFile(String filename, boolean save, String status, boolean bAppendTime, SendCallback sendCallback) {
//        if(is_sending && (!save))  return;
        if(FileHelper.file_size(filename) <= 0){
            is_sending = false;
            if (((Tab_Camera.sw_broadcast_camera != null) && Tab_Camera.sw_broadcast_camera.isChecked()) || ((Tab_Map.sw_broadcast_map != null) && Tab_Map.sw_broadcast_map.isChecked())) {
                try {
                    Tab_Camera.isTakingFrame = false;
                } catch (Throwable ex) {
                    MainActivity.MyLog(ex);
                }
            }
            return;
        }
        try {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        // create new filename
                        String strDir = MainActivity.strTCPPath+ socket_ip +File.separator;
                        File dir = new File(strDir);
                        if (!dir.exists()){
                            if(!dir.mkdirs()){
                                MainActivity.MyLogInfo(strDir + " not created...");
                            }
                        }

                        if(bAppendTime){
                            // send file
                            String currentDateTime = MainActivity.sdf_filename.format(new Date());
                            String strNewFile = strDir+currentDateTime+"_sent_"+(new File(filename)).getName();
                            if((status != null) && (status.length() > 0))   Tab_Messenger.showStatus(status, Color.RED);
//                            if(MainActivity.is_encoded)
//                                sendFileNative_Protected("encoded", filename, strNewFile, save);
//                            else
//                                sendFileNative_Protected("", filename, strNewFile, save);
                            sendFileNative_Protected("file", filename, strNewFile, save, MainActivity.is_encoded);

                            String ext = FileHelper.fileExt(strNewFile);
                            if(ext != null) {
                                if (!ext.contains("sta")) {
                                    Tab_Messenger.addFile(strNewFile, true, false);
                                }
                            }
                        }else{
                            // send file
                            String strNewFile = strDir+(new File(filename)).getName();
                            if((status != null) && (status.length() > 0))   Tab_Messenger.showStatus(status, Color.RED);
//                            if(MainActivity.is_encoded)
//                                sendFileNative_Protected("encoded", filename, strNewFile, save);
//                            else
//                                sendFileNative_Protected("", filename, strNewFile, save);
                            sendFileNative_Protected("file", filename, strNewFile, save, MainActivity.is_encoded);
                        }
                        if(sendCallback != null){
                            sendCallback.onFinish(0);
                        }
                    } catch (Throwable ex){
//                        try {
//                            out.close();
//                            out = new PrintWriter(socket.getOutputStream());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }

                        MainActivity.MyLog(ex);
                        if(sendCallback != null){
                            sendCallback.onFinish(1);
                        }
                    }
                    is_sending = false;
                    if (((Tab_Camera.sw_broadcast_camera != null) && Tab_Camera.sw_broadcast_camera.isChecked()) || ((Tab_Map.sw_broadcast_map != null) && Tab_Map.sw_broadcast_map.isChecked())) {
                        try {
                            Tab_Camera.isTakingFrame = false;
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    }
                }
            };
//            thread.setDaemon(true);
//            thread.start();
            if(save) {
                thread.start();
//                threads.add(thread);
            }
            else {
                if(!is_sending) {
                    thread.start();
                }else{
                    if (((Tab_Camera.sw_broadcast_camera != null) && Tab_Camera.sw_broadcast_camera.isChecked()) || ((Tab_Map.sw_broadcast_map != null) && Tab_Map.sw_broadcast_map.isChecked())) {
                        try {
                            Tab_Camera.isTakingFrame = false;
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    }
                }
            }
//            thread.start();

//            MainActivity.set_fullscreen();
        } catch (Throwable ex){
            MainActivity.MyLog(ex);
        }
    }

    public native String receiveFileNative(String new_dir, String new_section, boolean is_encoded);

    public void run() {
        try {
//            if(client != null){// Til now: Client just send, and Server just receive
//                return;
//            }
//            strDir = Tab_Messenger.create_ip_folder(socket_ip, false);
//                    String strDir = MainActivity.strTCPPath + socket_ip + File.separator;
//                    File dir = new File(strDir);
//                    if (!dir.exists()) {
//                        if (!dir.mkdirs()) {
//                            MainActivity.MyLogInfo(strDir + " not created...");
//                        }
//                    }

            terminated = false;
            is_receiving = false;
            while (!terminated) {
                try
                {
                    String currentDateTime = MainActivity.sdf_filename.format(new Date());
                    String strSectionPath = currentDateTime + "_received_";

                    String strNewFile = "invalid";
                    try
                    {
//                        if(!is_receiving) {
//                            is_receiving = true;
                            strNewFile = receiveFileNative(strDir, strSectionPath, MainActivity.is_encoded);
//                            is_receiving = false;
//                        }
                    }
                    catch (Throwable ignored)
                    {
                        is_receiving = false;
                    }
                    if(strNewFile == null){
                        is_receiving = false;
                        if(client != null) {
//                            sendMessage("file received...", true);
                            break;
                        }
                        else {
//                            sendMessage("file received...", true);
                            continue;
                        }
                    }
                    if(strNewFile.equals("invalid")){
                        continue;
                    }
                    if(strNewFile.equals("close")){
                        is_receiving = false;
                        Tab_Messenger.sw_listen.setText(strNewFile);
                        sendMessage("file received...", true);
                        break;
                    }
                    if(FileHelper.file_size(strNewFile) <= 0){
                        is_receiving = false;
                        FileHelper.delete_file(strNewFile);
//                        sendMessage("file received...", true);
                        continue;
                    }

                    String strStatus = "";
                    if(socket_ip.equals(Tab_Messenger.active_ip)) {
                        Tab_Messenger.addFile(strNewFile, false, true);
                    }else{
                        Tab_Messenger.increment_ip_messages_count(socket_ip);
                        strStatus += ", I am not watching you now!!!";
                    }

                    String ext = FileHelper.fileExt(strNewFile);
                    if(ext != null) {
                        if (ext.contains("lin")) {
                            sendMessage("message received..."+strStatus, true);
                        }else if (ext.contains("loc")) {
                            sendMessage("location received..."+strStatus, true);
                        }else if (ext.contains("sta")) {

                        }
                        else{
                            if(ext.length() > 0) {
                                sendMessage("file received..." + strStatus, true);
                            }else{
                                FileHelper.delete_file(strNewFile);
                                //sendMessage("unknown file received..." + strStatus, true);
                            }
                        }
                    }else{
                        FileHelper.delete_file(strNewFile);
                        //sendMessage("unknown file received..." + strStatus, true);
                    }
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            }
            this.close();

            // update user status
            try{
                Tab_Messenger.set_ip_connected(socket_ip, false);
                Tab_Messenger.refresh_ip_status(socket_ip);

//                tcp_user user2 = Tab_Messenger.get_ip_user(socket_ip);
//                if(user2 != null){
//                    if(user2.setConnected(false)) {
//                        Tab_Messenger.refresh_ip_status(socket_ip);
//                    }
//                }
            }catch (Throwable ex){
                MainActivity.MyLog(ex);
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }
}
