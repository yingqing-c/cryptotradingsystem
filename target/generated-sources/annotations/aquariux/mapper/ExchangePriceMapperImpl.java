package aquariux.mapper;

import aquariux.dto.BinancePriceDto;
import aquariux.dto.HuobiPriceDto;
import aquariux.model.Price;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-05T09:18:23+0800",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.41.0.z20250115-2156, environment: Java 21.0.5 (Eclipse Adoptium)"
)
@Component
public class ExchangePriceMapperImpl implements ExchangePriceMapper {

    @Override
    public Price fromBinance(BinancePriceDto binancePrice) {
        if ( binancePrice == null ) {
            return null;
        }

        Price.PriceBuilder price = Price.builder();

        price.symbol( binancePrice.getSymbol() );

        price.bidPrice( new BigDecimal(binancePrice.getBidPrice()) );
        price.askPrice( new BigDecimal(binancePrice.getAskPrice()) );
        price.bidSource( "BINANCE" );
        price.askSource( "BINANCE" );
        price.timestamp( LocalDateTime.now() );

        return price.build();
    }

    @Override
    public Price fromHuobi(HuobiPriceDto huobiPrice) {
        if ( huobiPrice == null ) {
            return null;
        }

        Price.PriceBuilder price = Price.builder();

        price.bidPrice( huobiPrice.getBid() );
        price.askPrice( huobiPrice.getAsk() );

        price.symbol( huobiPrice.getSymbol().toUpperCase() );
        price.bidSource( "HUOBI" );
        price.askSource( "HUOBI" );
        price.timestamp( LocalDateTime.now() );

        return price.build();
    }

    @Override
    public List<Price> fromBinanceList(List<BinancePriceDto> prices) {
        if ( prices == null ) {
            return null;
        }

        List<Price> list = new ArrayList<Price>( prices.size() );
        for ( BinancePriceDto binancePriceDto : prices ) {
            list.add( fromBinance( binancePriceDto ) );
        }

        return list;
    }

    @Override
    public List<Price> fromHuobiList(List<HuobiPriceDto> prices) {
        if ( prices == null ) {
            return null;
        }

        List<Price> list = new ArrayList<Price>( prices.size() );
        for ( HuobiPriceDto huobiPriceDto : prices ) {
            list.add( fromHuobi( huobiPriceDto ) );
        }

        return list;
    }
}
