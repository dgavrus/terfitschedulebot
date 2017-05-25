package ru.terfit;

import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Component;
import ru.terfit.data.ClubsHolder;
import ru.terfit.data.Constants;
import ru.terfit.data.Event;
import ru.terfit.data.HtmlParser;
import ru.terfit.data.users.Keyboards;
import ru.terfit.data.users.Remember;
import ru.terfit.data.users.UserProperties;
import ru.terfit.data.users.UsersHolder;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

import static ru.terfit.data.Constants.*;
import static ru.terfit.data.users.Keyboards.*;


@Component
public class TerfitBot extends TelegramLongPollingBot {

    @Inject
    private UsersHolder usersHolder;
    @Inject
    private ClubsHolder clubsHolder;
    @Inject
    private Keyboards keyboards;

    public void onUpdateReceived(Update update) {
        Integer id = update.getMessage().getFrom().getId();
        UserProperties userProperties;
        if(usersHolder.hasUserProperties(id)){
            userProperties = usersHolder.getUserProperties(id);
        } else {
            userProperties = new UserProperties();
            usersHolder.putUserProperties(id, userProperties);
        }
        Message message = update.getMessage();
        String text = message.getText();
        if(text.equals(CHANGE_CLUB)){
            userProperties.setState(1);
            userProperties.setRemember(Remember.NOT_NOW);
        }
        int state = userProperties.getState();

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(message.getChatId());
        ReplyKeyboardMarkup rkm = new ReplyKeyboardMarkup();
        rkm.setResizeKeyboard(true);
        rkm.setOneTimeKeyboard(true);

        switch (state){
            case 0:
            case 1:
                sendMessage.setReplyMarkup(keyboards.get(CLUBS));
                sendMessage.setText("Выберите клуб:");
                userProperties.setState(userProperties.getRemember() != Remember.YES ? 2 : 4);
                break;
            case 2:
                userProperties.setClub(text);
                sendMessage.setReplyMarkup(keyboards.get(REMEMBER));
                sendMessage.setText("Запомнить выбор?");
                userProperties.incState();
                break;
            case 3:
                userProperties.setRemember(Arrays.stream(Remember.values())
                        .filter(r -> r.getString().equals(text))
                        .findFirst()
                        .orElse(Remember.NOT_NOW));
                userProperties.incState();
            case 4:
                sendMessage.setReplyMarkup(keyboards.get(DAYS_CLASSES));
                sendMessage.setText("Выберите день или занятие:");
                userProperties.incState();
                break;
            case 5:
                HtmlParser parser = new HtmlParser(clubsHolder.getClub(userProperties.getClub()));
                try {
                    Collection<Event> classes;
                    if(text.equals(TODAY)){
                        classes = parser.today();
                    } else if(text.equals(TOMORROW)){
                        classes = parser.tomorrow();
                    } else {
                        classes = ImmutableList.of();
                        sendMessage.setText("Незнакомое мне что-то");
                    }

                    classes.forEach(s -> {
                        try {
                            sendMessage(new SendMessage(update.getMessage().getChatId(), s.print()));
                            System.out.println(s.print());
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    });

                    if(userProperties.getRemember() != Remember.YES){
                        sendMessage.setReplyMarkup(keyboards.get(CLUBS));
                        sendMessage.setText("Выберите клуб:");
                        userProperties.setState(2);
                    } else {
                        sendMessage.setReplyMarkup(keyboards.get(DAYS_CLASSES_CHANGE_CLUB));
                        sendMessage.setText("Выберите действие:");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return "terfitschedulebot";
    }

    public String getBotToken() {
        return "344669862:AAFlU5rcRUDrh2szJy440E2vW5ROzgeUA5k";
    }
}