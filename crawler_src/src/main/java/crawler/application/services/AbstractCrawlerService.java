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
import org.openqa.selenium.devtools.v110.emulation.Emulation;
import org.openqa.selenium.devtools.v110.log.Log;
import org.openqa.selenium.devtools.v110.network.Network;
import org.openqa.selenium.devtools.v110.page.Page;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;

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
        this.websiteStatistic = new WebsiteStatistic();
        this.screenShotService = new ScreenShotService(this.driver);
        this.jsonWriterService = new JsonWriterService();
        this.loggerService = new LoggerService();
    }

    @Override
    public boolean navigateToURL(String url){
        try {
            // Get the DevTools interface
            DevTools devTools = ((ChromeDriver) driver).getDevTools();

            // Start the DevTools session
            devTools.createSession();

            // Enable logging
            devTools.send(Log.enable());

            // Enable the Page and Emulation domains
            devTools.send(Page.enable());

            // Pause JavaScript execution on the page
            devTools.send(Emulation.setScriptExecutionDisabled(true));

            // Script to inject to detect canvas fingerprinting
            String script =  "HTMLCanvasElement.prototype.toDataURL = function() {" +
                    "console.error(\"Canvas toDataURL() called\");" +
                    "return;" +
                    "};" +
                    "CanvasRenderingContext2D.prototype.fillText = function() {" +
                    "console.error(\"Canvas fillText() called\");" +
                    "};";

            // Inject your custom JavaScript code
            devTools.send(Page.addScriptToEvaluateOnNewDocument(script, Optional.of(""), Optional.empty()));

            // Resume JavaScript execution on the page
            devTools.send(Emulation.setScriptExecutionDisabled(false));

            // Navigate to the page
            driver.get(url);

            // Resume JavaScript execution on the page
            devTools.send(Emulation.setScriptExecutionDisabled(false));

            // Sleep to allow website to execute canvas fingerprinting
            Thread.sleep(1500);

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
        // Search for needle within console errors
        LogEntries logs = driver.manage().logs().get("browser");
        if (logs != null) {
            for (LogEntry entry : logs) {
                if (entry.getMessage().contains("Canvas")) {
                    this.loggerService.log(Level.INFO, "Potential canvas fingerprinting detected.");
                    this.websiteStatistic.isCanvasFingerprinting = true;
                    return;
                }
            }
        }

        this.websiteStatistic.isCanvasFingerprinting = false;
        this.loggerService.log(Level.INFO, "Canvas fingerprinting not detected.");
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