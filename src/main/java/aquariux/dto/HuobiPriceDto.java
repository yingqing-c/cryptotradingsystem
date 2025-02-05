package aquariux.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HuobiPriceDto {
    private String symbol;

    private BigDecimal bid;

    private BigDecimal ask;

    private BigDecimal bidSize;

    private BigDecimal askSize;
}
