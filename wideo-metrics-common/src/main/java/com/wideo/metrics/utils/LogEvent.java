package com.wideo.metrics.utils;

public class LogEvent {

    public static String args(Object... variables) {
        StringBuffer sb = new StringBuffer();
        for (Object var : variables) {
            sb.append(var != null ? var.toString() : "NULL");
        }
        return sb.toString();
    }

}
