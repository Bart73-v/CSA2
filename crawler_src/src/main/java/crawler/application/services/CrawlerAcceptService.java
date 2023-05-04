package crawler.application.services;

import crawler.constants.Constants;

import java.util.logging.Level;

public class CrawlerAcceptService extends AbstractCrawlerService implements crawler.domain.services.CrawlerService {

    public CrawlerAcceptService(){
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

        // Sleep to await page loading before accepting cookie
        Thread.sleep(Constants.SLEEP_BEFORE_PAGE_LOAD_MILLISECONDS);

        // Take screenshot before accepting cookie banner
        super.screenShotService.takeScreenShot(domain, true, false);

        // Accept cookie
        if (!super.clickBanner(super.driver, Constants.RESOURCE_ACCEPT_WORDS)){
            super.loggerService.log(Level.WARNING, String.format("All acceptor methods failed for domain %s. " +
                    "Could not accept cookie!", domain));
        }

        // Sleep to await page loading after accepting cookie
        Thread.sleep(Constants.SLEEP_AFTER_PAGE_LOAD_MILLISECONDS);

        // Take screenshot after accepting cookie banner
        super.screenShotService.takeScreenShot(domain, true, true);

        // Save website statistics to JSON file
        super.saveWebsiteStatisticsToJson(super.websiteStatistic, true);

        // Close the browser session
        super.driver.quit();
    }


//
//    // Navigate to a website that uses canvas fingerprinting
//driver.get("https://amibeingtracked.com/");
//
//    // Execute JavaScript to get the canvas fingerprint
//    JavascriptExecutor js = (JavascriptExecutor) driver;
//    String canvasFingerprint = (String) js.executeScript("return document.createElement('canvas').toDataURL();");
//
//    // Compare the canvas fingerprint with a known list of fingerprints that are used by websites for tracking
//    List<String> knownFingerprints = Arrays.asList("d446ce6e8822fe2f1a8a1fc649f9acbf", "b14a478cfe8d7a7b0c5b45e0a1a52de7");
//if (knownFingerprints.contains(getMD5Hash(canvasFingerprint))) {
//        System.out.println("Canvas fingerprinting detected!");
//    } else {
//        System.out.println("Canvas fingerprinting not detected.");
//    }
//
//    // Utility function to get the MD5 hash of a string
//    private static String getMD5Hash(String input) throws NoSuchAlgorithmException {
//        MessageDigest md = MessageDigest.getInstance("MD5");
//        md.update(input.getBytes());
//        byte[] digest = md.digest();
//        return DatatypeConverter.printHexBinary(digest).toLowerCase();
//    }






}
