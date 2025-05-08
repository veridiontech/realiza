package bl.tech.realiza.configs;

import bl.tech.realiza.services.auth.ContractIdBranchFilter;
import bl.tech.realiza.services.auth.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final JwtService jwtService;
    private final RoleHierarchy roleHierarchy;

    @Bean
    public FilterRegistrationBean<ContractIdBranchFilter> loggingFilter() {
        FilterRegistrationBean<ContractIdBranchFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new ContractIdBranchFilter(jwtService, roleHierarchy));
        registrationBean.addUrlPatterns("/contract/finish/*");
        return registrationBean;
    }
}

