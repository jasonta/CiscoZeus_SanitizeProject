package sanitize;

import com.cisco.zeus.*;

import java.io.IOException;
import java.util.Random;

public class Main {

    // Key regular expression to match all employees
    private static final String EMPLOYEE_KEY = "sanitize-employee";

    // Columns
    private static final String TIMESTAMP = "timestamp";
    private static final String HUMIDITY = "humidity";
    private static final String SOAP_LEFT = "soap_left";
    private static final String SOAP_DISPENSED = "soap_dispensed";
    private static final String VISIT_DURATION = "visit_duration";
    private static final String VISIT_NUM = "visit_num";
    private static final String WATER_DURATION = "water_duration";

    private static ZeusAPIClient zeusClient;
    private static Random random;

    public static void main(String[] args) throws IOException {
        String result;
        String token = "5cf99026";
        System.out.println("****** Access token is " + token + " ******");
        zeusClient = new com.cisco.zeus.ZeusAPIClient(token);
        random = new Random(System.currentTimeMillis());

        System.out.println("Posting employee metric lists");
        addEmployeeMetrics(EMPLOYEE_KEY + "1");
        addEmployeeMetrics(EMPLOYEE_KEY + "2");
        addEmployeeMetrics(EMPLOYEE_KEY + "3");

        // search for all data matching reg exp: "sanitize_employee"
        System.out.println("Retrieving all metrics that matches pattern: sanitize-employee");
        Parameters metric_params = new Parameters();
        metric_params.add("metric_name", EMPLOYEE_KEY);
        result = zeusClient.retrieveMetricValues(metric_params);
        System.out.println("Metrics Get Result " + result);

        // Delete data to start over next run
        deleteEmployeeMetrics(EMPLOYEE_KEY + "1");
        deleteEmployeeMetrics(EMPLOYEE_KEY + "2");
        deleteEmployeeMetrics(EMPLOYEE_KEY + "3");

        System.out.println("Sending a log with log name: " + EMPLOYEE_KEY);
        Log log = new Log()
                .setKeyValues(EMPLOYEE_KEY + "1", "pass")
                .setKeyValues(EMPLOYEE_KEY + "2", "pass")
                .setKeyValues(EMPLOYEE_KEY + "3", "fail")
                .build();

        LogList logList = new com.cisco.zeus.LogList(EMPLOYEE_KEY)
                .addLog(log)
                .build();

        result = zeusClient.sendLogs(logList);
        System.out.println("Logs Post Result " + result);

        System.out.println("Retrieving all logs with log name: " + EMPLOYEE_KEY);
        Parameters log_params = new Parameters();
        log_params.add("log_name", EMPLOYEE_KEY);
        result = zeusClient.retrieveLogs(log_params);
        System.out.println("Logs Get Result " + result);
    }

    public static void addEmployeeMetrics(final String key) throws IOException {
        System.out.println("Posting metric list: " + key);
        MetricList metric = new com.cisco.zeus.MetricList(key);
        metric.addColumns(TIMESTAMP, HUMIDITY, SOAP_DISPENSED, SOAP_LEFT, VISIT_DURATION, VISIT_NUM, WATER_DURATION);
        final int NUM_ENTRIES = random.nextInt(4) + 7;
        System.out.println("# entries: " + NUM_ENTRIES);
        final long currMillis = System.currentTimeMillis();
        long timestamp;
        for (int ii = 0; ii < NUM_ENTRIES; ++ii) {
            timestamp = (currMillis - (70 * 60 * 1000 * ii) - random.nextInt(20 * 60 * 1000)) / 1000;
            System.out.println("timestamp: " + timestamp);
            metric.addValues(
                    timestamp,
                    random.nextDouble() * 30,
                    random.nextBoolean() ? 0.0 : 1.0,
                    random.nextBoolean() ? 0.0 : 1.0,
                    90 + random.nextDouble() * 30,
                    ii + 1,
                    random.nextDouble() * 35);
        }
        metric.build();
        String result = zeusClient.sendMetrics(metric);
        System.out.println("Metrics Post Result " + result);
    }

    public static void deleteEmployeeMetrics(final String key) throws IOException {
        System.out.println("Deleting metric with metric name: " + key);
        String result = zeusClient.deleteMetrics(key);
        System.out.println("Metrics Delete Result " + result);
    }

    public static void sleep(int sec) {
        System.out.println("Sleeping for " + sec + " seconds");
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
