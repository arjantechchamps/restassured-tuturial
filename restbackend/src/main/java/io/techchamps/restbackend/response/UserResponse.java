package io.techchamps.restbackend.response;


import io.techchamps.restbackend.request.ProfileRequest;

import java.util.Set;

public class UserResponse {
    private int id;
    private String name;
    private String username;
    private String email;
    private Set<String> roles;
    private ProfileRequest profile; // Optional profile details

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public ProfileRequest getProfile() {
        return profile;
    }

    public void setProfile(ProfileRequest profile) {
        this.profile = profile;
    }
}
