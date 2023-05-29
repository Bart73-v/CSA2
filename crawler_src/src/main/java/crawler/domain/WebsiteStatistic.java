package crawler.domain;

import java.util.ArrayList;
import java.util.List;

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
}
