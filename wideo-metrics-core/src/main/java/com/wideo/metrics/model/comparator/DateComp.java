package com.wideo.metrics.model.comparator;

import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import com.google.api.client.util.DateTime;

public class DateComp implements Comparator<Date> {

    private String key;
    private String type;

    public DateComp(String key, String type) {
        this.key = key;
        this.type = type;
    }

    @Override
    public int compare(Date d1, Date d2) {
        if ("asc".equals(type)) {
            if (d1 == null) {
                return 1;
            }
            else
                if (d2 == null) {
                    return -1;
                }
                else
                    if (d1.after(d2)) {
                        return -1;
                    }
                    else {
                        return 1;
                    }
        }
        else {
            if (d1 == null) {
                return -1;
            }
            else
                if (d2 == null) {
                    return 1;
                }
                else
                    if (d1.after(d2)) {
                        return 1;
                    }
                    else {
                        return -1;
                    }
        }

    }
}