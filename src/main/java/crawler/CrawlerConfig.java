package main.java.crawler;


import java.net.URL;
import java.util.Set;

public class CrawlerConfig {
    public final URL startUrl;
    public final int maxDepth;
    public final Set<String> allowedDomains;

    public CrawlerConfig(URL startUrl, int maxDepth, Set<String> allowedDomains) {
        this.startUrl = startUrl;
        this.maxDepth = maxDepth;
        this.allowedDomains = allowedDomains;
    }
}
