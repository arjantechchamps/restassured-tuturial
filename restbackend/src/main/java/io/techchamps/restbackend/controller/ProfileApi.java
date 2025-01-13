package io.techchamps.restbackend.controller;

import io.techchamps.restbackend.entity.User;
import io.techchamps.restbackend.exception.NotFoundException;
import io.techchamps.restbackend.request.ProfileRequest;
import io.techchamps.restbackend.response.ErrorResponse;
import io.techchamps.restbackend.response.ProfileResponse;
import io.techchamps.restbackend.services.ProfileService;
import io.techchamps.restbackend.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileApi {

    private final UserService userService;
    private final ProfileService profileService;

    public ProfileApi(UserService userService, ProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ProfileResponse> getCurrentUserProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ProfileResponse profileResponse = profileService.getProfile(user);
        return ResponseEntity.ok(profileResponse);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<Object> updateProfile(@RequestBody ProfileRequest profileRequest) {
        try {
            // Get the current authenticated user
            User currentUser = userService.getCurrentUser();  // Assuming this method retrieves the currently authenticated user

            // Update the profile using the ProfileService
            profileService.updateProfile(currentUser, profileRequest);

            // Get the updated profile response
            ProfileResponse updatedProfile = profileService.getProfile(currentUser);

            // Return the updated profile in the response
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error updating profile", 500, "server_error"));
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProfile(@PathVariable int id) {
        try {
            // Find the user by the given ID
            User userToDelete = userService.findById(id).orElseThrow(() -> new NotFoundException("User not found"));

            // Delete the profile
            profileService.deleteProfile(userToDelete);

            // Return a success response
            return ResponseEntity.ok("Profile deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error deleting profile", 500, "server_error"));
        }
    }
}


