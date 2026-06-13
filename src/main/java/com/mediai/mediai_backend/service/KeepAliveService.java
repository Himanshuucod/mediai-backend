package com.mediai.mediai_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class KeepAliveService {

    @Value("${app.url:http://localhost:8080}")
    private String appUrl;

    @Scheduled(fixedDelay = 840000) // every 14 minutes
    public void keepAlive() {
        try {
            URL url = new URL(appUrl + "/api/health");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.getResponseCode();
            connection.disconnect();
            System.out.println("✅ Keep alive ping sent");
        } catch (Exception e) {
            System.out.println("⚠️ Keep alive failed: " + e.getMessage());
        }
    }
}