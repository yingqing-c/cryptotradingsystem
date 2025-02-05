package aquariux.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Arrays;

import aquariux.model.User;
import aquariux.repository.UserRepository;
import aquariux.dto.response.WalletResponseDto;
import aquariux.mapper.WalletMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aquariux.model.Wallet;
import aquariux.repository.WalletRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final WalletMapper walletMapper;

    public Wallet createWallet(Long userId, String currency, BigDecimal initialBalance) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setCurrency(currency);
        wallet.setBalance(initialBalance != null ? initialBalance : BigDecimal.ZERO);

        return walletRepository.save(wallet);
    }

    public void validateAndUpdateBalance(Long userId, String currency, BigDecimal amount) {
        // Verify user exists first
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Try to find existing wallet, or create a new one if not found
        Wallet wallet = walletRepository.findByUserIdAndCurrency(userId, currency)
                .orElseGet(() -> {
                    // Create a new wallet with zero balance
                    Wallet newWallet = new Wallet();
                    newWallet.setUser(user);
                    newWallet.setCurrency(currency);
                    newWallet.setBalance(BigDecimal.ZERO);
                    return walletRepository.save(newWallet);
                });

        // Calculate new balance
        BigDecimal newBalance = wallet.getBalance().add(amount);

        // Check for insufficient balance for negative amounts
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Update wallet balance
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);
    }

    public List<WalletResponseDto> getUserWallets(Long userId) {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find wallets for the user
        List<Wallet> wallets = walletRepository.findByUserId(userId);

        // If no wallets exist, create default wallets
        if (wallets.isEmpty()) {
            wallets = createDefaultWallets(userId);
        }

        return walletMapper.toDtoList(wallets);
    }

    private List<Wallet> createDefaultWallets(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Wallet> defaultWallets = Arrays.asList(
                createDefaultWallet(user, "USDT"),
                createDefaultWallet(user, "BTCUSDT"),
                createDefaultWallet(user, "ETHUSDT")
        );

        return walletRepository.saveAll(defaultWallets);
    }

    private Wallet createDefaultWallet(User user, String currency) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setCurrency(currency);
        wallet.setBalance(BigDecimal.ZERO);
        return wallet;
    }
}