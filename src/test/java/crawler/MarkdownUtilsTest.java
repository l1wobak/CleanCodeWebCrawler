package crawler;

import crawler.model.CrawledPage;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownUtilsTest {

    @Test
    void normalizeUrl_removesTrailingSlash() {
        // arrange
        String url = "https://example.com/path/";

        // act
        String normalized = MarkdownUtils.normalizeUrl(url);

        // assert
        assertEquals("https://example.com/path", normalized);
    }

    @Test
    void normalizeUrl_removesFragmentAndTrailingSlash() {
        // arrange
        String url = "https://example.com/page/#section";

        // act
        String normalized = MarkdownUtils.normalizeUrl(url);

        // assert
        assertEquals("https://example.com/page", normalized);
    }

    @Test
    void normalizeUrl_worksWithoutFragmentOrSlash() {
        // arrange
        String url = "https://example.com/test";

        // act
        String normalized = MarkdownUtils.normalizeUrl(url);

        // assert
        assertEquals("https://example.com/test", normalized);
    }

    @Test
    void normalizeUrl_returnsEmptyOnNull() {
        // arrange
        String url = null;

        // act
        String result = MarkdownUtils.normalizeUrl(url);

        // assert
        assertEquals("", result);
    }

    @Test
    void extractUniqueLinks_removesSelfAndDuplicateLinks() {
        // arrange
        CrawledPage page = new CrawledPage();
        page.setUrl("https://example.com/");
        page.setLinks(List.of(
                "https://example.com/",
                "https://example.com/#top",
                "https://example.com/about",
                "https://example.com/about/"
        ));

        // act
        Set<String> links = MarkdownUtils.extractUniqueLinks(page);

        // assert
        assertEquals(1, links.size());
        assertTrue(links.contains("https://example.com/about"));
    }

    @Test
    void extractUniqueLinks_handlesEmptyLinkList() {
        // arrange
        CrawledPage page = new CrawledPage();
        page.setUrl("https://example.com");
        page.setLinks(List.of());

        // act
        Set<String> links = MarkdownUtils.extractUniqueLinks(page);

        // assert
        assertTrue(links.isEmpty());
    }

    @Test
    void extractUniqueLinks_handlesNullLinkList() {
        // arrange
        CrawledPage page = new CrawledPage();
        page.setUrl("https://example.com");
        page.setLinks(null);

        // act
        Set<String> links = MarkdownUtils.extractUniqueLinks(page);

        // assert
        assertTrue(links.isEmpty());
    }

    @Test
    void extractUniqueLinks_skipsBlankLinks() {
        // arrange
        CrawledPage page = new CrawledPage();
        page.setUrl("https://example.com");
        page.setLinks(List.of(" ", "https://example.com/valid"));

        // act
        Set<String> links = MarkdownUtils.extractUniqueLinks(page);

        // assert
        assertEquals(1, links.size());
        assertTrue(links.contains("https://example.com/valid"));
    }

    @Test
    void isLinkBroken_returnsTrueIfLinkIsBroken() {
        // arrange
        CrawledPage page1 = new CrawledPage();
        page1.setUrl( "https://broken.com");
        page1.setBroken(true);

        CrawledPage page2 = new CrawledPage();
        page2.setUrl( "https://ok.com");
        page2.setBroken(false);

        // act & assert
        assertTrue(MarkdownUtils.isLinkBroken("https://broken.com", List.of(page1, page2)));
    }

    @Test
    void isLinkBroken_returnsFalseForWorkingPage() {
        // arrange
        CrawledPage page = new CrawledPage();
        page.setUrl("https://ok.com");
        page.setBroken(false);

        // act
        boolean result = MarkdownUtils.isLinkBroken("https://ok.com", List.of(page));

        // assert
        assertFalse(result);
    }

    @Test
    void isLinkBroken_returnsFalseIfLinkNotInPages() {
        // arrange
        CrawledPage page = new CrawledPage();
        page.setUrl( "https://another.com");
        page.setBroken(false);

        // act
        boolean result = MarkdownUtils.isLinkBroken("https://missing.com", List.of(page));

        // assert
        assertFalse(result);
    }

    @Test
    void indent_returnsCorrectWhitespace() {
        // act & assert
        assertEquals("", MarkdownUtils.indent(0));
        assertEquals("  ", MarkdownUtils.indent(1));
        assertEquals("    ", MarkdownUtils.indent(2));
        assertEquals("      ", MarkdownUtils.indent(3));
    }
}
