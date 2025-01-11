package io.techchamps.restbackend.response;

import io.techchamps.restbackend.enums.Interest;

import java.util.List;

public class ProfileResponse {

    private List<AddressResponse> addresses;
    private List<PhoneNumberResponse> phoneNumbers;
    private List<Interest> interests;

    public List<AddressResponse> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressResponse> addresses) {
        this.addresses = addresses;
    }

    public List<PhoneNumberResponse> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumberResponse> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<Interest> getInterests() {
        return interests;
    }

    public void setInterests(List<Interest> interests) {
        this.interests = interests;
    }
}
