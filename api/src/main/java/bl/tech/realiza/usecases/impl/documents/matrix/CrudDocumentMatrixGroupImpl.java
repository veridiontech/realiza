package bl.tech.realiza.usecases.impl.documents.matrix;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrixGroup;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixGroupRepository;
import bl.tech.realiza.gateways.requests.documents.matrix.DocumentMatrixGroupRequestDto;
import bl.tech.realiza.gateways.requests.documents.matrix.DocumentMatrixRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.matrix.CrudDocumentMatrixGroup;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudDocumentMatrixGroupImpl implements CrudDocumentMatrixGroup {

    private final DocumentMatrixGroupRepository documentMatrixGroupRepository;

    @Override
    public DocumentMatrixResponseDto save(DocumentMatrixGroupRequestDto documentMatrixGroupRequestDto) {
        DocumentMatrixGroup newDocumentMatrixGroup = DocumentMatrixGroup.builder()
                .groupName(documentMatrixGroupRequestDto.getGroupName())
                .build();

        DocumentMatrixGroup savedDocumentMatrixGroup = documentMatrixGroupRepository.save(newDocumentMatrixGroup);

        DocumentMatrixResponseDto documentMatrixResponse = DocumentMatrixResponseDto.builder()
                .idDocumentGroup(savedDocumentMatrixGroup.getIdDocumentGroup())
                .groupName(savedDocumentMatrixGroup.getGroupName())
                .build();

        return documentMatrixResponse;
    }

    @Override
    public Optional<DocumentMatrixResponseDto> findOne(String id) {
        Optional<DocumentMatrixGroup> documentMatrixGroupOptional = documentMatrixGroupRepository.findById(id);

        DocumentMatrixGroup documentMatrixGroup = documentMatrixGroupOptional.orElseThrow(() -> new EntityNotFoundException("Group not found"));

        DocumentMatrixResponseDto documentMatrixResponse = DocumentMatrixResponseDto.builder()
                .idDocumentGroup(documentMatrixGroup.getIdDocumentGroup())
                .groupName(documentMatrixGroup.getGroupName())
                .build();

        return Optional.of(documentMatrixResponse);
    }

    @Override
    public Page<DocumentMatrixResponseDto> findAll(Pageable pageable) {
        Page<DocumentMatrixGroup> documentMatrixGroupPage = documentMatrixGroupRepository.findAll(pageable);

        Page<DocumentMatrixResponseDto> documentMatrixResponseDtoPage = documentMatrixGroupPage.map(
                documentMatrix -> DocumentMatrixResponseDto.builder()
                        .idDocumentMatrix(documentMatrix.getIdDocumentGroup())
                        .groupName(documentMatrix.getGroupName())
                        .build()
        );

        return documentMatrixResponseDtoPage;
    }

    @Override
    public Optional<DocumentMatrixResponseDto> update(String id, DocumentMatrixGroupRequestDto documentMatrixGroupRequestDto) {
        Optional<DocumentMatrixGroup> documentMatrixGroupOptional = documentMatrixGroupRepository.findById(id);

        DocumentMatrixGroup documentMatrixGroup = documentMatrixGroupOptional.orElseThrow(() -> new EntityNotFoundException("Group not found"));

        documentMatrixGroup.setGroupName(documentMatrixGroupRequestDto.getGroupName() != null ? documentMatrixGroupRequestDto.getGroupName() : documentMatrixGroup.getGroupName());

        DocumentMatrixGroup savedDocumentMatrixGroup = documentMatrixGroupRepository.save(documentMatrixGroup);

        DocumentMatrixResponseDto documentMatrixResponse = DocumentMatrixResponseDto.builder()
                .idDocumentGroup(savedDocumentMatrixGroup.getIdDocumentGroup())
                .groupName(savedDocumentMatrixGroup.getGroupName())
                .build();

        return Optional.of(documentMatrixResponse);
    }

    @Override
    public void delete(String id) {
        documentMatrixGroupRepository.deleteById(id);
    }
}
