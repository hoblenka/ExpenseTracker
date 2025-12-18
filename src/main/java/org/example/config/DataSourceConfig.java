package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);
    
    @Bean
    public DataSource dataSource(VaultConfig vault,
                                @Value("${spring.datasource.url}") String url,
                                @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}") String driver) {
        try {
            logger.info("Creating DataSource with URL: {}", url);
            return DataSourceBuilder.create()
                    .url(url)
                    .username(vault.getUsername())
                    .password(vault.getPassword())
                    .driverClassName(driver)
                    .build();
        } catch (Exception e) {
            logger.error("Failed to create DataSource: {}", e.getMessage());
            throw new RuntimeException("DataSource configuration failed", e);
        }
    }
}