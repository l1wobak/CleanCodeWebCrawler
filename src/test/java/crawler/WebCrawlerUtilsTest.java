package crawler;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WebCrawlerUtilsTest {

    @Test
    void normalizeUrl_removesTrailingSlash() {
        // Arrange
        String url = "https://example.com/page/";

        // Act
        String normalized = WebCrawlerUtils.normalizeUrl(url);

        // Assert
        assertEquals("https://example.com/page", normalized);
    }

    @Test
    void normalizeUrl_keepsProtocolAndHost() {
        // Arrange
        String url = "http://example.com/path/to/resource";

        // Act
        String normalized = WebCrawlerUtils.normalizeUrl(url);

        // Assert
        assertEquals("http://example.com/path/to/resource", normalized);
    }

    @Test
    void normalizeUrl_returnsEmptyStringForInvalidUrl() {
        // Arrange
        String badUrl = "this_is_not_a_url";

        // Act
        String result = WebCrawlerUtils.normalizeUrl(badUrl);

        // Assert
        assertEquals("", result);
    }

    @Test
    void isDomainAllowed_matchesExactHost() {
        // Arrange
        String url = "https://example.org/news";
        Set<String> allowed = Set.of("example.org");

        // Act
        boolean allowedResult = WebCrawlerUtils.isDomainAllowed(url, allowed);

        // Assert
        assertTrue(allowedResult);
    }

    @Test
    void isDomainAllowed_rejectsDisallowedHost() {
        // Arrange
        String url = "https://not-allowed.com/page";
        Set<String> allowed = Set.of("example.org");

        // Act
        boolean allowedResult = WebCrawlerUtils.isDomainAllowed(url, allowed);

        // Assert
        assertFalse(allowedResult);
    }

    @Test
    void isDomainAllowed_returnsFalseForMalformedUrl() {
        // Arrange
        String badUrl = "ht!tp:/nope";
        Set<String> allowed = Set.of("example.com");

        // Act
        boolean result = WebCrawlerUtils.isDomainAllowed(badUrl, allowed);

        // Assert
        assertFalse(result);
    }
}
