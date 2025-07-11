package bl.tech.realiza.gateways.controllers.impl.dashboard;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.gateways.controllers.interfaces.dashboard.DashboardController;
import bl.tech.realiza.gateways.responses.dashboard.DashboardDetailsResponseDto;
import bl.tech.realiza.gateways.responses.dashboard.DashboardGeneralDetailsResponseDto;
import bl.tech.realiza.gateways.responses.dashboard.DashboardHomeResponseDto;
import bl.tech.realiza.gateways.responses.dashboard.DashboardProviderDetailsResponseDto;
import bl.tech.realiza.services.dashboard.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
@Tag(name = "Dashboard")
public class DashboardControllerImpl implements DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/home/{branchId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Busca informações da home")
    @Override
    public ResponseEntity<DashboardHomeResponseDto> getDashboardHome(@PathVariable String branchId) {
        return ResponseEntity.ok(dashboardService.getHomeInfo(branchId));
    }

    @GetMapping("/{branchId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Busca informações da do dashboard detalhado")
    @Override
    public ResponseEntity<DashboardDetailsResponseDto> getDashboardDetails(@PathVariable String branchId) {
        return ResponseEntity.ok(dashboardService.getDetailsInfo(branchId));
    }

    @GetMapping("/{clientId}/general")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Busca informações do dashboard geral")
    @Override
    public ResponseEntity<DashboardGeneralDetailsResponseDto> getGeneralDetailsInfo(@PathVariable String clientId,
                                                                                    @RequestParam(required = false) List<String> branchIds,
                                                                                    @RequestParam(required = false) List<String> documentTypes,
                                                                                    @RequestParam(required = false) List<String> responsibleIds,
                                                                                    @RequestParam(required = false) List<Contract.IsActive> activeContract,
                                                                                    @RequestParam(required = false) List<Document.Status> statuses,
                                                                                    @RequestParam(required = false) List<String> documentTitles) {
        return ResponseEntity.ok(dashboardService.getGeneralDetailsInfo(clientId,
                branchIds,
                documentTypes,
                responsibleIds,
                activeContract,
                statuses,
                documentTitles));
    }

    @GetMapping("/{clientId}/provider")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Busca informações do dashboard de fornecedores e subcontratados")
    @Override
    public ResponseEntity<List<DashboardProviderDetailsResponseDto>> getProviderDetailsInfo(@PathVariable String clientId,
                                                                                            @RequestParam(required = false) List<String> branchIds,
                                                                                            @RequestParam(required = false) List<String> documentTypes,
                                                                                            @RequestParam(required = false) List<String> responsibleIds,
                                                                                            @RequestParam(required = false) List<Document.Status> statuses,
                                                                                            @RequestParam(required = false) List<String> documentTitles) {
        return ResponseEntity.ok(dashboardService.getProviderDetailsInfo(clientId,
                branchIds,
                documentTypes,
                responsibleIds,
                statuses,
                documentTitles));
    }
}
