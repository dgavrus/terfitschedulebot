package ru.terfit.data.users;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UsersHolder {

    private ConcurrentHashMap<Integer, UserProperties> users = new ConcurrentHashMap<>();

    public UserProperties getUserProperties(Integer id){
        return users.get(id);
    }

    public void putUserProperties(Integer id, UserProperties userProperties){
        users.putIfAbsent(id, userProperties);
    }

    public void updateUserProperties(Integer id, UserProperties userProperties){
        users.put(id, userProperties);
    }

    public boolean hasUserProperties(Integer id){
        return users.containsKey(id);
    }

}
