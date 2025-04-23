package crawler;

import crawler.model.CrawledPage;

import java.util.*;

public class MarkdownUtils {

    protected static Set<String> extractUniqueLinks(CrawledPage page) {
        Set<String> uniqueLinks = new LinkedHashSet<>();
        if (page.links == null || page.links.isEmpty()) return uniqueLinks;

        String currentPageURLNormalized = normalizeUrl(page.url);
        Set<String> seenNormalized = new HashSet<>();

        for (String link : page.links) {
            if (link == null || link.isBlank()) continue;
            String normalized = normalizeUrl(link);
            if (!normalized.equals(currentPageURLNormalized) && seenNormalized.add(normalized)) {
                uniqueLinks.add(normalized);
            }
        }
        return uniqueLinks;
    }

    protected static boolean isLinkBroken(String link, List<CrawledPage> pages) {
        // We iterate over all pages in the list, and check if the link points to any of them
        // If it does, then we return whether or not that page is broken
        // This can only return true for pages that have been crawled, all uncrawled pages are always returned as not broken
        return pages.stream()
                .filter(p -> p.url.equals(link))
                .anyMatch(p -> p.isBroken);
    }

    protected static String normalizeUrl(String url) {
        if (url == null) return "";
        int hashIndex = url.indexOf('#');
        return hashIndex >= 0 ? url.substring(0, hashIndex).replaceAll("/$", "") : url.replaceAll("/$", "");
    }

    protected static String indent(int depth) {
        return "  ".repeat(depth);
    }
}
