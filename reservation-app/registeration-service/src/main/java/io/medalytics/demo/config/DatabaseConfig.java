package io.medalytics.demo.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.data-username}") String username;
    @Value("${spring.datasource.data-password}") String password;
    @Value("${spring.datasource.url}") String jdbcUrl;
    @Value("${spring.datasource.driver-class-name}") String driver;

    @Bean
    public HikariDataSource hikariDataSource() {
        return new HikariDataSource(hikariConfig());
    }

    @Bean
    public HikariConfig hikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setDriverClassName(driver);
        config.setMaximumPoolSize(20);
        config.setUsername(username);
        config.setPassword(password);

        return config;
    }
}
