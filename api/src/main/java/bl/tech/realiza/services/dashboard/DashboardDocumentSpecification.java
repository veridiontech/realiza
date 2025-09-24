package bl.tech.realiza.services.dashboard;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.ContractDocument;
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
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardDocumentSpecification {
    public static Specification<Document> byClientId(String clientId) {
        return (root, query, criteriaBuilder) -> {
            if (clientId == null || clientId.isEmpty()) {
                return criteriaBuilder.conjunction(); // Retorna uma condição "true" se a lista for vazia
            }
            assert query != null;
            query.distinct(true);

            Join<Document, ContractDocument> contractDocumentJoin = root.join("contractDocuments");
            Join<ContractDocument, Contract> contractJoin = contractDocumentJoin.join("contract");

            Join<Contract, ContractProviderSupplier> supplierJoin = criteriaBuilder
                    .treat(contractJoin, ContractProviderSupplier.class)
                    .join("branch", JoinType.LEFT)
                    .join("client", JoinType.LEFT);

            Join<Contract, ContractProviderSubcontractor> subcontractorJoin = criteriaBuilder
                    .treat(contractJoin, ContractProviderSubcontractor.class)
                    .join("contractProviderSupplier")
                    .join("branch", JoinType.LEFT)
                    .join("client", JoinType.LEFT);

            Predicate supplierClientPredicate = criteriaBuilder
                    .equal(supplierJoin.get("idClient"), clientId);

            Predicate subcontractorClientPredicate = criteriaBuilder
                    .equal(subcontractorJoin.get("idClient"), clientId);

            return criteriaBuilder.or(supplierClientPredicate, subcontractorClientPredicate);
        };
    }

    public static Specification<Document> byBranchIds(List<String> branchIds) {
        return (root, query, criteriaBuilder) -> {
            if (branchIds == null || branchIds.isEmpty()) {
                return criteriaBuilder.conjunction(); // Retorna uma condição "true" se a lista for vazia
            }
            assert query != null;
            query.distinct(true);

            // Join: Document -> ContractDocument -> Contract
            Join<Document, ContractDocument> contractDocumentJoin = root.join("contractDocuments");
            Join<ContractDocument, Contract> contractJoin = contractDocumentJoin.join("contract");

            // Precisamos tratar os dois tipos de contrato: Supplier e Subcontractor
            // Join para ContractProviderSupplier
            Join<Contract, ContractProviderSupplier> supplierJoin = criteriaBuilder
                    .treat(contractJoin, ContractProviderSupplier.class)
                    .join("branch", JoinType.LEFT);

            // Join para ContractProviderSubcontractor
            Join<Contract, ContractProviderSubcontractor> subcontractorJoin = criteriaBuilder
                    .treat(contractJoin, ContractProviderSubcontractor.class)
                    .join("contractProviderSupplier")
                    .join("branch", JoinType.LEFT);

            // Cria as cláusulas 'IN' para ambos os caminhos e as une com um OR
            Predicate supplierBranchPredicate = supplierJoin
                    .get("idBranch")
                    .in(branchIds);
            Predicate subcontractorBranchPredicate = subcontractorJoin
                    .get("idBranch")
                    .in(branchIds);

            return criteriaBuilder.or(supplierBranchPredicate, subcontractorBranchPredicate);
        };
    }

    public static Specification<Document> byProviderIds(List<String> providerIds) {
        return (root, query, criteriaBuilder) -> {
            if (providerIds == null || providerIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Predicate supplierPredicate = criteriaBuilder
                    .treat(root, DocumentProviderSupplier.class)
                    .get("providerSupplier")
                    .get("idProvider")
                    .in(providerIds);

            Predicate subPredicate = criteriaBuilder
                    .treat(root, DocumentProviderSubcontractor.class)
                    .get("providerSubcontractor")
                    .get("idProvider")
                    .in(providerIds);

            Join<DocumentEmployee, Employee> employeeJoin = criteriaBuilder
                    .treat(root, DocumentEmployee.class)
                    .join("employee", JoinType.LEFT);

            Predicate empSupplierPredicate = employeeJoin
                    .join("supplier", JoinType.LEFT)
                    .get("idProvider")
                    .in(providerIds);

            Predicate empSubPredicate = employeeJoin
                    .join("subcontract", JoinType.LEFT)
                    .get("idProvider")
                    .in(providerIds);

            return criteriaBuilder.or(supplierPredicate, subPredicate, empSupplierPredicate, empSubPredicate);
        };
    }

    public static Specification<Document> byDocumentTypes(List<String> documentTypes) {
        return (root, query, criteriaBuilder) -> {
            if (documentTypes == null || documentTypes.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);
            return root.get("type").in(documentTypes);
        };
    }

    public static Specification<Document> byResponsibleIds(List<String> responsibleIds) {
        return (root, query, criteriaBuilder) -> {
            if (responsibleIds == null || responsibleIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Join<Document, ContractDocument> contractDocumentJoin = root.join("contractDocuments");
            Join<ContractDocument, Contract> contractJoin = contractDocumentJoin.join("contract");

            Join<Contract, ContractProviderSupplier> supplierJoin = criteriaBuilder
                    .treat(contractJoin, ContractProviderSupplier.class)
                    .join("responsible", JoinType.LEFT);

            Join<Contract, ContractProviderSubcontractor> subcontractorJoin = criteriaBuilder
                    .treat(contractJoin, ContractProviderSubcontractor.class)
                    .join("contractProviderSupplier")
                    .join("responsible", JoinType.LEFT);

            Predicate supplierResponsiblePredicate = supplierJoin
                    .get("idUser")
                    .in(responsibleIds);
            Predicate subcontractorResponsiblePredicate = subcontractorJoin
                    .get("idUser")
                    .in(responsibleIds);

            return criteriaBuilder.or(supplierResponsiblePredicate, subcontractorResponsiblePredicate);
        };
    }

    public static Specification<Document> byContractStatus(List<ContractStatusEnum> status) {
        return (root, query, criteriaBuilder) -> {
            List<ContractStatusEnum> finalActiveContract = new ArrayList<>();
            if (status == null || status.isEmpty()) {
                finalActiveContract.add(ContractStatusEnum.ACTIVE);
            } else {
                finalActiveContract.addAll(status);
            }
            assert query != null;
            query.distinct(true);

            Join<Document, ContractDocument> contractDocumentJoin = root.join("contractDocuments");
            Join<ContractDocument, Contract> contractJoin = contractDocumentJoin.join("contract");


            return contractJoin
                    .get("status")
                    .in(finalActiveContract);
        };
    }

    public static Specification<Document> byStatuses(List<Document.Status> statuses) {
        return (root, query, criteriaBuilder) -> {
            if (statuses == null || statuses.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);
            return root.get("status").in(statuses);
        };
    }

    public static Specification<Document> byTitles(List<String> titles) {
        return (root, query, criteriaBuilder) -> {
            if (titles == null || titles.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);
            Join<Document, DocumentMatrix> documentMatrixJoin = root.join("documentMatrix");

            return documentMatrixJoin
                    .get("name")
                    .in(titles);
        };
    }

    public static Specification<Document> byProviderCnpjs(List<String> providerCnpjs) {
        return (root, query, criteriaBuilder) -> {
            if (providerCnpjs == null || providerCnpjs.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Predicate supplierPredicate = criteriaBuilder
                    .treat(root, DocumentProviderSupplier.class)
                    .get("providerSupplier")
                    .get("cnpj")
                    .in(providerCnpjs);

            Predicate subPredicate = criteriaBuilder
                    .treat(root, DocumentProviderSubcontractor.class)
                    .get("providerSubcontractor")
                    .get("cnpj")
                    .in(providerCnpjs);

            Join<DocumentEmployee, Employee> employeeJoin = criteriaBuilder
                    .treat(root, DocumentEmployee.class)
                    .join("employee", JoinType.LEFT);

            Predicate empSupplierPredicate = employeeJoin
                    .join("supplier", JoinType.LEFT)
                    .get("cnpj")
                    .in(providerCnpjs);

            Predicate empSubPredicate = employeeJoin
                    .join("subcontract", JoinType.LEFT)
                    .get("cnpj")
                    .in(providerCnpjs);

            return criteriaBuilder.or(supplierPredicate, subPredicate, empSupplierPredicate, empSubPredicate);
        };
    }

    public static Specification<Document> byContractIds(List<String> contractIds) {
        return (root, query, criteriaBuilder) -> {
            if (contractIds == null || contractIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Join<Document, ContractDocument> contractDocumentJoin = root.join("contractDocuments");
            Join<ContractDocument, Contract> contractJoin = contractDocumentJoin.join("contract");


            return contractJoin
                    .get("idContract")
                    .in(contractIds);
        };
    }

    public static Specification<Document> byEmployeeIds(List<String> employeeIds) {
        return (root, query, criteriaBuilder) -> {
            if (employeeIds == null || employeeIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            return criteriaBuilder
                    .treat(root, DocumentEmployee.class)
                    .join("employee", JoinType.LEFT)
                    .get("idEmployee")
                    .in(employeeIds);
        };
    }

    public static Specification<Document> byEmployeeCpfs(List<String> employeeCpfs) {
        return (root, query, criteriaBuilder) -> {
            if (employeeCpfs == null || employeeCpfs.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            Join<DocumentEmployee, Employee> employeeJoin = criteriaBuilder
                    .treat(root, DocumentEmployee.class)
                    .join("employee", JoinType.LEFT);

            return criteriaBuilder
                    .treat(employeeJoin, EmployeeBrazilian.class)
                    .get("cpf")
                    .in(employeeCpfs);
        };
    }

    public static Specification<Document> byEmployeeSituations(List<Employee.Situation> employeeSituations) {
        return (root, query, criteriaBuilder) -> {
            if (employeeSituations == null || employeeSituations.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);

            return criteriaBuilder
                    .treat(root, DocumentEmployee.class)
                    .join("employee", JoinType.LEFT)
                    .get("situation")
                    .in(employeeSituations);
        };
    }

    public static Specification<Document> byDoesBlock(List<Boolean> doesBlockList) {
        return (root, query, criteriaBuilder) -> {
            if (doesBlockList == null || doesBlockList.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);
            return root.get("doesBlock").in(doesBlockList);
        };
    }

    public static Specification<Document> byValidities(List<DocumentValidityEnum> validity) {
        return (root, query, criteriaBuilder) -> {
            if (validity == null || validity.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);
            return root.get("validity").in(validity);
        };
    }

    public static Specification<Document> byUploadDates(List<LocalDate> uploadDates) {
        return (root, query, criteriaBuilder) -> {
            if (uploadDates == null || uploadDates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            assert query != null;
            query.distinct(true);
            if (uploadDates.size() == 1) {
                LocalDateTime startDate = uploadDates.get(0).atStartOfDay();
                LocalDateTime endDate = uploadDates.get(0).atTime(LocalTime.MAX);
                return criteriaBuilder.between(root.get("versionDate"), startDate, endDate);
            } else if (uploadDates.size() == 2) {
                List<LocalDate> sortedList = uploadDates.stream().sorted(LocalDate::compareTo).toList();
                LocalDateTime startDate = sortedList.get(0).atStartOfDay();
                LocalDateTime endDate = sortedList.get(1).atTime(LocalTime.MAX);

                return criteriaBuilder.between(root.get("versionDate"), startDate, endDate);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Document> byConformingIsTrue() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("conforming"));
    }

    public static Specification<Document> byAdherenceIsTrue() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("adherent"));
    }
}
