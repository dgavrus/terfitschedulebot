package ru.terfit;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        ApiContextInitializer.init();
        new AnnotationConfigApplicationContext(Config.class);

    }

}