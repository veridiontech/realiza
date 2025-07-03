package bl.tech.realiza.gateways.repositories.services;

import bl.tech.realiza.domains.services.IaAdditionalPrompt;
import bl.tech.realiza.gateways.responses.services.iaAditionalPrompt.IaAdditionalPromptNameListResponseDto;
import bl.tech.realiza.gateways.responses.services.iaAditionalPrompt.IaAdditionalPromptResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IaAdditionalPromptRepository extends JpaRepository<IaAdditionalPrompt, String> {
    IaAdditionalPrompt findByDocumentMatrix_Name(String expectedType);

    @Query("""
    SELECT new bl.tech.realiza.gateways.responses.services.iaAditionalPrompt.IaAdditionalPromptResponseDto(
        p.id,
        p.description,
        dm.idDocument,
        dm.name)
    FROM IaAdditionalPrompt p
    JOIN p.documentMatrix dm
""")
    List<IaAdditionalPromptResponseDto> findAllIaAdditionalPromptToDto();

    @Query("""
    SELECT new bl.tech.realiza.gateways.responses.services.iaAditionalPrompt.IaAdditionalPromptNameListResponseDto(
        p.id,
        dm.name)
    FROM IaAdditionalPrompt p
    JOIN p.documentMatrix dm
""")
    List<IaAdditionalPromptNameListResponseDto> findAllIaAdditionalPromptNameListToDto();
}
