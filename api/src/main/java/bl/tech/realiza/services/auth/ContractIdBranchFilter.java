package bl.tech.realiza.services.auth;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;

@WebFilter("/contract/finish/*")
@RequiredArgsConstructor
public class ContractIdBranchFilter implements Filter {

    private final JwtService jwtTokenService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {


    HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Recupera o idContract da URL ou dos parâmetros
        String idContract = httpRequest.getParameter("idContract");

        // Verifica se o token JWT foi passado na requisição
        String idBranchFromToken = jwtTokenService.getIdBranchFromToken();

        if (idBranchFromToken == null) {
            // Verifica a role do usuário e se a role permite ignorar a verificação do idClient
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            boolean isHigherRole = user.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_REALIZA_BASIC") || auth.getAuthority().equals("ROLE_REALIZA_PLUS"));

            if (!isHigherRole) {
                // Se o usuário não tem a role superior, nega o acesso
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Invalid client ID.");
                return;
            }
        }

        // Se a verificação passar (ou se a role for superior), continue com a execução da requisição
        chain.doFilter(request, response);
    }
}
