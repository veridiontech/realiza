package bl.tech.realiza.usecases.interfaces.users.profile;

import bl.tech.realiza.gateways.requests.users.profile.ProfileRepoRequestDto;
import bl.tech.realiza.gateways.responses.users.profile.ProfileRepoResponseDto;

import java.util.List;

public interface CrudProfileRepo {
    ProfileRepoResponseDto save(ProfileRepoRequestDto profileRequestDto);
    ProfileRepoResponseDto findOne(String id);
    List<ProfileRepoResponseDto> findAll();
    ProfileRepoResponseDto update(String id, ProfileRepoRequestDto profileRequestDto);
    void delete(String id);

    Boolean checkIfExistsByName(String name);
}
