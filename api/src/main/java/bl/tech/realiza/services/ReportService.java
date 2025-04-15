package bl.tech.realiza.services;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ProviderSupplierRepository providerRepository;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final EmployeeRepository employeeRepository;
    private final DocumentBranchRepository documentBranchRepository;
    private final DocumentProviderSupplierRepository documentProviderSupplierRepository;
    private final DocumentProviderSubcontractorRepository documentProviderSubcontractorRepository;

    public Long countSuppliersByBranch(String branchId) {
        return providerRepository.countByBranches_IdBranch(branchId);
    }

    public Long countSubcontractorsByBranch(String branchId) {
        List<String> suppliersIds = providerRepository.findAllByBranches_IdBranch(branchId)
                .stream().map(ProviderSupplier::getIdProvider).toList();

        return suppliersIds.isEmpty() ? 0L : providerSubcontractorRepository.countByProviderSupplier_IdProviderIn(suppliersIds);
    }

    public Long countSubcontractorsBySupplier(String supplierId) {
        return providerSubcontractorRepository.countByProviderSupplier_IdProvider(supplierId);
    }

    public Long countEmployeesByEnterprise(String enterpriseId, Provider.Company companyDegree) {
        switch (companyDegree) {
            case CLIENT -> {
                return employeeRepository.countAllByBranch_IdBranch(enterpriseId);
            }
            case SUPPLIER -> {
                return employeeRepository.countAllBySupplier_IdProvider(enterpriseId);
            }
            case SUBCONTRACTOR -> {
                return employeeRepository.countAllBySubcontract_IdProvider(enterpriseId);
            }
            default -> {
                throw new BadRequestException("Invalid company degree");
            }
        }
    }

    public Long countAdherenceByEnterprise(String enterpriseId, Provider.Company companyDegree) {
        switch (companyDegree) {
            case CLIENT -> {
                return documentBranchRepository.countByBranch_IdBranchAndDocumentationIsNotNullAndDocumentMatrix_SubGroup_Group_GroupName(enterpriseId, "Documento empresa")
                        / documentBranchRepository.countByBranch_IdBranchAndDocumentMatrix_SubGroup_Group_GroupName(enterpriseId, "Documento empresa");
            }
            case SUPPLIER -> {
                return documentProviderSupplierRepository.countByProviderSupplier_IdProviderAndDocumentationIsNotNullAndDocumentMatrix_SubGroup_Group_GroupName(enterpriseId, "Documento empresa")
                        / documentProviderSupplierRepository.countByProviderSupplier_IdProviderAndDocumentMatrix_SubGroup_Group_GroupName(enterpriseId, "Documento empresa");
            }
            case SUBCONTRACTOR -> {
                return documentProviderSubcontractorRepository.countByProviderSubcontractor_IdProviderAndDocumentationIsNotNullAndDocumentMatrix_SubGroup_Group_GroupName(enterpriseId, "Documento empresa")
                        / documentProviderSubcontractorRepository.countByProviderSubcontractor_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(enterpriseId, "Documento empresa", true);
            }
            default -> {
                throw new BadRequestException("Invalid company degree");
            }
        }
    }

    public Long countAccordanceByEnterprise(String enterpriseId, Provider.Company companyDegree) {
        switch (companyDegree) {
            case CLIENT -> {
                return documentBranchRepository.countByBranch_IdBranchAndStatusAndDocumentMatrix_SubGroup_Group_GroupName(enterpriseId, Document.Status.APROVADO, "Documento empresa")
                        / documentBranchRepository.countByBranch_IdBranchAndDocumentMatrix_SubGroup_Group_GroupName(enterpriseId, "Documento empresa");
            }
            case SUPPLIER -> {
                return documentProviderSupplierRepository.countByProviderSupplier_IdProviderAndStatusAndDocumentMatrix_SubGroup_Group_GroupName(enterpriseId, Document.Status.APROVADO, "Documento empresa")
                        / documentProviderSupplierRepository.countByProviderSupplier_IdProviderAndDocumentMatrix_SubGroup_Group_GroupName(enterpriseId, "Documento empresa");
            }
            case SUBCONTRACTOR -> {
                return documentProviderSubcontractorRepository.countByProviderSubcontractor_IdProviderAndStatusAndDocumentMatrix_SubGroup_Group_GroupName(enterpriseId, Document.Status.APROVADO, "Documento empresa")
                        / documentProviderSubcontractorRepository.countByProviderSubcontractor_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(enterpriseId, "Documento empresa", true);
            }
            default -> {
                throw new BadRequestException("Invalid company degree");
            }
        }
    }
}
