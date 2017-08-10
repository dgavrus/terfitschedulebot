package ru.terfit;

import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.terfit.data.ClubsHolder;
import ru.terfit.data.Event;
import ru.terfit.data.ScheduleCache;
import ru.terfit.data.State;
import ru.terfit.data.users.*;

import javax.inject.Inject;
import java.time.MonthDay;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.terfit.data.Constants.*;
import static ru.terfit.data.State.*;
import static ru.terfit.data.Utils.DTF;
import static ru.terfit.data.users.Keyboards.CLUBS;
import static ru.terfit.data.users.Keyboards.REMEMBER;


@Component
public class TerfitBot extends TelegramLongPollingBot {

    private static Logger logger = LogManager.getLogger();

    @Inject
    private UsersHolder usersHolder;
    @Inject
    private ClubsHolder clubsHolder;
    @Inject
    private Keyboards keyboards;
    @Inject
    private ScheduleCache scheduleCache;

    public void onUpdateReceived(Update update) {
        Integer id = update.getMessage().getFrom().getId();
        UserProperties userProperties;
        if(usersHolder.hasUserProperties(id)){
            userProperties = usersHolder.getUserProperties(id);
        } else {
            userProperties = new UserProperties(id);
            usersHolder.putUserProperties(id, userProperties);
        }
        Message message = update.getMessage();
        String text = message.getText();
        if(Put.getPut().getId() == id && text.toLowerCase().contains("привет")){
            try {
                sendMessage(new SendMessage(message.getChatId(), "Привет, Пут"));
            } catch (TelegramApiException e) {
                logger.error(e);
            }
        }
        logger.info("Message {} from {}", text, userProperties.toString());
        if(text.equals(CHANGE_CLUB)){
            userProperties.setState(CHOOSE_CLUB);
            userProperties.setRemember(Remember.NOT_NOW);
        }
        State state = userProperties.getState();

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(message.getChatId());

        switch (state){
            case START:
            case CHOOSE_CLUB:
                sendMessage.setReplyMarkup(keyboards.makeKeyboard(clubsHolder.clubsString(), 4));
                sendMessage.setText("Выберите клуб:");
                userProperties.setState(userProperties.getRemember() != Remember.YES ? CHOOSE_REMEMBER : CHOOSE_DAY_CLASS);
                break;
            case CHOOSE_REMEMBER:
                if(!clubsHolder.hasClub(text)){
                    sendMessage.setReplyMarkup(keyboards.makeKeyboard(clubsHolder.clubsString(), 4));
                    sendMessage.setText("Выберите клуб:");
                    break;
                }
                userProperties.setClub(text);
                sendMessage.setReplyMarkup(keyboards.get(REMEMBER));
                sendMessage.setText("Запомнить выбор?");
                userProperties.incState();
                break;
            case SET_REMEMBER:
                userProperties.setRemember(Arrays.stream(Remember.values())
                        .filter(r -> r.getString().equals(text))
                        .findFirst()
                        .orElse(Remember.NOT_NOW));
                userProperties.incState();
            case CHOOSE_DAY_CLASS:
                List<String> days = scheduleCache.availableDays(userProperties.getClub());
                sendMessage.setReplyMarkup(keyboards.makeKeyboard(days,  days.size() / 2 + days.size() % 2));
                sendMessage.setText("Выберите день или занятие:");
                userProperties.incState();
                break;
            case GET_SCHEDULE:
                Collection<Event> classes;
                days = scheduleCache
                        .forClub(userProperties.getClub()).keySet().stream()
                        .map(md -> md.format(DTF))
                        .collect(Collectors.toList());
                if(text.equals(TODAY)){
                    classes = Optional.of(scheduleCache.today(userProperties.getClub()))
                        .filter(l -> !l.isEmpty())
                            .orElse(ImmutableList.of(Event.EMPTY_DAY));
                } else if(text.equals(TOMORROW)){
                    classes = scheduleCache.tomorrow(userProperties.getClub());
                } else {
                    if(days.stream().anyMatch(d -> d.equals(text))){
                        classes = scheduleCache.forClub(userProperties.getClub())
                                .get(MonthDay.parse(text, DTF));
                    } else {
                        classes = ImmutableList.of();
                    }
                }

                classes.forEach(s -> {
                    try {
                        sendMessage(new SendMessage(update.getMessage().getChatId(), s.print()));
                    } catch (TelegramApiException e) {
                        logger.error("{} {} {}", text, userProperties.toString(), e);
                    }
                });

                if(userProperties.getRemember() != Remember.YES){
                    sendMessage.setReplyMarkup(keyboards.get(CLUBS));
                    sendMessage.setText("Выберите клуб:");
                    userProperties.setState(CHOOSE_REMEMBER);
                } else {
                    days = scheduleCache.availableDays(userProperties.getClub());
                    ReplyKeyboardMarkup keyboard = keyboards.makeKeyboard(days,  days.size() / 2 + days.size() % 2);
                    KeyboardRow changeClub = new KeyboardRow();
                    changeClub.add(CHANGE_CLUB);
                    keyboards.addRow(keyboard, changeClub);
                    sendMessage.setReplyMarkup(keyboard);
                    sendMessage.setText("Выберите действие:");
                }
        }
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            logger.error("{} {} {}", text, userProperties.toString(), e);;
        }
        usersHolder.updateUserProperties(userProperties.getId(), userProperties);
    }

    public String getBotUsername() {
        return "terfitschedulebot";
    }

    public String getBotToken() {
        return "344669862:AAFlU5rcRUDrh2szJy440E2vW5ROzgeUA5k";
    }
}
