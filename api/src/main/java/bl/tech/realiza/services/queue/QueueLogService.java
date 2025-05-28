package bl.tech.realiza.services.queue;

import org.springframework.stereotype.Service;

@Service
public class QueueLogService {

    public void logSuccess(String type, String id) {
        System.out.printf("✅ Processado com sucesso: %s - %s%n", type, id);
    }

    public void logFailure(String type, String id, Exception e) {
        System.err.printf("❌ Erro ao processar %s - %s: %s%n", type, id, e.getMessage());
    }
}

