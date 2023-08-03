package com.xm.reader;

import com.xm.data.CryptoData;
import com.xm.util.ExpectedTestDataPreparation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CsvDataReaderTest {

    private final String BTC = "BTC";
    private final String DOGE = "DOGE";

    @InjectMocks
    private CsvDataReader csvDataReader;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(csvDataReader, "csvFolderPath", "src\\test\\resources\\prices");
        csvDataReader.init();
    }

    @Test
    public void testGetAllCryptoNames_Success() {
        Set<String> expectedCryptoNames = Set.of(BTC, DOGE);

        Set<String> actualCryptoNames = csvDataReader.getAllCryptoNames();

        assertEquals(expectedCryptoNames, actualCryptoNames);
    }

//    @Test
//    public void testGetAllCryptoNames_NoCsvFiles() {
//        Set<String> expectedCryptoNames = Set.of();
//
//        Set<String> actualCryptoNames = csvDataReader.getAllCryptoNames();
//
//        assertEquals(expectedCryptoNames, actualCryptoNames);
//    }

    @Test
    public void testGetCryptoData_Success() {
        List<CryptoData> expectedBTC = ExpectedTestDataPreparation.getExpectedBTC();
        List<CryptoData> actualCryptoData = csvDataReader.getCryptoDataByName(BTC);

        assertEquals(expectedBTC.size(), actualCryptoData.size());
        for (int i = 0; i < expectedBTC.size(); i++) {
            assertEquals(expectedBTC.get(i), actualCryptoData.get(i));
        }
    }

    @Test
    public void testRefreshCryptoDataCache_Success() {
        Map<String, List<CryptoData>> cryptoDataCache = getCryptoDataCache();
        assertEquals(2, cryptoDataCache.size());

        csvDataReader.refreshCryptoDataCache();

        cryptoDataCache = getCryptoDataCache();
        assertEquals(2, cryptoDataCache.size());

    }

    private Map<String, List<CryptoData>> getCryptoDataCache() {
        return (Map<String, List<CryptoData>>) ReflectionTestUtils.getField(csvDataReader, "cryptoDataCache");
    }
}
