package bl.tech.realiza.gateways.controllers.impl.contracts;

import bl.tech.realiza.gateways.controllers.interfaces.contracts.SubcontractorController;
import bl.tech.realiza.gateways.requests.contracts.ContractSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSubcontractorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class SubcontractorControllerImpl implements SubcontractorController {
    @Override
    public ResponseEntity<ContractSubcontractorResponseDto> createContractSubcontractor(ContractSubcontractorRequestDto contractSubcontractorRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<ContractSubcontractorResponseDto>> getOneContractSubcontractor(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Page<ContractSubcontractorResponseDto>> getAllSubcontractors(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<ContractSubcontractorResponseDto>> updateContractSubcontractor(ContractSubcontractorRequestDto contractSubcontractorRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteContractSubcontractor(String id) {
        return null;
    }
}
