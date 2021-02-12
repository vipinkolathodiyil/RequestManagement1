package com.sample.reqmanagement.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
//@PropertySource("file:config/hibernate-config.properties")
@PropertySource("classpath:hibernate-config.properties")
public class HibernateConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(HibernateConfiguration.class);
    @Autowired
    private Environment env;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[] { "com.sample.reqmanagement" });

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(getJPAProperties());
        return em;
    }

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(env.getProperty("spring.datasource.hikari.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.hikari.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.hikari.password"));
        dataSource.setMaximumPoolSize(Integer.valueOf(env.getProperty("spring.datasource.hikari.maxPoolSize")));
        dataSource.setMaxLifetime(Long.valueOf(env.getProperty("spring.datasource.hikari.maxLifetimeInMillis")));
        return dataSource;
    }

    private Properties getJPAProperties() {
        Properties jpaProps = new Properties();
        jpaProps.put("hibernate.dialect", env.getProperty("spring.jpa.hibernate.dialect"));
        jpaProps.put("hibernate.show_sql", Boolean.valueOf(env.getProperty("spring.jpa.hibernate.show_sql")));
        return jpaProps;
    }
}
