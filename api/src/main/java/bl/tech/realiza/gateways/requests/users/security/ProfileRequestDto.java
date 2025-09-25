package bl.tech.realiza.gateways.requests.users.security;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequestDto {
    private String name;
    private String description;
    private Boolean admin;
    private DashboardRequest dashboard;
    private DocumentRequest document;
    private ContractRequest contract;
    private Boolean reception;

    @Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
    public static class DashboardRequest {
        private Boolean general;
        private Boolean provider;
        private Boolean document;
        private Boolean documentDetail;
    }

    @Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
    public static class ContractRequest {
        private Boolean finish;
        private Boolean suspend;
        private Boolean create;
    }

    @Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
    public static class DocumentRequest {
        private DocumentType view;
        private DocumentType upload;
        private DocumentType exempt;
    }

    @Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
    public static class DocumentType {
        private Boolean laboral;
        private Boolean workplaceSafety;
        private Boolean registrationAndCertificates;
        private Boolean general;
        private Boolean health;
        private Boolean environment;
    }
    private String clientId;
    private List<String> branchIds;
    private List<String> contractIds;
}
