package ru.terfit.data.users;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.query.In;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UsersHolder {

    @Inject
    private Dao<UserProperties, Integer> userPropertiesDao;

    @PostConstruct
    private void loadFromDb() throws SQLException {
        userPropertiesDao.queryForAll().stream().forEach(up -> users.put(up.getId(), up));
    }

    private ConcurrentHashMap<Integer, UserProperties> users = new ConcurrentHashMap<>();

    public UserProperties getUserProperties(Integer id){
        return users.get(id);
    }

    public void putUserProperties(Integer id, UserProperties userProperties){
        users.putIfAbsent(id, userProperties);
        try {
            userPropertiesDao.create(userProperties);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUserProperties(Integer id, UserProperties userProperties){
        users.put(id, userProperties);
        try {
            userPropertiesDao.update(userProperties);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean hasUserProperties(Integer id){
        return users.containsKey(id);
    }

}
