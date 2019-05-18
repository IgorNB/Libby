package com.lig.libby.config.util;

import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;
import java.math.BigInteger;

public class StringBigIntegerConverter implements Converter<String, BigInteger> {
    @Override
    public BigInteger convert(String source) {
        if (source.equals("null")) {
            return null;
        }
        return new BigDecimal(source).toBigIntegerExact();
    }
}
