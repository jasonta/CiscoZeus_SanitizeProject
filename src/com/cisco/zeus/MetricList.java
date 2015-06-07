package com.cisco.zeus;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MetricList {
    List<String> columnNames = new ArrayList<String>();
    List<List<Object>> columnValues = new ArrayList<>();
    String metricName = "";
    JSONArray list = new JSONArray();

    public MetricList(String name) {
        metricName = name;
    }

    public MetricList addColumns(String... cols) {
        columnNames = new ArrayList<>();
        for (String col : cols) {
            columnNames.add(col);
        }
        return this;
    }

    public MetricList addValues(Object... values) {
        List<Object> entry = new ArrayList<>();

        for (Object value : values) {
            entry.add(value);
        }

        columnValues.add(entry);
        return this;
    }

    public MetricList build() throws IOException {
        boolean timePresent = false;
        long timestamp = 0;
        for (int valueCount = 0; valueCount < columnValues.size(); valueCount++) {
            JSONObject data = new JSONObject();
            if (columnNames.size() != columnValues.get(valueCount).size())
                throw new IOException("Column name size and column value size do not match");
            for (int i = 0; i < columnNames.size(); i++) {
                if (columnNames.get(i).equals("timestamp")) {
                    timePresent = true;
                    timestamp = (long) columnValues.get(valueCount).get(i);
                } else {
                    data.put(columnNames.get(i), columnValues.get(valueCount).get(i));
                }
            }
            JSONObject datapoint = new JSONObject();
            datapoint.put("point", data);
            if (timePresent) {
                datapoint.put("timestamp", timestamp);
            }
            data = datapoint;
            list.add(data.clone());
        }
        return this;
    }

    public void clear() {
        list = new JSONArray();
        columnValues = new ArrayList<>();
    }

    @Override
    public String toString() {
        return list.toString();
    }
}



