package aquariux.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@Data
@Table (name = "prices")
@NoArgsConstructor
@AllArgsConstructor
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

   @Column(nullable = false)
    private String symbol;
    
    @Column(name = "bid_price", nullable = false)
    private BigDecimal bidPrice;

    @Column(name = "bid_source")
    private String bidSource;

    @Column(name = "ask_price", nullable = false)
    private BigDecimal askPrice;

    @Column(name = "ask_source")
    private String askSource;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
}
