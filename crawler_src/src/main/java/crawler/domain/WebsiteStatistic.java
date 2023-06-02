package crawler.domain;

import org.openqa.selenium.Cookie;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WebsiteStatistic {

    public String domain;
    public String postPageLoadURL;
    public long pageLoadStartTimestamp;
    public long pageLoadEndTimestamp;
    public boolean isCanvasFingerprinting;
    public List<HTTPRequest> httpRequests = new ArrayList<>();
    public String toDataUrl = "";
    public boolean consentClickError = false;
    public boolean pageLoadTimeout = false;
    public boolean dnsError = false;
    public Set<Cookie> cookies;
}
