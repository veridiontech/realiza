package bl.tech.realiza.services.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class QueueLogService {

    public void logSuccess(String type, String id) {
        log.info("✅ Processado com sucesso: {} - {}", type, id);
    }

    public void logFailure(String type, String id, Exception e) {
        log.info("❌ Erro ao processar {} - {}: {}", type, id, e.getMessage());
    }

}

