package crawler.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import crawler.domain.WebsiteStatistic;

import java.io.File;
import java.io.IOException;

public class JsonWriterService implements crawler.domain.services.JsonWriterService {

    @Override
    public void writeWebsiteStatisticToJSON(WebsiteStatistic websiteStatistic, Boolean acceptCookie) {

        // Create an ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();

        // Create filename
        String filename = websiteStatistic.domain;
        filename += acceptCookie ? "_accept.json" : "_noop.json";

        // Create an ObjectWriter with pretty printer
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

        // Convert the Java object to JSON and write to a file
        try {
            writer.writeValue(new File(System.getProperty("user.dir") + "/../crawl_data/" + filename), websiteStatistic);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
