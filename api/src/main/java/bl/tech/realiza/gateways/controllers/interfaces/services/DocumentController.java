package bl.tech.realiza.gateways.controllers.interfaces.services;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentController {
    ResponseEntity<String> extractTextDocument(MultipartFile file);
}
