package com.crm.authservice.auth_api1.controller;


import com.crm.authservice.auth_api1.Repository.ProfilePhotoRepository;
import com.crm.authservice.auth_api1.Repository.UserRepository;
import com.crm.authservice.auth_api1.Request.AuthenticationRequest;
import com.crm.authservice.auth_api1.Request.RegistrationRequest;
import com.crm.authservice.auth_api1.Response.AuthenticationResponse;
import com.crm.authservice.auth_api1.Service.AuthenticationService;
import com.crm.authservice.auth_api1.Service.ProfilePhotoService;
import com.crm.authservice.auth_api1.Service.RoleService;
import com.crm.authservice.auth_api1.filters.JwtService;
import com.crm.authservice.auth_api1.handle.UserNotFoundException;
import com.crm.authservice.auth_api1.models.ProfilePhoto;
import com.crm.authservice.auth_api1.models.Role;
import com.crm.authservice.auth_api1.models.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
// @CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {

    private final AuthenticationService service;


    private final JwtService jwtService;

    
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfilePhotoService profilePhotoService;




    // Upload user profile image


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                      @RequestBody @Valid RegistrationRequest request) {
        Logger logger = LoggerFactory.getLogger(this.getClass());

        // Check if the Authorization header is present and valid
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("error", "Unauthorized");
            responseBody.put("message", "Authorization token is required.");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
        }

        String jwt = authHeader.substring(7); // Extract JWT token
        String userRole = jwtService.extractUserRole(jwt); // You should have a method to extract user role

        // Check if the user has admin role
        if (userRole == null || !userRole.equals("ADMIN")) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("error", "Forbidden");
            responseBody.put("message", "You do not have permission to register users.");

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
        }

        try {
            service.register(request);
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("status", "success");
            successResponse.put("message", "Registration successful. A confirmation email has been sent.");

            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(successResponse);
        } catch (MessagingException e) {
            logger.error("Failed to send registration email for request: {}", request, e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Registration failed. Unable to send confirmation email.");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error occurred during registration for request: {}", request, e);

            Map<String, Object> badRequestResponse = new HashMap<>();
            badRequestResponse.put("status", "error");
            badRequestResponse.put("message", "Registration failed due to invalid input.");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(badRequestResponse);
        }

    }
//    @PostMapping("/register")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public ResponseEntity<?> register(
//            @RequestBody @Valid RegistrationRequest request
//    ) throws MessagingException {
//        service.register(request);
//        return ResponseEntity.accepted().build();
//    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
    //    @GetMapping("/activate-account")
//    public void confirm(
//            @RequestParam String token
//    ) throws MessagingException {
//        service.activateAccount(token);
//    }
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestParam String email,
            @RequestParam String newPassword
    ) throws MessagingException {
        service.changePassword(email, newPassword);
        return ResponseEntity.ok("Password changed successfully");
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            service.sendPasswordResetToken(email);
            return ResponseEntity.ok("Password reset email sent successfully.");
        } catch (MessagingException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to send password reset email.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        } catch (UsernameNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User with the given email not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(response);
        }
    }
    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestParam String token) {
        boolean isTokenValid = service.isTokenValid(token);
        if (isTokenValid) {
            return ResponseEntity.ok("Token is valid. Please proceed to set your new password.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword
    ) {
        service.resetPasswordWithToken(token, newPassword);
        return ResponseEntity.ok("Password reset successfully.");
    }

    // ==============================================
    // NEWLY ADDED USER MANAGEMENT FUNCTIONALITY
    // ==============================================

    // Update User by ID
    @PutMapping("/update-user/{id}")
    public ResponseEntity<?> updateUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Integer id,
            @RequestBody @Valid User updatedUserDetails
    ) {
        Logger logger = LoggerFactory.getLogger(this.getClass());

        // Check if the Authorization header is present and valid
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is required.");
        }

        String jwt = authHeader.substring(7); // Extract JWT token
        String userRole = jwtService.extractUserRole(jwt); // You should have a method to extract user role

        // Check if the user has admin role
        if (userRole == null || !userRole.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to update users.");
        }

        try {
            User updatedUser = service.updateUser(id, updatedUserDetails);
            logger.info("User updated successfully: {}", updatedUser);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            logger.error("User not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + id);
        } catch (Exception e) {
            logger.error("Failed to update user with ID: {} due to {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user");
    }
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<Object> deleteUser(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                             @PathVariable Integer id) {
        Logger logger = LoggerFactory.getLogger(this.getClass());

        // Check for Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Authorization token is missing or invalid for delete user request.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is required.");
        }

        String jwt = authHeader.substring(7);
        String userRole;
        try {
            userRole = jwtService.extractUserRole(jwt);
        } catch (Exception e) {
            logger.error("Failed to extract user role from JWT: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authorization token.");
        }

        // Check if user has ADMIN role
        if (userRole == null || !userRole.equals("ADMIN")) {
            logger.warn("User with role {} attempted to delete user with ID {} without permission.", userRole, id);
            Map<String, String> response = new HashMap<>();
            response.put("error", "You do not have permission to delete users.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // Attempt to delete user
        try {
            service.deleteUser(id);
            logger.info("User with ID {} deleted successfully.", id);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            logger.warn("User not found with ID: {}", id);
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            logger.error("Failed to delete user with ID: {}", id, e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to delete user.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // Get All Users
    @GetMapping("/all-users")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Logger logger = LoggerFactory.getLogger(this.getClass());

        // Check for Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Authorization token is missing or invalid for get all users request.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String jwt = authHeader.substring(7);
        String userRole;
        try {
            userRole = jwtService.extractUserRole(jwt);
        } catch (Exception e) {
            logger.error("Failed to extract user role from JWT: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Check if user has ADMIN role
        if (userRole == null || !userRole.equals("ADMIN")) {
            logger.warn("User with role '{}' attempted to access user list without permission.", userRole);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // Retrieve all users
        List<User> users;
        try {
            users = service.getAllUsers(); // Assuming service.getAllUsers() returns List<User>
            logger.info("Successfully retrieved list of users.");
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Failed to retrieve users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get Specific User by ID
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                         @PathVariable Integer id) {
        Logger logger = LoggerFactory.getLogger(this.getClass());

        // Check if the Authorization header is present and valid
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Authorization token is required.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String jwt = authHeader.substring(7); // Extract JWT token
        String userRole = jwtService.extractUserRole(jwt); // You should have a method to extract user role

        // Check if the user has admin role
        if (userRole == null || !userRole.equals("ADMIN")) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "You do not have permission to view user details.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // Validate ID
        if (id <= 0) {
            logger.warn("Invalid user ID: {}", id);
            Map<String, String> response = new HashMap<>();
            response.put("error", "User ID must be a positive integer.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            User user = service.getUserById(id);
            logger.info("User retrieved successfully: {}", user);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            logger.error("User not found with ID: {}", id);
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            logger.error("Failed to retrieve user with ID: {} due to {}", id, e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to retrieve user");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfoFromToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Authorization token is required.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String jwt = authHeader.substring(7); // Extract the JWT token

        try {
            User user = service.getUserInfoFromToken(jwt);
            logger.info("User information retrieved successfully for token.");
            return ResponseEntity.ok(user);
        } catch (UsernameNotFoundException e) {
            logger.error("User not found for provided token.");
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            logger.error("Failed to retrieve user information due to an error: {}", e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to retrieve user information.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/logout")
public ResponseEntity<Map<String, String>> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Authorization token is required.");
        return ResponseEntity.status(401).body(response);
    }

    String jwt = authHeader.substring(7); // Extract the JWT token

    try {
        service.logout(jwt);
        logger.info("User successfully logged out. Token invalidated.");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful.");
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        logger.error("Logout failed: {}", e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", "Logout failed.");
        return ResponseEntity.status(500).body(response);
    }
}

    @GetMapping("/roles/{name}")
    public ResponseEntity<?> getRoleByName(
        @PathVariable String name,
        @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Authorization token is required.");
            return ResponseEntity.status(401).body(response);
        }
    
        try {
            Role role = roleService.getRoleByName(name);
            return ResponseEntity.ok(role);
        } catch (RuntimeException e) {
            logger.error("Role not found: {}", e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("error", "No such role "+name);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(
        @PathVariable String email,
        @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Authorization token is required.");
            return ResponseEntity.status(401).body(response);
        }
        try {
            User user = service.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            logger.error("User not found: {}", e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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


    @PostMapping("/{userId}/upload")
    public ResponseEntity<?> uploadProfilePhoto(
            @PathVariable Long userId,  // Retrieve candidateId from URL path
            @RequestParam("file") MultipartFile file) {
        try {
            // Pass the file and candidateId to the service layer for processing
            profilePhotoService.saveProfilePhoto(file, userId);
            Map<String, Object> Response = new HashMap<>();
            Response.put("message", "Profile photo uploaded successfully.");

            return ResponseEntity.ok(Response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload profile photo: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    // Update profile photo (Update operation)
    @PutMapping("/{userId}/update")
    public ResponseEntity<String> updateProfilePhoto(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            profilePhotoService.updateProfilePhoto(file, userId);
            return ResponseEntity.ok("Profile photo updated successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to update profile photo: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Candidate not found: " + e.getMessage());
        }
    }

    // Delete profile photo by candidate ID (Delete operation)
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteProfilePhoto(@PathVariable Long userId) {
        try {
            profilePhotoService.deleteProfilePhoto(userId);
            return ResponseEntity.ok("Profile photo deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete profile photo: " + e.getMessage());
        }
    }
    @GetMapping("/{userFullName}/exists")
    public ResponseEntity<Boolean> doesUserExist(@PathVariable String userFullName) {
        boolean exists = service.doesUserExist(userFullName);
        return ResponseEntity.ok(exists);
    }
    @GetMapping("/{userfullname}/email")
    public ResponseEntity<String> getEmailByFullName(@PathVariable("userfullname") String userFullName) {
        String email = service.getEmailByFullName(userFullName);
        return ResponseEntity.ok(email);
    }

}



