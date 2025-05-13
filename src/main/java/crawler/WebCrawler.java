package crawler;

import crawler.model.CrawledPage;

import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WebCrawler {

    private final CrawlerConfig config;
    private final Set<String> visitedPages;
    private final List<CrawledPage> resultsList;
    private final PageProcessor pageProcessor;
    private final ExecutorService executor;
    private final CompletionService<Void> completionService;
    private final AtomicInteger submittedTaskCount = new AtomicInteger(0);
    private final int numberOfThreadsForExecutor = 20;

    public WebCrawler(CrawlerConfig config, PageProcessor pageProcessor) {
        this.config = config;
        this.visitedPages = ConcurrentHashMap.newKeySet();
        this.resultsList = Collections.synchronizedList(new ArrayList<>());
        this.pageProcessor = pageProcessor;
        this.executor = Executors.newFixedThreadPool(numberOfThreadsForExecutor);
        this.completionService = new ExecutorCompletionService<>(executor);
    }

    protected List<CrawledPage> crawl() {
        for (URL url : config.getStartUrls()) {
            submitCrawlTask(url.toString(), 0, url);
        }

        for (int i = 0; i < submittedTaskCount.get(); i++) {
            try {
                completionService.take().get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Crawling interrupted.");
            } catch (ExecutionException e) {
                System.err.println("Crawling task failed: " + e.getCause());
            }
        }

        executor.shutdown();
        return resultsList;
    }

    private void submitCrawlTask(String url, int depth, URL rootStartUrl) {
        submittedTaskCount.incrementAndGet();
        completionService.submit(() -> {
            crawlRecursively(url, depth, rootStartUrl);
            return null;
        });
    }

    private void crawlRecursively(String url, int currentDepth, URL rootStartUrl) {
        String normalized = WebCrawlerUtils.normalizeUrl(url);

        if (!shouldCrawl(url, currentDepth)) return;

        if (visitedPages.contains(normalized)) {
            addStartUrlToExistingPage(normalized, rootStartUrl);
            return;
        }
        visitedPages.add(normalized);

        System.out.printf("Crawling at %s (depth %d)\n", url, currentDepth);

        CrawledPage page = pageProcessor.processPage(url, currentDepth);
        page.fromStartUrls.add(rootStartUrl);
        resultsList.add(page);

        if (page.isBroken || page.links == null) return;

        for (String link : page.links) {
            String normalizedLink = WebCrawlerUtils.normalizeUrl(link);
            if (!normalizedLink.isEmpty()) {
                submitCrawlTask(link, currentDepth + 1, rootStartUrl);
            }
        }
    }

    private void addStartUrlToExistingPage(String normalizedUrl, URL rootStartUrl) {
        for (CrawledPage page : resultsList) {
            if (normalizedUrl.equals(WebCrawlerUtils.normalizeUrl(page.url))) {
                page.fromStartUrls.add(rootStartUrl);
                break;
            }
        }
    }

    protected boolean shouldCrawl(String url, int currentDepth) {
        if (currentDepth > config.getMaxDepth()) return false;
        if (url.isEmpty()) return false;

        String normalized = WebCrawlerUtils.normalizeUrl(url);
        if (!WebCrawlerUtils.isDomainAllowed(normalized, config.getAllowedDomains())) return false;

        return true;
    }
}
