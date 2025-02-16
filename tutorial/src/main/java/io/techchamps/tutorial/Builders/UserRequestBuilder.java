package io.techchamps.tutorial.Builders;

import generated.dtos.ProfileRequest;
import generated.dtos.RoleRequest;
import generated.dtos.UserRequest;

public class UserRequestBuilder {

    private final UserRequest userRequest;
    private final ProfileRequest profileRequest;
    private final RoleRequest roleRequest;

    public UserRequestBuilder() {
        // Initialize the objects with default values.
        userRequest = new UserRequest();
        profileRequest = new ProfileRequest();
        roleRequest = new RoleRequest();

        // Set default role
        roleRequest.setName("USER");

        // Set default user information
        userRequest.setName("John");
        userRequest.setUsername("John");
        userRequest.setEmail("John@test.nl");
        userRequest.setPassword("test1234");
        userRequest.addRolesItem(roleRequest);
        userRequest.setProfile(new ProfileRequestBuilder(). build());
    }

    // With method to override name.
    public UserRequestBuilder withName(String name) {
        userRequest.setName(name);
        return this;
    }

    // With method to override username.
    public UserRequestBuilder withUsername(String username) {
        userRequest.setUsername(username);
        return this;
    }

    // With method to override email.
    public UserRequestBuilder withEmail(String email) {
        userRequest.setEmail(email);
        return this;
    }

    // With method to override password.
    public UserRequestBuilder withPassword(String password) {
        userRequest.setPassword(password);
        return this;
    }

    // With method to override roles.
    public UserRequestBuilder withRole(RoleRequest role) {
        // Clear existing roles if you want a single role override
        userRequest.addRolesItem(role);
        return this;
    }

    // With method to override or add an address.
    public UserRequestBuilder withAddress(AdressRequestBuilder address) {
        // For example, clear all existing addresses and add a new one.
        profileRequest.addAddressesItem(address.build());
        return this;
    }

    // With method to add an interest.
    public UserRequestBuilder withInterest(ProfileRequest.InterestsEnum interest) {
        profileRequest.addInterestsItem(interest);
        return this;
    }

    public UserRequestBuilder withProfile(ProfileRequestBuilder profileRequest){
        userRequest.setProfile(profileRequest.build());
        return this;
    }

    // Build the UserRequest object.
    public UserRequest build() {
        return userRequest;
    }
}
