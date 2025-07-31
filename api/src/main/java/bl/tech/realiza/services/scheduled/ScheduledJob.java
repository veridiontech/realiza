package bl.tech.realiza.services.scheduled;

import bl.tech.realiza.domains.enums.DocumentValidityEnum;
import bl.tech.realiza.usecases.interfaces.documents.document.CrudDocument;
import bl.tech.realiza.usecases.interfaces.users.CrudUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledJob {
    private final CrudDocument crudDocument;
    private final CrudUser crudUser;

    @Scheduled(cron = "0 0 1 * * *", zone = "America/Sao_Paulo")
    public void runDailyTask() {
        log.info("Executando tarefas diárias de verificação de documentos...");
        dailyDocumentCheck();
        log.info("Executando tarefas diárias de verificação de usuários...");
        dailyUserCheck();
        log.info("Tarefa diária concluída.");
    }

    @Scheduled(cron = "0 0 2 * * 0", zone = "America/Sao_Paulo")
    public void runWeeklyTask() {
        log.info("Executando tarefas semanais de verificação de documentos...");
        weeklyDocumentCheck();
        log.info("Tarefa semanal concluída.");
    }

    @Scheduled(cron = "0 0 3 1 * *", zone = "America/Sao_Paulo")
    public void runMonthlyTask() {
        log.info("Executando tarefas mensais de verificação de documentos...");
        monthlyDocumentCheck();
        log.info("Tarefa mensal concluída.");
    }

    @Scheduled(cron = "0 0 4 1 1 *", zone = "America/Sao_Paulo")
    public void runAnnualTask() {
        log.info("Executando tarefas anuais de verificação de documentos...");
        annualDocumentCheck();
        log.info("Tarefa anual concluída.");
    }

    public void dailyDocumentCheck() {
        crudDocument.expirationChange();
        crudDocument.expirationCheck();
        crudDocument.deleteOldReprovedDocuments();
    }

    public void weeklyDocumentCheck() {
        crudDocument.documentValidityCheck(DocumentValidityEnum.WEEKLY);
    }

    public void monthlyDocumentCheck() {
        crudDocument.documentValidityCheck(DocumentValidityEnum.MONTHLY);
    }

    public void annualDocumentCheck() {
        crudDocument.documentValidityCheck(DocumentValidityEnum.ANNUAL);
    }

    public void dailyUserCheck() {
        crudUser.fourDigitCodeCheck();
    }
}
