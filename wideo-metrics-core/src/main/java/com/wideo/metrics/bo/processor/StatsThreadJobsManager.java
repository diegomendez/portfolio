package com.wideo.metrics.bo.processor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.wideo.metrics.bo.processor.key.StatsPendingJobsBO;
import com.wideo.metrics.bo.processor.key.StatsProcessingKeysBO;
import com.wideo.metrics.bo.processor.task.response.DateNotLivedException;
import com.wideo.metrics.bo.processor.task.response.ErrorTaskResponse;
import com.wideo.metrics.bo.processor.task.response.MapReduceMongoErrorResponse;
import com.wideo.metrics.bo.processor.task.response.ProcessingKeyUnknownException;
import com.wideo.metrics.bo.processor.task.response.SuccessfulTaskExecutionResponse;
import com.wideo.metrics.bo.processor.task.response.TaskResponse;
import com.wideo.metrics.bo.processor.task.response.TaskResponseEnum;
import com.wideo.metrics.bo.processor.task.response.UnknownTaskErrorResponse;
import com.wideo.metrics.bo.processor.task.response.WarningTaskExecutionResponse;
import com.wideo.metrics.constants.WideoMetricsCoreConstants;
import com.wideo.metrics.exceptions.InvalidProcessingKeyException;
import com.wideo.metrics.mail.MailSender;
import com.wideo.metrics.model.processor.key.PendingJobKey;
import com.wideo.metrics.model.processor.key.ProcessingKey;

public class StatsThreadJobsManager {

    private static final Logger LOGGER = Logger
            .getLogger(StatsThreadJobsManager.class);

    private Mongo mongo;

    @Autowired
    StatsProcessingKeysBO statsProcessingKeysBO;

    @Autowired
    StatsPendingJobsBO statsPendingJobsBO;

    @Autowired
    MailSender mailSender;
    
    @Autowired
    StatsFilterProcessorBO statsFilterProcessorBO;

    private static DateTimeFormatter formatter = DateTimeFormat
            .forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public StatsThreadJobsManager(Mongo mongo) {
        this.mongo = mongo;
    }

    public TaskResponse performPetition(String processingKeyName) {
        try {
            long lap = System.currentTimeMillis();

            ProcessingKey processingKey = statsProcessingKeysBO
                    .getProcessingKey(processingKeyName);

            if (processingKey == null) {
                LOGGER.error("No existe la key " + processingKeyName
                        + " en la coleccion keys");
                return new ProcessingKeyUnknownException();
            }

            DateTime startDate = null;
            if (processingKey.getLastDateProcessed() != null) {
                DateTime lastProcessedDate = new DateTime(
                        processingKey.getLastDateProcessed());
                startDate = lastProcessedDate.toDateTime(DateTimeZone.UTC);
            }
            else {
                startDate = formatter.withZone(DateTimeZone.UTC).parseDateTime(
                        WideoMetricsCoreConstants.BEGINNING_OF_ERA_DATE
                                .toString());
            }

            DateTime toDate = startDate.plusHours(processingKey
                    .getBatchInHours());
            
            if (startDate.isAfterNow() || toDate.isAfterNow()) {
                return new DateNotLivedException();
            }
            else {

                statsProcessingKeysBO.updateLastProcessedDateForKey(
                        processingKey.getName(), toDate.toDate());

                DBObject mapReduceQuery = new BasicDBObject();
                mapReduceQuery.put("action",
                        processingKey.getProcessingAction());
                mapReduceQuery.put("date",
                        new BasicDBObject("$gte", startDate.toDate()).append(
                                "$lt", toDate.toDate()));

                ExecutorService executor = Executors.newFixedThreadPool(10);
                List<Future<TaskResponse>> list = new ArrayList<Future<TaskResponse>>();
                Callable<TaskResponse> statsProcessorCallableInstance = new StatsProcessorCallableImpl(
                        mongo, mapReduceQuery, processingKey, statsFilterProcessorBO);
                try {
                    Future<TaskResponse> submit = executor
                            .submit(statsProcessorCallableInstance);
                    list.add(submit);
                }
                catch (Exception e) {
                    LOGGER.error("Error submitting new statsProcessorCallabledInstance");
                    return new UnknownTaskErrorResponse(e);
                }

                boolean finishedWithErrors = false;
                for (Future<TaskResponse> fut : list) {
                    try {
                        TaskResponse result = fut.get();
                        
                        if ((result.getCode() == TaskResponseEnum.MONGO_MAPREDUCE_ERROR
                                .getCode())
                                || (result.getCode() == TaskResponseEnum.UNKNOWN_TASK_ERROR
                                        .getCode())) {
                            addErrorAsPendingJob(result);
                            mailSender.sendEmail("diego.m@wideo.co",
                                    "diego.m@wideo.co",
                                    "Fallo el mapreduce de wideo-stats",
                                    ((ErrorTaskResponse) result).getData()
                                            .toString());
                            LOGGER.error("Ocurrio un error realizando el mapreduce para la key "
                                    + processingKeyName);
                            finishedWithErrors = true;
                        }
                    }
                    catch (Exception e) {
                        return new UnknownTaskErrorResponse(
                                "An exception occured while getting the future task response. "
                                        + "Please verify if the map-reduce job was processed or "
                                        + "if it is the pending job collection");
                    }

                }
                lap = System.currentTimeMillis() - lap;
                LOGGER.debug("Petition finished in " + lap + " msecs.");
                if (finishedWithErrors) {
                    return new WarningTaskExecutionResponse();
                }
                return new SuccessfulTaskExecutionResponse();
            }
        }
        catch (InvalidProcessingKeyException e) {
            e.printStackTrace();
            return new ErrorTaskResponse(e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new UnknownTaskErrorResponse(
                    "Error occured while preparing petition to run");
        }
    }
    
    private void addErrorAsPendingJob(TaskResponse result) {
        final ErrorTaskResponse response = (ErrorTaskResponse) result;
        try {
            DateFormat sdf = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy",
                    Locale.ENGLISH);
            JSONObject resultData = new JSONObject((String) response.getData());
            String processingKeyName = resultData
                    .getString("processingKeyName");
            JSONObject dates = (JSONObject) resultData.get("dateRange");
            Date startDate = sdf.parse((String) dates.get("$gte"));
            Date toDate = sdf.parse((String) dates.get("$lt"));
            PendingJobKey newPendingJob = new PendingJobKey(processingKeyName,
                    startDate, toDate);
            statsPendingJobsBO.addPendingJob(newPendingJob);
        }
        catch (Exception e) {
            LOGGER.error("Ocurrio un error intentando agregar el job pendiente "
                    + response.getData());
        }
    }
}
