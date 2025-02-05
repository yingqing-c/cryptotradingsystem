package aquariux.repository;

import java.util.List;

import aquariux.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aquariux.model.Trade;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByUserOrderByTimestampDesc(User user);
}