package crawler;


import java.net.URL;
import java.util.*;

public class CrawlerConfig {
    private final List<URL> startUrls;
    private final int maxDepth;
    private final Set<String> allowedDomains;

    public CrawlerConfig(List<URL> startUrlString, int maxDepth, Set<String> domains) throws IllegalArgumentException {
        this.startUrls = startUrlString;
        this.maxDepth = maxDepth;
        this.allowedDomains = normalizeDomains(domains);
    }

    private Set<String> normalizeDomains(Set<String> domains) throws IllegalArgumentException {
        Set<String> normalized = new HashSet<>();
        for (String domain : domains) {
            String cleaned = domain.trim().toLowerCase();
            if (cleaned.isEmpty()) continue;

            try {
                URL url = new URL(cleaned);
                normalized.add(url.getHost());
            } catch (Exception e) {
                throw new IllegalArgumentException("received invalid domain for allowed domains: " + e);
            }
        }
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Allowed domains must contain at least one valid domain.");
        }
        return normalized;
    }


    @Override
    public String toString() {
        return "CrawlerConfig{" +
                "startUrl=" + startUrls +
                ", maxDepth=" + maxDepth +
                ", allowedDomains=" + allowedDomains +
                '}';
    }

    public List<URL> getStartUrls() {
        return startUrls;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public Set<String> getAllowedDomains() {
        return allowedDomains;
    }
}