package com.lig.libby.service.exception;

import lombok.Getter;

@Getter
public class ServiceFallbackException extends RuntimeException {

    private final String source;

    public ServiceFallbackException(String source, Throwable cause) {
        super(cause);
        this.source = source;
    }

    @Override
    public String getMessage() {
        return "Service unavailable: " + source;
    }
}
