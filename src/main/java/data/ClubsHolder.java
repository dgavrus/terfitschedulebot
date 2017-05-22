package data;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

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

    public String[] clubsString(){
        return clubs.keySet().toArray(new String[clubs.size()]);
    }

    public String getClub(String clubString){
        return clubs.get(clubString);
    }

}
