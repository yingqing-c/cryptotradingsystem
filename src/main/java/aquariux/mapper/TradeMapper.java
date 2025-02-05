package aquariux.mapper;

import java.time.LocalDateTime;
import java.util.List;

import aquariux.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import aquariux.dto.request.TradeRequestDto;
import aquariux.dto.response.TradeResponseDto;
import aquariux.model.Trade;

@Mapper(
    componentModel = "spring", 
    imports = {LocalDateTime.class}
)
public interface TradeMapper {
    
    TradeMapper INSTANCE = Mappers.getMapper(TradeMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "totalTradeValue", ignore = true)
    @Mapping(target = "timestamp", expression = "java(LocalDateTime.now())")
    @Mapping(target = "user", expression = "java(createUser(request.getUserId()))")
    Trade toEntity(TradeRequestDto request);

    @Mapping(source = "user.id", target = "userId")
    TradeResponseDto toDto(Trade trade);

    List<TradeResponseDto> toDtoList(List<Trade> trades);

    default User createUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
}