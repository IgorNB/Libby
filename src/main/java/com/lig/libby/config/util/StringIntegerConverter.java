package com.lig.libby.config.util;

import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;

public class StringIntegerConverter implements Converter<String, Integer> {
    @Override
    public Integer convert(String source) {
        if (source.equals("null")) {
            return null;
        }
        return new BigDecimal(source).intValueExact();
    }
}
