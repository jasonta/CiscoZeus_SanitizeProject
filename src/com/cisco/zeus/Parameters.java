package com.cisco.zeus;

import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class Parameters {

    public HashMap<String,Object> data = new HashMap<>();
    
    public void add(String key, Object value) {
        data.put(key,value);
    }

    public void clear() {
        data.clear();
    }

}
