package com.wideo.metrics.bo.processor.task.refactor;

import java.util.Date;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.MongoException;
import com.wideo.metrics.bo.processor.StatsFilterProcessorBO;
import com.wideo.metrics.bo.processor.StatsFilterProcessorBOImpl;
import com.wideo.metrics.bo.processor.task.Task;
import com.wideo.metrics.bo.processor.task.response.MapReduceMongoErrorResponse;
import com.wideo.metrics.bo.processor.task.response.SuccessfulTaskExecutionResponse;
import com.wideo.metrics.bo.processor.task.response.TaskResponse;
import com.wideo.metrics.bo.processor.task.response.UnknownTaskErrorResponse;
import com.wideo.metrics.constants.WideoMetricsCoreConstants;
import com.wideo.metrics.events.WideoEventTypesEnum;
import com.wideo.metrics.model.processor.key.ProcessingKey;

public class WideoPlayingByWideoByDayTask_2 extends Task_2 {

    private static final Logger LOGGER = Logger.getLogger(WideoPlayingByWideoByDayTask_2.class);
    
    public WideoPlayingByWideoByDayTask_2(DBCollection dbCollection, StatsFilterProcessorBOImpl statsFilterProcessorBOImpl) {
        super(dbCollection, statsFilterProcessorBOImpl);
    }

    public TaskResponse process(ProcessingKey processingKey,
            DBObject mapReduceQuery, String sbKey) {
        try {

            StringBuilder sbMapKey = new StringBuilder();
            sbMapKey.append("{");
            sbMapKey.append("action: ");
            sbMapKey.append("\"" + WideoEventTypesEnum.WIDEO_PLAYING.getName()
                    + "\"");
            sbMapKey.append(",");
            sbMapKey.append("wideo_id: this.wideo_id");
            sbMapKey.append(",");
            sbMapKey.append("playing_sec: Number(this.properties.playing_sec)");
            sbMapKey.append(",");
            sbMapKey.append("date: date_utc");
            sbMapKey.append("}");

            StringBuilder sbMap = new StringBuilder();
            sbMap.append("function() {");
            sbMap.append("var date_object= new Date(this.date);");
            sbMap.append("var date_processed = date_object.getUTCFullYear() + \"-\" + (date_object.getUTCMonth() + 1) + \"-\" + date_object.getUTCDate();");
            sbMap.append("var date_utc = new Date(date_processed + \" 00:00:00 UTC\");");
            sbMap.append("var key=");
            sbMap.append(sbMapKey.toString() + ";");
            sbMap.append("emit(key, { wideo_id: this.wideo_id, playing_sec: Number(this.properties.playing_sec), date: date_utc, action: this.action, count: 1});");
            sbMap.append("}");

            String map = sbMap.toString();
            String reduce = "function (key, values) { "
                    + " total = 0; "
                    + " for (var i in values) { "
                    + " total += values[i].count; "
                    + " } "
                    + " return { wideo_id: values[i].wideo_id, playing_sec: values[i].playing_sec, date: values[i].date, action: values[i].action, count:total } }";

            LOGGER.info("Creando instancia de MapReduceCommand");
            MapReduceCommand cmd = new MapReduceCommand(dbCollection, map,
                    reduce, processingKey.getProcessingCollection(),
                    MapReduceCommand.OutputType.INLINE, mapReduceQuery);
            LOGGER.info("Instancia creada");
            cmd.setOutputDB(WideoMetricsCoreConstants.WIDEOMETRICS_STATS_DB_NAME);
            try {
                LOGGER.info("Mando a hacer mapReduce");
                MapReduceOutput out = dbCollection.mapReduce(cmd);
                BasicDBObject dateObject = (BasicDBObject) mapReduceQuery
                        .get("date");
                LOGGER.info("Finalizo map reduce. Mando a guardar los resultados");
                filterPlayingSecStats(out.results(), (Date) dateObject.get("$gte"));
                LOGGER.info("Termino de guardar los datos");
            }
            catch (MongoException e) {
                JSONObject errorJsonData = parseMongoErrorJsonResponse(
                        processingKey.getName(), sbMap.toString(),
                        mapReduceQuery.get("date"), e.getMessage());
                return new MapReduceMongoErrorResponse(errorJsonData.toString());
            }

            return new SuccessfulTaskExecutionResponse();
        }
        catch (Exception e) {
            JSONObject errorJsonData = parseErrorJsonResponse(
                    processingKey.getName(), mapReduceQuery.get("date"),
                    e.getMessage());
            return new UnknownTaskErrorResponse(errorJsonData);
        }
    }
}
