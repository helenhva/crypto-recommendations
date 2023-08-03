package com.xm.util;

import com.xm.data.CryptoData;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class ExpectedTestDataPreparation {
    public static final String BTC = "BTC";
    public static final String DOGE = "DOGE";

    public static Set<String> getSupportedCrypto() {
        return Set.of(BTC, DOGE);
    }

    public static List<CryptoData> getExpectedBTC() {
        return List.of(
                CryptoData.builder()
                        .timestamp(1641009600000L)
                        .symbol(BTC)
                        .price(new BigDecimal("10000"))
                        .build(),
                CryptoData.builder()
                        .timestamp(1641020400000L)
                        .symbol(BTC)
                        .price(new BigDecimal("20000"))
                        .build(),
                CryptoData.builder()
                        .timestamp(1641031200000L)
                        .symbol(BTC)
                        .price(new BigDecimal("30000"))
                        .build()
        );
    }

    public static List<CryptoData> getExpectedDOGE() {
        return List.of(
                CryptoData.builder()
                        .timestamp(1641009600000L)
                        .symbol(DOGE)
                        .price(new BigDecimal("26.00"))
                        .build(),
                CryptoData.builder()
                        .timestamp(1641031200000L)
                        .symbol(DOGE)
                        .price(new BigDecimal("27.00"))
                        .build(),
                CryptoData.builder()
                        .timestamp(1641049200000L)
                        .symbol(DOGE)
                        .price(new BigDecimal("28.00"))
                        .build()
        );
    }
}
