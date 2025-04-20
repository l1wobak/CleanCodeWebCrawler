package crawler;

import crawler.model.CrawledPage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MarkdownWriter {

    public void write(List<CrawledPage> pages, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("# Crawled Website Report\n\n");

            for (CrawledPage page : pages) {
                writePage(writer, page, pages);
                writer.write("\n");
            }
        }
    }

    private void writePage(FileWriter writer, CrawledPage page, List<CrawledPage> allPages) throws IOException {
        String indent = indent(page.depth);
        String arrow = "→".repeat(Math.max(1, page.depth));

        writer.write(String.format("%s## %s %s\n", indent, arrow, page.url));
        writer.write(String.format("%s- [%s] Page %s\n", indent,
                page.isBroken ? "✗" : "✓",
                page.isBroken ? "could not be loaded" : "loaded successfully"));

        writeHeadings(writer, page.headings, indent);
        writeLinks(writer, page, allPages, indent);
    }

    private void writeHeadings(FileWriter writer, List<String> headings, String indent) throws IOException {
        if (headings == null || headings.isEmpty()) return;

        writer.write(String.format("%s- Headings:\n", indent));
        for (String heading : headings) {
            writer.write(String.format("%s  - %s\n", indent, heading));
        }
    }

    private void writeLinks(FileWriter writer, CrawledPage page, List<CrawledPage> allPages, String indent) throws IOException {
        Set<String> uniqueLinks = extractUniqueLinks(page);
        if (uniqueLinks.isEmpty()) return;

        writer.write(String.format("%s- Links:\n", indent));
        for (String link : uniqueLinks) {
            boolean broken = isLinkBroken(link, allPages);
            writer.write(String.format("%s  - [%s](%s)%s\n", indent,
                    link, link, broken ? " ❌ broken" : ""));
        }
    }

    private Set<String> extractUniqueLinks(CrawledPage page) {
        Set<String> uniqueLinks = new LinkedHashSet<>();
        if (page.links == null || page.links.isEmpty()) return uniqueLinks;

        String currentPageURLNormalized = normalizeUrl(page.url);
        for (String link : page.links) {
            if (link == null || link.isBlank()) continue;
            String normalized = normalizeUrl(link);
            if (!normalized.equals(currentPageURLNormalized)) {
                uniqueLinks.add(link);
            }
        }
        return uniqueLinks;
    }


    private boolean isLinkBroken(String link, List<CrawledPage> pages) {
        // We iterate over all pages in the list, and check if the link points to any of them
        // If it does, then we return whether or not that page is broken
        // This can only return true for pages that have been crawled, all uncrawled pages are always returned as not broken
        return pages.stream()
                .filter(p -> p.url.equals(link))
                .anyMatch(p -> p.isBroken);
    }

    private String normalizeUrl(String url) {
        int hashIndex = url.indexOf('#');
        return hashIndex >= 0 ? url.substring(0, hashIndex).replaceAll("/$", "") : url.replaceAll("/$", "");
    }

    private String indent(int depth) {
        return "  ".repeat(depth);
    }
}
