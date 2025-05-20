package crawler;

import crawler.model.CrawledPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void writesBasicPageToMarkdown() throws IOException {
        CrawledPage page = createBasicPage();
        Path file = tempDir.resolve("report-basic.md");

        new MarkdownWriter().write(
                List.of(page),
                file.toString(),
                List.of(URI.create("https://example.com").toURL())
        );

        String output = Files.readString(file);
        assertTrue(output.contains("# Crawled Website Report"));
        assertTrue(output.contains("## Results for: https://example.com"));
        assertTrue(output.contains("→ https://example.com"));
        assertTrue(output.contains("- [✓] Page loaded successfully"));
        assertTrue(output.contains("- Headings:"));
        assertTrue(output.contains("Heading 1"));
        assertTrue(output.contains("[https://example.com/about](https://example.com/about)"));
    }

    @Test
    void marksBrokenLinks() throws IOException {
        CrawledPage ok = createBasicPage();
        CrawledPage broken = createBrokenPage();
        broken.getFromStartUrls().add(URI.create(ok.getUrl()).toURL());
        ok.setLinks(List.of(broken.getUrl()));
        Path file = tempDir.resolve("report-broken.md");

        new MarkdownWriter().write(
                List.of(ok, broken),
                file.toString(),
                List.of(URI.create(ok.getUrl()).toURL())
        );

        String output = Files.readString(file);
        assertTrue(output.contains("❌ broken"));
    }

    @Test
    void doesNotWriteEmptyHeadingsOrLinks() throws IOException {
        CrawledPage page = createEmptyPage();
        Path file = tempDir.resolve("report-empty.md");

        new MarkdownWriter().write(
                List.of(page),
                file.toString(),
                List.of(URI.create(page.getUrl()).toURL())
        );

        String output = Files.readString(file);
        assertFalse(output.contains("Headings:"));
        assertFalse(output.contains("Links:"));
    }

    @Test
    void skipsSelfReferencingLinks() throws IOException {
        CrawledPage page = createSelfLinkPage();
        Path file = tempDir.resolve("report-self.md");

        new MarkdownWriter().write(
                List.of(page),
                file.toString(),
                List.of(URI.create(page.getUrl()).toURL())
        );

        String output = Files.readString(file);
        assertFalse(output.contains("Links:"));
    }

    @Test
    void deduplicatesLinks() throws IOException {
        CrawledPage page = createDuplicateLinkPage();
        Path file = tempDir.resolve("report-dedup.md");

        new MarkdownWriter().write(
                List.of(page),
                file.toString(),
                List.of(URI.create(page.getUrl()).toURL())
        );

        String output = Files.readString(file);
        int count = output.split("https://example.com/about", -1).length - 1;
        assertEquals(2, count, "Link should only appear twice, once as text and once as a link");
    }

    // ---------- Utility Methods ----------

    private static CrawledPage createBasicPage() {
        CrawledPage page = new CrawledPage("https://example.com", 0, List.of("Heading 1", "Heading 2"), List.of("https://example.com/about"), false);
        page.getFromStartUrls().add(toUrl(page.getUrl()));
        return page;
    }

    private static CrawledPage createBrokenPage() {
        CrawledPage page = new CrawledPage( "https://broken-link.com", 1, List.of(), List.of(), true);
        page.getFromStartUrls().add(toUrl(page.getUrl()));
        return page;
    }

    private static CrawledPage createEmptyPage() {
        CrawledPage page = new CrawledPage("https://empty.com", 0, List.of(), List.of(), false);
        page.getFromStartUrls().add(toUrl(page.getUrl()));
        return page;
    }

    private static CrawledPage createSelfLinkPage() {
        CrawledPage page = new CrawledPage("https://example.com", 0, List.of("Self-ref"),List.of("https://example.com"), false );
        page.getFromStartUrls().add(toUrl(page.getUrl()));
        return page;
    }

    private static CrawledPage createDuplicateLinkPage() {
        CrawledPage page = new CrawledPage("https://example.com", 0, List.of("Duplicates"), List.of("https://example.com/about", "https://example.com/about"), false);
        page.getFromStartUrls().add(toUrl(page.getUrl()));
        return page;
    }

    private static URL toUrl(String url) {
        try {
            return URI.create(url).toURL();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
