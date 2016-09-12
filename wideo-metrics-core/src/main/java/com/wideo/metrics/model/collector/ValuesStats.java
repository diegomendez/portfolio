package com.wideo.metrics.model.collector;

public class ValuesStats extends Stats {

    private static final long serialVersionUID = 1L;

    private Long name;
    private Object value;
    
    public ValuesStats(Long name, Object value) {
        super();
        this.name = name;
        this.value = value;
    }

    public Long getName() {
        return name;
    }

    public void setName(Long name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" + name + ":" + value + "}";
    }  
}
