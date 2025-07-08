package bl.tech.realiza.gateways.requests.users;

import lombok.Data;

import java.util.List;

@Data
public class ProfileRequestDto {
    private String name;
    private String description;
    private Boolean admin;
    private Boolean viewer;
    private Boolean manager;
    private Boolean inspector;
    private Boolean documentViewer;
    private Boolean registration;
    private Boolean laboral;
    private Boolean workplaceSafety;
    private Boolean registrationAndCertificates;
    private Boolean general;
    private Boolean health;
    private Boolean environment;
    private Boolean concierge;
    private String clientId;
    private List<String> branchIds;
    private List<String> contractIds;
}
