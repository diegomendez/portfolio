package com.wideo.metrics.jackson.dtos;

import java.io.Serializable;

public class WideoMetricsResponseDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int status;
    private int code;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
