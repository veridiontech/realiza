package bl.tech.realiza.usecases.interfaces.documents.contract;

import bl.tech.realiza.gateways.responses.documents.ContractDocumentAndEmployeeResponseDto;

import java.util.List;

public interface CrudDocumentContract {
    ContractDocumentAndEmployeeResponseDto getDocumentAndEmployeeByContractId(String id);
}
