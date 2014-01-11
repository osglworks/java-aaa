package org.osgl.aaa0;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoAccessException extends RuntimeException {

    private static final long serialVersionUID = 1080270792010078044L;
    private static final Logger logger = LoggerFactory.getLogger(NoAccessException.class);

    public NoAccessException() {
    }

    public NoAccessException(Throwable cause) {
        super(cause.getMessage());
        logger.warn("Access denied", cause);
    }

    public NoAccessException(String message) {
        super(message);
    }

    public NoAccessException(String message, Throwable cause) {
        super(message);
        logger.warn("Access denied: " + message, cause);
    }

}
