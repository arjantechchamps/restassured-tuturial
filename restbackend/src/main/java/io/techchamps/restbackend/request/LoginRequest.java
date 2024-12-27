package io.techchamps.restbackend.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {
    @NotBlank
    @Size(min = 3, max = 60)
    private String username;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    public @NotBlank @Size(min = 3, max = 60) String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank @Size(min = 3, max = 60) String username) {
        this.username = username;
    }

    public @NotBlank @Size(min = 6, max = 40) String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank @Size(min = 6, max = 40) String password) {
        this.password = password;
    }
}
