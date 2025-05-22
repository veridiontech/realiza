package bl.tech.realiza.gateways.controllers.interfaces.dashboard;

import bl.tech.realiza.gateways.responses.dashboard.DashboardDetailsResponseDto;
import bl.tech.realiza.gateways.responses.dashboard.DashboardHomeResponseDto;
import org.springframework.http.ResponseEntity;

public interface DashboardController {
    ResponseEntity<DashboardHomeResponseDto> getDashboardHome(String branchId);
    ResponseEntity<DashboardDetailsResponseDto> getDashboardDetails(String branchId);
}
