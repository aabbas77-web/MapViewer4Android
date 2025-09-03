package com.oghab.mapviewer.mapviewer;

import com.oghab.mapviewer.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class tcp_user {
    public String ip;
    private String phone_number;
    private String name;
    private String avatar_path;

    public tcp_user(String ip, String name, String phone_number, String avatar_path){
        this.ip = ip;
        this.name = name;
        this.phone_number = phone_number;
        this.avatar_path = avatar_path;
    }

    public void setIP(String ip){
        this.ip = ip;
    }
    public String getIP(){
        return ip;
    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public void setPhoneNumber(String phone_number){
        this.phone_number = phone_number;
    }
    public String getPhoneNumber(){
        return phone_number;
    }

    public void setAvatarPath(String avatar_path){
        this.avatar_path = avatar_path;
    }
    public String getAvatarPath(){
        return avatar_path;
    }

    // Constructor to convert JSON object into a Java class instance
    public tcp_user(JSONObject object){
        try {
            this.ip = object.getString("ip");
            this.phone_number = object.getString("phone_number");
            this.name = object.getString("name");
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    // Factory method to convert an array of JSON objects into a list of objects
    // User.fromJson(jsonArray);
    public static ArrayList<tcp_user> fromJson(JSONArray jsonObjects) {
        ArrayList<tcp_user> users = new ArrayList<tcp_user>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                users.add(new tcp_user(jsonObjects.getJSONObject(i)));
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        }
        return users;
    }
}
