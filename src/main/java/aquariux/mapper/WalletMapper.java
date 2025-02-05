package aquariux.mapper;

import aquariux.dto.response.WalletResponseDto;
import aquariux.model.Wallet;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    WalletResponseDto toDto(Wallet wallet);
    List<WalletResponseDto> toDtoList(List<Wallet> wallets);
}