package crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

public class WebCrawlerUtils {

    public static String normalizeUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            String path = url.getPath().replaceAll("/$", "");
            return url.getProtocol() + "://" + url.getHost() + path;
        } catch (MalformedURLException e) {
            return "";
        }
    }

    public static boolean isDomainAllowed(String urlString, Set<String> allowedDomains) {
        try {
            URL url = new URL(urlString);
            return allowedDomains.contains(url.getHost());
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
