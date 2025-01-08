package bl.tech.realiza.usecases.impl.clients;

import bl.tech.realiza.gateways.requests.clients.BranchRequestDto;
import bl.tech.realiza.gateways.responses.clients.BranchResponseDto;
import bl.tech.realiza.usecases.interfaces.clients.CrudBranch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class CrudBranchImpl implements CrudBranch {
    @Override
    public BranchResponseDto save(BranchRequestDto branchRequestDto) {
        return null;
    }

    @Override
    public Optional<BranchResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<BranchResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<BranchResponseDto> update(BranchRequestDto branchRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}
