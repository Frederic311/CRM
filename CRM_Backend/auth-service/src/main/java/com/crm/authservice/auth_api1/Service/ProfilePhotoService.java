package com.crm.authservice.auth_api1.Service;

import com.crm.authservice.auth_api1.Repository.ProfilePhotoRepository;
import com.crm.authservice.auth_api1.Repository.UserRepository;
import com.crm.authservice.auth_api1.models.ProfilePhoto;
import com.crm.authservice.auth_api1.models.User;
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
    private UserRepository userRepository;

    // Save a new profile photo
    public ProfilePhoto saveProfilePhoto(MultipartFile file, Long userId) throws IOException {
        Optional<User> user = userRepository.findById(userId); // Use Long here
        if (user.isPresent()) {
            // Check if the user already has a profile photo
            Optional<ProfilePhoto> existingPhotoOptional = profilePhotoRepository.findByUserId(userId);
            
            ProfilePhoto profilePhoto;
            
            if (existingPhotoOptional.isPresent()) {
                // If photo exists, update the existing profile photo
                profilePhoto = existingPhotoOptional.get();
                profilePhoto.setPhotoData(file.getBytes());
            } else {
                // If no photo exists, create a new one
                profilePhoto = new ProfilePhoto();
                profilePhoto.setUser(user.get());
                profilePhoto.setPhotoData(file.getBytes());
            }
            return profilePhotoRepository.save(profilePhoto);
        } else {
            throw new RuntimeException("User not found with id: " + userId); // Proper exception message
        }
    }

    // Get the profile photo by user ID
    public Optional<ProfilePhoto> getProfilePhotoByCandidateId(Long userId) {
        return profilePhotoRepository.findByUserId(userId); // Use Long here
    }

    // Update an existing profile photo for a user
    public ProfilePhoto updateProfilePhoto(MultipartFile file, Long userId) throws IOException {
        Optional<User> user = userRepository.findById(userId); // Use Long here

        if (user.isPresent()) {
            Optional<ProfilePhoto> existingPhoto = profilePhotoRepository.findByUserId(userId);
            ProfilePhoto profilePhoto;

            if (existingPhoto.isPresent()) {
                profilePhoto = existingPhoto.get();
                profilePhoto.setPhotoData(file.getBytes());  // Update the photo data
            } else {
                profilePhoto = new ProfilePhoto();  // Create a new ProfilePhoto if one doesn't exist
                profilePhoto.setUser(user.get());  // Set the user properly
                profilePhoto.setPhotoData(file.getBytes());
            }

            return profilePhotoRepository.save(profilePhoto);  // Save the updated profile photo
        } else {
            throw new RuntimeException("User not found with id: " + userId); // Proper exception message
        }
    }

    // Delete a profile photo by user ID
    public void deleteProfilePhoto(Long userId) {
        Optional<ProfilePhoto> profilePhoto = profilePhotoRepository.findByUserId(userId);
        profilePhoto.ifPresent(photo -> profilePhotoRepository.delete(photo));
    }
}
