package com.wideo.metrics.model.comparator;

import java.util.Comparator;
import java.util.Map;

public class StatsComp implements Comparator<Object> {

    private String key;
    private String type;
    private Map<String, Map<String, Object>> map;

    public StatsComp(String key, String type, Map<String, Map<String, Object>> map) {
        this.key = key;
        this.type = type;
        this.map = map;
    }

    @Override
    public int compare(Object o1, Object o2) {
        if (o1.equals(o2)) {
            return 0;
        }
        Map<String, Object> mapO1 = map.get(o1);
        Map<String, Object> mapO2 = map.get(o2);
        if ("desc".equals(type)) {
            Double o1Value = (Double) mapO1.get(key);
            Double o2Value = (Double) mapO2.get(key);
            if (o1Value == null) {
                return 1;
            }
            else
                if (o2Value == null) {
                    return -1;
                }
                else
                    if (o1Value > o2Value) {
                        return -1;
                    }
                    else {
                        return 1;
                    }
        } else {
            Double o1Value = (Double) mapO1.get(key);
            Double o2Value = (Double) mapO2.get(key);
            if (o1Value == null) {
                return -1;
            }
            else
                if (o2Value == null) {
                    return 1;
                }
                else
                    if (o1Value > o2Value) {
                        return 1;
                    }
                    else {
                        return -1;
                    }
        }
        

    }
}