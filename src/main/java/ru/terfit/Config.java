package ru.terfit;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.springframework.context.annotation.*;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import ru.terfit.data.users.UserProperties;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;

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

    private final static String DATABASE_URL = "jdbc:h2:~/tbusers/tbusers";

    @Bean
    public Dao<UserProperties, Integer> userPropertiesDao() {
        ConnectionSource connectionSource = null;
        try {
            connectionSource = new JdbcConnectionSource(DATABASE_URL);
            TableUtils.createTableIfNotExists(connectionSource, UserProperties.class);
            return DaoManager.createDao(connectionSource, UserProperties.class);
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally {
            if (connectionSource != null) {
                try {
                    connectionSource.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
