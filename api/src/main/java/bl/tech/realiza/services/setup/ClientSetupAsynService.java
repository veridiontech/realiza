package bl.tech.realiza.services.setup;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.gateways.requests.clients.branch.BranchCreateRequestDto;
import bl.tech.realiza.gateways.requests.clients.client.ClientRequestDto;
import bl.tech.realiza.usecases.interfaces.clients.CrudBranch;
import bl.tech.realiza.usecases.interfaces.contracts.CrudServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientSetupAsynService {

    private final CrudServiceType crudServiceType;
    private final CrudBranch crudBranch;

    @Async
    public void setupClient(Client savedClient) {
        crudServiceType.transferFromRepoToClient(savedClient.getIdClient());

        crudBranch.save(
                BranchCreateRequestDto.builder()
                        .name(savedClient.getCorporateName() + " Base")
                        .cnpj(savedClient.getCnpj())
                        .cep(savedClient.getCep())
                        .state(savedClient.getState())
                        .city(savedClient.getCity())
                        .email(savedClient.getEmail())
                        .telephone(savedClient.getTelephone())
                        .address(savedClient.getAddress())
                        .number(savedClient.getNumber())
                        .client(savedClient.getIdClient())
                        .build()
        );
    }
}
