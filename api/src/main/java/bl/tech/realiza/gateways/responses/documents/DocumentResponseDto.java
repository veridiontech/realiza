package bl.tech.realiza.gateways.responses.documents;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.gateways.responses.services.DocumentIAValidationResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private List<DocumentMatrixResponseDto> selectedDocumentsEnterprise;
    private List<DocumentMatrixResponseDto> selectedDocumentsPersonal;
    private List<DocumentMatrixResponseDto> selectedDocumentsTraining;
    private List<DocumentMatrixResponseDto> selectedDocumentsService;
    private List<DocumentMatrixResponseDto> nonSelectedDocumentsEnterprise;
    private List<DocumentMatrixResponseDto> nonSelectedDocumentsPersonal;
    private List<DocumentMatrixResponseDto> nonSelectedDocumentsTraining;
    private List<DocumentMatrixResponseDto> nonSelectedDocumentsService;

    // gpt validation
    private DocumentIAValidationResponse documentIAValidationResponse;

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
