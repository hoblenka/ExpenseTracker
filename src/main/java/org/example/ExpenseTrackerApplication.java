package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExpenseTrackerApplication {
    public static void main(String[] args) {
        // Load .env file with security filtering
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> {
            String key = entry.getKey();
            if (key.startsWith("DB_") || key.startsWith("SPRING_")) {
                System.setProperty(key, entry.getValue());
            }
        });
        
        SpringApplication.run(ExpenseTrackerApplication.class, args);
    }
}