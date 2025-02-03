package bl.tech.realiza.gateways.controllers.impl.services;

import bl.tech.realiza.gateways.controllers.interfaces.services.DocumentController;
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<String> extractTextDocument(@RequestPart(value = "file") MultipartFile multipartFile, @RequestParam DocumentProcessingService.DocType docType) {
        if (multipartFile.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        File file = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
        try {
            multipartFile.transferTo(file); // Convert MultipartFile to File
            String extractedText = documentProcessingService.processFile(file, docType);
            return ResponseEntity.ok(extractedText);
        } catch (IOException | TesseractException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file: " + e.getMessage());
        } finally {
            file.delete(); // Cleanup temporary file
        }
    }
}
