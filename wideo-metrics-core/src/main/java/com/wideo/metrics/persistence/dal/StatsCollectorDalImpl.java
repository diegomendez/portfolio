package com.wideo.metrics.persistence.dal;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.wideo.metrics.constants.WideoMetricsCoreConstants;
import com.wideo.metrics.model.collector.ActionStats;
import com.wideo.metrics.model.collector.FormsStats;
import com.wideo.metrics.model.collector.InteractionsStats;
import com.wideo.metrics.model.collector.StatsResults;
import com.wideo.metrics.model.collector.ValuesStats;
import com.wideo.metrics.model.collector.WideoStatsResults;
import com.wideo.metrics.model.comparator.DateComp;
import com.wideo.metrics.model.processor.stats.UserFilteredStats;
import com.wideo.metrics.model.processor.stats.WideoFilteredStats;
import com.wideo.metrics.persistence.dal.interfaces.StatsCollectorDalInterface;

@Service
@ComponentScan("com.wideo.metrics")
public class StatsCollectorDalImpl implements StatsCollectorDalInterface {

    private static final long serialVersionUID = 1L;

    private final Logger LOGGER = Logger.getLogger(StatsCollectorDalImpl.class);

    private final DateTimeFormatter dtf = DateTimeFormat
            .forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    MongoClient mongoClient;
    DB statsDB;
    DB snapshotDB;
    DB newStatsDB;
    DB tempStatsDB;

    @Autowired
    public StatsCollectorDalImpl(@Value("${mongo.db.host}") String mongoHost) {
        try {
            mongoClient = new MongoClient(mongoHost);
            statsDB = mongoClient
                    .getDB(WideoMetricsCoreConstants.WIDEOMETRICS_STATS_DB_NAME);
            snapshotDB = mongoClient
                    .getDB(WideoMetricsCoreConstants.WIDEOMETRICS_SNAPSHOT_DB_NAME);
            newStatsDB = mongoClient.getDB("wideometrics-test");
            tempStatsDB = mongoClient.getDB("wideometrics-temp");
        }
        catch (UnknownHostException e) {
            LOGGER.error("Could not initialize mongo. Wrong host: " + mongoHost);
        }
    }

    @Override
    public ActionStats collectStatsForUser(JSONObject params) {
        try {
            DateTimeFormatter formatter = DateTimeFormat
                    .forPattern("yyyy-MM-dd");

            String dbCollection = params.getString("db_collection");
            String action = params.getString("action");
            String userID = params.getString("user_id");

            BasicDBObject fields = new BasicDBObject();
            fields.put("value.action", action);
            fields.put("value.user_id", Long.valueOf(userID));

            if (params.getJSONObject("date") != null) {
                JSONObject dateInterval = params.getJSONObject("date");
                String startDate = dateInterval.getString("start_date");
                String endDate = dateInterval.getString("end_date");
                DateTime startDateDateTime = formatter.withZone(
                        DateTimeZone.UTC).parseDateTime(startDate);
                // Sumo uno al final para hacer query lessthan(lt) y contemplar
                // hasta las 23:59:59 del dia anterior
                DateTime endDateDateTime = formatter.withZone(DateTimeZone.UTC)
                        .parseDateTime(endDate).plusDays(1);
                fields.put("value.date", new BasicDBObject("$gte",
                        startDateDateTime.toDate()).append("$lt",
                        endDateDateTime.toDate()));
            }
            else {
                fields.put("value.date", new BasicDBObject("$exists", 0));
            }

            DBObject groupFieldIds = new BasicDBObject();
            DBObject groupFields = new BasicDBObject();
            groupFieldIds.put("user_id", "$value.user_id");

            String groupField = params.optString("group_field");
            if ((groupField != null) && (!groupField.isEmpty())) {
                groupFieldIds.put(groupField, "$value." + groupField);
            }

            groupFields.put("_id", groupFieldIds);
            groupFields.put("count", new BasicDBObject("$sum", "$value.count"));
            return collectStatsByUserGroupedByDate(dbCollection, fields,
                    groupFields);
        }
        catch (Exception e) {
            LOGGER.error("Ocurrio un error parseando los parametros");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public StatsResults collectStatsForWideoForInteractions(JSONObject params) {
        try {
            DateTimeFormatter formatter = DateTimeFormat
                    .forPattern("yyyy-MM-dd");

            String dbCollection = params.getString("db_collection");
            String action = params.getString("action");
            String wideoID = params.getString("wideo");

            BasicDBObject fields = new BasicDBObject();
            fields.put("value.action", action);
            fields.put("value.wideo_id", wideoID);

            if (params.getJSONObject("date") != null) {
                JSONObject dateInterval = params.getJSONObject("date");
                String startDate = dateInterval.getString("start_date");
                String endDate = dateInterval.getString("end_date");
                DateTime startDateDateTime = formatter.withZone(
                        DateTimeZone.UTC).parseDateTime(startDate);
                // Sumo uno al final para hacer query lessthan(lt) y contemplar
                // hasta las 23:59:59 del dia anterior
                DateTime endDateDateTime = formatter.withZone(DateTimeZone.UTC)
                        .parseDateTime(endDate).plusDays(1);
                fields.put("value.date", new BasicDBObject("$gte",
                        startDateDateTime.toDate()).append("$lt",
                        endDateDateTime.toDate()));
            }
            else {
                fields.put("value.date", new BasicDBObject("$exists", 0));
            }

            DBObject groupFieldIds = new BasicDBObject();
            DBObject groupFields = new BasicDBObject();
            groupFieldIds.put("wideo_id", "$value.wideo_id");

            List<String> groupField = (List<String>) params.opt("group_field");
            if ((groupField != null) && (groupField.size() > 0)) {
                for (String field : groupField) {
                    groupFieldIds.put(field, "$value." + field);
                }
            }
            groupFields.put("_id", groupFieldIds);
            groupFields.put("count", new BasicDBObject("$sum", "$value.count"));

            return collectStatsGroupedByWideoByInteractionIDByDate(
                    dbCollection, fields, groupFields);
        }
        catch (Exception e) {
            LOGGER.error("Ocurrio un error parseando los parametros");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ActionStats collectStatsForWideo(JSONObject params) {
        try {
            DateTimeFormatter formatter = DateTimeFormat
                    .forPattern("yyyy-MM-dd");

            String dbCollection = params.getString("db_collection");
            String action = params.getString("action");
            String wideoID = params.getString("wideo");

            BasicDBObject fields = new BasicDBObject();
            fields.put("value.action", action);
            fields.put("value.wideo_id", wideoID);

            if (params.getJSONObject("date") != null) {
                JSONObject dateInterval = params.getJSONObject("date");
                String startDate = dateInterval.getString("start_date");
                String endDate = dateInterval.getString("end_date");
                DateTime startDateDateTime = formatter.withZone(
                        DateTimeZone.UTC).parseDateTime(startDate);
                // Sumo uno al final para hacer query lessthan(lt) y contemplar
                // hasta las 23:59:59 del dia anterior
                DateTime endDateDateTime = formatter.withZone(DateTimeZone.UTC)
                        .parseDateTime(endDate).plusDays(1);
                fields.put("value.date", new BasicDBObject("$gte",
                        startDateDateTime.toDate()).append("$lt",
                        endDateDateTime.toDate()));
            }
            else {
                fields.put("value.date", new BasicDBObject("$exists", 0));
            }

            DBObject groupFieldIds = new BasicDBObject();
            DBObject groupFields = new BasicDBObject();
            groupFieldIds.put("wideo_id", "$value.wideo_id");

            List<String> groupField = (List<String>) params.opt("group_field");
            if ((groupField != null) && (groupField.size() > 0)) {
                for (String field : groupField) {
                    groupFieldIds.put(field, "$value." + field);
                }
            }
            groupFields.put("_id", groupFieldIds);
            groupFields.put("count", new BasicDBObject("$sum", "$value.count"));

            return collectStatsByWideoGroupedByDate(dbCollection, fields,
                    groupFields);
        }
        catch (Exception e) {
            LOGGER.error("Ocurrio un error parseando los parametros");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ActionStats collectWideoDropOff(JSONObject params) {
        try {
            DateTimeFormatter formatter = DateTimeFormat
                    .forPattern("yyyy-MM-dd");

            String dbCollection = params.getString("db_collection");
            String action = params.getString("action");
            String wideo = params.getString("wideo");

            BasicDBObject fields = new BasicDBObject();
            fields.put("value.action", action);
            fields.put("value.wideo_id", wideo);

            if (params.getJSONObject("date") != null) {
                JSONObject dateInterval = params.getJSONObject("date");
                String startDate = dateInterval.getString("start_date");
                String endDate = dateInterval.getString("end_date");
                DateTime startDateDateTime = formatter.withZone(
                        DateTimeZone.UTC).parseDateTime(startDate);
                // Sumo uno al final para hacer query lessthan(lt) y contemplar
                // hasta las 23:59:59 del dia anterior
                DateTime endDateDateTime = formatter.withZone(DateTimeZone.UTC)
                        .parseDateTime(endDate).plusDays(1);
                fields.put("value.date", new BasicDBObject("$gte",
                        startDateDateTime.toDate()).append("$lt",
                        endDateDateTime.toDate()));
            }
            else {
                fields.put("value.date", new BasicDBObject("$exists", 0));
            }

            DBObject groupFieldIds = new BasicDBObject();
            DBObject groupFields = new BasicDBObject();
            groupFieldIds.put("wideo_id", "$value.wideo_id");

            List<String> groupField = (List<String>) params.opt("group_field");
            if ((groupField != null) && (groupField.size() > 0)) {
                for (String field : groupField) {
                    groupFieldIds.put(field, "$value." + field);
                }
            }
            groupFields.put("_id", groupFieldIds);
            groupFields.put("count", new BasicDBObject("$sum", "$value.count"));

            return collectStatsGroupByDropOff(dbCollection, fields, groupFields);
        }
        catch (Exception e) {
            LOGGER.error("Ocurrio un error parseando los parametros");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<WideoStatsResults> collectStatsByAction(JSONObject params) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        try {

            String dbCollection = params.getString("db_collection");
            String action = params.getString("action");
            String startDate = params.getString("start_date");
            String endDate = params.getString("end_date");

            DBObject fields = new BasicDBObject();
            fields.put("value.action", action);
            DateTime startDateDateTime = formatter.withZone(DateTimeZone.UTC)
                    .parseDateTime(startDate);
            // Sumo uno al final para hacer query lessthan(lt) y contemplar
            // hasta las 23:59:59 del dia anterior
            DateTime endDateDateTime = formatter.withZone(DateTimeZone.UTC)
                    .parseDateTime(endDate).plusDays(1);
            fields.put("value.date", new BasicDBObject("$gte",
                    startDateDateTime.toDate()).append("$lt",
                    endDateDateTime.toDate()));

            return collectStatsUngrouped(dbCollection, fields);
        }
        catch (Exception e) {
            return null;
        }
    }

    private List<WideoStatsResults> collectStatsUngrouped(String dbCollection,
            DBObject fields) {
        DBCollection coll = statsDB.getCollection(dbCollection);
        DBCursor cursor = coll.find(fields);

        Iterator<DBObject> cursorResults = cursor.iterator();
        List<WideoStatsResults> stats = new ArrayList<WideoStatsResults>();
        while (cursorResults.hasNext()) {
            WideoStatsResults wideoResult = new WideoStatsResults();

            BasicDBObject result = (BasicDBObject) cursorResults.next();
            BasicDBObject valueResult = (BasicDBObject) result.get("value");
            wideoResult.setWideoID(valueResult.getString("wideo_id"));

            stats.add(wideoResult);
        }
        return stats;
    }

    @Override
    public List<WideoStatsResults> collectSocialStats(JSONObject params) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        try {

            String dbCollection = params.getString("db_collection");
            String action = params.getString("action");
            String startDate = params.getString("start_date");
            String endDate = params.getString("end_date");

            DBObject fields = new BasicDBObject();
            fields.put("action", action);
            DateTime startDateDateTime = formatter.withZone(DateTimeZone.UTC)
                    .parseDateTime(startDate);
            // Sumo uno al final para hacer query lessthan(lt) y contemplar
            // hasta las 23:59:59 del dia anterior
            DateTime endDateDateTime = formatter.withZone(DateTimeZone.UTC)
                    .parseDateTime(endDate).plusDays(1);
            fields.put("date",
                    new BasicDBObject("$gte", startDateDateTime.toDate())
                            .append("$lt", endDateDateTime.toDate()));

            return collectSocialResults(dbCollection, fields);
        }
        catch (Exception e) {
            return null;
        }
    }

    private List<WideoStatsResults> collectSocialResults(String dbCollection,
            DBObject fields) {
        DBCollection coll = statsDB.getCollection(dbCollection);
        DBCursor cursor = coll.find(fields);

        Iterator<DBObject> cursorResults = cursor.iterator();
        List<WideoStatsResults> stats = new ArrayList<WideoStatsResults>();
        while (cursorResults.hasNext()) {
            WideoStatsResults wideoResult = new WideoStatsResults();

            BasicDBObject result = (BasicDBObject) cursorResults.next();
            wideoResult.setWideoID(result.getString("wideo_id"));

            stats.add(wideoResult);
        }
        return stats;
    }

    private ActionStats collectStatsByUserGroupedByDate(String dbCollection,
            DBObject fields, DBObject groupFields) {
        DBObject match = new BasicDBObject("$match", fields);
        DBObject group = new BasicDBObject("$group", groupFields);

        DBCollection coll = statsDB.getCollection(dbCollection);
        AggregationOutput output = coll.aggregate(match, group);

        Iterable<DBObject> results = output.results();

        ActionStats actionStats = new ActionStats(
                (String) fields.get("value.action"));
        Double total = new Double(0);

        for (DBObject result : results) {
            try {
                JSONObject jsonResult = new JSONObject(result.toString());
                Object value = jsonResult.get("count");
                JSONObject _id = (JSONObject) jsonResult.get("_id");
                JSONObject dateObject = _id.optJSONObject("date");

                if (dateObject != null) {
                    String date = dateObject.getString("$date");
                    actionStats.addStats(new ValuesStats(dtf.parseMillis(date),
                            value));
                    total += Double.valueOf(value.toString());
                    actionStats.sumTotal(total);
                }
                else {
                    actionStats.setTotal((Double) value);
                }

            }
            catch (Exception e) {
                LOGGER.error(
                        "An exception occured while reading results. Null results are retrieved",
                        e);
                return null;
            }
        }
        return actionStats;
    }

    private ActionStats collectStatsByWideoGroupedByDate(String dbCollection,
            DBObject fields, DBObject groupFields) {
        DBObject match = new BasicDBObject("$match", fields);
        DBObject group = new BasicDBObject("$group", groupFields);

        DBCollection coll = statsDB.getCollection(dbCollection);
        AggregationOutput output = coll.aggregate(match, group);

        Iterable<DBObject> results = output.results();

        ActionStats actionStats = new ActionStats(
                (String) fields.get("value.action"));

        for (DBObject result : results) {
            try {
                JSONObject jsonResult = new JSONObject(result.toString());
                Double value = jsonResult.getDouble("count");
                JSONObject _id = (JSONObject) jsonResult.get("_id");
                JSONObject dateObject = _id.optJSONObject("date");
                if (dateObject != null) {
                    String date = dateObject.getString("$date");
                    actionStats.addStats(dtf.parseMillis(date), value);
                    actionStats.sumTotal(value);
                }
                else {
                    actionStats.setTotal(value);
                }
            }
            catch (Exception e) {
                LOGGER.error(
                        "An exception occured while reading results. Null results are retrieved",
                        e);
                return null;
            }
        }

        return actionStats;
    }

    private StatsResults collectStatsGroupedByWideoByInteractionIDByDate(
            String dbCollection, DBObject fields, DBObject groupFields) {
        DBObject match = new BasicDBObject("$match", fields);
        DBObject group = new BasicDBObject("$group", groupFields);

        DBCollection coll = statsDB.getCollection(dbCollection);
        AggregationOutput output = coll.aggregate(match, group);

        Iterable<DBObject> results = output.results();

        InteractionsStats interactionsStats = new InteractionsStats();

        for (DBObject result : results) {
            try {
                JSONObject jsonResult = new JSONObject(result.toString());
                Double value = jsonResult.getDouble("count");
                JSONObject _id = (JSONObject) jsonResult.get("_id");
                String interactionID = _id.getString("interaction_id");

                ActionStats interactionStats = (ActionStats) interactionsStats
                        .getStats().get(interactionID);
                if (interactionStats == null) {
                    interactionStats = new ActionStats();
                }

                JSONObject dateObject = _id.optJSONObject("date");
                if (dateObject != null) {
                    String date = dateObject.getString("$date");
                    interactionStats.addStats(dtf.parseMillis(date), value);
                    interactionStats.sumTotal(value);
                    interactionsStats.sumTotal(value);
                    interactionsStats.addStats(interactionID, interactionStats);
                }
                else {
                    interactionStats.sumTotal(value);
                    interactionsStats.addStats(interactionID, interactionStats);
                    interactionsStats.sumTotal(value);
                }
            }
            catch (Exception e) {
                LOGGER.error(
                        "An exception occured while reading results. Null results are retrieved",
                        e);
                return null;
            }
        }

        return interactionsStats;
    }

    private ActionStats collectStatsGroupByDropOff(String dbCollection,
            DBObject fields, DBObject groupFields) {
        DBObject match = new BasicDBObject("$match", fields);
        DBObject group = new BasicDBObject("$group", groupFields);

        DBCollection coll = statsDB.getCollection(dbCollection);
        AggregationOutput output = coll.aggregate(match, group);

        Iterable<DBObject> results = output.results();

        ActionStats actionStats = new ActionStats(
                (String) fields.get("value.action"));
        for (DBObject result : results) {
            try {
                JSONObject jsonResult = new JSONObject(result.toString());
                Object value = jsonResult.get("count");
                JSONObject _id = (JSONObject) jsonResult.get("_id");
                Double playingSec = _id.optDouble("playing_sec");
                if (playingSec != null) {
                    actionStats.sumTotal((Double) value);
                    actionStats.addStats(new ValuesStats(
                            playingSec.longValue(), value));
                }
            }
            catch (Exception e) {
                LOGGER.error(
                        "An exception occured while reading results. Null results are retrieved",
                        e);
                return null;
            }
        }

        return actionStats;
    }

    @Override
    public Map<String, TreeMap<Date, FormsStats>> collectFormsInfoForWideo(
            JSONObject params) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        Map<String, TreeMap<Date, FormsStats>> results = new HashMap<String, TreeMap<Date, FormsStats>>();

        try {
            String dbCollection = params.getString("db_collection");
            String action = params.getString("action");
            String wideoID = params.getString("wideo");
            String interactionID = params.optString("interaction_id");

            DBCollection collection = snapshotDB.getCollection(dbCollection);

            DBObject fields = new BasicDBObject();
            fields.put("wideo_id", wideoID);
            fields.put("action", action);
            if ((interactionID != null) && (!interactionID.isEmpty())) {
                fields.put("interaction_id", interactionID);
            }

            if (params.getJSONObject("date") != null) {
                JSONObject dateInterval = params.getJSONObject("date");
                String startDate = dateInterval.getString("start_date");
                String endDate = dateInterval.getString("end_date");
                DateTime startDateDateTime = formatter.withZone(
                        DateTimeZone.UTC).parseDateTime(startDate);
                // Sumo uno al final para hacer query lessthan(lt) y contemplar
                // hasta las 23:59:59 del dia anterior
                DateTime endDateDateTime = formatter.withZone(DateTimeZone.UTC)
                        .parseDateTime(endDate).plusDays(1);
                fields.put("date",
                        new BasicDBObject("$gte", startDateDateTime.toDate())
                                .append("$lt", endDateDateTime.toDate()));
            }

            DBObject projectFields = new BasicDBObject();
            projectFields.put("_id", 0);
            projectFields.put("date", 1);
            projectFields.put("wideo_id", 1);
            projectFields.put("interaction_id", 1);
            projectFields.put("properties.fields", 1);
            projectFields.put("properties.destinationEmail", 1);
            DBObject match = new BasicDBObject("$match", fields);
            DBObject project = new BasicDBObject("$project", projectFields);

            DBCollection coll = snapshotDB.getCollection(dbCollection);
            AggregationOutput output = coll.aggregate(match, project);

            Iterable<DBObject> queryResults = output.results();
            for (DBObject result : queryResults) {
                try {
                    FormsStats formResult = new FormsStats();
                    // formResult.setWideoID((String) result.get("wideo_id"));
                    String interactionIDField = (String) result
                            .get("interaction_id");
                    TreeMap<Date, FormsStats> interactForms = results
                            .get(interactionIDField);

                    formResult.setDate((Date) result.get("date"));
                    Map<String, Object> data = new HashMap<String, Object>();
                    BasicDBObject properties = (BasicDBObject) result
                            .get("properties");
                    BasicDBList fieldsArray = (BasicDBList) properties
                            .get("fields");

                    for (Object field : fieldsArray) {

                        data.put((String) ((BasicDBObject) field).get("field"),
                                (String) ((BasicDBObject) field).get("value"));
                    }
                    formResult.setData(data);
                    if (interactForms != null) {
                        interactForms.put(formResult.getDate(), formResult);
                    }
                    else {
                        interactForms = new TreeMap<Date, FormsStats>(
                                new DateComp("FORM_SENT", "asc"));
                        interactForms.put(formResult.getDate(), formResult);
                    }
                    results.put(interactionIDField, interactForms);
                    // results.add(formResult);
                }
                catch (Exception e) {
                    LOGGER.error("Error obteniendo informacion del usuario", e);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(
                    "Ocurrio un error procesando la data de formularios para los params: "
                            + params.toString(), e);
        }
        return results;

    }

    @Override
    public Map<String, Object> collectWideoStats(JSONObject params) {
        try {
            String wideo = params.getString("wideo_id");
            String startDate = params.getString("start_date");
            String endDate = params.getString("end_date");
            Boolean filterByProperties = params
                    .optBoolean("filterByProperties");

            DateTimeFormatter formatter = DateTimeFormat
                    .forPattern("yyyy-MM-dd");
            DateTime startDateDateTime = formatter.withZone(DateTimeZone.UTC)
                    .parseDateTime(startDate);
            // Se suma uno a la fecha de fin para hacer query por Less Than
            DateTime endDateDateTime = formatter.withZone(DateTimeZone.UTC)
                    .parseDateTime(endDate).plusDays(1);

            DBCollection coll = tempStatsDB.getCollection("wideos");
            DBCursor results = null;

            DBObject fields = new BasicDBObject();
            fields.put("startDate", startDateDateTime.toDate());
            fields.put("endDate", endDateDateTime.toDate());
            fields.put("wideo_id", wideo);
            fields.put("filteredByProperties", filterByProperties);
            results = coll.find(fields);
            // Primero se busca si previamente ya se procesarn las estadisticas
            // para ese rango de fechas
            if (results.itcount() > 0) {
                System.out.println("Query already processed");
                DBObject result = results.iterator().next();
                BasicDBObject statsJson = (BasicDBObject) result.get("stats");
                return statsJson.toMap();
            }
            // Si no se encuentran, se procesan iterando sobre cada dia por
            // separado
            if (results.itcount() == 0) {
                fields = new BasicDBObject();
                fields.put("startDate", new BasicDBObject("$gte",
                        startDateDateTime.toDate()).append("$lt",
                        endDateDateTime.toDate()));
                fields.put("endDate", new BasicDBObject("$exists", 0));
                fields.put("wideo_id", wideo);
                fields.put("propertyKey", new BasicDBObject("$exists", 0));
                coll = newStatsDB.getCollection("wideos");
                results = coll.find(fields);

            }
            if (results.itcount() > 0) {
                System.out
                        .println("Query wasn't find. Processing days separately");
                Iterator<DBObject> it = results.iterator();
                Map<String, Object> sumStats = new HashMap<String, Object>();
                while (it.hasNext()) {
                    DBObject dayStats = it.next();
                    BasicDBObject stats = (BasicDBObject) dayStats.get("stats");
                    for (String key : stats.keySet()) {
                        if (key.equals("WIDEO_PLAYING")) {
                            Map<String, Double> playingSecs = (Map<String, Double>) stats
                                    .get(key);
                            Map<String, Double> oldPlayingSecs = (Map<String, Double>) sumStats
                                    .get(key);
                            if (oldPlayingSecs != null) {
                                for (String playingSec : playingSecs.keySet()) {
                                    Double value = playingSecs.get(playingSec);
                                    Double oldValue = oldPlayingSecs
                                            .get(playingSec);
                                    if (oldValue != null) {
                                        value += oldValue;
                                    }
                                    playingSecs.put(playingSec, value);
                                }
                            }
                            sumStats.put(key, playingSecs);
                        }
                        else
                            if (key.equals("interactions")) {
                                Map<String, Object> interactions = (Map<String, Object>) stats
                                        .get(key);
                                Map<String, Object> oldInteractions = (Map<String, Object>) sumStats
                                        .get(key);
                                if (oldInteractions != null) {
                                    for (String interactionID : interactions
                                            .keySet()) {
                                        Map<String, Double> interactionsValues = (Map<String, Double>) interactions
                                                .get(interactionID);
                                        Map<String, Double> oldInteractionsValues = (Map<String, Double>) oldInteractions
                                                .get(interactionID);
                                        if (oldInteractionsValues != null) {
                                            for (String interactionKey : interactionsValues
                                                    .keySet()) {
                                                Double value = interactionsValues
                                                        .get(interactionKey);
                                                Double oldValue = oldInteractionsValues
                                                        .get(interactionKey);
                                                if (oldValue != null) {
                                                    value += oldValue;
                                                }
                                                oldInteractionsValues.put(
                                                        interactionKey, value);
                                            }
                                        }
                                        oldInteractions.put(interactionID,
                                                oldInteractionsValues);
                                        sumStats.put("interactions",
                                                oldInteractions);
                                    }
                                }
                                else {
                                    sumStats.put("interactions", interactions);
                                }
                            }
                            else {
                                System.out.println(key);
                                Double value = stats.getDouble(key);
                                Double oldValue = (Double) sumStats.get(key);
                                if (oldValue != null) {
                                    value += oldValue;
                                }
                                sumStats.put(key, value);
                            }
                    }
                }

                if (filterByProperties) {
                    String[] properties = new String[] { "device_category",
                            "os", "browser", "location" };
                    for (String property : properties) {
                        Map<String, Object> propertyStats = processByProperty(
                                property, fields, coll);
                        if (propertyStats != null) {
                            sumStats.put(property, propertyStats);
                        }
                    }
                }
                // Se guarda un nuevo documento para ese rango de fechas
                if (sumStats != null) {
                    WideoFilteredStats newDocument = new WideoFilteredStats(
                            new Date(), wideo, startDateDateTime.toDate(),
                            endDateDateTime.toDate(), filterByProperties);
                    newDocument.setSats(sumStats);
                    coll = tempStatsDB.getCollection("wideos");
                    coll.insert(newDocument.toDBObject());
                }
                return sumStats;
            }
        }
        catch (Exception e) {
            LOGGER.error(
                    "Ocurrio un error obteniendo estadisticas para los parametros. "
                            + params.toString(), e);
        }
        return null;
    }

    public Map<String, Object> processByProperty(String propertyKey,
            DBObject fields, DBCollection coll) {
        try {
            fields.put("propertyKey", propertyKey);
            DBCursor results = coll.find(fields);
            if (results.itcount() > 0) {
                Iterator<DBObject> it = results.iterator();
                Map<String, Object> propertyValues = new HashMap<String, Object>();
                while (it.hasNext()) {
                    DBObject dayStats = it.next();
                    BasicDBObject stats = (BasicDBObject) dayStats.get("stats");
                    // Tomo cada valor de property. Ej: Desktop, Smartphone,
                    // Tablet
                    for (String propertyValue : stats.keySet()) {
                        BasicDBObject propertyStats = (BasicDBObject) stats
                                .get(propertyValue);
                        for (String key : propertyStats.keySet()) {
                            if ("WIDEO_VIEW".equals(key)) {
                                Double value = propertyStats.getDouble(key);
                                Map<String, Object> propertyKeysSumStats = (Map<String, Object>) propertyValues
                                        .get(propertyValue);
                                if (propertyKeysSumStats == null) {
                                    propertyKeysSumStats = new HashMap<String, Object>();
                                }
                                Double oldValue = null;
                                if (propertyKeysSumStats != null) {
                                    oldValue = (Double) propertyKeysSumStats
                                            .get(key);
                                }
                                if (oldValue != null) {
                                    value += oldValue;
                                }
                                propertyKeysSumStats.put(key, value);
                                propertyValues.put(propertyValue,
                                        propertyKeysSumStats);
                            }
                        }
                    }
                }
                return propertyValues;
            }
        }
        catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    @Override
    public Map<String, Object> collectUserStats(JSONObject params) {
        try {
            Long userID = params.getLong("user_id");
            String startDate = params.getString("start_date");
            String endDate = params.getString("end_date");
            Boolean filterByProperties = params
                    .optBoolean("filterByProperties");

            DateTimeFormatter formatter = DateTimeFormat
                    .forPattern("yyyy-MM-dd");
            DateTime startDateDateTime = formatter.withZone(DateTimeZone.UTC)
                    .parseDateTime(startDate);

            // Se suma uno a la fecha de fin para hacer query por Less Than
            DateTime endDateDateTime = formatter.withZone(DateTimeZone.UTC)
                    .parseDateTime(endDate).plusDays(1);

            DBCollection coll = tempStatsDB.getCollection("users");
            DBCursor results = null;

            DBObject fields = new BasicDBObject();
            fields.put("startDate", startDateDateTime.toDate());
            fields.put("endDate", endDateDateTime.toDate());
            fields.put("user_id", userID);
            fields.put("filterByProperties", filterByProperties);
            results = coll.find(fields);
            // Primero se busca si previamente ya se procesarn las estadisticas
            // para ese rango de fechas
            if (results.itcount() > 0) {
                System.out.println("Query already processed");
                DBObject result = results.iterator().next();
                BasicDBObject statsJson = (BasicDBObject) result.get("stats");
                return statsJson.toMap();
            }
            // Si no se encuentran, se procesan iterando sobre cada dia por
            // separado
            if (results.itcount() == 0) {
                fields = new BasicDBObject();
                fields.put("startDate", new BasicDBObject("$gte",
                        startDateDateTime.toDate()).append("$lt",
                        endDateDateTime.toDate()));
                fields.put("endDate", new BasicDBObject("$exists", 0));
                fields.put("user_id", userID);
                fields.put("propertyKey", new BasicDBObject("$exists", 0));
                coll = newStatsDB.getCollection("users");
                results = coll.find(fields);
            }
            if (results.itcount() > 0) {
                System.out
                        .println("Query wasn't find. Processing days separately");
                Iterator<DBObject> it = results.iterator();
                Map<String, Object> sumStats = new HashMap<String, Object>();
                while (it.hasNext()) {
                    DBObject dayStats = it.next();
                    BasicDBObject stats = (BasicDBObject) dayStats.get("stats");
                    for (String key : stats.keySet()) {
                        Double value = stats.getDouble(key);
                        Double oldValue = (Double) sumStats.get(key);
                        if (oldValue != null) {
                            value += oldValue;
                        }
                        sumStats.put(key, value);
                    }
                }

                if (filterByProperties) {
                    String[] properties = new String[] { "device_category",
                            "os", "browser", "location" };
                    for (String property : properties) {
                        Map<String, Object> propertyStats = processByProperty(
                                property, fields, coll);
                        if (propertyStats != null) {
                            sumStats.put(property, propertyStats);
                        }
                    }
                }

                // Se guarda un nuevo documento para ese rango de fechas
                if (sumStats != null) {
                    UserFilteredStats newDocument = new UserFilteredStats(
                            new Date(), userID, startDateDateTime.toDate(),
                            endDateDateTime.toDate(), filterByProperties);
                    newDocument.setStats(sumStats);
                    coll = tempStatsDB.getCollection("users");
                    coll.insert(newDocument.toDBObject());
                }
                return sumStats;
            }
        }
        catch (Exception e) {
            LOGGER.error(
                    "Ocurrio un error obteniendo estadisticas para los parametros. "
                            + params.toString(), e);
        }
        return null;
    }

    @Override
    public List<String> collectActiveWideosStats(JSONObject params) {
        try {
            List<String> wideos = new ArrayList<String>();

            DateTime startDate = (DateTime) params.get("start_date");
            DateTime endDate = (DateTime) params.get("end_date");

            DBCursor results = null;

            DBObject fields = new BasicDBObject();

            fields = new BasicDBObject();
            fields.put("startDate",
                    new BasicDBObject("$gte", startDate.toDate())
                            .append("$lt", endDate.toDate()));
            fields.put("endDate", new BasicDBObject("$exists", 0));
            DBCollection coll = newStatsDB.getCollection("wideos");
            results = coll.find(fields);

            if (results.itcount() > 0) {
                Iterator<DBObject> it = results.iterator();
                while (it.hasNext()) {
                    DBObject dayStats = it.next();
                    BasicDBObject stats = (BasicDBObject) dayStats.get("stats");
                    String wideoID = (String) dayStats.get("wideo_id");
                    if ((stats.containsKey("WIDEO_PLAY"))
                            || (stats.containsKey("YOUTUBE_VIEWS"))) {
                        if (!wideos.contains(wideoID)) {
                            wideos.add(wideoID);
                        }
                    }
                }
            }
            return wideos;
        }
        catch (Exception e) {
            LOGGER.error(
                    "Ocurrio un error obteniendo estadisticas para los parametros. "
                            + params.toString(), e);
        }
        return null;
    }

    @Override
    public Map<Long, Object> getUsersStatsForMailing(JSONObject params) {

        Map<Long, Object> usersStats = new HashMap<Long, Object>();

        try {
            DBCollection coll;
            coll = newStatsDB.getCollection("users");

            String startDate = params.getString("start_date");
            String endDate = params.getString("end_date");

            DateTimeFormatter formatter = DateTimeFormat
                    .forPattern("yyyy-MM-dd");
            DateTime startDateDateTime = formatter.withZone(DateTimeZone.UTC)
                    .parseDateTime(startDate);

            // Se suma uno a la fecha de fin para hacer query por Less Than
            DateTime endDateDateTime = formatter.withZone(DateTimeZone.UTC)
                    .parseDateTime(endDate).plusDays(1);

            DBObject matchFields = new BasicDBObject();

            matchFields.put("startDate", new BasicDBObject("$gte",
                    startDateDateTime.toDate()).append("$lt",
                    endDateDateTime.toDate()));

            DBObject projectFields = new BasicDBObject();
            projectFields.put("_id", 0);
            projectFields.put("user_id", 1);
            projectFields.put("stats.WIDEO_VIEW", 1);
            projectFields.put("stats.WIDEO_PLAY", 1);
            projectFields.put("stats.SECONDS_WATCHED", 1);

            DBObject groupFields = new BasicDBObject();
            groupFields.put("_id", "$user_id");
            groupFields.put("views", new BasicDBObject("$sum",
                    "$stats.WIDEO_VIEW"));
            groupFields.put("plays", new BasicDBObject("$sum",
                    "$stats.WIDEO_PLAY"));
            groupFields.put("secondsWatched", new BasicDBObject("$sum",
                    "$stats.SECONDS_WATCHED"));
            DBObject sortFields = new BasicDBObject();
            sortFields.put("plays", -1);

            DBObject match = new BasicDBObject("$match", matchFields);
            DBObject project = new BasicDBObject("$project", projectFields);
            DBObject group = new BasicDBObject("$group", groupFields);
            DBObject sort = new BasicDBObject("$sort", sortFields);

            AggregationOutput output = coll.aggregate(match, project, group,
                    sort);

            Iterable<DBObject> results = output.results();

            Iterator<DBObject> it = results.iterator();
            boolean processResults = true;
            while (processResults) {
                if (it.hasNext()) {
                    DBObject result = it.next();

                    try {
                        Long userID = (Long) result.get("_id");

                        Double views = (Double) result.get("views");
                        Double plays = (Double) result.get("plays");
                        Double minutesWatched = ((Double) result
                                .get("secondsWatched") / 60);

                        if (plays >= 5) {
                            matchFields.put("user_id", userID);
                            Map<String, Object> propertyResults = processByProperty(
                                    "location", matchFields, coll);
                            Map<String, Object> data = new HashMap<String, Object>();
                            data.put("views", views);
                            data.put("plays", plays);
                            data.put("minutesWatched", minutesWatched);
                            data.put("viewsByLocation", propertyResults);

                            usersStats.put(userID, data);
                        }
                        else {
                            processResults = false;
                        }
                    }
                    catch (Exception e) {
                        LOGGER.info(
                                "No data to recover from " + result.toString(),
                                e);
                    }
                }
                else {
                    processResults = false;
                }

            }
        }
        catch (Exception e) {
            LOGGER.info("Error obteniendo las stats para mailing", e);
        }
        return usersStats;
    }

    public Map<String, Object> getMetadataInfo(JSONObject params) {
        Map<String, Object> results = new HashMap<String, Object>();

        try {
            DateTime startDate = (DateTime) params.get("start_date");
            DateTime endDate = (DateTime) params.get("end_date");
            endDate = endDate.plusDays(1);
            String collection = params.getString("db_collection");

            DateTimeFormatter formatter = DateTimeFormat
                    .forPattern("yyyy-MM-dd");

            DBObject fields = new BasicDBObject();
            fields.put("interactions", new BasicDBObject("$exists", 1));

            fields.put("created_at",
                    new BasicDBObject("$gte", startDate.toDate()).append("$lt",
                            endDate.toDate()));

            DBCollection coll = statsDB.getCollection("metadata");
            DBCursor cursor = coll.find(fields);

            results.put("interactivity", cursor.size());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public Map<String, Object> getTotalStats(JSONObject params) {

        Map<String, Object> stats = new HashMap<String, Object>();

        try {
            DBCollection coll;
            coll = newStatsDB.getCollection("users");

            DateTime startDate = (DateTime) params.get("start_date");
            DateTime endDate = (DateTime) params.get("end_date");
            endDate = endDate.plusDays(1);

            DBObject matchFields = new BasicDBObject();

            matchFields.put("startDate",
                    new BasicDBObject("$gte", startDate.toDate()).append("$lt",
                            endDate.toDate()));

            DBObject projectFields = new BasicDBObject();
            projectFields.put("_id", 0);
            projectFields.put("stats.WIDEO_VIEW", 1);
            projectFields.put("stats.WIDEO_PLAY", 1);
            projectFields.put("stats.OBJECT_CLICK", 1);
            projectFields.put("stats.FORM_SENT", 1);

            DBObject groupFields = new BasicDBObject();
            groupFields.put("_id", "total");
            groupFields.put("views", new BasicDBObject("$sum",
                    "$stats.WIDEO_VIEW"));
            groupFields.put("plays", new BasicDBObject("$sum",
                    "$stats.WIDEO_PLAY"));
            groupFields.put("objectClicks", new BasicDBObject("$sum",
                    "$stats.OBJECT_CLICK"));
            groupFields.put("formSent", new BasicDBObject("$sum",
                    "$stats.FORM_SENT"));

            DBObject sortFields = new BasicDBObject();

            DBObject match = new BasicDBObject("$match", matchFields);
            DBObject project = new BasicDBObject("$project", projectFields);
            DBObject group = new BasicDBObject("$group", groupFields);

            AggregationOutput output = coll.aggregate(match, project, group);

            Iterable<DBObject> results = output.results();

            Iterator<DBObject> it = results.iterator();
            while (it.hasNext()) {
                
                DBObject result = it.next();
                Double views = (Double)result.get("views");
                Double plays = (Double)result.get("plays");
                Double forms = (Double)result.get("formSent");
                Double objectClicks = (Double)result.get("objectClicks");
                stats.put("views", views);
                stats.put("plays", plays);
                stats.put("forms", forms);
                stats.put("objectClicks", objectClicks);
            }

        }
        catch (Exception e) {
            LOGGER.info("Error obteniendo las stats para mailing", e);
        }
        return stats;
    }
}
