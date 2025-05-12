package crawler;

import org.jsoup.nodes.Document;

public interface HtmlFetcher {
    /**
     * Fetches and parses an HTML page from the given URL.
     *
     * @param url The URL to fetch.
     * @return Parsed JSoup Document.
     * @throws Exception If fetching or parsing fails.
     */
    Document fetchDocumentFromUrl(String url) throws Exception;
}

