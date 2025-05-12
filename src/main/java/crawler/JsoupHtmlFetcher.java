package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class JsoupHtmlFetcher implements HtmlFetcher {

    private final int timeoutMillis;

    public JsoupHtmlFetcher(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    public Document fetchDocumentFromUrl(String url) throws IOException {
        return Jsoup.connect(url)
                .timeout(timeoutMillis)
                .get();
    }
}
