package com.wideo.metrics.persistence.dal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.PortableInterceptor.USER_EXCEPTION;

import com.github.jmkgreen.morphia.AdvancedDatastore;
import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.Morphia;
import com.github.jmkgreen.morphia.dao.BasicDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.QueryBuilder;
import com.wideo.metrics.events.EventNamespaceTypesEnum;
import com.wideo.metrics.model.event.GenericEvent;
import com.wideo.metrics.model.event.GenericStatsEvent;
import com.wideo.metrics.persistence.dal.interfaces.EventTrackDalInterface;

public class EventTrackDalImpl implements EventTrackDalInterface {

    private static final String USER_COLLECTION = "user";
    private static final String PLAYER_COLLECTION = "player";
    private static final String INTERACTIVITY_COLLECTION = "interactivity";
    private static final String SOCIAL_COLLECTION = "social";
    private static final String WIDEOS_COLLECTION = "wideos";
    private static final String EDITOR_COLLECTION = "editor";

    DB trackingDatabase;
    DB trackingStatsDatabase;

    Map<String, String> namespaceCollections;

    private EventTrackDalImpl(Mongo mongo, String dbName, String dbStatsName) {
        trackingDatabase = mongo.getDB(dbName);
        trackingStatsDatabase = mongo.getDB(dbStatsName);
        initNamespaceCollections();
    }

    private void initNamespaceCollections() {
        namespaceCollections = new HashMap<String, String>();
        namespaceCollections.put(EventNamespaceTypesEnum.editor.getName(),
                EDITOR_COLLECTION);
        namespaceCollections.put(EventNamespaceTypesEnum.player.getName(),
                PLAYER_COLLECTION);
        namespaceCollections.put(EventNamespaceTypesEnum.user.getName(),
                USER_COLLECTION);
        namespaceCollections.put(
                EventNamespaceTypesEnum.interactivity.getName(),
                INTERACTIVITY_COLLECTION);
        namespaceCollections.put(EventNamespaceTypesEnum.social.getName(),
                SOCIAL_COLLECTION);
        namespaceCollections.put(EventNamespaceTypesEnum.wideos.getName(),
                WIDEOS_COLLECTION);
    }

    public void trackEvent(GenericEvent event) {
        DBCollection collection = trackingDatabase
                .getCollection(namespaceCollections.get(event.getNamespace()
                        .toLowerCase()));
        collection.save(event);
    }

    public void trackSocialStatsEvent(GenericStatsEvent event) {
        DBCollection collection = trackingStatsDatabase
                .getCollection(namespaceCollections.get(event.getNamespace()
                        .toLowerCase()));
        collection.save(event);
    }

    public void trackWideoStats(GenericEvent event) {
        DBCollection collection = trackingStatsDatabase
                .getCollection("metadata");

        BasicDBObject fields = new BasicDBObject();
        fields.put("action", "WIDEO_INFO");
        fields.put("wideo_id", event.get("wideo_id"));

        DBObject savedWideo = collection.findAndRemove(fields);
        
        collection.save(event);
    }

}
