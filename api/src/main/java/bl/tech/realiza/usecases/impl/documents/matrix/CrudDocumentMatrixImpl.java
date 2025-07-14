package bl.tech.realiza.usecases.impl.documents.matrix;

import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.serviceType.ServiceType;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrixSubgroup;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixSubgroupRepository;
import bl.tech.realiza.gateways.requests.documents.matrix.DocumentMatrixRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
import bl.tech.realiza.gateways.responses.queue.SetupMessage;
import bl.tech.realiza.services.queue.SetupAsyncQueueProducer;
import bl.tech.realiza.usecases.interfaces.documents.matrix.CrudDocumentMatrix;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudDocumentMatrixImpl implements CrudDocumentMatrix {

    private final DocumentMatrixRepository documentMatrixRepository;
    private final DocumentMatrixSubgroupRepository documentMatrixSubgroupRepository;
    private final SetupAsyncQueueProducer setupAsyncQueueProducer;

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
                .isDocumentUnique(documentMatrixRequestDto.getIsDocumentUnique())
                .subGroup(documentMatrixSubgroup)
                .build();

        DocumentMatrix savedDocumentMatrix = documentMatrixRepository.save(newDocumentMatrix);

        return toDto(savedDocumentMatrix);
    }

    @Override
    public Optional<DocumentMatrixResponseDto> findOne(String id) {
        Optional<DocumentMatrix> documentMatrixOptional = documentMatrixRepository.findById(id);

        DocumentMatrix documentMatrix = documentMatrixOptional.orElseThrow(() -> new EntityNotFoundException("DocumentMatrix not found"));

        return Optional.of(toDto(documentMatrix));
    }

    @Override
    public Page<DocumentMatrixResponseDto> findAll(Pageable pageable) {
        Page<DocumentMatrix> documentMatrixPage = documentMatrixRepository.findAll(pageable);

        return toDto(documentMatrixPage);
    }

    @Override
    public Optional<DocumentMatrixResponseDto> update(String id, Boolean replicate, DocumentMatrixRequestDto documentMatrixRequestDto) {
        Optional<DocumentMatrix> documentMatrixOptional = documentMatrixRepository.findById(id);

        DocumentMatrix documentMatrix = documentMatrixOptional.orElseThrow(() -> new EntityNotFoundException("DocumentMatrix not found"));

        Optional<DocumentMatrixSubgroup> documentMatrixSubgroupOptional = documentMatrixSubgroupRepository.findById(documentMatrixRequestDto.getSubgroup());

        DocumentMatrixSubgroup documentMatrixSubgroup = documentMatrixSubgroupOptional.orElseThrow(() -> new EntityNotFoundException("Subgroup not found"));

        documentMatrix.setName(documentMatrixRequestDto.getName() != null
                ? documentMatrixRequestDto.getName()
                : documentMatrix.getName());
        documentMatrix.setType(documentMatrixRequestDto.getType() != null
                ? documentMatrixRequestDto.getType()
                : documentMatrix.getType());
        documentMatrix.setDoesBlock(documentMatrixRequestDto.getDoesBlock() != null
                ? documentMatrixRequestDto.getDoesBlock()
                : documentMatrix.getDoesBlock());
        documentMatrix.setSubGroup(documentMatrixRequestDto.getSubgroup() != null
                ? documentMatrixSubgroup
                : documentMatrix.getSubGroup());
        documentMatrix.setIsDocumentUnique(documentMatrixRequestDto.getIsDocumentUnique() != null
                ? documentMatrixRequestDto.getIsDocumentUnique()
                : documentMatrix.getIsDocumentUnique());
        documentMatrix.setExpirationDateUnit(documentMatrixRequestDto.getExpirationDateUnit() != null
                ? documentMatrixRequestDto.getExpirationDateUnit()
                : documentMatrix.getExpirationDateUnit());
        documentMatrix.setExpirationDateAmount(documentMatrixRequestDto.getExpirationDateAmount() != null
                ? documentMatrixRequestDto.getExpirationDateAmount()
                : documentMatrix.getExpirationDateAmount());
        if (replicate != null && replicate) {
            setupAsyncQueueProducer.sendSetup(new SetupMessage("REPLICATE_DOCUMENT_MATRIX_FROM_SYSTEM",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    documentMatrix.getIdDocument(),
                    null,
                    Activity.Risk.LOW,
                    ServiceType.Risk.LOW));
        }

        return Optional.of(toDto(documentMatrix));
    }

    @Override
    public void delete(String id) {
        documentMatrixRepository.deleteById(id);
    }

    @Override
    public Page<DocumentMatrixResponseDto> findAllBySubgroup(String idSearch, Pageable pageable) {
        Page<DocumentMatrix> documentMatrixPage = documentMatrixRepository.findAllBySubGroup_Group_IdDocumentGroup(idSearch, pageable);

        return toDto(documentMatrixPage);
    }

    @Override
    public Page<DocumentMatrixResponseDto> findAllByGroup(String idSearch, Pageable pageable) {
        Page<DocumentMatrix> documentMatrixPage = documentMatrixRepository.findAllBySubGroup_Group_IdDocumentGroup(idSearch, pageable);

        return toDto(documentMatrixPage);
    }

    private DocumentMatrixResponseDto toDto(DocumentMatrix documentMatrix) {
        return DocumentMatrixResponseDto.builder()
                .idDocumentMatrix(documentMatrix.getIdDocument())
                .name(documentMatrix.getName())
                .expirationDateUnit(documentMatrix.getExpirationDateUnit())
                .expirationDateAmount(documentMatrix.getExpirationDateAmount())
                .type(documentMatrix.getType())
                .doesBlock(documentMatrix.getDoesBlock())
                .isDocumentUnique(documentMatrix.getIsDocumentUnique())
                .idDocumentSubgroup(documentMatrix.getSubGroup() != null
                        ? documentMatrix.getSubGroup().getIdDocumentSubgroup()
                        : null)
                .subgroupName(documentMatrix.getSubGroup() != null
                        ? documentMatrix.getSubGroup().getSubgroupName()
                        : null)
                .build();
    }

    private Page<DocumentMatrixResponseDto> toDto(Page<DocumentMatrix> documentMatrixPage) {
        return documentMatrixPage.map(this::toDto);
    }

    private List<DocumentMatrixResponseDto> toDto(List<DocumentMatrix> documentMatrixList) {
        return documentMatrixList.stream().map(this::toDto).toList();
    }
}
