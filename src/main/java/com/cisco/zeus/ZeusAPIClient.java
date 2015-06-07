package com.cisco.zeus;

import java.io.*;
import java.net.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder; 
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import org.apache.http.client.utils.URLEncodedUtils; 
import org.apache.http.params.HttpParams;

public class ZeusAPIClient {

    public final static String ZEUS_API = "http://api.ciscozeus.io";
    public final static String USER_AGENT = "Mozilla/5.0";
    
    String token = "";

    public ZeusAPIClient(String userToken){
        token = userToken;
    }

    public String sendMetrics(MetricList list){
        String tag = list.metricName;
        String ret = "" ;
        //add data from metrics
        HashMap<String,Object> params = new HashMap<>();
        params.put("metrics",list);
        try {
            ret = postRequest("/metrics/" + token + "/"+ tag + "/",params);
        } 
        catch(Exception e) {
            System.out.println(" Exception Raised in Sending Metrics"+ e.getMessage());
         }
        list.clear();
        return ret;
    }

    public String sendLogs(LogList list){
        String tag = list.logName;
        String ret = "" ;
        //add data from logs
        HashMap<String,Object> params = new HashMap<>();
        params.put("logs",list);
        try {
            ret = postRequest("/logs/" + token+ "/" + tag + "/", params);
        }
        catch(Exception e) {
            System.out.println(" Exception Raised in Sending Logs"+ e.getMessage());
         }
        list.clear();
        return ret;
    }

    public String retrieveMetricValues(Parameters params){
        String ret = "" ;
        //add data from metrics

        try {
            ret = getRequest("/metrics/"+token+"/_values/",params.data);
        }
        catch(Exception e) {
            System.out.println(" Exception Raised in Retrieving Metric Values"+ e.getMessage());
         }
         params.clear();
         return ret;
    }

    public String retrieveMetricNames(Parameters params){
        String ret = "" ;
        //add data from metrics

        try {
            ret = getRequest("/metrics/"+token+"/_names/",params.data);
        }
        catch(Exception e) {
            System.out.println(" Exception Raised in Retrieving Metric Names"+ e.getMessage());
         }
         params.clear();
         return ret;
    }

    public String deleteMetrics(String tag){
        String ret = "" ;
        try {
            ret = deleteRequest("/metrics/" + token + "/"+ tag + "/");
        }
        catch(Exception e) {
            System.out.println(" Exception Raised in deleting Metrics"+ e.getMessage());
         }
         return ret;
    }
    public String retrieveLogs(Parameters params){
        String ret = "" ;
        //add data from logs

        try {
            ret = getRequest("/logs/"+token+"/",params.data);
        }
        catch(Exception e) {
            System.out.println(" Exception Raised in Retrieving Logs"+ e.getMessage());
         }
         params.clear();
         return ret;
    }


    private String getRequest(String path, HashMap<String,Object> params) throws Exception {
 
        String url = ZEUS_API + path;
 
        URL obj = new URL(url);
        if(!url.endsWith("?"))
            url += "?";

        List<NameValuePair> parameters = new LinkedList<NameValuePair>();
        Set set = params.entrySet();
        Iterator i = set.iterator();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        while(i.hasNext()) {
             Map.Entry me = (Map.Entry)i.next();
             //System.out.println(me.getKey().toString() + " " +me.getValue().toString());
             parameters.add(new BasicNameValuePair(me.getKey().toString(),me.getValue().toString()));
        }

        String queryString = URLEncodedUtils.format(parameters, "utf-8");
        url += queryString;

        //System.out.println(" Url "+url);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
 
        // add request header
        request.addHeader("User-Agent", USER_AGENT);
        //request.setHeader("Authorization", "Bearer " + token);
        HttpResponse response = client.execute(request);
 
        //System.out.println("Response Code : " 
        //        + response.getStatusLine().getStatusCode());
 
        BufferedReader rd = new BufferedReader(
            new InputStreamReader(response.getEntity().getContent()));
 
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        //System.out.println(result);
        return result.toString(); 
    }
 
    // HTTP POST request
    private String postRequest(String url, HashMap<String,Object> params) throws Exception {

    //System.out.println(" url "+url+" params "+params);
    HttpClient client = HttpClientBuilder.create().build();
    HttpPost post = new HttpPost(ZEUS_API + url);
 
    // add header
    post.setHeader("User-Agent", USER_AGENT);
    //post.setHeader("Authorization", "Bearer " + token);

    Set set = params.entrySet();
    Iterator i = set.iterator();
    List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
    while(i.hasNext()) {
         Map.Entry me = (Map.Entry)i.next();
         urlParameters.add(new BasicNameValuePair(me.getKey().toString(), me.getValue().toString()));
    }
    post.setEntity(new UrlEncodedFormEntity(urlParameters));
 
    HttpResponse response = client.execute(post);
    //System.out.println("Response Code : " 
    //            + response.getStatusLine().getStatusCode());
 
    BufferedReader rd = new BufferedReader(
            new InputStreamReader(response.getEntity().getContent()));
 
    StringBuffer result = new StringBuffer();
    String line = "";
    while ((line = rd.readLine()) != null) {
        result.append(line);
    }
    //System.out.println(result);
    return result.toString();
    }


    // HTTP Delete request
    private String deleteRequest(String path) throws Exception {

        String url = ZEUS_API + path;

        URL obj = new URL(url);

        //System.out.println(" Url "+url);
        HttpClient client = HttpClientBuilder.create().build();
        HttpDelete request = new HttpDelete(url);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);
        //request.setHeader("Authorization", "Bearer " + token);
        HttpResponse response = client.execute(request);

        //System.out.println("Response Code : "
        //        + response.getStatusLine().getStatusCode());

         BufferedReader rd = new BufferedReader(
            new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        //System.out.println(result);
        return result.toString();
    }
}
