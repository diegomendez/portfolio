package com.wideo.metrics.bo.processor.task.refactor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.wideo.metrics.bo.processor.StatsFilterProcessorBO;
import com.wideo.metrics.bo.processor.StatsFilterProcessorBOImpl;
import com.wideo.metrics.constants.WideoMetricsCoreConstants;
import com.wideo.metrics.persistence.dal.StatsCollectorDalImpl;
import com.wideo.metrics.persistence.dal.StatsProcessorDalImpl;

public class Task_2 {

    private static final Logger LOGGER = Logger.getLogger(Task_2.class);

    DBCollection dbCollection;
    StatsFilterProcessorBO statsFilterProcessorBO;

    public Task_2(DBCollection dbCollection,
            StatsFilterProcessorBO statsFilterProcessorBO) {
        this.dbCollection = dbCollection;
        this.statsFilterProcessorBO = statsFilterProcessorBO;
    }

    public Task_2(DBCollection dbCollection) {
        this.dbCollection = dbCollection;
    }

    public JSONObject parseErrorJsonResponse(String processingKeyName,
            Object date, String exceptionDetail) {
        try {
            JSONObject errorJsonData = new JSONObject();
            errorJsonData.accumulate("processingKeyName", processingKeyName);
            errorJsonData.accumulate("dateRange", date);
            errorJsonData.accumulate("exception", exceptionDetail);
            return errorJsonData;
        }
        catch (Exception e) {
            LOGGER.error("Error armando la response de error", e);
            return null;
        }
    }

    public JSONObject parseMongoErrorJsonResponse(String processingKeyName,
            String map, Object date, String exceptionDetail) {
        try {
            JSONObject errorJsonData = new JSONObject();
            errorJsonData.accumulate("processingKeyName", processingKeyName);
            errorJsonData.accumulate("map_function", map);
            errorJsonData.accumulate("dateRange", date);
            errorJsonData.accumulate("exception", exceptionDetail);
            return errorJsonData;
        }
        catch (Exception e) {
            LOGGER.error("Error armando la response de error", e);
            return null;
        }
    }

    public void filterStats(Iterable<DBObject> results, Date date) {
        Iterator<DBObject> it = results.iterator();
        while (it.hasNext()) {
            DBObject result = it.next();
            try {
                DBObject valueObject = (DBObject) result.get("value");
                String wideoID = (String) valueObject.get("wideo_id");
                String action = (String) valueObject.get("action");
                Double value = (Double) valueObject.get("count");

                if (wideoID != null) {
                    statsFilterProcessorBO.updateWideoFilteredStats(wideoID,
                            action, value, date, null, null);
                }
                else {
                    System.out.println("wideoID es null");
                }
            }
            catch (Exception e) {
                LOGGER.error("Ocurrio un error intentando guardar el evento "
                        + result.toString(), e);
            }
        }
    }

    public void filterStatsByProperty(Iterable<DBObject> results, Date date,
            String propertyKey) {
        Iterator<DBObject> it = results.iterator();
        while (it.hasNext()) {
            DBObject result = it.next();
            try {
                DBObject valueObject = (DBObject) result.get("value");
                String wideoID = (String) valueObject.get("wideo_id");
                String action = (String) valueObject.get("action");
                Double value = (Double) valueObject.get("count");
                String propertyValue = (String) valueObject.get(propertyKey);
                if (propertyValue == null) {
                    propertyValue = "Other";
                }
                propertyValue = propertyValue.replace(".", "_");

                if (wideoID != null) {
                    LOGGER.info("mandando a hacer update: [wideoID=" + wideoID
                            + ", propertyKey=" + propertyKey
                            + ", propertyValue=" + propertyValue + "]");
                    statsFilterProcessorBO.updateWideoFilteredStats(wideoID,
                            action, value, date, propertyKey, propertyValue);
                }
                else {
                    LOGGER.info("wideoID es null");
                }
            }
            catch (Exception e) {
                LOGGER.error("Ocurrio un error intentando guardar el evento "
                        + result.toString(), e);
            }
        }
    }

    public void filterInteractionStats(Iterable<DBObject> results, Date date) {
        Iterator<DBObject> it = results.iterator();
        while (it.hasNext()) {
            DBObject result = it.next();
            try {
                DBObject valueObject = (DBObject) result.get("value");
                String wideoID = (String) valueObject.get("wideo_id");
                String interactionID = (String) valueObject
                        .get("interaction_id");
                String action = (String) valueObject.get("action");
                Double value = (Double) valueObject.get("count");

                if (wideoID != null) {
                    if (interactionID != null) {
                        statsFilterProcessorBO
                                .updateWideoInteractionsFilteredStats(wideoID,
                                        action, interactionID, value, date);
                    }
                    else {
                        System.out.println("interactionID es null");
                    }
                }
                else {
                    System.out.println("wideoID es null");
                }
            }
            catch (Exception e) {
                LOGGER.error("Ocurrio un error intentando guardar el evento "
                        + result.toString(), e);
            }
        }
    }

    public void filterUserStats(Iterable<DBObject> results, Date date) {
        Iterator<DBObject> it = results.iterator();
        while (it.hasNext()) {
            DBObject result = it.next();
            try {

                DBObject valueObject = (DBObject) result.get("value");
                Double userIDTemp = null;
                try {
                    userIDTemp = (Double) valueObject.get("user_id");
                }
                catch (Exception e) {
                    userIDTemp = Double.valueOf((String) valueObject
                            .get("user_id"));
                }
                if (userIDTemp != null) {
                    Long userID = userIDTemp.longValue();
                    String action = (String) valueObject.get("action");
                    Double value = (Double) valueObject.get("count");

                    statsFilterProcessorBO.updateUserFilteredStats(userID,
                            action, value, date, null, null);
                }
                else {
                    System.out.println("userID es null");
                }
            }
            catch (Exception e) {
                LOGGER.error("Ocurrio un error intentando guardar el evento "
                        + result.toString(), e);
            }
        }
    }

    public void filterUserStatsByProperty(Iterable<DBObject> results,
            Date date, String propertyKey) {
        Iterator<DBObject> it = results.iterator();
        while (it.hasNext()) {
            DBObject result = it.next();
            try {
                DBObject valueObject = (DBObject) result.get("value");
                Double userIDTemp = null;
                try {
                    userIDTemp = (Double) valueObject.get("user_id");
                }
                catch (Exception e) {
                    userIDTemp = Double.valueOf((String) valueObject
                            .get("user_id"));
                }
                if (userIDTemp != null) {
                    Long userID = userIDTemp.longValue();
                    String action = (String) valueObject.get("action");
                    Double value = (Double) valueObject.get("count");
                    String propertyValue = (String) valueObject
                            .get(propertyKey);
                    if (propertyValue == null) {
                        propertyValue = "Other";
                    }
                    propertyValue = propertyValue.replace(".", "_");

                    statsFilterProcessorBO.updateUserFilteredStats(userID,
                            action, value, date, propertyKey, propertyValue);
                }
                else {
                    System.out.println("userID es null");
                }
            }
            catch (Exception e) {
                LOGGER.error("Ocurrio un error intentando guardar el evento "
                        + result.toString(), e);
            }
        }
    }

    public void filterPlayingSecStats(Iterable<DBObject> results, Date date) {
        LOGGER.info("comenzando a guardar valores para fecha " + date.toString());
        Iterator<DBObject> it = results.iterator();
        while (it.hasNext()) {
            DBObject result = it.next();
            try {
                DBObject valueObject = (DBObject) result.get("value");
                String wideoID = (String) valueObject.get("wideo_id");
                String action = (String) valueObject.get("action");
                Double value = (Double) valueObject.get("count");
                Double playingSec = (Double) valueObject.get("playing_sec");
                if (wideoID != null) {

                    if (playingSec != null) {
                        statsFilterProcessorBO.updateWideoFilteredStatsPlayingSec(
                                wideoID, action, playingSec, value, date);
                    }
                    else {
                        System.out.println("playingsec es null");
                    }
                }
                else {
                    System.out.println("wideoID es null");
                }
            }
            catch (Exception e) {
                LOGGER.error("Resultado no se puede parsear", e);
            }
        }
    }

}
