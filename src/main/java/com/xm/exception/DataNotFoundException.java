package com.xm.exception;

import com.xm.data.TimeFrame;

public class DataNotFoundException extends RuntimeException {

    public DataNotFoundException(TimeFrame timeFrame) {
        super("No data available for the specified time frame: " + timeFrame);
    }
}