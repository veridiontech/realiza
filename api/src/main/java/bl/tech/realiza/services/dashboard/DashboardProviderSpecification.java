package bl.tech.realiza.services.dashboard;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.*;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.employees.EmployeeBrazilian;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import bl.tech.realiza.domains.enums.DocumentValidityEnum;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.user.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DashboardProviderSpecification {
    public static Specification<Provider> byClientId(String clientId) {
        return (root, query, criteriaBuilder) -> {
            if (clientId == null || clientId.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Root<ProviderSupplier> supplierRoot = criteriaBuilder.treat(root, ProviderSupplier.class);
            Root<ProviderSubcontractor> subcontractorRoot = criteriaBuilder.treat(root, ProviderSubcontractor.class);

            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = supplierRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, Branch> branchSupplierJoin = contractSupplierJoin.join("branch", JoinType.LEFT);
            Join<Branch, Client> branchClientSupplierJoin = branchSupplierJoin.join("client", JoinType.LEFT);

            Predicate supplierClientPredicate = criteriaBuilder
                    .equal(branchClientSupplierJoin.get("idClient"), clientId);

            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = subcontractorRoot.join("contractsSubcontractor", JoinType.LEFT);
            Join<ContractProviderSubcontractor, ContractProviderSupplier> contractSubToSupplierJoin = contractSubcontractorJoin.join("contractProviderSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, Branch> branchSubcontractorJoin = contractSubToSupplierJoin.join("branch", JoinType.LEFT);
            Join<Branch, Client> branchClientSubcontractorJoin = branchSubcontractorJoin.join("client", JoinType.LEFT);

            Predicate subcontractorClientPredicate = criteriaBuilder
                    .equal(branchClientSubcontractorJoin.get("idClient"), clientId);

            return criteriaBuilder.or(supplierClientPredicate, subcontractorClientPredicate);
        };
    }

    public static Specification<Provider> byBranchIds(List<String> branchIds) {
        return (root, query, criteriaBuilder) -> {
            if (branchIds == null || branchIds.isEmpty()) {
                return criteriaBuilder.conjunction(); // Retorna uma condição "true" se a lista for vazia
            }
            assert query != null;
            query.distinct(true);

            Root<ProviderSupplier> supplierRoot = criteriaBuilder.treat(root, ProviderSupplier.class);
            Root<ProviderSubcontractor> subcontractorRoot = criteriaBuilder.treat(root, ProviderSubcontractor.class);

            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = supplierRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, Branch> branchSupplierJoin = contractSupplierJoin.join("branch", JoinType.LEFT);

            Predicate supplierBranchPredicate = branchSupplierJoin
                    .get("idBranch")
                    .in(branchIds);

            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = subcontractorRoot.join("contractsSubcontractor", JoinType.LEFT);
            Join<ContractProviderSubcontractor, ContractProviderSupplier> contractSubToSupplierJoin = contractSubcontractorJoin.join("contractProviderSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, Branch> branchSubcontractorJoin = contractSubToSupplierJoin.join("branch", JoinType.LEFT);

            Predicate subcontractorBranchPredicate = branchSubcontractorJoin
                    .get("idBranch")
                    .in(branchIds);

            return criteriaBuilder.or(supplierBranchPredicate, subcontractorBranchPredicate);
        };
    }

    public static Specification<Provider> byProviderIds(List<String> providerIds) {
        return (root, query, criteriaBuilder) -> {
            if (providerIds == null || providerIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);
            return root.get("idProvider").in(providerIds);
        };
    }

    public static Specification<Provider> byDocumentTypes(List<String> documentTypes) {
        return (root, query, criteriaBuilder) -> {
            if (documentTypes == null || documentTypes.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Root<ProviderSupplier> supplierRoot = criteriaBuilder.treat(root, ProviderSupplier.class);
            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = supplierRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, ContractDocument> contractSupplierDocumentJoin = contractSupplierJoin.join("contractDocuments", JoinType.LEFT);
            Join<ContractDocument, Document> documentSupplierJoin = contractSupplierDocumentJoin.join("document", JoinType.LEFT);
            Predicate supplierPredicate = documentSupplierJoin
                    .get("type")
                    .in(documentTypes);

            Root<ProviderSubcontractor> subcontractorRoot = criteriaBuilder.treat(root, ProviderSubcontractor.class);
            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = subcontractorRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSubcontractor, ContractDocument> contractSubcontractorDocumentJoin = contractSubcontractorJoin.join("contractDocuments", JoinType.LEFT);
            Join<ContractDocument, Document> documentSubcontractorJoin = contractSubcontractorDocumentJoin.join("document", JoinType.LEFT);
            Predicate subcontractorPredicate = documentSubcontractorJoin
                    .get("type")
                    .in(documentTypes);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<Provider> byResponsibleIds(List<String> responsibleIds) {
        return (root, query, criteriaBuilder) -> {
            if (responsibleIds == null || responsibleIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Root<ProviderSupplier> supplierRoot = criteriaBuilder.treat(root, ProviderSupplier.class);
            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = supplierRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, User> userSupplierJoin = contractSupplierJoin.join("responsible", JoinType.LEFT);
            Predicate supplierPredicate = userSupplierJoin
                    .get("idUser")
                    .in(responsibleIds);

            Root<ProviderSubcontractor> subcontractorRoot = criteriaBuilder.treat(root, ProviderSubcontractor.class);
            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = subcontractorRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSubcontractor, User> userSubcontractorJoin = contractSubcontractorJoin.join("responsible", JoinType.LEFT);
            Predicate subcontractorPredicate = userSubcontractorJoin
                    .get("idUser")
                    .in(responsibleIds);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<Provider> byContractStatus(List<ContractStatusEnum> status) {
        return (root, query, criteriaBuilder) -> {
            List<ContractStatusEnum> finalActiveContract = new ArrayList<>();
            if (status == null || status.isEmpty()) {
                finalActiveContract.add(ContractStatusEnum.ACTIVE);
            } else {
                finalActiveContract.addAll(status);
            }
            assert query != null;
            query.distinct(true);

            Root<ProviderSupplier> supplierRoot = criteriaBuilder.treat(root, ProviderSupplier.class);
            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = supplierRoot.join("contractsSupplier", JoinType.LEFT);
            Predicate supplierPredicate = contractSupplierJoin
                    .get("status")
                    .in(status);

            Root<ProviderSubcontractor> subcontractorRoot = criteriaBuilder.treat(root, ProviderSubcontractor.class);
            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = subcontractorRoot.join("contractsSupplier", JoinType.LEFT);
            Predicate subcontractorPredicate = contractSubcontractorJoin
                    .get("status")
                    .in(status);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<Provider> byDocumentStatuses(List<Document.Status> statuses) {
        return (root, query, criteriaBuilder) -> {
            if (statuses == null || statuses.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Root<ProviderSupplier> supplierRoot = criteriaBuilder.treat(root, ProviderSupplier.class);
            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = supplierRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, ContractDocument> contractSupplierDocumentJoin = contractSupplierJoin.join("contractDocuments", JoinType.LEFT);
            Join<ContractDocument, Document> documentSupplierJoin = contractSupplierDocumentJoin.join("document", JoinType.LEFT);
            Predicate supplierPredicate = documentSupplierJoin
                    .get("status")
                    .in(statuses);

            Root<ProviderSubcontractor> subcontractorRoot = criteriaBuilder.treat(root, ProviderSubcontractor.class);
            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = subcontractorRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSubcontractor, ContractDocument> contractSubcontractorDocumentJoin = contractSubcontractorJoin.join("contractDocuments", JoinType.LEFT);
            Join<ContractDocument, Document> documentSubcontractorJoin = contractSubcontractorDocumentJoin.join("document", JoinType.LEFT);
            Predicate subcontractorPredicate = documentSubcontractorJoin
                    .get("status")
                    .in(statuses);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<Provider> byDocumentTitles(List<String> titles) {
        return (root, query, criteriaBuilder) -> {
            if (titles == null || titles.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Root<ProviderSupplier> supplierRoot = criteriaBuilder.treat(root, ProviderSupplier.class);
            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = supplierRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, ContractDocument> contractSupplierDocumentJoin = contractSupplierJoin.join("contractDocuments", JoinType.LEFT);
            Join<ContractDocument, Document> documentSupplierJoin = contractSupplierDocumentJoin.join("document", JoinType.LEFT);
            Join<Document, DocumentMatrix> documentMatrixSupplierJoin = documentSupplierJoin.join("documentMatrix", JoinType.LEFT);
            Predicate supplierPredicate = documentMatrixSupplierJoin
                    .get("name")
                    .in(titles);

            Root<ProviderSubcontractor> subcontractorRoot = criteriaBuilder.treat(root, ProviderSubcontractor.class);
            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = subcontractorRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSubcontractor, ContractDocument> contractSubcontractorDocumentJoin = contractSubcontractorJoin.join("contractDocuments", JoinType.LEFT);
            Join<ContractDocument, Document> documentSubcontractorJoin = contractSubcontractorDocumentJoin.join("document", JoinType.LEFT);
            Join<Document, DocumentMatrix> documentMatrixSubcontractorJoin = documentSubcontractorJoin.join("documentMatrix", JoinType.LEFT);
            Predicate subcontractorPredicate = documentMatrixSubcontractorJoin
                    .get("name")
                    .in(titles);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<Provider> byProviderCnpjs(List<String> providerCnpjs) {
        return (root, query, criteriaBuilder) -> {
            if (providerCnpjs == null || providerCnpjs.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            return root.get("cnpj").in(providerCnpjs);
        };
    }

    public static Specification<Provider> byContractIds(List<String> contractIds) {
        return (root, query, criteriaBuilder) -> {
            if (contractIds == null || contractIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Root<ProviderSupplier> supplierRoot = criteriaBuilder.treat(root, ProviderSupplier.class);
            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = supplierRoot.join("contractsSupplier", JoinType.LEFT);
            Predicate supplierPredicate = contractSupplierJoin
                    .get("idContract")
                    .in(contractIds);

            Root<ProviderSubcontractor> subcontractorRoot = criteriaBuilder.treat(root, ProviderSubcontractor.class);
            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = subcontractorRoot.join("contractsSupplier", JoinType.LEFT);
            Predicate subcontractorPredicate = contractSubcontractorJoin
                    .get("idContract")
                    .in(contractIds);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<Provider> byEmployeeIds(List<String> employeeIds) {
        return (root, query, criteriaBuilder) -> {
            if (employeeIds == null || employeeIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Root<ProviderSupplier> supplierRoot = criteriaBuilder.treat(root, ProviderSupplier.class);
            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = supplierRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, ContractEmployee> contractSupplierEmployeeJoin = contractSupplierJoin.join("employeeContracts", JoinType.LEFT);
            Join<ContractEmployee, Employee> employeeSupplierJoin = contractSupplierEmployeeJoin.join("employee", JoinType.LEFT);
            Predicate supplierPredicate = employeeSupplierJoin
                    .get("idEmployee")
                    .in(employeeIds);

            Root<ProviderSubcontractor> subcontractorRoot = criteriaBuilder.treat(root, ProviderSubcontractor.class);
            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = subcontractorRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSubcontractor, ContractEmployee> contractSubcontractorEmployeeJoin = contractSubcontractorJoin.join("employeeContracts", JoinType.LEFT);
            Join<ContractEmployee, Employee> employeeSubcontractorJoin = contractSubcontractorEmployeeJoin.join("employee", JoinType.LEFT);
            Predicate subcontractorPredicate = employeeSubcontractorJoin
                    .get("idEmployee")
                    .in(employeeIds);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<Provider> byEmployeeCpfs(List<String> employeeCpfs) {
        return (root, query, criteriaBuilder) -> {
            if (employeeCpfs == null || employeeCpfs.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Root<ProviderSupplier> supplierRoot = criteriaBuilder.treat(root, ProviderSupplier.class);
            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = supplierRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, ContractEmployee> contractSupplierEmployeeJoin = contractSupplierJoin.join("employeeContracts", JoinType.LEFT);
            Join<ContractEmployee, Employee> employeeSupplierJoin = contractSupplierEmployeeJoin.join("employee", JoinType.LEFT);
            Predicate supplierPredicate = criteriaBuilder
                    .treat(employeeSupplierJoin, EmployeeBrazilian.class)
                    .get("cpf")
                    .in(employeeCpfs);

            Root<ProviderSubcontractor> subcontractorRoot = criteriaBuilder.treat(root, ProviderSubcontractor.class);
            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = subcontractorRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSubcontractor, ContractEmployee> contractSubcontractorEmployeeJoin = contractSubcontractorJoin.join("employeeContracts", JoinType.LEFT);
            Join<ContractEmployee, Employee> employeeSubcontractorJoin = contractSubcontractorEmployeeJoin.join("employee", JoinType.LEFT);
            Predicate subcontractorPredicate = criteriaBuilder
                    .treat(employeeSubcontractorJoin, EmployeeBrazilian.class)
                    .get("cpf")
                    .in(employeeCpfs);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<Provider> byEmployeeSituations(List<Employee.Situation> employeeSituations) {
        return (root, query, criteriaBuilder) -> {
            if (employeeSituations == null || employeeSituations.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Root<ProviderSupplier> supplierRoot = criteriaBuilder.treat(root, ProviderSupplier.class);
            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = supplierRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, ContractEmployee> contractSupplierEmployeeJoin = contractSupplierJoin.join("employeeContracts", JoinType.LEFT);
            Join<ContractEmployee, Employee> employeeSupplierJoin = contractSupplierEmployeeJoin.join("employee", JoinType.LEFT);
            Predicate supplierPredicate = employeeSupplierJoin
                    .get("situation")
                    .in(employeeSituations);

            Root<ProviderSubcontractor> subcontractorRoot = criteriaBuilder.treat(root, ProviderSubcontractor.class);
            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = subcontractorRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSubcontractor, ContractEmployee> contractSubcontractorEmployeeJoin = contractSubcontractorJoin.join("employeeContracts", JoinType.LEFT);
            Join<ContractEmployee, Employee> employeeSubcontractorJoin = contractSubcontractorEmployeeJoin.join("employee", JoinType.LEFT);
            Predicate subcontractorPredicate = employeeSubcontractorJoin
                    .get("situation")
                    .in(employeeSituations);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<Provider> byDoesBlock(List<Boolean> doesBlockList) {
        return (root, query, criteriaBuilder) -> {
            if (doesBlockList == null || doesBlockList.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Root<ProviderSupplier> supplierRoot = criteriaBuilder.treat(root, ProviderSupplier.class);
            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = supplierRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, ContractDocument> contractSupplierDocumentJoin = contractSupplierJoin.join("contractDocuments", JoinType.LEFT);
            Join<ContractDocument, Document> documentSupplierJoin = contractSupplierDocumentJoin.join("document", JoinType.LEFT);
            Predicate supplierPredicate = criteriaBuilder
                    .treat(documentSupplierJoin, DocumentProviderSupplier.class)
                    .get("doesBLock")
                    .in(doesBlockList);

            Predicate employeeSupplierPredicate = criteriaBuilder
                    .treat(documentSupplierJoin, DocumentEmployee.class)
                    .get("doesBLock")
                    .in(doesBlockList);

            Root<ProviderSubcontractor> subcontractorRoot = criteriaBuilder.treat(root, ProviderSubcontractor.class);
            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = subcontractorRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSubcontractor, ContractDocument> contractSubcontractorDocumentJoin = contractSubcontractorJoin.join("contractDocuments", JoinType.LEFT);
            Join<ContractDocument, Document> documentSubcontractorJoin = contractSubcontractorDocumentJoin.join("document", JoinType.LEFT);
            Predicate subcontractorPredicate = criteriaBuilder
                    .treat(documentSubcontractorJoin, DocumentProviderSubcontractor.class)
                    .get("doesBLock")
                    .in(doesBlockList);

            Predicate employeeSubcontractorPredicate = criteriaBuilder
                    .treat(documentSubcontractorJoin, DocumentEmployee.class)
                    .get("doesBLock")
                    .in(doesBlockList);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate, employeeSupplierPredicate, employeeSubcontractorPredicate);
        };
    }

    public static Specification<Provider> byValidities(List<DocumentValidityEnum> validity) {
        return (root, query, criteriaBuilder) -> {
            if (validity == null || validity.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Root<ProviderSupplier> supplierRoot = criteriaBuilder.treat(root, ProviderSupplier.class);
            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = supplierRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, ContractDocument> contractSupplierDocumentJoin = contractSupplierJoin.join("contractDocuments", JoinType.LEFT);
            Join<ContractDocument, Document> documentSupplierJoin = contractSupplierDocumentJoin.join("document", JoinType.LEFT);
            Predicate supplierPredicate = documentSupplierJoin
                    .get("validity")
                    .in(validity);

            Root<ProviderSubcontractor> subcontractorRoot = criteriaBuilder.treat(root, ProviderSubcontractor.class);
            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = subcontractorRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSubcontractor, ContractDocument> contractSubcontractorDocumentJoin = contractSubcontractorJoin.join("contractDocuments", JoinType.LEFT);
            Join<ContractDocument, Document> documentSubcontractorJoin = contractSubcontractorDocumentJoin.join("document", JoinType.LEFT);
            Predicate subcontractorPredicate = documentSubcontractorJoin
                    .get("validity")
                    .in(validity);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<Provider> byUploadDates(List<LocalDate> uploadDates) {
        return (root, query, criteriaBuilder) -> {
            if (uploadDates == null || uploadDates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            LocalDateTime startDate = null;
            LocalDateTime endDate = null;
            if (uploadDates.size() == 1) {
                startDate = uploadDates.get(0).atStartOfDay();
                endDate = uploadDates.get(0).atTime(LocalTime.MAX);
            } else if (uploadDates.size() == 2) {
                List<LocalDate> sortedList = uploadDates.stream().sorted(LocalDate::compareTo).toList();
                startDate = sortedList.get(0).atStartOfDay();
                endDate = sortedList.get(1).atTime(LocalTime.MAX);
            } else {
                return criteriaBuilder.conjunction();
            }

            Root<ProviderSupplier> supplierRoot = criteriaBuilder.treat(root, ProviderSupplier.class);
            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = supplierRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, ContractDocument> contractSupplierDocumentJoin = contractSupplierJoin.join("contractDocuments", JoinType.LEFT);
            Join<ContractDocument, Document> documentSupplierJoin = contractSupplierDocumentJoin.join("document", JoinType.LEFT);
            Predicate supplierPredicate = criteriaBuilder
                    .between(documentSupplierJoin.get("versionDate"), startDate, endDate);

            Root<ProviderSubcontractor> subcontractorRoot = criteriaBuilder.treat(root, ProviderSubcontractor.class);
            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = subcontractorRoot.join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSubcontractor, ContractDocument> contractSubcontractorDocumentJoin = contractSubcontractorJoin.join("contractDocuments", JoinType.LEFT);
            Join<ContractDocument, Document> documentSubcontractorJoin = contractSubcontractorDocumentJoin.join("document", JoinType.LEFT);
            Predicate subcontractorPredicate = criteriaBuilder
                    .between(documentSubcontractorJoin.get("versionDate"), startDate, endDate);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<Provider> byIsActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> {
            if (isActive == null) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);
            return criteriaBuilder.equal(root.get("isActive"), isActive);
        };
    }
}
