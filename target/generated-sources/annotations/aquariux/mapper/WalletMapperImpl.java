package aquariux.mapper;

import aquariux.dto.response.WalletResponseDto;
import aquariux.model.Wallet;
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
public class WalletMapperImpl implements WalletMapper {

    @Override
    public WalletResponseDto toDto(Wallet wallet) {
        if ( wallet == null ) {
            return null;
        }

        WalletResponseDto.WalletResponseDtoBuilder walletResponseDto = WalletResponseDto.builder();

        walletResponseDto.balance( wallet.getBalance() );
        walletResponseDto.currency( wallet.getCurrency() );

        return walletResponseDto.build();
    }

    @Override
    public List<WalletResponseDto> toDtoList(List<Wallet> wallets) {
        if ( wallets == null ) {
            return null;
        }

        List<WalletResponseDto> list = new ArrayList<WalletResponseDto>( wallets.size() );
        for ( Wallet wallet : wallets ) {
            list.add( toDto( wallet ) );
        }

        return list;
    }
}
