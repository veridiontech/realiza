package bl.tech.realiza.gateways.controllers.interfaces.clients;

import bl.tech.realiza.gateways.requests.clients.BranchRequestDto;
import bl.tech.realiza.gateways.responses.clients.BranchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface BranchController {
    ResponseEntity<BranchResponseDto> createBranch(BranchRequestDto branchRequestDto);
    ResponseEntity<Optional<BranchResponseDto>> getOneBranch(String id);
    ResponseEntity<Page<BranchResponseDto>> getAllBranches(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<BranchResponseDto>> updateBranch(BranchRequestDto branchRequestDto);
    ResponseEntity<Void> deleteBranch(String id);
}
