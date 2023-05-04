package crawler.application.services;

import crawler.constants.Constants;

public class CrawlerNoopService extends AbstractCrawlerService implements crawler.domain.services.CrawlerService{

    public CrawlerNoopService(){
        super();
    }

    @Override
    public void crawl(String domain) throws InterruptedException {

        // Transform domain to URL
        String url = "https://" + domain;

        // Enable dev tools listener
        super.enableDevToolsListener();

        // Statistics
        super.websiteStatistic.domain = domain;
        super.websiteStatistic.postPageLoadURL = driver.getCurrentUrl();
        super.websiteStatistic.pageLoadStartTimestamp = System.currentTimeMillis();

        // Navigate to URL
        if(!super.navigateToURL(url)) return;

        // Statistics
        super.websiteStatistic.pageLoadEndTimestamp = System.currentTimeMillis();

        // Sleep to await page loading
        Thread.sleep(Constants.SLEEP_BEFORE_PAGE_LOAD_MILLISECONDS);

        // Take screenshot after page load
        super.screenShotService.takeScreenShot(domain, false, false);

        // Save website statistics to JSON file
        super.saveWebsiteStatisticsToJson(super.websiteStatistic, false);

        // Close the browser session
        super.driver.quit();
    }
}
