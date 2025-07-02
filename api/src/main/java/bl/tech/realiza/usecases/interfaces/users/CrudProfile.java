package bl.tech.realiza.usecases.interfaces.users;

import bl.tech.realiza.gateways.requests.users.ProfileRequestDto;
import bl.tech.realiza.gateways.responses.users.ProfileNameResponseDto;
import bl.tech.realiza.gateways.responses.users.ProfileResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CrudProfile {
    ProfileResponseDto save(ProfileRequestDto profileRequestDto);
    ProfileResponseDto findOne(String id);
    List<ProfileResponseDto> findAll();
    List<ProfileNameResponseDto> findAllByClientId(String clientId);
    ProfileResponseDto update(String id, ProfileRequestDto profileRequestDto);
    void delete(String id);
}
