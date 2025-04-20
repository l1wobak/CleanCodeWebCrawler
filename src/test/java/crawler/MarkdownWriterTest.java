package crawler;

import crawler.model.CrawledPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void writesBasicPageToMarkdown() throws IOException {
        // Arrange
        CrawledPage page = createBasicPage();
        Path file = tempDir.resolve("report-basic.md");

        // Act
        new MarkdownWriter().write(List.of(page), file.toString());

        // Assert
        String output = Files.readString(file);
        assertTrue(output.contains("# Crawled Website Report"));
        assertTrue(output.contains("## → https://example.com"));
        assertTrue(output.contains("- [✓] Page loaded successfully"));
        assertTrue(output.contains("- Headings:"));
        assertTrue(output.contains("Heading 1"));
        assertTrue(output.contains("[https://example.com/about](https://example.com/about)"));
    }

    @Test
    void marksBrokenLinks() throws IOException {
        // Arrange
        CrawledPage ok = createBasicPage();
        CrawledPage broken = createBrokenPage();
        ok.links = List.of(broken.url);
        Path file = tempDir.resolve("report-broken.md");

        // Act
        new MarkdownWriter().write(List.of(ok, broken), file.toString());

        // Assert
        String output = Files.readString(file);
        assertTrue(output.contains("❌ broken"));
    }

    @Test
    void doesNotWriteEmptyHeadingsOrLinks() throws IOException {
        // Arrange
        CrawledPage page = createEmptyPage();
        Path file = tempDir.resolve("report-empty.md");

        // Act
        new MarkdownWriter().write(List.of(page), file.toString());

        // Assert
        String output = Files.readString(file);
        assertFalse(output.contains("Headings:"));
        assertFalse(output.contains("Links:"));
    }

    @Test
    void skipsSelfReferencingLinks() throws IOException {
        // Arrange
        CrawledPage page = createSelfLinkPage();
        Path file = tempDir.resolve("report-self.md");

        // Act
        new MarkdownWriter().write(List.of(page), file.toString());

        // Assert
        String output = Files.readString(file);
        assertFalse(output.contains("Links:"));
    }

    @Test
    void deduplicatesLinks() throws IOException {
        // Arrange
        CrawledPage page = createDuplicateLinkPage();
        Path file = tempDir.resolve("report-dedup.md");

        // Act
        new MarkdownWriter().write(List.of(page), file.toString());

        // Assert
        String output = Files.readString(file);
        int count = output.split("https://example.com/about", -1).length - 1;
        //example of how output should look like:
        // [https://gilead-verein.at/sponsoren/](https://gilead-verein.at/sponsoren/)
        assertEquals(2, count, "Link should only appear twice, once as text and once as a link");
    }

    // ---------- Utility Methods ----------

    private static CrawledPage createBasicPage() {
        CrawledPage page = new CrawledPage();
        page.url = "https://example.com";
        page.depth = 0;
        page.isBroken = false;
        page.headings = List.of("Heading 1", "Heading 2");
        page.links = List.of("https://example.com/about");
        return page;
    }

    private static CrawledPage createBrokenPage() {
        CrawledPage page = new CrawledPage();
        page.url = "https://broken-link.com";
        page.depth = 1;
        page.isBroken = true;
        page.headings = List.of();
        page.links = List.of();
        return page;
    }

    private static CrawledPage createEmptyPage() {
        CrawledPage page = new CrawledPage();
        page.url = "https://empty.com";
        page.depth = 0;
        page.isBroken = false;
        page.headings = List.of();
        page.links = List.of();
        return page;
    }

    private static CrawledPage createSelfLinkPage() {
        CrawledPage page = new CrawledPage();
        page.url = "https://example.com";
        page.depth = 0;
        page.isBroken = false;
        page.headings = List.of("Self-ref");
        page.links = List.of("https://example.com");
        return page;
    }

    private static CrawledPage createDuplicateLinkPage() {
        CrawledPage page = new CrawledPage();
        page.url = "https://example.com";
        page.depth = 0;
        page.isBroken = false;
        page.headings = List.of("Duplicates");
        page.links = List.of("https://example.com/about", "https://example.com/about");
        return page;
    }
}
