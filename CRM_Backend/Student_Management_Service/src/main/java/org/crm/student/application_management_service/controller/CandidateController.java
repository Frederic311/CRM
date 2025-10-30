package org.crm.student.application_management_service.controller;

import org.crm.student.application_management_service.model.*;
import org.crm.student.application_management_service.repository.CandidateRepository;
import org.crm.student.application_management_service.service.CSVHelper;
import org.crm.student.application_management_service.service.CandidateService;
import org.crm.student.application_management_service.service.ProfilePhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private ProfilePhotoService profilePhotoService;

    @Autowired
    private CSVHelper csvHelper;

    private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".csv")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload a valid CSV file.");
        }

        try {
            List<Candidate> candidates = csvHelper.parseCSV(file.getInputStream());
            candidateService.saveAll(candidates);
            return ResponseEntity.status(HttpStatus.OK).body("Candidates successfully uploaded.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the file: " + e.getMessage());
        }
    }

    @PostMapping(value = "/", consumes = "application/json")
    public ResponseEntity<Candidate> createCandidate(@RequestBody Candidate candidateDetail) {
        return ResponseEntity.ok(candidateRepository.save(candidateDetail));
    }

    @GetMapping("/{candidateFullname}/exists")
    public ResponseEntity<Boolean> doesCandidateExist(@PathVariable String candidateFullname) {
        boolean exists = candidateService.doesCandidateExist(candidateFullname);
        return ResponseEntity.ok(exists);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCandidate(@PathVariable Integer id) {
        try {
            // First, delete the candidate's profile photo
            profilePhotoService.deleteProfilePhoto(Long.valueOf(id)); // Assuming `id` is the candidate's ID

            // Then, delete the candidate
            candidateService.deleteCandidate(id);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error deleting candidate or profile photo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete candidate or profile photo: " + e.getMessage());
        }
    }



    @PutMapping("/{id}/status")
    public ResponseEntity<Candidate> updateCandidateStatus(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        try {
            String newStatus = request.get("status");
            Optional<Candidate> candidateOpt = candidateService.getCandidateById(id);

            if (candidateOpt.isPresent()) {
                Candidate candidate = candidateOpt.get();

                // Check if the current status is STUDENT
                if (candidate.getStatus() == Status.STUDENT) {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Return forbidden if status is STUDENT
                }

                // Update the status
                candidate.setStatus(Status.valueOf(newStatus.toUpperCase()));
                candidateService.updateCandidate(candidate);
                return new ResponseEntity<>(candidate, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            // Handle invalid status value
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Log the exception (you can use a logger here)
            logger.error("Error updating candidate status", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<Candidate>> getCandidates() {
        List<Candidate> candidates = candidateService.getAllCandidates();
        if (candidates == null) {
            throw new RuntimeException("Candidates list is null");
        }
        return ResponseEntity.ok(candidates);
    }
    @PostMapping("/{candidateId}/upload")
    public ResponseEntity<String> uploadProfilePhoto(
            @PathVariable Long candidateId,  // Retrieve candidateId from URL path
            @RequestParam("file") MultipartFile file) {
        try {
            // Pass the file and candidateId to the service layer for processing
            profilePhotoService.saveProfilePhoto(file, candidateId);
            return ResponseEntity.ok("Profile photo uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload profile photo: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteProfilePhoto(@PathVariable Long candidateId) {
        try {
            profilePhotoService.deleteProfilePhoto(candidateId);
            return ResponseEntity.ok("Profile photo deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete profile photo: " + e.getMessage());
        }
    }
    @PutMapping("/{candidateId}/update")
    public ResponseEntity<String> updateProfilePhoto(
            @PathVariable Long candidateId,
            @RequestParam("file") MultipartFile file) {
        try {
            profilePhotoService.updateProfilePhoto(file, candidateId);
            return ResponseEntity.ok("Profile photo updated successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to update profile photo: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Candidate not found: " + e.getMessage());
        }
    }
    @GetMapping("/{candidateId}/profile-photo")
    public ResponseEntity<?> getProfilePhoto(@PathVariable Long candidateId) {
        Optional<ProfilePhoto> profilePhotoOpt = profilePhotoService.getProfilePhotoByCandidateId(candidateId);

        if (profilePhotoOpt.isPresent()) {
            ProfilePhoto profilePhoto = profilePhotoOpt.get();
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Adjust content type if necessary (image/png, etc.)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"profile_photo.jpg\"")
                    .body(profilePhoto.getPhotoData());  // Assuming the photo is stored as a byte array
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Profile photo not found");  // Return 404 with a message
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getCandidateById(@PathVariable Integer id) {
        Optional<Candidate> candidateOpt = candidateService.getCandidateById(id);

        if (candidateOpt.isPresent()) {
            return ResponseEntity.ok(candidateOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Candidate> updateCandidateInformation(
            @PathVariable Integer id,
            @RequestBody Candidate updatedCandidate) {
        try {
            Candidate updated = candidateService.updateCandidateInformation(id, updatedCandidate);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Handle the case where the candidate is not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Handle other possible errors
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}







