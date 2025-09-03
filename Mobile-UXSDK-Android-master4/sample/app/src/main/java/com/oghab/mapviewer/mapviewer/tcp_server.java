package com.oghab.mapviewer.mapviewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;
import com.oghab.mapviewer.utils.mv_utils;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class tcp_server {
    private boolean terminated = false;
    public ServerHandler serverHandler = null;
    static public String server_ip = null;

    public String getIP(){
        return server_ip;
    }

    public void start(int port) {
        try {
            stop();
            terminated = false;
            serverHandler = new ServerHandler(port);
            serverHandler.start();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    public void stop() {
        try
        {
            terminated = true;

            if(serverHandler != null){
                serverHandler.close();
                serverHandler = null;
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public class ServerHandler extends Thread {
        public ServerSocket serverSocket = null;
        int port;
        Queue<tcp_io_handler> ioHandlers = new LinkedList<>();

        public ServerHandler(int port) {
            this.port = port;
            try{
                if(serverSocket == null) {
                    serverSocket = new ServerSocket(); // <-- create an unbound socket first
                    serverSocket.setReuseAddress(true);
                    serverSocket.bind(new InetSocketAddress(port)); // <-- now bind it
                }
            }
            catch (Throwable ex)
            {
                terminated = true;
                MainActivity.MyLog(ex);
            }

//            Tab_Messenger.set_ip_connected("192.168.1.9", true);

            // sockets thread
//            try {
//                Thread thread = new Thread() {
//                    @Override
//                    public void run() {
//                        final Handler handler = new Handler();
//                        Runnable runnable = new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    boolean value = false;
//
//                                    for(int i=0;i<Tab_Messenger.ipAdapter.users_list.size();i++){
//                                        tcp_user user = Tab_Messenger.ipAdapter.users_list.get(i);
//                                        value = !value;
//                                        Tab_Messenger.set_ip_connected(user.ip, value);
//                                        int finalI = i;
//                                        MainActivity.activity.runOnUiThread(() -> {
//                                            try {
//                                                Tab_Messenger.ipAdapter.notifyItemChanged(finalI);
//                                            } catch (Throwable ex) {
//                                                MainActivity.MyLog(ex);
//                                            }
//                                        });
//                                    }
//
////                                    for (tcp_io_handler io : ioHandlers) {
////                                        if (io != null) {
////                                            if(io.socket != null){
////                                                try{
////                                                    value = !value;
////                                                    Tab_Messenger.set_ip_connected(io.socket_ip, value);
////
//////                                                    for(int i=0;i<Tab_Messenger.ipAdapter.users_list.size();i++){
//////                                                        tcp_user user = Tab_Messenger.ipAdapter.users_list.get(i);
//////                                                        if(user.ip.equals(io.socket_ip)) {
//////                                                            value = !value;
//////                                                            Tab_Messenger.set_ip_connected(io.socket_ip, value);
////////                                                            Tab_Messenger.ipAdapter.users_list.get(i).setConnected(value);
//////                                                            int finalI = i;
//////                                                            MainActivity.activity.runOnUiThread(() -> {
//////                                                                try {
//////                                                                    Tab_Messenger.ipAdapter.notifyItemChanged(finalI);
//////                                                                } catch (Throwable ex) {
//////                                                                    MainActivity.MyLog(ex);
//////                                                                }
//////                                                            });
//////
////////                                                            if(user.setConnected(value)) {
//////////                                                        if(user.setConnected(tcp_utils.is_socket_connected(io.socket))) {
//////////                                                            Tab_Messenger.refresh_ip_status(io.socket_ip);
////////                                                            }
//////                                                            break;
//////                                                        }
//////                                                    }
////
//////                                                    tcp_user user = Tab_Messenger.get_ip_user(io.socket_ip);
//////                                                    if(user != null){
////////                                                        if(user.setConnected(io.socket.isConnected())) {
////////                                                        boolean value = (Math.round(10*Math.random()) >= 5);
//////                                                        value = !value;
//////                                                        if(user.setConnected(value)) {
////////                                                        if(user.setConnected(tcp_utils.is_socket_connected(io.socket))) {
////////                                                            Tab_Messenger.refresh_ip_status(io.socket_ip);
//////                                                        }
//////                                                    }
////                                                }catch (Throwable ex){
////                                                    MainActivity.MyLog(ex);
////                                                }
////                                            }
////                                        }
////                                    }
//
//                                    MainActivity.activity.runOnUiThread(() -> {
//                                        try {
//                                            Tab_Messenger.ipAdapter.notifyDataSetChanged();
//                                            Tab_Messenger.rv_ips.invalidate();
//                                        } catch (Throwable ex) {
//                                            MainActivity.MyLog(ex);
//                                        }
//                                    });
//                                } catch (Throwable ex){
//                                    MainActivity.MyLog(ex);
//                                }
//
//                                handler.postDelayed(this, 1000L);  // 1 second delay
//                            }
//                        };
//                        handler.post(runnable);
//                    }
//                };
//                thread.start();
//            } catch (Throwable ex){
//                MainActivity.MyLog(ex);
//            }
        }

        public void close(){
            try{
                if(!tcp_utils.is_server_socket_closed(serverSocket)){
                    if(serverSocket != null)    serverSocket.close();
                    serverSocket = null;
                }

                for (tcp_io_handler io : ioHandlers) {
                    if (io != null) {
                        io.close();
                    }
                }
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        }

        public void run() {
            try {
                terminated = false;
//                try{
//                    ioHandlers = new ArrayList<>();
//                    if(serverSocket == null) {
//                        serverSocket = new ServerSocket(); // <-- create an unbound socket first
//                        serverSocket.setReuseAddress(true);
//                        serverSocket.bind(new InetSocketAddress(port)); // <-- now bind it
//                    }
//                }
//                catch (Throwable ex)
//                {
//                    terminated = true;
//                    MainActivity.MyLog(ex);
//                }

//                MainActivity.activity.runOnUiThread(() -> {
//                    try
//                    {
//                        server_ip = Tab_Messenger.getIPAddress(true);
//                        if(Patterns.IP_ADDRESS.matcher(server_ip).matches()) {
//                            Tab_Messenger.tv_server_ip.setText(server_ip);
//                            Tab_Messenger.et_server_ip.setText(server_ip);
//                            Tab_Messenger.et_server_name.setText(Tab_Messenger.get_ip_name(server_ip));
//                            Tab_Messenger.et_server_phone_number.setText(Tab_Messenger.get_ip_phone_number(server_ip));
//                            Tab_Messenger.create_ip_folder(server_ip, false);
//                            Tab_Messenger.update_ip_list();
//
//                            Bundle bundle = MainActivity.activity.getIntent().getExtras();
//                            if (bundle != null) {
//                                //bundle contains all info of "data" field of the notification
//                                String ip = bundle.getString("notification_ip");
//                                if(ip  != null){
//                                    Toast.makeText(MainActivity.ctx, "From ip: "+ip, Toast.LENGTH_SHORT).show();
//                                    MapViewerView.process_click(R.id.radio_messenger);
//                                    Tab_Messenger.connect_with_waiting(ip);
//                                }else{
//                                    Tab_Messenger.connect_with_waiting(server_ip);
//                                }
//                            }else{
//                                Tab_Messenger.connect_with_waiting(server_ip);
//                            }
//                        }else{
//                            terminated = true;
//                            Tab_Messenger.addError("Invalid IP Address: "+server_ip);
//                        }
//                    }
//                    catch (Throwable ex)
//                    {
//                        terminated = true;
//                        MainActivity.MyLog(ex);
//                    }
//                });
//
//                if(!terminated){
//                    MainActivity.activity.runOnUiThread(() -> {
//                        try
//                        {
//                            mv_utils.playResource(MainActivity.ctx, R.raw.connect);
//                            if(Tab_Messenger.sw_listen != null){
//                                Tab_Messenger.sw_listen.setChecked(true);
//
//                            SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = settings.edit();
//                            editor.putBoolean("isListen", Tab_Messenger.sw_listen.isChecked());
//                            editor.apply();
//                            }
//                        }
//                        catch (Throwable ex)
//                        {
//                            terminated = true;
//                            MainActivity.MyLog(ex);
//                        }
//                    });
//                }

                if(tcp_utils.is_server_socket_connected(MainActivity.server.serverHandler.serverSocket)) {
                    mv_utils.playResource(MainActivity.ctx, R.raw.connect);
                }

                while (!terminated){
                    try
                    {
//                        if(!tcp_utils.is_server_socket_closed(serverSocket)){
                        if(tcp_utils.is_server_socket_connected(serverSocket)){
                            if(serverSocket != null){
                                try{
                                    Socket socket = serverSocket.accept();
                                    if(socket != null){
                                        tcp_io_handler clientIOHandler = new tcp_io_handler(socket, null);
                                        clientIOHandler.start();
                                        ioHandlers.add(clientIOHandler);

                                        // AutoConnect to client
//                                        String hostAddress = socket.getInetAddress().getHostAddress();
//                                        if((hostAddress != null) && (!hostAddress.equals(server_ip))) {
//                                            if ((MainActivity.client.socket == null) || (!MainActivity.client.socket.isConnected())) {
//                                                Tab_Messenger.connect_with_waiting(socket.getInetAddress().getHostAddress());
//                                            } else {
//                                                hostAddress = MainActivity.client.socket.getInetAddress().getHostAddress();
//                                                if ((hostAddress != null) && (hostAddress.equals(server_ip))) {
//                                                    Tab_Messenger.connect_with_waiting(socket.getInetAddress().getHostAddress());
//                                                }
//                                            }
//                                        }
                                    }else{
                                        terminated = true;
                                        break;
                                    }
                                }
                                catch (Throwable ex)
                                {
                                    // MainActivity.MyLog(ex);// silent exception
                                    terminated = true;
                                    break;
                                }
                            }else{
                                terminated = true;
                                break;
                            }
                        }else{
                            terminated = true;
                            break;
                        }
                    }
                    catch (Throwable ex)
                    {
                        terminated = true;
                        MainActivity.MyLog(ex);
                    }
                }
                if(serverSocket != null){
                    serverSocket.close();
                    serverSocket = null;
                }

                MainActivity.activity.runOnUiThread(() -> {
                    try
                    {
                        mv_utils.playResource(MainActivity.ctx, R.raw.disconnect);
//                        if(Tab_Messenger.sw_listen != null){
//                            Tab_Messenger.sw_listen.setChecked(false);
//
//                            SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = settings.edit();
//                            editor.putBoolean("isListen", Tab_Messenger.sw_listen.isChecked());
//                            editor.apply();
//                        }
                    }
                    catch (Throwable ex)
                    {
                        terminated = true;
                        MainActivity.MyLog(ex);
                    }
                });
            }
            catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
            }
        }
    }
}
