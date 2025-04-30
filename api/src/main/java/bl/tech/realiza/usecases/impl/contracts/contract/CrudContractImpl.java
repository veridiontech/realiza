package bl.tech.realiza.usecases.impl.contracts.contract;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.usecases.interfaces.contracts.contract.CrudContract;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrudContractImpl implements CrudContract {
    private final ContractRepository contractRepository;

    @Override
    public String finishContract(String idContract) {
        Contract contract = contractRepository.findById(idContract)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        contract.setFinished(true);

        contractRepository.save(contract);

        return "Contract finished successfully";
    }
}
