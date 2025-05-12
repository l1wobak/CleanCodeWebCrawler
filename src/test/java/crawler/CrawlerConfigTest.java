package crawler;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CrawlerConfigTest {

    @Test
    void shouldCreateValidConfig() throws MalformedURLException {
        // Arrange

        ArrayList<URL> startUrl = new ArrayList<>();
        startUrl.add(new URL("https://test.com"));
        Set<String> domains = Set.of("https://example.com");

        // Act
        CrawlerConfig config = new CrawlerConfig(startUrl, 2, domains);

        // Assert
        assertEquals(startUrl, config.getStartUrls());
        assertEquals(2, config.getMaxDepth());
        assertTrue(config.getAllowedDomains().contains("example.com"));
    }

    @Test
    void shouldNormalizeDomains() throws MalformedURLException {
        // Arrange
        ArrayList<URL> startUrl = new ArrayList<>();
        startUrl.add(new URL("https://test.com"));
        Set<String> domains = Set.of("  https://Example.COM/path  ");

        // Act
        CrawlerConfig config = new CrawlerConfig(startUrl, 1, domains);

        // Assert
        assertTrue(config.getAllowedDomains().contains("example.com"));
    }

    @Test
    void shouldThrowOnInvalidDomain() throws MalformedURLException {
        // Arrange
        ArrayList<URL> startUrl = new ArrayList<>();
        startUrl.add(new URL("https://test.com"));
        Set<String> domains = Set.of("bad:url");

        // Act (and partial Assert)
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new CrawlerConfig(startUrl, 1, domains)
        );
        // Assert
        assertTrue(exception.getMessage().contains("invalid domain"));
    }

    @Test
    void shouldThrowOnEmptyDomainList() throws MalformedURLException {
        // Arrange
        ArrayList<URL> startUrl = new ArrayList<>();
        startUrl.add(new URL("https://test.com"));
        Set<String> domains = Set.of("   "); // whitespace only

        // Act (and partial Assert)
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new CrawlerConfig(startUrl, 1, domains)
        );
        // Assert
        assertTrue(exception.getMessage().contains("at least one valid domain"));
    }

    @Test
    void shouldThrowOnPartiallyInvalidDomainList() throws MalformedURLException {
        // Arrange
        ArrayList<URL> startUrl = new ArrayList<>();
        startUrl.add(new URL("https://test.com"));
        Set<String> domains = Set.of("https://example.com", "not a url");

        // Act (and partial Assert)
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new CrawlerConfig(startUrl, 1, domains)
        );
        // Assert
        assertTrue(exception.getMessage().contains("invalid domain"));
    }
}
