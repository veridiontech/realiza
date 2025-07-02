package bl.tech.realiza.gateways.controllers.impl.users;

import bl.tech.realiza.gateways.controllers.interfaces.users.ProfileController;
import bl.tech.realiza.gateways.requests.users.ProfileRequestDto;
import bl.tech.realiza.gateways.responses.users.ProfileNameResponseDto;
import bl.tech.realiza.gateways.responses.users.ProfileResponseDto;
import bl.tech.realiza.usecases.interfaces.users.CrudProfile;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
@Tag(name = "Profile")
public class ProfileControllerImpl implements ProfileController {
    private final CrudProfile crudProfile;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ProfileResponseDto> createProfile(@Valid @RequestBody ProfileRequestDto profileRequestDto) {
        return ResponseEntity.ok(crudProfile.save(profileRequestDto));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<ProfileResponseDto> getOneProfile(@PathVariable String id) {
        return ResponseEntity.ok(crudProfile.findOne(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<List<ProfileResponseDto>> getAllProfiles() {
        return ResponseEntity.ok(crudProfile.findAll());
    }

    @GetMapping("/by-name/{clientId}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<List<ProfileNameResponseDto>> getAllProfileNamesByClientId(@PathVariable String clientId) {
        return ResponseEntity.ok(crudProfile.findAllByClientId(clientId));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<ProfileResponseDto> updateProfile(@PathVariable String id, @Valid @RequestBody ProfileRequestDto profileRequestDto) {
        return ResponseEntity.ok(crudProfile.update(id, profileRequestDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteProfile(@PathVariable String id) {
        crudProfile.delete(id);
        return ResponseEntity.noContent().build();
    }
}
