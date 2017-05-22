package data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class HtmlParser {

    private static final String BASE_URL = "http://terfit.ru/schedule/";
    private static final String SUFFIX = "/?ajax=Y&getContent=Y&COACH=0&PAGEN_1=1";

    private final String URL;

    public HtmlParser(String club){
        URL = BASE_URL + club + SUFFIX;
    }

    public String loadPage() throws IOException {
        Document document = Jsoup.connect(URL).get();
        return document.toString();
    }

}
