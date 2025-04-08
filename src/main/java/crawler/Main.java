package main.java.crawler;

import main.java.crawler.model.CrawledPage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java -jar crawler.jar <URL> <depth> <domain1,domain2,...>");
            return;
        }

        String startUrlAddress = args[0];
        int maxDepth;
        URL startUrl;
        try {
            startUrl = new URL(startUrlAddress);
        } catch (MalformedURLException e) {
            System.out.println("Invalid start URL: " + startUrlAddress);
            return;
        }

        try {
            maxDepth = Integer.parseInt(args[1]);
            if (maxDepth < 0) {
                System.out.println("Depth must be >= 0.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid depth: " + args[1]);
            return;
        }

        Set<String> allowedDomains = new HashSet<>(Arrays.asList(args[2].split(",")));
        if (allowedDomains.isEmpty()) {
            System.out.println("Please provide at least one allowed domain.");
            return;
        }

        CrawlerConfig config = new CrawlerConfig(startUrl, maxDepth, allowedDomains);
        WebCrawler crawler = new WebCrawler(config);

        System.out.println("Starting crawl from: " + startUrl);
        List<CrawledPage> results = crawler.crawl();

        MarkdownWriter writer = new MarkdownWriter();

        writer.write(results, "report.md");
        System.out.println("Report written to report.md");

    }
}
