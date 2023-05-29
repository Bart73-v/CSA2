package crawler.domain.services;

import crawler.domain.WebsiteStatistic;
import org.openqa.selenium.WebDriver;

public interface CrawlerService  {
    void crawl(String domain) throws InterruptedException;

    void takeScreenShot(String domain, boolean consented);

    void takeScreenShot(String domain, boolean consented, Boolean postConsented);

    void detectCanvasFingerprinting();

    boolean navigateToURL(String url);

    boolean clickBanner(WebDriver driver, String resourceWordlist);

    void enableDevToolsListener();

    void saveWebsiteStatisticsToJson(WebsiteStatistic websiteStatistic, Boolean acceptCookies);

    void dumpOnly(String domain, boolean acceptCookies);
}
