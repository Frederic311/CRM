package org.crm.student.application_management_service.service;

import org.crm.student.application_management_service.model.Document;
import org.crm.student.application_management_service.model.Candidate; // Import the Candidate model
import org.crm.student.application_management_service.repository.DocumentRepository;
import org.crm.student.application_management_service.repository.CandidateRepository; // Import the Candidate repository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private CandidateRepository candidateRepository; // Inject the CandidateRepository

    private final String uploadDir = "uploads/";

    public Document uploadDocument(Integer candidateId, MultipartFile file, String documentType) throws IOException {
        // Ensure the upload directory exists
        Path uploadPath = Paths.get(uploadDir);
        if (Files.notExists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Define the file path and transfer the file
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IOException("Invalid file name.");
        }
        Path filePath = uploadPath.resolve(originalFilename);
        file.transferTo(filePath);

        // Fetch the candidate from the database
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid candidate ID: " + candidateId));

        // Create and save the document entity
        Document document = new Document();
        document.setCandidate(candidate); // Set the entire candidate object
        document.setDocumentType(documentType);
        document.setFilePath(filePath.toString());
        document.setUploadDate(LocalDate.now());
        document.setExpiryDate(LocalDate.now().plusYears(1));
        document.setVersion(1);

        return documentRepository.save(document);
    }
}
