package bl.tech.realiza.gateways.requests.users.profile;

import lombok.Data;

import java.util.List;

@Data
public class ProfileRepoRequestDto {
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
}
