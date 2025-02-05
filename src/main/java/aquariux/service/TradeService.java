package aquariux.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import aquariux.model.User;
import aquariux.repository.PriceRepository;
import aquariux.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aquariux.dto.request.TradeRequestDto;
import aquariux.dto.response.TradeResponseDto;
import aquariux.exception.PriceNotFoundException;
import aquariux.mapper.TradeMapper;
import aquariux.model.Price;
import aquariux.model.Trade;
import aquariux.repository.TradeRepository;
import aquariux.util.enums.TradeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;

    private final WalletService walletService;

    private final PriceRepository priceRepository;

    private final TradeMapper tradeMapper;

    private final UserRepository userRepository;
    
    /**
     * Execute a trade based on the provided request
     */
    @Transactional
    public TradeResponseDto executeTrade(TradeRequestDto request) {
        log.info("Executing trade request: {}", request);
        validateTradeRequest(request);

        Price currentPrice = getCurrentPrice(request.getSymbol());
        Trade trade = createTrade(request, currentPrice);

        executeWalletUpdates(request, trade.getTotalTradeValue());

        Trade savedTrade = tradeRepository.save(trade);
        log.info("Trade executed successfully. Trade ID: {}", savedTrade.getId());

        return tradeMapper.toDto(savedTrade);
    }

    /**
     * Get user's trading history
     */
    public List<TradeResponseDto> getUserTrades(User user) {
        log.debug("Fetching trade history for user: {}", user);
        List<Trade> trades = tradeRepository.findByUserOrderByTimestampDesc(user);
        log.debug("Found {} trades for user: {}", trades.size(), user);
        return tradeMapper.toDtoList(trades);
    }

    /**
     * Calculate execution price based on trade type
     */
    private BigDecimal calculateExecutionPrice(TradeType tradeType, Price price) {
        if (tradeType == null || price == null) {
            throw new IllegalArgumentException("Trade type and price must not be null");
        }
        
        switch (tradeType) {
            case BUY:
                return price.getAskPrice();
            case SELL:
                return price.getBidPrice();
            default:
                throw new IllegalArgumentException("Unsupported trade type: " + tradeType);
        }
    }

    /**
     * Validate trade request
     */
    private void validateTradeRequest(TradeRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Trade request cannot be null");
        }

        validateSymbol(request.getSymbol());
        validateQuantity(request.getQuantity());
        validateUserId(request.getUserId());
    }

    /**
     * Validate trading symbol
     */
    private void validateSymbol(String symbol) {
        if (!isValidTradingPair(symbol)) {
            throw new IllegalArgumentException(
                "Invalid trading pair: " + symbol + ". Supported pairs: BTCUSDT, ETHUSDT"
            );
        }
    }

    /**
     * Validate trade quantity
     */
    private void validateQuantity(BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Trade quantity must be positive");
        }

        if (quantity.scale() > 8) {
            throw new IllegalArgumentException("Maximum 8 decimal places allowed for quantity");
        }
    }

    /**
     * Validate user ID
     */
    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
    }

    /**
     * Check if trading pair is supported
     */
    private boolean isValidTradingPair(String symbol) {
        return symbol != null && 
               (symbol.equals("BTCUSDT") || symbol.equals("ETHUSDT"));
    }

    private Price getCurrentPrice(String symbol) {
        log.info("Attempting to retrieve latest price for symbol: {}", symbol);

        Optional<Price> priceOptional = priceRepository.findTopBySymbolOrderByTimestampDesc(symbol);

        if (priceOptional.isEmpty()) {
            log.error("No price found in database for symbol: {}", symbol);
            throw new PriceNotFoundException("No price data available for " + symbol);
        }

        Price price = priceOptional.get();

        log.debug("Retrieved price - Symbol: {}, Ask: {}, Bid: {}, Timestamp: {}",
                price.getSymbol(), price.getAskPrice(), price.getBidPrice(), price.getTimestamp());

        return price;
    }

    private Trade createTrade(TradeRequestDto request, Price currentPrice) {
        BigDecimal executionPrice = calculateExecutionPrice(
                request.getTradeType(),
                currentPrice
        );

        // Ensure user exists before creating trade
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Trade trade = tradeMapper.toEntity(request);
        trade.setUser(user);
        trade.setPrice(executionPrice);
        trade.setTotalTradeValue(executionPrice.multiply(request.getQuantity()));

        return trade;
    }

    private void executeWalletUpdates(TradeRequestDto request, BigDecimal total) {
        String baseCurrency = request.getSymbol().replace("USDT", "");
        
        if (request.getTradeType() == TradeType.BUY) {
            walletService.validateAndUpdateBalance(
                request.getUserId(), 
                "USDT",
                total.negate()
            );
            walletService.validateAndUpdateBalance(
                request.getUserId(), 
                baseCurrency, 
                request.getQuantity()
            );
        } else {
            walletService.validateAndUpdateBalance(
                request.getUserId(), 
                baseCurrency, 
                request.getQuantity().negate()
            );
            walletService.validateAndUpdateBalance(
                request.getUserId(),
                "USDT",
                total
            );
        }
    }
}