package bl.tech.realiza.gateways.responses.users.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProfileRepoResponseDto {
    private String id;
    private String name;
    private String description;
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
    private String clientId;
}
