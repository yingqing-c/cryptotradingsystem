package aquariux.mapper;


import aquariux.dto.BinancePriceDto;
import aquariux.dto.HuobiPriceDto;
import aquariux.model.Price;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(
        componentModel = "spring",
        imports = {LocalDateTime.class, BigDecimal.class}
)
public interface ExchangePriceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bidPrice", expression = "java(new BigDecimal(binancePrice.getBidPrice()))")
    @Mapping(target = "askPrice", expression = "java(new BigDecimal(binancePrice.getAskPrice()))")
    @Mapping(target = "bidSource", constant = "BINANCE")
    @Mapping(target = "askSource", constant = "BINANCE")
    @Mapping(target = "timestamp", expression = "java(LocalDateTime.now())")
    Price fromBinance(BinancePriceDto binancePrice);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "symbol", expression = "java(huobiPrice.getSymbol().toUpperCase())")
    @Mapping(target = "bidPrice", source = "bid")
    @Mapping(target = "askPrice", source = "ask")
    @Mapping(target = "bidSource", constant = "HUOBI")
    @Mapping(target = "askSource", constant = "HUOBI")
    @Mapping(target = "timestamp", expression = "java(LocalDateTime.now())")
    Price fromHuobi(HuobiPriceDto huobiPrice);

    List<Price> fromBinanceList(List<BinancePriceDto> prices);

    List<Price> fromHuobiList(List<HuobiPriceDto> prices);
}