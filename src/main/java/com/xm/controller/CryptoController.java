package com.xm.controller;

import com.xm.data.CryptoStatistics;
import com.xm.data.TimeFrame;
import com.xm.service.RecommendationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/crypto")
@AllArgsConstructor
@Api(tags = "Crypto Controller", produces = "application/json", consumes = "application/json")
public class CryptoController {

    private final RecommendationService recommendationService;

    @GetMapping("/statistics")
    @ApiOperation(value = "Get all cryptocurrency statistics", httpMethod = "GET")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully retrieved cryptocurrency statistics",
                    response = CryptoStatistics.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<List<CryptoStatistics>> getAllCryptoStatistics() {
        var cryptoStatisticsList = recommendationService.getAllCryptoStatistics();
        return new ResponseEntity<>(cryptoStatisticsList, HttpStatus.OK);
    }

    @GetMapping("/statistics/{cryptoName}")
    @ApiOperation(value = "Get cryptocurrency statistics by name", httpMethod = "GET")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully retrieved cryptocurrency statistics",
                    response = CryptoStatistics.class),
            @ApiResponse(code = 404, message = "Cryptocurrency not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<CryptoStatistics> getCryptoStatisticsByName(@PathVariable String cryptoName,
                                                                      @RequestParam(required = false, defaultValue = "ONE_MONTH")
                                                                      TimeFrame timeFrame) {
        var cryptoStatistics = recommendationService.getCryptoStatisticsByName(cryptoName, timeFrame);
        return cryptoStatistics != null
                ? new ResponseEntity<>(cryptoStatistics, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @GetMapping("/highest-normalized-range")
    @ApiOperation(value = "Get cryptocurrency with highest normalized range for a specific day", httpMethod = "GET")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully retrieved cryptocurrency statistics",
                    response = CryptoStatistics.class),
            @ApiResponse(code = 404, message = "Cryptocurrency not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<CryptoStatistics> getHighestNormalizedRangeCryptoByDay(@RequestParam("timestamp") long timestamp) {
        var cryptoStatistics = recommendationService.getHighestNormalizedRangeCryptoByDay(timestamp);
        return cryptoStatistics != null
                ? new ResponseEntity<>(cryptoStatistics, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
