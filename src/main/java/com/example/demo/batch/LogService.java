package com.example.demo.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    public void logInfo(String message) {
        logger.info(message);
    }

    public void logError(String message, Exception e) {
        logger.error(message, e);
    }

    public void logWarn(String message) {
        logger.warn(message);
    }

    public void logDebug(String message) {
        logger.debug(message);
    }
}
