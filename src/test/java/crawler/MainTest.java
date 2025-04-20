package crawler;

import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void parseStartUrl_shouldReturnValidUrlForValidInput() {
        // Arrange
        String input = "https://example.com";

        // Act
        URL url = Main.parseStartUrl(input);

        // Assert
        assertNotNull(url);
        assertEquals(input, url.toString());
    }

    @Test
    void parseStartUrl_shouldReturnNullForInvalidUrlInput() {
        // Arrange
        String input = "ht!tp://::invalid-url";

        // Act
        URL url = Main.parseStartUrl(input);

        // Assert
        assertNull(url);
    }

    @Test
    void parseDepth_shouldReturnIntegerForValidInput() {
        // Act
        int depth = Main.parseDepth("3");

        // Assert
        assertEquals(3, depth);
    }

    @Test
    void parseDepth_shouldReturnMinusOneForNegativeInput() {
        // Act
        int depth = Main.parseDepth("-1");

        // Assert
        assertEquals(-1, depth);
    }

    @Test
    void parseDepth_shouldReturnMinusOneForInvalidInput() {
        // Act
        int depth = Main.parseDepth("abc");

        // Assert
        assertEquals(-1, depth);
    }

    @Test
    void parseAllowedDomains_shouldReturnCleanedSetForValidInput() {
        // Act
        Set<String> result = Main.parseAllowedDomains(" https://example.com ,HTTP://Another.com  ");

        // Assert
        assertTrue(result.contains("https://example.com"));
        assertTrue(result.contains("http://another.com"));
        assertEquals(2, result.size());
    }

    @Test
    void parseAllowedDomains_shouldReturnEmptySetForBlankInput() {
        // Act
        Set<String> result = Main.parseAllowedDomains("   ");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void buildConfigFromArgs_shouldReturnValidConfigIfArgsAreValid() {
        // Arrange
        String[] args = {
                "https://example.com",
                "1",
                "https://example.com,https://another.com"
        };

        // Act
        CrawlerConfig config = Main.buildConfigFromArgs(args);

        // Assert
        assertNotNull(config);
        assertEquals(1, config.getMaxDepth());
        assertEquals( "https://example.com", config.getStartUrl().toString());
        assertTrue(config.getAllowedDomains().contains("example.com"));
    }

    @Test
    void buildConfigFromArgs_shouldReturnNullIfArgsAreInvalid() {
        // Arrange
        String[] args = {
                "not_a_url",
                "foo",
                "   "
        };

        // Act
        CrawlerConfig config = Main.buildConfigFromArgs(args);

        // Assert
        assertNull(config);
    }
}
