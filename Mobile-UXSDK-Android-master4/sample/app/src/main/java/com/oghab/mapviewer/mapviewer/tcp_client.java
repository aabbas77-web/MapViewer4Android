package com.oghab.mapviewer.mapviewer;

import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.utils.mv_utils;

import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class tcp_client {
    public tcp_io_handler ioHandler = null;
    public Socket socket = null;
    public String ip;
    public int port;
    public int timeout;
    SocketAddress endPoint = null;

    public boolean checkConnection(){
        try {
            if(!tcp_utils.is_socket_connected(socket)){
                try{
                    stopConnection();
                    socket = new Socket();
                    socket.connect(endPoint, timeout);
                    ioHandler = new tcp_io_handler(socket, this);
                    ioHandler.start();
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                    return false;
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
            return false;
        }
        return true;
    }

    public boolean startConnection(String ip, int port, int timeout) {
        try {
            this.ip = ip;
            this.port = port;
            this.timeout = timeout;
            this.endPoint = new InetSocketAddress(ip, port);
            return checkConnection();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
            return false;
        }
    }

    public void stopConnection() {
        try {
            if(ioHandler != null){
                ioHandler.close();
                ioHandler = null;
            }

            if(!tcp_utils.is_socket_closed(socket)){
                if(socket != null)  socket.close();
            }

            socket = null;
        }
        catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public void ping(String ip, int port, int timeout, String msg){
        if(ip == null)  return;
        if(msg == null) return;
        try {
            Socket socket = new Socket();
            SocketAddress endPoint = new InetSocketAddress(ip, port);
            socket.connect(endPoint,timeout);
            if(msg.length() > 0) {
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println(msg);
                out.flush();
            }
            socket.close();
        }
        catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public boolean check_connection(String ip, int port, int timeout, boolean silent){
        if(ip == null)  return false;
        try {
            Socket socket = new Socket();
            SocketAddress endPoint = new InetSocketAddress(ip, port);
            socket.connect(endPoint,timeout);
            socket.close();
            return true;
        }
        catch (Throwable ex) {
            if(!silent) MainActivity.MyLog(ex);
            return false;
        }
    }

}
