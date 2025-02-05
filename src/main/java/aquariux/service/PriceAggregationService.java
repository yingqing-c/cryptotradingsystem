package aquariux.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import aquariux.dto.BinancePriceDto;
import aquariux.dto.HuobiPriceDto;
import aquariux.dto.HuobiResponseDto;
import aquariux.mapper.ExchangePriceMapper;
import aquariux.model.Price;
import aquariux.repository.PriceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class PriceAggregationService {

    @Value("${exchange.binance.url}")
    private String binanceUrl;

    @Value("${exchange.huobi.url}")
    private String huobiUrl;

    private final RestTemplate restTemplate;

    private final PriceRepository priceRepository;

    private final ExchangePriceMapper exchangePriceMapper;

    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 10000) // Aggregate every 10 seconds
    @Transactional
    public void aggregatePrices() {
        try {
            log.info("Starting price aggregation at: {}", LocalDateTime.now());

            // Fetch from both exchanges
            Map<String, Price> bestPrices = new HashMap<>();

            // Fetch and process Binance prices
            processBinancePrices(bestPrices);

            // Fetch and process Huobi prices
            processHuobiPrices(bestPrices);

            // Save best prices
            if (!bestPrices.isEmpty()) {
                List<Price> savedPrices = priceRepository.saveAll(bestPrices.values());
                log.info("Saved {} aggregated prices", savedPrices.size());

                savedPrices.forEach(price ->
                        log.info("Saved price - Symbol: {}, Bid: {} ({}), Ask: {} ({})",
                                price.getSymbol(),
                                price.getBidPrice(), price.getBidSource(),
                                price.getAskPrice(), price.getAskSource())
                );
            } else {
                log.warn("No valid prices found to save");
            }

        } catch (Exception e) {
            log.error("Error during price aggregation: ", e);
        }
    }

    private void processBinancePrices(Map<String, Price> bestPrices) {
        try {
            log.info("Fetching Binance prices from: {}", binanceUrl);
            String binanceResponse = restTemplate.getForObject(binanceUrl, String.class);
            log.debug("Binance raw response: {}", binanceResponse);

            List<BinancePriceDto> binancePrices = objectMapper.readValue(
                    binanceResponse,
                    new TypeReference<List<BinancePriceDto>>() {}
            );

            log.info("Parsed {} Binance prices", binancePrices.size());

            binancePrices.stream()
                    .filter(p -> "BTCUSDT".equals(p.getSymbol()) || "ETHUSDT".equals(p.getSymbol()))
                    .forEach(bp -> updateBestPrices(bestPrices, convertBinancePrice(bp)));

        } catch (Exception e) {
            log.error("Error processing Binance prices: ", e);
        }
    }

    private void processHuobiPrices(Map<String, Price> bestPrices) {
        try {
            log.info("Fetching Huobi prices from: {}", huobiUrl);
            String huobiResponse = restTemplate.getForObject(huobiUrl, String.class);
            log.debug("Huobi raw response: {}", huobiResponse);

            HuobiResponseDto response = objectMapper.readValue(huobiResponse, HuobiResponseDto.class);

            if (response != null && response.getData() != null) {
                response.getData().stream()
                        .filter(p -> "btcusdt".equalsIgnoreCase(p.getSymbol()) ||
                                "ethusdt".equalsIgnoreCase(p.getSymbol()))
                        .forEach(hp -> updateBestPrices(bestPrices, convertHuobiPrice(hp)));
            }

        } catch (Exception e) {
            log.error("Error processing Huobi prices: ", e);
        }
    }

    private Price convertBinancePrice(BinancePriceDto bp) {
        return Price.builder()
                .symbol(bp.getSymbol())
                .bidPrice(new BigDecimal(bp.getBidPrice()))
                .askPrice(new BigDecimal(bp.getAskPrice()))
                .bidSource("BINANCE")
                .askSource("BINANCE")
                .timestamp(LocalDateTime.now())
                .build();
    }

    private Price convertHuobiPrice(HuobiPriceDto hp) {
        return Price.builder()
                .symbol(hp.getSymbol().toUpperCase())
                .bidPrice(hp.getBid())
                .askPrice(hp.getAsk())
                .bidSource("HUOBI")
                .askSource("HUOBI")
                .timestamp(LocalDateTime.now())
                .build();
    }

    private void updateBestPrices(Map<String, Price> bestPrices, Price newPrice) {
        if (!isValidPrice(newPrice)) {
            log.warn("Invalid price skipped: {}", newPrice);
            return;
        }

        Price existingPrice = bestPrices.get(newPrice.getSymbol());

        if (existingPrice == null) {
            bestPrices.put(newPrice.getSymbol(), newPrice);
            return;
        }

        // Update bid if new price is higher
        if (newPrice.getBidPrice().compareTo(existingPrice.getBidPrice()) > 0) {
            existingPrice.setBidPrice(newPrice.getBidPrice());
            existingPrice.setBidSource(newPrice.getBidSource());
            log.debug("Updated best bid for {}: {} from {}",
                    newPrice.getSymbol(), newPrice.getBidPrice(), newPrice.getBidSource());
        }

        // Update ask if new price is lower
        if (newPrice.getAskPrice().compareTo(existingPrice.getAskPrice()) < 0) {
            existingPrice.setAskPrice(newPrice.getAskPrice());
            existingPrice.setAskSource(newPrice.getAskSource());
            log.debug("Updated best ask for {}: {} from {}",
                    newPrice.getSymbol(), newPrice.getAskPrice(), newPrice.getAskSource());
        }
    }

    private boolean isValidPrice(Price price) {
        try {
            boolean isValid = price != null &&
                    price.getSymbol() != null &&
                    price.getBidPrice() != null &&
                    price.getAskPrice() != null &&
                    price.getBidPrice().compareTo(BigDecimal.ZERO) > 0 &&
                    price.getAskPrice().compareTo(BigDecimal.ZERO) > 0 &&
                    price.getAskPrice().compareTo(price.getBidPrice()) >= 0;

            if (!isValid) {
                log.warn("Invalid price found: {}", price);
            }

            return isValid;
        } catch (Exception e) {
            log.error("Error validating price: {}", price, e);
            return false;
        }
    }
}