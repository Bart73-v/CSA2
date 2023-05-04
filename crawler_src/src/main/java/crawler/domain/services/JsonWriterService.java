package crawler.domain.services;

import crawler.domain.WebsiteStatistic;

public interface JsonWriterService {
    void writeWebsiteStatisticToJSON(WebsiteStatistic websiteStatistic, Boolean acceptCookies);
}
