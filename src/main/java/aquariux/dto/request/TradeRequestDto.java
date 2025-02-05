package aquariux.dto.request;

import java.math.BigDecimal;

import aquariux.util.enums.TradeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeRequestDto {

    @NotNull
    private Long userId;
    
    @NotNull
    @Pattern(regexp = "^(BTC|ETH)USDT$")
    private String symbol;
    
    @NotNull
    private TradeType tradeType;
    
    @NotNull
    private BigDecimal quantity;
}
