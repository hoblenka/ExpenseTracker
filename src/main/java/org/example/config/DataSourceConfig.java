package org.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);
    
    @Bean
    public DataSource dataSource(@Autowired VaultConfig vault,
                                @Value("${spring.datasource.url}") String url,
                                @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}") String driver) {
        try {
            if (vault == null) {
                throw new IllegalArgumentException("VaultConfig cannot be null");
            }
            logger.info("Creating DataSource");
            return getHikariDataSource(vault, url, driver);
        } catch (IllegalStateException e) {
            logger.error("Configuration not properly initialized: {}", e.getMessage());
            throw new RuntimeException("DataSource configuration failed: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid DataSource configuration: {}", e.getMessage());
            throw new RuntimeException("DataSource configuration failed", e);
        } catch (Exception e) {
            logger.error("Unexpected error creating DataSource: {}", e.getMessage(), e);
            throw new RuntimeException("DataSource creation failed", e);
        }
    }

    private static HikariDataSource getHikariDataSource(VaultConfig vault, String url, String driver) {
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("Database URL is not configured");
        }
        String username = vault.getUsername();
        String password = vault.getPassword();
        if (username == null || username.isBlank()) {
            throw new IllegalStateException("Username is not configured");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalStateException("Password is not configured");
        }
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driver);
        dataSource.setMaximumPoolSize(10);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        return dataSource;
    }
}