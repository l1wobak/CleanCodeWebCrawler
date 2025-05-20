package crawler.model;

import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CrawledPage {
    private String url;
    private int depth;
    private List<String> headings;
    private List<String> links;
    private boolean isBroken;
    private Set<URL> fromStartUrls = ConcurrentHashMap.newKeySet();

    public CrawledPage(String url, int depth, List<String> headings, List<String> links, boolean isBroken) {
        this.url = url;
        this.depth = depth;
        this.headings = headings;
        this.links = links;
        this.isBroken = isBroken;
    }
    public CrawledPage(){}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public List<String> getHeadings() {
        return headings;
    }

    public void setHeadings(List<String> headings) {
        this.headings = headings;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public boolean isBroken() {
        return isBroken;
    }

    public void setBroken(boolean broken) {
        isBroken = broken;
    }

    public Set<URL> getFromStartUrls() {
        return fromStartUrls;
    }

    public void setFromStartUrls(Set<URL> fromStartUrls) {
        this.fromStartUrls = fromStartUrls;
    }
}
