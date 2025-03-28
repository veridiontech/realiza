package bl.tech.realiza.usecases.interfaces.ultragaz;

import bl.tech.realiza.gateways.requests.ultragaz.MarketRequestDto;
import bl.tech.realiza.gateways.responses.ultragaz.MarketResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudMarket {
    MarketResponseDto save(MarketRequestDto request);
    MarketResponseDto findOne(String id);
    Page<MarketResponseDto> findAll(Pageable pageable);
    MarketResponseDto update(String id, MarketRequestDto request);
    void delete(String id);
}