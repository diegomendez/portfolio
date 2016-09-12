package com.wideo.metrics.collector.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.wideo.metrics.bo.core.CheckOutBO;
import com.wideo.metrics.bo.core.UsersBO;
import com.wideo.metrics.bo.core.WideoosBO;
import com.wideo.metrics.model.processor.stats.DashboardStats;
import com.wideo.metrics.processor.controller.Test;

@Controller
@ComponentScan("com.wideo.metrics")
public class MetricsDashboardController {

    @Autowired
    WideoosBO wideoosBO;
    @Autowired
    UsersBO usersBO;
    @Autowired
    CheckOutBO checkOutBO;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView loadDashboard(HttpServletRequest request,
            HttpServletResponse response, Model model) {

        return new ModelAndView("/metrics/ng/home");
    }

    @RequestMapping(value = "/metrics/wideos/count", method = RequestMethod.GET)
    public String getWideosQtyByDate(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "userCategories", required = false) List<String> userCategories)
            throws JSONException {
        System.out.println("in wideos");
        userCategories = parseUserCategoriesFilter(userCategories);

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
            endDateTime = new LocalDate().toDateMidnight().toDateTime();

        }

        List<DashboardStats> stats = new ArrayList<DashboardStats>();
        for (int i = 4; i >= 0; i--) {
            String week = "Week from "
                    + startDateTime.minusWeeks(i).getDayOfMonth() + "/"
                    + startDateTime.minusWeeks(i).getMonthOfYear() + " to "
                    + endDateTime.minusWeeks(i).getDayOfMonth() + "/"
                    + endDateTime.minusWeeks(i).getMonthOfYear();
            Integer weekCount = wideoosBO
                    .getWideosCreatedByDate(startDateTime.minusWeeks(i)
                            .toDate(), endDateTime.minusWeeks(i).toDate(),userCategories);

            stats.add(new DashboardStats(week, weekCount));
        }

        model.addAttribute("wideosCreated", stats);

        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/metrics/signups", method = RequestMethod.GET)
    public String getSignupsQtyByDate(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "historical", required = false, defaultValue = "false") Boolean historical,
            @RequestParam(value = "userCategories", required = false) List<String> userCategories) {
        System.out.println("in signups");
        
        userCategories = parseUserCategoriesFilter(userCategories);
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
            endDateTime = new LocalDate().toDateMidnight().toDateTime();

        }

        if (historical) {
            List<DashboardStats> stats = new ArrayList<DashboardStats>();
            for (int i = 4; i >= 0; i--) {
                String week = "Week from "
                        + startDateTime.minusWeeks(i).getDayOfMonth() + "/"
                        + startDateTime.minusWeeks(i).getMonthOfYear() + " to "
                        + endDateTime.minusWeeks(i).getDayOfMonth() + "/"
                        + endDateTime.minusWeeks(i).getMonthOfYear();

                Long weekSignups = usersBO.getSignupsByDate(startDateTime
                        .minusWeeks(i).toDate(), endDateTime.minusWeeks(i)
                        .toDate(), null);

                stats.add(new DashboardStats(week, weekSignups));
            }
            model.addAttribute("signupsByWeek", stats);
        }
        else {
            Long currentWeekSignups = usersBO.getSignupsByDate(
                    startDateTime.toDate(), endDateTime.toDate(),userCategories);
            Long previousWeekSignups = usersBO
                    .getSignupsByDate(startDateTime.minusWeeks(1).toDate(),
                            endDateTime.minusWeeks(1).toDate(),userCategories);

            model.addAttribute("currentWeekSignups", currentWeekSignups);
            model.addAttribute("lastWeekSignups", previousWeekSignups);
        }

        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/metrics/signupsByCategory", method = RequestMethod.GET)
    public String getSignupsByCategoryQtyByDate(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        System.out.println("in signups by cat");
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
            endDateTime = new LocalDate().toDateMidnight().toDateTime();

        }

        List<DashboardStats> signupsByCategory = usersBO
                .getSignupsByUserCategoryByDate(startDateTime.toDate(),
                        endDateTime.toDate());

        model.addAttribute("signupsByCategory", signupsByCategory);

        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/metrics/sales", method = RequestMethod.GET)
    public String getSalesData(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime startDateTime;
        DateTime endDateTime;
        if (startDate != null) {
            startDateTime = formatter.parseDateTime(startDate);
            endDateTime = formatter.parseDateTime(endDate);
        }
        else {
            startDateTime = new LocalDate().toDateMidnight().toDateTime();
            endDateTime = new LocalDate().toDateMidnight().toDateTime();

        }

        Date today = new LocalDate().toDateMidnight().toDate();

        List<DashboardStats> soldPlansByName = checkOutBO.getSoldPlansByName(
                startDateTime.toDate(), endDateTime.toDate());

        Map<String, Object> todayRevenueData = checkOutBO
                .getTodayTotalRevenue(today);

        model.addAttribute("salesByCharge", soldPlansByName);
        model.addAttribute("revenueBySignup",
                todayRevenueData.get("totalRevenueBySignup"));

        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/metrics/revenue", method = RequestMethod.GET)
    public String getRevenueData(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "historical", required = false, defaultValue = "false") Boolean historical) {

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
            endDateTime = new LocalDate().toDateMidnight().toDateTime();
        }

        System.out.println("in revenue");
        if (historical) {
            List<DashboardStats> stats = new ArrayList<DashboardStats>();
            for (int i = 4; i >= 0; i--) {
                String week = "Week from "
                        + startDateTime.minusWeeks(i).getDayOfMonth() + "/"
                        + startDateTime.minusWeeks(i).getMonthOfYear() + " to "
                        + endDateTime.minusWeeks(i).getDayOfMonth() + "/"
                        + endDateTime.minusWeeks(i).getMonthOfYear();
                Double weekRevenue = checkOutBO.getRevenue(startDateTime
                        .minusWeeks(i).toDate(), endDateTime.minusWeeks(i)
                        .toDate());

                stats.add(new DashboardStats(week, weekRevenue));
            }
            model.addAttribute("revenueByWeek", stats);

        }
        else {
            Date today = new LocalDate().toDateMidnight().toDate();

            Map<String, Object> todayRevenueData = checkOutBO
                    .getTodayTotalRevenue(today);
            model.addAttribute("totalRevenue",
                    todayRevenueData.get("totalRevenue"));
            Date lastWeekSameDay = new LocalDate().minusDays(7)
                    .toDateMidnight().toDate();
            Map<String, Object> lastWeekRevenueData = checkOutBO
                    .getTodayTotalRevenue(lastWeekSameDay);
            model.addAttribute("lastWeekSameDayTotalRevenue",
                    lastWeekRevenueData.get("totalRevenue"));
        }

        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/metrics/bestWideos", method = RequestMethod.GET)
    public String getWeekBestWideos(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "count", required = false, defaultValue = "5") Integer count) {
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
        List<DashboardStats> bestWideos = wideoosBO.getBestWideos(
                startDateTime.toDate(), endDateTime.toDate(), count);

        model.addAttribute("bestWideos", bestWideos);
        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/metrics/churn", method = RequestMethod.GET)
    public String getChurnRate(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {

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
            endDateTime = new LocalDate().toDateMidnight().toDateTime();
        }

        Integer month = endDateTime.getMonthOfYear();
        Integer year = endDateTime.getYear();

        LocalDate today = new LocalDate();
        if ((month != null) && (year != null)) {
            today = today.withMonthOfYear(month).withYear(year);
        }

        List<DashboardStats> stats = new ArrayList<DashboardStats>();
        for (int i = 5; i > 0; i--) {
            Integer currentMonth = today.minusMonths(i).getMonthOfYear();
            Integer currentYear = today.minusMonths(i).getYear();
            String monthText = "Month " + currentMonth + "/" + currentYear;

            Double monthChurnRate = checkOutBO.getMonthChurnRate(currentMonth,
                    currentYear);

            stats.add(new DashboardStats(monthText, monthChurnRate * 100));
        }
        model.addAttribute("monthlyChurnRate", stats);

        return "wideoMetricsJsonView";
    }

    @RequestMapping(value = "/metrics/conversion/signups", method = RequestMethod.GET)
    public String getWeeklyConversionRate(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {

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
            endDateTime = new LocalDate().toDateMidnight().toDateTime();
        }

        List<DashboardStats> stats = new ArrayList<DashboardStats>();
        for (int i = 4; i >= 0; i--) {
            String week = "Week from "
                    + startDateTime.minusWeeks(i).getDayOfMonth() + "/"
                    + startDateTime.minusWeeks(i).getMonthOfYear() + " to "
                    + endDateTime.minusWeeks(i).getDayOfMonth() + "/"
                    + endDateTime.minusWeeks(i).getMonthOfYear();

            Long weekSignups = usersBO
                    .getSignupsByDate(startDateTime.minusWeeks(i).toDate(),
                            endDateTime.minusWeeks(i).toDate(),null);

            Long distinctPaidUsers = checkOutBO.getDistinctPaidUsers(
                    startDateTime.minusWeeks(i).toDate(), endDateTime
                            .minusWeeks(i).toDate());

            Double conversion = (double) distinctPaidUsers
                    / (double) weekSignups;

            stats.add(new DashboardStats(week, conversion * 100));
        }
        model.addAttribute("weeklySignupConversion", stats);
        return "wideoMetricsJsonView";
    }
    
    private List<String> parseUserCategoriesFilter(List<String> userCategories) {
        List<String> userCategoriesFiltered = new ArrayList<String>();
        if (userCategories != null) {
            for (String s : userCategories) {
                try {
                    JSONObject j = new JSONObject(s);
                    userCategoriesFiltered.add(j.getString("id"));
                }
                catch (Exception e) {
                }
            }
            return userCategoriesFiltered;
        }
        return null;
    }
}
