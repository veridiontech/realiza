package bl.tech.realiza.usecases.impl.ultragaz;

import bl.tech.realiza.domains.ultragaz.Market;
import bl.tech.realiza.domains.ultragaz.Market;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.ultragaz.MarketRepository;
import bl.tech.realiza.gateways.repositories.ultragaz.MarketRepository;
import bl.tech.realiza.gateways.requests.ultragaz.MarketRequestDto;
import bl.tech.realiza.gateways.responses.ultragaz.MarketResponseDto;
import bl.tech.realiza.usecases.interfaces.ultragaz.CrudMarket;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrudMarketImpl implements CrudMarket {

    private final MarketRepository marketRepository;

    @Override
    public MarketResponseDto save(MarketRequestDto request) {

        Market market = Market.builder()
                .name(request.getName())
                .build();

        Market saved = marketRepository.save(market);

        return toResponse(saved);
    }

    @Override
    public MarketResponseDto findOne(String id) {
        Market market = marketRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Market not found"));
        return toResponse(market);
    }

    @Override
    public Page<MarketResponseDto> findAll(Pageable pageable) {
        return marketRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    public MarketResponseDto update(String id, MarketRequestDto request) {
        Market market = marketRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Market não encontrado"));

        market.setName(request.getName() != null ? request.getName() : market.getName());

        Market updated = marketRepository.save(market);
        return toResponse(updated);
    }

    @Override
    public void delete(String id) {
        marketRepository.deleteById(id);
    }

    private MarketResponseDto toResponse(Market market) {
        return MarketResponseDto.builder()
                .idMarket(market.getIdMarket())
                .name(market.getName())
                .build();
    }
}