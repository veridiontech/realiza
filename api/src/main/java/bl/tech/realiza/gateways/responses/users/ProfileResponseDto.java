package bl.tech.realiza.gateways.responses.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProfileResponseDto {
    private String id;
    private String name;
    private String description;
    private Boolean admin;
    private Boolean viewer;
    private Boolean manager;
    private Boolean inspector;
    private Boolean laboral;
    private Boolean workplaceSafety;
    private Boolean registrationAndCertificates;
    private Boolean general;
    private Boolean health;
    private Boolean environment;
    private Boolean concierge;
    private String clientId;
    private List<ProfileBranchResponseDto> branches;
    private List<ProfileContractResponseDto> contracts;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ProfileBranchResponseDto {
        private String id;
        private String name;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ProfileContractResponseDto {
        private String id;
        private String reference;
    }
}
