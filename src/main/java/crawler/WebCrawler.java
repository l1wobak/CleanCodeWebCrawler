package crawler;

import crawler.model.CrawledPage;

import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

public class WebCrawler {

    private final CrawlerConfig config;
    private final Set<String> visitedPages;
    private final List<CrawledPage> resultsList;
    private final PageProcessor pageProcessor;
    private final ExecutorService executor;

    public WebCrawler(CrawlerConfig config, PageProcessor pageProcessor) {
        this.config = config;
        this.visitedPages = ConcurrentHashMap.newKeySet();
        this.resultsList = Collections.synchronizedList(new ArrayList<>());
        this.pageProcessor = pageProcessor;
        this.executor = Executors.newFixedThreadPool(
                Math.max(4, Runtime.getRuntime().availableProcessors()));
    }

    protected List<CrawledPage> crawl() {
        List<Future<?>> futures = new ArrayList<>();

        // Start a crawl task for each start URL
        for (URL url : config.getStartUrls()) {
            submitCrawlTask(url.toString(), 0, futures);
        }

        // Wait for all crawl tasks to finish
        for (Future<?> future : futures) {
            try {
                future.get();
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

    private void submitCrawlTask(String url, int depth, List<Future<?>> futures) {
        Future<?> future = executor.submit(() -> crawlRecursively(url, depth, futures));
        synchronized (futures) {
            futures.add(future);
        }
    }

    private void crawlRecursively(String url, int currentDepth, List<Future<?>> futures) {
        if (!shouldCrawl(url, currentDepth)) return;

        String normalized = WebCrawlerUtils.normalizeUrl(url);
        visitedPages.add(normalized);

        System.out.printf("Crawling at %s (depth %d)\n", url, currentDepth);

        CrawledPage page = pageProcessor.processPage(url, currentDepth);
        resultsList.add(page);

        if (page.isBroken) return;

        for (String link : page.links) {
            String normalizedLink = WebCrawlerUtils.normalizeUrl(link);
            if (!normalizedLink.isEmpty() && !visitedPages.contains(normalizedLink)) {
                submitCrawlTask(link, currentDepth + 1, futures);
            }
        }
    }

    protected boolean shouldCrawl(String url, int currentDepth) {
        if (currentDepth > config.getMaxDepth()) return false;
        if (url.isEmpty()) return false;

        String normalized = WebCrawlerUtils.normalizeUrl(url);
        if (visitedPages.contains(normalized)) return false;
        if (!WebCrawlerUtils.isDomainAllowed(normalized, config.getAllowedDomains())) return false;

        return true;
    }
}
