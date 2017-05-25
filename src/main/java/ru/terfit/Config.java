package ru.terfit;

import org.springframework.context.annotation.*;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created by dgavrus on 25.05.17.
 */

@Configuration
@ComponentScan(basePackageClasses = Config.class)
public class Config {

    @Inject
    private TerfitBot terfitBot;

    @PostConstruct
    public void terfitBot() throws TelegramApiRequestException {
        TelegramBotsApi botsApi = new TelegramBotsApi();
        botsApi.registerBot(terfitBot);
    }

}
