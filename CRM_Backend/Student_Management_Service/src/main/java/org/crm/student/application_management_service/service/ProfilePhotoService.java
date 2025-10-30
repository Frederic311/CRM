package org.crm.student.application_management_service.service;

import org.crm.student.application_management_service.model.Candidate;
import org.crm.student.application_management_service.model.ProfilePhoto;
import org.crm.student.application_management_service.repository.CandidateRepository;
import org.crm.student.application_management_service.repository.ProfilePhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class ProfilePhotoService {

    @Autowired
    private ProfilePhotoRepository profilePhotoRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    // Save a new profile photo
    public ProfilePhoto saveProfilePhoto(MultipartFile file, Long candidateId) throws IOException {
        Optional<Candidate> candidate = candidateRepository.findById(candidateId); // Use Long here
        if (candidate.isPresent()) {
            ProfilePhoto profilePhoto = new ProfilePhoto();
            profilePhoto.setCandidate(candidate.get());
            profilePhoto.setPhotoData(file.getBytes());
            return profilePhotoRepository.save(profilePhoto);
        } else {
            throw new RuntimeException("Candidate not found with id: " + candidateId);
        }
    }

    // Get the profile photo by candidate ID
    public Optional<ProfilePhoto> getProfilePhotoByCandidateId(Long candidateId) {
        return profilePhotoRepository.findByCandidateId(candidateId); // Use Long here
    }

    // Update an existing profile photo for a candidate
    public ProfilePhoto updateProfilePhoto(MultipartFile file, Long candidateId) throws IOException {
        Optional<Candidate> candidate = candidateRepository.findById(candidateId); // Use Long here

        if (candidate.isPresent()) {
            Optional<ProfilePhoto> existingPhoto = profilePhotoRepository.findByCandidateId(candidateId);
            ProfilePhoto profilePhoto;

            if (existingPhoto.isPresent()) {
                profilePhoto = existingPhoto.get();
                profilePhoto.setPhotoData(file.getBytes());  // Update the photo data
            } else {
                profilePhoto = new ProfilePhoto();  // Create a new ProfilePhoto if one doesn't exist
                profilePhoto.setCandidate(candidate.get());
                profilePhoto.setPhotoData(file.getBytes());
            }

            return profilePhotoRepository.save(profilePhoto);  // Save the updated profile photo
        } else {
            throw new RuntimeException("Candidate not found with id: " + candidateId);
        }
    }

    // Delete a profile photo by candidate ID
    public void deleteProfilePhoto(Long candidateId) {
        Optional<ProfilePhoto> profilePhoto = profilePhotoRepository.findByCandidateId(candidateId);
        profilePhoto.ifPresent(photo -> profilePhotoRepository.delete(photo));
    }
}
