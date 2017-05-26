package ru.terfit.data;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class ClubsHolder {

    private final Map<String, String> clubs;

    public ClubsHolder(){
        clubs = ImmutableMap.<String, String>builder()
                .put("Чебоксары", "cheboksary")
                .put("Самара", "samara")
                .put("Курск", "kursk")
                .put("Авиамоторная", "aviamotornaya")
                .put("Балашиха", "balashiha")
                .put("Братиславская", "bratislavskaya")
                .put("Жулебино", "zhulebino")
                .put("Люберцы", "lyubertsy")
                .put("Новокосино", "novokosino")
                .put("Печатники", "pechatniki")
                .put("Пражская", "prazhskaya")
                .put("Сходненская", "skhodnenskaya").build();
    }

    public Collection<String> clubsString(){
        return clubs.keySet();
    }

    public String getClub(String clubString){
        return clubs.get(clubString);
    }

}
