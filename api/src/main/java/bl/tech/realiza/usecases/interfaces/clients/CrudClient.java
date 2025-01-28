package bl.tech.realiza.usecases.interfaces.clients;

import bl.tech.realiza.gateways.requests.enterprises.EnterpriseAndUserRequestDto;
import bl.tech.realiza.gateways.requests.clients.ClientRequestDto;
import bl.tech.realiza.gateways.responses.enterprises.EnterpriseAndUserResponseDto;
import bl.tech.realiza.gateways.responses.clients.ClientResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface CrudClient {
    ClientResponseDto save(ClientRequestDto clientRequestDto, MultipartFile file) throws IOException;
    Optional<ClientResponseDto> findOne(String id);
    Page<ClientResponseDto> findAll(Pageable pageable);
    Optional<ClientResponseDto> update(String id, ClientRequestDto clientRequestDto);
    void delete(String id);
    EnterpriseAndUserResponseDto saveBoth(EnterpriseAndUserRequestDto enterpriseAndUserRequestDto);
    String changeLogo(String id, MultipartFile file) throws IOException;
}
