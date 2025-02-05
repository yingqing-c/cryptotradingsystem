package aquariux.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import aquariux.model.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    List<Wallet> findByUserId(@Param("userId") Long userId);
    Optional<Wallet> findByUserIdAndCurrency(Long userId, String currency);
}
