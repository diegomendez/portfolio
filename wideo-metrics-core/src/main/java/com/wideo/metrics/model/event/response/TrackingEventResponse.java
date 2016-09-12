package com.wideo.metrics.model.event.response;

import java.io.Serializable;

public class TrackingEventResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private String status;
    private String code;
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }

}
