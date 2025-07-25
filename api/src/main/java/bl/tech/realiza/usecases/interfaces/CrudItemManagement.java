package bl.tech.realiza.usecases.interfaces;

import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementContractRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementDocumentRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementProviderRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementUserRequestDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.contract.ItemManagementContractDetailsResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.contract.ItemManagementContractResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.document.ItemManagementDocumentDetailsResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.document.ItemManagementDocumentResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.provider.ItemManagementProviderDetailsResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.provider.ItemManagementProviderResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.user.ItemManagementUserDetailsResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.user.ItemManagementUserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface CrudItemManagement {
    // user
    ItemManagementUserResponseDto saveUserSolicitation(ItemManagementUserRequestDto itemManagementUserRequestDto);
    Page<ItemManagementUserResponseDto> findAllUserSolicitation(Pageable pageable);
    ItemManagementUserDetailsResponseDto findUserSolicitationDetails(String idSolicitation);

    // provider
    ItemManagementProviderResponseDto saveProviderSolicitation(ItemManagementProviderRequestDto itemManagementProviderRequestDto);
    Page<ItemManagementProviderResponseDto> findAllProviderSolicitation(Pageable pageable);
    ItemManagementProviderDetailsResponseDto findProviderSolicitationDetails(String idSolicitation);

    String approveSolicitation(String id);
    String denySolicitation(String id);

    void deleteSolicitation(String id);

    // document
    ItemManagementDocumentResponseDto saveDocumentSolicitation(ItemManagementDocumentRequestDto requestDto);
    Page<ItemManagementDocumentResponseDto> findAllDocumentSolicitation(Pageable pageable);
    ItemManagementDocumentDetailsResponseDto findDocumentSolicitationDetails(String idSolicitation);

    // contract
    ItemManagementContractResponseDto saveContractSolicitation(ItemManagementContractRequestDto requestDto);
    Page<ItemManagementContractResponseDto> findAllContractSolicitation(Pageable pageable);
    ItemManagementContractDetailsResponseDto findContractSolicitationDetails(String idSolicitation);
}
