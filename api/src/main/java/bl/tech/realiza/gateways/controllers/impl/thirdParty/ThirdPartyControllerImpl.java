package bl.tech.realiza.gateways.controllers.impl.thirdParty;

import bl.tech.realiza.gateways.controllers.interfaces.thirdParty.ThirdPartyController;
import bl.tech.realiza.gateways.requests.services.LoginRequestDto;
import bl.tech.realiza.services.thirdParty.ThirdPartyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/third-party")
@Tag(name = "Third Party")
public class ThirdPartyControllerImpl implements ThirdPartyController {
    private final ThirdPartyService thirdPartyService;

    @GetMapping("/status/{employeeCpf}")
    @Override
    public ResponseEntity<Boolean> checkEmployeeByClient(@PathVariable String employeeCpf, @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(thirdPartyService.employeeStatus(employeeCpf, request));
    }
}
