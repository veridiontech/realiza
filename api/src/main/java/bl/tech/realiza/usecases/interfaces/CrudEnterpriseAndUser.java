package bl.tech.realiza.usecases.interfaces;

import bl.tech.realiza.gateways.requests.services.EnterpriseAndUserRequestDto;
import bl.tech.realiza.gateways.requests.users.UserClientRequestDto;
import bl.tech.realiza.gateways.responses.services.EnterpriseAndUserResponseDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;

public interface CrudEnterpriseAndUser {
    EnterpriseAndUserResponseDto saveBothClient(EnterpriseAndUserRequestDto enterpriseAndUserRequestDto);
    EnterpriseAndUserResponseDto saveBothSupplier(EnterpriseAndUserRequestDto enterpriseAndUserRequestDto);
    EnterpriseAndUserResponseDto saveBothSubcontractor(EnterpriseAndUserRequestDto enterpriseAndUserRequestDto);
}
