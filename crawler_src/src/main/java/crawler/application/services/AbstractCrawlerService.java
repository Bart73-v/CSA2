package crawler.application.services;
import crawler.constants.Constants;
import crawler.domain.HTTPRequest;
import crawler.domain.WebsiteStatistic;
import crawler.domain.services.CrawlerService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v110.network.Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

public class AbstractCrawlerService implements CrawlerService {

    public final crawler.domain.services.ScreenShotService screenShotService;
    public final crawler.domain.services.JsonWriterService jsonWriterService;
    public final crawler.domain.services.LoggerService loggerService;
    public final WebDriver driver;
    public WebsiteStatistic websiteStatistic;

    public AbstractCrawlerService(){
        this.driver = new ChromeDriver();
        // todo
//        this.driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
//        this.driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
//        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(5));

        this.websiteStatistic = new WebsiteStatistic();

        this.screenShotService = new ScreenShotService(this.driver);
        this.jsonWriterService = new JsonWriterService();
        this.loggerService = new LoggerService();
    }

    @Override
    public boolean navigateToURL(String url){
        try {
            this.driver.get(url);
        } catch (Exception e){
            this.loggerService.log(Level.SEVERE, String.format("URL %s seems down.", url));
            return false;
        }

        return true;
    }

    @Override
    public void takeScreenShot(String domain, boolean consented) {
        this.screenShotService.takeScreenShot(domain, consented);
    }

    @Override
    public void takeScreenShot(String domain, boolean consented, Boolean postConsented) {
        this.screenShotService.takeScreenShot(domain, consented, postConsented);
    }

    @Override
    public void enableDevToolsListener() {
        DevTools devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();

        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        devTools.addListener(Network.requestWillBeSent(), request -> {

            try {
                HTTPRequest httpRequest = new HTTPRequest();
                httpRequest.requestURL = request.getRequest().getUrl();
                httpRequest.requestHeader = request.getRequest().getHeaders().toString().substring(0, 512);
                httpRequest.requestTimestamp = System.currentTimeMillis();
                httpRequest.requestID = request.getRequestId().toString();

                this.websiteStatistic.httpRequests.add(httpRequest);
            } catch (Exception e){
                this.loggerService.log(Level.SEVERE, "DevTools could not capture request.");
            }
        });

        devTools.addListener(Network.responseReceived(), response -> {
            try{
                // Get HTTP request by request ID
                Optional<HTTPRequest> httpRequestOptional = this.websiteStatistic.httpRequests.stream()
                        .filter(httpRequest -> httpRequest.requestID.equals(response.getRequestId().toString()))
                        .findFirst();

                // Set response header
                if (httpRequestOptional.isPresent()) {
                    // Set response header
                    HTTPRequest httpRequest = httpRequestOptional.get();
                    httpRequest.responseHeader = response.getResponse().getHeaders().toString().substring(0, 512);
                }
            }
            catch(Exception e){
                this.loggerService.log(Level.SEVERE, "DevTools could not capture response.");
            }

        });
    }

    @Override
    public void detectCanvasFingerprinting() {

    }

    @Override
    public void saveWebsiteStatisticsToJson(WebsiteStatistic websiteStatistic, Boolean acceptCookies) {
        this.jsonWriterService.writeWebsiteStatisticToJSON(websiteStatistic, acceptCookies);
    }

    @Override
    public boolean clickBanner(WebDriver driver, String resourceWordlist) {
        // Create streams
        List<String> acceptWordsList = getResourceLines(resourceWordlist);
        List<WebElement> contents = driver.findElements(By.cssSelector(Constants.GLOBAL_SELECTOR));
        WebElement candidate = contents.stream()
                .filter(c -> acceptWordsList.contains(c.getText().toLowerCase().replaceAll("[ ✓›!\n]", "")))
                .findFirst()
                .orElse(null);

        // Click the candidate
        if (candidate != null) {
            try {
                candidate.click();
                return true;
            } catch (Exception e) {
                this.loggerService.log(Level.WARNING, "Could not click candidate.");
            }
        }
        return false;
    }

    protected List<String> getResourceLines(String resourceName) {
        List<String> lines = new ArrayList<>();

        // Read lines and filter by conditions
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(CrawlerAcceptService.class.getResourceAsStream(resourceName))))) {
            br.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .forEach(lines::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    @Override
    public void crawl(String domain) throws InterruptedException {}
}
