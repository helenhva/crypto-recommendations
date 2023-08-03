package com.xm.reader;

import com.xm.data.CryptoData;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CsvDataReader {

    @Value("${csv.folder.path}")
    private String csvFolderPath;

    private final String CRYPTO_DATA_CACHE_NAME = "cryptoData";
    private final String FILE_ENDS_WITH_EXTENSION = "_values.csv";
    private final Map<String, List<CryptoData>> cryptoDataCache = new HashMap<>();

    @PostConstruct
    @SneakyThrows
    public void init() {
        populateCryptoDataCache();
    }

    @Cacheable(CRYPTO_DATA_CACHE_NAME)
    public List<CryptoData> getCryptoDataByName(String cryptoName) {
        return cryptoDataCache.get(cryptoName);
    }

    @CacheEvict(value = CRYPTO_DATA_CACHE_NAME, allEntries = true)
    public void refreshCryptoDataCache() {
        cryptoDataCache.clear();
        populateCryptoDataCache();
    }

    public Set<String> getAllCryptoNames() {
        return cryptoDataCache.keySet();
    }

    @SneakyThrows
    private void populateCryptoDataCache() {
        List<Path> csvFiles = listCsvFilesInFolder();
        for (Path file : csvFiles) {
            String cryptoName = extractCryptoNameFromFileName(file.getFileName().toString());
            List<CryptoData> cryptoDataList = readCryptoDataFromFile(file);
            cryptoDataCache.put(cryptoName, cryptoDataList);
        }
    }

    @SneakyThrows
    private List<Path> listCsvFilesInFolder() {
        var folderPath = Paths.get(csvFolderPath);
        if (!Files.isDirectory(folderPath)) {
            return Collections.emptyList();
        }
        try (Stream<Path> walk = Files.walk(folderPath)) {
            return walk
                    .filter(path -> path.getFileName().toString().endsWith(FILE_ENDS_WITH_EXTENSION))
                    .collect(Collectors.toList());
        }
    }

    private String extractCryptoNameFromFileName(String fileName) {
        return fileName.replace(FILE_ENDS_WITH_EXTENSION, "");
    }

    @SneakyThrows
    private List<CryptoData> readCryptoDataFromFile(Path filePath) {
        List<CryptoData> cryptoDataList = new ArrayList<>();

        try (FileReader fileReader = new FileReader(filePath.toFile());
             CSVParser csvParser = CSVFormat.DEFAULT.builder()
                     .setHeader(CsvHeader.class)
                     .setIgnoreHeaderCase(true)
                     .setSkipHeaderRecord(true)
                     .build().parse(fileReader)) {

            for (CSVRecord record : csvParser) {
                long timestamp = Long.parseLong(record.get(CsvHeader.TIMESTAMP.getHeader()));
                String symbol = record.get(CsvHeader.SYMBOL.getHeader());
                BigDecimal price = new BigDecimal(record.get(CsvHeader.PRICE.getHeader()));
                cryptoDataList.add(new CryptoData(timestamp, symbol, price));
            }
        }
        return cryptoDataList;
    }
}
