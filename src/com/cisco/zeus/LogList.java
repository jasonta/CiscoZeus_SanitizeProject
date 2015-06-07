package com.cisco.zeus;

import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class LogList {
    
    JSONArray list = new JSONArray();
    String logName = "";
    public LogList(String name){
        logName = name;
    }

    public LogList(Log log){
        list.add(log.data);
    }

    public LogList addLog(Log log){
        list.add(log.data);
        return this;
    }
    
    public LogList build(){
        return this;
    }

    public void clear(){
        list = new JSONArray();
    }

    @Override
    public String toString() {
        return list.toString();
    }
}



