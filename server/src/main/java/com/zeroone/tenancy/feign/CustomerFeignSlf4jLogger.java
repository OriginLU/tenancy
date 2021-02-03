package com.zeroone.tenancy.feign;

import feign.Request;
import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CustomerFeignSlf4jLogger extends feign.Logger {


    private final Logger logger;

    public CustomerFeignSlf4jLogger() {
        this(feign.Logger.class);
    }

    public CustomerFeignSlf4jLogger(Class<?> clazz) {
        this(LoggerFactory.getLogger(clazz));
    }

    public CustomerFeignSlf4jLogger(String name) {
        this(LoggerFactory.getLogger(name));
    }

    CustomerFeignSlf4jLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {

        super.logRequest(configKey, logLevel, request);

    }

    @Override
    protected Response logAndRebufferResponse(String configKey,
                                              Level logLevel,
                                              Response response,
                                              long elapsedTime)
            throws IOException {

        return super.logAndRebufferResponse(configKey, logLevel, response, elapsedTime);

    }

    @Override
    protected void log(String configKey, String format, Object... args) {
        // Not using SLF4J's support for parameterized messages (even though it
        // would be more efficient) because it would
        // require the incoming message formats to be SLF4J-specific.
        logger.info(String.format(methodTag(configKey) + format, args));

    }

}
