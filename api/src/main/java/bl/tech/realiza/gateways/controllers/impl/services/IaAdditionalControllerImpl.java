package bl.tech.realiza.gateways.controllers.impl.services;

import bl.tech.realiza.gateways.controllers.interfaces.services.IaAdditionalPromptController;
import bl.tech.realiza.gateways.requests.services.iaAdditionalPrompt.IaAdditionalPromptRequestDto;
import bl.tech.realiza.gateways.responses.services.iaAditionalPrompt.IaAdditionalPromptNameListResponseDto;
import bl.tech.realiza.gateways.responses.services.iaAditionalPrompt.IaAdditionalPromptResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/prompt")
@Tag(name = "IA Additional Prompt")
public class IaAdditionalControllerImpl implements IaAdditionalPromptController {
    @Override
    public ResponseEntity<IaAdditionalPromptResponseDto> createPrompt(IaAdditionalPromptRequestDto iaAdditionalPromptRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<List<IaAdditionalPromptResponseDto>> getAllPrompt() {
        return null;
    }

    @Override
    public ResponseEntity<IaAdditionalPromptResponseDto> getOnePrompt(String id) {
        return null;
    }

    @Override
    public ResponseEntity<IaAdditionalPromptResponseDto> updatePrompt(String id, IaAdditionalPromptRequestDto iaAdditionalPromptRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<IaAdditionalPromptNameListResponseDto> getAllPromptNameList() {
        return null;
    }
}
