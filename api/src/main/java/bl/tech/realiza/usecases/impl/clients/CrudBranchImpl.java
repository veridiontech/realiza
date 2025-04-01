package bl.tech.realiza.usecases.impl.clients;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.ultragaz.Center;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.ultragaz.CenterRepository;
import bl.tech.realiza.gateways.requests.clients.branch.BranchCreateRequestDto;
import bl.tech.realiza.gateways.responses.clients.BranchResponseDto;
import bl.tech.realiza.usecases.impl.contracts.CrudActivityImpl;
import bl.tech.realiza.usecases.interfaces.clients.CrudBranch;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CrudBranchImpl implements CrudBranch {

    private final BranchRepository branchRepository;
    private final ClientRepository clientRepository;
    private final CrudActivityImpl crudActivity;
    private final CenterRepository centerRepository;

    @Override
    public BranchResponseDto save(BranchCreateRequestDto branchCreateRequestDto) {
        Center center = null;

        Client client = clientRepository.findById(branchCreateRequestDto.getClient())
                    .orElseThrow(() -> new NotFoundException("Client not found"));

        if (branchCreateRequestDto.getCenter() != null && !branchCreateRequestDto.getCenter().isBlank()) {
            center = centerRepository.findById(branchCreateRequestDto.getCenter())
                    .orElseThrow(() -> new NotFoundException("Center not found"));
        }

        Branch newBranch = Branch.builder()
                .name(branchCreateRequestDto.getName())
                .cnpj(branchCreateRequestDto.getCnpj())
                .cep(branchCreateRequestDto.getCep())
                .state(branchCreateRequestDto.getState())
                .city(branchCreateRequestDto.getCity())
                .email(branchCreateRequestDto.getEmail())
                .telephone(branchCreateRequestDto.getTelephone())
                .address(branchCreateRequestDto.getAddress())
                .number(branchCreateRequestDto.getNumber())
                .client(client)
                .center(center)
                .build();

        Branch savedBranch = branchRepository.save(newBranch);

        crudActivity.transferFromRepo(savedBranch.getIdBranch());

        return BranchResponseDto.builder()
                .idBranch(savedBranch.getIdBranch())
                .name(savedBranch.getName())
                .cnpj(savedBranch.getCnpj())
                .cep(savedBranch.getCep())
                .state(savedBranch.getState())
                .city(savedBranch.getCity())
                .email(savedBranch.getEmail())
                .telephone(savedBranch.getTelephone())
                .address(savedBranch.getAddress())
                .number(savedBranch.getNumber())
                .client(savedBranch.getClient().getIdClient())
                .center(savedBranch.getCenter().getIdCenter())
                .build();
    }

    @Override
    public Optional<BranchResponseDto> findOne(String id) {
        Optional<Branch> branchOptional = branchRepository.findById(id);

        Branch branch = branchOptional.orElseThrow(() -> new NotFoundException("Branch not found"));

        return getBranchResponseDto(branch);
    }

    @Override
    public Page<BranchResponseDto> findAll(Pageable pageable) {
        Page<Branch> pageBranch = branchRepository.findAllByIsActiveIsTrue(pageable);

        return getBranchResponseDtos(pageBranch);
    }

    @Override
    public Page<BranchResponseDto> findAllByCenter(String idCenter, Pageable pageable) {
        return getBranchResponseDtos(branchRepository.findAllByCenter_IdCenter(idCenter, pageable));
    }

    @NotNull
    private Page<BranchResponseDto> getBranchResponseDtos(Page<Branch> pageBranch) {

        return pageBranch.map(
                branch -> BranchResponseDto.builder()
                        .idBranch(branch.getIdBranch())
                        .name(branch.getName())
                        .cnpj(branch.getCnpj())
                        .cep(branch.getCep())
                        .state(branch.getState())
                        .city(branch.getCity())
                        .email(branch.getEmail())
                        .telephone(branch.getTelephone())
                        .address(branch.getAddress())
                        .number(branch.getNumber())
                        .client(branch.getClient().getIdClient())
                        .center(branch.getCenter().getIdCenter())
                        .build()
        );
    }

    @Override
    public Optional<BranchResponseDto> update(String id, BranchCreateRequestDto branchCreateRequestDto) {
        Optional<Branch> branchOptional = branchRepository.findById(id);

        Branch branch = branchOptional.orElseThrow(() -> new NotFoundException("Branch not found"));

        branch.setName(branchCreateRequestDto.getName() != null ? branchCreateRequestDto.getName() : branch.getName());
        branch.setCnpj(branchCreateRequestDto.getCnpj() != null ? branchCreateRequestDto.getCnpj() : branch.getCnpj());
        branch.setCep(branchCreateRequestDto.getCep() != null ? branchCreateRequestDto.getCep() : branch.getCep());
        branch.setState(branchCreateRequestDto.getState() != null ? branchCreateRequestDto.getState() : branch.getState());
        branch.setCity(branchCreateRequestDto.getCity() != null ? branchCreateRequestDto.getCity() : branch.getCity());
        branch.setEmail(branchCreateRequestDto.getEmail() != null ? branchCreateRequestDto.getEmail() : branch.getEmail());
        branch.setTelephone(branchCreateRequestDto.getTelephone() != null ? branchCreateRequestDto.getTelephone() : branch.getTelephone());
        branch.setAddress(branchCreateRequestDto.getAddress() != null ? branchCreateRequestDto.getAddress() : branch.getAddress());
        branch.setNumber(branchCreateRequestDto.getNumber() != null ? branchCreateRequestDto.getNumber() : branch.getNumber());


        Branch savedBranch = branchRepository.save(branch);

        return getBranchResponseDto(savedBranch);
    }

    @NotNull
    private Optional<BranchResponseDto> getBranchResponseDto(Branch savedBranch) {
        BranchResponseDto branchResponseDto = BranchResponseDto.builder()
                .idBranch(savedBranch.getIdBranch())
                .name(savedBranch.getName())
                .cnpj(savedBranch.getCnpj())
                .cep(savedBranch.getCep())
                .state(savedBranch.getState())
                .city(savedBranch.getCity())
                .email(savedBranch.getEmail())
                .telephone(savedBranch.getTelephone())
                .address(savedBranch.getAddress())
                .number(savedBranch.getNumber())
                .client(savedBranch.getClient().getIdClient())
                .center(savedBranch.getCenter().getIdCenter())
                .build();

        return Optional.of(branchResponseDto);
    }

    @Override
    public void delete(String id) {
        branchRepository.deleteById(id);
    }

    @Override
    public Page<BranchResponseDto> findAllByClient(String idSearch, Pageable pageable) {
        Page<Branch> pageBranch = branchRepository.findAllByClient_IdClientAndIsActiveIsTrue(idSearch, pageable);

        return getBranchResponseDtos(pageBranch);
    }
}
