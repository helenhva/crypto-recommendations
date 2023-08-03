package com.xm.controller;

import com.xm.data.CryptoStatistics;
import com.xm.data.TimeFrame;
import com.xm.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CryptoControllerTest {

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private CryptoController cryptoController;

    @Test
    public void testGetAllCryptoStats_Success() {
        CryptoStatistics stat1 = new CryptoStatistics("BTC", new BigDecimal("46813.21"),
                new BigDecimal("48000.12"), new BigDecimal("45000.50"),
                new BigDecimal("48500.65"), new BigDecimal("0.077"));
        CryptoStatistics stat2 = new CryptoStatistics("ETH", new BigDecimal("3200.45"),
                new BigDecimal("3500.78"), new BigDecimal("3100.21"),
                new BigDecimal("3600.50"), new BigDecimal("0.161"));

        List<CryptoStatistics> statsList = List.of(stat1, stat2);

        when(recommendationService.getAllCryptoStatistics()).thenReturn(statsList);

        ResponseEntity<List<CryptoStatistics>> responseEntity = cryptoController.getAllCryptoStatistics();

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(statsList, responseEntity.getBody());

        verify(recommendationService, times(1)).getAllCryptoStatistics();
        verifyNoMoreInteractions(recommendationService);
    }

    @Test
    public void testGetAllCryptoStats_EmptyStats() {
        List<CryptoStatistics> statsList = new ArrayList<>();

        when(recommendationService.getAllCryptoStatistics()).thenReturn(statsList);

        ResponseEntity<List<CryptoStatistics>> responseEntity = cryptoController.getAllCryptoStatistics();

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertTrue(responseEntity.getBody().isEmpty());

        verify(recommendationService, times(1)).getAllCryptoStatistics();
        verifyNoMoreInteractions(recommendationService);
    }

    @Test
    public void testGetCryptoStatsByName_Success() {
        String cryptoName = "BTC";
        CryptoStatistics stat = new CryptoStatistics("BTC", new BigDecimal("46813.21"),
                new BigDecimal("48000.12"), new BigDecimal("45000.50"),
                new BigDecimal("48500.65"), new BigDecimal("0.077"));

        when(recommendationService.getCryptoStatisticsByName(cryptoName, TimeFrame.ONE_MONTH)).thenReturn(stat);

        ResponseEntity<CryptoStatistics> responseEntity = cryptoController.getCryptoStatisticsByName(cryptoName, TimeFrame.ONE_MONTH);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(stat, responseEntity.getBody());

        verify(recommendationService, times(1)).getCryptoStatisticsByName(cryptoName, TimeFrame.ONE_MONTH);
        verifyNoMoreInteractions(recommendationService);
    }

    @Test
    public void testGetCryptoStatsByName_InvalidName() {
        String cryptoName = "INVALID";

        when(recommendationService.getCryptoStatisticsByName(cryptoName, TimeFrame.ONE_MONTH)).thenReturn(null);

        ResponseEntity<CryptoStatistics> responseEntity = cryptoController.getCryptoStatisticsByName(cryptoName, TimeFrame.ONE_MONTH);

        assertEquals(404, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());

        verify(recommendationService, times(1)).getCryptoStatisticsByName(cryptoName, TimeFrame.ONE_MONTH);
        verifyNoMoreInteractions(recommendationService);
    }

    @Test
    public void testGetHighestNormalizedRangeCryptoByDay_Success() {
        long timestamp = 1641009600000L;
        CryptoStatistics stat = new CryptoStatistics("BTC", new BigDecimal("46813.21"),
                new BigDecimal("48000.12"), new BigDecimal("45000.50"),
                new BigDecimal("48500.65"), new BigDecimal("0.077"));

        when(recommendationService.getHighestNormalizedRangeCryptoByDay(timestamp)).thenReturn(stat);

        ResponseEntity<CryptoStatistics> responseEntity = cryptoController.getHighestNormalizedRangeCryptoByDay(timestamp);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(stat, responseEntity.getBody());

        verify(recommendationService, times(1)).getHighestNormalizedRangeCryptoByDay(timestamp);
        verifyNoMoreInteractions(recommendationService);
    }

    @Test
    public void testGetHighestNormalizedRangeCryptoByDay_NoMatchingData() {
        long timestamp = 1641009600000L;

        when(recommendationService.getHighestNormalizedRangeCryptoByDay(timestamp)).thenReturn(null);

        ResponseEntity<CryptoStatistics> responseEntity = cryptoController.getHighestNormalizedRangeCryptoByDay(timestamp);

        assertEquals(404, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());

        verify(recommendationService, times(1)).getHighestNormalizedRangeCryptoByDay(timestamp);
        verifyNoMoreInteractions(recommendationService);
    }
}
