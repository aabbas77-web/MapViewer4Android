package com.oghab.mapviewer.mapviewer;

import android.content.SharedPreferences;

import com.oghab.mapviewer.MainActivity;

import java.net.ServerSocket;
import java.net.Socket;

public class tcp_utils {

    static public boolean is_socket_connected(Socket socket){
        try
        {
//            return (socket != null) && socket.isConnected() && socket.isBound() && (!socket.isClosed());
            return (socket != null) && socket.isConnected();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    static public boolean is_socket_closed(Socket socket){
        try
        {
//            return (socket != null) && socket.isConnected() && socket.isBound() && socket.isClosed();
            return (socket != null) && socket.isClosed();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return true;
    }

    static public boolean is_server_socket_connected(ServerSocket socket){
        try
        {
//            return (socket != null) && socket.isBound() && (!socket.isClosed());
            return (socket != null) && (!socket.isClosed());
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    static public boolean is_server_socket_closed(ServerSocket socket){
        try
        {
//            return (socket != null) && socket.isBound() && socket.isClosed();
            return (socket != null) && socket.isClosed();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return true;
    }
}
