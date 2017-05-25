package ru.terfit.data.users;

import ru.terfit.data.State;

import static ru.terfit.data.State.*;

public class UserProperties {

    private String club;
    private Remember remember;
    private State state = START;

    public UserProperties(){
        this(null, Remember.NOT_NOW);
    }

    public UserProperties(String club){
        this(club, Remember.NOT_NOW);
    }

    public UserProperties(String club, Remember remember){
        this.club = club;
        this.remember = remember;
        this.state = START;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public Remember getRemember() {
        return remember;
    }

    public void setRemember(Remember remember) {
        this.remember = remember;
    }

    public State getState(){
        return state;
    }

    public State incState(){
        state = state.ordinal() < State.values().length - 1
                ? State.values()[state.ordinal() + 1] :
                state;
        return state;
    }

    public void setState(State state){
        this.state = state;
    }
}
