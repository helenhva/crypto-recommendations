package com.xm.service;

import com.xm.data.CryptoData;
import com.xm.data.CryptoStatistics;
import com.xm.data.TimeFrame;
import com.xm.exception.UnsupportedCryptoException;
import com.xm.reader.CsvDataReader;
import com.xm.util.ExpectedTestDataPreparation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @Mock
    private CsvDataReader csvDataReader;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    public void testGetAllCryptoStats_Success() {
        List<CryptoData> btcDataList = ExpectedTestDataPreparation.getExpectedBTC();
        List<CryptoData> dogeDataList = ExpectedTestDataPreparation.getExpectedDOGE();

        when(csvDataReader.getAllCryptoNames()).thenReturn(ExpectedTestDataPreparation.getSupportedCrypto());
        when(csvDataReader.getCryptoDataByName(ExpectedTestDataPreparation.BTC)).thenReturn(btcDataList);
        when(csvDataReader.getCryptoDataByName(ExpectedTestDataPreparation.DOGE)).thenReturn(dogeDataList);

        List<CryptoStatistics> statsList = recommendationService.getAllCryptoStatistics();

        assertEquals(2, statsList.size());

        CryptoStatistics btcStats = statsList.stream()
                .filter(stat -> stat.getCryptoName().equals(ExpectedTestDataPreparation.BTC))
                .findFirst().orElse(null);
        CryptoStatistics dogeStats = statsList.stream()
                .filter(stat -> stat.getCryptoName().equals(ExpectedTestDataPreparation.DOGE))
                .findFirst().orElse(null);

        assertNotNull(btcStats);
        assertNotNull(dogeStats);

        assertEquals(ExpectedTestDataPreparation.BTC, btcStats.getCryptoName());
        assertEquals(new BigDecimal("10000"), btcStats.getOldest());
        assertEquals(new BigDecimal("30000"), btcStats.getNewest());
        assertEquals(new BigDecimal("10000"), btcStats.getMin());
        assertEquals(new BigDecimal("30000"), btcStats.getMax());
        assertEquals(new BigDecimal("2.00000"), btcStats.getNormalizedRange());

        assertEquals(ExpectedTestDataPreparation.DOGE, dogeStats.getCryptoName());
        assertEquals(new BigDecimal("26.00"), dogeStats.getOldest());
        assertEquals(new BigDecimal("28.00"), dogeStats.getNewest());
        assertEquals(new BigDecimal("26.00"), dogeStats.getMin());
        assertEquals(new BigDecimal("28.00"), dogeStats.getMax());
        assertEquals(new BigDecimal("0.07692"), dogeStats.getNormalizedRange());

        verify(csvDataReader, times(1)).getAllCryptoNames();
        verify(csvDataReader, times(1)).getCryptoDataByName(ExpectedTestDataPreparation.BTC);
        verify(csvDataReader, times(1)).getCryptoDataByName(ExpectedTestDataPreparation.DOGE);
        verifyNoMoreInteractions(csvDataReader);
    }

    @Test
    public void testGetAllCryptoStats_EmptyData() {
        when(csvDataReader.getAllCryptoNames()).thenReturn(Set.of());

        List<CryptoStatistics> statsList = recommendationService.getAllCryptoStatistics();

        assertTrue(statsList.isEmpty());

        verify(csvDataReader, times(1)).getAllCryptoNames();
        verifyNoMoreInteractions(csvDataReader);
    }

    @Test
    public void testGetCryptoStatsByName_Success() {
        String cryptoName = ExpectedTestDataPreparation.BTC;
        List<CryptoData> btcDataList = ExpectedTestDataPreparation.getExpectedBTC();
        when(csvDataReader.getAllCryptoNames()).thenReturn(ExpectedTestDataPreparation.getSupportedCrypto());
        when(csvDataReader.getCryptoDataByName(cryptoName)).thenReturn(btcDataList);
        CryptoStatistics stats = recommendationService.getCryptoStatisticsByName(cryptoName, TimeFrame.ONE_MONTH);

        assertNotNull(stats);
        assertEquals(cryptoName, stats.getCryptoName());
        assertEquals(new BigDecimal("10000"), stats.getOldest());
        assertEquals(new BigDecimal("30000"), stats.getNewest());
        assertEquals(new BigDecimal("10000"), stats.getMin());
        assertEquals(new BigDecimal("30000"), stats.getMax());
        assertEquals(new BigDecimal("2.00000"), stats.getNormalizedRange());

        verify(csvDataReader, times(1)).getCryptoDataByName(cryptoName);
        verifyNoMoreInteractions(csvDataReader);
    }

    @Test
    public void testGetCryptoStatsByName_InvalidName() {
        String cryptoName = "INVALID";

        assertThrows(UnsupportedCryptoException.class, () -> recommendationService.getCryptoStatisticsByName(cryptoName, TimeFrame.ONE_MONTH));
    }

    @Test
    public void testGetHighestNormalizedRangeCryptoByDay_Success() {
        long timestamp = 1641009600000L;

        List<CryptoData> btcDataList = ExpectedTestDataPreparation.getExpectedBTC();
        List<CryptoData> dogeDataList = ExpectedTestDataPreparation.getExpectedDOGE();

        when(csvDataReader.getAllCryptoNames()).thenReturn(ExpectedTestDataPreparation.getSupportedCrypto());
        when(csvDataReader.getCryptoDataByName(ExpectedTestDataPreparation.DOGE)).thenReturn(dogeDataList);
        when(csvDataReader.getCryptoDataByName(ExpectedTestDataPreparation.BTC)).thenReturn(btcDataList);

        CryptoStatistics stat = recommendationService.getHighestNormalizedRangeCryptoByDay(timestamp);

        assertNotNull(stat);
        assertEquals(ExpectedTestDataPreparation.BTC, stat.getCryptoName());
        assertEquals(new BigDecimal("2.00000"), stat.getNormalizedRange());

        verify(csvDataReader, times(1)).getAllCryptoNames();
        verify(csvDataReader, times(1)).getCryptoDataByName("BTC");
        verifyNoMoreInteractions(csvDataReader);
    }

    @Test
    public void testGetHighestNormalizedRangeCryptoByDay_NoMatchingData() {
        long timestamp = 1641009600000L;

        when(csvDataReader.getAllCryptoNames()).thenReturn(Set.of("BTC"));
        when(csvDataReader.getCryptoDataByName("BTC")).thenReturn(new ArrayList<>());

        CryptoStatistics stat = recommendationService.getHighestNormalizedRangeCryptoByDay(timestamp);

        assertNull(stat);

        verify(csvDataReader, times(1)).getAllCryptoNames();
        verify(csvDataReader, times(1)).getCryptoDataByName("BTC");
        verifyNoMoreInteractions(csvDataReader);
    }
}
