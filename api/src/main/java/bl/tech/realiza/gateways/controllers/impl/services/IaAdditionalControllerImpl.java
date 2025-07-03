package bl.tech.realiza.gateways.controllers.impl.services;

import bl.tech.realiza.gateways.controllers.interfaces.services.IaAdditionalPromptController;
import bl.tech.realiza.gateways.requests.services.iaAdditionalPrompt.IaAdditionalPromptRequestDto;
import bl.tech.realiza.gateways.responses.services.iaAditionalPrompt.IaAdditionalPromptNameListResponseDto;
import bl.tech.realiza.gateways.responses.services.iaAditionalPrompt.IaAdditionalPromptResponseDto;
import bl.tech.realiza.usecases.impl.documents.CrudIaAdditionalPromptImpl;
import bl.tech.realiza.usecases.interfaces.documents.CrudIaAdditionalPrompt;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/prompt")
@Tag(name = "IA Additional Prompt")
public class IaAdditionalControllerImpl implements IaAdditionalPromptController {
    private final CrudIaAdditionalPrompt crudIaAdditionalPrompt;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<IaAdditionalPromptResponseDto> createPrompt(@Valid  @RequestBody IaAdditionalPromptRequestDto iaAdditionalPromptRequestDto) {
        return ResponseEntity.ok(crudIaAdditionalPrompt.save(iaAdditionalPromptRequestDto));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<List<IaAdditionalPromptResponseDto>> getAllPrompt() {
        return ResponseEntity.ok(crudIaAdditionalPrompt.findAll());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<IaAdditionalPromptResponseDto> getOnePrompt(@PathVariable String id) {
        return ResponseEntity.ok(crudIaAdditionalPrompt.findById(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<IaAdditionalPromptResponseDto> updatePrompt(@PathVariable String id, @Valid @RequestBody IaAdditionalPromptRequestDto iaAdditionalPromptRequestDto) {
        return ResponseEntity.ok(crudIaAdditionalPrompt.update(id, iaAdditionalPromptRequestDto));
    }

    @GetMapping("/name-list")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<List<IaAdditionalPromptNameListResponseDto>> getAllPromptNameList() {
        return ResponseEntity.ok(crudIaAdditionalPrompt.findAllNameList());
    }
}
