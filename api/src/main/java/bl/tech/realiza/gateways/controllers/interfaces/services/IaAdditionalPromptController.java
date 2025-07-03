package bl.tech.realiza.gateways.controllers.interfaces.services;

import bl.tech.realiza.gateways.requests.services.iaAdditionalPrompt.IaAdditionalPromptRequestDto;
import bl.tech.realiza.gateways.responses.services.iaAditionalPrompt.IaAdditionalPromptNameListResponseDto;
import bl.tech.realiza.gateways.responses.services.iaAditionalPrompt.IaAdditionalPromptResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IaAdditionalPromptController {
    ResponseEntity<IaAdditionalPromptResponseDto> createPrompt(IaAdditionalPromptRequestDto iaAdditionalPromptRequestDto);
    ResponseEntity<List<IaAdditionalPromptResponseDto>> getAllPrompt();
    ResponseEntity<IaAdditionalPromptResponseDto> getOnePrompt(String id);
    ResponseEntity<IaAdditionalPromptResponseDto> updatePrompt(String id, IaAdditionalPromptRequestDto iaAdditionalPromptRequestDto);
    ResponseEntity<IaAdditionalPromptNameListResponseDto> getAllPromptNameList();
}
