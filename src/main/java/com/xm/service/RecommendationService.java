package com.xm.service;

import com.xm.data.CryptoData;
import com.xm.data.CryptoStatistics;
import com.xm.data.TimeFrame;
import com.xm.exception.DataNotFoundException;
import com.xm.exception.UnsupportedCryptoException;
import com.xm.reader.CsvDataReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class RecommendationService {

    private final CsvDataReader csvDataReader;

    public List<CryptoStatistics> getAllCryptoStatistics() {
        return csvDataReader.getAllCryptoNames().stream()
                .map(cryptoName -> {
                    List<CryptoData> cryptoDataList = csvDataReader.getCryptoDataByName(cryptoName);
                    return calculateStats(cryptoName, cryptoDataList);
                })
                .sorted(Comparator.comparing(CryptoStatistics::getNormalizedRange).reversed())
                .collect(Collectors.toList());
    }

    public CryptoStatistics getCryptoStatisticsByName(String cryptoName, TimeFrame timeFrame) {
        if (!isSupportedCrypto(cryptoName)) {
            throw new UnsupportedCryptoException(cryptoName);
        }
        List<CryptoData> cryptoDataList = csvDataReader.getCryptoDataByName(cryptoName);
        List<CryptoData> filteredData = filterCryptoDataByTimeFrame(cryptoDataList, timeFrame);
        if (filteredData.isEmpty()) {
            throw new DataNotFoundException(timeFrame);
        }
        return calculateStats(cryptoName, filteredData);
    }

    public CryptoStatistics getHighestNormalizedRangeCryptoByDay(long timestamp) {
        Set<String> cryptoNames = csvDataReader.getAllCryptoNames();

        return cryptoNames.parallelStream()
                .map(cryptoName -> {
                    List<CryptoData> cryptoDataList = csvDataReader.getCryptoDataByName(cryptoName);
                    CryptoData cryptoData = getCryptoDataByTimestamp(cryptoDataList, timestamp);

                    if (cryptoData != null) {
                        BigDecimal normalizedRange = calculateNormalizedRange(cryptoDataList);
                        return CryptoStatistics.builder()
                                .cryptoName(cryptoData.getSymbol())
                                .normalizedRange(normalizedRange)
                                .build();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .max(Comparator.comparing(CryptoStatistics::getNormalizedRange))
                .orElse(null);
    }

    private CryptoStatistics calculateStats(String cryptoName, List<CryptoData> cryptoDataList) {
        if (cryptoDataList == null || cryptoDataList.isEmpty()) {
            return null;
        }

        var min = cryptoDataList.stream()
                .map(CryptoData::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        var max = cryptoDataList.stream()
                .map(CryptoData::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        var normalizedRange = max.subtract(min).divide(min, 5, RoundingMode.HALF_UP);

        var oldestCryptoData = cryptoDataList.stream()
                .min(Comparator.comparing(CryptoData::getTimestamp))
                .orElseThrow(NoSuchElementException::new);
        var oldest = oldestCryptoData.getPrice();

        var newestCryptoData = cryptoDataList.stream()
                .max(Comparator.comparing(CryptoData::getTimestamp))
                .orElseThrow(NoSuchElementException::new);
        var newest = newestCryptoData.getPrice();

        return CryptoStatistics.builder()
                .cryptoName(cryptoName)
                .oldest(oldest)
                .newest(newest)
                .min(min)
                .max(max)
                .normalizedRange(normalizedRange)
                .build();
    }

    private BigDecimal calculateNormalizedRange(List<CryptoData> cryptoDataList) {
        var min = cryptoDataList.stream().map(CryptoData::getPrice).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        var max = cryptoDataList.stream().map(CryptoData::getPrice).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        return max.subtract(min).divide(min, 5, RoundingMode.HALF_UP);
    }

    private CryptoData getCryptoDataByTimestamp(List<CryptoData> cryptoDataList, long timestamp) {
        return cryptoDataList.stream()
                .filter(cryptoData -> cryptoData.getTimestamp() == timestamp)
                .findFirst()
                .orElse(null);
    }

    private boolean isSupportedCrypto(String cryptoSymbol) {
        return csvDataReader.getAllCryptoNames().contains(cryptoSymbol);
    }

    private List<CryptoData> filterCryptoDataByTimeFrame(List<CryptoData> cryptoDataList, TimeFrame timeFrame) {
        LocalDate currentDate = LocalDate.now().minusYears(2); // Since we only have data for 2022
        LocalDate startDate;
        switch (timeFrame){
            case ONE_MONTH:
                startDate = currentDate.minusMonths(1);
                break;
            case SIX_MONTHS:
                startDate = currentDate.minusMonths(6);
                break;
            case ONE_YEAR:
                startDate = currentDate.minusYears(1);
                break;
            default:
                throw new IllegalArgumentException("Unsupported time frame: " + timeFrame);
        }
        long startTimestamp = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return cryptoDataList.stream()
                .filter(cryptoData -> cryptoData.getTimestamp() >= startTimestamp)
                .collect(Collectors.toList());
    }
}
