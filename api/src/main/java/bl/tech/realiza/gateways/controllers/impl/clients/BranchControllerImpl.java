package bl.tech.realiza.gateways.controllers.impl.clients;

import bl.tech.realiza.gateways.controllers.interfaces.clients.BranchController;
import bl.tech.realiza.gateways.requests.clients.BranchRequestDto;
import bl.tech.realiza.gateways.responses.clients.BranchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class BranchControllerImpl implements BranchController {
    @Override
    public ResponseEntity<BranchResponseDto> createBranch(BranchRequestDto branchRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<BranchResponseDto>> getOneBranch(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Page<BranchResponseDto>> getAllBranches(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<BranchResponseDto>> updateBranch(BranchRequestDto branchRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteBranch(String id) {
        return null;
    }
}
