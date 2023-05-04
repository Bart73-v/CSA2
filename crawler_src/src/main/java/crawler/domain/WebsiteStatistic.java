package crawler.domain;

import java.util.ArrayList;
import java.util.List;

public class WebsiteStatistic {

    public String domain;
    public String postPageLoadURL;
    public long pageLoadStartTimestamp;
    public long pageLoadEndTimestamp;
    public List<HTTPRequest> httpRequests = new ArrayList<>();
}
