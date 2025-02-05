package aquariux.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletResponseDto {
    private String currency;

    private BigDecimal balance;

    private BigDecimal usdtEquivalent;
}
