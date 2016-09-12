//package com.wideo.metrics.collector.controller;
//
//import java.io.File;
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.joda.time.format.DateTimeFormat;
//import org.joda.time.format.DateTimeFormatter;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.http.HttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.client.util.GenericData;
//import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
//import com.google.api.services.analyticsreporting.v4.AnalyticsReportingScopes;
//import com.google.api.services.analyticsreporting.v4.model.ColumnHeader;
//import com.google.api.services.analyticsreporting.v4.model.DateRange;
//import com.google.api.services.analyticsreporting.v4.model.DateRangeValues;
//import com.google.api.services.analyticsreporting.v4.model.Dimension;
//import com.google.api.services.analyticsreporting.v4.model.GetReportsRequest;
//import com.google.api.services.analyticsreporting.v4.model.GetReportsResponse;
//import com.google.api.services.analyticsreporting.v4.model.Metric;
//import com.google.api.services.analyticsreporting.v4.model.MetricHeaderEntry;
//import com.google.api.services.analyticsreporting.v4.model.OrderBy;
//import com.google.api.services.analyticsreporting.v4.model.Report;
//import com.google.api.services.analyticsreporting.v4.model.ReportRequest;
//import com.google.api.services.analyticsreporting.v4.model.ReportRow;
//import com.wideo.metrics.bo.collector.AnalyticsCollectorBO;
//import com.wideo.metrics.bo.core.CheckOutBO;
//import com.wideo.metrics.bo.core.UsersBO;
//import com.wideo.metrics.model.collector.GoogleAnalyticsData;
//
//import java.util.List;
//
//@Controller
//@ComponentScan("com.wideo.metrics")
//public class AnalyticsReportingController {
//
//    @Autowired
//    AnalyticsCollectorBO analyticsCollectorBO;
//
//    @Autowired
//    UsersBO usersBO;
//
//    @Autowired
//    CheckOutBO checkOutBO;
//    @RequestMapping(value = "/analytics/sources/data", method = RequestMethod.GET)
//    public String testAnalytics(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            Model model,
//            @RequestParam("startDate") String startDate,
//            @RequestParam("endDate") String endDate,
//            @RequestParam(value = "userCategories", required = false) List<String> userCategories) {
//        
//        System.out.println("in analytics");
//        System.out.println(startDate);
//        System.out.println(endDate);
//        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
//
//        Map<String, String> filters = new HashMap<String, String>();
//        filters.put("googleOrganic", "ga:source==google;ga:medium==organic");
//        filters.put("shareMedium", "ga:medium==share");
//        filters.put(
//                "cj",
//                "ga:source==cjaffilate,ga:source==promopro,ga:source==dealspotr.com,ga:source==turbocupones.mx");
//        filters.put("intercom", "ga:source==intercom");
//        filters.put("blog", "ga:source==Blog");
//        filters.put("bing", "ga:source==bing");
//        filters.put("naver", "ga:source==naver");
//        filters.put("facebookCPA",
//                "ga:source==facebook;ga:medium==CPA,ga:medium==CPC");
//        filters.put("facebookNotCPA",
//                "ga:source==facebook;ga:medium!=CPA;ga:medium!=CPC");
//        filters.put("googleCPA",
//                "ga:source==google;ga:medium==CPA,ga:medium==CPC");
//
//        try {
//            userCategories = parseUserCategoriesFilter(userCategories);
//
//            Date startDateObject = formatter.parseDateTime(startDate).toDate();
//            Date endDateObject = formatter.parseDateTime(endDate).toDate();
//            Long totalSignups = usersBO.getSignupsByDate(startDateObject,
//                    endDateObject, userCategories);
//            List<GoogleAnalyticsData> results = new ArrayList<GoogleAnalyticsData>();
//            for (String filterName : filters.keySet()) {
//                String filter = filters.get(filterName);
//                GoogleAnalyticsData filterData = new GoogleAnalyticsData();
//                filterData.setFilterName(filterName);
//                List<Long> userIDs = analyticsCollectorBO
//                        .getUsersFromSourceMedium(startDate, endDate, filter);
//                System.out.println("debug here");
//                if (!userIDs.isEmpty()) {
//                    Integer usersSignedUpCount = usersBO.getUsersSignedUp(
//                            startDateObject, endDateObject, userIDs,
//                            userCategories);
//                    System.out.println(usersSignedUpCount + "-" + Double.valueOf(usersSignedUpCount));
//                    filterData
//                            .setSignupsPct(Double.valueOf(usersSignedUpCount));
//                    Double revenue = checkOutBO.getRevenueForUsers(userIDs,startDateObject,endDateObject);
//                    filterData.setRevenue(revenue);
//                }
//                results.add(filterData);
//            }
//            model.addAttribute("data", results);
//
//        }
//        catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        return "wideoMetricsJsonView";
//    }
//
//    private List<String> parseUserCategoriesFilter(List<String> userCategories) {
//        List<String> userCategoriesFiltered = new ArrayList<String>();
//        if (userCategories != null) {
//            for (String s : userCategories) {
//                try {
//                    JSONObject j = new JSONObject(s);
//                    userCategoriesFiltered.add(j.getString("id"));
//                }
//                catch (Exception e) {
//                }
//            }
//            return userCategoriesFiltered;
//        }
//        return null;
//    }
//}
