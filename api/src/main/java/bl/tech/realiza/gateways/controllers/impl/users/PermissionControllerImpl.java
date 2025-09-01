package bl.tech.realiza.gateways.controllers.impl.users;

import bl.tech.realiza.gateways.controllers.interfaces.users.PermissionController;
import bl.tech.realiza.gateways.requests.users.security.CreatePermissionRequest;
import bl.tech.realiza.gateways.responses.users.profile.PermissionResponse;
import bl.tech.realiza.usecases.interfaces.users.security.CrudPermission;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/permission")
@Tag(name = "Permission")
public class PermissionControllerImpl implements PermissionController {
    private final CrudPermission crudPermission;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<PermissionResponse> save(@RequestBody CreatePermissionRequest request) {
        return ResponseEntity.ok(crudPermission.save(request));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<PermissionResponse> findOne(@PathVariable String id) {
        return ResponseEntity.ok(crudPermission.findOne(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<List<PermissionResponse>> findAll() {
        return ResponseEntity.ok(crudPermission.findAll());
    }
}
