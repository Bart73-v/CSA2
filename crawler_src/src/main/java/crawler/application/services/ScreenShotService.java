package crawler.application.services;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

public class ScreenShotService implements crawler.domain.services.ScreenShotService {

    private final WebDriver driver;
    private String domain;
    private Boolean consented;
    private Boolean postConsented;

    public ScreenShotService(WebDriver driver){
        this.driver = driver;
    }

    @Override
    public String buildFileString() {
        String outputFile = System.getProperty("user.dir") + "/../crawl_data/" + domain;

        if (consented) {
            outputFile += postConsented ? "_accept_post_" : "_accept_pre_";
            outputFile += "consent";
        } else {
            outputFile += "_noop";
        }

        outputFile += ".png";

        return outputFile;
    }


    @Override
    public void takeScreenShot(String domain, Boolean consented) {
        this.domain = domain;
        this.consented = consented;

        // Take and save screenshot
        File scrFile = ((TakesScreenshot)this.driver).getScreenshotAs(OutputType.FILE);
        this.saveScreenShot(scrFile);
    }

    @Override
    public void takeScreenShot(String domain, Boolean consented, Boolean postConsented) {
        this.domain = domain;
        this.consented = consented;
        this.postConsented = postConsented;

        // Take and save screenshot
        File scrFile = ((TakesScreenshot)this.driver).getScreenshotAs(OutputType.FILE);
        this.saveScreenShot(scrFile);
    }

    @Override
    public void saveScreenShot(File srcFile) {

        // Create filename
        String fileName = this.buildFileString();

        try {
            FileUtils.copyFile(srcFile, new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
