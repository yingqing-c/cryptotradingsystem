package aquariux.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aquariux.model.Price;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
    Optional<Price> findTopBySymbolOrderByTimestampDesc(String symbol);

    List<Price> findBySymbolAndTimestampGreaterThanOrderByTimestampDesc(
        String symbol, 
        LocalDateTime timestamp);
}
