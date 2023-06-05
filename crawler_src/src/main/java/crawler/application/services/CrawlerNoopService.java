package crawler.application.services;

import crawler.constants.Constants;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class CrawlerNoopService extends AbstractCrawlerService implements crawler.domain.services.CrawlerService{

    public CrawlerNoopService(){
        super();
    }

    @Override
    public void crawl(String domain) throws InterruptedException {

        // Initialize driver and SSS
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        chromeOptions.setHeadless(true);

        super.driver = new ChromeDriver(chromeOptions);
        super.driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

        this.screenShotService = new ScreenShotService(this.driver);

        // Transform domain to URL
        String url = "https://" + domain;

        // Enable dev tools listener
        super.enableDevToolsListener();

        // Statistics
        super.websiteStatistic.domain = domain;
        super.websiteStatistic.postPageLoadURL = driver.getCurrentUrl();
        super.websiteStatistic.pageLoadStartTimestamp = System.currentTimeMillis();

        // Navigate to URL
        if(!super.navigateToURL(url)) {
            // Write PageLoadTimeOut error
            this.saveWebsiteStatisticsToJson(this.websiteStatistic, false);
            return;
        }

        // Poll devTool logs for canvas fingerprinting
        super.detectCanvasFingerprinting();

        // Statistics
        super.websiteStatistic.pageLoadEndTimestamp = System.currentTimeMillis();

        // Sleep to await page loading
        Thread.sleep(Constants.SLEEP_BEFORE_PAGE_LOAD_MILLISECONDS);

        // Take screenshot after page load
        super.screenShotService.takeScreenShot(domain, false, false);

        // Save cookies
        Set<Cookie> cookies = this.driver.manage().getCookies();
        this.websiteStatistic.cookies = cookies;

        // Save website statistics to JSON file
        super.saveWebsiteStatisticsToJson(super.websiteStatistic, false);

        // Close the browser session
        super.driver.quit();
    }
}
