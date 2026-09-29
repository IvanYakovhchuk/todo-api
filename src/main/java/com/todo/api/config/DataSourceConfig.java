package com.todo.api.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties props = new DataSourceProperties();
        String databaseUrl = System.getenv("DATABASE_URL");
        if (StringUtils.hasText(databaseUrl)) {
            applyDatabaseUrl(props, databaseUrl.trim());
        } else {
            props.setUrl("jdbc:h2:file:./data/todos;DB_CLOSE_DELAY=-1;AUTO_SERVER=FALSE");
            props.setDriverClassName("org.h2.Driver");
            props.setUsername("sa");
            props.setPassword("");
        }
        return props;
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    private static void applyDatabaseUrl(DataSourceProperties props, String databaseUrl) {
        try {
            String normalized = databaseUrl;
            if (normalized.startsWith("postgres://") || normalized.startsWith("postgresql://")) {
                URI uri = new URI(normalized.replaceFirst("^postgres(ql)?://", "http://"));
                String userInfo = uri.getUserInfo();
                if (userInfo != null) {
                    String[] parts = userInfo.split(":", 2);
                    props.setUsername(parts[0]);
                    if (parts.length > 1) {
                        props.setPassword(parts[1]);
                    }
                }
                String path = uri.getPath() != null ? uri.getPath() : "";
                String query = uri.getQuery() != null ? "?" + uri.getQuery() : "";
                // Neon/Render usually need SSL
                if (!query.contains("sslmode=")) {
                    query = query.isEmpty() ? "?sslmode=require" : query + "&sslmode=require";
                }
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                props.setUrl("jdbc:postgresql://" + uri.getHost() + ":" + port + path + query);
                props.setDriverClassName("org.postgresql.Driver");
            } else if (normalized.startsWith("jdbc:")) {
                props.setUrl(normalized);
            } else {
                throw new IllegalArgumentException("Unsupported DATABASE_URL format");
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid DATABASE_URL", e);
        }
    }
}
