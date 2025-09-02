package bl.tech.realiza.gateways.responses.users.profile;

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
