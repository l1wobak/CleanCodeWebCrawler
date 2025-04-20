package crawler;


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

    public CrawlerConfig(URL startUrlString, int maxDepth, Set<String> domains) throws IllegalArgumentException {
        this.startUrl = startUrlString;
        this.maxDepth = maxDepth;
        this.allowedDomains = normalizeDomains(domains);
    }

    private Set<String> normalizeDomains(Set<String> domains) throws IllegalArgumentException {
        Set<String> normalized = new HashSet<>();
        for (String domain : domains) {
            String cleaned = domain.trim().toLowerCase();
            if (cleaned.isEmpty()) continue;

            try {
                if (!cleaned.contains("://")) {
                    cleaned = "http://" + cleaned;
                }
                URL cleanedURL = new URL(cleaned);
                normalized.add(cleanedURL.getHost());
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
                "startUrl=" + startUrl +
                ", maxDepth=" + maxDepth +
                ", allowedDomains=" + allowedDomains +
                '}';
    }

    public URL getStartUrl() {
        return startUrl;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public Set<String> getAllowedDomains() {
        return allowedDomains;
    }
}