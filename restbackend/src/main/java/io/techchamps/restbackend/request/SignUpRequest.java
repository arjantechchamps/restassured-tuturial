package io.techchamps.restbackend.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class SignUpRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String name;

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Size(max = 60)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private Set<String> roles;

    public @NotBlank @Size(min = 3, max = 50) String getName() {
        return name;
    }

    public void setName(@NotBlank @Size(min = 3, max = 50) String name) {
        this.name = name;
    }

    public @NotBlank @Size(min = 3, max = 50) String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank @Size(min = 3, max = 50) String username) {
        this.username = username;
    }

    public @NotBlank @Size(max = 60) @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank @Size(max = 60) @Email String email) {
        this.email = email;
    }

    public @NotBlank @Size(min = 6, max = 40) String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank @Size(min = 6, max = 40) String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
