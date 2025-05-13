package crawler;

import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void parseStartUrls_shouldReturnValidUrlsForValidInput() {
        // Arrange
        String input = "https://example.com, http://another.com";

        // Act
        List<URL> urls = Main.parseStartUrls(input);

        // Assert
        assertNotNull(urls);
        assertEquals(2, urls.size());
        assertEquals("https://example.com", urls.get(0).toString());
        assertEquals("http://another.com", urls.get(1).toString());
    }

    @Test
    void parseStartUrls_shouldSkipInvalidUrls() {
        // Arrange
        String input = "http://valid.com,not_a_url";

        // Act
        List<URL> urls = Main.parseStartUrls(input);

        // Assert
        assertEquals(1, urls.size());
        assertEquals("http://valid.com", urls.get(0).toString());
    }

    @Test
    void parseStartUrls_shouldReturnEmptyListForOnlyInvalidUrls() {
        // Arrange
        String input = "not_a_url,also_bad://url";

        // Act
        List<URL> urls = Main.parseStartUrls(input);

        // Assert
        assertNull(urls);
    }

    @Test
    void parseDepth_shouldReturnIntegerForValidInput() {
        int depth = Main.parseDepth("3");
        assertEquals(3, depth);
    }

    @Test
    void parseDepth_shouldReturnMinusOneForNegativeInput() {
        int depth = Main.parseDepth("-1");
        assertEquals(-1, depth);
    }

    @Test
    void parseDepth_shouldReturnMinusOneForInvalidInput() {
        int depth = Main.parseDepth("abc");
        assertEquals(-1, depth);
    }

    @Test
    void parseAllowedDomains_shouldReturnCleanedSetForValidInput() {
        Set<String> result = Main.parseAllowedDomains(" https://example.com ,HTTP://Another.com  ");

        assertTrue(result.contains("https://example.com"));
        assertTrue(result.contains("http://another.com"));
        assertEquals(2, result.size());
    }

    @Test
    void parseAllowedDomains_shouldReturnEmptySetForBlankInput() {
        Set<String> result = Main.parseAllowedDomains("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    void buildConfigFromArgs_shouldReturnValidConfigIfArgsAreValid() {
        // Arrange
        String[] args = {
                "https://example.com,https://another.com",
                "1",
                "https://example.com,https://another.com"
        };

        // Act
        CrawlerConfig config = Main.buildConfigFromArgs(args);

        // Assert
        assertNotNull(config);
        assertEquals(1, config.getMaxDepth());
        assertEquals(2, config.getStartUrls().size());
        assertEquals("https://example.com", config.getStartUrls().get(0).toString());
        assertEquals("https://another.com", config.getStartUrls().get(1).toString());
        assertTrue(config.getAllowedDomains().contains("example.com"));
        assertTrue(config.getAllowedDomains().contains("another.com"));
    }

    @Test
    void buildConfigFromArgs_shouldReturnNullIfArgsAreInvalid() {
        String[] args = {
                "not_a_url",
                "foo",
                "   "
        };

        CrawlerConfig config = Main.buildConfigFromArgs(args);
        assertNull(config);
    }
}
