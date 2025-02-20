package bl.tech.realiza.gateways.controllers.impl.services;

import bl.tech.realiza.gateways.controllers.interfaces.services.DocumentController;
import bl.tech.realiza.gateways.responses.services.DocumentIAValidationResponse;
import bl.tech.realiza.services.documentProcessing.DocumentProcessingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/validate-document")
@Tag(name = "Validation")
public class DocumentControllerImpl implements DocumentController {

    private final DocumentProcessingService documentProcessingService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<DocumentIAValidationResponse> validateDocument(@RequestParam("file") MultipartFile file) throws IOException {
        DocumentIAValidationResponse response = documentProcessingService.processDocument(file);
        return ResponseEntity.ok(response);
    }
}
