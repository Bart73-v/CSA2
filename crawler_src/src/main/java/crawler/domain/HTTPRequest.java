package crawler.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class HTTPRequest {

    @JsonIgnore
    public String requestID;

    public String requestURL;
    public long requestTimestamp;
    public String requestHeader;
    public String responseHeader;
}
