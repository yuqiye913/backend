package com.programming.techie.springredditclone.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.net.URI;

@Configuration
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "railway")
public class RailwayDatabaseConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @PostConstruct
    public void configureDatabaseUrl() {
        if (databaseUrl != null && !databaseUrl.isEmpty() && databaseUrl.startsWith("postgresql://")) {
            try {
                // Parse Railway's DATABASE_URL format: postgresql://user:password@host:port/database
                URI uri = new URI(databaseUrl.replace("postgresql://", "http://"));
                
                String host = uri.getHost();
                int port = uri.getPort();
                String database = uri.getPath().substring(1); // Remove leading slash
                String userInfo = uri.getUserInfo();
                
                if (userInfo != null) {
                    String[] userPass = userInfo.split(":");
                    String username = userPass[0];
                    String password = userPass.length > 1 ? userPass[1] : "";
                    
                    // Set system properties that Spring Boot will use
                    System.setProperty("spring.datasource.url", 
                        String.format("jdbc:postgresql://%s:%d/%s", host, port, database));
                    System.setProperty("spring.datasource.username", username);
                    System.setProperty("spring.datasource.password", password);
                    
                    System.out.println("✅ Railway DATABASE_URL parsed successfully");
                    System.out.println("Host: " + host);
                    System.out.println("Port: " + port);
                    System.out.println("Database: " + database);
                    System.out.println("Username: " + username);
                }
            } catch (Exception e) {
                System.err.println("❌ Failed to parse Railway DATABASE_URL: " + e.getMessage());
            }
        }
    }
}
