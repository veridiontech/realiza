package bl.tech.realiza.usecases.interfaces.documents;

import bl.tech.realiza.gateways.requests.services.iaAdditionalPrompt.IaAdditionalPromptRequestDto;
import bl.tech.realiza.gateways.responses.services.iaAditionalPrompt.IaAdditionalPromptNameListResponseDto;
import bl.tech.realiza.gateways.responses.services.iaAditionalPrompt.IaAdditionalPromptResponseDto;

import java.util.List;

public interface CrudIaAdditionalPrompt {
    IaAdditionalPromptResponseDto save(IaAdditionalPromptRequestDto requestDto);
    IaAdditionalPromptResponseDto update(String id, IaAdditionalPromptRequestDto requestDto);
    IaAdditionalPromptResponseDto delete(String id);
    IaAdditionalPromptResponseDto findById(String id);
    List<IaAdditionalPromptResponseDto> findAll();
    List<IaAdditionalPromptNameListResponseDto> findAllNameList();
}
