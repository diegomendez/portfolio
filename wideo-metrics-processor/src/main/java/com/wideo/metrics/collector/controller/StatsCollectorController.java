package com.wideo.metrics.collector.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.wideo.metrics.bo.collector.StatsCollectorBO;
import com.wideo.metrics.bo.collector.writer.ExcelWriter;
import com.wideo.metrics.bo.processor.StatsThreadJobsManager;
import com.wideo.metrics.bo.processor.task.refactor.TotalPlaysByUserByDayTask_2;
import com.wideo.metrics.bo.processor.task.refactor.TotalViewsByUserByDayTask_2;
import com.wideo.metrics.events.EventNamespaceTypesEnum;
import com.wideo.metrics.events.WideoEventTypesEnum;
import com.wideo.metrics.mail.MailSender;
import com.wideo.metrics.model.collector.ActionStats;
import com.wideo.metrics.model.collector.FormsStats;
import com.wideo.metrics.model.collector.InteractionsStats;
import com.wideo.metrics.model.collector.InteractionsSummaryStats;
import com.wideo.metrics.model.collector.StatsResults;
import com.wideo.metrics.model.collector.UserStatsResults;
import com.wideo.metrics.model.collector.ValuesStats;
import com.wideo.metrics.model.collector.WideoStatsResults;
import com.wideo.metrics.model.collector.WideosListStatsResults;
import com.wideo.metrics.model.collector.writer.csv.CSVField;
import com.wideo.metrics.model.comparator.StatsComp;
import com.wideo.metrics.utils.HttpUtils;

@Controller
@ComponentScan("com.wideo.metrics")
public class StatsCollectorController {

    private static Logger LOGGER = Logger
            .getLogger(StatsCollectorController.class);

    @Autowired
    StatsCollectorBO statsCollectorBO;

    @Autowired
    MailSender mailSender;

    @Value("${api.wideo.endpoint}")
    String wideoApiEndpoint;

    @RequestMapping(value = "/stats/alive", method = RequestMethod.GET)
    public String isAlive(HttpServletRequest request,
            HttpServletResponse respone, Model model) {
        model.addAttribute("data", "stats it's alive");
        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/stats/2/collect/wideos/{wideoID}", method = RequestMethod.GET)
    public String testingQueries(
            HttpServletRequest request,
            HttpServletResponse respone,
            Model model,
            @PathVariable(value = "wideoID") String wideoID,
            @RequestParam(value = "start", required = true) String startDate,
            @RequestParam(value = "end", required = true) String endDate,
            @RequestParam(value = "filterByProperties", required = false, defaultValue = "true") String filterByProperties) {

        try {

            JSONObject queryParams = new JSONObject();
            queryParams.accumulate("wideo_id", wideoID);
            queryParams.accumulate("start_date", startDate);
            queryParams.accumulate("end_date", endDate);
            queryParams.accumulate("filterByProperties", filterByProperties);
            model.addAttribute("stats",
                    statsCollectorBO.collectWideoStats(queryParams));
        }
        catch (Exception e) {
            LOGGER.error(
                    "Ocurrio un error procesando las estadisticas para el wideo: "
                            + wideoID + "en las fechas " + startDate + "-"
                            + endDate, e);
        }
        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/stats/2/collect/users/{userID}", method = RequestMethod.GET)
    public String testingUserQueries(
            HttpServletRequest request,
            HttpServletResponse respone,
            Model model,
            @PathVariable(value = "userID") Long userID,
            @RequestParam(value = "start", required = true) String startDate,
            @RequestParam(value = "end", required = true) String endDate,
            @RequestParam(value = "userWideos", required = false) JSONArray userWideos,
            @RequestParam(value = "filterByProperties", required = false, defaultValue = "true") String filterByProperties) {
        try {

            JSONObject queryParams = new JSONObject();
            queryParams.accumulate("user_id", userID);
            queryParams.accumulate("start_date", startDate);
            queryParams.accumulate("end_date", endDate);
            queryParams.accumulate("filterByProperties", filterByProperties);

            Map<String, Object> userStats = (Map<String, Object>) statsCollectorBO
                    .collectUserStats(queryParams);

            if (userWideos != null) {
                LOGGER.info("Collecting stats for Wideos " + userWideos);
                Map<String, Object> wideosStats = new HashMap<String, Object>();
                for (int i = 0; i < userWideos.length(); i++) {
                    String wideoID = userWideos.optString(i);
                    if ((wideoID != null) && (!wideoID.isEmpty())) {
                        JSONObject wideoParams = new JSONObject();
                        wideoParams.accumulate("wideo_id", wideoID);
                        wideoParams.accumulate("start_date", startDate);
                        wideoParams.accumulate("end_date", endDate);

                        Map<String, Object> wideoStats = (Map<String, Object>) statsCollectorBO
                                .collectWideoStats(wideoParams);
                        if (wideoStats != null) {
                            wideosStats.put(wideoID, wideoStats);
                        }
                    }
                }
                userStats.put("wideos", wideosStats);
            }
            model.addAttribute("stats", userStats);
        }
        catch (Exception e) {
            LOGGER.error(
                    "Ocurrio un error procesando las estadisticas para el usuario: "
                            + userID + "en las fechas " + startDate + "-"
                            + endDate, e);
        }
        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/stats/collect/users/{userID}/sort", method = RequestMethod.GET)
    public String collectSortedByKey(
            Model model,
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable(value = "userID") Long userID,
            @RequestParam(value = "start", required = true) String startDate,
            @RequestParam(value = "end", required = true) String endDate,
            @RequestParam(value = "sortKey", required = true) String sortKey,
            @RequestParam(value = "sortType", required = false, defaultValue = "desc") String sortType,
            @RequestParam(value = "wideos", required = false) JSONArray wideosList,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "amount", required = false, defaultValue = "5") Integer pageAmount,
            @RequestParam(value = "filterByProperties", required = false, defaultValue = "true") String filterByProperties) {
        try {
            JSONObject userQueryParams = new JSONObject();
            userQueryParams.accumulate("user_id", userID);
            userQueryParams.accumulate("start_date", startDate);
            userQueryParams.accumulate("end_date", endDate);
            userQueryParams
                    .accumulate("filterByProperties", filterByProperties);

            Map<String, Object> userStats = (Map<String, Object>) statsCollectorBO
                    .collectUserStats(userQueryParams);

// String getResponse = HttpUtils.urlGet(wideoApiEndpoint
// + "/wco/users/" + userID + "/wideos");
// JSONObject jsonResponse = new JSONObject(getResponse);
// JSONObject o = jsonResponse.getJSONObject("data");
// wideosList = o.getJSONArray("wideos");

            JSONObject queryParams = new JSONObject();
            queryParams.accumulate("start_date", startDate);
            queryParams.accumulate("end_date", endDate);

            Map<String, Map<String, Object>> wideosStats = new HashMap<String, Map<String, Object>>();

            for (int i = 0; i < wideosList.length(); i++) {
                String wideoID = wideosList.getString(i);
                queryParams.put("wideo_id", wideoID);
                Map<String, Object> stats = (Map<String, Object>) statsCollectorBO
                        .collectWideoStats(queryParams);
                if (stats != null) {
                    wideosStats.put(wideoID, stats);
                }
            }

            StatsComp comparator = new StatsComp(sortKey, sortType, wideosStats);
            TreeMap<String, Map<String, Object>> wideosStatsOrdered = new TreeMap<String, Map<String, Object>>(
                    comparator);
            wideosStatsOrdered.putAll(wideosStats);

            int beginLimit = 0;
            int endLimit = wideosList.length();
            if (page != null) {
                beginLimit = (page - 1) * pageAmount;
                endLimit = page * pageAmount;
            }
            int i = 0;

            Iterator<String> it = wideosStatsOrdered.keySet().iterator();
            while (it.hasNext()) {
                String entryKey = it.next();
                if ((i < beginLimit) || (i >= endLimit)) {
                    it.remove();
                }
                i++;
            }

            userStats.put("wideos",
                    new LinkedHashMap<String, Map<String, Object>>(
                            wideosStatsOrdered));

            model.addAttribute("stats", userStats);
        }
        catch (Exception e) {
            LOGGER.error("Ocurrio un error", e);
            model.addAttribute("errorProcessing", true);
        }
        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/stats/collect/wideos", method = RequestMethod.GET)
    public String collectWideosStats(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            @RequestParam(value = "start", required = true) String startDate,
            @RequestParam(value = "end", required = true) String endDate,
            @RequestParam(value = "userWideos", required = true) JSONArray userWideos) {

        try {
            Map<String, Object> wideosStats = new HashMap<String, Object>();
            for (int i = 0; i < userWideos.length(); i++) {
                String wideoID = userWideos.optString(i);
                if ((wideoID != null) && (!wideoID.isEmpty())) {
                    JSONObject wideoParams = new JSONObject();
                    wideoParams.accumulate("wideo_id", wideoID);
                    wideoParams.accumulate("start_date", startDate);
                    wideoParams.accumulate("end_date", endDate);

                    Map<String, Object> wideoStats = (Map<String, Object>) statsCollectorBO
                            .collectWideoStats(wideoParams);
                    if (wideoStats != null) {
                        wideosStats.put(wideoID, wideoStats);
                    }
                }
            }
            model.addAttribute("stats", wideosStats);
            return "wideoMetricsJsonView";
        }
        catch (Exception e) {
            return null;
        }
    }

    @RequestMapping(value = "/stats/collect/user/{userID}/summary", method = RequestMethod.GET)
    public String collectAllStatsForWideo(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            @PathVariable(value = "userID") Long userID,
            @RequestParam(value = "start", required = false) String startDate,
            @RequestParam(value = "end", required = false) String endDate,
            @RequestParam(value = "byDate", required = false, defaultValue = "false") Boolean byDate,
            @RequestParam(value = "userWideos", required = false) JSONArray userWideos) {

        LOGGER.info("Collecting stats for user: " + userID);
        UserStatsResults userStatsResultResponse = new UserStatsResults(userID);

        List<String> groupFilter = new ArrayList<String>();
        if (byDate) {
            groupFilter.add("date");
        }

        JSONObject queryParams = new JSONObject();

        queryParams = prepareUserQueryParams(
                EventNamespaceTypesEnum.player.getName(), userID,
                WideoEventTypesEnum.WIDEO_VIEW.getName(), startDate, endDate,
                groupFilter);

        ActionStats viewsStats = statsCollectorBO
                .collectStatsForUser(queryParams);

        if (viewsStats.getTotal() != null) {

            queryParams = prepareUserQueryParams(
                    EventNamespaceTypesEnum.player.getName(), userID,
                    WideoEventTypesEnum.WIDEO_PLAY.getName(), startDate,
                    endDate, groupFilter);

            ActionStats playsStats = statsCollectorBO
                    .collectStatsForUser(queryParams);

            queryParams = prepareUserQueryParams(
                    EventNamespaceTypesEnum.interactivity.getName(), userID,
                    WideoEventTypesEnum.OBJECT_CLICK.getName(), startDate,
                    endDate, groupFilter);

            ActionStats interactionsClickedStats = statsCollectorBO
                    .collectStatsForUser(queryParams);

            if (interactionsClickedStats.getTotal() == null) {
                interactionsClickedStats.setTotal(0.0);
            }

            queryParams = prepareUserQueryParams(
                    EventNamespaceTypesEnum.interactivity.getName(), userID,
                    WideoEventTypesEnum.FORM_SENT.getName(), startDate,
                    endDate, groupFilter);

            ActionStats formSentStats = statsCollectorBO
                    .collectStatsForUser(queryParams);

            if (formSentStats.getTotal() != null) {
                interactionsClickedStats.sumTotal(formSentStats.getTotal());
            }

            queryParams = prepareUserQueryParams(
                    EventNamespaceTypesEnum.player.getName(), userID,
                    WideoEventTypesEnum.SECONDS_WATCHED.getName(), startDate,
                    endDate, groupFilter);

            ActionStats secondsWatchedStats = statsCollectorBO
                    .collectStatsForUser(queryParams);

            userStatsResultResponse.addStatsResult("views", viewsStats);
            userStatsResultResponse.addStatsResult("plays", playsStats);
            userStatsResultResponse.addStatsResult("secondsWatched",
                    secondsWatchedStats);
            userStatsResultResponse.addStatsResult("interactions",
                    interactionsClickedStats);

            if (userWideos != null) {
                LOGGER.info("Collecting stats for Wideos " + userWideos);
                WideosListStatsResults wideosListStats = new WideosListStatsResults();
                for (int i = 0; i < userWideos.length(); i++) {
                    String wideoID = userWideos.optString(i);
                    if ((wideoID != null) && (!wideoID.isEmpty())) {
                        WideoStatsResults wideoSummaryStats = collectSummaryStatsForWideo(
                                wideoID, startDate, endDate, byDate, null);
                        LOGGER.info("Stats for wideo: "
                                + wideoSummaryStats.getActionStats().toString());
                        if (wideoSummaryStats.getActionStats().size() > 0) {
                            wideosListStats.addStatsResult(wideoID,
                                    wideoSummaryStats);
                        }
                    }
                }
                userStatsResultResponse.addStatsResult("wideos",
                        wideosListStats);

            }

        }

        model.addAttribute("stats", userStatsResultResponse);
        LOGGER.info("Stats a devolver: " + userStatsResultResponse.toString());
        return "wideoMetricsJsonView";

    }

    public WideoStatsResults collectSummaryStatsForWideo(String wideoID,
            String startDate, String endDate, Boolean byDate,
            Integer wideoLength) {

        try {
            WideoStatsResults wideoStatsResults = new WideoStatsResults(wideoID);

            JSONObject queryParams = new JSONObject();

            List<String> dateGroupFilter = new ArrayList<String>();
            if (byDate) {
                dateGroupFilter.add("date");
            }

            queryParams = prepareWideoQueryParams(
                    EventNamespaceTypesEnum.player.getName(), wideoID,
                    WideoEventTypesEnum.WIDEO_VIEW.getName(), startDate,
                    endDate, dateGroupFilter);

            ActionStats viewsStats = statsCollectorBO
                    .collectStatsForWideo(queryParams);

            if (viewsStats.getTotal() != null) {
                wideoStatsResults.addStatsResult("views", viewsStats);

                queryParams = prepareWideoQueryParams(
                        EventNamespaceTypesEnum.player.getName(), wideoID,
                        WideoEventTypesEnum.WIDEO_PLAY.getName(), startDate,
                        endDate, dateGroupFilter);

                ActionStats playStats = statsCollectorBO
                        .collectStatsForWideo(queryParams);

                if (playStats.getTotal() == null) {
                    playStats.setTotal(0.0);
                }

                wideoStatsResults.addStatsResult("plays", playStats);

                try {
                    Double playRatePct = null;
                    if (viewsStats.getTotal() > 0) {
                        Double totalViews = viewsStats.getTotal();
                        Double totalPlays = playStats.getTotal();
                        Double playRateValue = totalPlays / totalViews;
                        playRatePct = playRateValue * 100;
                    }
                    else {
                        playRatePct = 0.0;
                    }
                    ActionStats playRate = new ActionStats("PLAY_RATE");
                    playRate.setTotal(playRatePct);
                    wideoStatsResults.addStatsResult("playRate", playRate);
                }
                catch (Exception e) {
                    LOGGER.error("Error calculando el playrate para el wideo "
                            + wideoID, e);
                }

                List<String> interactionIDGroupFilter = new ArrayList<String>();
                interactionIDGroupFilter.add("interaction_id");
                if (byDate) {
                    interactionIDGroupFilter.add("date");
                }

                queryParams = prepareWideoQueryParams(
                        EventNamespaceTypesEnum.interactivity.getName(),
                        wideoID, WideoEventTypesEnum.OBJECT_CLICK.getName(),
                        startDate, endDate, interactionIDGroupFilter);

                InteractionsStats interactionsClikedStats = (InteractionsStats) statsCollectorBO
                        .collectStatsForWideoForInteractions(queryParams);

                if (interactionsClikedStats.getTotal() == null) {
                    interactionsClikedStats.setTotal(0.0);
                }

                queryParams = prepareWideoQueryParams(
                        EventNamespaceTypesEnum.interactivity.getName(),
                        wideoID, WideoEventTypesEnum.FORM_SENT.getName(),
                        startDate, endDate, interactionIDGroupFilter);

                InteractionsStats interactionsFormSentStats = (InteractionsStats) statsCollectorBO
                        .collectStatsForWideoForInteractions(queryParams);

                if (interactionsFormSentStats.getTotal() == null) {
                    interactionsFormSentStats.setTotal(0.0);
                }
                interactionsClikedStats.sumTotal(interactionsFormSentStats
                        .getTotal());
                wideoStatsResults.addStatsResult("interactionsClicked",
                        interactionsClikedStats);

                queryParams = prepareWideoQueryParams(
                        EventNamespaceTypesEnum.player.getName(), wideoID,
                        WideoEventTypesEnum.SECONDS_WATCHED.getName(),
                        startDate, endDate, dateGroupFilter);

                ActionStats secondsWatchedStats = statsCollectorBO
                        .collectStatsForWideo(queryParams);

                wideoStatsResults.addStatsResult("secondsWatched",
                        secondsWatchedStats);

                try {
                    if (wideoLength != null) {
                        Double totalSecondsWatched = secondsWatchedStats
                                .getTotal();
                        Double totalPlays = playStats.getTotal();
                        Double totalAvailableSeconds = totalPlays * wideoLength;
                        Double engagementValuePct = 0.0;
                        if (totalSecondsWatched >= totalAvailableSeconds) {
                            engagementValuePct = 100.0;
                        }
                        else {
                            Double engagementValue = totalSecondsWatched
                                    / totalAvailableSeconds;
                            engagementValuePct = engagementValue * 100;
                        }
                        ActionStats engagementStats = new ActionStats(
                                "ENGAGEMENT");
                        engagementStats.setTotal(engagementValuePct);
                        wideoStatsResults.addStatsResult("engagementRate",
                                engagementStats);
                    }

                }
                catch (Exception e) {
                    LOGGER.error(
                            "Error calculando el engagement para el wideo "
                                    + wideoID, e);
                }
            }
            return wideoStatsResults;
        }
        catch (Exception e) {
            LOGGER.error("Ocurrio un error instanciando keyParams");
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/stats/collect/wideos/{wideoID}/all", method = RequestMethod.GET)
    public String collectAllStatsForWideo(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            @PathVariable(value = "wideoID") String wideoID,
            @RequestParam(value = "start", required = false) String startDate,
            @RequestParam(value = "end", required = false) String endDate,
            @RequestParam(value = "byDate", required = false, defaultValue = "false") Boolean byDate,
            @RequestParam(value = "wideoLength", required = false) Integer wideoLength) {

        try {
            WideoStatsResults wideoStatsResults = new WideoStatsResults(wideoID);

            JSONObject queryParams = new JSONObject();

            List<String> dateGroupFilter = new ArrayList<String>();
            if (byDate) {
                dateGroupFilter.add("date");
            }

            queryParams = prepareWideoQueryParams(
                    EventNamespaceTypesEnum.player.getName(), wideoID,
                    WideoEventTypesEnum.WIDEO_VIEW.getName(), startDate,
                    endDate, dateGroupFilter);

            ActionStats viewsStats = statsCollectorBO
                    .collectStatsForWideo(queryParams);

            if (viewsStats.getTotal() != null) {
                wideoStatsResults.addStatsResult("views", viewsStats);

                queryParams = prepareWideoQueryParams(
                        EventNamespaceTypesEnum.player.getName(), wideoID,
                        WideoEventTypesEnum.WIDEO_PLAY.getName(), startDate,
                        endDate, dateGroupFilter);

                ActionStats playStats = statsCollectorBO
                        .collectStatsForWideo(queryParams);

                wideoStatsResults.addStatsResult("plays", playStats);

                try {
                    Double playRatePct = null;
                    if (viewsStats.getTotal() > 0) {
                        Double totalViews = viewsStats.getTotal();
                        Double totalPlays = playStats.getTotal();
                        Double playRateValue = totalPlays / totalViews;
                        playRatePct = playRateValue * 100;
                    }
                    else {
                        playRatePct = 0.0;
                    }
                    ActionStats playRate = new ActionStats("PLAY_RATE");
                    playRate.setTotal(playRatePct);
                    wideoStatsResults.addStatsResult("playRate", playRate);
                }
                catch (Exception e) {
                    LOGGER.error("Error calculando el playrate para el wideo "
                            + wideoID, e);
                }

                List<String> interactionIDGroupFilter = new ArrayList<String>();
                interactionIDGroupFilter.add("interaction_id");
                if (byDate) {
                    interactionIDGroupFilter.add("date");
                }

                queryParams = prepareWideoQueryParams(
                        EventNamespaceTypesEnum.interactivity.getName(),
                        wideoID,
                        WideoEventTypesEnum.INTERACTION_SHOWN.getName(),
                        startDate, endDate, interactionIDGroupFilter);

                InteractionsStats interactionsShownStats = (InteractionsStats) statsCollectorBO
                        .collectStatsForWideoForInteractions(queryParams);

// wideoStatsResults.addStatsResult("interactionsShown",
// interactionsShownStats);

                queryParams = prepareWideoQueryParams(
                        EventNamespaceTypesEnum.interactivity.getName(),
                        wideoID, WideoEventTypesEnum.OBJECT_CLICK.getName(),
                        startDate, endDate, interactionIDGroupFilter);

                InteractionsStats interactionsClikedStats = (InteractionsStats) statsCollectorBO
                        .collectStatsForWideoForInteractions(queryParams);

// wideoStatsResults.addStatsResult("interactionsClicked",
// interactionsClikedStats);

                queryParams = prepareWideoQueryParams(
                        EventNamespaceTypesEnum.interactivity.getName(),
                        wideoID, WideoEventTypesEnum.FORM_SENT.getName(),
                        startDate, endDate, interactionIDGroupFilter);

                InteractionsStats interactionsFormSentStats = (InteractionsStats) statsCollectorBO
                        .collectStatsForWideoForInteractions(queryParams);

                InteractionsSummaryStats interactionsStats = parseInteractions(
                        interactionsShownStats, interactionsClikedStats,
                        interactionsFormSentStats);

                wideoStatsResults.addStatsResult("interactions",
                        interactionsStats);

                queryParams = prepareWideoQueryParams(
                        EventNamespaceTypesEnum.player.getName(), wideoID,
                        WideoEventTypesEnum.SECONDS_WATCHED.getName(),
                        startDate, endDate, dateGroupFilter);

                ActionStats secondsWatchedStats = statsCollectorBO
                        .collectStatsForWideo(queryParams);

                wideoStatsResults.addStatsResult("secondsWatched",
                        secondsWatchedStats);

                try {
                    if (wideoLength != null) {
                        Double totalSecondsWatched = secondsWatchedStats
                                .getTotal();
                        Double totalAvailableSeconds = wideoLength
                                * playStats.getTotal();
                        Double engagementValuePct = 0.0;
                        if (totalSecondsWatched >= totalAvailableSeconds) {
                            engagementValuePct = 100.0;
                        }
                        else {
                            Double engagementValue = totalSecondsWatched
                                    / totalAvailableSeconds;
                            engagementValuePct = engagementValue * 100;
                        }
                        ActionStats engagementStats = new ActionStats(
                                "ENGAGEMENT");
                        engagementStats.setTotal(engagementValuePct);
                        wideoStatsResults.addStatsResult("engagementRate",
                                engagementStats);
                    }
                }
                catch (Exception e) {
                    LOGGER.error(
                            "Error calculando el engagement para el wideo "
                                    + wideoID, e);
                }

                List<String> dropOffGroupFilter = new ArrayList<String>();
                dropOffGroupFilter.add("playing_sec");

                queryParams = prepareWideoQueryParams(
                        EventNamespaceTypesEnum.player.getName(), wideoID,
                        WideoEventTypesEnum.WIDEO_PLAYING.getName(), startDate,
                        endDate, dropOffGroupFilter);

                ActionStats dropOffStats = statsCollectorBO
                        .collectWideoDropOff(queryParams);

                wideoStatsResults.addStatsResult("dropOff", dropOffStats);
            }
            model.addAttribute("stats", wideoStatsResults);
        }
        catch (Exception e) {
            LOGGER.error("Ocurrio un error instanciando keyParams");
            e.printStackTrace();
        }
        return "wideoMetricsJsonView";
    }

    private InteractionsSummaryStats parseInteractions(
            InteractionsStats interactionsShownStats,
            InteractionsStats interactionsClikedStats,
            InteractionsStats interactionsFormSentStats) {

        Double totalInteractionsShown = interactionsShownStats.getTotal();
        Double totalInteractionsClicked = interactionsClikedStats.getTotal();
        Double totalInteractionsFormSentStats = interactionsFormSentStats
                .getTotal();
        if (totalInteractionsClicked == null) {
            totalInteractionsClicked = 0.0;
        }
        if (totalInteractionsShown == null) {
            totalInteractionsShown = 0.0;
        }
        if (totalInteractionsFormSentStats == null) {
            totalInteractionsFormSentStats = 0.0;
        }
        Double totalCTR = 0.0;
        if (totalInteractionsShown > 0) {
            totalCTR = totalInteractionsClicked / totalInteractionsShown;
        }
        Double totalCTRPct = totalCTR * 100;

        InteractionsSummaryStats interactionsSummary = new InteractionsSummaryStats();
        interactionsSummary.setCtr(totalCTRPct);
        interactionsSummary.setTotalClicks(totalInteractionsClicked
                + totalInteractionsFormSentStats);
        interactionsSummary.setTotal(totalInteractionsShown);

        Map<String, StatsResults> shownStats = interactionsShownStats
                .getStats();
        Map<String, StatsResults> clickedStats = interactionsClikedStats
                .getStats();
        Map<String, StatsResults> sentStats = interactionsFormSentStats
                .getStats();
        for (String interactionID : shownStats.keySet()) {
            InteractionsSummaryStats summary = new InteractionsSummaryStats();

            ActionStats interactionShownStats = (ActionStats) shownStats
                    .get(interactionID);
            if (interactionShownStats != null) {
                double shownTotal = interactionShownStats.getTotal();
                summary.setTotal(shownTotal);

                summary.addStats("displays", interactionShownStats.getStats());

                ActionStats interactionClickedStats = (ActionStats) clickedStats
                        .get(interactionID);
                ActionStats actionedStats = null;

                if (interactionClickedStats != null) {
                    actionedStats = interactionClickedStats;
                }
                else {
                    ActionStats interactionFormSentStats = (ActionStats) sentStats
                            .get(interactionID);
                    if (interactionFormSentStats != null) {
                        actionedStats = interactionFormSentStats;
                    }
                }
                double CTRValue = 0;
                double clickTotal = 0;

                if (actionedStats != null) {
                    clickTotal = actionedStats.getTotal();
                    CTRValue = clickTotal / shownTotal;
                    summary.addStats("clicks", actionedStats.getStats());
                }
                Double CTRValuePct = CTRValue * 100;
                summary.setCtr(CTRValuePct);
                summary.setTotalClicks(clickTotal);
                summary.setTotal(shownTotal);

                interactionsSummary.addStats(interactionID, summary);
            }
        }

        return interactionsSummary;
    }

    /**
     * @param wideoID
     * @param startDate
     * @param endDate
     * @param byDate
     * @param queryParams
     * @return
     * @throws JSONException
     */
    private JSONObject prepareWideoQueryParams(String collection,
            String wideoID, String action, String startDate, String endDate,
            List<String> groupFilters) {
        try {
            JSONObject queryParams = new JSONObject();
            queryParams.accumulate("db_collection", collection);
            queryParams.accumulate("wideo", wideoID);
            queryParams.accumulate("action", action);

            if ((startDate != null) && (endDate != null)) {
                JSONObject dateObject = new JSONObject();
                dateObject.accumulate("start_date", startDate);
                dateObject.accumulate("end_date", endDate);
                queryParams.accumulate("date", dateObject);
            }

            if (groupFilters != null) {
                queryParams.accumulate("group_field", groupFilters);
            }

            return queryParams;
        }
        catch (Exception e) {
            LOGGER.error(
                    "Ocurrio un error preparando los parametros para la query",
                    e);
            return null;
        }
    }

    private JSONObject prepareUserQueryParams(String collection, Long userID,
            String action, String startDate, String endDate,
            List<String> groupFilters) {
        try {
            JSONObject queryParams = new JSONObject();
            queryParams.accumulate("db_collection", collection);
            queryParams.accumulate("user_id", userID);
            queryParams.accumulate("action", action);

            if ((startDate != null) && (endDate != null)) {
                JSONObject dateObject = new JSONObject();
                dateObject.accumulate("start_date", startDate);
                dateObject.accumulate("end_date", endDate);
                queryParams.accumulate("date", dateObject);
            }

// if (groupFilters != null) {
// for (String filter : groupFilters) {
// queryParams.accumulate("group_field", filter);
// }
// }

// JSONArray array = new JSONArray();
// array.put("testing");
// queryParams.accumulate("group_field", array);
            return queryParams;
        }
        catch (Exception e) {
            LOGGER.error(
                    "Ocurrio un error preparando los parametros para la query",
                    e);
            return null;
        }
    }

    @RequestMapping(value = "/stats/collect/wideos/active", method = RequestMethod.GET)
    public String collectActiveWideos(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "lastWeekDiff", required = false, defaultValue = "false") Boolean showLastWeek) {

        try {
            
            DateTimeFormatter formatter = DateTimeFormat
                    .forPattern("yyyy-MM-dd");

            DateTime startDateDateTime;
            DateTime endDateDateTime;
            if (startDate != null) {
                startDateDateTime = formatter.withZone(DateTimeZone.UTC)
                        .parseDateTime(startDate);
            } else {
                startDateDateTime = new LocalDate().withDayOfWeek(1).toDateMidnight().toDateTime(DateTimeZone.UTC);
            }
            if (endDate != null) {
                endDateDateTime = formatter.withZone(DateTimeZone.UTC)
                        .parseDateTime(endDate);
            } else {
                endDateDateTime = new LocalDate().toDateMidnight().toDateTime(DateTimeZone.UTC);
            }
            
            // Se suma uno a la fecha de fin para hacer query por Less Than
            endDateDateTime = endDateDateTime.plusDays(1);

            
            JSONObject params = new JSONObject();
            params.put("start_date", startDateDateTime);
            params.put("end_date", endDateDateTime);

            List<String> results = statsCollectorBO
                    .collectActiveWideosStats(params);

            model.addAttribute("results", results.size());

            if (showLastWeek) {
                DateTime previousWeekStartDate = startDateDateTime.minusDays(7);
                        
                DateTime previousWeekEndDate = endDateDateTime.minusDays(7);

                params.put("start_date", previousWeekStartDate);
                params.put("end_date", previousWeekEndDate);

                List<String> lastWeekResults = statsCollectorBO
                        .collectActiveWideosStats(params);

                model.addAttribute("lastWeekResults", lastWeekResults.size());

            }
        }
        catch (Exception e) {
            LOGGER.error("Ocurrio un error");
            e.printStackTrace();
        }
        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/stats/collect/wideos/{wideoID}/forms", method = RequestMethod.GET)
    public String getFormsInformationForWideo(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            @PathVariable(value = "wideoID") String wideoID,
            @RequestParam(value = "start", required = true) String startDate,
            @RequestParam(value = "end", required = true) String endDate,
            @RequestParam(value = "interactionID", required = false) String interactionID,
            @RequestParam(value = "attachment", required = false, defaultValue = "false") Boolean createFile) {
        try {
            JSONObject queryParams = new JSONObject();
            List<String> interactionIDGroupFilter = new ArrayList<String>();
            interactionIDGroupFilter.add("interaction_id");
            queryParams = prepareWideoQueryParams(
                    EventNamespaceTypesEnum.interactivity.getName(), wideoID,
                    WideoEventTypesEnum.FORM_SENT.getName(), startDate,
                    endDate, interactionIDGroupFilter);
            if ((interactionID != null) && (!interactionID.isEmpty())) {
                queryParams.accumulate("interaction_id", interactionID);
            }

            Map<String, TreeMap<Date, FormsStats>> results = statsCollectorBO
                    .collectFormsInfoForWideo(queryParams);
            if (!createFile) {
                model.addAttribute("data", results);
            }
            else {
                ExcelWriter writer = new ExcelWriter();
                for (String resultInteractionID : results.keySet()) {
                    List<CSVField> formFields = new ArrayList<CSVField>();
                    formFields.add(new CSVField("date"));
                    TreeMap<Date, FormsStats> forms = results
                            .get(resultInteractionID);
                    Entry<Date, FormsStats> dateForms = forms.firstEntry();
                    Set<String> formKeys = dateForms.getValue().getData()
                            .keySet();
                    for (String formKey : formKeys) {
                        formFields.add(new CSVField(formKey));
                    }
                    writer.init(formFields, resultInteractionID,
                            response.getOutputStream());
                    for (Date d : forms.keySet()) {
                        if (forms.containsKey(d)) {
                            System.out.println("la tiene");
                        }
                        FormsStats f = forms.get(d);
                        Map<String, String> formRecord = new HashMap<String, String>();
                        formRecord.put("date", d.toString());
                        for (Entry<String, Object> set : f.getData().entrySet()) {
                            formRecord.put(set.getKey(),
                                    String.valueOf(set.getValue()));
                        }
                        writer.writeRecord(formRecord);
                    }
                }
                int length = writer.writeAndclose();
                response.setContentLength(length);

                response.setStatus(HttpServletResponse.SC_OK);
                response.setHeader("Content-Disposition",
                        "attachment; filename=" + "forms.xls");
                response.setContentType("application/vnd.ms-excel");
            }
        }
        catch (Exception e) {
            LOGGER.error(
                    "Ocurrio un error intentando recopilar las stats de formularios del wideo: "
                            + wideoID, e);
        }
        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/stats/collect/mailing/users", method = RequestMethod.GET)
    public String getUsersForMailing(HttpServletRequest request,
            HttpServletResponse response, Model model,
            @RequestParam(value = "start", required = true) String startDate,
            @RequestParam(value = "end", required = true) String endDate) {

        try {
            System.out.println("in");
            JSONObject params = new JSONObject();

            params.accumulate("start_date", startDate);
            params.accumulate("end_date", endDate);
            Map<Long, Object> data = statsCollectorBO
                    .getUsersStatsForMailing(params);
            Set<Long> users = data.keySet();
            List<Long> unique = new ArrayList<Long>();
            System.out.println("data finished");
            for (Long u : users) {

                if (unique.contains(u)) {
                    System.out.println("repetido");
                }
                unique.add(u);
            }
            model.addAttribute("data", data);
        }
        catch (Exception e) {
            LOGGER.info("Ocurrio un error procesando los usuarios", e);
        }
        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/stats/collect/metadata/interactivity", method = RequestMethod.GET)
    public String getMetadataInfo(HttpServletRequest request,
            HttpServletResponse response, Model model,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
            DateTime startDateTime;
            DateTime endDateTime;
            if (startDate != null) {
                startDateTime = formatter.parseDateTime(startDate);
                endDateTime = formatter.parseDateTime(endDate);
            }
            else {
                startDateTime = new LocalDate().withDayOfWeek(1).toDateMidnight()
                        .toDateTime();
                endDateTime = new LocalDate().withDayOfWeek(7).toDateMidnight()
                        .toDateTime();

            }

            JSONObject params = new JSONObject();
            params.accumulate("start_date", startDateTime);
            params.accumulate("end_date", endDateTime);
            params.accumulate("db_collection", "metadata");

            Map<String, Object> results = statsCollectorBO
                    .getMetadataInfo(params);
            model.addAttribute("interactivity", results.get("interactivity"));
        }
        catch (Exception e) {
            LOGGER.info("Error obteniendo metadata", e);
        }

        return "wideoMetricsJsonView";
    }
    
    @RequestMapping(value = "/metrics/overall", method = RequestMethod.GET)
    public String getOverallStats(HttpServletRequest request, HttpServletResponse response, Model model,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
            DateTime startDateTime;
            DateTime endDateTime;
            if (startDate != null) {
                startDateTime = formatter.parseDateTime(startDate);
                endDateTime = formatter.parseDateTime(endDate);
            }
            else {
                startDateTime = new LocalDate().withDayOfWeek(1).toDateMidnight()
                        .toDateTime();
                endDateTime = new LocalDate().withDayOfWeek(7).toDateMidnight()
                        .toDateTime();

            }
            
            JSONObject params = new JSONObject();
            params.accumulate("start_date", startDateTime);
            params.accumulate("end_date", endDateTime);
            
            Map<String, Object> results = statsCollectorBO.getTotalStats(params);
            System.out.println(results);
            model.addAttribute("overallStats", results);
            
            
            
        } catch (Exception e) {
            
        }
        
        return "wideoMetricsJsonView";
    }

    private void addUniqueWideosToList(List<String> resultList,
            List<WideoStatsResults> statsToAdd) {
        if (statsToAdd != null) {
            for (WideoStatsResults s : statsToAdd) {
                if (!resultList.contains(s.getWideoID())) {
                    resultList.add(s.getWideoID());
                }
            }
        }
    }

}
