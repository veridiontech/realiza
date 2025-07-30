package bl.tech.realiza.gateways.controllers.interfaces.clients;

import bl.tech.realiza.gateways.requests.clients.branch.BranchCreateRequestDto;
import bl.tech.realiza.gateways.responses.clients.branches.BranchNameResponseDto;
import bl.tech.realiza.gateways.responses.clients.branches.BranchResponseDto;
import bl.tech.realiza.gateways.responses.clients.controlPanel.ControlPanelResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface BranchControlller {
    ResponseEntity<BranchResponseDto> createBranch(BranchCreateRequestDto branchCreateRequestDto);
    ResponseEntity<Optional<BranchResponseDto>> getOneBranch(String id);
    ResponseEntity<Page<BranchResponseDto>> getAllBranches(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Page<BranchResponseDto>> getAllBranchesByCenter(int page, int size, String sort, Sort.Direction direction, String idCenter);
    ResponseEntity<Optional<BranchResponseDto>> updateBranch(String id, BranchCreateRequestDto branchCreateRequestDto);
    ResponseEntity<Void> deleteBranch(String id);
    ResponseEntity<Page<BranchResponseDto>> getAllBranchesByClient(int page, int size, String sort, Sort.Direction direction, String idSearch);
    ResponseEntity<Page<BranchResponseDto>> getAllBranchesByClientUnfiltered(int page, int size, String sort, Sort.Direction direction, String idSearch);
    ResponseEntity<ControlPanelResponseDto> getControlPanelSummarizedByBranch(String branchId);
    ResponseEntity<List<BranchNameResponseDto>> getAllBranchesByAccess(List<String> branchIds);
}
