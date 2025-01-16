package bl.tech.realiza.usecases.impl.documents.matrix;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrixSubgroup;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixSubgroupRepository;
import bl.tech.realiza.gateways.requests.documents.matrix.DocumentMatrixRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentMatrixResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.matrix.CrudDocumentMatrix;
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
        Optional<DocumentMatrixSubgroup> documentMatrixSubgroupOptional = documentMatrixSubgroupRepository.findById(documentMatrixRequestDto.getSubGroup());

        DocumentMatrixSubgroup documentMatrixSubgroup = documentMatrixSubgroupOptional.orElseThrow(() -> new RuntimeException("Subgroup not found"));

        DocumentMatrix newDocumentMatrix = DocumentMatrix.builder()
                .risk(documentMatrixRequestDto.getRisk())
                .expiration(documentMatrixRequestDto.getExpiration())
                .type(documentMatrixRequestDto.getType())
                .doesBlock(documentMatrixRequestDto.getDoesBlock())
                .subGroup(documentMatrixSubgroup)
                .build();

        DocumentMatrix savedDocumentMatrix = documentMatrixRepository.save(newDocumentMatrix);

        DocumentMatrixResponseDto documentMatrixResponse = DocumentMatrixResponseDto.builder()
                .idDocumentMatrix(savedDocumentMatrix.getIdDocument())
                .risk(savedDocumentMatrix.getRisk())
                .expiration(savedDocumentMatrix.getExpiration())
                .type(savedDocumentMatrix.getType())
                .doesBlock(savedDocumentMatrix.getDoesBlock())
                .idDocumentSubgroup(savedDocumentMatrix.getSubGroup().getIdDocumentSubgroup())
                .build();

        return documentMatrixResponse;
    }

    @Override
    public Optional<DocumentMatrixResponseDto> findOne(String id) {
        Optional<DocumentMatrix> documentMatrixOptional = documentMatrixRepository.findById(id);

        DocumentMatrix documentMatrix = documentMatrixOptional.orElseThrow(() -> new RuntimeException("DocumentMatrix not found"));

        DocumentMatrixResponseDto documentMatrixResponse = DocumentMatrixResponseDto.builder()
                .idDocumentMatrix(documentMatrix.getIdDocument())
                .risk(documentMatrix.getRisk())
                .expiration(documentMatrix.getExpiration())
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
                        .risk(documentMatrix.getRisk())
                        .expiration(documentMatrix.getExpiration())
                        .type(documentMatrix.getType())
                        .doesBlock(documentMatrix.getDoesBlock())
                        .idDocumentSubgroup(documentMatrix.getSubGroup().getIdDocumentSubgroup())
                        .build()
        );

        return documentMatrixResponseDtoPage;
    }

    @Override
    public Optional<DocumentMatrixResponseDto> update(DocumentMatrixRequestDto documentMatrixRequestDto) {
        Optional<DocumentMatrix> documentMatrixOptional = documentMatrixRepository.findById(documentMatrixRequestDto.getIdDocumentMatrix());

        DocumentMatrix documentMatrix = documentMatrixOptional.orElseThrow(() -> new RuntimeException("DocumentMatrix not found"));

        Optional<DocumentMatrixSubgroup> documentMatrixSubgroupOptional = documentMatrixSubgroupRepository.findById(documentMatrixRequestDto.getSubGroup());

        DocumentMatrixSubgroup documentMatrixSubgroup = documentMatrixSubgroupOptional.orElseThrow(() -> new RuntimeException("Subgroup not found"));

        documentMatrix.setRisk(documentMatrixRequestDto.getRisk() != null ? documentMatrixRequestDto.getRisk() : documentMatrix.getRisk());
        documentMatrix.setExpiration(documentMatrixRequestDto.getExpiration() != null ? documentMatrixRequestDto.getExpiration() : documentMatrix.getExpiration());
        documentMatrix.setType(documentMatrixRequestDto.getType() != null ? documentMatrixRequestDto.getType() : documentMatrix.getType());
        documentMatrix.setDoesBlock(documentMatrixRequestDto.getDoesBlock() != null ? documentMatrixRequestDto.getDoesBlock() : documentMatrix.getDoesBlock());
        documentMatrix.setSubGroup(documentMatrixRequestDto.getSubGroup() != null ? documentMatrixSubgroup : documentMatrix.getSubGroup());

        DocumentMatrixResponseDto documentMatrixResponse = DocumentMatrixResponseDto.builder()
                .idDocumentMatrix(documentMatrix.getIdDocument())
                .risk(documentMatrix.getRisk())
                .expiration(documentMatrix.getExpiration())
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
}
