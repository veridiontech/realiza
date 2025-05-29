package bl.tech.realiza.services.setup;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.activity.ActivityDocuments;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityDocumentRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.matrix.DocumentMatrixRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.usecases.interfaces.contracts.CrudServiceType;
import bl.tech.realiza.usecases.interfaces.contracts.activity.CrudActivity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SetupService {

    private final CrudServiceType crudServiceType;
    private final BranchRepository branchRepository;
    private final CrudActivity crudActivity;
    private final DocumentMatrixRepository documentMatrixRepository;
    private final DocumentBranchRepository documentBranchRepository;
    private final ActivityRepository activityRepository;
    private final ActivityDocumentRepository activityDocumentRepository;
    private final DocumentProviderSupplierRepository documentProviderSupplierRepository;
    private final DocumentProviderSubcontractorRepository documentProviderSubcontractorRepository;

    public void setupNewClient(Client savedClient) {
        Branch baseBranch = branchRepository.save(
                Branch.builder()
                        .name(savedClient.getCorporateName() + " Base")
                        .cnpj(savedClient.getCnpj())
                        .cep(savedClient.getCep())
                        .state(savedClient.getState())
                        .city(savedClient.getCity())
                        .email(savedClient.getEmail())
                        .telephone(savedClient.getTelephone())
                        .address(savedClient.getAddress())
                        .number(savedClient.getNumber())
                        .client(savedClient)
                        .build()
        );

        crudServiceType.transferFromRepoToClient(savedClient.getIdClient());

        documentBranchRepository.saveAll(
                documentMatrixRepository.findAll()
                        .stream()
                        .map(documentMatrix -> DocumentBranch.builder()
                                .title(documentMatrix.getName())
                                .type(documentMatrix.getType())
                                .status(Document.Status.PENDENTE)
                                .isActive(true)
                                .branch(baseBranch)
                                .documentMatrix(documentMatrix)
                                .build())
                        .collect(Collectors.toList()));

        crudServiceType.transferFromClientToBranch(savedClient.getIdClient(), baseBranch.getIdBranch());
        crudActivity.transferFromRepo(baseBranch.getIdBranch());
    }

    public void setupBranch(Branch baseBranch) {
        crudServiceType.transferFromClientToBranch(baseBranch.getClient().getIdClient(), baseBranch.getIdBranch());

        documentBranchRepository.saveAll(
                documentMatrixRepository.findAll()
                        .stream()
                        .map(documentMatrix -> DocumentBranch.builder()
                                .title(documentMatrix.getName())
                                .type(documentMatrix.getType())
                                .status(Document.Status.PENDENTE)
                                .isActive(true)
                                .branch(baseBranch)
                                .documentMatrix(documentMatrix)
                                .build())
                        .collect(Collectors.toList()));

        crudActivity.transferFromRepo(baseBranch.getIdBranch());
    }

    public void setupContractSupplier(ContractProviderSupplier contractProviderSupplier, List<String> activitiesId) {
        List<Activity> activities = new ArrayList<>(List.of());
        List<String> idDocuments = new ArrayList<>(List.of());
        List<DocumentBranch> documentBranch;
        List<DocumentProviderSupplier> documentProviderSupplier = new ArrayList<>(List.of());

        if (contractProviderSupplier.getHse() && !activitiesId.isEmpty()) {
            activities = activityRepository.findAllById(activitiesId);
            if (activities.isEmpty()) {
                throw new NotFoundException("Activities not found");
            }

            activities.forEach(
                    activity -> {
                        List<ActivityDocuments> activityDocumentsList = activityDocumentRepository.findAllByActivity_IdActivity(activity.getIdActivity());
                        activityDocumentsList.forEach(
                                activityDocument -> idDocuments.add(activityDocument.getDocumentBranch().getIdDocumentation())
                        );
                    }
            );
        }

        contractProviderSupplier.setActivities(!activities.isEmpty()
                ? activities
                : contractProviderSupplier.getActivities());

        documentBranch = documentBranchRepository.findAllById(idDocuments);

        ProviderSupplier finalNewProviderSupplier = contractProviderSupplier.getProviderSupplier();
        documentBranch.forEach(
                document -> documentProviderSupplier.add(DocumentProviderSupplier.builder()
                        .title(document.getTitle())
                        .status(Document.Status.PENDENTE)
                        .type(document.getType())
                        .isActive(true)
                        .documentMatrix(document.getDocumentMatrix())
                        .providerSupplier(finalNewProviderSupplier)
                        .build()));

        documentProviderSupplierRepository.saveAll(documentProviderSupplier);
    }

    public void setupContractSubcontractor(ContractProviderSubcontractor contractProviderSubcontractor, List<String> activitiesId) {
        List<Activity> activities = new ArrayList<>(List.of());;
        List<DocumentProviderSupplier> documentSupplier;
        List<String> idDocuments = new ArrayList<>(List.of());
        List<DocumentProviderSubcontractor> documentProviderSubcontractor = new ArrayList<>(List.of());

        if (contractProviderSubcontractor.getHse() && !activitiesId.isEmpty()) {
            activities = activityRepository.findAllById(activitiesId);
            if (activities.isEmpty()) {
                throw new NotFoundException("Activities not found");
            }

            activities.forEach(
                    activity -> {
                        List<ActivityDocuments> activityDocumentsList = activityDocumentRepository.findAllByActivity_IdActivity(activity.getIdActivity());
                        activityDocumentsList.forEach(
                                activityDocument -> idDocuments.add(activityDocument.getDocumentBranch().getIdDocumentation())
                        );
                    }
            );
        }

        contractProviderSubcontractor.setActivities(!activities.isEmpty()
                ? activities
                : contractProviderSubcontractor.getActivities());

        documentSupplier = documentProviderSupplierRepository.findAllById(idDocuments);

        ProviderSubcontractor finalNewProviderSubcontractor = contractProviderSubcontractor.getProviderSubcontractor();
        documentSupplier.forEach(
                document -> documentProviderSubcontractor.add(DocumentProviderSubcontractor.builder()
                        .title(document.getTitle())
                        .status(Document.Status.PENDENTE)
                        .type(document.getType())
                        .isActive(true)
                        .documentMatrix(document.getDocumentMatrix())
                        .providerSubcontractor(finalNewProviderSubcontractor)
                        .build()));

        documentProviderSubcontractorRepository.saveAll(documentProviderSubcontractor);
    }
}
