package bl.tech.realiza.domains.services;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.enums.DocumentStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FileDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;

    private String contentType;
    private String url;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    @Builder.Default
    private DocumentStatusEnum status = DocumentStatusEnum.EM_ANALISE;
    private Boolean canBeOverwritten;
    @Builder.Default
    private Boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idDocumentation")
    private Document document;
}
