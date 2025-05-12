package bl.tech.realiza.domains.clients;

import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogClient;
import bl.tech.realiza.domains.documents.client.DocumentClient;
import bl.tech.realiza.domains.ultragaz.Board;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idClient;
    private String cnpj;
    private String tradeName; // nome fantasia
    private String corporateName; // razão social
    private String logo;
    private String email;
    private String telephone;
    private String cep;
    private String state;
    private String city;
    private String address;
    @Builder.Default
    private Boolean isUltragaz = false;
    private String number;
    @Builder.Default
    private Boolean isActive = true;
    @Builder.Default
    private Boolean deleteRequest = false;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    // -------------------------------
    // Relacionamentos CONTRATUAIS
    // -------------------------------
    @JsonIgnore
    @OneToMany(mappedBy = "client", cascade = CascadeType.REMOVE)
    private List<Branch> branches;

    @JsonIgnore
    @OneToMany(mappedBy = "client", cascade = CascadeType.REMOVE)
    private List<Board> boards;

    @JsonIgnore
    @OneToMany(mappedBy = "client", cascade = CascadeType.REMOVE)
    private List<DocumentClient> documentClients;

    @JsonIgnore
    @OneToMany(mappedBy = "idRecord", cascade = CascadeType.REMOVE)
    private List<AuditLogClient> auditLogClients;
}
