package com.cisco.zeus;

import org.junit.*;
import static org.junit.Assert.*;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.Iterator;
import java.util.UUID;

public class ZeusAPILogTest 
{

    /////////////////////////////////////// 
    //Edit the below line to add your token
    //////////////////////////////////////
    //String token = "Your_token_here";
    String token = System.getenv("ZEUS_TOKEN");
 
    String testLogName = UUID.randomUUID().toString();
    ZeusAPIClient zeusClient = new ZeusAPIClient(token);

    @BeforeClass
    public static void oneTimeSetUp() {
        // one-time initialization code   
        System.out.println("OnetimeSetup: Setting up ZeusAPI Client for testing logs");
    }
 
    @AfterClass
    public static void oneTimeTearDown() {
        // one-time cleanup code
        System.out.println("oneTimeTearDown: Zeus API Client log testing completed");
    }
   
    @After
    public void tearDown() {
        System.out.println("Testcase teardown"); 
    }
 
    public void sleep(int sec) {
        System.out.println("Sleeping for "+sec+" seconds");
        try {
            Thread.sleep(sec*1000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testSingleLog() throws IOException
    {
        Log log = new Log()
                        .setKeyValues("key1","value1")
                        .setKeyValues("key2","value2")
                        .build();
        LogList loglist = new LogList(testLogName)
                        .addLog(log)
                        .build();

        String result = zeusClient.sendLogs(loglist);
        System.out.println("Logs Sent: "+result);

        sleep(2);
        Parameters params = new Parameters();
        params.add("log_name",testLogName);
        result = zeusClient.retrieveLogs(params);
        System.out.println("Logs Got: "+result);        

    }

    @Test
    public void testMultipleLogs() throws IOException
    {
        Log log = new Log()
                        .setKeyValues("key1","value1")
                        .setKeyValues("key2","value2")
                        .build();

        Log log1 = new Log()
                        .setKeyValues("key3","value3")
                        .setKeyValues("key4","value4")
                        .build();
        
        LogList loglist = new LogList(testLogName+"3")
                        .addLog(log)
                        .addLog(log1)
                        .build();

        String result = zeusClient.sendLogs(loglist);
        System.out.println("Logs Sent: "+result);

        sleep(2);

        Parameters params = new Parameters();
        params.add("log_name",testLogName+"3");
        result = zeusClient.retrieveLogs(params);
        System.out.println("Logs Got: "+result);        
    }

    @Test
    public void testLogMessageTypes() throws IOException
    {
        Log log = new Log()
                        .setKeyValues("key1","v:v;v-v$v*v%v_v!v_v~v_v`v~v#v")
                        .setKeyValues("key2",1000.000)
                        .setKeyValues("key3",1000)
                        .build();

        Log log1 = new Log()
                        .setKeyValues("key3",132132849812348913L)
                        .setKeyValues("key4","testlog")
                        .setKeyValues("key5","test-log")
                        .setKeyValues("k:k;k-k$k*k%k_k!k_k~k_k`k~k#k","v:v;v-v$v*v%v")
                        .build();

        LogList loglist = new LogList(testLogName+"0")
                        .addLog(log)
                        .addLog(log1)
                        .build();

        String result = zeusClient.sendLogs(loglist);
        System.out.println("Logs Sent: "+result);

        sleep(2);

        Parameters params = new Parameters();
        params.add("log_name",testLogName+"0");
        result = zeusClient.retrieveLogs(params);
        System.out.println("Logs Got: "+result);
    }

    @Test
    public void testLogsWithTimestamps() throws IOException
    {
        Log log = new Log()
                        .setKeyValues("key2",1000.000)
                        .setKeyValues("key3",1000)
                        .setKeyValues("timestamp",1433198361)
                        .build();

        Log log1 = new Log()
                        .setKeyValues("key3",132132849812348913L)
                        .setKeyValues("key4","testlog")
                        .setKeyValues("key5","test-log")
                        .build();

        LogList loglist = new LogList(testLogName+"0")
                        .addLog(log)
                        .addLog(log1)
                        .build();

        String result = zeusClient.sendLogs(loglist);
        System.out.println("Logs Sent: "+result);

        sleep(2);

        Parameters params = new Parameters();
        params.add("log_name",testLogName+"0");
        result = zeusClient.retrieveLogs(params);
        System.out.println("Logs Got: "+result);
    }

  
}
