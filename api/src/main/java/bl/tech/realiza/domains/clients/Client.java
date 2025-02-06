package bl.tech.realiza.domains.clients;

import bl.tech.realiza.domains.contract.Activity;
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
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idClient;
    private String cnpj;
    private String tradeName;
    private String corporateName;
    private String logo;
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
    private Boolean deleteRequest = false;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
}
