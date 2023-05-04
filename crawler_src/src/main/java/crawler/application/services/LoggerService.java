package crawler.application.services;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerService implements crawler.domain.services.LoggerService {

    Logger logger = Logger.getLogger(crawler.application.services.LoggerService.class.getName());

    public void log(Level level, String message) {

        // Set visibility
        logger.setLevel(Level.ALL);

        // Create consoleHandler and attach
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);

        // Log message
        logger.log(level, message);
    }
}
