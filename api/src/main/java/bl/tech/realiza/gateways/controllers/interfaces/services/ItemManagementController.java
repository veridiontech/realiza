package bl.tech.realiza.gateways.controllers.interfaces.services;

import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementProviderRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementUserRequestDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.ItemManagementProviderResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.ItemManagementUserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

public interface ItemManagementController {
    ResponseEntity<ItemManagementUserResponseDto> createUserSolicitation(ItemManagementUserRequestDto itemManagementUserRequestDto);
    ResponseEntity<ItemManagementProviderResponseDto> createProviderSolicitation(ItemManagementProviderRequestDto itemManagementProviderRequestDto);
    ResponseEntity<Page<ItemManagementUserResponseDto>> getUserSolicitations(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Page<ItemManagementProviderResponseDto>> getProviderSolicitations(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Void> deleteSolicitation(String id);
    ResponseEntity<String> approveSolicitation(String id);
    ResponseEntity<String> denySolicitation(String id);
}
