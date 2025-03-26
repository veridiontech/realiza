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

    @Override
    public CenterResponseDto save(CenterRequestDto request) {
        Center center = Center.builder()
                .name(request.getName())
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
                .build();
    }
}