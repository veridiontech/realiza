package bl.tech.realiza.usecases.interfaces;

import bl.tech.realiza.gateways.requests.services.ItemManagementRequestDto;
import bl.tech.realiza.gateways.responses.services.ItemManagementResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudItemManagement {
    ItemManagementResponseDto saveUserSolicitation(ItemManagementRequestDto itemManagementRequestDto);
    Page<ItemManagementResponseDto> findAllUserSolicitation(Pageable pageable);
    void deleteUserSolicitation(String id);
    String approveUserSolicitation(String id);
    String denyUserSolicitation(String id);
}
