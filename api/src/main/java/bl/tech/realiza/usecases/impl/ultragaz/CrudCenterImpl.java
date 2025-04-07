package bl.tech.realiza.usecases.impl.ultragaz;

import bl.tech.realiza.domains.ultragaz.Center;
import bl.tech.realiza.domains.ultragaz.Market;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.ultragaz.CenterRepository;
import bl.tech.realiza.gateways.repositories.ultragaz.MarketRepository;
import bl.tech.realiza.gateways.requests.ultragaz.CenterRequestDto;
import bl.tech.realiza.gateways.responses.ultragaz.CenterResponseDto;
import bl.tech.realiza.usecases.interfaces.ultragaz.CrudCenter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrudCenterImpl implements CrudCenter {

    private final CenterRepository centerRepository;
    private final MarketRepository marketRepository;

    @Override
    public CenterResponseDto save(CenterRequestDto request) {

        Market market = marketRepository.findById(request.getIdMarket())
                .orElseThrow(() -> new NotFoundException("Market not found"));

        Center center = Center.builder()
                .name(request.getName())
                .market(market)
                .build();

        Center saved = centerRepository.save(center);
        return toResponse(saved);
    }

    @Override
    public CenterResponseDto findOne(String id) {
        Center center = centerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Center not found"));
        return toResponse(center);
    }

    @Override
    public Page<CenterResponseDto> findAll(Pageable pageable) {
        return centerRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    public Page<CenterResponseDto> findAllByMarket(String idMarket, Pageable pageable) {
        return centerRepository.findAllByMarket_IdMarket(idMarket, pageable)
                .map(this::toResponse);
    }

    @Override
    public CenterResponseDto update(String id, CenterRequestDto request) {
        Center center = centerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Center not found"));

        center.setName(request.getName() != null ? request.getName() : center.getName());

        Center updated = centerRepository.save(center);
        return toResponse(updated);
    }

    @Override
    public void delete(String id) {
        centerRepository.deleteById(id);
    }

    private CenterResponseDto toResponse(Center center) {
        return CenterResponseDto.builder()
                .idCenter(center.getIdCenter())
                .name(center.getName())
                .idMarket(center.getMarket().getIdMarket())
                .build();
    }
}