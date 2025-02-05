package aquariux.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PriceResponseDto {
    private String symbol;

    private BigDecimal bidPrice;

    private BigDecimal askPrice;

    private String source;

    private LocalDateTime timestamp;
}
