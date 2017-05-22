import com.sun.xml.internal.ws.util.StringUtils;
import data.ClubsHolder;
import data.HtmlParser;
import data.users.Remember;
import data.users.UserProperties;
import data.users.UsersHolder;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TerfitBot extends TelegramLongPollingBot{

    private static UsersHolder usersHolder;
    private static ClubsHolder clubsHolder;

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
        int state = userProperties.getState();

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(message.getChatId());
        ReplyKeyboardMarkup rkm = new ReplyKeyboardMarkup();

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
                userProperties.setState(userProperties.getRemember() != Remember.YES ? 2 : 3);
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
                sendMessage.setReplyMarkup(rkm);
                rkm.setKeyboard(Collections.singletonList(new KeyboardRow() {{
                    add(new KeyboardButton("Сегодня"));
                }}));
                sendMessage.setText("Выберите день или занятие:");
                userProperties.incState();
                break;
            case 4:
                HtmlParser parser = new HtmlParser(clubsHolder.getClub(userProperties.getClub()));
                try {
                    sendMessage.setText(parser.loadPage());
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
