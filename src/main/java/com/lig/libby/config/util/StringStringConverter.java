package com.lig.libby.config.util;

import org.springframework.core.convert.converter.Converter;

public class StringStringConverter implements Converter<String, String> {
    @Override
    public String convert(String source) {
        if (source.equals("null")) {
            return null;
        }
        return source;
    }
}
