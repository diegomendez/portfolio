package com.wideo.metrics.processor.controller;

import java.io.Serializable;

public class Test implements Serializable {

    String a;
    
    public Test(String ab) {
        this.a = ab;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public Test() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    
}
