package bl.tech.realiza.domains.clients;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idBranch;
    private String name;
    private String cnpj;
    private String email;
    private String telephone;
    private String cep;
    private String state;
    private String city;
    private String address;
    private String number;
    @Builder.Default
    private Boolean isActive = false;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToOne(cascade = CascadeType.REMOVE)
    private Client client;

    @ManyToMany
    @JoinTable(
            name = "BRANCH_DOCUMENT_MATRIX",
            joinColumns = @JoinColumn(name = "idBranch"),
            inverseJoinColumns = @JoinColumn(name = "idDocument")
    )
    private List<DocumentMatrix> documents;
}
