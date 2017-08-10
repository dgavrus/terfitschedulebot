package ru.terfit.data.users;

public class Put {

    private final int id;

    private Put(){
        id = 366066806;
    }

    private static final Put put = new Put();

    public static Put getPut(){
        return put;
    }

    public int getId(){
        return id;
    }

}
