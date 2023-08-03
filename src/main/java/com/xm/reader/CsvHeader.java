package com.xm.reader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CsvHeader {
    TIMESTAMP("timestamp"),
    SYMBOL("symbol"),
    PRICE("price");

    @Getter
    private final String header;
}
