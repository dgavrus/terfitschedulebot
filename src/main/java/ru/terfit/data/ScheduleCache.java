package ru.terfit.data;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.terfit.data.Constants.TODAY;
import static ru.terfit.data.Constants.TOMORROW;
import static ru.terfit.data.Utils.DTF;

@Repository
public class ScheduleCache {

    private static Logger logger = LogManager.getLogger();

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
                logger.error(e);
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
                        logger.info("Schedule loaded for {}", clubsHolder.getClub(club));
                        return map;
                    }
                });
    }

    public Map<MonthDay, List<Event>> forClub(String club){
        try {
            Map<MonthDay, List<Event>> schedule = cache.get(club);
            if(schedule.isEmpty()){
                return ImmutableMap.of();
            }
            if(MonthDay.now().isAfter(schedule.keySet().iterator().next())){
                cache.refresh(club);
            }
            return cache.get(club);

        } catch (ExecutionException e) {
            logger.error("{} {}", club, e);
            throw new RuntimeException(e);
        }
    }

    public List<Event> today(String club){
        return forClub(club).get(MonthDay.now()).stream()
                .filter(ev -> LocalTime.parse(ev.getTime().split("" + (char)8212)[0].trim()).isAfter(LocalTime.now()))
                .collect(Collectors.toList());
    }

    public List<Event> tomorrow(String club){
        LocalDate tomorrowDate = LocalDate.now().plusDays(1);
        MonthDay tomorrowMonthDay = MonthDay.of(tomorrowDate.getMonth(), tomorrowDate.getDayOfMonth());
        return forClub(club).get(tomorrowMonthDay);
    }

    public List<String> availableDays(String club){
        List<String> days = forClub(club).keySet().stream()
                .map(md -> md.format(DTF))
                .collect(Collectors.toList());
        return Stream.concat(days.stream().limit(1).map(s -> TODAY),
                    Stream.concat(days.stream().limit(2).skip(1).map(s -> TOMORROW),
                            days.stream().skip(2))).collect(Collectors.toList());
    }
}
