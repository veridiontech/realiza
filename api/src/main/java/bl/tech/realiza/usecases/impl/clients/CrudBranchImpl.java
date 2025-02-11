package bl.tech.realiza.usecases.impl.clients;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.exceptions.UnprocessableEntityException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.requests.clients.branch.BranchRequestDto;
import bl.tech.realiza.gateways.responses.clients.BranchResponseDto;
import bl.tech.realiza.usecases.interfaces.clients.CrudBranch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CrudBranchImpl implements CrudBranch {

    private final BranchRepository branchRepository;
    private final ClientRepository clientRepository;
    private final DocumentMatrixRepository documentMatrixRepository;

    @Override
    public BranchResponseDto save(BranchRequestDto branchRequestDto) {

        Optional<Client> clientOptional = clientRepository.findById(branchRequestDto.getClient());
        Client client = clientOptional.orElseThrow(() -> new NotFoundException("Client not found"));

        Optional<Branch> branchOptional = branchRepository.findByCnpj(branchRequestDto.getCnpj());
        if (branchOptional.isPresent()) {
            throw new UnprocessableEntityException("CNPJ already exists");
        }

        Branch newBranch = Branch.builder()
                .name(branchRequestDto.getName())
                .cnpj(branchRequestDto.getCnpj())
                .cep(branchRequestDto.getCep())
                .state(branchRequestDto.getState())
                .city(branchRequestDto.getCity())
                .email(branchRequestDto.getEmail())
                .telephone(branchRequestDto.getTelephone())
                .address(branchRequestDto.getAddress())
                .number(branchRequestDto.getNumber())
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
        Page<Branch> pageBranch = branchRepository.findAll(pageable);

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
    public Optional<BranchResponseDto> update(String id, BranchRequestDto branchRequestDto) {
        Optional<Branch> branchOptional = branchRepository.findById(id);

        Branch branch = branchOptional.orElseThrow(() -> new NotFoundException("Branch not found"));

        branch.setName(branchRequestDto.getName() != null ? branchRequestDto.getName() : branch.getName());
        branch.setCnpj(branchRequestDto.getCnpj() != null ? branchRequestDto.getCnpj() : branch.getCnpj());
        branch.setCep(branchRequestDto.getCep() != null ? branchRequestDto.getCep() : branch.getCep());
        branch.setState(branchRequestDto.getState() != null ? branchRequestDto.getState() : branch.getState());
        branch.setCity(branchRequestDto.getCity() != null ? branchRequestDto.getCity() : branch.getCity());
        branch.setEmail(branchRequestDto.getEmail() != null ? branchRequestDto.getEmail() : branch.getEmail());
        branch.setTelephone(branchRequestDto.getTelephone() != null ? branchRequestDto.getTelephone() : branch.getTelephone());
        branch.setAddress(branchRequestDto.getAddress() != null ? branchRequestDto.getAddress() : branch.getAddress());
        branch.setNumber(branchRequestDto.getNumber() != null ? branchRequestDto.getNumber() : branch.getNumber());


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
        Page<Branch> pageBranch = branchRepository.findAllByClient_IdClient(idSearch, pageable);

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
