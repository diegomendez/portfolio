package com.wideo.metrics.bo.processor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;

import com.github.jmkgreen.morphia.MapreduceResults.Stats;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;
import com.wideo.metrics.bo.processor.key.StatsProcessingKeysBO;
import com.wideo.metrics.bo.processor.task.response.TaskResponse;
import com.wideo.metrics.bo.processor.task.response.UnknownTaskErrorResponse;
import com.wideo.metrics.constants.WideoMetricsCoreConstants;
import com.wideo.metrics.model.processor.key.ProcessingKey;
import com.wideo.metrics.persistence.dal.StatsCollectorDalImpl;

public class StatsProcessorCallableImpl implements StatsProcessorCallable,
        Callable<TaskResponse> {

    private Mongo mongo;
    private DBObject mapReduceQuery;
    private ProcessingKey processingKey;
    private StatsFilterProcessorBO statsFilterProcessorBO;
    
    private Logger LOGGER = Logger.getLogger(StatsProcessorCallableImpl.class);

    public StatsProcessorCallableImpl(Mongo mongo, DBObject mapReduceQuery,
            ProcessingKey processingKey, StatsFilterProcessorBO statsFilterProcessorBO) {
        this.mongo = mongo;
        this.mapReduceQuery = mapReduceQuery;
        this.processingKey = processingKey;
        this.statsFilterProcessorBO = statsFilterProcessorBO;
    }

    @Async
    public TaskResponse collectAndProcess() {
        try {
            Class<?> taskProcessorClass = Class.forName(processingKey
                    .getProcessingClass());
            System.out.println(taskProcessorClass.getName());
            Object classInstance = taskProcessorClass
                    .getConstructor(DBCollection.class, StatsFilterProcessorBOImpl.class)
                    .newInstance(
                            mongo.getDB(
                                    WideoMetricsCoreConstants.WIDEOMETRICS_SNAPSHOT_DB_NAME)
                                    .getCollection(
                                            processingKey
                                                    .getProcessingCollection()), statsFilterProcessorBO);

            Method processTaskMethod = taskProcessorClass.getMethod(
                    WideoMetricsCoreConstants.PROCESS_TASKS_METHOD_NAME,
                    ProcessingKey.class, DBObject.class, String.class);

            TaskResponse methodResponse = (TaskResponse) processTaskMethod
                    .invoke(classInstance, processingKey, mapReduceQuery,
                            processingKey.getProcessingAction());

            return methodResponse;
        }
        catch (ClassNotFoundException e) {
            LOGGER.error("Ocurrio un error intentando crear la clase para el nombre "
                    + processingKey.getProcessingClass());
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            LOGGER.error("Ocurrio un error accediendo a la clase del tipo "
                    + processingKey.getProcessingClass());
        }
        catch (SecurityException e) {
            LOGGER.error(
                    "Ocurrio un error intentando la reflexion de la clase para procesar",
                    e);
        }
        catch (NoSuchMethodException e) {
            LOGGER.error(
                    "No existe el metodo process(String, DBObject, String) en la clase instanciada",
                    e);
        }
        catch (IllegalArgumentException e) {
            LOGGER.error("Error en los argumentos enviados al metodo process");
        }
        catch (InvocationTargetException e) {
            LOGGER.error("Error en la invocacion del target", e);
        }
        catch (InstantiationException e) {
            LOGGER.error("Error instanciando la clase");
            e.printStackTrace();
        }
        return null;
    }

    public TaskResponse call() throws Exception {
        return collectAndProcess();
    }

}
