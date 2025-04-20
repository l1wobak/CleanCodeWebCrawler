package crawler;

import crawler.model.CrawledPage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java -jar crawler.jar <URL> <depth> <domain1,domain2,...>");
            return;
        }

        CrawlerConfig config = buildConfigFromArgs(args);
        if (config == null) return;

        List<CrawledPage> results = runCrawl(config);
        writeReport(results);
    }

    private static CrawlerConfig buildConfigFromArgs(String[] args) {
        URL startUrl = parseStartUrl(args[0]);
        if (startUrl == null) return null;

        int maxDepth = parseDepth(args[1]);
        if (maxDepth < 0) return null;

        Set<String> allowedDomains = parseAllowedDomains(args[2]);
        if (allowedDomains.isEmpty()) return null;

        try {
            CrawlerConfig config = new CrawlerConfig(startUrl, maxDepth, allowedDomains);
            System.out.println("Configuration loaded: " + config);
            return config;
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid configuration: " + e.getMessage());
            return null;
        }
    }

    private static URL parseStartUrl(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            System.out.println("Invalid start URL: " + urlString);
            return null;
        }
    }

    private static int parseDepth(String depthStr) {
        try {
            int depth = Integer.parseInt(depthStr);
            if (depth < 0) {
                System.out.println("Depth must be >= 0.");
                return -1;
            }
            return depth;
        } catch (NumberFormatException e) {
            System.out.println("Invalid depth: " + depthStr);
            return -1;
        }
    }

    private static Set<String> parseAllowedDomains(String domainArg) {
        if (domainArg == null || domainArg.isBlank()) {
            System.out.println("No domains provided.");
            return Collections.emptySet();
        }

        String[] domainArray = domainArg.split(",");
        Set<String> domains = new HashSet<>();
        for (String d : domainArray) {
            String cleaned = d.trim().toLowerCase();
            if (!cleaned.isEmpty()) {
                domains.add(cleaned);
            }
        }

        if (domains.isEmpty()) {
            System.out.println("Please provide at least one valid domain.");
        }

        return domains;
    }

    private static List<CrawledPage> runCrawl(CrawlerConfig config) {
        System.out.println("Starting crawl from: " + config.getStartUrl());

        WebCrawler crawler = new WebCrawler(config);
        return crawler.crawl();
    }

    private static void writeReport(List<CrawledPage> results) {
        MarkdownWriter writer = new MarkdownWriter();
        try {
            writer.write(results, "report.md");
            System.out.println("Report written to report.md");
        } catch (IOException e) {
            System.out.println("Error writing report: " + e.getMessage());
        }
    }
}
