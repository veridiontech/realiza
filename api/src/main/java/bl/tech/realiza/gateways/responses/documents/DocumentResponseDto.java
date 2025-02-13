package bl.tech.realiza.gateways.responses.documents;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DocumentResponseDto {
    // document
    private String idDocumentation;
    private String title;
    private Document.Status status;
    private String documentation;
    private String fileName;
    private String fileContentType;
    private byte[] fileData;
    private LocalDateTime creationDate;
    private Date versionDate;
    private Date expirationDate;
    private List<DocumentMatrix> selectedDocumentsEnterprise;
    private List<DocumentMatrix> selectedDocumentsPersonal;
    private List<DocumentMatrix> selectedDocumentsTrainning;
    private List<DocumentMatrix> selectedDocumentsService;
    private List<DocumentMatrix> nonSelectedDocumentsEnterprise;
    private List<DocumentMatrix> nonSelectedDocumentsPersonal;
    private List<DocumentMatrix> nonSelectedDocumentsTrainning;
    private List<DocumentMatrix> nonSelectedDocumentsService;

    // branch
    private String branch;

    // client
    private String client;

    // employee
    private String employee;

    // subcontractor
    private String subcontractor;

    // supplier
    private String supplier;

    // contract
    private String contract;
}
