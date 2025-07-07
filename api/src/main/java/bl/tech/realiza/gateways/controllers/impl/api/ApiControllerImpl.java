package bl.tech.realiza.gateways.controllers.impl.api;

import bl.tech.realiza.gateways.controllers.interfaces.api.ApiController;
import bl.tech.realiza.gateways.responses.api.document.employee.DocumentEmployeeExternalApiResponseDto;
import bl.tech.realiza.services.api.ApiService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "API Externa")
public class ApiControllerImpl implements ApiController {
    private final ApiService apiService;

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<String> createToken(@RequestParam String enterpriseName,
                                              @RequestParam String enterpriseCnpj,
                                              @RequestParam String password) {
        return ResponseEntity.ok(apiService.generateToken(enterpriseName, enterpriseCnpj, password));
    }

    @GetMapping("/employee/status")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Boolean> checkEmployeeStatus(@RequestParam String cpf) {
        return ResponseEntity.ok(true);
    }

    @GetMapping("/employee/status/details")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<DocumentEmployeeExternalApiResponseDto> checkEmployeeNotApprovedDocuments(@RequestParam String cpf) {
        return null;
    }
}
