package ru.terfit.data;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOError;
import java.io.IOException;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Repository
public class ScheduleCache {

    @Inject
    private ClubsHolder clubsHolder;

    private LoadingCache<String, Map<MonthDay, List<Event>>> cache;

    @PostConstruct
    private void init(){
        cache = createCache();
        clubsHolder.clubsString().forEach(c -> {
            try {
                cache.get(c);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private LoadingCache<String, Map<MonthDay, List<Event>>> createCache(){
        return CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build(new CacheLoader<String, Map<MonthDay, List<Event>>>() {

                    @Override
                    public Map<MonthDay, List<Event>> load(String club) throws Exception {
                        Map<MonthDay, List<Event>> map = new TreeMap<>();
                        HtmlParser parser = new HtmlParser(clubsHolder.getClub(club));
                        map.putAll(parser.all());
                        return map;
                    }
                });
    }

    public Map<MonthDay, List<Event>> forClub(String club){
        try {
            return cache.get(club);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /*public List<Event> today(){
        return
    }*/



}
