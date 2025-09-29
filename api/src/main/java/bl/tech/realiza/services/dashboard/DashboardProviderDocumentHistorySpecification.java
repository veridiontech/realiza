package bl.tech.realiza.services.dashboard;

import bl.tech.realiza.domains.auditLogs.dashboard.DocumentStatusHistory;
import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.ContractDocument;
import bl.tech.realiza.domains.contract.ContractEmployee;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
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
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class DashboardProviderDocumentHistorySpecification {
    public static Specification<DocumentStatusHistory> byClientId(String clientId) {
        return (root, query, criteriaBuilder) -> {
            if (clientId == null || clientId.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Join<DocumentStatusHistory, Provider> providerJoin = root
                    .join("provider", JoinType.LEFT);

            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSupplier.class)
                    .join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, Branch> branchSupplierJoin = contractSupplierJoin.join("branch", JoinType.LEFT);
            Join<Branch, Client> branchClientSupplierJoin = branchSupplierJoin.join("client", JoinType.LEFT);

            Predicate supplierClientPredicate = criteriaBuilder
                    .equal(branchClientSupplierJoin.get("idClient"), clientId);

            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSubcontractor.class)
                    .join("contractsSubcontractor", JoinType.LEFT);
            Join<ContractProviderSubcontractor, ContractProviderSupplier> contractSubToSupplierJoin = contractSubcontractorJoin.join("contractProviderSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, Branch> branchSubcontractorJoin = contractSubToSupplierJoin.join("branch", JoinType.LEFT);
            Join<Branch, Client> branchClientSubcontractorJoin = branchSubcontractorJoin.join("client", JoinType.LEFT);

            Predicate subcontractorClientPredicate = criteriaBuilder
                    .equal(branchClientSubcontractorJoin.get("idClient"), clientId);

            return criteriaBuilder.or(supplierClientPredicate, subcontractorClientPredicate);
        };
    }

    public static Specification<DocumentStatusHistory> byBranchIds(List<String> branchIds) {
        return (root, query, criteriaBuilder) -> {
            if (branchIds == null || branchIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Join<DocumentStatusHistory, Provider> providerJoin = root
                    .join("provider", JoinType.LEFT);

            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSupplier.class)
                    .join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, Branch> branchSupplierJoin = contractSupplierJoin.join("branch", JoinType.LEFT);
            Predicate supplierBranchPredicate = branchSupplierJoin
                    .get("idBranch")
                    .in(branchIds);

            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSubcontractor.class)
                    .join("contractsSubcontractor", JoinType.LEFT);
            Join<ContractProviderSubcontractor, ContractProviderSupplier> contractSubToSupplierJoin = contractSubcontractorJoin.join("contractProviderSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, Branch> branchSubcontractorJoin = contractSubToSupplierJoin.join("branch", JoinType.LEFT);
            Predicate subcontractorBranchPredicate = branchSubcontractorJoin
                    .get("idBranch")
                    .in(branchIds);

            return criteriaBuilder.or(supplierBranchPredicate, subcontractorBranchPredicate);
        };
    }

    public static Specification<DocumentStatusHistory> byProviderIds(List<String> providerIds) {
        return (root, query, criteriaBuilder) -> {
            if (providerIds == null || providerIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);
            return root.join("provider").get("idProvider").in(providerIds);
        };
    }

    public static Specification<DocumentStatusHistory> byDocumentTypes(List<String> documentTypes) {
        return (root, query, criteriaBuilder) -> {
            if (documentTypes == null || documentTypes.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            return root.get("documentType").in(documentTypes);
        };
    }

    public static Specification<DocumentStatusHistory> byResponsibleIds(List<String> responsibleIds) {
        return (root, query, criteriaBuilder) -> {
            if (responsibleIds == null || responsibleIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Join<DocumentStatusHistory, Provider> providerJoin = root
                    .join("provider", JoinType.LEFT);

            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSupplier.class)
                    .join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, User> userSupplierJoin = contractSupplierJoin.join("responsible", JoinType.LEFT);
            Predicate supplierPredicate = userSupplierJoin
                    .get("idUser")
                    .in(responsibleIds);

            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSubcontractor.class)
                    .join("contractsSubcontractor", JoinType.LEFT);
            Join<ContractProviderSubcontractor, User> userSubcontractorJoin = contractSubcontractorJoin.join("responsible", JoinType.LEFT);
            Predicate subcontractorPredicate = userSubcontractorJoin
                    .get("idUser")
                    .in(responsibleIds);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<DocumentStatusHistory> byContractStatus(List<ContractStatusEnum> status) {
        return (root, query, criteriaBuilder) -> {
            List<ContractStatusEnum> finalActiveContract = new ArrayList<>();
            if (status == null || status.isEmpty()) {
                finalActiveContract.add(ContractStatusEnum.ACTIVE);
            } else {
                finalActiveContract.addAll(status);
            }
            assert query != null;
            query.distinct(true);

            Join<DocumentStatusHistory, Provider> providerJoin = root
                    .join("provider", JoinType.LEFT);

            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSupplier.class)
                    .join("contractsSupplier", JoinType.LEFT);
            Predicate supplierPredicate = contractSupplierJoin
                    .get("status")
                    .in(finalActiveContract);

            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSubcontractor.class)
                    .join("contractsSubcontractor", JoinType.LEFT);
            Predicate subcontractorPredicate = contractSubcontractorJoin
                    .get("status")
                    .in(finalActiveContract);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<DocumentStatusHistory> byDocumentStatuses(List<Document.Status> statuses) {
        return (root, query, criteriaBuilder) -> {
            if (statuses == null || statuses.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Join<DocumentStatusHistory, Provider> providerJoin = root
                    .join("provider", JoinType.LEFT);

            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSupplier.class)
                    .join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, ContractDocument> contractSupplierDocumentJoin = contractSupplierJoin.join("contractDocuments", JoinType.LEFT);
            Join<ContractDocument, Document> documentSupplierJoin = contractSupplierDocumentJoin.join("document", JoinType.LEFT);
            Predicate supplierPredicate = documentSupplierJoin
                    .get("status")
                    .in(statuses);

            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSubcontractor.class)
                    .join("contractsSubcontractor", JoinType.LEFT);
            Join<ContractProviderSubcontractor, ContractDocument> contractSubcontractorDocumentJoin = contractSubcontractorJoin.join("contractDocuments", JoinType.LEFT);
            Join<ContractDocument, Document> documentSubcontractorJoin = contractSubcontractorDocumentJoin.join("document", JoinType.LEFT);
            Predicate subcontractorPredicate = documentSubcontractorJoin
                    .get("status")
                    .in(statuses);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<DocumentStatusHistory> byDocumentTitles(List<String> titles) {
        return (root, query, criteriaBuilder) -> {
            if (titles == null || titles.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Join<DocumentStatusHistory, Provider> providerJoin = root
                    .join("provider", JoinType.LEFT);

            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSupplier.class)
                    .join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, ContractDocument> contractSupplierDocumentJoin = contractSupplierJoin.join("contractDocuments", JoinType.LEFT);
            Join<ContractDocument, Document> documentSupplierJoin = contractSupplierDocumentJoin.join("document", JoinType.LEFT);
            Join<Document, DocumentMatrix> documentMatrixSupplierJoin = documentSupplierJoin.join("documentMatrix", JoinType.LEFT);
            Predicate supplierPredicate = documentMatrixSupplierJoin
                    .get("name")
                    .in(titles);

            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSubcontractor.class)
                    .join("contractsSubcontractor", JoinType.LEFT);
            Join<ContractProviderSubcontractor, ContractDocument> contractSubcontractorDocumentJoin = contractSubcontractorJoin.join("contractDocuments", JoinType.LEFT);
            Join<ContractDocument, Document> documentSubcontractorJoin = contractSubcontractorDocumentJoin.join("document", JoinType.LEFT);
            Join<Document, DocumentMatrix> documentMatrixSubcontractorJoin = documentSubcontractorJoin.join("documentMatrix", JoinType.LEFT);
            Predicate subcontractorPredicate = documentMatrixSubcontractorJoin
                    .get("name")
                    .in(titles);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<DocumentStatusHistory> byProviderCnpjs(List<String> providerCnpjs) {
        return (root, query, criteriaBuilder) -> {
            if (providerCnpjs == null || providerCnpjs.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            return root.join("provider").get("cnpj").in(providerCnpjs);
        };
    }

    public static Specification<DocumentStatusHistory> byContractIds(List<String> contractIds) {
        return (root, query, criteriaBuilder) -> {
            if (contractIds == null || contractIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Join<DocumentStatusHistory, Provider> providerJoin = root
                    .join("provider", JoinType.LEFT);

            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSupplier.class)
                    .join("contractsSupplier", JoinType.LEFT);
            Predicate supplierPredicate = contractSupplierJoin
                    .get("idContract")
                    .in(contractIds);

            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSubcontractor.class)
                    .join("contractsSubcontractor", JoinType.LEFT);
            Predicate subcontractorPredicate = contractSubcontractorJoin
                    .get("idContract")
                    .in(contractIds);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<DocumentStatusHistory> byEmployeeIds(List<String> employeeIds) {
        return (root, query, criteriaBuilder) -> {
            if (employeeIds == null || employeeIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Join<DocumentStatusHistory, Provider> providerJoin = root
                    .join("provider", JoinType.LEFT);

            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSupplier.class)
                    .join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, ContractEmployee> contractSupplierEmployeeJoin = contractSupplierJoin.join("employeeContracts", JoinType.LEFT);
            Join<ContractEmployee, Employee> employeeSupplierJoin = contractSupplierEmployeeJoin.join("employee", JoinType.LEFT);
            Predicate supplierPredicate = employeeSupplierJoin
                    .get("idEmployee")
                    .in(employeeIds);

            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSubcontractor.class)
                    .join("contractsSubcontractor", JoinType.LEFT);
            Join<ContractProviderSubcontractor, ContractEmployee> contractSubcontractorEmployeeJoin = contractSubcontractorJoin.join("employeeContracts", JoinType.LEFT);
            Join<ContractEmployee, Employee> employeeSubcontractorJoin = contractSubcontractorEmployeeJoin.join("employee", JoinType.LEFT);
            Predicate subcontractorPredicate = employeeSubcontractorJoin
                    .get("idEmployee")
                    .in(employeeIds);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<DocumentStatusHistory> byEmployeeCpfs(List<String> employeeCpfs) {
        return (root, query, criteriaBuilder) -> {
            if (employeeCpfs == null || employeeCpfs.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Join<DocumentStatusHistory, Provider> providerJoin = root
                    .join("provider", JoinType.LEFT);

            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSupplier.class)
                    .join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, ContractEmployee> contractSupplierEmployeeJoin = contractSupplierJoin.join("employeeContracts", JoinType.LEFT);
            Join<ContractEmployee, Employee> employeeSupplierJoin = contractSupplierEmployeeJoin.join("employee", JoinType.LEFT);
            Predicate supplierPredicate = criteriaBuilder
                    .treat(employeeSupplierJoin, EmployeeBrazilian.class)
                    .get("cpf")
                    .in(employeeCpfs);

            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSubcontractor.class)
                    .join("contractsSubcontractor", JoinType.LEFT);
            Join<ContractProviderSubcontractor, ContractEmployee> contractSubcontractorEmployeeJoin = contractSubcontractorJoin.join("employeeContracts", JoinType.LEFT);
            Join<ContractEmployee, Employee> employeeSubcontractorJoin = contractSubcontractorEmployeeJoin.join("employee", JoinType.LEFT);
            Predicate subcontractorPredicate = criteriaBuilder
                    .treat(employeeSubcontractorJoin, EmployeeBrazilian.class)
                    .get("cpf")
                    .in(employeeCpfs);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<DocumentStatusHistory> byEmployeeSituations(List<Employee.Situation> employeeSituations) {
        return (root, query, criteriaBuilder) -> {
            if (employeeSituations == null || employeeSituations.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Join<DocumentStatusHistory, Provider> providerJoin = root
                    .join("provider", JoinType.LEFT);

            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSupplier.class)
                    .join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, ContractEmployee> contractSupplierEmployeeJoin = contractSupplierJoin.join("employeeContracts", JoinType.LEFT);
            Join<ContractEmployee, Employee> employeeSupplierJoin = contractSupplierEmployeeJoin.join("employee", JoinType.LEFT);
            Predicate supplierPredicate = employeeSupplierJoin
                    .get("situation")
                    .in(employeeSituations);

            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSubcontractor.class)
                    .join("contractsSubcontractor", JoinType.LEFT);
            Join<ContractProviderSubcontractor, ContractEmployee> contractSubcontractorEmployeeJoin = contractSubcontractorJoin.join("employeeContracts", JoinType.LEFT);
            Join<ContractEmployee, Employee> employeeSubcontractorJoin = contractSubcontractorEmployeeJoin.join("employee", JoinType.LEFT);
            Predicate subcontractorPredicate = employeeSubcontractorJoin
                    .get("situation")
                    .in(employeeSituations);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<DocumentStatusHistory> byDoesBlock(List<Boolean> doesBlockList) {
        return (root, query, criteriaBuilder) -> {
            if (doesBlockList == null || doesBlockList.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Join<DocumentStatusHistory, Provider> providerJoin = root
                    .join("provider", JoinType.LEFT);

            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSupplier.class)
                    .join("contractsSupplier", JoinType.LEFT);
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

            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSubcontractor.class)
                    .join("contractsSubcontractor", JoinType.LEFT);
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

    public static Specification<DocumentStatusHistory> byValidities(List<DocumentValidityEnum> validity) {
        return (root, query, criteriaBuilder) -> {
            if (validity == null || validity.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Join<DocumentStatusHistory, Provider> providerJoin = root
                    .join("provider", JoinType.LEFT);

            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSupplier.class)
                    .join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, ContractDocument> contractSupplierDocumentJoin = contractSupplierJoin.join("contractDocuments", JoinType.LEFT);
            Join<ContractDocument, Document> documentSupplierJoin = contractSupplierDocumentJoin.join("document", JoinType.LEFT);
            Predicate supplierPredicate = documentSupplierJoin
                    .get("validity")
                    .in(validity);

            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSubcontractor.class)
                    .join("contractsSubcontractor", JoinType.LEFT);
            Join<ContractProviderSubcontractor, ContractDocument> contractSubcontractorDocumentJoin = contractSubcontractorJoin.join("contractDocuments", JoinType.LEFT);
            Join<ContractDocument, Document> documentSubcontractorJoin = contractSubcontractorDocumentJoin.join("document", JoinType.LEFT);
            Predicate subcontractorPredicate = documentSubcontractorJoin
                    .get("validity")
                    .in(validity);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<DocumentStatusHistory> byUploadDates(List<LocalDate> uploadDates) {
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

            Join<DocumentStatusHistory, Provider> providerJoin = root
                    .join("provider", JoinType.LEFT);

            Join<ProviderSupplier, ContractProviderSupplier> contractSupplierJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSupplier.class)
                    .join("contractsSupplier", JoinType.LEFT);
            Join<ContractProviderSupplier, ContractDocument> contractSupplierDocumentJoin = contractSupplierJoin.join("contractDocuments", JoinType.LEFT);
            Join<ContractDocument, Document> documentSupplierJoin = contractSupplierDocumentJoin.join("document", JoinType.LEFT);
            Predicate supplierPredicate = criteriaBuilder
                    .between(documentSupplierJoin.get("versionDate"), startDate, endDate);

            Join<ProviderSubcontractor, ContractProviderSubcontractor> contractSubcontractorJoin = criteriaBuilder
                    .treat(providerJoin, ProviderSubcontractor.class)
                    .join("contractsSubcontractor", JoinType.LEFT);
            Join<ContractProviderSubcontractor, ContractDocument> contractSubcontractorDocumentJoin = contractSubcontractorJoin.join("contractDocuments", JoinType.LEFT);
            Join<ContractDocument, Document> documentSubcontractorJoin = contractSubcontractorDocumentJoin.join("document", JoinType.LEFT);
            Predicate subcontractorPredicate = criteriaBuilder
                    .between(documentSubcontractorJoin.get("versionDate"), startDate, endDate);

            return criteriaBuilder.or(supplierPredicate, subcontractorPredicate);
        };
    }

    public static Specification<DocumentStatusHistory> byIsActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> {
            if (isActive == null) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);
            return criteriaBuilder.equal(root.join("provider").get("isActive"), isActive);
        };
    }

    public static Specification<DocumentStatusHistory> byHistoryPeriodBetween(YearMonth start, YearMonth end) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("historyPeriod"), start, end);
    }
}
