package bl.tech.realiza.usecases.interfaces.users.security;

import bl.tech.realiza.gateways.requests.users.security.ProfileRequestDto;
import bl.tech.realiza.gateways.responses.users.profile.ProfileNameResponseDto;
import bl.tech.realiza.gateways.responses.users.profile.ProfileResponseDto;

import java.util.List;

public interface CrudProfile {
    ProfileResponseDto save(ProfileRequestDto profileRequestDto);
    ProfileResponseDto findOne(String id);
    List<ProfileResponseDto> findAll();
    List<ProfileNameResponseDto> findAllByClientId(String clientId);
    ProfileResponseDto update(String id, ProfileRequestDto profileRequestDto);
    void delete(String id);

    void transferFromRepoToClient(String idClient);
}
