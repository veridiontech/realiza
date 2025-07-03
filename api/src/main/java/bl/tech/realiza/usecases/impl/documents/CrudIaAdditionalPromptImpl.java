package bl.tech.realiza.usecases.impl.documents;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.services.IaAdditionalPrompt;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.services.IaAdditionalPromptRepository;
import bl.tech.realiza.gateways.requests.services.iaAdditionalPrompt.IaAdditionalPromptRequestDto;
import bl.tech.realiza.gateways.responses.services.iaAditionalPrompt.IaAdditionalPromptNameListResponseDto;
import bl.tech.realiza.gateways.responses.services.iaAditionalPrompt.IaAdditionalPromptResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.CrudIaAdditionalPrompt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CrudIaAdditionalPromptImpl implements CrudIaAdditionalPrompt {
    private final DocumentMatrixRepository documentMatrixRepository;
    private final IaAdditionalPromptRepository iaAdditionalPromptRepository;

    @Override
    public IaAdditionalPromptResponseDto save(IaAdditionalPromptRequestDto requestDto) {
        return toDto(iaAdditionalPromptRepository.save(toEntity(requestDto)));
    }

    @Override
    public IaAdditionalPromptResponseDto update(String id, IaAdditionalPromptRequestDto requestDto) {
        IaAdditionalPrompt additionalPrompt = iaAdditionalPromptRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Prompt not found"));

        additionalPrompt.setDescription(requestDto.getDescription());

        return toDto(iaAdditionalPromptRepository.save(additionalPrompt));
    }

    @Override
    public void delete(String id) {
        iaAdditionalPromptRepository.deleteById(id);
    }

    @Override
    public IaAdditionalPromptResponseDto findById(String id) {
        return toDto(iaAdditionalPromptRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Prompt not found")));
    }

    @Override
    public List<IaAdditionalPromptResponseDto> findAll() {
        return toDto(iaAdditionalPromptRepository.findAll());
    }

    @Override
    public List<IaAdditionalPromptNameListResponseDto> findAllNameList() {
        return iaAdditionalPromptRepository.findAll().stream().map(
                iaAdditionalPrompt -> IaAdditionalPromptNameListResponseDto.builder()
                        .id(iaAdditionalPrompt.getId())
                        .documentTitle(iaAdditionalPrompt.getDocumentMatrix().getName())
                        .build()
        ).toList();
    }

    private IaAdditionalPrompt toEntity(IaAdditionalPromptRequestDto requestDto) {
        DocumentMatrix documentMatrix = documentMatrixRepository.findById(requestDto.getDocumentId())
                .orElseThrow(() -> new NotFoundException("Document Matrix not found"));
        return IaAdditionalPrompt.builder()
                .description(requestDto.getDescription())
                .documentMatrix(documentMatrix)
                .build();
    }

    private IaAdditionalPromptResponseDto toDto(IaAdditionalPrompt iaAdditionalPrompt) {
        return IaAdditionalPromptResponseDto.builder()
                .id(iaAdditionalPrompt.getId())
                .description(iaAdditionalPrompt.getDescription())
                .documentId(iaAdditionalPrompt.getDocumentMatrix().getIdDocument())
                .documentTitle(iaAdditionalPrompt.getDocumentMatrix().getName())
                .build();
    }

    private List<IaAdditionalPromptResponseDto> toDto(List<IaAdditionalPrompt> iaAdditionalPrompt) {
        return iaAdditionalPrompt.stream().map(this::toDto).toList();
    }
}
