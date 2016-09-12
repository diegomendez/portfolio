//package com.wideo.metrics.controllers;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Required;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.mongodb.Mongo;
//import com.wideo.metrics.bo.processor.StatsThreadJobsManager;
//import com.wideo.metrics.mail.MailSender;
//
//@Controller
//public class StatsProcessorController extends AbstractController {
//
//    Mongo mongo;
//
//    @Autowired
//    MailSender mailSender;
//    
//    private Logger LOGGER = Logger.getLogger(StatsProcessorController.class);
//
//    @RequestMapping(value = "/stats", method = RequestMethod.POST)
//    public String doFirstProcess(HttpServletRequest request,
//            HttpServletResponse response, Model model,
//            @RequestParam(value = "petition", required = true) String petition) {
//
//        StatsThreadJobsManager statsThreadJobsManager = new StatsThreadJobsManager(
//                mongo);
//
//        return "wideoMetricsJsonView";
//    }
//
//    @RequestMapping(value = "/stats/processPetition", method = RequestMethod.GET)
//    public void testStats(HttpServletRequest request,
//            HttpServletResponse respone, Model model,
//            @RequestParam(value = "petition", required = true) String petitionKey) {
//        
////        statsThreadJobsManager.firstProcessing(petitionKey);
//        mailSender.sendEmail("diego.m@wideo.co", "diego.m@wideo.co", "testing mail", "anduvo puto");
//        System.out.println("finished");
//    }
//    
//    @Required
//    public void setMongo(Mongo mongo) {
//        this.mongo = mongo;
//    }
//
//}
