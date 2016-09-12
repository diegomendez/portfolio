package com.wideo.metrics.processor.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.wideo.metrics.bo.processor.StatsThreadJobsManager;
import com.wideo.metrics.bo.processor.key.StatsProcessingKeysBO;
import com.wideo.metrics.bo.processor.task.response.DateNotLivedException;
import com.wideo.metrics.bo.processor.task.response.TaskResponse;
import com.wideo.metrics.model.processor.key.ProcessingKey;

@Controller
@ComponentScan("com.wideo.metrics")
public class StatsProcessorController {

    private static final Logger LOGGER = Logger
            .getLogger(StatsProcessorController.class);

    @Autowired
    StatsThreadJobsManager statsThreadJobsManager;

    @Autowired
    StatsProcessingKeysBO statsProcessingKeysBO;

    @RequestMapping(value = "/stats/processPetition", method = RequestMethod.GET)
    public String processStat(HttpServletRequest request,
            HttpServletResponse respone, Model model,
            @RequestParam(value = "key", required = true) String petitionKey) {

        TaskResponse response = statsThreadJobsManager
                .performPetition(petitionKey);

        model.addAttribute("data", response);

        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/stats/process/all", method = RequestMethod.GET)
    public String processStat(HttpServletRequest request,
            HttpServletResponse respone, Model model) {

        List<ProcessingKey> processingKeys = statsProcessingKeysBO
                .getAllProcessingKeys();

        for (ProcessingKey processingKey : processingKeys) {
            LOGGER.info("Procesando key: " + processingKey.toString());
            statsThreadJobsManager.performPetition(processingKey.getName());
        }

        model.addAttribute("data", "finished. Check logger for errors");

        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/stats/process/all/lifetime", method = RequestMethod.GET)
    public String processStatsLifetime(HttpServletRequest request,
            HttpServletResponse respone, Model model) {

        List<ProcessingKey> processingKeys = statsProcessingKeysBO
                .getAllProcessingKeys();

        DateTime toDate = new DateTime(2015, 8, 10, 00, 00, 00, 00);
        
        while (toDate.isBeforeNow()) {
            for (ProcessingKey processingKey : processingKeys) {
                LOGGER.info("Procesando key: " + processingKey.toString());
                statsThreadJobsManager.performPetition(processingKey.getName());
            }
            toDate = toDate.plusDays(1);
        }
        model.addAttribute("data", "finished. Check logger for errors");

        return "wideoMetricsJsonView";
    }
}
