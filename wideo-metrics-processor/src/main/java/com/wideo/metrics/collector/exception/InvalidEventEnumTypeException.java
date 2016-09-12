package com.wideo.metrics.collector.exception;

public class InvalidEventEnumTypeException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public InvalidEventEnumTypeException() {
        super("Invalid event enum key");
    }
    
}
