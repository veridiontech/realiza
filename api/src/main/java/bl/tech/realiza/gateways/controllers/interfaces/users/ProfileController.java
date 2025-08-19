package bl.tech.realiza.gateways.controllers.interfaces.users;

import bl.tech.realiza.gateways.requests.users.profile.ProfileRepoRequestDto;
import bl.tech.realiza.gateways.requests.users.profile.ProfileRequestDto;
import bl.tech.realiza.gateways.responses.users.profile.ProfileNameResponseDto;
import bl.tech.realiza.gateways.responses.users.profile.ProfileRepoResponseDto;
import bl.tech.realiza.gateways.responses.users.profile.ProfileResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProfileController {
    // repo
    ResponseEntity<ProfileRepoResponseDto> createProfileRepo(ProfileRepoRequestDto profileRequestDto);
    ResponseEntity<ProfileRepoResponseDto> getOneProfileRepo(String id);
    ResponseEntity<List<ProfileRepoResponseDto>> getAllProfilesRepo();
    ResponseEntity<ProfileRepoResponseDto> updateProfileRepo(String id, ProfileRepoRequestDto profileRequestDto);
    ResponseEntity<Void> deleteProfileRepo(String id);
    ResponseEntity<Boolean> checkIfExistsByNameProfile(String name);

    // client related
    ResponseEntity<ProfileResponseDto> createProfile(ProfileRequestDto profileRequestDto);
    ResponseEntity<ProfileResponseDto> getOneProfile(String id);
    ResponseEntity<List<ProfileResponseDto>> getAllProfiles();
    ResponseEntity<List<ProfileNameResponseDto>> getAllProfileNamesByClientId(String clientId);
    ResponseEntity<ProfileResponseDto> updateProfile(String id, ProfileRequestDto profileRequestDto);
    ResponseEntity<Void> deleteProfile(String id);
}
