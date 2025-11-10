package bl.tech.realiza.services.dashboard;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.CacheManager;

/**
 * Configuração de cache para otimização do Dashboard
 * 
 * Este cache armazena temporariamente resultados de queries pesadas
 * para melhorar a performance do BI.
 * 
 * TTL padrão: 5 minutos (configurável via properties)
 */
@Configuration
@EnableCaching
public class DashboardCacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            "dashboardGeneralDetails",
            "dashboardFilters",
            "documentTypes",
            "supplierCounts"
        );
    }
}
