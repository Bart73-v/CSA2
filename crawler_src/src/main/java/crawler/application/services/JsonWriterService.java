package crawler.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import crawler.domain.WebsiteStatistic;

import java.io.File;
import java.io.IOException;

public class JsonWriterService implements crawler.domain.services.JsonWriterService {

    @Override
    public void writeWebsiteStatisticToJSON(WebsiteStatistic websiteStatistic, Boolean acceptCookie) {
        String outputDir = getClass().getProtectionDomain().getCodeSource().getLocation().toString();
        outputDir = outputDir.substring(6);

        // Get env variable and correct output location
        String sysEnvStr = System.getenv("CRAWLER_DEV_MODE");

        // Create an ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();

        // Create filename
        String filename = websiteStatistic.domain;
        filename += acceptCookie ? "_accept.json" : "_noop.json";

        String outputFile;

        if (sysEnvStr != null){
            outputFile = outputDir + "..\\..\\..\\crawl_data\\" + filename;
        } else {
            outputDir = outputDir.substring(0, outputDir.length() - 11);
            outputFile = outputDir + "crawl_data\\" + filename;
        }

        // Create an ObjectWriter with pretty printer
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

        // Convert the Java object to JSON and write to a file
        try {
            writer.writeValue(new File(outputFile), websiteStatistic);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
