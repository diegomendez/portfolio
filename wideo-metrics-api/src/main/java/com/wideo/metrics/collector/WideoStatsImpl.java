package com.wideo.metrics.collector;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.util.JSON;
import com.wideo.metrics.stats.WideoStatsResults;
import com.wideo.metrics.utils.HttpUtils;

public class WideoStatsImpl implements WideoStats {

    private static final Logger LOGGER = Logger.getLogger(WideoStatsImpl.class);

    private String wideoMetricsStatsURL;

    public List<String> getActiveWideos(String startDate, String endDate) {
        try {
            String response = HttpUtils.urlGet(wideoMetricsStatsURL
                    + "/stats/collect/wideos/active?start=" + startDate
                    + "&end=" + endDate);

            JSONObject jsonResponse = new JSONObject(response);
            JSONObject dataResponse = jsonResponse.getJSONObject("data");
            JSONArray resultsArray = dataResponse.getJSONArray("results");

            Gson gson = new Gson();
            java.lang.reflect.Type t = new TypeToken<List<String>>() {
            }.getType();
            List<String> results = gson.fromJson(resultsArray.toString(), t);
            return results;
        }
        catch (Exception e) {
            return null;
        }
    }

    public JSONObject getAllStatsForWideo(String wideoID, String startDate,
            String endDate, Boolean byDate, Integer wideoLength) {

        String url = wideoMetricsStatsURL + "/stats/collect/wideos/" + wideoID
                + "/all?" + "&start=" + startDate + "&end=" + endDate
                + "&byDate=" + byDate.toString();

        if (wideoLength != null) {
            url = url.concat("&wideoLength=" + wideoLength);
        }

        String response = HttpUtils.urlGet(url);

        try {
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse;
        }
        catch (Exception e) {
            LOGGER.error(
                    "Ocurrio un error parseando la response de WideoStats", e);
            return null;
        }
    }

    public JSONObject getNewStatsForWideo(String wideoID, String startDate,
            String endDate) {

        String url = wideoMetricsStatsURL + "/stats/2/collect/wideos/"
                + wideoID + "?" + "start=" + startDate + "&end=" + endDate;

        String response = HttpUtils.urlGet(url);

        try {
            JSONObject jsonResponse = new JSONObject(response);
            System.out.println(jsonResponse);
            return jsonResponse;
        }
        catch (Exception e) {
            LOGGER.error(
                    "Ocurrio un error parseando la response de WideoStats", e);
            return null;
        }
    }

    public JSONObject getNewStatsForUser(Long userID, String startDate,
            String endDate, List<String> userWideos) {

        String url = wideoMetricsStatsURL + "/stats/2/collect/users/" + userID
                + "?" + "start=" + startDate + "&end=" + endDate;

        if (userWideos != null) {
            JSONArray j = new JSONArray(userWideos);
            url = url.concat("&userWideos=" + j.toString());
        }

        String response = HttpUtils.urlGet(url);

        try {
            JSONObject jsonResponse = new JSONObject(response);
            System.out.println(jsonResponse);
            return jsonResponse;
        }
        catch (Exception e) {
            LOGGER.error(
                    "Ocurrio un error parseando la response de WideoStats", e);
            return null;
        }
    }

    public JSONObject getNewStatsForUserSorted(Long userID, String startDate,
            String endDate, String sortKey, String sortType,
            List<String> wideos, Integer page, Integer amount) {

        String url = wideoMetricsStatsURL + "/stats/collect/users/" + userID
                + "/sort?" + "start=" + startDate + "&end=" + endDate;

        url = url.concat("&sortKey=" + sortKey);
        if (sortType != null) {
            url = url.concat("&sortType=" + sortType);
        }
        if (page != null) {
            url = url.concat("&page=" + page);
        }
        if (wideos != null) {
            String toJson = new Gson().toJson(wideos);
            url = url.concat("&wideos=" + toJson);
        }
        if (amount != null) {
            url = url.concat("&amount=" + amount);
        }

        try {
            String response = HttpUtils.urlGet(url);
            System.out.println(response);
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse;
        }
        catch (Exception e) {
            LOGGER.error(
                    "Ocurrio un error parseando la response de WideoStats", e);
            return null;
        }
    }

    @Override
    public JSONObject getMoreWideosStatsForUser(String startDate,
            String endDate, List<String> userWideos) {
        String url = wideoMetricsStatsURL + "/stats/collect/wideos" + "?"
                + "start=" + startDate + "&end=" + endDate;

        JSONArray j = new JSONArray(userWideos);
        url = url.concat("&userWideos=" + j.toString());

        String response = HttpUtils.urlGet(url);

        try {
            JSONObject jsonResponse = new JSONObject(response);
            System.out.println(jsonResponse);
            return jsonResponse;
        }
        catch (Exception e) {
            LOGGER.error(
                    "Ocurrio un error parseando la response de WideoStats", e);
            return null;
        }
    }

    @Override
    public JSONObject getSummaryStatsForUser(Long userID, String startDate,
            String endDate, Boolean byDate, List<String> userWideos) {

        String url = wideoMetricsStatsURL + "/stats/collect/user/" + userID
                + "/summary?" + "&start=" + startDate + "&end=" + endDate
                + "&byDate=" + byDate.toString();
        if (userWideos != null) {
            JSONArray j = new JSONArray(userWideos);
            url = url.concat("&userWideos=" + j.toString());
        }

        String response = HttpUtils.urlGet(url);

        try {
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse;
        }
        catch (Exception e) {
            LOGGER.error(
                    "Ocurrio un error parseando la response de WideoStats", e);
            return null;
        }
    }

    @Override
    public JSONObject getFormsDataForWideo(String wideoID, String startDate,
            String endDate, String interactionID) {
        String url = wideoMetricsStatsURL + "/stats/collect/wideos/" + wideoID
                + "/forms" + "?start=" + startDate + "&end=" + endDate;

        if (interactionID != null) {
            url = url.concat("&interactionID=" + interactionID);
        }

        String response = HttpUtils.urlGet(url);

        try {
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse;
        }
        catch (Exception e) {
            LOGGER.error("Ocurrio un error parseando la response de FormsData",
                    e);
            return null;
        }

    }

    @Override
    public JSONObject getUserStatsForMailing(String startDate, String endDate) {
        String url = wideoMetricsStatsURL + "/stats/collect/mailing/users"
                + "?start=" + startDate + "&end=" + endDate;
        String response = HttpUtils.urlGet(url);

        try {
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse;
        }
        catch (Exception e) {
            LOGGER.error("Ocurrio un error parseando la response de FormsData",
                    e);
            return null;
        }
    }

    @Required
    public void setWideoMetricsStatsURL(String wideoMetricsStatsURL) {
        this.wideoMetricsStatsURL = wideoMetricsStatsURL;
    }
}
