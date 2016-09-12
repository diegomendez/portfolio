package com.wideo.metrics.persistence.jdbc;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

public abstract class AbstractJdbcTemplate {

    protected JdbcTemplate jdbcTemplate;
    protected TransactionTemplate txTemplate;
    protected Logger logger = Logger.getLogger(this.getClass());

    @Required
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Required
    public void setTransactionManager(PlatformTransactionManager txManager) {
        this.txTemplate = new TransactionTemplate(txManager);
        this.txTemplate
                .setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
    }

    public Logger getLogger() {
        return this.logger;
    }

}
