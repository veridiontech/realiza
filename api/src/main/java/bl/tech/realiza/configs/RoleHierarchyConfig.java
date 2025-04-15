package bl.tech.realiza.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Configuration
public class RoleHierarchyConfig {
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = """
            ROLE_ADMIN > ROLE_REALIZA_PLUS
            ROLE_REALIZA_PLUS > ROLE_REALIZA_BASIC
            ROLE_REALIZA_BASIC > ROLE_CLIENT_RESPONSIBLE
            ROLE_REALIZA_BASIC > ROLE_SUPPLIER_RESPONSIBLE
            ROLE_REALIZA_BASIC > ROLE_SUBCONTRACTOR_RESPONSIBLE
            ROLE_CLIENT_RESPONSIBLE > ROLE_CLIENT_MANAGER
            ROLE_SUPPLIER_RESPONSIBLE > ROLE_SUPPLIER_MANAGER
            ROLE_SUBCONTRACTOR_RESPONSIBLE > ROLE_SUBCONTRACTOR_MANAGER
            """;
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

    @Bean
    public DefaultMethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }

    /*
    ROLE_ADMIN,
        ROLE_REALIZA_PLUS,
        ROLE_REALIZA_BASIC,
        ROLE_CLIENT_RESPONSIBLE,
        ROLE_CLIENT_MANAGER,
        ROLE_SUPPLIER_RESPONSIBLE,
        ROLE_SUPPLIER_MANAGER,
        ROLE_SUBCONTRACTOR_RESPONSIBLE,
        ROLE_SUBCONTRACTOR_MANAGER,
        ROLE_VIEWER
     */
}
