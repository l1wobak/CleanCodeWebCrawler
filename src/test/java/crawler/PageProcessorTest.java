package crawler;

import crawler.model.CrawledPage;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageProcessorTest {

    private MockWebServer server;
    private PageProcessor processor;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        processor = new PageProcessor(new JsoupHtmlFetcher(5000));
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void processesHeadingsCorrectly() {
        // Arrange
        server.enqueue(new MockResponse().setBody("""
            <html><body>
              <h1>Main Title</h1>
              <h2>Subheading</h2>
              <h3>Another</h3>
            </body></html>
        """));
        String testUrl = server.url("/headings").toString();

        // Act
        CrawledPage page = processor.processPage(testUrl, 1);

        // Assert
        assertFalse(page.isBroken());
        assertEquals(List.of("Main Title", "Subheading", "Another"), page.getHeadings());
    }

    @Test
    void processesLinksCorrectly() {
        // Arrange
        server.enqueue(new MockResponse().setBody("""
            <html><body>
              <a href="/link1">Link1</a>
              <a href="https://example.com/external">External</a>
            </body></html>
        """));
        String testUrl = server.url("/links").toString();

        // Act
        CrawledPage page = processor.processPage(testUrl, 0);

        // Assert
        assertFalse(page.isBroken());
        assertEquals(2, page.getLinks().size());
    }

    @Test
    void marksPageAsBrokenIfRequestFails() {
        // Arrange
        String badUrl = "http://localhost:9999/doesnotexist";

        // Act
        CrawledPage result = processor.processPage(badUrl, 0);

        // Assert
        assertTrue(result.isBroken());
        assertTrue(result.getHeadings().isEmpty());
        assertTrue(result.getLinks().isEmpty());
    }

    @Test
    void handlesPageWithNoHeadingsOrLinks() {
        // Arrange
        server.enqueue(new MockResponse().setBody("""
            <html><body><p>Just text</p></body></html>
        """));
        String testUrl = server.url("/no-headings").toString();

        // Act
        CrawledPage result = processor.processPage(testUrl, 1);

        // Assert
        assertFalse(result.isBroken());
        assertTrue(result.getHeadings().isEmpty());
        assertTrue(result.getLinks().isEmpty());
    }

    @Test
    void ignoresEmptyOrWhitespaceOnlyHeadings() {
        // Arrange
        server.enqueue(new MockResponse().setBody("""
            <html><body>
              <h1> </h1>
              <h2>   </h2>
              <h3>Valid Heading</h3>
            </body></html>
        """));
        String testUrl = server.url("/empty-headings").toString();

        // Act
        CrawledPage result = processor.processPage(testUrl, 0);

        // Assert
        assertFalse(result.isBroken());
        assertEquals(List.of("Valid Heading"), result.getHeadings());
    }

    @Test
    void handlesDuplicateHeadingsAndLinks() {
        // Arrange
        server.enqueue(new MockResponse().setBody("""
            <html><body>
              <h1>Same</h1><h2>Same</h2>
              <a href="https://example.com/dup">Link</a>
              <a href="https://example.com/dup">Link</a>
            </body></html>
        """));
        String testUrl = server.url("/duplicates").toString();

        // Act
        CrawledPage result = processor.processPage(testUrl, 2);

        // Assert
        assertFalse(result.isBroken());
        assertEquals(List.of("Same", "Same"), result.getHeadings());
        assertEquals(2, result.getLinks().size());
    }

}
