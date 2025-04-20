package crawler;

import crawler.model.CrawledPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageProcessor {

    private static final int TIMEOUT_MS = 5000;

    public CrawledPage processPage(String url, int depth) {
        CrawledPage page = new CrawledPage();
        page.url = url;
        page.depth = depth;
        page.headings = new ArrayList<>();
        page.links = new ArrayList<>();
        page.isBroken = false;

        try {
            Document document = loadDocument(url);
            page.headings = extractHeadings(document);
            page.links = extractLinks(document);
        } catch (IOException e) {
            page.isBroken = true;
        }

        return page;
    }

    private Document loadDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .timeout(TIMEOUT_MS)
                .get();
    }

    private List<String> extractHeadings(Document document) {
        List<String> headings = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            Elements headerElements = document.select("h" + i);
            for (Element header : headerElements) {
                String text = header.text().trim();
                if (!text.isEmpty()) {
                    headings.add(text);
                }
            }
        }
        return headings;
    }

    private List<String> extractLinks(Document document) {
        List<String> links = new ArrayList<>();
        Elements anchors = document.select("a[href]");
        for (Element anchor : anchors) {
            String href = anchor.attr("abs:href").trim().toLowerCase();
            if (!href.isEmpty()) {
                links.add(href);
            }
        }
        return links;
    }
}
