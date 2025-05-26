package bl.tech.realiza.gateways.controllers.impl.dashboard;

import bl.tech.realiza.gateways.controllers.interfaces.dashboard.DashboardController;
import bl.tech.realiza.gateways.responses.dashboard.DashboardDetailsResponseDto;
import bl.tech.realiza.gateways.responses.dashboard.DashboardHomeResponseDto;
import bl.tech.realiza.services.dashboard.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
