package com.wideo.metrics.jackson;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.wideo.metrics.jackson.dtos.WideoMetricsResponseDataWrapperDTO;


public class WideoMetricsMappingJacksonJsonView extends MappingJackson2JsonView {

    @Override
    protected Object filterModel(Map<String, Object> model) {
        Map<String, Object> filterModel = (Map<String, Object>) super
                .filterModel(model);
        WideoMetricsResponseDataWrapperDTO wideoResponse = new WideoMetricsResponseDataWrapperDTO();
        wideoResponse.setStatus(HttpStatus.OK.value());
        wideoResponse.setCode(HttpStatus.OK.value());
        if (filterModel.get("data") != null) {
            wideoResponse.setData(filterModel.get("data"));
        }
        else {
            wideoResponse.setData(filterModel);
        }
        return wideoResponse;
    }
}
