import com.sun.xml.internal.ws.util.StringUtils;
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

import java.util.Arrays;
import java.util.stream.Collectors;

public class TerfitBot extends TelegramLongPollingBot{

    public static void main(String[] args) {
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new TerfitBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        String text = message.getText().toLowerCase();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        if(!text.equalsIgnoreCase("лох") && !text.equalsIgnoreCase("пидр")){
            ReplyKeyboardMarkup rkm = new ReplyKeyboardMarkup();
            rkm.setKeyboard(Arrays.stream(new String[]{"Лох", "Пидр"}).map(s -> {
                KeyboardRow keyboardRow = new KeyboardRow();
                keyboardRow.add(new KeyboardButton(s));
                return keyboardRow;
            }).collect(Collectors.toList()));
            sendMessage.setReplyMarkup(rkm);
            sendMessage.setText("Лох или пидр?");
        } else {
            sendMessage.setText(StringUtils.capitalize(text));
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
