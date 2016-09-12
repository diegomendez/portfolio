package com.wideo.metrics.collector;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.wideo.metrics.stats.StatsResult;
import com.wideo.metrics.stats.WideoStatsResults;

public interface WideoStats {

    public List<String> getActiveWideos(String startDate, String endDate);

    public JSONObject getAllStatsForWideo(String wideoID, String startDate,
            String endDate, Boolean byDate, Integer wideoLength);
    
    public JSONObject getNewStatsForWideo(String wideoID, String startDate,
            String endDate);

    public JSONObject getNewStatsForUser(Long userID, String startDate,
            String endDate, List<String> userWideos);
    
    public JSONObject getNewStatsForUserSorted(Long userID, String startDate,
            String endDate, String sortKey, String sortType, List<String> wideos, Integer page, Integer amount);
    
    public JSONObject getSummaryStatsForUser(Long userID, String startDate,
            String endDate, Boolean byDate, List<String> userWideos);

    public JSONObject getMoreWideosStatsForUser(String startDate,
            String endDate, List<String> userWideos);
    
    public JSONObject getFormsDataForWideo(String wideoID, String startDate, String endDate, String interactionID);
    
    public JSONObject getUserStatsForMailing(String startDate,String endDate);
}
