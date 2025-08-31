package bl.tech.realiza.domains.user.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USER_PROFILE_REPO")
public class ProfileRepo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Builder.Default
    private String name = "default";
    @Builder.Default
    private String description = "default";
    @Builder.Default
    private Boolean admin = false; // admin - pode tudo da filial

    @ManyToMany
    @JoinTable(
            name = "PROFILE_REPO_PERMISSION",
            joinColumns = @JoinColumn(name = "profileRepoId"),
            inverseJoinColumns = @JoinColumn(name = "permissionId", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT))
    )
    private Set<Permission> permissions;
//    @Builder.Default
//    private Boolean viewer = true; // colaborador - pode só visualizar os serviços da filial
//    @Builder.Default
//    private Boolean manager = false; // gestor - visualiza e conclui / suspende contratos que for responsável
//    @Builder.Default
//    private Boolean inspector = false; // fiscal - visualiza contratos que for responsável
//    @Builder.Default
//    private Boolean documentViewer = false; // pode visualizar pdf do documento
//    @Builder.Default
//    private Boolean registrationUser = false; // pode cadastrar novos usuários
//    @Builder.Default
//    private Boolean registrationContract = false; // pode cadastrar novos contratos
//    @Builder.Default
//    private Boolean laboral = false; // trabalhista - visualiza todos os documentos trabalhista do contrato que for vinculado
//    @Builder.Default
//    private Boolean workplaceSafety = false; // segurança - visualiza todos os documentos segurança do contrato que for vinculado
//    @Builder.Default
//    private Boolean registrationAndCertificates = false; // ssma - visualiza todos os documentos ssma do contrato que for vinculado
//    @Builder.Default
//    private Boolean general = false; // geral - visualiza todos os documentos geral do contrato que for vinculado
//    @Builder.Default
//    private Boolean health = false; // saude - visualiza todos os documentos saude do contrato que for vinculado
//    @Builder.Default
//    private Boolean environment = false; // meio ambiente - visualiza todos os documentos meio ambiente do contrato que for vinculado
//    @Builder.Default
//    private Boolean concierge = false; // portaria - apenas acesso ao modulo de portaria
}
