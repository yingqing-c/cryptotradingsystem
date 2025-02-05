package aquariux.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import aquariux.exception.PriceNotFoundException;
import aquariux.model.Price;
import aquariux.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceService {
    private final PriceRepository priceRepository;

    // Get latest price
    public Price getLatestPrice(String symbol) {
        log.debug("Fetching latest price for symbol: {}", symbol);
        
        return priceRepository.findTopBySymbolOrderByTimestampDesc(symbol)
            .filter(this::isPriceValid)
            .filter(this::isPriceNotStale)
            .orElseThrow(() -> new PriceNotFoundException(
                "No valid price found for symbol: " + symbol
            ));
    }

    /**
     * Get best bid price
     */
    public BigDecimal getBestBidPrice(String symbol) {
        return getLatestPrice(symbol).getBidPrice();
    }

    /**
     * Get best ask price
     */
    public BigDecimal getBestAskPrice(String symbol) {
        return getLatestPrice(symbol).getAskPrice();
    }

    private boolean isPriceValid(Price price) {
        return price != null &&
               price.getBidPrice() != null &&
               price.getAskPrice() != null &&
               price.getBidPrice().compareTo(BigDecimal.ZERO) > 0 &&
               price.getAskPrice().compareTo(BigDecimal.ZERO) > 0 &&
               price.getAskPrice().compareTo(price.getBidPrice()) >= 0;
    }

    private boolean isPriceNotStale(Price price) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusSeconds(30);
        return price.getTimestamp().isAfter(cutoffTime);
    }
}
