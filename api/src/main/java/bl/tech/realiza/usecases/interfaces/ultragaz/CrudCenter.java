package bl.tech.realiza.usecases.interfaces.ultragaz;

import bl.tech.realiza.gateways.requests.ultragaz.CenterRequestDto;
import bl.tech.realiza.gateways.responses.ultragaz.CenterResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudCenter {
    CenterResponseDto save(CenterRequestDto request);
    CenterResponseDto findOne(String id);
    Page<CenterResponseDto> findAll(Pageable pageable);
    CenterResponseDto update(String id, CenterRequestDto request);
    void delete(String id);
}
