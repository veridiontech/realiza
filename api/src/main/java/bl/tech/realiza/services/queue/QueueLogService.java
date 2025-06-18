package bl.tech.realiza.services.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class QueueLogService {

    public void logSuccess(String type, String id, long start) {
        log.info("✅ Processado com sucesso: {} - {} em {} ms", type, id, System.currentTimeMillis() - start);
    }

    public void logFailure(String type, String id, Exception e, long start) {
        log.info("❌ Erro ao processar {} - {}: {} em {} ms", type, id, e.getMessage(), System.currentTimeMillis() - start);
    }

}

