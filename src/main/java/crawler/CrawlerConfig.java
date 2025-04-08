package main.java.crawler;


import java.util.Set;

public class CrawlerConfig {
    public final String startUrl;
    public final int maxDepth;
    public final Set<String> allowedDomains;

    public CrawlerConfig(String startUrl, int maxDepth, Set<String> allowedDomains) {
        this.startUrl = startUrl;
        this.maxDepth = maxDepth;
        this.allowedDomains = allowedDomains;
    }
}
