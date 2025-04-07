package bl.tech.realiza.gateways.controllers.interfaces.ultragaz;

import bl.tech.realiza.gateways.requests.ultragaz.CenterRequestDto;
import bl.tech.realiza.gateways.responses.ultragaz.CenterResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

public interface CenterController {
    ResponseEntity<CenterResponseDto> createCenter(CenterRequestDto centerRequestDto);
    ResponseEntity<CenterResponseDto> getOneCenter(String id);
    ResponseEntity<Page<CenterResponseDto>> getAllCenters(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Page<CenterResponseDto>> getAllCentersByMarket(int page, int size, String sort, Sort.Direction direction, String idMarket);
    ResponseEntity<CenterResponseDto> updateCenter(String id, CenterRequestDto centerRequestDto);
    ResponseEntity<Void> deleteCenter(String id);
}
