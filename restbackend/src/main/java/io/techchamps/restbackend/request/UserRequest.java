package io.techchamps.restbackend.request;


import java.util.Set;

public class UserRequest {

    private String name;
    private String username;
    private String email;
    private Set<String> roles;
    private AddressRequest workAddress;
    private AddressRequest homeAddress;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public AddressRequest getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(AddressRequest workAddress) {
        this.workAddress = workAddress;
    }

    public AddressRequest getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(AddressRequest homeAddress) {
        this.homeAddress = homeAddress;
    }
}
