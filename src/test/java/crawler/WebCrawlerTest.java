package crawler;

import crawler.model.CrawledPage;
import crawler.HtmlFetcher;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class WebCrawlerTest {

    private CrawlerConfig config;
    private FakePageProcessor processor;
    private WebCrawler crawler;

    @BeforeEach
    void setUp() throws Exception {
        URL startUrl = new URL("https://example.com");
        Set<String> allowedDomains = Set.of("https://example.com");

        HtmlFetcher dummyFetcher = url -> null;

        config = new CrawlerConfig(List.of(startUrl), 2, allowedDomains);
        processor = new FakePageProcessor(dummyFetcher);
        crawler = new WebCrawler(config, processor);
    }

    @Test
    void crawlsInitialPage() {
        processor.stubPage("https://example.com", List.of(), false);

        List<CrawledPage> result = crawler.crawl();

        assertEquals(1, result.size());
        assertEquals("https://example.com", result.get(0).getUrl());
        assertFalse(result.get(0).isBroken());
    }

    @Test
    void skipsBrokenPages() {
        processor.stubPage("https://example.com", List.of("https://example.com/broken"), false);
        processor.stubPage("https://example.com/broken", List.of(), true);

        List<CrawledPage> result = crawler.crawl();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(CrawledPage::isBroken));
    }

    @Test
    void respectsMaxDepth() throws MalformedURLException {
        processor.stubPage("https://example.com", List.of("https://example.com/level1"), false);
        processor.stubPage("https://example.com/level1", List.of("https://example.com/level2"), false);
        processor.stubPage("https://example.com/level2", List.of("https://example.com/level3"), false);

        config = new CrawlerConfig(List.of(new URL("https://example.com")), 1, Set.of("https://example.com"));
        crawler = new WebCrawler(config, processor);

        List<CrawledPage> result = crawler.crawl();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(p -> p.getUrl().contains("level2")));
    }

    @Test
    void avoidsDuplicateCrawls() {
        processor.stubPage("https://example.com", List.of("https://example.com/page"), false);
        processor.stubPage("https://example.com/page", List.of("https://example.com"), false);

        List<CrawledPage> result = crawler.crawl();

        assertEquals(2, result.size(), "Each page should only be crawled once");
    }

    @Test
    void skipsPagesOutsideDomain() {
        processor.stubPage("https://example.com", List.of("https://other.com/page"), false);
        processor.stubPage("https://other.com/page", List.of(), false);

        List<CrawledPage> result = crawler.crawl();

        assertEquals(1, result.size());
        assertEquals("https://example.com", result.get(0).getUrl());
    }

    // -------------- Fake Page Processor --------------
    static class FakePageProcessor extends PageProcessor {
        private final java.util.Map<String, CrawledPage> stubbedPages = new java.util.HashMap<>();

        public FakePageProcessor(HtmlFetcher fetcher) {
            super(fetcher);
        }

        public void stubPage(String url, List<String> links, boolean isBroken) {
            CrawledPage page = new CrawledPage(url, -1, List.of("Fake heading"),links,  isBroken);
            page.setFromStartUrls(ConcurrentHashMap.newKeySet());
            stubbedPages.put(url, page);
        }

        @Override
        public CrawledPage processPage(String url, int depth) {
            CrawledPage page = stubbedPages.getOrDefault(url, new CrawledPage());
            page.setDepth(depth);
            return page;
        }
    }
}
