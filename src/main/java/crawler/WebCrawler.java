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
        if (!isDomainAllowed(url)) return;
        String normalizedUrl = normalizeUrl(url);
        if (this.visited.contains(normalizedUrl)) return;
        this.visited.add(normalizedUrl);

        System.out.printf("Crawling at %s and depth %d\n", url, currentDepth);

        CrawledPage crawledPage = processor.processPage(url, currentDepth);
        result.add(crawledPage);

        if (crawledPage.isBroken) return;

        for (String link : crawledPage.links) {
            normalizedUrl = normalizeUrl(url);
            if (this.visited.contains(normalizedUrl)) continue;
            crawlRecursively(link, currentDepth + 1);
        }
    }

    private boolean isDomainAllowed(String urlString) {
        try {
            urlString = normalizeUrl(urlString);
            URL url = new URL(urlString);
            String domain = url.getHost();
            return this.config.getAllowedDomains().contains(domain);
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private String normalizeUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            return url.getProtocol() + "://" + url.getHost();
        } catch (MalformedURLException e) {
            return "";
        }
    }


}
