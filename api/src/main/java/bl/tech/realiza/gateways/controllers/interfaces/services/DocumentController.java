package bl.tech.realiza.gateways.controllers.interfaces.services;

import bl.tech.realiza.services.documentProcessing.DocumentProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface DocumentController {
    ResponseEntity<String> extractTextDocument(MultipartFile file, DocumentProcessingService.DocType docType);
}
