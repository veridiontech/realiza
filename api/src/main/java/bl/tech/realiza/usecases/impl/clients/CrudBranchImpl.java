package bl.tech.realiza.usecases.impl.clients;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.exceptions.UnprocessableEntityException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.requests.clients.branch.BranchCreateRequestDto;
import bl.tech.realiza.gateways.responses.clients.BranchResponseDto;
import bl.tech.realiza.usecases.interfaces.clients.CrudBranch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CrudBranchImpl implements CrudBranch {

    private final BranchRepository branchRepository;
    private final ClientRepository clientRepository;
    private final DocumentBranchRepository documentBranchRepository;
    private final DocumentMatrixRepository documentMatrixRepository;

    @Override
    public BranchResponseDto save(BranchCreateRequestDto branchCreateRequestDto) {

        Optional<Client> clientOptional = clientRepository.findById(branchCreateRequestDto.getClient());
        Client client = clientOptional.orElseThrow(() -> new NotFoundException("Client not found"));

        Optional<Branch> branchOptional = branchRepository.findByCnpj(branchCreateRequestDto.getCnpj());
        if (branchOptional.isPresent()) {
            throw new UnprocessableEntityException("CNPJ already exists");
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
                .build();

        Branch savedBranch = branchRepository.save(newBranch);

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
                .build();

        return branchResponseDto;
    }

    @Override
    public Optional<BranchResponseDto> findOne(String id) {
        Optional<Branch> branchOptional = branchRepository.findById(id);

        Branch branch = branchOptional.orElseThrow(() -> new NotFoundException("Branch not found"));

        BranchResponseDto branchResponse = BranchResponseDto.builder()
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
                .build();

        return Optional.of(branchResponse);
    }

    @Override
    public Page<BranchResponseDto> findAll(Pageable pageable) {
        Page<Branch> pageBranch = branchRepository.findAllByIsActiveIsTrue(pageable);

        Page<BranchResponseDto> pageBranchResponse = pageBranch.map(
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
                        .build()
        );

        return pageBranchResponse;
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

        Page<BranchResponseDto> pageBranchResponse = pageBranch.map(
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
                        .build()
        );

        return pageBranchResponse;
    }
}
