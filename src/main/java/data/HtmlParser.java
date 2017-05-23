package data;

import com.google.common.collect.ImmutableList;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class HtmlParser {

    private static final String BASE_URL = "http://terfit.ru/schedule/";
    private static final String SUFFIX = "/?ajax=Y&getContent=Y&AGE[]=32&COACH=0&NOPAY=1&PAGEN_1=1";

    private final String URL;

    public HtmlParser(String club){
        URL = BASE_URL + club + SUFFIX;
    }

    public Collection<Event> today() throws IOException {
        Document document = Jsoup.connect(URL).get();
        Element body = document.body();
        Elements day = body.getElementsByClass("scp-day");
        Collection<Element> dayString = day.get(0).select(".scp-tile").stream().collect(Collectors.toList());
        Collection<Event> events = dayString.stream().map(this::parseEvent).collect(Collectors.toList());
        return events;
    }

    public Collection<Event> tomorrow() throws IOException {
        Document document = Jsoup.connect(URL).get();
        Element body = document.body();
        Elements day = body.getElementsByClass("scp-day");
        Collection<Element> dayString = day.get(1).select(".scp-tile").stream().collect(Collectors.toList());
        Collection<Event> events = dayString.stream().map(this::parseEvent).collect(Collectors.toList());
        return events;
    }

    private Event parseEvent(Element scpTile){
        Elements elements = scpTile.select("span:not(span > span), a");
        List<String> eventParams = elements.stream().map(Element::text).collect(Collectors.toList());
        while (eventParams.size() < 4) eventParams.add("");
        return new Event(eventParams.get(0), eventParams.get(1), eventParams.get(2), eventParams.get(3));
    }

}
