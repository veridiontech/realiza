package bl.tech.realiza.services.daily;

import bl.tech.realiza.usecases.impl.documents.document.CrudDocumentImpl;
import bl.tech.realiza.usecases.interfaces.documents.document.CrudDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyJob {
    private final CrudDocument crudDocument;

    @Scheduled(cron = "0 0 0 * * *", zone = "America/Sao_Paulo")
    public void runDailyTask() {
        log.info("Executando tarefa diária de verificação de documentos...");
        dailyDocumentCheck();
        log.info("Tarefa diária concluída.");
    }

    public void dailyDocumentCheck() {
        crudDocument.expirationChange();
        crudDocument.expirationCheck();
    }
}
