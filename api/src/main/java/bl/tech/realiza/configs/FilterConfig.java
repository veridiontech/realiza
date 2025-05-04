package bl.tech.realiza.configs;

import bl.tech.realiza.services.auth.ContractIdBranchFilter;
import bl.tech.realiza.services.auth.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final JwtService jwtService;

    @Bean
    public FilterRegistrationBean<ContractIdBranchFilter> loggingFilter() {
        FilterRegistrationBean<ContractIdBranchFilter> registrationBean = new FilterRegistrationBean<>();

        // Agora passamos o JwtService para o filtro
        registrationBean.setFilter(new ContractIdBranchFilter(jwtService));
        registrationBean.addUrlPatterns("/contract/finish/*"); // Aplica o filtro apenas à rota de contrato
        return registrationBean;
    }
}
