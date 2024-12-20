package bl.tech.realiza.usecases.impl.documents.client;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.requests.documents.client.DocumentBranchRequestDto;
import bl.tech.realiza.gateways.responses.documents.client.DocumentBranchResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.client.CrudDocumentBranch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudDocumentBranchImpl implements CrudDocumentBranch {

    private final DocumentBranchRepository documentBranchRepository;
    private final BranchRepository branchRepository;

    @Override
    public DocumentBranchResponseDto save(DocumentBranchRequestDto documentBranchRequestDto) {

        Optional<Branch> branchOptional = branchRepository.findById(documentBranchRequestDto.getBranch());

        Branch branch = branchOptional.orElseThrow(() -> new RuntimeException("Branch not found"));

        DocumentBranch newDocumentBranch = DocumentBranch.builder()
                .title(documentBranchRequestDto.getTitle())
                .risk(documentBranchRequestDto.getRisk())
                .status(documentBranchRequestDto.getStatus())
                .documentation(documentBranchRequestDto.getDocumentation())
                .creation_date(documentBranchRequestDto.getCreation_date())
                .branch(branch)
                .build();

        DocumentBranch savedDocumentBranch = documentBranchRepository.save(newDocumentBranch);

        DocumentBranchResponseDto documentBranchResponse = DocumentBranchResponseDto.builder()
                .id_documentation(savedDocumentBranch.getId_documentation())
                .title(savedDocumentBranch.getTitle())
                .risk(savedDocumentBranch.getRisk())
                .status(savedDocumentBranch.getStatus())
                .documentation(savedDocumentBranch.getDocumentation())
                .creation_date(savedDocumentBranch.getCreation_date())
                .branch(savedDocumentBranch.getBranch().getIdBranch())
                .build();

        return documentBranchResponse;
    }

    @Override
    public Optional<DocumentBranchResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<DocumentBranchResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<DocumentBranchResponseDto> update(DocumentBranchRequestDto documentBranchRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}
