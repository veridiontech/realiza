package bl.tech.realiza.gateways.controllers.interfaces.services;

import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementProviderRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementUserRequestDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.provider.ItemManagementProviderDetailsResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.provider.ItemManagementProviderResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.user.ItemManagementUserDetailsResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.user.ItemManagementUserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

public interface ItemManagementController {
    // user
    ResponseEntity<ItemManagementUserResponseDto> createUserSolicitation(ItemManagementUserRequestDto itemManagementUserRequestDto);
    ResponseEntity<Page<ItemManagementUserResponseDto>> getUserSolicitations(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<ItemManagementUserDetailsResponseDto> getUserSolicitationDetails(String idSolicitation);

    // provider
    ResponseEntity<ItemManagementProviderResponseDto> createProviderSolicitation(ItemManagementProviderRequestDto itemManagementProviderRequestDto);
    ResponseEntity<Page<ItemManagementProviderResponseDto>> getProviderSolicitations(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<ItemManagementProviderDetailsResponseDto> getProviderSolicitationDetails(String idSolicitation);

    ResponseEntity<String> approveSolicitation(String idSolicitation);
    ResponseEntity<String> denySolicitation(String idSolicitation);

    ResponseEntity<Void> deleteSolicitation(String idSolicitation);
}
