package bl.tech.realiza.gateways.responses.users;

import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.gateways.responses.clients.branches.BranchResponseDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserResponseDto {
    // user
    private String idUser;
    private String cpf;
    private String description;
    private String password;
    private String position;
    private User.Role role;
    private String firstName;
    private String surname;
    private String profilePictureSignedUrl;
    private String email;
    private String telephone;
    private String cellphone;
    private List<String> branches;


    private List<String> branchAccess;
    private List<String> contractAccess;
    private Boolean admin;
    private Boolean viewer;
    private Boolean manager;
    private Boolean inspector;
    private Boolean documentViewer;
    private Boolean registrationUser;
    private Boolean registrationContract;
    private Boolean laboral;
    private Boolean workplaceSafety;
    private Boolean registrationAndCertificates;
    private Boolean general;
    private Boolean health;
    private Boolean environment;
    private Boolean concierge;

    // client
    private String branch;
    private String idClient;
    private BranchResponseDto branchResponse;
    private String tradeName;
    private String corporateName;

    // subcontractor
    private String subcontractor;

    // supplier
    private String supplier;
    private ProviderResponseDto providerResponseDto;
}
