package org.crm.student.application_management_service.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.transaction.Transactional;
import org.crm.student.application_management_service.model.*;
import org.crm.student.application_management_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProfilePhotoRepository profilePhotoRepository;

    public void saveAll(List<Candidate> candidates) {
        candidateRepository.saveAll(candidates);
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + date, e);
        }
    }

    private LocalDateTime parseDateTime(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid datetime format: " + dateTime, e);
        }
    }

    public boolean doesCandidateExist(String candidateFullName) {
        // Split the full name into first and last name
        String[] nameParts = candidateFullName.split(" ");
        if (nameParts.length != 2) {
            // Handle the case where the full name doesn't contain exactly two parts
            return false;
        }

        String firstName = nameParts[0];
        String lastName = nameParts[1];

        // Use the repository method to check existence
        return candidateRepository.existsByFirstNameAndLastName(firstName, lastName);
    }

    public Optional<Candidate> getCandidateById(Integer id) {
        return candidateRepository.findById(id);
    }

    public void deleteCandidate(Integer id) {
        Optional<Candidate> candidate = candidateRepository.findById(id);
        candidate.ifPresent(c -> {
            // Manually delete the associated ProfilePhoto if it exists
            if (c.getProfilePhoto() != null) {
                profilePhotoRepository.delete(c.getProfilePhoto());
            }
            // Delete the Candidate
            candidateRepository.deleteById(id);
        });
    }

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public void updateCandidate(Candidate candidate) {
        // If the candidate's status is being updated to STUDENT, generate the candidateId
        if (candidate.getStatus() == Status.STUDENT) {
            generateCandidateId(candidate);
        }

        candidateRepository.save(candidate);

        // Send notifications if the status is updated to STUDENT
        if (candidate.getStatus() == Status.STUDENT) {
            try {
                notificationService.sendEmailNotification(
                        candidate.getEmail(),
                        "Your Candidate Application has been Updated",
                        candidate.getFirstName()
                );
            } catch (Exception e) {
                System.err.println("Failed to send email notification: " + e.getMessage());
            }

            try {
                notificationService.sendSmsNotification(
                        candidate.getPhoneNumber(),
                        "Dear " + candidate.getFirstName() + ", your application status has been updated to STUDENT."
                );
            } catch (Exception e) {
                System.err.println("Failed to send SMS notification: " + e.getMessage());
            }
        }
    }

    // Method to generate candidate ID
    private void generateCandidateId(Candidate candidate) {
        // Get the current year (e.g., 2024)
        String currentYear = String.valueOf(LocalDate.now().getYear());

        // Get the first 3 letters of the field (e.g., "ENG" for Engineering)
        String fieldCode = candidate.getField() != null ? candidate.getField().substring(0, 3).toUpperCase() : "UNK";

        // Get the candidate count for the same year and field
        int candidateCount = getCandidateCountForYearAndField(currentYear, fieldCode);

        // Generate the candidate ID
        String incrementedId = String.format("%04d", candidateCount + 1);
        candidate.setCandidateId(currentYear + fieldCode + incrementedId);
    }

    // Method to count candidates in the same year and field
    private int getCandidateCountForYearAndField(String year, String field) {
        // Query the database to count the number of candidates for the same year and field
        String prefix = year + field;
        return candidateRepository.countByCandidateIdStartingWith(prefix);
    }

    private Status parseStatus(String status) {
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid status: " + status + ". Defaulting to NEW.");
            return Status.NEW; // Default value
        }
    }

    private boolean candidateExists(String firstName, String lastName) {
        return candidateRepository.findByFirstNameAndLastName(firstName, lastName).isPresent();
    }
    // Method to update all candidate information
    // Method to update all candidate information
    @Transactional
    public Candidate updateCandidateInformation(Integer id, Candidate updatedCandidate) {
        // Fetch the existing candidate by ID
        Optional<Candidate> existingCandidateOpt = candidateRepository.findById(id);

        if (existingCandidateOpt.isPresent()) {
            Candidate existingCandidate = existingCandidateOpt.get();

            // Merge the existing candidate's fields with the updated information
            if (updatedCandidate.getFirstName() != null) {
                existingCandidate.setFirstName(updatedCandidate.getFirstName());
            }
            if (updatedCandidate.getLastName() != null) {
                existingCandidate.setLastName(updatedCandidate.getLastName());
            }
            if (updatedCandidate.getEmail() != null) {
                existingCandidate.setEmail(updatedCandidate.getEmail());
            }
            if (updatedCandidate.getPhoneNumber() != null) {
                existingCandidate.setPhoneNumber(updatedCandidate.getPhoneNumber());
            }
            if (updatedCandidate.getField() != null) {
                existingCandidate.setField(updatedCandidate.getField());
            }
            if (updatedCandidate.getApplicationDate() != null) {
                existingCandidate.setApplicationDate(updatedCandidate.getApplicationDate());
            }
            if (updatedCandidate.getProfilePhoto() != null) {
                existingCandidate.setProfilePhoto(updatedCandidate.getProfilePhoto());
            }

            // Update nested objects
            if (updatedCandidate.getParentDetail() != null) {
                Parent parentDetail = existingCandidate.getParentDetail();
                Parent updatedParentDetail = updatedCandidate.getParentDetail();
                if (parentDetail != null && updatedParentDetail != null) {
                    if (updatedParentDetail.getFullName() != null) {
                        parentDetail.setFullName(updatedParentDetail.getFullName());
                    }
                    if (updatedParentDetail.getEmail() != null) {
                        parentDetail.setEmail(updatedParentDetail.getEmail());
                    }
                    if (updatedParentDetail.getPhoneNumber() != null) {
                        parentDetail.setPhoneNumber(updatedParentDetail.getPhoneNumber());
                    }
                    if (updatedParentDetail.getRelationshipToCandidate() != null) {
                        parentDetail.setRelationshipToCandidate(updatedParentDetail.getRelationshipToCandidate());
                    }
                }
            }

            if (updatedCandidate.getEmploymentDetail() != null) {
                EmploymentDetails employmentDetail = existingCandidate.getEmploymentDetail();
                EmploymentDetails updatedEmploymentDetail = updatedCandidate.getEmploymentDetail();
                if (employmentDetail != null && updatedEmploymentDetail != null) {
                    if (updatedEmploymentDetail.getCompanyName() != null) {
                        employmentDetail.setCompanyName(updatedEmploymentDetail.getCompanyName());
                    }
                    if (updatedEmploymentDetail.getResponsibilities() != null) {
                        employmentDetail.setResponsibilities(updatedEmploymentDetail.getResponsibilities());
                    }
                }
            }

            if (updatedCandidate.getEducationDetail() != null) {
                EducationDetails educationDetail = existingCandidate.getEducationDetail();
                EducationDetails updatedEducationDetail = updatedCandidate.getEducationDetail();
                if (educationDetail != null && updatedEducationDetail != null) {
                    if (updatedEducationDetail.getHighestEducation() != null) {
                        educationDetail.setHighestEducation(updatedEducationDetail.getHighestEducation());
                    }
                    if (updatedEducationDetail.getInstitutionName() != null) {
                        educationDetail.setInstitutionName(updatedEducationDetail.getInstitutionName());
                    }
                    if (updatedEducationDetail.getGraduationYear() != 0) {
                        educationDetail.setGraduationYear(updatedEducationDetail.getGraduationYear());
                    }
                    if (updatedEducationDetail.getFieldOfStudy() != null) {
                        educationDetail.setFieldOfStudy(updatedEducationDetail.getFieldOfStudy());
                    }
                }
            }

            // Save the updated candidate
            candidateRepository.save(existingCandidate);

            return existingCandidate;
        } else {
            throw new RuntimeException("Candidate not found with ID: " + id);
        }
    }


}

