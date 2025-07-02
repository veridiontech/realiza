package bl.tech.realiza.gateways.controllers.interfaces.users;

import bl.tech.realiza.gateways.requests.users.ProfileRequestDto;
import bl.tech.realiza.gateways.responses.users.ProfileNameResponseDto;
import bl.tech.realiza.gateways.responses.users.ProfileResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProfileController {
    ResponseEntity<ProfileResponseDto> createProfile(ProfileRequestDto profileRequestDto);
    ResponseEntity<ProfileResponseDto> getOneProfile(String id);
    ResponseEntity<List<ProfileResponseDto>> getAllProfiles();
    ResponseEntity<List<ProfileNameResponseDto>> getAllProfileNamesByClientId(String clientId);
    ResponseEntity<ProfileResponseDto> updateProfile(String id, ProfileRequestDto profileRequestDto);
    ResponseEntity<Void> deleteProfile(String id);
}
