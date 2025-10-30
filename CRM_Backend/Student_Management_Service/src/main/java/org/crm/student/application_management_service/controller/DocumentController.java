package org.crm.student.application_management_service.controller;

import org.crm.student.application_management_service.model.Document;
import org.crm.student.application_management_service.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/candidates/{candidateId}/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping
    public ResponseEntity<Document> uploadDocument(
            @PathVariable Integer candidateId, // Changed from studentId to candidateId
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType) {
        try {
            Document document = documentService.uploadDocument(candidateId, file, documentType); // Use candidateId here
            return ResponseEntity.ok(document);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
