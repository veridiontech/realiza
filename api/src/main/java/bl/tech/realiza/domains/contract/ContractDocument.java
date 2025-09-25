package bl.tech.realiza.domains.contract;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.services.ItemManagement;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import static bl.tech.realiza.domains.documents.Document.Status.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ContractDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // 🔗 Relacionamento com Contract
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idContract")
    private Contract contract;

    // 🔗 Relacionamento com Document
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idDocumentation")
    private Document document;

    @Builder.Default
    private Document.Status status = PENDENTE;

    @JsonIgnore
    @OneToMany(mappedBy = "contractDocument", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ItemManagement> itemManagementList;
}
