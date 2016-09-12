package com.wideo.metrics.dal.mysql;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.wideo.metrics.commons.sqlcache.SQLCache;
import com.wideo.metrics.model.processor.stats.DashboardStats;
import com.wideo.metrics.persistence.jdbc.AbstractJdbcTemplate;

public class WideoosDal extends AbstractJdbcTemplate implements WideoosDalMySqlInterface {

    @Override
    public int getCreatedWideos(Date startDate, Date endDate, List<String> userCategories) {
        String query;
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("startDate", startDate);
        values.put("endDate", endDate);
        if (userCategories != null) {
            values.put("userCategories", userCategories);
            query = SQLCache.getSql("GET_CREATED_WIDEOOS_BY_DATERANGE_USERCATEGORIES_QUERY");
        } else {
            query = SQLCache.getSql("GET_CREATED_WIDEOOS_BY_DATERANGE_QUERY");
        }
        
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(
                values);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
                jdbcTemplate.getDataSource());
        
        return namedParameterJdbcTemplate.queryForObject(query, values, Integer.class);
    }

    public List<DashboardStats> getBestWideos(Date startDate, Date endDate,
            int count) {
        String query = SQLCache.getSql("GET_BEST_WIDEOS_BY_DATERANGE_QUERY");
        Object[] params = { startDate, endDate, count};
        List<DashboardStats> results = this.jdbcTemplate.query(query, params,
                new RowMapper<DashboardStats>() {
                    @Override
                    public DashboardStats mapRow(ResultSet rs, int rowNum) {
                        try {
                            String wideoID = rs.getString("wideoo_id");
                            Double wideoScore = rs.getDouble("score");
                            return new DashboardStats(wideoID,
                                    wideoScore);
                        }
                        catch (Exception e) {
                            return null;
                        }
                    }
                });
        return results;
    }

}
