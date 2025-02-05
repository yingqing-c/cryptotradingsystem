package aquariux.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import aquariux.util.enums.TradeType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TradeResponseDto {
    private Long id;

    private Long userId;

    private String symbol;

    private TradeType tradeType;

    private BigDecimal price;

    private BigDecimal quantity;

    private BigDecimal totalTradeValue;

    private LocalDateTime timestamp;
}
