package ru.terfit.data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.MonthDay;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.terfit.data.Utils.DTF;

public class HtmlParser {

    private static final String BASE_URL = "http://terfit.ru/schedule/";
    private static final String SUFFIX = "/?ajax=Y&getContent=Y&AGE[]=32&COACH=0&NOPAY=1&PAGEN_1=1";
    private final String URL;

    public HtmlParser(String club){
        URL = BASE_URL + club + SUFFIX;
    }

    public Map<MonthDay, List<Event>> all() throws IOException {
        Document document = Jsoup.connect(URL).timeout(10000).get();
        Element body = document.body();
        List<MonthDay> dayTitles = body.select(".scp-day__title > span").stream()
                .map(Element::text)
                .map(t -> MonthDay.parse(t, DTF))
                .collect(Collectors.toList());
        Elements day = body.getElementsByClass("scp-day");
        for(int i = day.size() - 1; i >= 0; i--){
            Element e = day.get(i);
            if(e.text().isEmpty()){
                day.remove(i);
                dayTitles.remove(i);
            }
        }
        Collection<List<Element>> daysEvents = day.stream().map(d -> d.select(".scp-tile").stream().collect(Collectors.toList())).collect(Collectors.toList());
        Iterator<MonthDay> dayTitlesIterator = dayTitles.iterator();
        Map<MonthDay, List<Event>> events = daysEvents.stream()
                .map(d -> d.stream()
                        .map(this::parseEvent)
                        .collect(Collectors.toList()))
                .collect(Collectors.toMap(e -> dayTitlesIterator.next(), e -> e));
        return events;
    }

    private Event parseEvent(Element scpTile){
        Elements elements = scpTile.select("span:not(span > span), a");
        List<String> eventParams = elements.stream().map(Element::text).collect(Collectors.toList());
        while (eventParams.size() < 4) eventParams.add("");
        return new Event(eventParams.get(0), eventParams.get(1), eventParams.get(2), eventParams.get(3));
    }

}
