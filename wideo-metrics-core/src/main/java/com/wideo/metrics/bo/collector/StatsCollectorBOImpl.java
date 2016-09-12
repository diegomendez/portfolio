package com.wideo.metrics.bo.collector;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import com.wideo.metrics.model.collector.ActionStats;
import com.wideo.metrics.model.collector.FormsStats;
import com.wideo.metrics.model.collector.StatsResults;
import com.wideo.metrics.model.collector.WideoStatsResults;
import com.wideo.metrics.persistence.dal.StatsCollectorDalImpl;

@Service
@ComponentScan("com.wideo.metrics")
public class StatsCollectorBOImpl implements StatsCollectorBO {

    @Autowired
    StatsCollectorDalImpl statsCollectorDalImpl;

    public ActionStats collectStatsForUser(JSONObject params) {
        return statsCollectorDalImpl.collectStatsForUser(params);
    }
    
    public List<WideoStatsResults> collectStatsByAction(JSONObject params) {
        return statsCollectorDalImpl.collectStatsByAction(params);
    }

    public ActionStats collectStatsForWideo(JSONObject params) {
        return statsCollectorDalImpl.collectStatsForWideo(params);
    }
    
    public List<WideoStatsResults> collectSocialStats(JSONObject params) {
        return statsCollectorDalImpl.collectSocialStats(params);
    }
    
    public ActionStats collectWideoDropOff(JSONObject params) {
        return statsCollectorDalImpl.collectWideoDropOff(params);
    }
    
    public StatsResults collectStatsForWideoForInteractions(JSONObject params) {
        return statsCollectorDalImpl.collectStatsForWideoForInteractions(params);
    }
    
    public Map<String, TreeMap<Date, FormsStats>> collectFormsInfoForWideo(JSONObject params) {
        return statsCollectorDalImpl.collectFormsInfoForWideo(params);
    }

    public Map<String, Object> collectWideoStats(JSONObject params) {
        return statsCollectorDalImpl.collectWideoStats(params);
    }
    
    public Map<String, Object> collectUserStats(JSONObject params) {
        return statsCollectorDalImpl.collectUserStats(params);
    }
    
    public List<String> collectActiveWideosStats(JSONObject params) {
        return statsCollectorDalImpl.collectActiveWideosStats(params);
    }
    
    public Map<Long, Object> getUsersStatsForMailing(JSONObject params) {
        return statsCollectorDalImpl.getUsersStatsForMailing(params);
    }

    public Map<String, Object> getMetadataInfo(JSONObject params) {
        return statsCollectorDalImpl.getMetadataInfo(params);
    }
    
    public Map<String, Object> getTotalStats(JSONObject params) {
        return statsCollectorDalImpl.getTotalStats(params);
    }
    
}
