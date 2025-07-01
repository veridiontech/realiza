package bl.tech.realiza.usecases.impl.clients;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.serviceType.ServiceType;
import bl.tech.realiza.domains.enums.AuditLogActionsEnum;
import bl.tech.realiza.domains.enums.AuditLogTypeEnum;
import bl.tech.realiza.domains.ultragaz.Center;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityRepository;
import bl.tech.realiza.gateways.repositories.contracts.serviceType.ServiceTypeBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.ultragaz.CenterRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.clients.branch.BranchCreateRequestDto;
import bl.tech.realiza.gateways.responses.clients.BranchResponseDto;
import bl.tech.realiza.gateways.responses.clients.controlPanel.ControlPanelResponseDto;
import bl.tech.realiza.gateways.responses.clients.controlPanel.activity.ActivityControlPanelResponseDto;
import bl.tech.realiza.gateways.responses.clients.controlPanel.activity.ActivityRiskControlPanelResponseDto;
import bl.tech.realiza.gateways.responses.clients.controlPanel.document.DocumentControlPanelResponseDto;
import bl.tech.realiza.gateways.responses.clients.controlPanel.document.DocumentTypeControlPanelResponseDto;
import bl.tech.realiza.gateways.responses.clients.controlPanel.service.ServiceTypeControlPanelResponseDto;
import bl.tech.realiza.gateways.responses.clients.controlPanel.service.ServiceTypeRiskControlPanelResponseDto;
import bl.tech.realiza.gateways.responses.queue.SetupMessage;
import bl.tech.realiza.gateways.responses.ultragaz.CenterResponseDto;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.services.queue.SetupAsyncQueueProducer;
import bl.tech.realiza.usecases.impl.contracts.CrudServiceTypeImpl;
import bl.tech.realiza.usecases.impl.contracts.activity.CrudActivityImpl;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import bl.tech.realiza.usecases.interfaces.clients.CrudBranch;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static bl.tech.realiza.domains.enums.AuditLogActionsEnum.*;
import static bl.tech.realiza.domains.enums.AuditLogTypeEnum.*;

@Service
@RequiredArgsConstructor
public class CrudBranchImpl implements CrudBranch {

    private final BranchRepository branchRepository;
    private final ClientRepository clientRepository;
    private final CenterRepository centerRepository;
    private final AuditLogService auditLogServiceImpl;
    private final UserRepository userRepository;
    private final SetupAsyncQueueProducer setupQueueProducer;
    private final CrudServiceTypeImpl crudServiceTypeImpl;
    private final CrudActivityImpl crudActivityImpl;
    private final DocumentMatrixRepository documentMatrixRepository;
    private final DocumentBranchRepository documentBranchRepository;
    private final ActivityRepository activityRepository;
    private final ServiceTypeBranchRepository serviceTypeBranchRepository;

    @Override
    public BranchResponseDto save(BranchCreateRequestDto branchCreateRequestDto) {
        List<Center> center = List.of();
        Client client = null;

        if (branchCreateRequestDto.getClient() != null && !branchCreateRequestDto.getClient().isEmpty()) {
            client = clientRepository.findById(branchCreateRequestDto.getClient())
                        .orElseThrow(() -> new NotFoundException("Client not found"));
        }

        if (branchCreateRequestDto.getCenter() != null && !branchCreateRequestDto.getCenter().isEmpty()) {
            center = centerRepository.findAllById(branchCreateRequestDto.getCenter());
        }

        Branch savedBranch = branchRepository.save(Branch.builder()
                .name(branchCreateRequestDto.getName())
                .cnpj(branchCreateRequestDto.getCnpj())
                .cep(branchCreateRequestDto.getCep())
                .state(branchCreateRequestDto.getState())
                .city(branchCreateRequestDto.getCity())
                .email(branchCreateRequestDto.getEmail())
                .telephone(branchCreateRequestDto.getTelephone())
                .address(branchCreateRequestDto.getAddress())
                .number(branchCreateRequestDto.getNumber())
                .client(client)
                .center(center)
                .build());

        setupQueueProducer.sendSetup(new SetupMessage("NEW_BRANCH",
                null,
                savedBranch.getIdBranch(),
                null,
                null,
                null,
                null,
                null));

        if (JwtService.getAuthenticatedUserId() != null) {
            userRepository.findById(JwtService.getAuthenticatedUserId()).ifPresent(
                    userResponsible -> auditLogServiceImpl.createAuditLog(
                        savedBranch.getIdBranch(),
                        BRANCH,
                        userResponsible.getEmail() + " criou filial " + savedBranch.getName(),
                        null,
                        CREATE,
                        userResponsible.getIdUser()));
        }

        return BranchResponseDto.builder()
                .idBranch(savedBranch.getIdBranch())
                .name(savedBranch.getName())
                .cnpj(savedBranch.getCnpj())
                .cep(savedBranch.getCep())
                .state(savedBranch.getState())
                .city(savedBranch.getCity())
                .email(savedBranch.getEmail())
                .telephone(savedBranch.getTelephone())
                .address(savedBranch.getAddress())
                .number(savedBranch.getNumber())
                .client(savedBranch.getClient() != null ? savedBranch.getClient().getIdClient() : null)
                .center(!savedBranch.getCenter().isEmpty() ? savedBranch.getCenter().stream().map(
                        center1 -> CenterResponseDto.builder()
                                .idCenter(center1.getIdCenter())
                                .name(center1.getName())
                                .idMarket(center1.getMarket().getIdMarket())
                                .build()
                ).toList() : null)
                .build();
    }

    @Override
    public Optional<BranchResponseDto> findOne(String id) {
        Optional<Branch> branchOptional = branchRepository.findById(id);

        Branch branch = branchOptional.orElseThrow(() -> new NotFoundException("Branch not found"));

        return getBranchResponseDto(branch);
    }

    @Override
    public Page<BranchResponseDto> findAll(Pageable pageable) {
        Page<Branch> pageBranch = branchRepository.findAllByIsActiveIsTrue(pageable);

        return getBranchResponseDtos(pageBranch);
    }

    @Override
    public Page<BranchResponseDto> findAllByCenter(String idCenter, Pageable pageable) {
        return getBranchResponseDtos(branchRepository.findAllByCenter_IdCenter(idCenter, pageable));
    }

    @NotNull
    private Page<BranchResponseDto> getBranchResponseDtos(Page<Branch> pageBranch) {

        return pageBranch.map(
                branch -> {
                    String client = branch.getClient() != null ? branch.getClient().getIdClient() : null;
                    List<Center> center = !branch.getCenter().isEmpty() ? branch.getCenter().stream().map(
                            center1 -> Center.builder()
                                    .idCenter(center1.getIdCenter())
                                    .name(center1.getName())
                                    .build()
                    ).toList() : List.of();
                    return BranchResponseDto.builder()
                            .idBranch(branch.getIdBranch())
                            .name(branch.getName())
                            .cnpj(branch.getCnpj())
                            .cep(branch.getCep())
                            .state(branch.getState())
                            .city(branch.getCity())
                            .email(branch.getEmail())
                            .telephone(branch.getTelephone())
                            .address(branch.getAddress())
                            .number(branch.getNumber())
                            .client(client)
                            .center(!center.isEmpty() ? center.stream().map(
                                    center1 -> CenterResponseDto.builder()
                                            .idCenter(center1.getIdCenter())
                                            .name(center1.getName())
                                            .idMarket(center1.getMarket().getIdMarket())
                                            .build()
                            ).toList() : null)
                            .build();
                }
        );
    }

    @Override
    public Optional<BranchResponseDto> update(String id, BranchCreateRequestDto branchCreateRequestDto) {
        Optional<Branch> branchOptional = branchRepository.findById(id);

        Branch branch = branchOptional.orElseThrow(() -> new NotFoundException("Branch not found"));

        branch.setName(branchCreateRequestDto.getName() != null ? branchCreateRequestDto.getName() : branch.getName());
        branch.setCnpj(branchCreateRequestDto.getCnpj() != null ? branchCreateRequestDto.getCnpj() : branch.getCnpj());
        branch.setCep(branchCreateRequestDto.getCep() != null ? branchCreateRequestDto.getCep() : branch.getCep());
        branch.setState(branchCreateRequestDto.getState() != null ? branchCreateRequestDto.getState() : branch.getState());
        branch.setCity(branchCreateRequestDto.getCity() != null ? branchCreateRequestDto.getCity() : branch.getCity());
        branch.setEmail(branchCreateRequestDto.getEmail() != null ? branchCreateRequestDto.getEmail() : branch.getEmail());
        branch.setTelephone(branchCreateRequestDto.getTelephone() != null ? branchCreateRequestDto.getTelephone() : branch.getTelephone());
        branch.setAddress(branchCreateRequestDto.getAddress() != null ? branchCreateRequestDto.getAddress() : branch.getAddress());
        branch.setNumber(branchCreateRequestDto.getNumber() != null ? branchCreateRequestDto.getNumber() : branch.getNumber());

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLog(
                        branch.getIdBranch(),
                        BRANCH,
                        userResponsible.getEmail() + " atualizou filial " + branch.getName(),
                        null,
                        UPDATE,
                        userResponsible.getIdUser());
            }
        }

        Branch savedBranch = branchRepository.save(branch);

        return getBranchResponseDto(savedBranch);
    }

    @NotNull
    private Optional<BranchResponseDto> getBranchResponseDto(Branch savedBranch) {
        BranchResponseDto branchResponseDto = BranchResponseDto.builder()
                .idBranch(savedBranch.getIdBranch())
                .name(savedBranch.getName())
                .cnpj(savedBranch.getCnpj())
                .cep(savedBranch.getCep())
                .state(savedBranch.getState())
                .city(savedBranch.getCity())
                .email(savedBranch.getEmail())
                .telephone(savedBranch.getTelephone())
                .address(savedBranch.getAddress())
                .number(savedBranch.getNumber())
                .client(savedBranch.getClient().getIdClient())
                .center(savedBranch.getCenter().stream().map(
                        center -> CenterResponseDto.builder()
                                .idCenter(center.getIdCenter())
                                .name(center.getName())
                                .idMarket(center.getMarket().getIdMarket())
                                .build()
                ).toList())
                .build();

        return Optional.of(branchResponseDto);
    }

    @Override
    public void delete(String id) {
        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            Branch branch = branchRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Branch not found"));
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLog(
                        branch.getIdBranch(),
                        BRANCH,
                        userResponsible.getEmail() + " deletou filial " + branch.getName(),
                        null,
                        DELETE,
                        userResponsible.getIdUser());
            }
        }
        branchRepository.deleteById(id);
    }

    @Override
    public Page<BranchResponseDto> findAllByClient(String idSearch, Pageable pageable) {
        Page<Branch> pageBranch = branchRepository.findAllByClient_IdClientAndIsActiveIsTrue(idSearch, pageable);

        return getBranchResponseDtos(pageBranch);
    }

    @Override
    public ControlPanelResponseDto findControlPanelSummary(String branchId) {
        ControlPanelResponseDto response = ControlPanelResponseDto.builder().build();
        response.setDocuments(new ArrayList<>());
        response.setActivities(new ArrayList<>());
        response.setServices(new ArrayList<>());

        // documents
        List<DocumentControlPanelResponseDto> documents = documentBranchRepository.findAllControlPanelDocumentResponseDtoByBranch_IdBranch(branchId);
        Map<String, List<DocumentControlPanelResponseDto>> documentsByType = documents.stream()
                .collect(Collectors.groupingBy(DocumentControlPanelResponseDto::getType));

        documentsByType.forEach((type, docList) -> {
            docList.forEach(doc -> doc.setType(null));

            response.getDocuments().add(
                    DocumentTypeControlPanelResponseDto.builder()
                            .typeName(type)
                            .documents(docList)
                            .build()
            );
        });

        // activities
        List<ActivityControlPanelResponseDto> activities = activityRepository.findAllControlPanelActivityResponseDtoByBranch_IdBranch(branchId);
        Map<Activity.Risk, List<ActivityControlPanelResponseDto>> activitiesByRisk = activities.stream()
                .collect(Collectors.groupingBy(ActivityControlPanelResponseDto::getRisk));

        activitiesByRisk.forEach((risk, activityList) -> {
            activityList.forEach(activity -> activity.setRisk(null));

            response.getActivities().add(
                    ActivityRiskControlPanelResponseDto.builder()
                            .risk(risk)
                            .activities(activityList)
                            .build()
            );
        });

        // service types
        List<ServiceTypeControlPanelResponseDto> services = serviceTypeBranchRepository.findAllControlPanelActivityResponseDtoByBranch_IdBranch(branchId);
        Map<ServiceType.Risk, List<ServiceTypeControlPanelResponseDto>> servicesByRisk = services.stream()
                .collect(Collectors.groupingBy(ServiceTypeControlPanelResponseDto::getRisk));

        servicesByRisk.forEach((risk, serviceList) -> {
            serviceList.forEach(service -> service.setRisk(null));

            response.getServices().add(
                    ServiceTypeRiskControlPanelResponseDto.builder()
                            .risk(risk)
                            .services(serviceList)
                            .build()
            );
        });

        return response;
    }
}
