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

public class ZeusAPIMetricTest 
{

    /////////////////////////////////////// 
    //Edit the below line to add your token
    //////////////////////////////////////
    //String token = "Your_token_here";
    String token = System.getenv("ZEUS_TOKEN");
 
    String testSeriesName = UUID.randomUUID().toString();
    ZeusAPIClient zeusClient = new ZeusAPIClient(token);

    @BeforeClass
    public static void oneTimeSetUp() {
        // one-time initialization code   
        System.out.println("OnetimeSetup: Setting up ZeusAPI Client for testing metrics");
    }
 
    @AfterClass
    public static void oneTimeTearDown() {
        // one-time cleanup code
        System.out.println("oneTimeTearDown: Zeus API Client metrics testing completed");
    }
   
    @After
    public void tearDown() {
        System.out.println("Deleting Metric"); 
        String result = zeusClient.deleteMetrics(testSeriesName);
    }
 
    public void sleep(int sec) {
        System.out.println("Sleeping for "+sec+" seconds");
        try {
            Thread.sleep(sec*1000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public long countDataPointsInMetric(String metricName, String result) throws ParseException {
        long length = -1;
        JSONParser parser=new JSONParser();
        Object obj=parser.parse(result);
        JSONArray array=(JSONArray)obj;
        Iterator<JSONObject> iterator = array.iterator();
        while (iterator.hasNext()) {
            JSONObject json = (JSONObject) (iterator.next());
            if((json.get("name")).equals(metricName)) {
                length = ((JSONArray)json.get("points")).size();
                break;
            }
        }
        return length;         
    }

    public long countAggregateDataPointsInMetric(String metricName, String aggregateFn , String result) throws ParseException {
        long length = -1;
        JSONParser parser=new JSONParser();
        Object obj=parser.parse(result);
        JSONArray array=(JSONArray)obj;
        Iterator<JSONObject> iterator = array.iterator();
        while (iterator.hasNext()) {
            JSONObject json = (JSONObject) (iterator.next());
            if((json.get("name")).equals(metricName)) {
                int aggregateIndex = ((JSONArray)json.get("columns")).indexOf(aggregateFn);
                JSONArray arr = (JSONArray)json.get("points");
                Iterator<JSONArray> itr = arr.iterator();
                length = 0;
                while (itr.hasNext()) {
                    length += (long)((JSONArray) (itr.next())).get(aggregateIndex);
                }
                break;
            }
        }
        return length;         
    }

    public int countTotalMetricNames(String result) throws ParseException {
        JSONParser parser=new JSONParser();
        Object obj=parser.parse(result);
        JSONArray array=(JSONArray)obj;
        return array.size();         
    }

    public boolean isMetricNamePresent(String metricName, String result) throws ParseException {
        JSONParser parser=new JSONParser();
        boolean isPresent = false;
        Object obj=parser.parse(result);
        JSONArray array=(JSONArray)obj;
        Iterator<JSONObject> iterator = array.iterator();
        while (iterator.hasNext()) {
            JSONObject json = (JSONObject) (iterator.next());
            if((json.get("name")).equals(metricName)) {
                isPresent = true;
                break;
            }
        }
        return isPresent;         
    }

    @Test
    public void testSingleMetric() throws IOException, ParseException
    {
        //send a single Metric to Zeus with only value field 

        //Each Metric is (timestamp, <list of columns>) pair in the system
        //If timestamp is omitted, system generated timestamp will be used
        System.out.println("Sending Metric");
        MetricList metric = new MetricList(testSeriesName)
                            .addColumns("col1","col2")
                            .addValues(3,3)
                            .build();
        
        String result = zeusClient.sendMetrics(metric);
        System.out.println("Metric Sent: "+result);

        sleep(2);

        System.out.println("Retrieving Metric");
        Parameters params = new Parameters();
        params.add("metric_name",testSeriesName);
        result = zeusClient.retrieveMetricValues(params);

        System.out.println("Metric Got: "+result);
        assertTrue(isMetricNamePresent(testSeriesName,result));        
        assertTrue(countDataPointsInMetric(testSeriesName, result) == 1);
        assertTrue(countTotalMetricNames(result) == 1);
    
    }
 
    @Test 
    public void testGetMetricNames() throws IOException, ParseException
    {
        //send a single Metric to Zeus with only value field 

        //Each Metric is (timestamp, <list of columns>) pair in the system
        //If timestamp is omitted, system generated timestamp will be used
        System.out.println("Sending Metric");
        MetricList metric = new MetricList(testSeriesName)
                            .addColumns("col1","col2")
                            .addValues(3,3)
                            .build();
        
        String result = zeusClient.sendMetrics(metric);
        System.out.println("Metric Sent: "+result);

         MetricList metric1 = new MetricList(testSeriesName+"1")
                            .addColumns("col3","col4")
                            .addValues(4,4)
                            .build();
        
        result = zeusClient.sendMetrics(metric1);
        System.out.println("Metric Sent: "+result);


        sleep(2);

        System.out.println("Retrieving Metric");
        Parameters params = new Parameters();
        params.add("metric_name",testSeriesName);
        result = zeusClient.retrieveMetricNames(params);

        System.out.println("Metric Got: "+result);
        assertTrue(countTotalMetricNames(result) == 2);

        System.out.println("Delete Metric"); 
        result = zeusClient.deleteMetrics(testSeriesName+"1");
        System.out.println("Metric Delete: "+result);
            
    }


    @Test 
    public void testMultipleMetrics() throws IOException, ParseException
    {
        //send a single Metric to Zeus with only value field 

        //Each Metric is (timestamp, <list of columns>) pair in the system
        //If timestamp is omitted, system generated timestamp will be used
        System.out.println("Sending Metric");
        MetricList metric = new MetricList(testSeriesName)
                            .addColumns("col1","col2")
                            .addValues(3,3)
                            .build();
        
        String result = zeusClient.sendMetrics(metric);
        System.out.println("Metric Sent: "+result);

        MetricList metric1 = new MetricList(testSeriesName+"1")
                            .addColumns("col3","col4")
                            .addValues(4,4)
                            .build();
        
        result = zeusClient.sendMetrics(metric1);
        System.out.println("Metric Sent: "+result);


        sleep(2);

        System.out.println("Retrieving Metric");
        Parameters params = new Parameters();
        params.add("metric_name",testSeriesName);
        result = zeusClient.retrieveMetricValues(params);

        System.out.println("Metric Got: "+result);
        assertTrue(isMetricNamePresent(testSeriesName,result));        
        assertTrue(isMetricNamePresent(testSeriesName+"1",result));        
        assertTrue(countDataPointsInMetric(testSeriesName, result) == 1);
        assertTrue(countDataPointsInMetric(testSeriesName+"1", result) == 1);
        assertTrue(countTotalMetricNames(result) == 2);

        System.out.println("Delete Metric"); 
        result = zeusClient.deleteMetrics(testSeriesName+"1");
        System.out.println("Metric Delete: "+result);
            
    }


  
    @Test
    public void testMultipleDataPointsWithoutTimestamps() throws IOException, ParseException
    {
        //send mulitple Metrics to Zeus

        //Each Metric is (timestamp, <list of columns>) pair in the system
        //If timestamp is omitted, system generated timestamp will be used
        System.out.println("Sending Metrics");
        MetricList metric = new MetricList(testSeriesName)
                            .addColumns("col1","col2")
                            .addValues(3,3)
                            .addValues(4,4)
                            .build();
        
        String result = zeusClient.sendMetrics(metric);
        System.out.println("Metric Sent: "+result);

        sleep(2);

        System.out.println("Retrieving Metrics");
        Parameters params = new Parameters();
        params.add("metric_name",testSeriesName);
        params.add("limit",10);
        result = zeusClient.retrieveMetricValues(params);

        System.out.println("Metric Got: "+result);
        assertTrue(isMetricNamePresent(testSeriesName,result));        
        assertTrue(countDataPointsInMetric(testSeriesName, result) == 2);
        assertTrue(countTotalMetricNames(result) == 1);

    }

    @Test
    public void testMultipleDataPointsWithTimestamps() throws IOException, ParseException
    {
        //send mulitple Metrics to Zeus

        //Each Metric is (timestamp, <list of columns>) pair in the system
        //Timestamp can be supplied with "timestamp" column
        System.out.println("Sending Metrics");
        MetricList metric = new MetricList(testSeriesName)
                            .addColumns("timestamp","col1","col2")
                            .addValues(1423034086.343,3,3)
                            .addValues(1423034089,4,4)
                            .build();
        
        String result = zeusClient.sendMetrics(metric);
        System.out.println("Metric Sent: "+result);

        sleep(2);

        System.out.println("Retrieving Metrics");
        Parameters params = new Parameters();
        params.add("metric_name",testSeriesName);
        params.add("limit",10);
        result = zeusClient.retrieveMetricValues(params);

        System.out.println("Metric Got: "+result);
        assertTrue(isMetricNamePresent(testSeriesName,result));        
        assertTrue(countDataPointsInMetric(testSeriesName, result) == 2);
        assertTrue(countTotalMetricNames(result) == 1);

    }


    @Test
    public void testMetricAggregation() throws IOException, ParseException
    {
        //send mulitple Metrics to Zeus

        //Each Metric is (timestamp, <list of columns>) pair in the system
        //Timestamp can be supplied with "timestamp" column
        System.out.println("Sending Metrics");
        MetricList metric = new MetricList(testSeriesName)
                            .addColumns("timestamp","col1","col2")
                            .addValues(1433198001,3,3)
                            .addValues(1433198121,4,4)
                            .addValues(1433198241,5,5)
                            .addValues(1433198361,6,6)
                            .addValues(1433198481,7,7)
                            .build();
        
        String result = zeusClient.sendMetrics(metric);
        System.out.println("Metric Sent: "+result);

        sleep(2);

        System.out.println("Retrieving Metrics");
        Parameters params = new Parameters();
        params.add("metric_name",testSeriesName);
        params.add("aggregator_function","count");
        params.add("aggregator_column","col1");
        params.add("group_interval","5m");
        result = zeusClient.retrieveMetricValues(params);

        System.out.println("Metric Got: "+result);
        assertTrue(isMetricNamePresent(testSeriesName,result));        
        assertTrue(countAggregateDataPointsInMetric(testSeriesName, "count", result) == 5);
        assertTrue(countTotalMetricNames(result) == 1);

    }

    @Test
    public void testMetricOffsetAndLimit() throws IOException, ParseException
    {
        //send multiple Metrics to Zeus

        //Each Metric is (timestamp, <list of columns>) pair in the system
        //Timestamp can be supplied with "timestamp" column
        System.out.println("Sending Metrics");
        MetricList metric = new MetricList(testSeriesName)
                            .addColumns("timestamp","col1","col2")
                            .addValues(1433198001,3,3)
                            .addValues(1433198121,4,4)
                            .addValues(1433198241,5,5)
                            .addValues(1433198361,6,6)
                            .addValues(1433198481,7,7)
                            .build();
        
        String result = zeusClient.sendMetrics(metric);
        System.out.println("Metric Sent: "+result);

        sleep(2);

        System.out.println("Retrieving Metrics");
        Parameters params = new Parameters();
        params.add("metric_name",testSeriesName);
        params.add("limit",3);
        result = zeusClient.retrieveMetricValues(params);

        System.out.println("Metric Got: "+result);
        assertTrue(isMetricNamePresent(testSeriesName,result));        
        assertTrue(countDataPointsInMetric(testSeriesName, result) == 3);
        assertTrue(countTotalMetricNames(result) == 1);

 
        System.out.println("Retrieving Metrics");
        params = new Parameters();
        params.add("metric_name",testSeriesName);
        params.add("offset",1);
        result = zeusClient.retrieveMetricValues(params);

        System.out.println("Metric Got: "+result);
        assertTrue(isMetricNamePresent(testSeriesName,result));        
        assertTrue(countDataPointsInMetric(testSeriesName, result) == 4);
        assertTrue(countTotalMetricNames(result) == 1);      


        System.out.println("Retrieving Metrics");
        params = new Parameters();
        params.add("metric_name",testSeriesName);
        params.add("offset",1);
        params.add("limit",3);
        result = zeusClient.retrieveMetricValues(params);

        System.out.println("Metric Got: "+result);
        assertTrue(isMetricNamePresent(testSeriesName,result));        
        assertTrue(countDataPointsInMetric(testSeriesName, result) == 3);
        assertTrue(countTotalMetricNames(result) == 1);

        
    }

    @Test
    public void testMetricFilterTimestamp() throws IOException, ParseException
    {
        //send mulitple Metrics to Zeus

        //Each Metric is (timestamp, <list of columns>) pair in the system
        //Timestamp can be supplied with "timestamp" column
        System.out.println("Sending Metrics");
        MetricList metric = new MetricList(testSeriesName)
                            .addColumns("timestamp","col1","col2")
                            .addValues(1433198001,3,3)
                            .addValues(1433198121.123,4,4)
                            .addValues(1433198241,5,5)
                            .addValues(1433198361.5678909,6,6)
                            .addValues(1433198481,7,7)
                            .build();
        
        String result = zeusClient.sendMetrics(metric);
        System.out.println("Metric Sent: "+result);

        sleep(2);

        System.out.println("Retrieving Metrics");
        Parameters params = new Parameters();
        params.add("metric_name",testSeriesName);
        params.add("from",1433198121);
        params.add("to",1433198361);
        result = zeusClient.retrieveMetricValues(params);

        System.out.println("Metric Got: "+result);
        assertTrue(isMetricNamePresent(testSeriesName,result));        
        assertTrue(countDataPointsInMetric(testSeriesName, result) == 2);
        assertTrue(countTotalMetricNames(result) == 1);

        params = new Parameters();
        params.add("metric_name",testSeriesName);
        params.add("from",1433198121);
        result = zeusClient.retrieveMetricValues(params);

        System.out.println("Metric Got: "+result);
        assertTrue(isMetricNamePresent(testSeriesName,result));
        assertTrue(countDataPointsInMetric(testSeriesName, result) == 4);
        assertTrue(countTotalMetricNames(result) == 1);

        params = new Parameters();
        params.add("metric_name",testSeriesName);
        params.add("from",1433198121.130);
        result = zeusClient.retrieveMetricValues(params);

        sleep(2);
        System.out.println("Metric Got: "+result);
        assertTrue(isMetricNamePresent(testSeriesName,result));
        assertTrue(countDataPointsInMetric(testSeriesName, result) == 3);
        assertTrue(countTotalMetricNames(result) == 1);

        params = new Parameters();
        params.add("metric_name",testSeriesName);
        params.add("from",1433198121.2090);
        params.add("to",1433198360.989);
        result = zeusClient.retrieveMetricValues(params);

        sleep(2);
        System.out.println("Metric Got: "+result);
        assertTrue(isMetricNamePresent(testSeriesName,result));
        assertTrue(countDataPointsInMetric(testSeriesName, result) == 1);
        assertTrue(countTotalMetricNames(result) == 1);

    }

    @Test
    public void testMetricFilterCondition() throws IOException, ParseException
    {
        //send mulitple Metrics to Zeus

        //Each Metric is (timestamp, <list of columns>) pair in the system
        //Timestamp can be supplied with "timestamp" column
        System.out.println("Sending Metrics");
        MetricList metric = new MetricList(testSeriesName)
                            .addColumns("timestamp","col1","col2")
                            .addValues(1433198001,3,3)
                            .addValues(1433198121,4,4)
                            .addValues(1433198241,5,5)
                            .addValues(1433198361,6,6)
                            .addValues(1433198481,7,7)
                            .build();
        
        String result = zeusClient.sendMetrics(metric);
        System.out.println("Metric Sent: "+result);

        sleep(2);

        System.out.println("Retrieving Metrics");
        Parameters params = new Parameters();
        params.add("metric_name",testSeriesName);
        params.add("filter_condition","col1 > 4 and col2 < 7");
        result = zeusClient.retrieveMetricValues(params);

        System.out.println("Metric Got: "+result);
        assertTrue(isMetricNamePresent(testSeriesName,result));        
        assertTrue(countDataPointsInMetric(testSeriesName, result) == 2);
        assertTrue(countTotalMetricNames(result) == 1);

    }


}
