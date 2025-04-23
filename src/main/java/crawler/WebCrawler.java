package crawler;

import crawler.model.CrawledPage;

import java.net.URL;
import java.util.*;

public class WebCrawler {

    private final CrawlerConfig config;
    private final Set<String> visitedPages;
    private final List<CrawledPage> resultsList;
    private final PageProcessor pageProcessor;

    public WebCrawler(CrawlerConfig config, PageProcessor pageProcessor) {
        this.config = config;
        this.visitedPages = new HashSet<>();
        this.resultsList = new ArrayList<>();
        this.pageProcessor = pageProcessor;
    }

    protected List<CrawledPage> crawl() {
        crawlRecursively(config.getStartUrl().toString(), 0);
        return resultsList;
    }

    private void crawlRecursively(String url, int currentDepth) {
        if (!shouldCrawl(url, currentDepth)) return;

        visitedPages.add(WebCrawlerUtils.normalizeUrl(url));
        System.out.printf("Crawling at %s (depth %d)\n", url, currentDepth);

        CrawledPage page = pageProcessor.processPage(url, currentDepth);
        resultsList.add(page);

        if (page.isBroken) return;

        crawlFollowupLinks(page, currentDepth + 1);
    }

    protected boolean shouldCrawl(String url, int currentDepth) {
        if (currentDepth > config.getMaxDepth()) return false;
        if (url.isEmpty()) return false;

        String normalized = WebCrawlerUtils.normalizeUrl(url);
        if (visitedPages.contains(normalized)) return false;
        if (!WebCrawlerUtils.isDomainAllowed(normalized, config.getAllowedDomains())) return false;

        return true;
    }

    protected void crawlFollowupLinks(CrawledPage page, int nextDepth) {
        for (String link : page.links) {
            String normalizedLink = WebCrawlerUtils.normalizeUrl(link);
            if (!normalizedLink.isEmpty() && !visitedPages.contains(normalizedLink)) {
                crawlRecursively(link, nextDepth);
            }
        }
    }
}
