package com.xm.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CryptoStatistics {
    private String cryptoName;
    private BigDecimal oldest;
    private BigDecimal newest;
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal normalizedRange;
}
