package com.wideo.metrics.dal.mysql;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.wideo.metrics.commons.sqlcache.SQLCache;
import com.wideo.metrics.model.processor.stats.DashboardStats;
import com.wideo.metrics.persistence.jdbc.AbstractJdbcTemplate;

public class CheckOutDal extends AbstractJdbcTemplate implements
        CheckOutDalMySqlInterface {

    @Override
    public List<DashboardStats> getSoldPlansByName(Date startDate, Date endDate) {
        String query = SQLCache.getSql("GET_PLANS_SOLD_BY_NAME_BY_DATERANGE");
        Object[] params = { startDate, endDate };

        List<DashboardStats> results = this.jdbcTemplate.query(query, params,
                new RowMapper<DashboardStats>() {
                    @Override
                    public DashboardStats mapRow(ResultSet rs, int rowNum) {
                        try {
                            String chargeName = rs.getString("charge_name");
                            Long chargePlansSoldCount = rs
                                    .getLong("chargeSoldPlansCount");
                            return new DashboardStats(chargeName,
                                    chargePlansSoldCount);
                        }
                        catch (Exception e) {
                            return null;
                        }
                    }
                });
        return results;
    }

    @Override
    public Double getTodayTotalRevenue(Date today) {
        String query = SQLCache.getSql("GET_REVENUE");
        Object[] params = { today, today };

        return this.jdbcTemplate.queryForObject(query, params, Double.class);
    }

    @Override
    public Long getRecurrentPlansSold(Date startDate, Date endDate) {
        String query = SQLCache.getSql("GET_RECCURENT_PLANS_SOLD");
        Object[] params = { startDate, endDate };

        return this.jdbcTemplate.queryForObject(query, params, Long.class);
    }
           
    @Override
    public Long getTotalPlansSold(List<Integer> chargeTypeIds, Date startDate,
            Date endDate) {

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("chargeTypesIds", chargeTypeIds);
        values.put("startDate", startDate);
        values.put("endDate", endDate);
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(
                values);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
                jdbcTemplate.getDataSource());
        return namedParameterJdbcTemplate.queryForObject(
                SQLCache.getSql("GET_PLANS_SOLD"), namedParameters,
                Long.class);
    }

    public Long getDistinctPaidUsers(Date startDate, Date endDate) {
        String query = SQLCache.getSql("GET_DISTINCT_PAID_USERS");
        Object[] params = { startDate, endDate };
        return this.jdbcTemplate.queryForObject(query, params, Long.class);
    }

    public Double getRevenue(Date startDate, Date endDate) {
        String query = SQLCache.getSql("GET_REVENUE");
        Object[] params = { startDate, endDate };
        return this.jdbcTemplate.queryForObject(query, params, Double.class);
    }

    public Long getRecurrentPlansSold(List<Integer> plansIDs, Date startDate,
            Date endDate) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("chargeTypesIds", plansIDs);
        values.put("startDate", startDate);
        values.put("endDate", endDate);
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(
                values);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
                jdbcTemplate.getDataSource());
        return namedParameterJdbcTemplate.queryForObject(
                SQLCache.getSql("GET_RECURRENT_PLANS_BY_IDS_SOLD"), namedParameters,
                Long.class);
    }

    @Override
    public Double getRevenueForUsers(List<Long> userIDs, Date startDate,
            Date endDate) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userIDs", userIDs);
        values.put("startDate", startDate);
        values.put("endDate", endDate);
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(
                values);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
                jdbcTemplate.getDataSource());
        return namedParameterJdbcTemplate.queryForObject(
                SQLCache.getSql("GET_REVENUE_FOR_USERS"), namedParameters,
                Double.class);
    }
}
