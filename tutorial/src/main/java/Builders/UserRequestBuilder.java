package Builders;

import generated.dtos.AddressRequest;
import generated.dtos.ProfileRequest;
import generated.dtos.RoleRequest;
import generated.dtos.UserRequest;

public class UserRequestBuilder {

    private final UserRequest userRequest;
    private final ProfileRequest profileRequest;
    private final RoleRequest roleRequest;
    private final AddressRequest addressRequest;

    public UserRequestBuilder() {
        // Initialize the objects with default values.
        userRequest = new UserRequest();
        profileRequest = new ProfileRequest();
        roleRequest = new RoleRequest();
        addressRequest = new AddressRequest();

        // Set default role
        roleRequest.setName("USER");

        // Set default address
        addressRequest.setCity("Enschede");
        addressRequest.setCountry("Netherlands");
        addressRequest.setStreet("TestStreet");
        addressRequest.setHouseNumber("5");
        addressRequest.setZipcode("1111AA");

        // Set default profile: add interests and address
        profileRequest.addInterestsItem(ProfileRequest.InterestsEnum.SPORTS);
        profileRequest.addInterestsItem(ProfileRequest.InterestsEnum.TRAVEL);
        profileRequest.addAddressesItem(addressRequest);

        // Set default user information
        userRequest.setName("Joe");
        userRequest.setUsername("Joe");
        userRequest.setEmail("Joe@test.nl");
        userRequest.setPassword("test1234");
        userRequest.addRolesItem(roleRequest);
        userRequest.setProfile(profileRequest);
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
        userRequest.getRoles().clear();
        userRequest.addRolesItem(role);
        return this;
    }

    // With method to override or add an address.
    public UserRequestBuilder withAddress(AddressRequest address) {
        // For example, clear all existing addresses and add a new one.
        profileRequest.getAddresses().clear();
        profileRequest.addAddressesItem(address);
        return this;
    }

    // With method to add an interest.
    public UserRequestBuilder withInterest(ProfileRequest.InterestsEnum interest) {
        profileRequest.addInterestsItem(interest);
        return this;
    }

    public UserRequestBuilder withProfile(ProfileRequest profileRequest){
        userRequest.setProfile(profileRequest);
        return this;
    }

    // Build the UserRequest object.
    public UserRequest build() {
        return userRequest;
    }
}
