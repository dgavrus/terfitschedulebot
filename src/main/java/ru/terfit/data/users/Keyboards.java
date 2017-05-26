package ru.terfit.data.users;

import com.google.common.collect.ImmutableMap;
import ru.terfit.data.ClubsHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
                .put(CLUBS, makeKeyboard(clubsHolder.clubsString(), 4))
                .put(REMEMBER, new ReplyKeyboardMarkup()
                        .setKeyboard(Arrays.stream(Remember.values())
                            .map(s -> {
                                KeyboardRow keyboardRow = new KeyboardRow();
                                keyboardRow.add(s.getString());
                                return keyboardRow;
                            }).collect(Collectors.toList()))
                        .setResizeKeyboard(true)
                        .setOneTimeKeyboard(true))
                .put(DAYS_CLASSES, new ReplyKeyboardMarkup()
                        .setKeyboard(Collections.singletonList(new KeyboardRow() {{
                            add(TODAY);
                            add(TOMORROW);
                        }}))
                        .setResizeKeyboard(true)
                        .setOneTimeKeyboard(true))
                .put(DAYS_CLASSES_CHANGE_CLUB, new ReplyKeyboardMarkup()
                    .setKeyboard(Arrays.asList(new KeyboardRow() {{
                            add(TODAY);
                            add(TOMORROW);
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

    public ReplyKeyboardMarkup makeKeyboard(Collection<String> buttons, int rows){
        int buttonsPerRow = buttons.size() / rows;
        int mod = buttons.size() % rows;
        List<KeyboardRow> keyboarsRows = IntStream.range(0, rows).mapToObj(i -> new KeyboardRow()).collect(Collectors.toList());
        Iterator<String> buttonsIterator = buttons.iterator();
        for(int i = 0; i < keyboarsRows.size(); i++){
            int cb = buttonsPerRow;
            if(i + mod >= rows) cb++;
            KeyboardRow kr = keyboarsRows.get(i);
            for(int j = 0; j < cb; j++){
                kr.add(buttonsIterator.next());
            }
        }
        ReplyKeyboardMarkup rkm = new ReplyKeyboardMarkup();
        rkm.setKeyboard(keyboarsRows);
        rkm.setResizeKeyboard(true);
        rkm.setOneTimeKeyboard(true);
        return rkm;
    }

}
