package com.wideo.metrics.controllers;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.wideo.metrics.model.event.GenericEvent;
import com.wideo.metrics.model.event.GenericStatsEvent;
import com.wideo.metrics.services.tracker.EventTrackerService;
import com.wideo.metrics.utils.HttpUtils;

/**
 * @author diego.mendez
 */

@Controller
public class EventTrackerController extends AbstractController {

    @Autowired
    EventTrackerService eventTrackerService;

    @Value("${api.wideo.endpoint}")
    String wideoApiEndpoint;

    ExecutorService executor = Executors.newFixedThreadPool(4);

    private final Logger LOGGER = Logger
            .getLogger(EventTrackerController.class);

    @RequestMapping(value = "/alive", method = RequestMethod.GET)
    public String test(HttpServletRequest request,
            HttpServletResponse response, Model model) {
        model.addAttribute("data", "wideo-metrics it's alive.");
        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/trackDirectLinkInteraction", method = RequestMethod.GET)
    public RedirectView trackInteraction(HttpServletRequest request,
            Model model, HttpServletResponse response,
            @RequestParam(value = "event", required = true) String eventObject,
            @RequestParam(value = "href", required = true) String target) {
        try {
            GenericEvent trackingEvent = new GenericEvent(eventObject);
            trackingEvent.setUserAgentInfo(getUserAgentInfo(request));
            eventTrackerService.trackEvent(trackingEvent);
            LOGGER.debug("Evento de interactividad logueado: "
                    + trackingEvent.toString());
            RedirectView redirectView = new RedirectView(target);
            return redirectView;
        }
        catch (Exception e) {
            LOGGER.error(
                    "Ocurrio un error logueando el evento de interactividad "
                            + eventObject, e);
            RedirectView redirectView = new RedirectView(target);
            return redirectView;
        }
    }

    @RequestMapping(value = "/trackJS", method = RequestMethod.POST)
    public String trackJavascriptEvent(Model model, HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(value = "event", required = true) String eventObject) {
        try {
            GenericEvent trackingEvent = new GenericEvent(eventObject);

            trackingEvent.setUserAgentInfo(getUserAgentInfo(request));

            eventTrackerService.trackEvent(trackingEvent);
            model.addAttribute("data", "Evento " + trackingEvent.getAction()
                    + " almacenado");
            LOGGER.debug("Evento trackeado desde libereria JS: "
                    + trackingEvent.toString());
        }
        catch (Exception e) {
            LOGGER.error("Ocurrio un error almancenando el evento "
                    + eventObject, e);
            model.addAttribute("data", "Evento " + eventObject
                    + " no pudo ser almacenado");
        }

        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/trackAPI", method = RequestMethod.POST)
    public String trackBackendEvent(Model model, HttpServletRequest request,
            HttpServletResponse response) {
        String eventObject = request.getHeader("event");
        try {
            GenericEvent trackingEvent = new GenericEvent(eventObject);

            eventTrackerService.trackEvent(trackingEvent);
            model.addAttribute("data", "Evento " + trackingEvent.getAction()
                    + " almacenado");
            LOGGER.debug("Evento desde API logueado: "
                    + trackingEvent.toString());
        }
        catch (Exception e) {
            LOGGER.error("Ocurrio un error almancenando el evento "
                    + eventObject, e);
            model.addAttribute("data", "Evento " + eventObject
                    + " no pudo ser almacenado");
        }
        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/trackFormCompleted", method = RequestMethod.POST)
    public String trackJFormCompletedEvent(Model model,
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "event", required = true) String eventObject) {
        try {
            GenericEvent trackingEvent = new GenericEvent(eventObject);

            trackingEvent.setUserAgentInfo(getUserAgentInfo(request));

            eventTrackerService.trackEvent(trackingEvent);

            // Preparo params para el llamado a wideoo-api
            JSONObject params = new JSONObject();
            params.put("user_id", trackingEvent.get("user_id"));

            params.put("wideo_id", trackingEvent.get("wideo_id"));
            BasicDBObject properties = (BasicDBObject) trackingEvent
                    .get("properties");
            params.put("destinationEmail", properties.get("destinationEmail"));
            BasicDBList d = (BasicDBList) properties.get("fields");
            Map<String, Object> m = d.toMap();
            params.put("fields", m);
            final JSONObject mailParams = params;
            LOGGER.info("Calling develepe mail enqueue method");
            executor.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    final String params = "params=" + mailParams.toString();
                    LOGGER.info("Calling mail enqueue for event: "
                            + params);
                    String responseObj = HttpUtils.urlPost(wideoApiEndpoint
                            + "/wco/mail/enqueue/form", params, "");
                    LOGGER.info(responseObj);
                    return "ok";
                }
            });

            model.addAttribute("data", "Evento " + trackingEvent.getAction()
                    + " almacenado");
            LOGGER.debug("Evento trackeado desde libereria JS: "
                    + trackingEvent.toString());
        }
        catch (Exception e) {
            LOGGER.error("Ocurrio un error almancenando el evento "
                    + eventObject, e);
        }

        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/trackWideoStats", method = RequestMethod.POST)
    public String trackWideoStatsEvent(Model model, HttpServletRequest request,
            HttpServletResponse response) {
        try {
            String eventObject = request.getHeader("event");
            LOGGER.info("Tracking object: " + eventObject);
            GenericEvent trackingEvent = new GenericEvent(eventObject);

            eventTrackerService.trackWideoStats(trackingEvent);
            model.addAttribute("data", "Evento " + trackingEvent.getAction()
                    + " almacenado");
            LOGGER.debug("Evento desde API logueado: "
                    + trackingEvent.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Ocurrio un error almancenando el evento", e);
        }
        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/trackSocialStats", method = RequestMethod.POST)
    public String trackSocialStatsEvent(Model model,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            LOGGER.info("Comienzo de tracking de social stats");
            String eventObject = request.getHeader("event");
            GenericStatsEvent trackingEvent = new GenericStatsEvent(eventObject);

            eventTrackerService.trackSocialStatEvent(trackingEvent);
            model.addAttribute("data", "Evento " + trackingEvent.getAction()
                    + " almacenado");
            LOGGER.info("Evento de Social Stats desde API logueado: "
                    + trackingEvent.toString());
        }
        catch (Exception e) {
            LOGGER.error("Ocurrio un error almancenando el evento", e);
        }
        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/trackNewSocialStats", method = RequestMethod.POST)
    public String trackNewSocialStatsEvent(Model model,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            LOGGER.info("Comienzo de tracking de new-social stats");
            String eventObject = request.getHeader("event");
            GenericStatsEvent trackingEvent = new GenericStatsEvent(eventObject);

            eventTrackerService.trackNewSocialStatEvent(trackingEvent);
            model.addAttribute("data", "Evento " + trackingEvent.getAction()
                    + " almacenado");
            LOGGER.info("Evento de Social Stats desde API logueado: "
                    + trackingEvent.toString());
        }
        catch (Exception e) {
            LOGGER.error("Ocurrio un error almancenando el evento", e);
        }
        return "wideoMetricsJsonView";
    }
    
}
