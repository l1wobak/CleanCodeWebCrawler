package crawler;

import crawler.model.CrawledPage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class WebCrawler {

    private final CrawlerConfig config;
    private final Set<String> visited;
    private final List<CrawledPage> result;
    private final PageProcessor processor;

    public WebCrawler(CrawlerConfig config) {
        this.config = config;
        this.visited = new HashSet<>();
        this.result = new ArrayList<>();
        this.processor = new PageProcessor();
    }

    public List<CrawledPage> crawl() {
        crawlRecursively(config.getStartUrl().toString(), 0);
        return result;
    }

    private void crawlRecursively(String url, int currentDepth) {
        if (currentDepth > config.getMaxDepth()) return;
        if (visited.contains(url)) return;
        if (!isDomainAllowed(url)) return;

        visited.add(url);

        CrawledPage page = processor.process(url, currentDepth);
        result.add(page);

        if (page.isBroken) return;

        for (String link : page.links) {
            crawlRecursively(link, currentDepth + 1);
        }
    }

    private boolean isDomainAllowed(String urlString) {
        try {
            urlString = urlString.trim().toLowerCase();
            new URL(urlString);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
