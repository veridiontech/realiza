package bl.tech.realiza.gateways.controllers.interfaces.dashboard;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.gateways.requests.dashboard.DashboardFiltersRequestDto;
import bl.tech.realiza.gateways.responses.dashboard.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static bl.tech.realiza.domains.contract.Contract.*;
import static bl.tech.realiza.domains.documents.Document.*;

public interface DashboardController {
    ResponseEntity<DashboardHomeResponseDto> getDashboardHome(String branchId);
    ResponseEntity<DashboardDetailsResponseDto> getDashboardDetails(String branchId);
    ResponseEntity<DashboardDocumentResponseDto> getDocumentDetails(String clientId, DashboardFiltersRequestDto dashboardFiltersRequestDto);
    ResponseEntity<DashboardGeneralDetailsResponseDto> getGeneralDetailsInfo(String clientId, DashboardFiltersRequestDto dashboardFiltersRequestDto);
    ResponseEntity<List<DashboardProviderDetailsResponseDto>> getProviderDetailsInfo(String clientId, DashboardFiltersRequestDto dashboardFiltersRequestDto);
}
