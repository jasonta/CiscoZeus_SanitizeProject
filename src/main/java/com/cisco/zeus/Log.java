package com.cisco.zeus;

import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.io.IOException;


public class Log {
    JSONObject data = new JSONObject();
    boolean mutable = true;

    public Log setKeyValues(String key, Object value) throws IOException{
        if(!mutable) {
            throw new IOException("Log object is immutable");
        }
        data.put(key,value);
        return this;
    }

    /*
    public Log setKeyValues(String key, double value){
        data.put(key,value);
        return this;
    }*/

    public Log build() {
        mutable = false;
        return this;
    }

     @Override
     public String toString() {
        return "["+data.toString()+"]";
    }

}



