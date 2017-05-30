package ru.terfit.data.users;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import ru.terfit.data.State;

import static ru.terfit.data.State.START;

@DatabaseTable(tableName = "users")
public class UserProperties {

    @DatabaseField(id = true)
    private Integer id;
    @DatabaseField(dataType = DataType.STRING, width = 32)
    private String club;
    @DatabaseField(dataType = DataType.ENUM_STRING, width = 7)
    private Remember remember;
    @DatabaseField(dataType = DataType.ENUM_STRING, width = 16)
    private State state = START;

    public UserProperties(){}

    public UserProperties(Integer id){
        this(id, null, Remember.NOT_NOW);
    }

    public UserProperties(Integer id, String club){
        this(id, club, Remember.NOT_NOW);
    }

    public UserProperties(Integer id, String club, Remember remember){
        this.id = id;
        this.club = club;
        this.remember = remember;
        this.state = START;
    }

    public Integer getId(){ return id; }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserProperties that = (UserProperties) o;

        if (!id.equals(that.id)) return false;
        if (club != null ? !club.equals(that.club) : that.club != null) return false;
        if (remember != that.remember) return false;
        return state == that.state;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (club != null ? club.hashCode() : 0);
        result = 31 * result + remember.hashCode();
        result = 31 * result + state.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(',')
                .append(club).append(',')
                .append(remember).append(',')
                .append(state);
        return sb.toString();
    }
}
