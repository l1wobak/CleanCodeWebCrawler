package crawler;

import crawler.model.CrawledPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WebCrawlerTest {

    private CrawlerConfig config;
    private FakePageProcessor processor;
    private WebCrawler crawler;

    @BeforeEach
    void setUp() throws Exception {
        URL startUrl = new URL("https://example.com");
        Set<String> allowedDomains = Set.of("example.com");

        config = new CrawlerConfig(startUrl, 2, allowedDomains);
        processor = new FakePageProcessor();
        crawler = new WebCrawler(config, processor);
    }

    @Test
    void crawlsInitialPage() {
        // Arrange
        processor.stubPage("https://example.com", List.of(), false);

        // Act
        List<CrawledPage> result = crawler.crawl();

        // Assert
        assertEquals(1, result.size());
        assertEquals("https://example.com", result.get(0).url);
        assertFalse(result.get(0).isBroken);
    }

    @Test
    void skipsBrokenPages() {
        // Arrange
        processor.stubPage("https://example.com", List.of("https://example.com/broken"), false);
        processor.stubPage("https://example.com/broken", List.of(), true);

        // Act
        List<CrawledPage> result = crawler.crawl();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.isBroken));
    }

    @Test
    void respectsMaxDepth() throws MalformedURLException {
        // Arrange
        processor.stubPage("https://example.com", List.of("https://example.com/level1"), false);
        processor.stubPage("https://example.com/level1", List.of("https://example.com/level2"), false);
        processor.stubPage("https://example.com/level2", List.of(), false); // should not be crawled, depth = 3

        config = new CrawlerConfig(new URL("https://example.com"), 1, Set.of("example.com"));
        crawler = new WebCrawler(config, processor);

        // Act
        List<CrawledPage> result = crawler.crawl();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(p -> p.url.contains("level2")));
    }

    @Test
    void avoidsDuplicateCrawls() {
        // Arrange
        processor.stubPage("https://example.com", List.of("https://example.com/page"), false);
        processor.stubPage("https://example.com/page", List.of("https://example.com"), false);

        // Act
        List<CrawledPage> result = crawler.crawl();

        // Assert
        assertEquals(2, result.size(), "Each page should only be crawled once");
    }

    @Test
    void skipsPagesOutsideDomain() throws Exception {
        // Arrange
        processor.stubPage("https://example.com", List.of("https://other.com/page"), false);
        processor.stubPage("https://other.com/page", List.of(), false);

        // Act
        List<CrawledPage> result = crawler.crawl();

        // Assert
        assertEquals(1, result.size());
        assertEquals("https://example.com", result.get(0).url);
    }

    // -------------- Fake Page Processor --------------
    static class FakePageProcessor extends PageProcessor {
        private final java.util.Map<String, CrawledPage> stubbedPages = new java.util.HashMap<>();

        public void stubPage(String url, List<String> links, boolean isBroken) {
            CrawledPage page = new CrawledPage();
            page.url = url;
            page.depth = -1; // not important for test
            page.links = links;
            page.headings = List.of("Fake heading");
            page.isBroken = isBroken;
            stubbedPages.put(url, page);
        }

        @Override
        public CrawledPage processPage(String url, int depth) {
            CrawledPage page = stubbedPages.getOrDefault(url, new CrawledPage());
            page.depth = depth;
            return page;
        }
    }
}
