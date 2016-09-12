package com.wideo.metrics.jackson.dtos;

public class WideoMetricsResponseDataWrapperDTO extends WideoMetricsResponseDTO {

    /**
         * 
         */
    private static final long serialVersionUID = 1L;
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
