package io.techchamps.restbackend.request;


import io.techchamps.restbackend.entity.Role;

import java.util.Set;

public class UserRequest {

    private String name;
    private String username;
    private String email;
    public String password;
    private Set<RoleRequest> roles;
    private ProfileRequest profile; // Optional profile details

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

    public Set<RoleRequest> getRoles() {
        return roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(Set<RoleRequest> roles) {
        this.roles = roles;
    }

    public ProfileRequest getProfile() {
        return profile;
    }

    public void setProfile(ProfileRequest profile) {
        this.profile = profile;
    }
}
