package bl.tech.realiza.services.daily;

import bl.tech.realiza.usecases.interfaces.documents.document.CrudDocument;
import bl.tech.realiza.usecases.interfaces.users.CrudUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyJob {
    private final CrudDocument crudDocument;
    private final CrudUser crudUser;

    @Scheduled(cron = "0 0 3 * * *", zone = "America/Sao_Paulo")
    public void runDailyTask() {
        log.info("Executando tarefas diárias de verificação de documentos...");
        dailyDocumentCheck();
        log.info("Executando tarefas diárias de verificação de usuários...");
        dailyUserCheck();
        log.info("Tarefa diária concluída.");
    }

    public void dailyDocumentCheck() {
        crudDocument.expirationChange();
        crudDocument.expirationCheck();
        crudDocument.deleteOldReprovedDocuments();
    }

    public void dailyUserCheck() {
        crudUser.fourDigitCodeCheck();
    }
}
