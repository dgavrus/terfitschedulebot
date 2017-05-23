package data;

import java.util.Optional;

import static java.lang.System.*;

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
        sb.append(name).append(lineSeparator());
        sb.append(time).append(lineSeparator());
        sb.append(coach).append(lineSeparator());
        sb.append(room).append(Optional.of(room)
                                    .filter(String::isEmpty)
                                    .map(s -> "")
                                    .orElse(lineSeparator()));
        return sb.toString();
    }
}
