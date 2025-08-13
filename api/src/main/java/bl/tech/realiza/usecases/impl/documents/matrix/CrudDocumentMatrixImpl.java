package bl.tech.realiza.usecases.impl.documents.matrix;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrixSubgroup;
import bl.tech.realiza.domains.enums.RiskEnum;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixSubgroupRepository;
import bl.tech.realiza.gateways.requests.documents.matrix.DocumentMatrixRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
import bl.tech.realiza.services.queue.replication.ReplicationMessage;
import bl.tech.realiza.services.queue.replication.ReplicationQueueProducer;
import bl.tech.realiza.usecases.interfaces.documents.matrix.CrudDocumentMatrix;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudDocumentMatrixImpl implements CrudDocumentMatrix {

    private final DocumentMatrixRepository documentMatrixRepository;
    private final DocumentMatrixSubgroupRepository documentMatrixSubgroupRepository;
    private final ReplicationQueueProducer replicationQueueProducer;

    @Override
    public DocumentMatrixResponseDto save(DocumentMatrixRequestDto documentMatrixRequestDto) {
        if (documentMatrixRequestDto.getSubgroup() == null || documentMatrixRequestDto.getSubgroup().isEmpty()) {
            throw new BadRequestException("Invalid subgroup");
        }
        DocumentMatrixSubgroup documentMatrixSubgroup = documentMatrixSubgroupRepository.findById(documentMatrixRequestDto.getSubgroup())
                .orElseThrow(() -> new EntityNotFoundException("Subgroup not found"));

        DocumentMatrix savedDocumentMatrix = documentMatrixRepository.save(DocumentMatrix.builder()
                .name(documentMatrixRequestDto.getName())
                .type(documentMatrixRequestDto.getType())
                .doesBlock(documentMatrixRequestDto.getDoesBlock())
                .isDocumentUnique(documentMatrixRequestDto.getIsDocumentUnique())
                        .isValidityFixed(documentMatrixRequestDto.getIsValidityFixed())
                        .fixedValidityAt(parseDayMonth(documentMatrixRequestDto.getFixedValidityAt()))
                .subGroup(documentMatrixSubgroup)
                .build());

        replicationQueueProducer.send(new ReplicationMessage("CREATE_DOCUMENT_MATRIX",
                null,
                null,
                null,
                savedDocumentMatrix.getIdDocument(),
                null,
                RiskEnum.LOW,
                RiskEnum.LOW));

        return toDto(savedDocumentMatrix);
    }

    @Override
    public Optional<DocumentMatrixResponseDto> findOne(String id) {
        DocumentMatrix documentMatrix = documentMatrixRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DocumentMatrix not found"));

        return Optional.of(toDto(documentMatrix));
    }

    @Override
    public Page<DocumentMatrixResponseDto> findAll(Pageable pageable) {
        return toDto(documentMatrixRepository.findAll(pageable));
    }

    @Override
    public Optional<DocumentMatrixResponseDto> update(String id, Boolean replicate, DocumentMatrixRequestDto documentMatrixRequestDto) {
        DocumentMatrix documentMatrix = documentMatrixRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DocumentMatrix not found"));

        DocumentMatrixSubgroup documentMatrixSubgroup = documentMatrix.getSubGroup();

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
        documentMatrix.setIsValidityFixed(documentMatrixRequestDto.getIsValidityFixed() != null
                ? documentMatrixRequestDto.getIsValidityFixed()
                : documentMatrix.getIsValidityFixed());
        documentMatrix.setFixedValidityAt(documentMatrixRequestDto.getFixedValidityAt() != null
                ? (parseDayMonth(documentMatrixRequestDto.getFixedValidityAt()))
                : documentMatrix.getFixedValidityAt());

        if (replicate != null && replicate) {
            replicationQueueProducer.send(new ReplicationMessage("REPLICATE_DOCUMENT_MATRIX_FROM_SYSTEM",
                    null,
                    null,
                    null,
                    documentMatrix.getIdDocument(),
                    null,
                    RiskEnum.LOW,
                    RiskEnum.LOW));
        }

        return Optional.of(toDto(documentMatrix));
    }

    @Override
    public void delete(String id) {
        documentMatrixRepository.deleteById(id);
    }

    @Override
    public Page<DocumentMatrixResponseDto> findAllBySubgroup(String idSearch, Pageable pageable) {
        return toDto(documentMatrixRepository.findAllBySubGroup_Group_IdDocumentGroup(idSearch, pageable));
    }

    @Override
    public Page<DocumentMatrixResponseDto> findAllByGroup(String idSearch, Pageable pageable) {
        return toDto(documentMatrixRepository.findAllBySubGroup_Group_IdDocumentGroup(idSearch, pageable));
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
                .isValidityFixed(documentMatrix.getIsValidityFixed())
                .fixedValidityAt(formatDayMonth(documentMatrix.getFixedValidityAt()))
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

    public LocalDateTime parseDayMonth(String dayMonth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        LocalDate parsedDate = LocalDate.parse(dayMonth, formatter);

        parsedDate = parsedDate.withYear(LocalDate.now().getYear());

        return parsedDate.atStartOfDay();
    }

    public String formatDayMonth(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        return dateTime.format(formatter);
    }

}
