package bl.tech.realiza.services.auth;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.user.*;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final String SECRET_KEY;
    private final long EXPIRATION_TIME;
    private final ProviderSupplierRepository providerSupplierRepository;

    public JwtService(Dotenv dotenv1, Dotenv dotenv, BranchRepository branchRepository, ProviderSupplierRepository providerSupplierRepository) {
        this.SECRET_KEY = dotenv.get("SECRET_KEY");
        if (this.SECRET_KEY == null || this.SECRET_KEY.isEmpty()) {
            throw new IllegalArgumentException("SECRET_KEY is missing or empty in the environment variables.");
        }

        String expirationTime = dotenv.get("EXPIRATION_TIME");
        if (expirationTime == null || expirationTime.isEmpty()) {
            throw new IllegalArgumentException("EXPIRATION_TIME is missing or empty in the environment variables.");
        }

        try {
            this.EXPIRATION_TIME = Long.parseLong(expirationTime);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("EXPIRATION_TIME must be a valid long value.");
        }
        this.providerSupplierRepository = providerSupplierRepository;
    }

    public String generateTokenManager(UserManager user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("idUser", user.getIdUser());
        claims.put("cpf", user.getCpf());
        claims.put("description", user.getDescription());
        claims.put("password", user.getPassword());
        claims.put("position", user.getPosition());
        claims.put("role", user.getRole().name());
        claims.put("firstName", user.getFirstName());
        claims.put("surname", user.getSurname());
        claims.put("email", user.getEmail());
        claims.put("telephone", user.getTelephone());
        claims.put("cellphone", user.getCellphone());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String generateTokenClient(UserClient user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("idUser", user.getIdUser());
        claims.put("idBranch",user.getBranch().getIdBranch());
        claims.put("cpf", user.getCpf());
        claims.put("description", user.getDescription());
        claims.put("password", user.getPassword());
        claims.put("position", user.getPosition());
        claims.put("role", user.getRole().name());
        claims.put("firstName", user.getFirstName());
        claims.put("surname", user.getSurname());
        claims.put("email", user.getEmail());
        claims.put("profilePicture", user.getProfilePicture() != null
                ? user.getProfilePicture().getUrl()
                : null);
        claims.put("telephone", user.getTelephone());
        claims.put("cellphone", user.getCellphone());
        claims.put("idClient", user.getBranch().getClient().getIdClient());
        claims.put("clientTradeName", user.getBranch().getClient().getTradeName());
        claims.put("clientCorporateName", user.getBranch().getClient().getCorporateName());
        if (!user.getBranchesAccess().stream().map(Branch::getIdBranch).toList().isEmpty()) {
            claims.put("branchAccess", user.getBranchesAccess().stream().map(Branch::getIdBranch).toList());
        } else {
            user.getBranchesAccess().add(user.getBranch());
            claims.put("branchAccess", user.getBranchesAccess().stream().map(Branch::getIdBranch).toList());
        }
        claims.put("contractAccess", user.getContractsAccess().stream().map(Contract::getIdContract).toList());
        claims.put("admin", user.getProfile() != null
                ? user.getProfile().getAdmin()
                : false);
        claims.put("viewer", user.getProfile() != null
                ? user.getProfile().getViewer()
                : true);
        claims.put("manager", user.getProfile() != null
                ? user.getProfile().getManager()
                : false);
        claims.put("inspector", user.getProfile() != null
                ? user.getProfile().getInspector()
                : false);
        claims.put("documentViewer", user.getProfile() != null
                ? user.getProfile().getDocumentViewer()
                : false);
        claims.put("registrationUser", user.getProfile() != null
                ? user.getProfile().getRegistrationUser()
                : false);
        claims.put("registrationContract", user.getProfile() != null
                ? user.getProfile().getRegistrationContract()
                : false);
        claims.put("laboral", user.getProfile() != null
                ? user.getProfile().getLaboral()
                : false);
        claims.put("workplaceSafety", user.getProfile() != null
                ? user.getProfile().getWorkplaceSafety()
                : false);
        claims.put("registrationAndCertificates", user.getProfile() != null
                ? user.getProfile().getRegistrationAndCertificates()
                : false);
        claims.put("general", user.getProfile() != null
                ? user.getProfile().getGeneral()
                : false);
        claims.put("health", user.getProfile() != null
                ? user.getProfile().getHealth()
                : false);
        claims.put("environment", user.getProfile() != null
                ? user.getProfile().getEnvironment()
                : false);
        claims.put("concierge", user.getProfile() != null
                ? user.getProfile().getConcierge()
                : false);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String generateTokenSupplier(UserProviderSupplier user) {
        Map<String, Object> claims = new HashMap<>();

        ProviderSupplier supplier = providerSupplierRepository.findById(user.getProviderSupplier().getIdProvider()).orElseThrow(() -> new NotFoundException("Branch not found"));
        List<String> branchesIds = supplier.getBranches().stream().map(Branch::getIdBranch).toList();

        claims.put("idUser", user.getIdUser());
        claims.put("idSupplier",user.getProviderSupplier().getIdProvider());
        claims.put("cpf", user.getCpf());
        claims.put("description", user.getDescription());
        claims.put("password", user.getPassword());
        claims.put("position", user.getPosition());
        claims.put("role", user.getRole().name());
        claims.put("firstName", user.getFirstName());
        claims.put("surname", user.getSurname());
        claims.put("email", user.getEmail());
        claims.put("profilePicture", user.getProfilePicture() != null
                ? user.getProfilePicture().getUrl()
                : null);
        claims.put("telephone", user.getTelephone());
        claims.put("cellphone", user.getCellphone());
        claims.put("branches", branchesIds);
        claims.put("idClient", user.getProviderSupplier().getBranches().get(0).getClient().getIdClient());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String generateTokenSubcontractor(UserProviderSubcontractor user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("idUser", user.getIdUser());
        claims.put("idSubcontractor",user.getProviderSubcontractor().getIdProvider());
        claims.put("cpf", user.getCpf());
        claims.put("description", user.getDescription());
        claims.put("password", user.getPassword());
        claims.put("position", user.getPosition());
        claims.put("role", user.getRole().name());
        claims.put("firstName", user.getFirstName());
        claims.put("surname", user.getSurname());
        claims.put("email", user.getEmail());
        claims.put("profilePicture", user.getProfilePicture() != null
                ? user.getProfilePicture().getUrl()
                : null);
        claims.put("telephone", user.getTelephone());
        claims.put("cellphone", user.getCellphone());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("idUser", user.getIdUser());
        claims.put("cpf", user.getCpf());
        claims.put("description", user.getDescription());
        claims.put("password", user.getPassword());
        claims.put("position", user.getPosition());
        claims.put("role", user.getRole().name());
        claims.put("firstName", user.getFirstName());
        claims.put("surname", user.getSurname());
        claims.put("email", user.getEmail());
        claims.put("profilePicture", user.getProfilePicture() != null
                ? user.getProfilePicture().getUrl()
                : null);
        claims.put("telephone", user.getTelephone());
        claims.put("cellphone", user.getCellphone());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public UserResponseDto extractAllClaims(String token) {
        // Parse do JWT para obter as claims
        Map<String, Object> claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        // Extração dos branches (branches IDs) do token
        Object branchesObject = claims.get("branches");
        List<String> branchesIds = (branchesObject instanceof List<?>)
                ? ((List<?>) branchesObject).stream()
                .map(Object::toString)
                .collect(Collectors.toList())
                : List.of(); // Se não existir, retorna uma lista vazia

        // Extração do branchesAccess do token
        Object branchesAccessObject = claims.get("branchAccess");
        List<String> branchesAccessIds = (branchesAccessObject instanceof List<?>)
                ? ((List<?>) branchesAccessObject).stream()
                .map(Object::toString)
                .collect(Collectors.toList())
                : List.of(); // Se não existir, retorna uma lista vazia

        // Extração do contractAccess do token
        Object contractAccessObject = claims.get("contractAccess");
        List<String> contractAccessIds = (contractAccessObject instanceof List<?>)
                ? ((List<?>) contractAccessObject).stream()
                .map(Object::toString)
                .collect(Collectors.toList())
                : List.of(); // Se não existir, retorna uma lista vazia

        // Extração das claims de perfil (como laboral, manager, etc.)
        Map<String, Boolean> profileClaims = new HashMap<>();
        String[] profileKeys = {"admin",
                "viewer",
                "manager",
                "inspector",
                "documentViewer",
                "registrationUser",
                "registrationContract",
                "laboral",
                "workplaceSafety",
                "registrationAndCertificates",
                "general",
                "health",
                "environment",
                "concierge"};

        for (String key : profileKeys) {
            profileClaims.put(key, (Boolean) claims.getOrDefault(key, null));
        }

        // Construção do DTO com todas as claims extraídas, incluindo as novas
        return UserResponseDto.builder()
                .idUser((String) claims.getOrDefault("idUser", ""))
                .branch((String) claims.getOrDefault("idBranch", ""))
                .supplier((String) claims.getOrDefault("idSupplier", ""))
                .subcontractor((String) claims.getOrDefault("idSubcontractor", ""))
                .cpf((String) claims.getOrDefault("cpf", ""))
                .position((String) claims.getOrDefault("position", ""))
                .role(User.Role.valueOf((String) claims.getOrDefault("role", "")))
                .firstName((String) claims.getOrDefault("firstName", ""))
                .surname((String) claims.getOrDefault("surname", ""))
                .email((String) claims.getOrDefault("email", ""))
                .telephone((String) claims.getOrDefault("telephone", ""))
                .cellphone((String) claims.getOrDefault("cellphone", ""))
                .idClient((String) claims.getOrDefault("idClient", ""))
                .tradeName((String) claims.getOrDefault("clientTradeName", ""))
                .corporateName((String) claims.getOrDefault("clientCorporateName", ""))
                .branches(branchesIds)
                .branchAccess(branchesAccessIds)
                .contractAccess(contractAccessIds)
                .corporateName((String) claims.getOrDefault("clientCorporateName", "")) // Corrigido duplicação
                .admin(profileClaims.get("admin"))
                .viewer(profileClaims.get("viewer"))
                .manager(profileClaims.get("manager"))
                .inspector(profileClaims.get("inspector"))
                .documentViewer(profileClaims.get("documentViewer"))
                .registrationUser(profileClaims.get("registrationUser"))
                .registrationContract(profileClaims.get("registrationContract"))
                .laboral(profileClaims.get("laboral"))
                .workplaceSafety(profileClaims.get("workplaceSafety"))
                .registrationAndCertificates(profileClaims.get("registrationAndCertificates"))
                .general(profileClaims.get("general"))
                .health(profileClaims.get("health"))
                .environment(profileClaims.get("environment"))
                .concierge(profileClaims.get("concierge"))
                .build();
    }

    public String getIdBranchFromToken() {
        // Pega o token do contexto de segurança (esse token será passado em cada requisição)
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();

            // Extraia o idClient a partir do token
            return claims.get("idBranch", String.class);  // A chave "idClient" precisa ser a mesma que você usa no seu JWT
        } catch (Exception e) {
            return null;  // Se ocorrer algum erro ao ler o token, retorna null
        }
    }

    public static UserResponseDto getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserResponseDto user) {
            return user;
        }
        return null;
    }

    public static String getAuthenticatedUserId() {
        UserResponseDto user = getAuthenticatedUser();
        return user != null ? user.getIdUser() : null;
    }

    public String getTokenFromRequest() {
        String token = null;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            token = attributes.getRequest().getHeader("Authorization");
        }

        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }

        return null;
    }
}
