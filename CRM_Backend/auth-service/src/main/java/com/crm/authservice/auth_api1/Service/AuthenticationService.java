package com.crm.authservice.auth_api1.Service;

import com.crm.authservice.auth_api1.Repository.RoleRepository;
import com.crm.authservice.auth_api1.Repository.TokenRepository;
import com.crm.authservice.auth_api1.Repository.UserRepository;
import com.crm.authservice.auth_api1.Request.AuthenticationRequest;
import com.crm.authservice.auth_api1.Response.AuthenticationResponse;
import com.crm.authservice.auth_api1.Request.RegistrationRequest;
import com.crm.authservice.auth_api1.models.EmailTemplateName;
import com.crm.authservice.auth_api1.filters.JwtService;
import com.crm.authservice.auth_api1.models.Token;
import com.crm.authservice.auth_api1.models.User;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final TokenRepository tokenRepository;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;
    @Value("http://localhost:4200/resertUrl")
    private String resetUrl;



    // Upload user image

    // Update User method
    public User updateUser(Integer userId, User updatedUserDetails) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        try {
            existingUser.setFirstname(updatedUserDetails.getFirstname());
            existingUser.setLastname(updatedUserDetails.getLastname());
            existingUser.setEmail(updatedUserDetails.getEmail());
            if (updatedUserDetails.getPassword() != null && !updatedUserDetails.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(updatedUserDetails.getPassword()));
            }
            existingUser.setEnabled(updatedUserDetails.isEnabled());
            existingUser.setDateOfBirth(updatedUserDetails.getDateOfBirth());
            existingUser.setAccountLocked(updatedUserDetails.isAccountLocked());
            existingUser.setRoles(updatedUserDetails.getRoles());

            return userRepository.save(existingUser);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Data integrity violation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
    }


    // Delete User method
    @Transactional
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        // Delete associated tokens
        tokenRepository.deleteAllByUser(user);

        // Delete the user
        userRepository.delete(user);
    }


    // Get All Users method
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get Specific User method
    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initiated"));

        String temporaryPassword = generateTemporaryPassword(8);

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(temporaryPassword))
                .accountLocked(false)
                .enabled(true)
                .roles(List.of(userRole))
                .isTemporaryPassword(true)
                .build();

        userRepository.save(user);
        sendValidationEmail(user, temporaryPassword);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var user = (User) auth.getPrincipal();
        if (user.isTemporaryPassword()) {
            return AuthenticationResponse.builder()
                    .message("Temporary password used. Please change your password.")
                    .requirePasswordChange(true)
                    .build();
        }

        var claims = new HashMap<String, Object>();
        claims.put("fullName", user.getFullName());
        var jwtToken = jwtService.generateToken(claims, user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .requirePasswordChange(false)
                .build();
    }

//    @Transactional
//    public void activateAccount(String token) throws MessagingException {
//        Token savedToken = tokenRepository.findByToken(token)
//                .orElseThrow(() -> new RuntimeException("Invalid token"));
//
//        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
//            sendValidationEmail(savedToken.getUser(), null);
//            throw new RuntimeException("Activation token has expired. A new token has been sent to the same email address");
//        }
//
//        var user = userRepository.findById(savedToken.getUser().getId())
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//        user.setEnabled(true);
//        userRepository.save(user);
//        savedToken.setValidatedAt(LocalDateTime.now());
//        tokenRepository.save(savedToken);
//    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private void sendValidationEmail(User user, String temporaryPassword) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Welcome to the Platform! Your Temporary Password",
                temporaryPassword
        );
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    private String generateTemporaryPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder passwordBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            passwordBuilder.append(characters.charAt(randomIndex));
        }

        return passwordBuilder.toString();
    }

    public void changePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setTemporaryPassword(false);
        userRepository.save(user);
    }




    private String generateAndSavePasswordResetToken(User user) {
        String generatedToken = generateActivationCode(6);
        Token token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }


    public void sendPasswordResetToken(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = generateAndSavePasswordResetToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.RESET_PASSWORD,
                resetUrl,
                token,
                "Password Reset Request",
                null
        );
    }



    public String resetPasswordWithToken(String token, String newPassword) {
        // Retrieve the token from the repository
        Optional<Token> savedTokenOpt = tokenRepository.findByToken(token);

        if (savedTokenOpt.isPresent()) {
            Token savedToken = savedTokenOpt.get();

            // Check if the token has expired
            if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
                return "Token has expired. Please request a new password reset.";
            }

            // Proceed with password reset
            User user = savedToken.getUser();
            user.setPassword(passwordEncoder.encode(newPassword)); // Encode the new password
            user.setTemporaryPassword(false); // Set temporary password flag to false
            userRepository.save(user); // Save the updated user

            // Send confirmation email after successful password reset
            sendPasswordResetConfirmationEmail(user);

            // Delete the token after it has been used
            tokenRepository.delete(savedToken);

            return "Password reset successfully.";
        } else {
            return "Invalid token. Please request a new password reset.";
        }
    }

    // Token validation for password reset
    public boolean isTokenValid(String token) {
        Optional<Token> savedTokenOpt = tokenRepository.findByToken(token);
        return savedTokenOpt.isPresent() && !LocalDateTime.now().isAfter(savedTokenOpt.get().getExpiresAt());
    }
    private void sendPasswordResetConfirmationEmail(User user) {
        try {
            emailService.sendEmail(
                    user.getEmail(),
                    user.getFullName(),
                    EmailTemplateName.PASSWORD_RESET_CONFIRMATION,
                    null,
                    null,
                    "Your Password Has Been Reset",
                    "Your password has been successfully reset. If you did not request this change, please contact support."
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    public User getUserInfoFromToken(String token) {
        // Extract email or username from the token
        String email = jwtService.extractUsername(token);

        // Fetch the user by email
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }


    @Transactional
    public void logout(String token) {
        Optional<Token> savedTokenOpt = tokenRepository.findByToken(token);
        if (savedTokenOpt.isPresent()) {
            Token savedToken = savedTokenOpt.get();
            savedToken.setExpiresAt(LocalDateTime.now()); // Mark as expired
            tokenRepository.save(savedToken); // Update the token status in the database
        }
    }





    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public boolean doesUserExist(String userFullName) {
        // Split the full name into first and last name
        String[] nameParts = userFullName
                .split(" ");

        // Ensure the full name contains exactly two parts: first and last name
        if (nameParts.length != 2) {
            return false; // Invalid full name format
        }

        String firstname = nameParts[0];
        String lastname = nameParts[1];

        // Use the repository method to check existence
        return userRepository. existsByFirstnameAndLastname(firstname, lastname);
    }
    public String getEmailByFullName(String fullName) {
        Optional<User> user = userRepository.findByFullName(fullName);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User with full name " + fullName + " not found");
        }
        return user.get().getEmail();
    }
}
