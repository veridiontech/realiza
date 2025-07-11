package bl.tech.realiza.gateways.controllers.interfaces.dashboard;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.gateways.responses.dashboard.DashboardDetailsResponseDto;
import bl.tech.realiza.gateways.responses.dashboard.DashboardGeneralDetailsResponseDto;
import bl.tech.realiza.gateways.responses.dashboard.DashboardHomeResponseDto;
import bl.tech.realiza.gateways.responses.dashboard.DashboardProviderDetailsResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static bl.tech.realiza.domains.contract.Contract.*;
import static bl.tech.realiza.domains.documents.Document.*;

public interface DashboardController {
    ResponseEntity<DashboardHomeResponseDto> getDashboardHome(String branchId);
    ResponseEntity<DashboardDetailsResponseDto> getDashboardDetails(String branchId);
    ResponseEntity<DashboardGeneralDetailsResponseDto> getGeneralDetailsInfo(String clientId,
                                                                    List<String> branchIds,
                                                                    List<String> documentTypes,
                                                                    List<String> responsibleIds,
                                                                    List<IsActive> activeContract,
                                                                    List<Status> statuses,
                                                                    List<String> documentTitles);
    ResponseEntity<List<DashboardProviderDetailsResponseDto>> getProviderDetailsInfo(String clientId,
                                                                            List<String> branchIds,
                                                                            List<String> documentTypes,
                                                                            List<String> responsibleIds,
                                                                            List<Status> statuses,
                                                                            List<String> documentTitles);
}
