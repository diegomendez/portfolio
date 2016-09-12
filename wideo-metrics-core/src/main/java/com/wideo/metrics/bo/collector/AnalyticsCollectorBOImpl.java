//package com.wideo.metrics.bo.collector;
//
//import java.io.File;
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.stereotype.Service;
//
//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.http.HttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.gson.GsonFactory;
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
//
//@Service
//@ComponentScan("com.wideo.metrics")
//public class AnalyticsCollectorBOImpl implements AnalyticsCollectorBO {
//
//    private static final String APPLICATION_NAME = "Wideo Stats Dashboard";
//    private static final JsonFactory JSON_FACTORY = GsonFactory
//            .getDefaultInstance();
//    private static final String KEY_FILE_LOCATION = "/Wideo-260e114b5853.p12";
//    private static final String SERVICE_ACCOUNT_EMAIL = "wideo-dashboard-stats@wideo-00.iam.gserviceaccount.com";
//    private static final String VIEW_ID = "83506413";
//    private static AnalyticsReporting analytics = initializeAnalyticsReporting();
//
//    private static AnalyticsReporting initializeAnalyticsReporting() {
//        try {
//            HttpTransport httpTransport = GoogleNetHttpTransport
//                    .newTrustedTransport();
//            GoogleCredential credential = new GoogleCredential.Builder()
//                    .setTransport(httpTransport)
//                    .setJsonFactory(JSON_FACTORY)
//                    .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
//                    .setServiceAccountPrivateKeyFromP12File(
//                            new File(AnalyticsCollectorBOImpl.class
//                                    .getResource(KEY_FILE_LOCATION).getFile()))
//                    .setServiceAccountScopes(AnalyticsReportingScopes.all())
//                    .build();
//
//            // Construct the Analytics Reporting service object.
//            return new AnalyticsReporting.Builder(httpTransport, JSON_FACTORY,
//                    credential).setApplicationName(APPLICATION_NAME).build();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Override
//    public List<Long> getUsersFromSourceMedium(String startDate,
//            String endDate, String filterExpression) {
//        List<Long> userIDs = new ArrayList<Long>();
//
//        List<String> metricsToLoad = new ArrayList<String>();
//        metricsToLoad.add("ga:sessions");
//        metricsToLoad.add("ga:pageviews");
//        metricsToLoad.add("ga:transactions");
//
//        List<String> dimensionsToLoad = new ArrayList<String>();
//        dimensionsToLoad.add("ga:source");
//        dimensionsToLoad.add("ga:medium");
//        dimensionsToLoad.add("ga:dimension2");
//
//        List<String> ordersToLoad = new ArrayList<String>();
//        ordersToLoad.add("ga:pageviews");
//
//        System.out.println(filterExpression);
//        boolean nextPage = true;
//        String pageToken = null;
//        while (nextPage != false) {
//            System.out.println("processing");
//            GetReportsResponse response = queryGoogleAnalyticsData(startDate,
//                    endDate, metricsToLoad, dimensionsToLoad, ordersToLoad,
//                    filterExpression, pageToken);
//            List<Long> users = getUserIDs(response);
//            if (users != null) {
//                userIDs.addAll(users);                
//            }
//            System.out.println(response.getReports().get(0).getNextPageToken());
//            pageToken = response.getReports().get(0).getNextPageToken();
//            if (pageToken == null) {
//                nextPage = false;
//            }
//        }
//
//        return userIDs;
//    }
//
//    @Override
//    public GetReportsResponse queryGoogleAnalyticsData(String startDate,
//            String endDate, List<String> metricsToLoad,
//            List<String> dimensionsToLoad, List<String> ordersToLoad,
//            String filterExpression, String pageToken) {
//
//        try {
//            // Create the DateRange object.
//            DateRange dateRange = new DateRange();
//            dateRange.setStartDate(startDate);
//            dateRange.setEndDate(endDate);
//
//            List<Metric> metrics = new ArrayList<Metric>();
//            for (String metricName : metricsToLoad) {
//                Metric m = new Metric();
//                m.setExpression(metricName);
//                m.setAlias(metricName.split("ga:")[1]);
//                metrics.add(m);
//            }
//
//            List<Dimension> dimensions = new ArrayList<Dimension>();
//            for (String dimensionName : dimensionsToLoad) {
//                Dimension d = new Dimension();
//                d.setName(dimensionName);
//                dimensions.add(d);
//            }
//
//            List<OrderBy> orders = new ArrayList<OrderBy>();
//            for (String orderName : ordersToLoad) {
//                OrderBy o = new OrderBy();
//                o.setFieldName(orderName);
//                orders.add(o);
//            }
//
//            // Create the ReportRequest object.
//            ReportRequest request = new ReportRequest().setViewId(VIEW_ID)
//                    .setDateRanges(Arrays.asList(dateRange))
//                    .setDimensions(dimensions).setMetrics(metrics)
//                    .setOrderBys(orders).setFiltersExpression(filterExpression)
//                    .setPageSize(10000);
//
//            if (pageToken != null) {
//                request.setPageToken(pageToken);
//            }
//            
//            ArrayList<ReportRequest> requests = new ArrayList<ReportRequest>();
//            requests.add(request);
//
//            // Create the GetReportsRequest object.
//            GetReportsRequest getReport = new GetReportsRequest()
//                    .setReportRequests(requests);
//
//            // Call the batchGet method.
//            GetReportsResponse response = analytics.reports()
//                    .batchGet(getReport).execute();
//
//            // Return the response.
//            return response;
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private List<Long> getUserIDs(GetReportsResponse response) {
//        List<Long> userIDs = new ArrayList<Long>();
//        if (response != null) {
//            for (Report report : response.getReports()) {
//                ColumnHeader header = report.getColumnHeader();
//                List<String> dimensionHeaders = header.getDimensions();
//                List<MetricHeaderEntry> metricHeaders = header
//                        .getMetricHeader().getMetricHeaderEntries();
//                List<ReportRow> rows = report.getData().getRows();
//
//                if (rows == null) {
//                    System.out.println("No data found for " + VIEW_ID);
//                    return null;
//                }
//
//                for (ReportRow row : rows) {
//                    List<String> dimensions = row.getDimensions();
//                    List<DateRangeValues> metrics = row.getMetrics();
//// for (int i = 0; i < dimensionHeaders.size()
//// && i < dimensions.size(); i++) {
//                    int k = 2;
//                    userIDs.add(Long.valueOf((String) dimensions.get(k)));
//                    // System.out.println(dimensionHeaders.get(i) + ": "
//// + dimensions.get(i));
//// }
//
//// }
//                }
//            }
//        }
//        return userIDs;
//    }
//
//}
