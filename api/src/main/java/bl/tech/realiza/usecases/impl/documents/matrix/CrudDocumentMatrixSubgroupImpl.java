package bl.tech.realiza.usecases.impl.documents.matrix;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrixGroup;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrixSubgroup;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixGroupRepository;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixSubgroupRepository;
import bl.tech.realiza.gateways.requests.documents.matrix.DocumentMatrixSubgroupRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.matrix.CrudDocumentMatrixSubgroup;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudDocumentMatrixSubgroupImpl implements CrudDocumentMatrixSubgroup {

    private final DocumentMatrixSubgroupRepository documentMatrixSubgroupRepository;
    private final DocumentMatrixGroupRepository documentMatrixGroupRepository;

    @Override
    public DocumentMatrixResponseDto save(DocumentMatrixSubgroupRequestDto documentMatrixSubgroupRequest) {

        Optional<DocumentMatrixGroup> documentMatrixGroupOptional = documentMatrixGroupRepository.findById(documentMatrixSubgroupRequest.getGroup());

        DocumentMatrixGroup documentMatrixGroup = documentMatrixGroupOptional.orElseThrow(() -> new RuntimeException("Group not found"));

        DocumentMatrixSubgroup newDocumentMatrixSubgroup = DocumentMatrixSubgroup.builder()
                .subgroupName(documentMatrixSubgroupRequest.getSubgroupName())
                .group(documentMatrixGroup)
                .build();

        DocumentMatrixSubgroup savedDocumentMatrixSubgroup = documentMatrixSubgroupRepository.save(newDocumentMatrixSubgroup);

        DocumentMatrixResponseDto documentMatrixResponse = DocumentMatrixResponseDto.builder()
                .idDocumentSubgroup(savedDocumentMatrixSubgroup.getIdDocumentSubgroup())
                .subgroupName(savedDocumentMatrixSubgroup.getSubgroupName())
                .idDocumentGroup(savedDocumentMatrixSubgroup.getGroup().getIdDocumentGroup())
                .build();

        return documentMatrixResponse;
    }

    @Override
    public Optional<DocumentMatrixResponseDto> findOne(String id) {

        Optional<DocumentMatrixSubgroup> documentMatrixSubgroupOptional = documentMatrixSubgroupRepository.findById(id);

        DocumentMatrixSubgroup documentMatrixSubgroup = documentMatrixSubgroupOptional.orElseThrow(() -> new RuntimeException("Subgroup not found"));

        DocumentMatrixResponseDto documentMatrixResponse = DocumentMatrixResponseDto.builder()
                .idDocumentSubgroup(documentMatrixSubgroup.getIdDocumentSubgroup())
                .subgroupName(documentMatrixSubgroup.getSubgroupName())
                .idDocumentGroup(documentMatrixSubgroup.getGroup().getIdDocumentGroup())
                .build();

        return Optional.of(documentMatrixResponse);
    }

    @Override
    public Page<DocumentMatrixResponseDto> findAll(Pageable pageable) {
        Page<DocumentMatrixSubgroup> documentMatrixSubgroupPage = documentMatrixSubgroupRepository.findAll(pageable);

        Page<DocumentMatrixResponseDto> documentMatrixResponseDtoPage = documentMatrixSubgroupPage.map(
                documentMatrixSubgroup -> DocumentMatrixResponseDto.builder()
                        .idDocumentSubgroup(documentMatrixSubgroup.getIdDocumentSubgroup())
                        .subgroupName(documentMatrixSubgroup.getSubgroupName())
                        .idDocumentGroup(documentMatrixSubgroup.getGroup().getIdDocumentGroup())
                        .build()
        );

        return documentMatrixResponseDtoPage;
    }

    @Override
    public Optional<DocumentMatrixResponseDto> update(DocumentMatrixSubgroupRequestDto documentMatrixSubgroupRequest) {
        Optional<DocumentMatrixSubgroup> documentMatrixSubgroupOptional = documentMatrixSubgroupRepository.findById(documentMatrixSubgroupRequest.getIdSubgroup());

        DocumentMatrixSubgroup documentMatrixSubgroup = documentMatrixSubgroupOptional.orElseThrow(() -> new RuntimeException("Subgroup not found"));

        documentMatrixSubgroup.setSubgroupName(documentMatrixSubgroupRequest.getSubgroupName() != null ? documentMatrixSubgroupRequest.getSubgroupName() : documentMatrixSubgroup.getSubgroupName());

        DocumentMatrixResponseDto documentMatrixResponse = DocumentMatrixResponseDto.builder()
                .idDocumentSubgroup(documentMatrixSubgroup.getIdDocumentSubgroup())
                .subgroupName(documentMatrixSubgroup.getSubgroupName())
                .idDocumentGroup(documentMatrixSubgroup.getGroup().getIdDocumentGroup())
                .build();

        return Optional.of(documentMatrixResponse);
    }

    @Override
    public void delete(String id) {
        documentMatrixGroupRepository.deleteById(id);
    }
}
