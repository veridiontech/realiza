package bl.tech.realiza.gateways.controllers.interfaces.dashboard;

import bl.tech.realiza.gateways.requests.dashboard.DashboardFiltersRequestDto;
import bl.tech.realiza.gateways.responses.dashboard.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DashboardController {
    ResponseEntity<DashboardHomeResponseDto> getDashboardHome(String branchId);
    ResponseEntity<DashboardDetailsResponseDto> getDashboardDetails(String branchId);
    ResponseEntity<DashboardDocumentStatusResponseDto> getDocumentStatusInfo(String clientId, DashboardFiltersRequestDto dashboardFiltersRequestDto);
    ResponseEntity<Page<DashboardDocumentDetailsResponseDto>> getDocumentDetailsInfo(String clientId, DashboardFiltersRequestDto dashboardFiltersRequestDto, int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<DashboardGeneralDetailsResponseDto> getGeneralDetailsInfo(String clientId, DashboardFiltersRequestDto dashboardFiltersRequestDto);
    ResponseEntity<List<DashboardProviderDetailsResponseDto>> getProviderDetailsInfo(String clientId, DashboardFiltersRequestDto dashboardFiltersRequestDto);
    ResponseEntity<DashboardFiltersResponse> getFiltersInfo(String clientId);
}
