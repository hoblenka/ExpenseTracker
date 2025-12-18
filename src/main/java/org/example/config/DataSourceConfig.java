package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    
    @Bean
    public DataSource dataSource(VaultConfig vault, 
                                @Value("${spring.datasource.url}") String url,
                                @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}") String driver) {
        return DataSourceBuilder.create()
                .url(url)
                .username(vault.getUsername())
                .password(vault.getPassword())
                .driverClassName(driver)
                .build();
    }
}