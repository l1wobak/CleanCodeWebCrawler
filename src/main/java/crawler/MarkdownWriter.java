package crawler;

import crawler.model.CrawledPage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MarkdownWriter {

    public void write(List<CrawledPage> pages, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("# Crawled Website Report\n\n");

            for (CrawledPage page : pages) {
                String indent = "  ".repeat(page.depth);
                String arrow = "→".repeat(Math.max(1, page.depth));

                writer.write(String.format("%s## %s %s\n", indent, arrow, page.url));
                writer.write(String.format("%s- [%s] Page %s\n", indent,
                        page.isBroken ? "✗" : "✓",
                        page.isBroken ? "could not be loaded" : "loaded successfully"
                ));

                if (!page.headings.isEmpty()) {
                    writer.write(String.format("%s- Headings:\n", indent));
                    for (String heading : page.headings) {
                        writer.write(String.format("%s  - %s\n", indent, heading));
                    }
                }

                if (!page.links.isEmpty()) {
                    writer.write(String.format("%s- Links:\n", indent));
                    for (String link : page.links) {
                        boolean broken = isLinkBroken(link, pages);
                        writer.write(String.format("%s  - [%s](%s)%s\n", indent,
                                link, link, broken ? " ❌ broken" : ""
                        ));
                    }
                }

                writer.write("\n");
            }
        }
    }

    private boolean isLinkBroken(String link, List<CrawledPage> pages) {
        return pages.stream()
                .filter(p -> p.url.equals(link))
                .anyMatch(p -> p.isBroken);
    }

}
