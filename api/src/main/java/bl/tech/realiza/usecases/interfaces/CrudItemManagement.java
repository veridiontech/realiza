package bl.tech.realiza.usecases.interfaces;

import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementProviderRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementUserRequestDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.ItemManagementProviderResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.ItemManagementUserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudItemManagement {
    ItemManagementUserResponseDto saveUserSolicitation(ItemManagementUserRequestDto itemManagementUserRequestDto);
    ItemManagementProviderResponseDto saveProviderSolicitation(ItemManagementProviderRequestDto itemManagementProviderRequestDto);
    Page<ItemManagementUserResponseDto> findAllUserSolicitation(Pageable pageable);
    Page<ItemManagementProviderResponseDto> findAllProviderSolicitation(Pageable pageable);
    void deleteSolicitation(String id);
    String approveSolicitation(String id);
    String denySolicitation(String id);

}
