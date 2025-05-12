package crawler.model;

import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CrawledPage {
    public String url;
    public int depth;
    public List<String> headings;
    public List<String> links;
    public boolean isBroken;
    public Set<URL> fromStartUrls = ConcurrentHashMap.newKeySet();

}
