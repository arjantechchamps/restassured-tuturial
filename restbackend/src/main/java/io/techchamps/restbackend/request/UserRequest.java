package io.techchamps.restbackend.request;


import java.util.List;
import java.util.Set;

public class UserRequest {

    private String name;
    private String username;
    private String email;
    public String password;
    private Set<String> roles;
    private List<AdressRequest> adresses;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public List<AdressRequest> getAdresses() {
        return adresses;
    }

    public void setAdresses(List<AdressRequest> adresses) {
        this.adresses = adresses;
    }
}
