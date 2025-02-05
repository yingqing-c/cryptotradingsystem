package aquariux.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HuobiResponseDto {
    private String status;
    private Long ts;
    private List<HuobiPriceDto> data;
}
