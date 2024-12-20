package bl.tech.realiza.usecases.impl.clients;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.requests.clients.BranchRequestDto;
import bl.tech.realiza.gateways.responses.clients.BranchResponseDto;
import bl.tech.realiza.usecases.interfaces.clients.CrudBranch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudBranchImpl implements CrudBranch {

    private final BranchRepository branchRepository;
    private final ClientRepository clientRepository;

    @Override
    public BranchResponseDto save(BranchRequestDto branchRequestDto) {

        Optional<Client> clientOptional = clientRepository.findById(branchRequestDto.getClient());

        Client client = clientOptional.orElseThrow(() -> new RuntimeException("Client not found"));

        Branch newBranch = Branch.builder()
                .name(branchRequestDto.getName())
                .client(client)
                .build();

        Branch savedBranch = branchRepository.save(newBranch);

        BranchResponseDto branchResponse = BranchResponseDto.builder()
                .idBranch(savedBranch.getIdBranch())
                .name(savedBranch.getName())
                .client(savedBranch.getClient().getIdClient())
                .build();

        return branchResponse;
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
