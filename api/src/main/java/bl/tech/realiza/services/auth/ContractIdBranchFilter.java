package bl.tech.realiza.services.auth;

import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;

@WebFilter("/contract/finish/*")
@RequiredArgsConstructor
public class ContractIdBranchFilter implements Filter {

    private final JwtService jwtTokenService;
    private final RoleHierarchy roleHierarchy;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String idBranchFromToken = jwtTokenService.getIdBranchFromToken();

        if (idBranchFromToken == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            SecurityExpressionRoot root = new SecurityExpressionRoot(auth) {};
            root.setRoleHierarchy(roleHierarchy);

            if (!root.hasRole("ROLE_CLIENT_MANAGER")) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: insufficient role.");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}

