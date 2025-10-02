package bl.tech.realiza.gateways.responses.users.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProfileResponse {
    private String id;
    private String name;
    private String description;
    private boolean admin;
    private DashboardResponse dashboard;
    private DocumentResponse document;
    private ContractResponse contract;
    private boolean reception;

    @Getter
    @Setter
    @Builder
    @Jacksonized
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class DashboardResponse {
        private boolean general;
        private boolean provider;
        private boolean document;
        private boolean documentDetail;
    }

    @Getter
    @Setter
    @Builder
    @Jacksonized
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ContractResponse {
        private boolean finish;
        private boolean suspend;
        private boolean create;
    }

    @Getter
    @Setter
    @Builder
    @Jacksonized
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class DocumentResponse {
        private DocumentType view;
        private DocumentType upload;
        private DocumentType exempt;
    }

    @Getter
    @Setter
    @Builder
    @Jacksonized
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class DocumentType {
        private boolean laboral;
        private boolean workplaceSafety;
        private boolean registrationAndCertificates;
        private boolean general;
        private boolean health;
        private boolean environment;
    }

    private String clientId;
}
