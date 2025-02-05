package aquariux.mapper;

import aquariux.dto.request.TradeRequestDto;
import aquariux.dto.response.TradeResponseDto;
import aquariux.model.Trade;
import aquariux.model.User;
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
public class TradeMapperImpl implements TradeMapper {

    @Override
    public Trade toEntity(TradeRequestDto request) {
        if ( request == null ) {
            return null;
        }

        Trade trade = new Trade();

        trade.setQuantity( request.getQuantity() );
        trade.setSymbol( request.getSymbol() );
        trade.setTradeType( request.getTradeType() );

        trade.setTimestamp( LocalDateTime.now() );
        trade.setUser( createUser(request.getUserId()) );

        return trade;
    }

    @Override
    public TradeResponseDto toDto(Trade trade) {
        if ( trade == null ) {
            return null;
        }

        TradeResponseDto.TradeResponseDtoBuilder tradeResponseDto = TradeResponseDto.builder();

        tradeResponseDto.userId( tradeUserId( trade ) );
        tradeResponseDto.id( trade.getId() );
        tradeResponseDto.price( trade.getPrice() );
        tradeResponseDto.quantity( trade.getQuantity() );
        tradeResponseDto.symbol( trade.getSymbol() );
        tradeResponseDto.timestamp( trade.getTimestamp() );
        tradeResponseDto.totalTradeValue( trade.getTotalTradeValue() );
        tradeResponseDto.tradeType( trade.getTradeType() );

        return tradeResponseDto.build();
    }

    @Override
    public List<TradeResponseDto> toDtoList(List<Trade> trades) {
        if ( trades == null ) {
            return null;
        }

        List<TradeResponseDto> list = new ArrayList<TradeResponseDto>( trades.size() );
        for ( Trade trade : trades ) {
            list.add( toDto( trade ) );
        }

        return list;
    }

    private Long tradeUserId(Trade trade) {
        if ( trade == null ) {
            return null;
        }
        User user = trade.getUser();
        if ( user == null ) {
            return null;
        }
        Long id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
