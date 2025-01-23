package bl.tech.realiza.domains.services;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "documents")
public class FileDocument {
    @Id
    private ObjectId _id;
    private String name;

    private String contentType;
    private byte[] data;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    // Retorna o ID como String para compatibilidade
    public String getIdDocumentAsString() {
        return _id != null ? _id.toHexString() : null;
    }
}
