package crawler.main;

import crawler.application.services.CrawlerAcceptService;
import crawler.application.services.CrawlerNoopService;
import crawler.domain.services.CrawlerService;
import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        // Parse CLI arguments
        parseCLIArguments(args);
    }

    public static void parseCLIArguments(String[] args){
        // Define options
        Options options = new Options();
        options.addOption("u", "url", true, "Target website URL/domain");
        options.addOption("i", "input-file", true, "List of target website URLs/domains in a file");
        options.addOption("a", "accept", false, "Enable consent mode");
        options.addOption("n", "noop", false, "Enable no consent mode");

        // Parse command line arguments
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            boolean acceptMode = cmd.hasOption("a");
            boolean noopMode = cmd.hasOption("n");

            String targetUrl = cmd.getOptionValue("u");
            List<String> targetUrls = new ArrayList<>();
            if (cmd.hasOption("i")) {
                String filename = cmd.getOptionValue("i");
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(filename));
                    String line = reader.readLine();
                    while (line != null) {
                        targetUrls.add(line);
                        line = reader.readLine();
                    }
                    reader.close();
                } catch (Exception e) {
                    System.err.println("Error reading file: " + filename);
                    e.printStackTrace();
                    System.exit(1);
                }
            }

            // Validate arguments
            if (targetUrl == null && targetUrls.isEmpty()) {
                System.err.println("Error: please specify a target URL with -u or a file with -i");
                System.exit(1);
            }

            // Start crawling
            if (targetUrl != null) {
                startCrawling(targetUrl, acceptMode, noopMode);
            } else {
                for (String url : targetUrls) {
                    startCrawling(url, acceptMode, noopMode);
                }
            }

        } catch (ParseException e) {
            System.err.println("Error parsing command line arguments: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Crawler", options);
            System.exit(1);
        }
    }

    public static void startCrawling(String url, boolean acceptMode, boolean noopMode) {

        String domain = "";
        try {
            URL netUrl = new URL(url.startsWith("http") ? url : "https://" + url);
            domain = netUrl.getHost();

            if (domain.startsWith("www.")) {
                domain = domain.substring(4);
            }
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL: " + url);
            e.printStackTrace();
        }

        try {
            if (acceptMode) {
                CrawlerService crawlerAcceptService = new CrawlerAcceptService();
                crawlerAcceptService.crawl(domain);

            } if (noopMode){
                CrawlerService crawlerNoopService = new CrawlerNoopService();
                crawlerNoopService.crawl(domain);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    public static void main(String[] args) {
//
//        CrawlerService crawlerService = new CrawlerAcceptService();
////        crawlerService.crawl("https://wordpress.com");
////        crawlerService.crawl("https://google.com");
//        crawlerService.crawl("https://google.com:81");
////        crawlerService.crawl("https://tumblr.com");
//    }
}
