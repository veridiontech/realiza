package bl.tech.realiza.gateways.controllers.impl.dashboard;

import bl.tech.realiza.gateways.controllers.interfaces.dashboard.DashboardController;
import bl.tech.realiza.gateways.requests.dashboard.DashboardFiltersRequestDto;
import bl.tech.realiza.gateways.responses.dashboard.*;
import bl.tech.realiza.services.dashboard.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @PostMapping("/{clientId}/document")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Busca informações dos documentos por status")
    @Override
    public ResponseEntity<DashboardDocumentStatusResponseDto> getDocumentStatusInfo(@PathVariable String clientId,
                                                                                    @RequestBody(required = false) DashboardFiltersRequestDto dashboardFiltersRequestDto) {
        return ResponseEntity.ok(dashboardService.getDocumentStatusInfo(clientId, dashboardFiltersRequestDto));
    }

    @PostMapping("/{clientId}/document/details")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Busca informações detalhadas dos documentos")
    public ResponseEntity<Page<DashboardDocumentDetailsResponseDto>> getDocumentDetailsInfo(@PathVariable String clientId,
                                                                                            @RequestBody(required = false) DashboardFiltersRequestDto dashboardFiltersRequestDto,
                                                                                            @RequestParam(defaultValue = "0") int page,
                                                                                            @RequestParam(defaultValue = "50") int size,
                                                                                            @RequestParam(defaultValue = "branchName") String sort,
                                                                                            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));
        return ResponseEntity.ok(dashboardService.getDocumentDetailsInfo(clientId, dashboardFiltersRequestDto, pageable));
    }

    @PostMapping("/{clientId}/general")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Busca informações do dashboard geral")
    @Override
    public ResponseEntity<DashboardGeneralDetailsResponseDto> getGeneralDetailsInfo(@PathVariable String clientId,
                                                                                    @RequestBody(required = false) DashboardFiltersRequestDto dashboardFiltersRequestDto) {
        return ResponseEntity.ok(dashboardService.getGeneralDetailsInfo(clientId, dashboardFiltersRequestDto));
    }

    @PostMapping("/{clientId}/provider")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Busca informações do dashboard de fornecedores e subcontratados")
    @Override
    public ResponseEntity<List<DashboardProviderDetailsResponseDto>> getProviderDetailsInfo(@PathVariable String clientId,
                                                                                            @RequestBody(required = false) DashboardFiltersRequestDto dashboardFiltersRequestDto) {
        return ResponseEntity.ok(dashboardService.getProviderDetailsInfo(clientId, dashboardFiltersRequestDto));
    }

    @GetMapping("/{clientId}/filters")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Busca filtros para o dashboard")
    @Override
    public ResponseEntity<DashboardFiltersResponse> getFiltersInfo(@PathVariable String clientId) {
        return ResponseEntity.ok(dashboardService.getFiltersInfo(clientId));
    }
}
