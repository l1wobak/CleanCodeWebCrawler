package main.java.crawler;

import main.java.crawler.model.CrawledPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PageProcessor {

    private static final int TIMEOUT_MS = 5000;

    public CrawledPage process(String url, int depth) {
        CrawledPage page = new CrawledPage();
        page.url = url;
        page.depth = depth;
        page.headings = new ArrayList<>();
        page.links = new ArrayList<>();
        page.isBroken = false;

        try {
            Document doc = Jsoup.connect(url).timeout(TIMEOUT_MS).get();

            // Extract headings h1 to h6
            for (int i = 1; i <= 6; i++) {
                Elements headers = doc.select("h" + i);
                for (Element header : headers) {
                    page.headings.add(header.text().trim());
                }
            }

            // Extract links
            Elements anchors = doc.select("a[href]");
            for (Element anchor : anchors) {
                String href = anchor.attr("abs:href"); // resolve to absolute
                if (!href.isEmpty()) {
                    page.links.add(href);
                }
            }

        } catch (IOException e) {
            page.isBroken = true;
        }

        return page;
    }
}
