package bl.tech.realiza.gateways.controllers.interfaces.ultragaz;

import bl.tech.realiza.gateways.requests.ultragaz.MarketRequestDto;
import bl.tech.realiza.gateways.responses.ultragaz.MarketResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

public interface MarketController {
    ResponseEntity<MarketResponseDto> createMarket(MarketRequestDto marketRequestDto);
    ResponseEntity<MarketResponseDto> getOneMarket(String id);
    ResponseEntity<Page<MarketResponseDto>> getAllMarkets(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<MarketResponseDto> updateMarket(String id, MarketRequestDto marketRequestDto);
    ResponseEntity<Void> deleteMarket(String id);
}
