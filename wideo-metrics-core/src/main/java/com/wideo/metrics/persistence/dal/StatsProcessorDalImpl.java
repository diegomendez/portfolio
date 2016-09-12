package com.wideo.metrics.persistence.dal;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import com.github.jmkgreen.morphia.Morphia;
import com.github.jmkgreen.morphia.dao.BasicDAO;
import com.github.jmkgreen.morphia.query.Query;
import com.mongodb.Mongo;
import com.wideo.metrics.model.processor.key.ProcessingKey;
import com.wideo.metrics.model.processor.stats.FilteredStats;
import com.wideo.metrics.model.processor.stats.UserFilteredStats;
import com.wideo.metrics.model.processor.stats.WideoFilteredStats;
import com.wideo.metrics.persistence.dal.interfaces.StatsProcessorDalInterface;

public class StatsProcessorDalImpl extends BasicDAO<WideoFilteredStats, String>
        implements StatsProcessorDalInterface {

    private static final Logger LOGGER = Logger.getLogger(StatsProcessorDalImpl.class);
    
    public StatsProcessorDalImpl(Mongo mongo, Morphia morphia, String dbName) {
        super(mongo, morphia, dbName);
    }

    @Override
    public boolean updateWideoFilteredStats(String wideoID, Date date,
            Double value, String action, String propertyKey,
            String propertyValue) {
        Query<WideoFilteredStats> q = ds.find(WideoFilteredStats.class)
                .field("wideo_id").equal(wideoID).field("startDate")
                .equal(date).field("endDate").equal(null);
        q.field("propertyKey").equal(propertyKey);

        WideoFilteredStats wideoFilteredStats = q.get();

        if (wideoFilteredStats != null) {
            wideoFilteredStats.updateStats(propertyValue, action, value);
        }
        else {
            WideoFilteredStats newDocument = new WideoFilteredStats(wideoID,
                    date, propertyKey);
            newDocument.updateStats(propertyValue, action, value);
            wideoFilteredStats = newDocument;
        }

        ds.save(wideoFilteredStats);

        return true;
    }

    @Override
    public boolean updateWideoFilteredStatsPlayingSec(String wideoID,
            Date date, Double playingSec, Double value, String action) {
        Query<WideoFilteredStats> q = ds.find(WideoFilteredStats.class)
                .field("wideo_id").equal(wideoID).field("startDate")
                .equal(date).field("endDate").equal(null).field("propertyKey")
                .equal(null);
        WideoFilteredStats wideoFilteredStats = q.get();

        if (wideoFilteredStats != null) {
            LOGGER.info(wideoFilteredStats.toDBObject().toString());
            wideoFilteredStats.updatePlayingSecStats(action, playingSec, value);
        }
        else {
            WideoFilteredStats newDocument = new WideoFilteredStats(wideoID,
                    date);
            newDocument.updatePlayingSecStats(action, playingSec, value);
            wideoFilteredStats = newDocument;
        }

        ds.save(wideoFilteredStats);

        return true;
    }

    @Override
    public boolean updateUserFilteredStats(Long userID, Date date,
            Double value, String action, String propertyKey,
            String propertyValue) {
        Query<UserFilteredStats> q = ds.find(UserFilteredStats.class)
                .field("user_id").equal(userID).field("startDate").equal(date)
                .field("endDate").equal(null);
        q.field("propertyKey").equal(propertyKey);

        UserFilteredStats userFilteredStats = q.get();

        if (userFilteredStats != null) {
            userFilteredStats.updateStats(propertyValue, action, value);
        }
        else {
            UserFilteredStats newDocument = new UserFilteredStats(userID, date,
                    propertyKey);
            newDocument.updateStats(propertyValue, action, value);
            userFilteredStats = newDocument;
        }

        ds.save(userFilteredStats);

        return true;
    }

    public boolean updateWideoInteractionsFilteredStats(String wideoID,
            String action, String interactionID, Double value, Date date) {
        Query<WideoFilteredStats> q = ds.find(WideoFilteredStats.class)
                .field("wideo_id").equal(wideoID).field("startDate")
                .equal(date).field("endDate").equal(null).field("propertyKey")
                .equal(null);;
        WideoFilteredStats wideoFilteredStats = q.get();

        if (wideoFilteredStats != null) {
            wideoFilteredStats.updateInteractionsStats(action, interactionID,
                    value);
        }
        else {
            WideoFilteredStats newDocument = new WideoFilteredStats(wideoID,
                    date);
            newDocument.updateInteractionsStats(action, interactionID, value);
            wideoFilteredStats = newDocument;
        }

        ds.save(wideoFilteredStats);

        return true;
    }

}
