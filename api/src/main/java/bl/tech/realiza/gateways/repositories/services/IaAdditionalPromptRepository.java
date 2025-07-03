package bl.tech.realiza.gateways.repositories.services;

import bl.tech.realiza.domains.services.IaAdditionalPrompt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IaAdditionalPromptRepository extends JpaRepository<IaAdditionalPrompt, String> {
    IaAdditionalPrompt findByDocumentMatrix_Name(String expectedType);
}
