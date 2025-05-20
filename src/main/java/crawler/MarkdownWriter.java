package crawler;

import crawler.model.CrawledPage;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MarkdownWriter {

    protected void write(List<CrawledPage> pages, String filename, List<URL> startUrls) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("# Crawled Website Report\n\n");

            for (URL root : startUrls) {
                writer.write(String.format("## Results for: %s\n\n", root));

                List<CrawledPage> forRoot = pages.stream()
                        .filter(p -> p.getFromStartUrls().contains(root))
                        .sorted(Comparator.comparingInt(CrawledPage::getDepth))
                        .toList();

                for (CrawledPage page : forRoot) {
                    writePage(writer, page, forRoot);
                    writer.write("\n");
                }
            }
        }
    }

    private void writePage(FileWriter writer, CrawledPage page, List<CrawledPage> allPages) throws IOException {
        String indent = MarkdownUtils.indent(page.getDepth());
        String arrow = "→".repeat(Math.max(1, page.getDepth()));

        writer.write(String.format("%s### %s %s\n", indent, arrow, page.getUrl()));
        writer.write(String.format("%s- [%s] Page %s\n", indent,
                page.isBroken() ? "✗" : "✓",
                page.isBroken() ? "could not be loaded" : "loaded successfully"));

        writeHeadings(writer, page.getHeadings(), indent);
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
