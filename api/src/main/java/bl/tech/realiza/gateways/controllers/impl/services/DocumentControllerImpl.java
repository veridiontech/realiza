package bl.tech.realiza.gateways.controllers.impl.services;

import bl.tech.realiza.gateways.controllers.interfaces.services.DocumentController;
import bl.tech.realiza.services.documentProcessing.DocumentProcessingService;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class DocumentControllerImpl implements DocumentController {

    private final DocumentProcessingService documentProcessingService;

    @Override
    public ResponseEntity<String> extractTextDocument(@RequestPart(value = "file", required = false) MultipartFile file) {
        String extractedText;
        try {
            extractedText = documentProcessingService.extractText(file);
        } catch (IOException | TesseractException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(extractedText);
    }
}
