package ru.terfit.data.users;


import com.google.common.collect.ImmutableMap;
import com.sun.org.apache.regexp.internal.RE;
import ru.terfit.data.ClubsHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.terfit.data.Constants;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.terfit.data.Constants.*;

@Component
public class Keyboards {

    private Map<String, ReplyKeyboardMarkup> keyboards;

    @Autowired
    private ClubsHolder clubsHolder;

    public static final String CLUBS = "clubs";
    public static final String REMEMBER = "remember";
    public static final String DAYS_CLASSES = "days_classes";
    public static final String DAYS_CLASSES_CHANGE_CLUB = "days_classes_change_club";

    @PostConstruct
    public void keyboards(){
        ImmutableMap.Builder<String, ReplyKeyboardMarkup> builder = ImmutableMap.<String, ReplyKeyboardMarkup>builder()
                .put(CLUBS, new ReplyKeyboardMarkup()
                        .setKeyboard(Arrays.stream(clubsHolder.clubsString())
                            .map(s -> {
                                KeyboardRow keyboardRow = new KeyboardRow();
                                keyboardRow.add(new KeyboardButton(s));
                                return keyboardRow;
                            }).collect(Collectors.toList()))
                        .setResizeKeyboard(true)
                        .setOneTimeKeyboard(true))
                .put(REMEMBER, new ReplyKeyboardMarkup()
                        .setKeyboard(Arrays.stream(Remember.values())
                            .map(s -> {
                                KeyboardRow keyboardRow = new KeyboardRow();
                                keyboardRow.add(new KeyboardButton(s.getString()));
                                return keyboardRow;
                            }).collect(Collectors.toList()))
                        .setResizeKeyboard(true)
                        .setOneTimeKeyboard(true))
                .put(DAYS_CLASSES, new ReplyKeyboardMarkup()
                        .setKeyboard(Collections.singletonList(new KeyboardRow() {{
                            add(new KeyboardButton(TODAY));
                            add(new KeyboardButton(TOMORROW));
                        }}))
                        .setResizeKeyboard(true)
                        .setOneTimeKeyboard(true))
                .put(DAYS_CLASSES_CHANGE_CLUB, new ReplyKeyboardMarkup()
                    .setKeyboard(Arrays.asList(new KeyboardRow() {{
                            add(new KeyboardButton(TODAY));
                            add(new KeyboardButton(TOMORROW));
                        }},
                        new KeyboardRow(){{
                            add(CHANGE_CLUB);
                        }}))
                    .setResizeKeyboard(true)
                    .setOneTimeKeyboard(true));
        keyboards = builder.<String, ReplyKeyboardMarkup>build();
    }


    public ReplyKeyboardMarkup get(String keyboard){
        return keyboards.get(keyboard);
    };

}
