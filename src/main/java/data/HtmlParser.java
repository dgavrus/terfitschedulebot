package data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.stream.Collectors;

public class HtmlParser {

    private static final String BASE_URL = "http://terfit.ru/schedule/";
    private static final String SUFFIX = "/?ajax=Y&getContent=Y&COACH=0&NOPAY=1&PAGEN_1=1";

    private final String URL;

    public HtmlParser(String club){
        URL = BASE_URL + club + SUFFIX;
    }

    public String loadPage() throws IOException {
        Document document = Jsoup.connect(URL).get();
        Element body = document.body();
        Elements today = body.getElementsByClass("scp-day__title--today");
        Elements day = body.getElementsByClass("scp-day");
        String dayString = day.get(0).select("span, a").stream().map(e -> e.text()).collect(Collectors.joining("\n"));
        return dayString;
    }

}
