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
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DashboardDocumentSpecification {
    public static Specification<Document> byClientId(String clientId) {
        return (root, query, criteriaBuilder) -> {
            if (clientId == null || clientId.isEmpty()) {
                return criteriaBuilder.conjunction(); // Retorna uma condição "true" se a lista for vazia
            }

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
            return root.get("type").in(documentTypes);
        };
    }

    public static Specification<Document> byResponsibleIds(List<String> responsibleIds) {
        return (root, query, criteriaBuilder) -> {
            if (responsibleIds == null || responsibleIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

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
            return root.get("status").in(statuses);
        };
    }

    public static Specification<Document> byTitles(List<String> titles) {
        return (root, query, criteriaBuilder) -> {
            if (titles == null || titles.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<Document, DocumentMatrix> documentMatrixJoin = root.join("documentMatrix");

            return documentMatrixJoin
                    .get("name")
                    .in(titles);
        };
    }

    // TODO specifications
    //      providerCnpjs
    //      contractIds
    //      employeeIds
    //      employeeCpfs
    //      employeeSituations
    //      documentDoesBlock
    //      documentValidity
    //      documentUploadDate
}
