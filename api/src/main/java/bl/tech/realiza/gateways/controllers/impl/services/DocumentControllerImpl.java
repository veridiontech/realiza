package bl.tech.realiza.gateways.controllers.impl.services;

import bl.tech.realiza.gateways.controllers.interfaces.services.DocumentController;
import bl.tech.realiza.gateways.responses.services.DocumentResponse;
import bl.tech.realiza.services.documentProcessing.DocumentProcessingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    public ResponseEntity<DocumentResponse> validateDocument(@RequestParam("file") MultipartFile file) throws IOException {
        DocumentResponse response = documentProcessingService.processDocument(file);
        return ResponseEntity.ok(response);
    }
}
