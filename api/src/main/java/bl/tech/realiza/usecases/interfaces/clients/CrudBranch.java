package bl.tech.realiza.usecases.interfaces.clients;

import bl.tech.realiza.gateways.requests.clients.BranchRequestDto;
import bl.tech.realiza.gateways.responses.clients.BranchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudBranch {
    BranchResponseDto save(BranchRequestDto branchRequestDto);
    Optional<BranchResponseDto> findOne(String id);
    Page<BranchResponseDto> findAll(Pageable pageable);
    Optional<BranchResponseDto> update(String id, BranchRequestDto branchRequestDto);
    void delete(String id);
    Page<BranchResponseDto> findAllByClient(String idSearch, Pageable pageable);
}
