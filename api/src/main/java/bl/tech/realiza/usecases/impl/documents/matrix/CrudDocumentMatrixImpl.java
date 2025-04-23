package bl.tech.realiza.usecases.impl.documents.matrix;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrixSubgroup;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixSubgroupRepository;
import bl.tech.realiza.gateways.requests.documents.matrix.DocumentMatrixRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.matrix.CrudDocumentMatrix;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudDocumentMatrixImpl implements CrudDocumentMatrix {

    private final DocumentMatrixRepository documentMatrixRepository;
    private final DocumentMatrixSubgroupRepository documentMatrixSubgroupRepository;

    @Override
    public DocumentMatrixResponseDto save(DocumentMatrixRequestDto documentMatrixRequestDto) {
        if (documentMatrixRequestDto.getSubgroup() == null || documentMatrixRequestDto.getSubgroup().isEmpty()) {
            throw new BadRequestException("Invalid subgroup");
        }
        Optional<DocumentMatrixSubgroup> documentMatrixSubgroupOptional = documentMatrixSubgroupRepository.findById(documentMatrixRequestDto.getSubgroup());

        DocumentMatrixSubgroup documentMatrixSubgroup = documentMatrixSubgroupOptional.orElseThrow(() -> new EntityNotFoundException("Subgroup not found"));

        DocumentMatrix newDocumentMatrix = DocumentMatrix.builder()
                .name(documentMatrixRequestDto.getName())
                .type(documentMatrixRequestDto.getType())
                .doesBlock(documentMatrixRequestDto.getDoesBlock())
                .subGroup(documentMatrixSubgroup)
                .build();

        DocumentMatrix savedDocumentMatrix = documentMatrixRepository.save(newDocumentMatrix);

        DocumentMatrixResponseDto documentMatrixResponse = DocumentMatrixResponseDto.builder()
                .name(savedDocumentMatrix.getName())
                .idDocumentMatrix(savedDocumentMatrix.getIdDocument())
                .type(savedDocumentMatrix.getType())
                .doesBlock(savedDocumentMatrix.getDoesBlock())
                .idDocumentSubgroup(savedDocumentMatrix.getSubGroup().getIdDocumentSubgroup())
                .build();

        return documentMatrixResponse;
    }

    @Override
    public Optional<DocumentMatrixResponseDto> findOne(String id) {
        Optional<DocumentMatrix> documentMatrixOptional = documentMatrixRepository.findById(id);

        DocumentMatrix documentMatrix = documentMatrixOptional.orElseThrow(() -> new EntityNotFoundException("DocumentMatrix not found"));

        DocumentMatrixResponseDto documentMatrixResponse = DocumentMatrixResponseDto.builder()
                .idDocumentMatrix(documentMatrix.getIdDocument())
                .name(documentMatrix.getName())
                .type(documentMatrix.getType())
                .doesBlock(documentMatrix.getDoesBlock())
                .idDocumentSubgroup(documentMatrix.getSubGroup().getIdDocumentSubgroup())
                .build();

        return Optional.of(documentMatrixResponse);
    }

    @Override
    public Page<DocumentMatrixResponseDto> findAll(Pageable pageable) {
        Page<DocumentMatrix> documentMatrixPage = documentMatrixRepository.findAll(pageable);

        Page<DocumentMatrixResponseDto> documentMatrixResponseDtoPage = documentMatrixPage.map(
                documentMatrix -> DocumentMatrixResponseDto.builder()
                        .idDocumentMatrix(documentMatrix.getIdDocument())
                        .name(documentMatrix.getName())
                        .type(documentMatrix.getType())
                        .doesBlock(documentMatrix.getDoesBlock())
                        .idDocumentSubgroup(documentMatrix.getSubGroup().getIdDocumentSubgroup())
                        .build()
        );

        return documentMatrixResponseDtoPage;
    }

    @Override
    public Optional<DocumentMatrixResponseDto> update(String id, DocumentMatrixRequestDto documentMatrixRequestDto) {
        Optional<DocumentMatrix> documentMatrixOptional = documentMatrixRepository.findById(id);

        DocumentMatrix documentMatrix = documentMatrixOptional.orElseThrow(() -> new EntityNotFoundException("DocumentMatrix not found"));

        Optional<DocumentMatrixSubgroup> documentMatrixSubgroupOptional = documentMatrixSubgroupRepository.findById(documentMatrixRequestDto.getSubgroup());

        DocumentMatrixSubgroup documentMatrixSubgroup = documentMatrixSubgroupOptional.orElseThrow(() -> new EntityNotFoundException("Subgroup not found"));

        documentMatrix.setName(documentMatrixRequestDto.getName() != null ? documentMatrixRequestDto.getName() : documentMatrix.getName());
        documentMatrix.setType(documentMatrixRequestDto.getType() != null ? documentMatrixRequestDto.getType() : documentMatrix.getType());
        documentMatrix.setDoesBlock(documentMatrixRequestDto.getDoesBlock() != null ? documentMatrixRequestDto.getDoesBlock() : documentMatrix.getDoesBlock());
        documentMatrix.setSubGroup(documentMatrixRequestDto.getSubgroup() != null ? documentMatrixSubgroup : documentMatrix.getSubGroup());

        DocumentMatrixResponseDto documentMatrixResponse = DocumentMatrixResponseDto.builder()
                .idDocumentMatrix(documentMatrix.getIdDocument())
                .name(documentMatrix.getName())
                .type(documentMatrix.getType())
                .doesBlock(documentMatrix.getDoesBlock())
                .idDocumentSubgroup(documentMatrix.getSubGroup().getIdDocumentSubgroup())
                .build();

        return Optional.of(documentMatrixResponse);
    }

    @Override
    public void delete(String id) {
        documentMatrixRepository.deleteById(id);
    }

    @Override
    public Page<DocumentMatrixResponseDto> findAllBySubgroup(String idSearch, Pageable pageable) {
        Page<DocumentMatrix> documentMatrixPage = documentMatrixRepository.findAllBySubGroup_Group_IdDocumentGroup(idSearch, pageable);

        Page<DocumentMatrixResponseDto> documentMatrixResponseDtoPage = documentMatrixPage.map(
                documentMatrix -> DocumentMatrixResponseDto.builder()
                        .idDocumentMatrix(documentMatrix.getIdDocument())
                        .name(documentMatrix.getName())
                        .type(documentMatrix.getType())
                        .doesBlock(documentMatrix.getDoesBlock())
                        .idDocumentSubgroup(documentMatrix.getSubGroup().getIdDocumentSubgroup())
                        .build()
        );

        return documentMatrixResponseDtoPage;
    }

    @Override
    public Page<DocumentMatrixResponseDto> findAllByGroup(String idSearch, Pageable pageable) {
        Page<DocumentMatrix> documentMatrixPage = documentMatrixRepository.findAllBySubGroup_Group_IdDocumentGroup(idSearch, pageable);

        Page<DocumentMatrixResponseDto> documentMatrixResponseDtoPage = documentMatrixPage.map(
                documentMatrix -> DocumentMatrixResponseDto.builder()
                        .idDocumentMatrix(documentMatrix.getIdDocument())
                        .name(documentMatrix.getName())
                        .type(documentMatrix.getType())
                        .doesBlock(documentMatrix.getDoesBlock())
                        .idDocumentSubgroup(documentMatrix.getSubGroup().getIdDocumentSubgroup())
                        .build()
        );

        return documentMatrixResponseDtoPage;
    }
}
