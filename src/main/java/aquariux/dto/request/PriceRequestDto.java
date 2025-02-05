package aquariux.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PriceRequestDto {
    @NotNull(message = "Trading pair symbol is required")
    @Pattern(
        regexp = "^(BTC|ETH)USDT$",
        message = "Only BTCUSDT and ETHUSDT pairs are supported"
    )
    private String symbol;

    private Integer timeRangeMinutes;
    
    @Pattern(
        regexp = "^(BINANCE|HUOBI)?$", 
        message = "Price source must be either BINANCE or HUOBI"
    )
    private String source;

}
