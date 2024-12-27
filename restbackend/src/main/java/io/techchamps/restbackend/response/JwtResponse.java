package io.techchamps.restbackend.response;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.util.List;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private List<String> roles;

    public JwtResponse(String token, Authentication authentication) {
        this.token = token;
        this.roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
