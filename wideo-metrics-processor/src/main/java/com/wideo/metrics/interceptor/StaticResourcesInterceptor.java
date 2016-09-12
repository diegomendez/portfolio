package com.wideo.metrics.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class StaticResourcesInterceptor extends HandlerInterceptorAdapter {

    // properties by spring
    private String wideoResourceStaticUrl;
    private String externalLibStaticUrl;
    private String apiUrl;

    public static Logger LOGGER = Logger
            .getLogger(StaticResourcesInterceptor.class);

    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

        if (modelAndView != null) {
                request.setAttribute("wideoStaticUrl", wideoResourceStaticUrl);
                request.setAttribute("externalLibStaticUrl",
                        externalLibStaticUrl);
                request.setAttribute("apiUrl", apiUrl);
            }
        
        super.postHandle(request, response, handler, modelAndView);
    }

    @Required
    public void setExternalLibStaticUrl(String externalLibStaticUrl) {
        this.externalLibStaticUrl = externalLibStaticUrl;
    }

    @Required
    public void setWideoResourceStaticUrl(String wideoResourceStaticUrl) {
        this.wideoResourceStaticUrl = wideoResourceStaticUrl;
    }

    @Required
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

}
