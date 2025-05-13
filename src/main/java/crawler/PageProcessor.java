package crawler;

import crawler.model.CrawledPage;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class PageProcessor {

    private final HtmlFetcher fetcher;
    private final int htmlNumberOfHeadings = 6;

    public PageProcessor(HtmlFetcher fetcher) {
        this.fetcher = fetcher;
    }

    public CrawledPage processPage(String url, int depth) {
        CrawledPage page = new CrawledPage();
        page.url = url;
        page.depth = depth;
        page.headings = new ArrayList<>();
        page.links = new ArrayList<>();
        page.isBroken = false;

        try {
            Document document = fetcher.fetchDocumentFromUrl(url);
            page.headings = extractHeadings(document);
            page.links = extractLinks(document);
        } catch (Exception e) {
            page.isBroken = true;
        }

        return page;
    }

    private List<String> extractHeadings(Document document) {
        List<String> headings = new ArrayList<>();
        for (int i = 1; i <= htmlNumberOfHeadings; i++) {
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
