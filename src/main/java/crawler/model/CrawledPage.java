package crawler.model;

import java.util.List;

public class CrawledPage {
    public String url;
    public int depth;
    public List<String> headings;
    public List<String> links;
    public boolean isBroken;
}
