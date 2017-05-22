package data.users;

import java.util.concurrent.ConcurrentHashMap;

public class UsersHolder {

    private ConcurrentHashMap<Integer, UserProperties> users;

    public UsersHolder(){
        this.users = new ConcurrentHashMap<>();
    }

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
