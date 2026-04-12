package com.yemenptc.bss.sdk.config;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.regex.Pattern;

/**
 * Logback layout that redacts PII from log messages.
 * Redacts phone numbers, emails, national IDs.
 */
public class PiiRedactingLayout extends PatternLayout {

    private static final Pattern PHONE_PATTERN = Pattern.compile("(\\+?\\d{1,3})\\d{6,10}(\\d{4})");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("([a-zA-Z0-9._%+-])[^@]*@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})");
    private static final Pattern NATIONAL_ID_PATTERN = Pattern.compile("(\\d{2})\\d{6,8}(\\d{2})");
    private static final Pattern CREDIT_CARD_PATTERN = Pattern.compile("(\\d{4})\\d{8,12}(\\d{4})");

    @Override
    public String doLayout(ILoggingEvent event) {
        String original = super.doLayout(event);
        return redact(original);
    }

    private String redact(String message) {
        if (message == null) return null;

        message = PHONE_PATTERN.matcher(message).replaceAll("$1****$2");
        message = EMAIL_PATTERN.matcher(message).replaceAll("$1***@$2");
        message = NATIONAL_ID_PATTERN.matcher(message).replaceAll("$1******$2");
        message = CREDIT_CARD_PATTERN.matcher(message).replaceAll("$1********$2");

        return message;
    }
}
