package com.wideo.metrics.bo.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import com.wideo.metrics.dal.mysql.CheckOutDal;
import com.wideo.metrics.dal.mysql.UsersDal;
import com.wideo.metrics.model.processor.stats.DashboardStats;

@Service
@ComponentScan("com.wideo.metrics")
public class CheckOutBOImpl implements CheckOutBO {

    private static final List<Integer> monthlyPlans;
    private static final List<Integer> quarterPlans;
    private static final List<Integer> annualPlans;

    static {
        List<Integer> plans = new ArrayList<Integer>();
        plans.add(26);
        plans.add(27);
        plans.add(28);
        plans.add(34);
        plans.add(36);
        plans.add(38);
        plans.add(40);
        monthlyPlans = plans;
        List<Integer> plansA = new ArrayList<Integer>();
        plansA.add(19);
        plansA.add(20);
        plansA.add(21);
        plansA.add(33);
        plansA.add(35);
        plansA.add(37);
        plansA.add(39);
        plansA.add(41);
        annualPlans = plansA;
        List<Integer> plansQ = new ArrayList<Integer>();
        plansQ.add(42);
        quarterPlans = plansQ;
    }

    @Autowired
    CheckOutDal checkOutDal;
    @Autowired
    UsersDal usersDal;

    @Override
    public List<DashboardStats> getSoldPlansByName(Date startDate, Date endDate) {
        return checkOutDal.getSoldPlansByName(startDate, endDate);
    }

    @Override
    public Map<String, Object> getTodayTotalRevenue(Date today) {
        Map<String, Object> revenueStats = new HashMap<String, Object>();
        Long todaySignups = usersDal.getTodayTotalSignups();
        Double todayRevenue = checkOutDal.getTodayTotalRevenue(today);

        revenueStats.put("totalRevenue", todayRevenue);
        revenueStats.put("totalRevenueBySignup", todayRevenue / todaySignups);
        return revenueStats;
    }

    @Override
    public Long getRecurrentPlansSold(Date startDate, Date endDate) {
        return checkOutDal.getRecurrentPlansSold(startDate, endDate);
    }

    @Override
    public Long getTotalPlansSold(List<Integer> chargeTypeIds, Date startDate,
            Date endDate) {
        return checkOutDal.getTotalPlansSold(chargeTypeIds, startDate, endDate);
    }

    @Override
    public Double getMonthChurnRate(Integer month, Integer year) {
        DateTime monthStartDateTime = new DateTime().withMonthOfYear(month)
                .withYear(year).dayOfMonth().withMinimumValue()
                .toDateMidnight().toDateTime();
        DateTime monthEndDateTime = new DateTime().withMonthOfYear(month)
                .withYear(year).dayOfMonth().withMaximumValue()
                .toDateMidnight().toDateTime();

        DateTime previousMonthStartDateTime = monthStartDateTime.minusMonths(1)
                .toDateMidnight().toDateTime();
        DateTime previousMonthEndDateTime = monthEndDateTime.minusMonths(1)
                .dayOfMonth().withMaximumValue().toDateMidnight().toDateTime();

        DateTime monthMinusThreeStartDateTime = monthStartDateTime
                .minusMonths(3).dayOfMonth().withMinimumValue()
                .toDateMidnight().toDateTime();
        DateTime monthMinusThreeEndDateTime = monthEndDateTime.minusMonths(3)
                .dayOfMonth().withMaximumValue().toDateMidnight().toDateTime();

        DateTime lastSameMonthStartDateTime = monthStartDateTime.minusYears(1)
                .toDateMidnight().toDateTime();
        DateTime lastSameMonthEndDateTime = monthEndDateTime.minusYears(1)
                .toDateMidnight().toDateTime();

        DateTime lastElevenMonthsStartDateTime = monthStartDateTime
                .minusMonths(11).dayOfMonth().withMinimumValue()
                .toDateMidnight().toDateTime();
        DateTime lastElevenMonthsEndDateTime = monthEndDateTime.minusMonths(1)
                .dayOfMonth().withMaximumValue().toDateMidnight().toDateTime();

        DateTime lastQuarterMonthStartDateTime = monthStartDateTime
                .minusMonths(3).dayOfMonth().withMinimumValue()
                .toDateMidnight().toDateTime();
        DateTime lastQuarterMonthEndDateTime = monthEndDateTime.minusMonths(1)
                .dayOfMonth().withMaximumValue().toDateMidnight().toDateTime();

        Long monthlyPreviousMonth = checkOutDal.getTotalPlansSold(monthlyPlans,
                previousMonthStartDateTime.toDate(),
                previousMonthEndDateTime.toDate());

        Long monthlyRecurrentsPreviousMonth = checkOutDal
                .getRecurrentPlansSold(monthlyPlans,
                        previousMonthStartDateTime.toDate(),
                        previousMonthEndDateTime.toDate());
        Long quarterlyThreeMonthsAgo = checkOutDal.getTotalPlansSold(
                quarterPlans, monthMinusThreeStartDateTime.toDate(),
                monthMinusThreeEndDateTime.toDate());
        Long quarterlyThreeMonthsAgoRecurrent = checkOutDal
                .getRecurrentPlansSold(quarterPlans,
                        monthMinusThreeStartDateTime.toDate(),
                        monthMinusThreeEndDateTime.toDate());
        Long lastYearAnnual = checkOutDal.getTotalPlansSold(annualPlans,
                lastSameMonthStartDateTime.toDate(),
                lastSameMonthEndDateTime.toDate());
        Long lastYearAnnualRecurrent = checkOutDal.getRecurrentPlansSold(
                annualPlans, lastSameMonthStartDateTime.toDate(),
                lastSameMonthEndDateTime.toDate());
        Long currentRecurrent = checkOutDal.getRecurrentPlansSold(
                monthStartDateTime.toDate(), monthEndDateTime.toDate());
        Long lastElevenAnnual = checkOutDal.getTotalPlansSold(annualPlans,
                lastElevenMonthsStartDateTime.toDate(),
                lastElevenMonthsEndDateTime.toDate());
        Long lastElevenAnnualRecurrent = checkOutDal.getRecurrentPlansSold(
                annualPlans, lastElevenMonthsStartDateTime.toDate(),
                lastElevenMonthsEndDateTime.toDate());
        Long lastQuarterSales = checkOutDal.getTotalPlansSold(quarterPlans,
                lastQuarterMonthStartDateTime.toDate(),
                lastQuarterMonthEndDateTime.toDate());
        Long lastQuarterSalesRecurrent = checkOutDal.getRecurrentPlansSold(
                quarterPlans, lastQuarterMonthStartDateTime.toDate(),
                lastQuarterMonthEndDateTime.toDate());

        Long num = monthlyPreviousMonth + monthlyRecurrentsPreviousMonth
                + quarterlyThreeMonthsAgo + quarterlyThreeMonthsAgoRecurrent
                + lastYearAnnual + lastYearAnnualRecurrent - currentRecurrent;
        Long denom = lastElevenAnnual + lastElevenAnnualRecurrent
                + lastQuarterSales + lastQuarterSalesRecurrent
                + monthlyPreviousMonth + monthlyRecurrentsPreviousMonth;

        return Double.valueOf((double) num / (double) denom);
    }

    @Override
    public Long getDistinctPaidUsers(Date startDate, Date endDate) {
        return checkOutDal.getDistinctPaidUsers(startDate, endDate);
    }

    @Override
    public Double getRevenue(Date startDate, Date endDate) {
        return checkOutDal.getRevenue(startDate, endDate);
    }

    @Override
    public Double getRevenueForUsers(List<Long> userIDs, Date startDate,
            Date endDate) {
        return checkOutDal.getRevenueForUsers(userIDs,startDate,endDate);
    }
}
