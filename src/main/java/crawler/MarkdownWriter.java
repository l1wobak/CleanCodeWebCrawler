package crawler;

import crawler.model.CrawledPage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MarkdownWriter {

    protected void write(List<CrawledPage> pages, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("# Crawled Website Report\n\n");

            for (CrawledPage page : pages) {
                writePage(writer, page, pages);
                writer.write("\n");
            }
        }
    }

    private void writePage(FileWriter writer, CrawledPage page, List<CrawledPage> allPages) throws IOException {
        String indent = MarkdownUtils.indent(page.depth);
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
        Set<String> uniqueLinks = MarkdownUtils.extractUniqueLinks(page);
        if (uniqueLinks.isEmpty()) return;

        writer.write(String.format("%s- Links:\n", indent));
        for (String link : uniqueLinks) {
            boolean broken = MarkdownUtils.isLinkBroken(link, allPages);
            writer.write(String.format("%s  - [%s](%s)%s\n", indent,
                    link, link, broken ? " ❌ broken" : ""));
        }
    }
}
