package bl.tech.realiza.usecases.impl.documents.client;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.requests.documents.client.DocumentBranchRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
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
    public DocumentResponseDto save(DocumentBranchRequestDto documentBranchRequestDto) {
        Optional<Branch> branchOptional = branchRepository.findById(documentBranchRequestDto.getBranch());

        Branch branch = branchOptional.orElseThrow(() -> new RuntimeException("Branch not found"));

        DocumentBranch newDocumentBranch = DocumentBranch.builder()
                .title(documentBranchRequestDto.getTitle())
                .risk(documentBranchRequestDto.getRisk())
                .status(documentBranchRequestDto.getStatus())
                .documentation(documentBranchRequestDto.getDocumentation())
                .creationDate(documentBranchRequestDto.getCreationDate())
                .branch(branch)
                .build();

        DocumentBranch savedDocumentBranch = documentBranchRepository.save(newDocumentBranch);

        DocumentResponseDto documentBranchResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentBranch.getIdDocumentation())
                .title(savedDocumentBranch.getTitle())
                .risk(savedDocumentBranch.getRisk())
                .status(savedDocumentBranch.getStatus())
                .documentation(savedDocumentBranch.getDocumentation())
                .creationDate(savedDocumentBranch.getCreationDate())
                .branch(savedDocumentBranch.getBranch().getIdBranch())
                .build();

        return documentBranchResponse;
    }

    @Override
    public Optional<DocumentResponseDto> findOne(String id) {
        Optional<DocumentBranch> documentBranchOptional = documentBranchRepository.findById(id);

        DocumentBranch documentBranch = documentBranchOptional.orElseThrow(() -> new RuntimeException("DocumentBranch not found"));

        DocumentResponseDto documentBranchResponse = DocumentResponseDto.builder()
                .idDocumentation(documentBranch.getIdDocumentation())
                .title(documentBranch.getTitle())
                .risk(documentBranch.getRisk())
                .status(documentBranch.getStatus())
                .documentation(documentBranch.getDocumentation())
                .creationDate(documentBranch.getCreationDate())
                .branch(documentBranch.getBranch().getIdBranch())
                .build();

        return Optional.of(documentBranchResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAll(Pageable pageable) {
        Page<DocumentBranch> documentBranchPage = documentBranchRepository.findAll(pageable);

        Page<DocumentResponseDto> documentBranchResponseDtoPage = documentBranchPage.map(
                documentBranch -> DocumentResponseDto.builder()
                        .idDocumentation(documentBranch.getIdDocumentation())
                        .title(documentBranch.getTitle())
                        .risk(documentBranch.getRisk())
                        .status(documentBranch.getStatus())
                        .documentation(documentBranch.getDocumentation())
                        .creationDate(documentBranch.getCreationDate())
                        .branch(documentBranch.getBranch().getIdBranch())
                        .build()
        );

        return documentBranchResponseDtoPage;
    }

    @Override
    public Optional<DocumentResponseDto> update(DocumentBranchRequestDto documentBranchRequestDto) {
        Optional<DocumentBranch> documentBranchOptional = documentBranchRepository.findById(documentBranchRequestDto.getIdDocumentation());

        DocumentBranch documentBranch = documentBranchOptional.orElseThrow(() -> new RuntimeException("DocumentBranch not found"));

        documentBranch.setTitle(documentBranchRequestDto.getTitle() != null ? documentBranchRequestDto.getTitle() : documentBranch.getTitle());
        documentBranch.setRisk(documentBranchRequestDto.getRisk() != null ? documentBranchRequestDto.getRisk() : documentBranch.getRisk());
        documentBranch.setStatus(documentBranchRequestDto.getStatus() != null ? documentBranchRequestDto.getStatus() : documentBranch.getStatus());
        documentBranch.setDocumentation(documentBranchRequestDto.getDocumentation() != null ? documentBranchRequestDto.getDocumentation() : documentBranch.getDocumentation());
        documentBranch.setCreationDate(documentBranchRequestDto.getCreationDate() != null ? documentBranchRequestDto.getCreationDate() : documentBranch.getCreationDate());

        DocumentBranch savedDocumentBranch = documentBranchRepository.save(documentBranch);

        DocumentResponseDto documentBranchResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentBranch.getIdDocumentation())
                .title(savedDocumentBranch.getTitle())
                .risk(savedDocumentBranch.getRisk())
                .status(savedDocumentBranch.getStatus())
                .documentation(savedDocumentBranch.getDocumentation())
                .creationDate(savedDocumentBranch.getCreationDate())
                .branch(savedDocumentBranch.getBranch().getIdBranch())
                .build();

        return Optional.of(documentBranchResponse);
    }

    @Override
    public void delete(String id) {
        documentBranchRepository.deleteById(id);
    }
}
