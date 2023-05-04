package crawler.domain.services;

import java.io.File;

public interface ScreenShotService {

    String buildFileString();

    void takeScreenShot(String domain, Boolean consented);

    void takeScreenShot(String domain, Boolean consented, Boolean postConsented);

    void saveScreenShot(File srcFile);
}
