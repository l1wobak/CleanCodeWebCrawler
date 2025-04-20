package crawler;

import crawler.model.CrawledPage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class WebCrawler {

    private final CrawlerConfig config;
    private final Set<String> visitedPages;
    private final List<CrawledPage> resultsList;
    private final PageProcessor pageProcessor;

    public WebCrawler(CrawlerConfig config) {
        this.config = config;
        this.visitedPages = new HashSet<>();
        this.resultsList = new ArrayList<>();
        this.pageProcessor = new PageProcessor();
    }

    public List<CrawledPage> crawl() {
        crawlRecursively(config.getStartUrl().toString(), 0);
        return resultsList;
    }

    private void crawlRecursively(String url, int currentDepth) {
        if (!shouldCrawl(url, currentDepth)) return;

        visitedPages.add(url);
        System.out.printf("Crawling at %s (depth %d)\n", url, currentDepth);

        CrawledPage page = pageProcessor.processPage(url, currentDepth);
        resultsList.add(page);

        if (page.isBroken) return;

        crawlFollowupLinks(page, currentDepth + 1);
    }


    private boolean shouldCrawl(String url, int currentDepth) {
        if (currentDepth > config.getMaxDepth()) return false;
        if (url.isEmpty()) return false;
        if (visitedPages.contains(url)) return false;
        if (!isDomainAllowed(url)) return false;
        return true;
    }

    private void crawlFollowupLinks(CrawledPage page, int nextDepth) {
        for (String link : page.links) {
            String normalizedLink = normalizeUrl(link);
            if (!normalizedLink.isEmpty() && !visitedPages.contains(normalizedLink)) {
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
