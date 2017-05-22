package data.users;

public enum Remember {
    YES("Запомнить"),
    NOT_NOW("Не сейчас"),
    NEVER("Никогда не запоминать");

    private String remember;

    Remember(String remember){
        this.remember = remember;
    }

    public String getString(){
        return remember;
    }
};
