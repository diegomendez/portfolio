package com.wideo.metrics.bo.collector;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;

import com.wideo.metrics.model.collector.ActionStats;
import com.wideo.metrics.model.collector.FormsStats;
import com.wideo.metrics.model.collector.StatsResults;
import com.wideo.metrics.model.collector.UserStatsResults;
import com.wideo.metrics.model.collector.WideoStatsResults;

public interface StatsCollectorBO {

    public ActionStats collectStatsForUser(JSONObject params);
    
    public ActionStats collectStatsForWideo(JSONObject params);

    public List<WideoStatsResults> collectStatsByAction(JSONObject params);
    
    public List<WideoStatsResults> collectSocialStats(JSONObject params);
    
    public ActionStats collectWideoDropOff(JSONObject params);
    
    public StatsResults collectStatsForWideoForInteractions(JSONObject params);

    public Map<String, TreeMap<Date, FormsStats>> collectFormsInfoForWideo(JSONObject params);
    
    public Map<String, Object> collectWideoStats(JSONObject params);

    public Map<String, Object> collectUserStats(JSONObject params);
    
    public List<String> collectActiveWideosStats(JSONObject params);
    
    public Map<Long, Object> getUsersStatsForMailing(JSONObject params);

    public Map<String, Object> getMetadataInfo(JSONObject params);

    public Map<String, Object> getTotalStats(JSONObject params);
}
