package bl.tech.realiza.gateways.controllers.interfaces.services;

import bl.tech.realiza.gateways.responses.services.DocumentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DocumentController {
    ResponseEntity<DocumentResponse> validateDocument(MultipartFile file) throws IOException;
}
