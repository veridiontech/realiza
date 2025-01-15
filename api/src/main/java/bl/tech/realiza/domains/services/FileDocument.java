package bl.tech.realiza.domains.services;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "documents")
public class FileDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idDocument;
    private String name;

    private String contentType;
    private byte[] data;
}
