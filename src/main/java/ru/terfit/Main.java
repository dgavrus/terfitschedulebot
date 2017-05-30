package ru.terfit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;

import java.io.IOException;

public class Main {

    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws IOException {
        ApiContextInitializer.init();
        new AnnotationConfigApplicationContext(Config.class);
        logger.info("Context initialization...");
    }
}