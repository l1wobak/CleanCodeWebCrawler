package main.java.crawler;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CrawlerConfig {
    private final URL startUrl;
    private final int maxDepth;
    private final Set<String> allowedDomains;

    public CrawlerConfig(URL startUrlString, int maxDepth, Set<String> domains) throws IllegalArgumentException{
        this.startUrl = startUrlString;
        this.maxDepth = maxDepth;
        this.allowedDomains = normalizeDomains(domains);
    }

    private Set<String> normalizeDomains(Set<String> domains) {
        Set<String> normalized = new HashSet<>();
        for (String domain : domains) {
            String cleaned = domain.trim().toLowerCase();
            if (!cleaned.isEmpty()) {
                normalized.add(cleaned);
            }
        }
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Allowed domains must not be empty.");
        }
        return normalized;
    }

    @Override
    public String toString() {
        return "CrawlerConfig{" +
                "startUrl=" + startUrl +
                ", maxDepth=" + maxDepth +
                ", allowedDomains=" + allowedDomains +
                '}';
    }
}