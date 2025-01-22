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

        BranchResponseDto branchResponseDto = BranchResponseDto.builder()
                .idBranch(savedBranch.getIdBranch())
                .name(savedBranch.getName())
                .client(savedBranch.getClient().getIdClient())
                .build();

        return branchResponseDto;
    }

    @Override
    public Optional<BranchResponseDto> findOne(String id) {
        Optional<Branch> branchOptional = branchRepository.findById(id);

        Branch branch = branchOptional.orElseThrow(() -> new RuntimeException("Branch not found"));

        BranchResponseDto branchResponse = BranchResponseDto.builder()
                .idBranch(branch.getIdBranch())
                .name(branch.getName())
                .client(branch.getClient().getIdClient())
                .build();

        return Optional.of(branchResponse);
    }

    @Override
    public Page<BranchResponseDto> findAll(Pageable pageable) {
        Page<Branch> pageBranch = branchRepository.findAll(pageable);

        Page<BranchResponseDto> pageBranchResponse = pageBranch.map(
                branch -> BranchResponseDto.builder()
                        .idBranch(branch.getIdBranch())
                        .name(branch.getName())
                        .client(branch.getClient().getIdClient())
                        .build()
        );

        return pageBranchResponse;
    }

    @Override
    public Optional<BranchResponseDto> update(BranchRequestDto branchRequestDto) {
        Optional<Branch> branchOptional = branchRepository.findById(branchRequestDto.getIdBranch());

        Branch branch = branchOptional.orElseThrow(() -> new RuntimeException("Branch not found"));

        branch.setName(branchRequestDto.getName() != null ? branchRequestDto.getName() : branch.getName());
        branch.setIsActive(branchRequestDto.getIsActive() != null ? branchRequestDto.getIsActive() : branch.getIsActive());

        Branch savedBranch = branchRepository.save(branch);

        BranchResponseDto branchResponseDto = BranchResponseDto.builder()
                .idBranch(savedBranch.getIdBranch())
                .name(savedBranch.getName())
                .client(savedBranch.getClient().getIdClient())
                .build();

        return Optional.of(branchResponseDto);
    }

    @Override
    public void delete(String id) {
        branchRepository.deleteById(id);
    }

    @Override
    public Page<BranchResponseDto> findAllByClient(String idSearch, Pageable pageable) {
        Page<Branch> pageBranch = branchRepository.findAllByClient_IdClient(idSearch, pageable);

        Page<BranchResponseDto> pageBranchResponse = pageBranch.map(
                branch -> BranchResponseDto.builder()
                        .idBranch(branch.getIdBranch())
                        .name(branch.getName())
                        .client(branch.getClient().getIdClient())
                        .build()
        );

        return pageBranchResponse;
    }
}
