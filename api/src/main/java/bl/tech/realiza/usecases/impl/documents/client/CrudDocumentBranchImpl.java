package bl.tech.realiza.usecases.impl.documents.client;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.requests.documents.client.DocumentBranchRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.client.CrudDocumentBranch;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudDocumentBranchImpl implements CrudDocumentBranch {

    private final DocumentBranchRepository documentBranchRepository;
    private final BranchRepository branchRepository;
    private final FileRepository fileRepository;

    @Override
    public DocumentResponseDto save(DocumentBranchRequestDto documentBranchRequestDto, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Invalid file");
        }
        if (documentBranchRequestDto.getBranch() == null || documentBranchRequestDto.getBranch().isEmpty()) {
            throw new BadRequestException("Invalid branch");
        }

        FileDocument fileDocument = null;
        String fileDocumentId = null;

        Optional<Branch> branchOptional = branchRepository.findById(documentBranchRequestDto.getBranch());

        Branch branch = branchOptional.orElseThrow(() -> new EntityNotFoundException("Branch not found"));

        try {
            fileDocument = FileDocument.builder()
                    .name(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .data(file.getBytes())
                    .build();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new EntityNotFoundException(e);
        }

        FileDocument savedFileDocument= null;
        try {
            savedFileDocument = fileRepository.save(fileDocument);
            fileDocumentId = savedFileDocument.getIdDocumentAsString(); // Garante que seja uma String válida
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new EntityNotFoundException(e);
        }

        DocumentBranch newDocumentBranch = DocumentBranch.builder()
                .title(documentBranchRequestDto.getTitle())
                .status(documentBranchRequestDto.getStatus())
                .documentation(fileDocumentId)
                .branch(branch)
                .build();

        DocumentBranch savedDocumentBranch = documentBranchRepository.save(newDocumentBranch);

        DocumentResponseDto documentBranchResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentBranch.getIdDocumentation())
                .title(savedDocumentBranch.getTitle())
                .status(savedDocumentBranch.getStatus())
                .documentation(savedDocumentBranch.getDocumentation())
                .creationDate(savedDocumentBranch.getCreationDate())
                .branch(savedDocumentBranch.getBranch().getIdBranch())
                .build();

        return documentBranchResponse;
    }

    @Override
    public Optional<DocumentResponseDto> findOne(String id) {
        Optional<DocumentBranch> documentBranchOptional = documentBranchRepository.findById(id);

        DocumentBranch documentBranch = documentBranchOptional.orElseThrow(() -> new EntityNotFoundException("DocumentBranch not found"));

        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentBranch.getDocumentation()));

        FileDocument fileDocument = fileDocumentOptional.orElseThrow(() -> new EntityNotFoundException("FileDocument not found"));

        DocumentResponseDto documentBranchResponse = DocumentResponseDto.builder()
                .idDocumentation(documentBranch.getIdDocumentation())
                .title(documentBranch.getTitle())
                .status(documentBranch.getStatus())
                .documentation(documentBranch.getDocumentation())
                .fileName(fileDocument.getName())
                .fileContentType(fileDocument.getContentType())
                .fileData(fileDocument.getData())
                .creationDate(documentBranch.getCreationDate())
                .branch(documentBranch.getBranch().getIdBranch())
                .build();

        return Optional.of(documentBranchResponse);
    }

    @Override
    public Page<DocumentResponseDto> findAll(Pageable pageable) {
        Page<DocumentBranch> documentBranchPage = documentBranchRepository.findAll(pageable);

        Page<DocumentResponseDto> documentBranchResponseDtoPage = documentBranchPage.map(
                documentBranch -> {
                    Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentBranch.getDocumentation()));
                    FileDocument fileDocument = fileDocumentOptional.orElse(null);

                    return DocumentResponseDto.builder()
                            .idDocumentation(documentBranch.getIdDocumentation())
                            .title(documentBranch.getTitle())
                            .status(documentBranch.getStatus())
                            .documentation(documentBranch.getDocumentation())
                            .fileName(fileDocument != null ? fileDocument.getName() : null)
                            .fileContentType(fileDocument != null ? fileDocument.getContentType() : null)
                            .fileData(fileDocument != null ? fileDocument.getData() : null)
                            .creationDate(documentBranch.getCreationDate())
                            .branch(documentBranch.getBranch().getIdBranch())
                            .build();
                }

        );

        return documentBranchResponseDtoPage;
    }

    @Override
    public Optional<DocumentResponseDto> update(String id, DocumentBranchRequestDto documentBranchRequestDto, MultipartFile file) throws IOException {
        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument= null;

        Optional<DocumentBranch> documentBranchOptional = documentBranchRepository.findById(id);

        DocumentBranch documentBranch = documentBranchOptional.orElseThrow(() -> new EntityNotFoundException("DocumentBranch not found"));

        if (file != null && !file.isEmpty()) {
            try {
                fileDocument = FileDocument.builder()
                        .name(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .data(file.getBytes())
                        .build();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new EntityNotFoundException(e);
            }

            try {
                savedFileDocument = fileRepository.save(fileDocument);
                fileDocumentId = savedFileDocument.getIdDocumentAsString();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new EntityNotFoundException(e);
            }
            documentBranch.setDocumentation(fileDocumentId);
        }

        documentBranch.setTitle(documentBranchRequestDto.getTitle() != null ? documentBranchRequestDto.getTitle() : documentBranch.getTitle());
        documentBranch.setStatus(documentBranchRequestDto.getStatus() != null ? documentBranchRequestDto.getStatus() : documentBranch.getStatus());

        DocumentBranch savedDocumentBranch = documentBranchRepository.save(documentBranch);

        DocumentResponseDto documentBranchResponse = DocumentResponseDto.builder()
                .idDocumentation(savedDocumentBranch.getIdDocumentation())
                .title(savedDocumentBranch.getTitle())
                .status(savedDocumentBranch.getStatus())
                .documentation(savedDocumentBranch.getDocumentation())
                .creationDate(savedDocumentBranch.getCreationDate())
                .branch(savedDocumentBranch.getBranch().getIdBranch())
                .build();

        return Optional.of(documentBranchResponse);
    }

    @Override
    public void delete(String id) {
        documentBranchRepository.deleteById(id);
    }

    @Override
    public Page<DocumentResponseDto> findAllByBranch(String idSearch, Pageable pageable) {
        Page<DocumentBranch> documentBranchPage = documentBranchRepository.findAllByBranch_IdBranch(idSearch, pageable);

        Page<DocumentResponseDto> documentBranchResponseDtoPage = documentBranchPage.map(
                documentBranch -> {
                    Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(documentBranch.getDocumentation()));
                    FileDocument fileDocument = fileDocumentOptional.orElse(null);

                    return DocumentResponseDto.builder()
                            .idDocumentation(documentBranch.getIdDocumentation())
                            .title(documentBranch.getTitle())
                            .status(documentBranch.getStatus())
                            .documentation(documentBranch.getDocumentation())
                            .fileName(fileDocument != null ? fileDocument.getName() : null)
                            .fileContentType(fileDocument != null ? fileDocument.getContentType() : null)
                            .fileData(fileDocument != null ? fileDocument.getData() : null)
                            .creationDate(documentBranch.getCreationDate())
                            .branch(documentBranch.getBranch().getIdBranch())
                            .build();
                }

        );

        return documentBranchResponseDtoPage;
    }
}
