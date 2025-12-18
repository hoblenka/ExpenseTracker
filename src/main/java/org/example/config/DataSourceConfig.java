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
            if (vault == null) {
                throw new IllegalArgumentException("VaultConfig cannot be null");
            }
            logger.info("Creating DataSource with URL: {}", url);
            String username = vault.getUsername();
            String password = vault.getPassword();
            return DataSourceBuilder.create()
                    .url(url)
                    .username(username)
                    .password(password)
                    .driverClassName(driver)
                    .build();
        } catch (IllegalStateException e) {
            logger.error("Configuration not properly initialized: {}", e.getMessage());
            throw new RuntimeException("DataSource configuration failed: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid configuration: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to create DataSource: {}", e.getMessage());
            throw new RuntimeException("DataSource configuration failed", e);
        }
    }
}