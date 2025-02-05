package aquariux.controller;

import java.util.List;

import aquariux.dto.response.WalletResponseDto;
import aquariux.model.User;
import aquariux.repository.UserRepository;
import aquariux.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aquariux.dto.request.TradeRequestDto;
import aquariux.dto.response.TradeResponseDto;
import aquariux.model.Price;
import aquariux.model.Trade;
import aquariux.repository.PriceRepository;
import aquariux.repository.TradeRepository;
import aquariux.service.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TradeController {

    private final PriceRepository priceRepository;

    private final TradeService tradeService;

    private final WalletService walletService;

    private final TradeRepository tradeRepository;

    private final UserRepository userRepository;

    // Retrieve the best aggregated price
    @GetMapping("/prices/{symbol}")
    public ResponseEntity<Price> getLatestPrice(@PathVariable String symbol) {
        return priceRepository.findTopBySymbolOrderByTimestampDesc(symbol)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Allow users to trade based on the latest best aggregated price.
    @PostMapping("/trades")
    public ResponseEntity<TradeResponseDto> executeTrade(
            @Valid @RequestBody TradeRequestDto request) {

                TradeResponseDto response = tradeService.executeTrade(request);
        return ResponseEntity.ok(response);
    }

    // Retrieve the user's wallet balance
    @GetMapping("/wallets/{userId}")
    public ResponseEntity<List<WalletResponseDto>> getUserWallets(@PathVariable Long userId) {
        List<WalletResponseDto> wallets = walletService.getUserWallets(userId);
        return ResponseEntity.ok(wallets);
    }

    // Retrieve the user's trading history
    @GetMapping("/trades/{userId}")
    public ResponseEntity<List<Trade>> getUserTrades(@PathVariable Long userId) {
        java.util.Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();

        List<Trade> trades = tradeRepository.findByUserOrderByTimestampDesc(user);

        return ResponseEntity.ok(trades);
    }
}
