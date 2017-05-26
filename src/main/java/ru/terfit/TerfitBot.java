package ru.terfit;

import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Component;
import ru.terfit.data.*;
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
import java.time.Month;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static ru.terfit.data.Constants.*;
import static ru.terfit.data.State.*;
import static ru.terfit.data.users.Keyboards.*;


@Component
public class TerfitBot extends TelegramLongPollingBot {

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
        if(text.equals(CHANGE_CLUB)){
            userProperties.setState(CHOOSE_CLUB);
            userProperties.setRemember(Remember.NOT_NOW);
        }
        State state = userProperties.getState();

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(message.getChatId());
        ReplyKeyboardMarkup rkm = new ReplyKeyboardMarkup();
        rkm.setResizeKeyboard(true);
        rkm.setOneTimeKeyboard(true);

        switch (state){
            case START:
            case CHOOSE_CLUB:
                //sendMessage.setReplyMarkup(keyboards.get(CLUBS));
                sendMessage.setReplyMarkup(keyboards.makeKeyboard(clubsHolder.clubsString(), 4));
                sendMessage.setText("Выберите клуб:");
                userProperties.setState(userProperties.getRemember() != Remember.YES ? CHOOSE_REMEMBER : CHOOSE_DAY_CLASS);
                break;
            case CHOOSE_REMEMBER:
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
                //sendMessage.setReplyMarkup(keyboards.get(DAYS_CLASSES));
                List<String> days = scheduleCache
                        .forClub(userProperties.getClub()).keySet().stream()
                        .map(md -> md.format(DateTimeFormatter.ofPattern("dd.MM")))
                        .collect(Collectors.toList());
                days = Stream.concat(days.stream().limit(1).map(s -> TODAY),
                        Stream.concat(days.stream().limit(2).skip(1).map(s -> TOMORROW), days.stream().skip(2))).collect(Collectors.toList());
                sendMessage.setReplyMarkup(keyboards.makeKeyboard(days,  days.size() / 2 + days.size() % 2));
                sendMessage.setText("Выберите день или занятие:");
                userProperties.incState();
                break;
            case GET_SCHEDULE:
                HtmlParser parser = new HtmlParser(clubsHolder.getClub(userProperties.getClub()));
                try {
                    Collection<Event> classes;
                    days = scheduleCache
                            .forClub(userProperties.getClub()).keySet().stream()
                            .map(md -> md.format(DateTimeFormatter.ofPattern("dd.MM")))
                            .collect(Collectors.toList());
                    if(text.equals(TODAY)){
                        classes = parser.today();
                    } else if(text.equals(TOMORROW)){
                        classes = parser.tomorrow();
                    } else {
                        if(days.stream().anyMatch(d -> d.equals(text))){
                            classes = scheduleCache.forClub(userProperties.getClub())
                                    .get(MonthDay.parse(text, DateTimeFormatter.ofPattern("dd.MM")));
                        } else {
                            classes = ImmutableList.of();
                            sendMessage.setText("Незнакомое мне что-то");
                        }
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
                        userProperties.setState(CHOOSE_REMEMBER);
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
        usersHolder.updateUserProperties(userProperties.getId(), userProperties);
    }

    public String getBotUsername() {
        return "terfitschedulebot";
    }

    public String getBotToken() {
        return "344669862:AAFlU5rcRUDrh2szJy440E2vW5ROzgeUA5k";
    }
}
