package bl.tech.realiza.usecases.impl.documents;

import bl.tech.realiza.domains.services.IaAdditionalPrompt;
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
    @Override
    public IaAdditionalPromptResponseDto save(IaAdditionalPromptRequestDto requestDto) {
        return null;
    }

    @Override
    public IaAdditionalPromptResponseDto update(String id, IaAdditionalPromptRequestDto requestDto) {
        return null;
    }

    @Override
    public IaAdditionalPromptResponseDto delete(String id) {
        return null;
    }

    @Override
    public IaAdditionalPromptResponseDto findById(String id) {
        return null;
    }

    @Override
    public List<IaAdditionalPromptResponseDto> findAll() {
        return List.of();
    }

    @Override
    public List<IaAdditionalPromptNameListResponseDto> findAllNameList() {
        return List.of();
    }

    private IaAdditionalPromptResponseDto toDto(IaAdditionalPrompt iaAdditionalPrompt) {
        return null;
    }
}
