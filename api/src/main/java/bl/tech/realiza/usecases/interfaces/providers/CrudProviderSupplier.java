package bl.tech.realiza.usecases.interfaces.providers;

import bl.tech.realiza.gateways.requests.providers.ProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.requests.providers.ProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CrudProviderSupplier {
    ProviderResponseDto save(ProviderSupplierRequestDto providerSupplierRequestDto, MultipartFile file) throws IOException;
    Optional<ProviderResponseDto> findOne(String id);
    Page<ProviderResponseDto> findAll(Pageable pageable);
    Optional<ProviderResponseDto> update(String id, ProviderSupplierRequestDto providerSupplierRequestDto);
    void delete(String id);
    Page<ProviderResponseDto> findAllByClient(String idSearch, Pageable pageable);
    String changeLogo(String id, MultipartFile file) throws IOException;
    String addBranch(String providerId, List<String> idBranch);
}
