package bl.tech.realiza.usecases.interfaces.clients;

import bl.tech.realiza.gateways.requests.clients.branch.BranchCreateRequestDto;
import bl.tech.realiza.gateways.responses.clients.branches.BranchNameResponseDto;
import bl.tech.realiza.gateways.responses.clients.branches.BranchResponseDto;
import bl.tech.realiza.gateways.responses.clients.controlPanel.ControlPanelResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CrudBranch {
    BranchResponseDto save(BranchCreateRequestDto branchCreateRequestDto);
    Optional<BranchResponseDto> findOne(String id);
    Page<BranchResponseDto> findAll(Pageable pageable);
    Page<BranchResponseDto> findAllByCenter(String idCenter, Pageable pageable);
    Optional<BranchResponseDto> update(String id, BranchCreateRequestDto branchCreateRequestDto);
    void delete(String id);
    Page<BranchResponseDto> findAllByClient(String idSearch, Pageable pageable);
    Page<BranchResponseDto> findAllByClientUnfiltered(String idSearch, Pageable pageable);
    ControlPanelResponseDto findControlPanelSummary(String branchId);
    List<BranchNameResponseDto> findAllNameByBranchAccess(List<String> branchIds);

    void setupBranch(String branchId, List<String> activityIds);
}
