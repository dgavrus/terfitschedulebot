import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.sun.org.apache.regexp.internal.RE;
import data.ClubsHolder;
import data.Event;
import data.HtmlParser;
import data.users.Remember;
import data.users.UserProperties;
import data.users.UsersHolder;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class TerfitBot extends TelegramLongPollingBot{

    private static UsersHolder usersHolder;
    private static ClubsHolder clubsHolder;
    private static Map<String, Integer> keyWords;

    public static void main(String[] args) throws IOException {
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();
        usersHolder = new UsersHolder();
        clubsHolder = new ClubsHolder();

        try {
            botsApi.registerBot(new TerfitBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

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
        if(text.equals("Сменить клуб")){
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
                sendMessage.setReplyMarkup(rkm);
                rkm.setKeyboard(Arrays.stream(clubsHolder.clubsString())
                        .map(s -> {
                            KeyboardRow keyboardRow = new KeyboardRow();
                            keyboardRow.add(new KeyboardButton(s));
                            return keyboardRow;
                        }).collect(Collectors.toList()));
                sendMessage.setText("Выберите клуб:");
                userProperties.setState(userProperties.getRemember() != Remember.YES ? 2 : 4);
                break;
            case 2:
                userProperties.setClub(text);
                sendMessage.setReplyMarkup(rkm);
                rkm.setKeyboard(Arrays.stream(Remember.values())
                        .map(s -> {
                            KeyboardRow keyboardRow = new KeyboardRow();
                            keyboardRow.add(new KeyboardButton(s.getString()));
                            return keyboardRow;
                        }).collect(Collectors.toList()));
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
                sendMessage.setReplyMarkup(rkm);
                rkm.setKeyboard(Collections.singletonList(new KeyboardRow() {{
                    add(new KeyboardButton("Сегодня"));
                    add(new KeyboardButton("Завтра"));
                }}));
                sendMessage.setText("Выберите день или занятие:");
                userProperties.incState();
                break;
            case 5:
                HtmlParser parser = new HtmlParser(clubsHolder.getClub(userProperties.getClub()));
                try {
                    Collection<Event> classes;
                    if(text.equals("Сегодня")){
                        classes = parser.today();
                    } else if(text.equals("Завтра")){
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
                        sendMessage.setReplyMarkup(rkm);
                        rkm.setKeyboard(Arrays.stream(clubsHolder.clubsString())
                                .map(s -> {
                                    KeyboardRow keyboardRow = new KeyboardRow();
                                    keyboardRow.add(new KeyboardButton(s));
                                    return keyboardRow;
                                }).collect(Collectors.toList()));
                        sendMessage.setText("Выберите клуб:");
                        userProperties.setState(2);
                    } else {
                        rkm.setKeyboard(Arrays.asList(new KeyboardRow() {{
                                                          add(new KeyboardButton("Сегодня"));
                                                          add(new KeyboardButton("Завтра"));
                                                      }},
                                        new KeyboardRow() {{
                                            add(new KeyboardButton("Сменить клуб"));
                                        }})
                        );
                        sendMessage.setReplyMarkup(rkm);
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
