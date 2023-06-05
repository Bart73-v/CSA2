package crawler.application.services;

import crawler.constants.Constants;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class CrawlerAcceptService extends AbstractCrawlerService implements crawler.domain.services.CrawlerService {

    public CrawlerAcceptService(){
        super();
    }

    @Override
    public void crawl(String domain) throws InterruptedException {

        // Initialize driver and SSS
        super.driver = new ChromeDriver();
        super.driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

        this.screenShotService = new ScreenShotService(this.driver);

        // Transform domain to URL
        String url = "https://" + domain;

        // Enable dev tools listener
        super.enableDevToolsListener();

        // Statistics
        super.websiteStatistic.domain = domain;
        super.websiteStatistic.pageLoadStartTimestamp = System.currentTimeMillis();

        // Navigate to URL
        if(!super.navigateToURL(url)) {
            // Write PageLoadTimeOut error
            this.saveWebsiteStatisticsToJson(this.websiteStatistic, true);
            return;
        }

        // Canvas fingerprinting
        super.detectCanvasFingerprinting();

        // Statistics
        super.websiteStatistic.pageLoadEndTimestamp = System.currentTimeMillis();
        super.websiteStatistic.postPageLoadURL = driver.getCurrentUrl();

        // Sleep to await page loading before accepting cookie
        Thread.sleep(Constants.SLEEP_BEFORE_PAGE_LOAD_MILLISECONDS);

        // Take screenshot before accepting cookie banner
        super.screenShotService.takeScreenShot(domain, true, false);

        // Accept cookie
        if (!super.clickBanner(super.driver, Constants.RESOURCE_ACCEPT_WORDS)){
            super.websiteStatistic.consentClickError = true;
            super.loggerService.log(Level.WARNING, String.format("All acceptor methods failed for domain %s. " +
                    "Could not accept cookie!", domain));
        }

        // Sleep to await page loading after accepting cookie
        Thread.sleep(Constants.SLEEP_AFTER_PAGE_LOAD_MILLISECONDS);

        // Take screenshot after accepting cookie banner
        super.screenShotService.takeScreenShot(domain, true, true);

        // Save cookies
        Set<Cookie> cookies = this.driver.manage().getCookies();
        this.websiteStatistic.cookies = cookies;

        // Save website statistics to JSON file
        super.saveWebsiteStatisticsToJson(super.websiteStatistic, true);

        // Close the browser session
        super.driver.quit();
    }
}
