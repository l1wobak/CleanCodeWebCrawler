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
        if (!shouldCrawl(url, currentDepth)) return;

        visited.add(url);
        System.out.printf("Crawling at %s (depth %d)\n", url, currentDepth);

        CrawledPage page = processor.processPage(url, currentDepth);
        result.add(page);

        if (page.isBroken) return;

        crawlFollowupLinks(page, currentDepth + 1);
    }


    private boolean shouldCrawl(String url, int currentDepth) {
        if (currentDepth > config.getMaxDepth()) return false;
        if (url.isEmpty()) return false;
        if (visited.contains(url)) return false;
        if (!isDomainAllowed(url)) return false;
        return true;
    }

    private void crawlFollowupLinks(CrawledPage page, int nextDepth) {
        for (String link : page.links) {
            String normalizedLink = normalizeUrl(link);
            if (!normalizedLink.isEmpty() && !visited.contains(normalizedLink)) {
                crawlRecursively(link, nextDepth);
            }
        }
    }

    private boolean isDomainAllowed(String urlString) {
        try {
            URL url = new URL(urlString);
            String domain = url.getHost();
            return config.getAllowedDomains().contains(domain);
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private String normalizeUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            String path = url.getPath().replaceAll("/$", "");
            return url.getProtocol() + "://" + url.getHost() + path;
        } catch (MalformedURLException e) {
            return "";
        }
    }
}
