package ru.terfit.data.users;

public class UserProperties {

    private String club;
    private Remember remember;
    private int state = 0;

    public UserProperties(){
        this(null, Remember.NOT_NOW);
    }

    public UserProperties(String club){
        this(club, Remember.NOT_NOW);
    }

    public UserProperties(String club, Remember remember){
        this.club = club;
        this.remember = remember;
        this.state = 0;
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

    public int getState(){
        return state;
    }

    public int incState(){
        return ++state;
    }

    public void setState(int state){
        this.state = state;
    }
}
