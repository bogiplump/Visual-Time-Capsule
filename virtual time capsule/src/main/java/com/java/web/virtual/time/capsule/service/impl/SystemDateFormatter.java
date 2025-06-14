package com.java.web.virtual.time.capsule.service.impl;

import java.time.format.DateTimeFormatter;

public class SystemDateFormatter {
    public final static String dateTimePattern = "HH-mm-ss_dd-MM-yyyy";
    public final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimePattern);
}
