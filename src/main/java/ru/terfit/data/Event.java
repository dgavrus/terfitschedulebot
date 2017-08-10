package ru.terfit.data;

import java.util.Optional;

import static java.lang.System.lineSeparator;

public class Event {

    private final String name;
    private final String time;
    private final String coach;
    private final String room;

    public Event(String time, String name, String coach, String room){
        this.name = name;
        this.time = time;
        this.coach = coach;
        this.room = room;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getCoach() {
        return coach;
    }

    public String getRoom() {
        return room;
    }

    public String print(){
        StringBuilder sb = new StringBuilder();
        if(name != null) sb.append(name).append(lineSeparator());
        if(time != null) sb.append(time).append(lineSeparator());
        if(coach != null) sb.append(coach).append(lineSeparator());
        if(room != null) sb.append(room).append(Optional.of(room)
                                    .filter(String::isEmpty)
                                    .map(s -> "")
                                    .orElse(lineSeparator()));
        return sb.toString();
    }

    public static final Event EMPTY_DAY = new Event(null, "Сегодня уже ничего не будет", null, null);
}
