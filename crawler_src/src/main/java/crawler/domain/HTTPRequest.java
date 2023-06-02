package crawler.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openqa.selenium.Cookie;

import java.util.Set;

public class HTTPRequest {

    @JsonIgnore
    public String requestID;

    public String requestURL;
    public long requestTimestamp;
    public String requestHeader;
    public String responseHeader;
//    public Set<Cookie> cookies;
}
