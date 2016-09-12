package com.wideo.metrics.dal.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
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

public class UsersDal extends AbstractJdbcTemplate implements
        UsersDalMySqlInterface {

    @Override
    public Long getSignupsByDate(Date startDate, Date endDate,
            List<String> userCategories) {
        String query;
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("startDate", startDate);
        values.put("endDate", endDate);
        if (userCategories != null) {
            values.put("userCategories", userCategories);
            query = SQLCache
                    .getSql("GET_SIGNUPS_BY_DATERANGE_USERCATEGORIES_QUERY");
        }
        else {
            query = SQLCache.getSql("GET_SIGNUPS_BY_DATERANGE_QUERY");
        }
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(
                values);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
                jdbcTemplate.getDataSource());

        return namedParameterJdbcTemplate.queryForObject(query, values,
                Long.class);

// String query = SQLCache.getSql("GET_SIGNUPS_BY_DATERANGE_QUERY");
// Object[] params = { startDate, endDate };
// return this.jdbcTemplate.queryForObject(query, params, Long.class);
    }

    @Override
    public List<DashboardStats> getSignupsByUserCategoryByDate(Date startDate,
            Date endDate) {
        String query = SQLCache
                .getSql("GET_SIGNUPS_BY_DATERANGE_BY_USER_CATEGORY_QUERY");
        Object[] params = { startDate, endDate };

        List<DashboardStats> results = this.jdbcTemplate.query(query, params,
                new RowMapper<DashboardStats>() {
                    @Override
                    public DashboardStats mapRow(ResultSet rs, int rowNum) {
                        try {
                            String userCategory = rs.getString("user_category");
                            Long signupsCount = rs.getLong("signups");
                            return new DashboardStats(userCategory,
                                    signupsCount);
                        }
                        catch (Exception e) {
                            return null;
                        }
                    }
                });
        return results;
    }

    public Long getTodayTotalSignups() {
        String query = SQLCache.getSql("GET_TODAY_SIGNUPS");
        return this.jdbcTemplate.queryForObject(query, Long.class);
    }

    public List<Long> getUsersSignedUp(Date startDate, Date endDate,
            List<Long> userIDs, List<String> userCategories) {
        String query;
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("startDate", startDate);
        values.put("endDate", endDate);
        values.put("userIDs", userIDs);
        if (userCategories == null) {
            query = SQLCache.getSql("GET_SIGNUPS_FROM_LIST");
        }
        else {
            values.put("userCategories", userCategories);
            query = SQLCache.getSql("GET_SIGNUPS_BY_USERCATEGORY_FROM_LIST");
        }
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(
                values);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
                jdbcTemplate.getDataSource());

        List<Long> users = namedParameterJdbcTemplate.query(query, values,
                new RowMapper<Long>() {

                    @Override
                    public Long mapRow(ResultSet rs, int row)
                            throws SQLException {
                        return rs.getLong("user_id");

                    }
                });
        return users;
    }
}
