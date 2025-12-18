package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExpenseTrackerApplication {
    public static void main(String[] args) {
        // Load .env file with security filtering
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().stream()
                .filter(entry -> entry.getKey().startsWith("DB_") || entry.getKey().startsWith("SPRING_"))
                .forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        
        SpringApplication.run(ExpenseTrackerApplication.class, args);
    }
}