package io.techchamps.restbackend.request;

import io.techchamps.restbackend.enums.Interest;

import java.util.List;

public class ProfileRequest {

    private List<AddressRequest> addresses;
    private List<Interest> interests;


    public List<AddressRequest> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressRequest> addresses) {
        this.addresses = addresses;
    }

    public List<Interest> getInterests() {
        return interests;
    }

    public void setInterests(List<Interest> interests) {
        this.interests = interests;
    }
}
